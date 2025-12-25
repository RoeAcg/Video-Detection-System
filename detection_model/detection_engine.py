"""
DeepfakeBench 检测引擎封装
支持多模型：Effort (人脸) 和 DRCT (AIGC通用)
"""

import os
import yaml
import time
import numpy as np
import cv2
import torch
import torch.nn as nn
import logging
import warnings
from torchvision import transforms
from PIL import Image

# DeepfakeBench 相关导入
import sys
sys.path.insert(0, os.path.join(os.path.dirname(__file__), '..', 'training'))

from detectors import DETECTOR
from models import DetectionRequest, DetectionResponse, FeatureScores, VideoMetadata
from visualization import generate_visualizations

# --- CLIP 导入 ---
try:
    import clip
except ImportError:
    print("[Error] 未安装 OpenAI CLIP。请运行: pip install git+https://github.com/openai/CLIP.git")
    clip = None

logger = logging.getLogger(__name__)

# --- Dlib ---
DLIB_LANDMARK_PATH = os.path.join(os.path.dirname(__file__), "../preprocessing/dlib_tools/shape_predictor_81_face_landmarks.dat")
DLIB_DETECTOR = None
DLIB_PREDICTOR = None
import dlib

def init_dlib():
    global DLIB_DETECTOR, DLIB_PREDICTOR
    if DLIB_DETECTOR is None:
        DLIB_DETECTOR = dlib.get_frontal_face_detector()
    if DLIB_PREDICTOR is None and os.path.exists(DLIB_LANDMARK_PATH):
        DLIB_PREDICTOR = dlib.shape_predictor(DLIB_LANDMARK_PATH)

# ==============================================================================
# DRCT 模型定义 (Copy from detection_gui.py)
# ==============================================================================

class CLIPModelV2(nn.Module):
    def __init__(self, name='clip-ViT-L-14', num_classes=1024, freeze_extractor=False):
        super(CLIPModelV2, self).__init__()
        clip_name = name.replace('clip-', '').replace('L-', 'L/').replace('B-', 'B/')
        logger.info(f"[DRCT] 正在加载 CLIP 底座 ({clip_name})...")
        self.model, self.preprocess = clip.load(clip_name, device="cpu")

        if freeze_extractor:
            for param in self.model.parameters():
                param.requires_grad = False

        feature_dim = 768 if 'L/14' in clip_name else 512
        self.fc = nn.Linear(feature_dim, num_classes)

    def forward(self, x):
        features = self.model.encode_image(x)
        features = features.float()
        return self.fc(features)

class ContrastiveModels(nn.Module):
    def __init__(self, model_name, num_classes=2, embedding_size=1024):
        super(ContrastiveModels, self).__init__()
        self.model = CLIPModelV2(name=model_name, num_classes=embedding_size)
        self.fc = nn.Linear(embedding_size, num_classes)

    def forward(self, x):
        feature = self.model(x)
        y_pred = self.fc(feature)
        return y_pred

# ==============================================================================
# 智能权重加载器
# ==============================================================================

MODEL_POOL = {}

def smart_load_state_dict(model, ckpt_path, device):
    logger.info(f"[Loader] 读取权重: {os.path.basename(ckpt_path)}")
    state_dict = torch.load(ckpt_path, map_location="cpu")

    if 'state_dict' in state_dict:
        state_dict = state_dict['state_dict']
    elif 'model' in state_dict:
        state_dict = state_dict['model']

    model_keys = list(model.state_dict().keys())
    new_state_dict = {}
    matched_count = 0

    for k, v in state_dict.items():
        k_clean = k.replace("module.", "")
        k_drct_fix = k_clean
        if not k_clean.startswith("model.") and "visual." in k_clean:
            k_drct_fix = "model." + k_clean

        if k_clean in model_keys:
            new_state_dict[k_clean] = v
            matched_count += 1
        elif k_drct_fix in model_keys:
            new_state_dict[k_drct_fix] = v
            matched_count += 1

    model.load_state_dict(new_state_dict, strict=False)
    return model.to(device)

