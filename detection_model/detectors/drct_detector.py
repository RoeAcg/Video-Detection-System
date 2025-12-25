# training/detectors/drct_detector.py

import torch
import torch.nn as nn

try:
    import clip
except ImportError:
    print("请先安装 clip: pip install git+https://github.com/openai/CLIP.git")

from detectors import DETECTOR


class CLIPModelV2(nn.Module):
    """
    对应 DRCT 官方 models.py 中的 CLIPModelV2
    负责 CLIP Backbone + 第一层投影 (Projection)
    """
    CHANNELS = {
        "RN50": 1024,
        "ViT-B/32": 512,
        "ViT-L/14": 768  # DRCT 使用的是这个
    }

    def __init__(self, name='clip-ViT-L-14', num_classes=1024, freeze_extractor=False):
        super(CLIPModelV2, self).__init__()
        # 处理名称以匹配 clip.load 的要求
        self.clip_name = name.replace('clip-', '').replace('L-', 'L/').replace('B-', 'B/')

        # 加载 CLIP 模型
        # 注意：这里 device设为 cpu，由 DeepfakeBench 框架统一调度到 GPU
        self.model, self.preprocess = clip.load(self.clip_name, device="cpu")

        if freeze_extractor:
            for param in self.model.parameters():
                param.requires_grad = False

        # DRCT 的关键结构：CLIP 输出后的第一层全连接
        # 输入维度是 CLIP 的 visual output (768)，输出是 embedding_size (通常是 1024)
        in_features = self.CHANNELS.get(self.clip_name, 768)
        self.fc = nn.Linear(in_features, num_classes)

    def forward(self, x):
        # CLIP encode_image 期望输入是归一化后的 tensor
        features = self.model.encode_image(x)
        features = features.float()  # 确保精度
        return self.fc(features)


@DETECTOR.register_module(module_name='drct')
class DRCTDetector(nn.Module):
    """
    对应 DRCT 官方 models.py 中的 ContrastiveModels
    这是 DeepfakeBench 调用的顶层类
    """

    def __init__(self, config):
        super(DRCTDetector, self).__init__()

        # 从 config 获取参数，如果未定义则使用 DRCT 默认值
        self.model_name = config.get('backbone_name', 'clip-ViT-L-14')
        self.num_classes = config.get('num_classes', 2)
        self.embedding_size = config.get('embedding_size', 1024)  # DRCT 核心参数
        self.freeze_extractor = config.get('freeze_extractor', False)

        # 1. 初始化 Backbone (CLIP + Projection)
        # 注意：这里的 num_classes 传给 CLIPModelV2 的实际上是 embedding_size
        self.model = CLIPModelV2(
            name=self.model_name,
            num_classes=self.embedding_size,
            freeze_extractor=self.freeze_extractor
        )

        # 2. 初始化分类头 (Embedding -> Real/Fake)
        self.fc = nn.Linear(self.embedding_size, self.num_classes)

    def forward(self, data_dict, inference=False):
        # DeepfakeBench 传入的 data_dict
        images = data_dict['image']

        # 1. 提取特征 (经过了 CLIP encoder 和第一层 fc)
        # features shape: [Batch, 1024]
        features = self.model(images)

        # 2. 分类
        # logits shape: [Batch, 2]
        logits = self.fc(features)

        # 3. 计算概率
        prob = torch.softmax(logits, dim=1)[:, 1]

        return {
            'cls': logits,  # 用于计算 Loss
            'prob': prob,  # 用于推理输出
            'feat': features  # 用于可视化 (t-SNE)
        }

    def get_features(self, data_dict):
        """辅助函数：仅获取特征"""
        images = data_dict['image']
        return self.model(images)