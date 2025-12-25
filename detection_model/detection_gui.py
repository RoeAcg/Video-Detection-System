# detection_gui.py

import os
import sys
import torch
import torch.nn as nn
import numpy as np
import cv2
import yaml
import time
import dlib
import warnings
from torchvision import transforms
from PIL import Image

# --- 尝试导入 CLIP ---
try:
    import clip
except ImportError:
    print("[Error] 未安装 OpenAI CLIP。请运行: pip install git+https://github.com/openai/CLIP.git")
    clip = None

# --- 尝试导入 DeepfakeBench 检测器 ---
from detectors import DETECTOR


# --- 环境配置 ---
os.environ["KMP_DUPLICATE_LIB_OK"] = "TRUE"
warnings.filterwarnings("ignore")
torch.backends.cudnn.benchmark = True

# --- 设备检测 ---
DEVICE = torch.device("cuda" if torch.cuda.is_available() else "cpu")

# --- Dlib ---
DLIB_LANDMARK_PATH = "preprocessing/dlib_tools/shape_predictor_81_face_landmarks.dat"
DLIB_DETECTOR = None
DLIB_PREDICTOR = None


def init_dlib():
    global DLIB_DETECTOR, DLIB_PREDICTOR
    if DLIB_DETECTOR is None:
        DLIB_DETECTOR = dlib.get_frontal_face_detector()
    if DLIB_PREDICTOR is None and os.path.exists(DLIB_LANDMARK_PATH):
        DLIB_PREDICTOR = dlib.shape_predictor(DLIB_LANDMARK_PATH)


# ==============================================================================
# DRCT 模型定义
# ==============================================================================

class CLIPModelV2(nn.Module):
    def __init__(self, name='clip-ViT-L-14', num_classes=1024, freeze_extractor=False):
        super(CLIPModelV2, self).__init__()
        clip_name = name.replace('clip-', '').replace('L-', 'L/').replace('B-', 'B/')

        print(f"[DRCT] 正在加载 CLIP 底座 ({clip_name})...")
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
    print(f"[Loader] 读取权重: {os.path.basename(ckpt_path)}")
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

        # 自动修复 model. 前缀缺失
        k_drct_fix = k_clean
        if not k_clean.startswith("model.") and "visual." in k_clean:
            k_drct_fix = "model." + k_clean

        if k_clean in model_keys:
            new_state_dict[k_clean] = v
            matched_count += 1
        elif k_drct_fix in model_keys:
            new_state_dict[k_drct_fix] = v
            matched_count += 1

    print(f"[Loader] 成功匹配键值: {matched_count} / {len(model_keys)}")

    if "fc.weight" in new_state_dict:
        print("[Loader] ✅ 核心分类层加载成功！")
    else:
        print("[Loader] ⚠️ 警告：核心分类层未找到！")

    model.load_state_dict(new_state_dict, strict=False)
    return model.to(device)


def load_deepfake_model(detector_name="effort", device=DEVICE):
    global MODEL_POOL
    key = (detector_name, device)
    if key in MODEL_POOL: return MODEL_POOL[key]

    t0 = time.time()

    # === 请修改为你的实际路径 ===
    detector_config_map = {
        "effort": ("training/config/detector/effort.yaml", "training/weights/effort_clip_L14.pth"),
        "drct": (None, "training/weights/clip-ViT-L-14_224_drct_amp_crop.pth")
    }

    if detector_name not in detector_config_map: raise ValueError(f"Unknown model: {detector_name}")
    config_path, weights_path = detector_config_map[detector_name]

    if not os.path.exists(weights_path):
        raise FileNotFoundError(f"权重文件缺失: {weights_path}")

    if detector_name == "drct":
        print(f"[Init] 初始化 DRCT...")
        model = ContrastiveModels(model_name="clip-ViT-L-14", num_classes=2, embedding_size=1024)
        model = smart_load_state_dict(model, weights_path, device)
        model.eval()

    elif detector_name == "effort":
        print(f"[Init] 初始化 Effort...")
        with open(config_path, 'r') as f:
            config = yaml.safe_load(f)
        model_class = DETECTOR[config['model_name']]
        model = model_class(config).to("cpu")
        ckpt = torch.load(weights_path, map_location='cpu')
        if list(ckpt.keys())[0].startswith('module.'):
            ckpt = {k.replace('module.', '', 1): v for k, v in ckpt.items()}
        new_ckpt = {k.replace('backbone.', 'backbone.vision_model.', 1) if k.startswith(
            'backbone.') and 'vision_model' not in k else k: v for k, v in ckpt.items()}
        model.load_state_dict(new_ckpt, strict=False)
        model = model.to(device)
        model.eval()

    MODEL_POOL[key] = model
    print(f"[Success] 加载耗时: {time.time() - t0:.2f}s")
    return model


# ==============================================================================
# 图像处理 (Fix: Squish resize & Antialias)
# ==============================================================================

