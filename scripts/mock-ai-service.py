#!/usr/bin/env python3
"""
Mock AI Detection Service - 模拟 DeepfakeBench 检测服务
符合 AI_Client_API_Documentation.md 规范
"""
from flask import Flask, request, jsonify
import random
import time
import json
from datetime import datetime
import os

app = Flask(__name__)

# 模拟配置
GPU_AVAILABLE = True
MODEL_VERSION_MAP = {
    'standard': 'effort',
    'aigc': 'drct'
}

@app.route('/health', methods=['GET'])
def health():
    """健康检查接口"""
    return jsonify({
        'service': 'AI Detection Service',
        'status': 'running',
        'gpu_available': GPU_AVAILABLE,
        'timestamp': datetime.now().isoformat()
    }), 200


@app.route('/api/detect', methods=['POST'])
def detect():
    """视频/图片检测接口"""
    try:
        data = request.json
        print("\n" + "=" * 60)
        print("[MOCK AI] 收到检测请求")
        print("=" * 60)
        print(f"请求内容: {json.dumps(data, indent=2, ensure_ascii=False)}")

        # 提取请求参数
        task_id = data.get('taskId')
        video_path = data.get('videoPath')
        mode = data.get('mode', 'standard')
        file_hash = data.get('fileHash', '')
        frame_rate = data.get('frameRate', 5)
        max_frames = data.get('maxFrames', 300)

        # 验证必填参数
        if not task_id:
            return jsonify({'error': '缺少必填参数: taskId'}), 400
        if not video_path:
            return jsonify({'error': '缺少必填参数: videoPath'}), 400

        # 模拟文件检查（实际环境中会真实检查）
        if not os.path.exists(video_path):
            return jsonify({
                'error': f'无法打开视频文件: {video_path}'
            }), 500

        # 模拟处理延迟
        processing_start = time.time()
        time.sleep(random.uniform(0.5, 2.0))
        processing_time_ms = int((time.time() - processing_start) * 1000)

        # 生成模拟结果
        confidence = round(random.uniform(0.30, 0.99), 4)
        is_fake = confidence > 0.6
        
        # 根据模式选择模型版本
        model_version = MODEL_VERSION_MAP.get(mode, 'effort')
        
        # 生成特征分数
        features = {
            'temporal_consistency': round(random.uniform(0.4, 1.0), 2),
            'facial_landmarks_score': round(random.uniform(0.3, 0.9), 2),
            'compression_artifacts': round(random.uniform(0.0, 0.9), 2),
            'color_histogram_score': round(random.uniform(0.7, 0.95), 2),
            'optical_flow_score': round(random.uniform(0.8, 1.0), 2)
        }

        # 如果是AIGC模式，调整特征（不依赖人脸）
        if mode == 'aigc':
            features['facial_landmarks_score'] = 0.0
        
        # 生成伪造痕迹列表
        artifacts = []
        if is_fake:
            if features['temporal_consistency'] < 0.7:
                artifacts.append('temporal_inconsistencies')
            if features['facial_landmarks_score'] < 0.5 and mode == 'standard':
                artifacts.append('unnatural_facial_movements')
            if features['compression_artifacts'] > 0.6:
                artifacts.append('compression_artifacts')

        # 模拟帧数分析
        frames_analyzed = min(int(frame_rate * 10), max_frames)
        if mode == 'aigc':
            frames_analyzed = 1  # AIGC模式通常只分析单帧或少量帧

        # 构建响应
        response = {
            'taskId': task_id,
            'result': 'FAKE' if is_fake else 'REAL',
            'confidence': confidence,
            'modelVersion': model_version,
            'processingTimeMs': processing_time_ms,
            'framesAnalyzed': frames_analyzed,
            'features': features,
            'artifacts': artifacts,
            'metadata': {
                'resolution': '1920x1080',
                'duration': round(random.uniform(5.0, 30.0), 1),
                'codec': 'h264',
                'bitrate': random.randint(2000000, 8000000)
            }
        }

        print("\n[MOCK AI] 返回响应")
        print("-" * 60)
        print(json.dumps(response, indent=2, ensure_ascii=False))
        print("=" * 60 + "\n")

        return jsonify(response), 200

    except Exception as e:
        print(f"\n[MOCK AI] ❌ 错误: {str(e)}\n")
        return jsonify({
            'error': f'服务器内部错误: {str(e)}'
        }), 500


if __name__ == '__main__':
    print("=" * 60)
    print("Mock AI Detection Service")
    print("符合 AI_Client_API_Documentation.md 规范")
    print("=" * 60)
    print(f"Base URL: http://127.0.0.1:5000")
    print(f"GPU Available: {GPU_AVAILABLE}")
    print("=" * 60)
    print("\n可用接口:")
    print("  GET  /health        - 健康检查")
    print("  POST /api/detect    - 视频/图片检测")
    print("\n支持的检测模式:")
    print("  - standard (默认)  - 人脸伪造检测 (Effort)")
    print("  - aigc            - 通用生成检测 (DRCT)")
    print("\n" + "=" * 60 + "\n")
    
    app.run(host='0.0.0.0', port=5000, debug=False)
