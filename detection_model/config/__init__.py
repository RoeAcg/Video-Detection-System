import os
import sys
current_file_path = os.path.abspath(__file__)
parent_dir = os.path.dirname(os.path.dirname(current_file_path))
project_root_dir = os.path.dirname(parent_dir)
sys.path.append(parent_dir)
sys.path.append(project_root_dir)


"""
配置管理
"""


class Config:
    """应用配置"""

    # Flask 配置
    DEBUG = os.getenv('DEBUG', 'False').lower() == 'true'
    SECRET_KEY = os.getenv('SECRET_KEY', 'dev-secret-key')

    # DeepfakeBench 配置
    DETECTOR_CONFIG = os.getenv(
        'DETECTOR_CONFIG',
        os.path.join(project_root_dir, 'training', 'config', 'detector', 'ucf.yaml')
    )
    WEIGHTS_PATH = os.getenv(
        'WEIGHTS_PATH',
        os.path.join(project_root_dir, 'training', 'weights', 'xception_best.pth')
    )
    DEVICE = 'cuda' if os.getenv('USE_GPU', 'true').lower() == 'true' else 'cpu'

    # 输出目录
    OUTPUT_DIR = os.getenv('OUTPUT_DIR', os.path.join(project_root_dir, 'outputs'))
    os.makedirs(OUTPUT_DIR, exist_ok=True)