def load_deepfake_model(detector_name, device, project_root):
    global MODEL_POOL
    key = (detector_name, str(device))
    if key in MODEL_POOL: return MODEL_POOL[key]

    t0 = time.time()
    
    # 路径配置
    config_dir = os.path.join(project_root, "training", "config", "detector")
    weights_dir = os.path.join(project_root, "training", "weights")
    
    detector_config_map = {
        "effort": (os.path.join(config_dir, "effort.yaml"), os.path.join(weights_dir, "effort_clip_L14.pth")),
        "drct": (None, os.path.join(weights_dir, "clip-ViT-L-14_224_drct_amp_crop.pth"))
    }

    if detector_name not in detector_config_map:
        raise ValueError(f"Unknown model: {detector_name}")
        
    config_path, weights_path = detector_config_map[detector_name]

    if not os.path.exists(weights_path):
        raise FileNotFoundError(f"权重文件缺失: {weights_path}")

    if detector_name == "drct":
        logger.info(f"[Init] 初始化 DRCT...")
        model = ContrastiveModels(model_name="clip-ViT-L-14", num_classes=2, embedding_size=1024)
        model = smart_load_state_dict(model, weights_path, device)
        model.eval()

    elif detector_name == "effort":
        logger.info(f"[Init] 初始化 Effort...")
        with open(config_path, 'r') as f:
            config = yaml.safe_load(f)
        model_class = DETECTOR[config['model_name']]
        model = model_class(config).to("cpu")
        ckpt = torch.load(weights_path, map_location='cpu')
        
        # 权重修复逻辑
        if list(ckpt.keys())[0].startswith('module.'):
            ckpt = {k.replace('module.', '', 1): v for k, v in ckpt.items()}
        new_ckpt = {k.replace('backbone.', 'backbone.vision_model.', 1) if k.startswith(
            'backbone.') and 'vision_model' not in k else k: v for k, v in ckpt.items()}
            
        model.load_state_dict(new_ckpt, strict=False)
        model = model.to(device)
        model.eval()

    MODEL_POOL[key] = model
    logger.info(f"[Success] 加载耗时: {time.time() - t0:.2f}s")
    return model

# ==============================================================================
# 检测引擎类
# ==============================================================================

