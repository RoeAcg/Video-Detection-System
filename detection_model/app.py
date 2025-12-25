"""
AI Service - 视频深伪检测 API 服务
基于 DeepfakeBench，提供 Flask HTTP 接口供 worker-service 调用
"""

from flask import Flask, request, jsonify
import os
import time
import logging
from datetime import datetime
from detection_engine import DeepfakeDetectionEngine
from models import DetectionRequest, DetectionResponse
from config import Config

# 配置日志
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger(__name__)

# 初始化 Flask
app = Flask(__name__)
app.config.from_object(Config)

# 初始化检测引擎（全局单例，避免重复加载模型）
detection_engine = None


def init_detection_engine():
    """延迟初始化检测引擎"""
    global detection_engine
    if detection_engine is None:
        logger.info("正在初始化检测引擎...")
        detection_engine = DeepfakeDetectionEngine(
            detector_config=Config.DETECTOR_CONFIG,
            weights_path=Config.WEIGHTS_PATH,
            device=Config.DEVICE
        )
        logger.info("✅ 检测引擎初始化完成")
    return detection_engine


@app.route('/health', methods=['GET'])
def health_check():
    """健康检查接口"""
    return jsonify({
        'status': 'healthy',
        'service': 'AI Detection Service',
        'version': '1.0.0',
        'timestamp': datetime.utcnow().isoformat(),
        'gpu_available': Config.DEVICE == 'cuda'
    }), 200


@app.route('/api/detect', methods=['POST'])
def detect_video():
    """
    核心接口：视频深伪检测

    请求体格式见后端文档：
    {
        "taskId": "uuid",
        "videoPath": "/app/uploads/...",
        "fileHash": "sha256:...",
        "mode": "standard",
        "frameRate": 5,
        "maxFrames": 300,
        "includeFeatures": true
    }
    """
    start_time = time.time()

    try:
        # 1. 解析请求
        data = request.get_json()
        if not data:
            return jsonify({
                'error': 'Invalid JSON',
                'errorCode': 'INVALID_REQUEST'
            }), 400

        logger.info(f"[{data.get('taskId')}] 接收到检测请求")

        # 2. 验证必填字段
        required_fields = ['taskId', 'videoPath', 'fileHash']
        for field in required_fields:
            if field not in data:
                return jsonify({
                    'error': f'Missing required field: {field}',
                    'errorCode': 'MISSING_FIELD',
                    'taskId': data.get('taskId', 'unknown')
                }), 400

        # 3. 构造请求对象
        req = DetectionRequest(
            task_id=data['taskId'],
            video_path=data['videoPath'],
            file_hash=data['fileHash'],
            mode=data.get('mode', 'standard'),  # 'standard' (Effort) or 'aigc' (DRCT)
            frame_rate=data.get('frameRate', 5),
            max_frames=data.get('maxFrames', 300),
            include_features=data.get('includeFeatures', True),
            timeout_seconds=data.get('timeoutSeconds', 120)
        )

        # 4. 检查视频文件是否存在
        if not os.path.exists(req.video_path):
            logger.error(f"[{req.task_id}] 视频文件不存在: {req.video_path}")
            return jsonify({
                'error': 'Video file not found',
                'errorCode': 'FILE_NOT_FOUND',
                'taskId': req.task_id,
                'timestamp': datetime.utcnow().isoformat()
            }), 404

        # 5. 初始化检测引擎
        engine = init_detection_engine()

        # 6. 执行检测
        logger.info(f"[{req.task_id}] 开始检测...")
        result = engine.detect(req)

        # 7. 计算处理时间
        processing_time_ms = int((time.time() - start_time) * 1000)
        result.processing_time_ms = processing_time_ms

        logger.info(
            f"[{req.task_id}] ✅ 检测完成 | "
            f"结果={result.result} | "
            f"置信度={result.confidence:.4f} | "
            f"耗时={processing_time_ms}ms"
        )

        # 8. 返回结果（转为字典）
        return jsonify(result.to_dict()), 200

    except FileNotFoundError as e:
        logger.error(f"文件错误: {str(e)}")
        return jsonify({
            'error': str(e),
            'errorCode': 'FILE_NOT_FOUND',
            'taskId': data.get('taskId', 'unknown'),
            'timestamp': datetime.utcnow().isoformat()
        }), 404

    except TimeoutError as e:
        logger.error(f"超时错误: {str(e)}")
        return jsonify({
            'error': 'Processing timeout',
            'errorCode': 'TIMEOUT',
            'taskId': data.get('taskId', 'unknown'),
            'timestamp': datetime.utcnow().isoformat()
        }), 504

    except Exception as e:
        logger.error(f"检测失败: {str(e)}", exc_info=True)
        return jsonify({
            'error': str(e),
            'errorCode': 'INTERNAL_ERROR',
            'taskId': data.get('taskId', 'unknown'),
            'timestamp': datetime.utcnow().isoformat()
        }), 500


@app.errorhandler(404)
def not_found(error):
    return jsonify({'error': 'Endpoint not found'}), 404


@app.errorhandler(500)
def internal_error(error):
    return jsonify({'error': 'Internal server error'}), 500


if __name__ == '__main__':
    # 开发模式运行
    app.run(
        host='0.0.0.0',
        port=5000,
        debug=Config.DEBUG
    )