def process_image_tensor(img_pil, device, mode="face"):
    if mode == "whole":
        # --- DRCT 预处理修正 ---
        # 使用强制缩放 (Resize to 224x224) 而非 CenterCrop
        # 这能确保无论图片长宽比如何，模型都能看到全图（包括人脸）
        tf = transforms.Compose([
            transforms.Resize((224, 224), interpolation=transforms.InterpolationMode.BICUBIC, antialias=True),
            transforms.ToTensor(),
            # CLIP 标准归一化
            transforms.Normalize((0.48145466, 0.4578275, 0.40821073), (0.26862954, 0.26130258, 0.27577711)),
        ])
        return tf(img_pil)

    # Effort (Dlib Face Crop)
    init_dlib()
    img_np = np.array(img_pil)
    h, w = img_np.shape[:2]
    scale = 1.0
    if max(h, w) > 1280:
        scale = 1280 / max(h, w)
        img_small = cv2.resize(img_np, (0, 0), fx=scale, fy=scale)
    else:
        img_small = img_np

    faces = DLIB_DETECTOR(img_small, 0)
    if len(faces) == 0: raise ValueError("NO_FACE_DETECTED")

    face = max(faces, key=lambda rect: rect.width() * rect.height())
    left, top, right, bottom = int(face.left() / scale), int(face.top() / scale), int(face.right() / scale), int(
        face.bottom() / scale)

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


# ==============================================================================
# 推理逻辑 (Fix: Index 0 Mapping)
# ==============================================================================

def predict_on_image(img_path, detector_name="effort", device=DEVICE):
    try:
        model = load_deepfake_model(detector_name, device)
        img_raw = Image.open(img_path).convert("RGB")

        mode = "whole" if detector_name == "drct" else "face"
        tensor = process_image_tensor(img_raw, device, mode=mode)
        input_tensor = tensor.unsqueeze(0).to(device)

        with torch.no_grad():
            if detector_name == "drct":
                # DRCT 推理
                logits = model(input_tensor)
                probs = torch.softmax(logits, dim=1)

                # --- 关键修正 ---
                # DRCT (GenImage) 标准: Index 0 = Real, Index 1 = Fake
                fake_prob = float(probs[0, 1].item())

                print(f"[Debug] DRCT Logits: {logits.cpu().numpy()} | Prob(Fake): {fake_prob:.4f}")
            else:
                # Effort 推理
                data = {"image": input_tensor, "label": torch.zeros(1).to(device)}
                pred = model(data, inference=True)
                if "prob" in pred:
                    fake_prob = float(pred["prob"][0])
                else:
                    fake_prob = float(torch.softmax(pred["cls"], 1)[0, 1])

        return {
            "file": os.path.basename(img_path),
            "is_fake": fake_prob > 0.5,
            "fake_probability": fake_prob,
            "msg": "Success",
            "model": detector_name
        }
    except ValueError as e:
        if str(e) == "NO_FACE_DETECTED":
            return {"msg": "NO_FACE_DETECTED", "is_fake": False, "fake_probability": 0.0}
        return {"msg": str(e), "is_fake": False, "fake_probability": 0.0}
    except Exception as e:
        import traceback;
        traceback.print_exc()
        return {"msg": str(e), "is_fake": False, "fake_probability": 0.0}


def predict_on_video(video_path, detector_name="effort", device=DEVICE, max_frames=20):
    try:
        model = load_deepfake_model(detector_name, device)
        cap = cv2.VideoCapture(video_path)
        total = int(cap.get(cv2.CAP_PROP_FRAME_COUNT))
        if total <= 0: return {"msg": "Read Error", "fake_probability": 0.0}

        idxs = np.linspace(0, total - 1, min(total, max_frames), dtype=int)
        batch = []
        mode = "whole" if detector_name == "drct" else "face"

        for i in idxs:
            cap.set(cv2.CAP_PROP_POS_FRAMES, i)
            ret, frame = cap.read()
            if not ret: continue
            img = Image.fromarray(cv2.cvtColor(frame, cv2.COLOR_BGR2RGB))
            try:
                batch.append(process_image_tensor(img, device, mode=mode))
            except:
                pass
        cap.release()

        if not batch: return {"msg": "No Frames/Faces", "fake_probability": 0.0}

        batch_t = torch.stack(batch).to(device)
        probs = []
        with torch.no_grad():
            for i in range(0, len(batch_t), 16):
                x = batch_t[i:i + 16]
                if detector_name == "drct":
                    logits = model(x)
                    # 批量推理: Index 1 = Fake
                    p = torch.softmax(logits, dim=1)[:, 1].cpu().numpy()
                else:
                    d = {"image": x, "label": torch.zeros(len(x)).to(device)}
                    res = model(d, inference=True)
                    if "prob" in res:
                        p = res["prob"].cpu().numpy()
                    else:
                        p = torch.softmax(res["cls"], 1)[:, 1].cpu().numpy()
                probs.extend(p)

        mean_prob = float(np.mean(probs))
        return {"file": os.path.basename(video_path), "is_fake": mean_prob > 0.5, "fake_probability": mean_prob,
                "msg": "Success"}
    except Exception as e:
        return {"msg": str(e), "fake_probability": 0.0}


if __name__ == "__main__":
    print("Test done.")