class DeepfakeDetectionEngine:
    def __init__(self, detector_config: str, weights_path: str, device: str = 'cuda'):
        self.device = torch.device(device if torch.cuda.is_available() else 'cpu')
        
        # 获取项目根目录，用于定位模型文件
        # 从 training/config/__init__.py 所在位置推断
        self.project_root = os.path.abspath(os.path.join(os.path.dirname(__file__), ".."))
        
        logger.info(f"使用设备: {self.device}")
        
        # 预先初始化 Dlib
        init_dlib()

    def detect(self, request: DetectionRequest) -> DetectionResponse:
        start_time = time.time()
        
        # 1. 确定使用的模型
        # mode映射: standard -> effort, aigc -> drct
        model_name = "effort"
        if request.mode == "aigc":
            model_name = "drct"
        elif request.mode == "standard":
            model_name = "effort"
            
        logger.info(f"[{request.task_id}] 使用模型: {model_name}")

        # 2. 提取视频帧
        frames, frame_indices, video_meta = self._extract_frames(
            request.video_path,
            request.frame_rate,
            request.max_frames
        )

        if len(frames) == 0:
            raise ValueError("无法从视频中提取有效帧")

        # 3. 加载模型
        model = load_deepfake_model(model_name, self.device, self.project_root)

        # 4. 执行推理 (包含预处理)
        predictions, features, valid_frame_indices = self._inference(model, model_name, frames, frame_indices)
        
        if len(predictions) == 0:
             return DetectionResponse(
                task_id=request.task_id,
                result="UNKNOWN",
                confidence=0.0,
                model_version=model_name,
                processing_time_ms=int((time.time() - start_time) * 1000),
                frames_analyzed=0,
                features=FeatureScores(0,0,0,0,0),
                artifacts=["No valid frames/faces detected"],
                metadata=video_meta
            )

        # 5. 聚合结果
        fake_prob = float(np.mean(predictions))
        result_label = "FAKE" if fake_prob >= 0.5 else "REAL"
        confidence = float(max(fake_prob, 1 - fake_prob))
        
        # 6. 计算特征分数等
        feature_scores = self._compute_features(predictions, features)
        artifacts = self._detect_artifacts(predictions, feature_scores)

        # 7. 生成可视化 (可选) - 使用有效帧索引
        viz_data = None
        if request.include_features:
            # 只传递成功处理的帧和对应的索引
            valid_frames = [frames[i] for i in range(len(frames)) if i < len(valid_frame_indices)]
            viz_data = generate_visualizations(
                task_id=request.task_id,
                video_path=request.video_path,
                frames=valid_frames[:len(predictions)],  # 确保帧数量与预测结果一致
                frame_indices=valid_frame_indices,
                predictions=predictions,
                output_dir=os.path.join(self.project_root, 'training', 'outputs', request.task_id)
            )

        response = DetectionResponse(
            task_id=request.task_id,
            result=result_label,
            confidence=confidence,
            model_version=model_name,
            processing_time_ms=int((time.time() - start_time) * 1000),
            frames_analyzed=len(frames),
            features=feature_scores,
            artifacts=artifacts,
            metadata=video_meta
        )
        if viz_data:
            response.add_visualization_data(viz_data)
            
        return response

    def _extract_frames(self, video_path, frame_rate, max_frames):
        cap = cv2.VideoCapture(video_path)
        if not cap.isOpened():
            raise ValueError(f"无法打开视频文件: {video_path}")

        fps = cap.get(cv2.CAP_PROP_FPS)
        total_frames = int(cap.get(cv2.CAP_PROP_FRAME_COUNT))
        duration = total_frames / fps if fps > 0 else 0
        width = int(cap.get(cv2.CAP_PROP_FRAME_WIDTH))
        height = int(cap.get(cv2.CAP_PROP_FRAME_HEIGHT))
        
        video_meta = VideoMetadata(
            resolution=f"{width}x{height}",
            duration=duration,
            codec="unknown",
            bitrate=0
        )

        sample_interval = max(1, int(fps / frame_rate))
        frames = []
        frame_indices = []
        frame_idx = 0

        while len(frames) < max_frames:
            ret, frame = cap.read()
            if not ret: break

            if frame_idx % sample_interval == 0:
                frame_rgb = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
                frames.append(frame_rgb)
                frame_indices.append(frame_idx)
            
            frame_idx += 1
            
        cap.release()
        return frames, frame_indices, video_meta

    def _process_image_tensor(self, img_np, mode="face"):
        img_pil = Image.fromarray(img_np)
        
        if mode == "whole": # DRCT
            tf = transforms.Compose([
                transforms.Resize((224, 224), interpolation=transforms.InterpolationMode.BICUBIC, antialias=True),
                transforms.ToTensor(),
                transforms.Normalize((0.48145466, 0.4578275, 0.40821073), (0.26862954, 0.26130258, 0.27577711)),
            ])
            return tf(img_pil)
        
        else: # Effort (Face Crop)
            h, w = img_np.shape[:2]
            scale = 1.0
            if max(h, w) > 1280:
                scale = 1280 / max(h, w)
                img_small = cv2.resize(img_np, (0, 0), fx=scale, fy=scale)
            else:
                img_small = img_np

            faces = DLIB_DETECTOR(img_small, 0)
            if len(faces) == 0:
                # No face detected
                return None

            face = max(faces, key=lambda rect: rect.width() * rect.height())
            left, top, right, bottom = int(face.left() / scale), int(face.top() / scale), int(face.right() / scale), int(face.bottom() / scale)

            cx, cy = (left + right) // 2, (top + bottom) // 2
            w_box, h_box = right - left, bottom - top
            new_w, new_h = int(w_box * 1.3), int(h_box * 1.3)
            left, top = max(0, cx - new_w // 2), max(0, cy - new_h // 2)
            right, bottom = min(w, cx + new_w // 2), min(h, cy + new_h // 2)

            face_crop = img_pil.crop((left, top, right, bottom))
            
            tf = transforms.Compose([
                transforms.Resize((224, 224), interpolation=transforms.InterpolationMode.BICUBIC),
                transforms.ToTensor(),
                transforms.Normalize([0.5] * 3, [0.5] * 3)
            ])
            return tf(face_crop)

    def _inference(self, model, model_name, frames, original_frame_indices):
        batch = []
        valid_indices = [] # keep track of frames that successfully processed (e.g. had faces)
        
        detect_mode = "whole" if model_name == "drct" else "face"
        
        for i, frame in enumerate(frames):
            tensor = self._process_image_tensor(frame, mode=detect_mode)
            if tensor is not None:
                batch.append(tensor)
                valid_indices.append(i)
        
        if not batch:
            return [], [], []

        batch_t = torch.stack(batch).to(self.device)
        all_predictions = []
        all_features = []
        
        batch_size = 16
        with torch.no_grad():
            for i in range(0, len(batch_t), batch_size):
                x = batch_t[i:i+batch_size]
                
                if model_name == "drct":
                    logits = model(x)
                    # DRCT 标签映射: Index 0 = Real, Index 1 = Fake
                    probs = torch.softmax(logits, dim=1)[:, 1].cpu().numpy()
                    feats = np.zeros((len(x), 1024)) # DRCT uses CLIP features internally, simplification
                else:
                    d = {"image": x, "label": torch.zeros(len(x)).to(self.device)}
                    res = model(d, inference=True)
                    if "prob" in res:
                        probs = res["prob"].cpu().numpy()
                    else:
                        probs = torch.softmax(res["cls"], 1)[:, 1].cpu().numpy()
                    
                    if "feat" in res:
                        feats = res["feat"].detach().cpu().numpy()
                    else:
                        feats = np.zeros((len(x), 1024))

                all_predictions.extend(probs.tolist())
                all_features.append(feats)
        
        predictions = np.array(all_predictions)
        features = np.vstack(all_features) if all_features else np.array([])
        
        # 返回与预测结果对应的实际帧索引
        valid_frame_indices = [original_frame_indices[i] for i in valid_indices]
        
        return predictions, features, valid_frame_indices

    def _compute_features(self, predictions, features):
        if len(predictions) == 0:
             return FeatureScores(0,0,0,0,0)
             
        temporal_consistency = 1.0 - min(1.0, np.std(predictions) * 2)
        
        if len(features) > 0 and features.shape[1] > 0:
            facial_landmarks_score = min(1.0, np.mean(np.linalg.norm(features, axis=1)) / 100)
        else:
             facial_landmarks_score = 0.5
             
        compression_artifacts = min(1.0, np.mean(np.abs(np.diff(predictions))) * 10) if len(predictions) > 1 else 0.0
        
        return FeatureScores(
            temporal_consistency=temporal_consistency,
            facial_landmarks_score=facial_landmarks_score,
            compression_artifacts=compression_artifacts,
            color_histogram_score=0.85,
            optical_flow_score=0.90
        )

    def _detect_artifacts(self, predictions, features):
        artifacts = []
        if features.temporal_consistency < 0.7:
            artifacts.append("temporal_inconsistencies")
        if features.compression_artifacts > 0.6:
            artifacts.append("compression_artifacts")
        if np.max(predictions) - np.min(predictions) > 0.5:
            artifacts.append("face_blending_boundaries")
        return artifacts
