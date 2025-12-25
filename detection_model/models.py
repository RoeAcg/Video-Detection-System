"""
数据模型定义
严格对应后端 API 规范
"""

from dataclasses import dataclass, asdict
from typing import List, Optional, Dict, Any


@dataclass
class DetectionRequest:
    """检测请求"""
    task_id: str
    video_path: str
    file_hash: str
    mode: str = 'standard'
    frame_rate: int = 5
    max_frames: int = 300
    include_features: bool = True
    timeout_seconds: int = 120


@dataclass
class FeatureScores:
    """特征分数"""
    temporal_consistency: float
    facial_landmarks_score: float
    compression_artifacts: float
    color_histogram_score: float
    optical_flow_score: float

    def to_dict(self):
        return asdict(self)


@dataclass
class VideoMetadata:
    """视频元数据"""
    resolution: str
    duration: float
    codec: str
    bitrate: int

    def to_dict(self):
        return asdict(self)


@dataclass
class DetectionResponse:
    """
    检测响应
    对应后端 API 规范的返回格式
    """
    task_id: str
    result: str  # "FAKE" 或 "REAL"
    confidence: float
    model_version: str
    processing_time_ms: int
    frames_analyzed: int
    features: FeatureScores
    artifacts: List[str]
    metadata: VideoMetadata

    # 扩展字段（用于前端可视化，可选）
    visualization_data: Optional[Dict[str, Any]] = None

    def to_dict(self):
        """转为字典（用于 JSON 序列化）"""
        result = {
            'taskId': self.task_id,
            'result': self.result,
            'confidence': self.confidence,
            'modelVersion': self.model_version,
            'processingTimeMs': self.processing_time_ms,
            'framesAnalyzed': self.frames_analyzed,
            'features': self.features.to_dict(),
            'artifacts': self.artifacts,
            'metadata': self.metadata.to_dict()
        }

        # 添加可视化数据（如果有）
        if self.visualization_data:
            result['visualizationData'] = self.visualization_data

        return result

    def add_visualization_data(self, viz_data: Dict):
        """添加可视化数据"""
        self.visualization_data = viz_data
