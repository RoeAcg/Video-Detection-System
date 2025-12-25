# training/utils/json_export.py
import os
import json
import numpy as np
from statistics import mean
from collections import defaultdict
import logging

logger = logging.getLogger(__name__)


def convert_to_serializable(obj):
    """
    🔧 修复：将不可序列化的对象转为可序列化格式
    """
    if isinstance(obj, np.ndarray):
        return obj.tolist()
    elif isinstance(obj, np.integer):
        return int(obj)
    elif isinstance(obj, np.floating):
        return float(obj)
    elif isinstance(obj, defaultdict):
        return dict(obj)
    elif isinstance(obj, dict):
        return {k: convert_to_serializable(v) for k, v in obj.items()}
    elif isinstance(obj, (list, tuple)):
        return [convert_to_serializable(v) for v in obj]
    else:
        return obj


def build_deploy_json_per_dataset(output_dir, all_details, all_metrics, task_id, model_version):
    """
    导出包含每个数据集的：指标、曲线/图路径、视频聚合、关键帧等

    🔧 修复：处理 JSON 序列化问题
    """
    try:
        payload = {
            'task_id': task_id,
            'model_version': model_version,
            'datasets': {}
        }

        for ds, det in all_details.items():
            logger.info(f"处理数据集: {ds}")

            # 汇总每个视频的统计
            videos = {}
            for vid, obj in det['video_map'].items():
                scores = [float(v) for v in obj['scores']]  # 确保是 float

                if len(scores) == 0:
                    continue

                videos[vid] = {
                    'frame_count': len(scores),
                    'score_mean': float(np.mean(scores)),
                    'score_max': float(np.max(scores)),
                    'score_min': float(np.min(scores)),
                    'score_std': float(np.std(scores)),
                    'timeline_img': det['timeline_paths'].get(vid) if det.get('timeline_paths') else None,
                    'keyframes': det['keyframe_paths'].get(vid) if det.get('keyframe_paths') else None,
                }

            # 转换 metrics（确保所有值可序列化）
            metrics_clean = convert_to_serializable(det['metrics'])

            payload['datasets'][ds] = {
                'metrics': metrics_clean,
                'video_count': len(videos),
                'frame_count': len(det['predictions']),
                'curves_dir': det['curves_dir'],
                'confusion_path': det['confusion_path'],
                'tsne_path': det['tsne_path'],
                'distribution_plot': det.get('distribution_plot'),  # 新增
                'videos': videos  # 只包含必要信息
            }

            logger.info(f"  视频数: {len(videos)}, 帧数: {len(det['predictions'])}")

        # 保存 JSON
        out_path = os.path.join(output_dir, f'benchmark_export_{task_id}.json')
        with open(out_path, 'w', encoding='utf-8') as f:
            json.dump(payload, f, ensure_ascii=False, indent=2)

        logger.info(f"✅ JSON 导出成功: {out_path}")
        return out_path

    except Exception as e:
        logger.error(f"❌ JSON 导出失败: {str(e)}", exc_info=True)
        raise


def build_detectionresponse_for_backend(all_details, task_id, model_version):
    """
    构造与后端协定的 DetectionResponse（任务级）

    🔧 优化：选择最可疑的视频作为代表
    """
    try:
        # 选择全局"最可疑视频"
        best_vid, best_mean = None, -1.0
        best_ds = None

        for ds, det in all_details.items():
            for vid, obj in det['video_map'].items():
                scores = [float(v) for v in obj['scores']]
                if len(scores) == 0:
                    continue

                m = mean(scores)
                if m > best_mean:
                    best_mean = m
                    best_vid = vid
                    best_ds = ds

        fake_prob = max(0.0, min(1.0, best_mean))
        is_fake = fake_prob >= 0.5

        # 构造关键帧
        kf_items = []
        if best_vid and best_ds:
            keyframes_data = all_details[best_ds].get('keyframe_paths', {}).get(best_vid, [])
            for item in keyframes_data[:5]:  # 只取前5个
                kf_items.append({
                    'frame_idx': -1,
                    'timestamp': 0.0,
                    'is_fake_score': float(item['score']),
                    'is_suspicious': True,
                    'reason': 'High fake probability',
                    'thumb_path': item['thumb_path']
                })

        # 构造响应
        detection = {
            'task_id': task_id,
            'is_fake': is_fake,
            'fake_probability': float(fake_prob),
            'confidence': float(max(0.5, fake_prob)),
            'key_frames': kf_items,
            'processing_time_ms': 0,
            'model_version': model_version,
            'created_at': '',

            # 简化的分析字段
            'summary': {
                'best_video_id': best_vid,
                'best_video_score': float(fake_prob),
                'total_videos': sum(len(det['video_map']) for det in all_details.values())
            }
        }

        return detection

    except Exception as e:
        logger.error(f"❌ 构造 DetectionResponse 失败: {str(e)}")
        raise


def post_result_to_backend(url, payload):
    """发送结果到后端"""
    import requests
    try:
        r = requests.post(url, json=payload, timeout=30)
        return (r.status_code == 200, r.text)
    except Exception as e:
        return (False, str(e))
