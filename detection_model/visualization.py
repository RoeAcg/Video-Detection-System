"""
可视化生成模块
生成热力图、关键帧、时间轴等可视化数据
"""

import os
import numpy as np
import cv2
import matplotlib

matplotlib.use('Agg')
import matplotlib.pyplot as plt
from typing import List, Dict
import logging

logger = logging.getLogger(__name__)


def generate_visualizations(
        task_id: str,
        video_path: str,
        frames: List[np.ndarray],
        frame_indices: List[int],
        predictions: np.ndarray,
        output_dir: str
) -> Dict:
    """
    生成可视化数据

    Returns:
        visualization_data: 包含各种可视化路径和数据的字典
    """
    os.makedirs(output_dir, exist_ok=True)

    viz_data = {}

    try:
        # 1. 生成时间轴热力图
        timeline_path = generate_timeline_heatmap(
            predictions, frame_indices, output_dir
        )
        viz_data['timeline_heatmap'] = timeline_path

        # 2. 提取关键帧（Top-K 最可疑帧）
        keyframes = extract_key_frames(
            frames, frame_indices, predictions, output_dir, topk=5
        )
        viz_data['key_frames'] = keyframes

        # 3. 生成置信度曲线
        confidence_curve_path = generate_confidence_curve(
            predictions, frame_indices, output_dir
        )
        viz_data['confidence_curve'] = confidence_curve_path

        # 4. 帧级预测详情
        viz_data['frame_predictions'] = [
            {
                'frame_index': int(idx),
                'timestamp': float(idx / 30.0),  # 假设30fps
                'fake_probability': float(pred),
                'is_suspicious': bool(pred > 0.7)
            }
            for idx, pred in zip(frame_indices, predictions)
        ]

        logger.info(f"[{task_id}] ✅ 可视化生成完成")

    except Exception as e:
        logger.error(f"[{task_id}] 可视化生成失败: {str(e)}")

    return viz_data


def generate_timeline_heatmap(predictions, frame_indices, output_dir):
    """生成时间轴热力图"""
    plt.figure(figsize=(12, 3))

    # 创建热力图矩阵
    heatmap = predictions.reshape(1, -1)

    plt.imshow(heatmap, cmap='RdYlGn_r', aspect='auto', vmin=0, vmax=1)
    plt.colorbar(label='Fake Probability')
    plt.xlabel('Frame Index')
    plt.title('Timeline Heatmap: Deepfake Detection')
    plt.yticks([])

    output_path = os.path.join(output_dir, 'timeline_heatmap.png')
    plt.tight_layout()
    plt.savefig(output_path, dpi=150, bbox_inches='tight')
    plt.close()

    return output_path


def extract_key_frames(frames, frame_indices, predictions, output_dir, topk=5):
    """提取关键帧（最可疑的帧）"""
    # 按预测值排序，取 Top-K
    sorted_indices = np.argsort(-predictions)[:topk]

    keyframes = []

    for rank, idx in enumerate(sorted_indices):
        frame = frames[idx]
        frame_idx = frame_indices[idx]
        pred_score = predictions[idx]

        # 保存关键帧
        keyframe_path = os.path.join(output_dir, f'keyframe_{rank + 1}.jpg')

        # 添加文字标注
        annotated = frame.copy()
        cv2.putText(
            annotated,
            f'Fake: {pred_score:.3f}',
            (10, 30),
            cv2.FONT_HERSHEY_SIMPLEX,
            1.0,
            (255, 0, 0),
            2
        )

        # BGR 转换（OpenCV 保存格式）
        cv2.imwrite(keyframe_path, cv2.cvtColor(annotated, cv2.COLOR_RGB2BGR))

        keyframes.append({
            'frame_index': int(frame_idx),
            'timestamp': float(frame_idx / 30.0),
            'fake_probability': float(pred_score),
            'thumbnail_path': keyframe_path,
            'reason': '高伪造概率' if pred_score > 0.7 else '可疑帧'
        })

    return keyframes


def generate_confidence_curve(predictions, frame_indices, output_dir):
    """生成置信度曲线"""
    plt.figure(figsize=(10, 4))

    plt.plot(frame_indices, predictions, linewidth=2, color='#E74C3C')
    plt.axhline(y=0.5, color='gray', linestyle='--', alpha=0.5, label='Threshold')
    plt.fill_between(
        frame_indices, predictions, 0.5,
        where=(predictions > 0.5), alpha=0.3, color='red', label='Fake Region'
    )
    plt.fill_between(
        frame_indices, predictions, 0.5,
        where=(predictions <= 0.5), alpha=0.3, color='green', label='Real Region'
    )

    plt.xlabel('Frame Index')
    plt.ylabel('Fake Probability')
    plt.title('Frame-level Deepfake Confidence')
    plt.legend()
    plt.grid(alpha=0.3)

    output_path = os.path.join(output_dir, 'confidence_curve.png')
    plt.tight_layout()
    plt.savefig(output_path, dpi=150, bbox_inches='tight')
    plt.close()

    return output_path

