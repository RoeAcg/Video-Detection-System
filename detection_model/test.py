"""
eval pretrained model with enhanced visualization and deploy-oriented JSON export.

指令：
python training/test.py --detector_path training/config/detector/xception.yaml --test_dataset UADFV --weights_path training/weights/xception_best.pth --output_dir training/outputs/xception_uadfv --save_curves --save_tsne --save_timelines --save_keyframes --save_json --task_id bench-uadfv-xception

ucf:
python training/test.py --detector_name ucf --test_dataset UADFV --output_dir training/outputs/ucf_uadfv --save_curves --save_tsne --save_timelines --save_keyframes --save_json --task_id bench-uadfv-ucf

effort:
python training/test.py --detector_name effort --test_dataset UADFV --output_dir training/outputs/effort_uadfv --save_curves --save_tsne --save_timelines --save_keyframes --save_json --task_id bench-uadfv-effort


"""

import os
import json
import numpy as np
from os.path import join, dirname, abspath
import cv2
import random
import time
import yaml
from tqdm import tqdm
import argparse
from collections import defaultdict

import torch
import torch.nn.functional as F
import torch.utils.data

from dataset.abstract_dataset import DeepfakeAbstractBaseDataset
from detectors import DETECTOR
from metrics.utils import get_test_metrics

# 新增：可视化与导出工具
from utils.vis import (
    plot_roc_pr_curves,
    plot_confusion,
    plot_tsne_features,
    plot_video_timelines,
    save_keyframe_thumbnails, plot_video_score_distribution,
)
from utils.json_export import (
    build_deploy_json_per_dataset,
    build_detectionresponse_for_backend,
    post_result_to_backend,
)

DETECTOR_CONFIG_MAP = {
    'xception': 'training/config/detector/xception.yaml',
    'ucf':      'training/config/detector/ucf.yaml',
    'effort':   'training/config/detector/effort.yaml',  # 如果后面要加 Effort
}

DETECTOR_WEIGHTS_MAP = {
    'xception': 'training/weights/xception_best.pth',
    'ucf':      'training/weights/ucf_best.pth',
    'effort':   'training/weights/effort_clip_L14.pth',
}


parser = argparse.ArgumentParser(description='Enhanced Test with Visualization and JSON Export')

parser.add_argument('--detector_name',type=str,default=None,choices=['xception', 'ucf', 'effort'],help='name of detector (e.g., xception / ucf / effort); '
'if set, detector_path and weights_path can be inferred'
)

# 2) 将 detector_path / weights_path 改为可选（和 detector_name 互斥使用）
parser.add_argument(
    '--detector_path',
    type=str,
    default=None,
    help='path to detector YAML file (override detector_name mapping)'
)

parser.add_argument(
    "--test_dataset",
    nargs="+",
    required=True,
    help='one or more dataset names to test'
)

parser.add_argument('--weights_path',type=str,default=None,help='path to model weights (override detector_name mapping)')
# 新增导出与可视化参数
parser.add_argument('--output_dir', type=str, default='./training/outputs', help='dir to save figures and json')
parser.add_argument('--save_json', action='store_true', default=False, help='export detailed json per dataset')
parser.add_argument('--save_curves', action='store_true', default=False, help='save ROC/PR curves and confusion matrix')
parser.add_argument('--save_tsne', action='store_true', default=False, help='save t-SNE feature visualization')
parser.add_argument('--save_timelines', action='store_true', default=False, help='save per-video timeline heatmaps')
parser.add_argument('--save_keyframes', action='store_true', default=False,
                    help='export top-K suspicious frame thumbnails')
parser.add_argument('--topk', type=int, default=8, help='top-K suspicious frames per video')
parser.add_argument('--post_backend', action='store_true', default=False, help='post DetectionResponse JSON to backend')
parser.add_argument('--backend_url', type=str, default='http://backend:8080/api/v1/detections/results',
                    help='backend endpoint')
parser.add_argument('--task_id', type=str, default='', help='task id for backend DetectionResponse packaging')
parser.add_argument('--model_version', type=str, default='DeepfakeBench-v2', help='model version string for packaging')



args = parser.parse_args()
device = torch.device("cuda" if torch.cuda.is_available() else "cpu")


def init_seed(config):
    if config.get('manualSeed', None) is None:
        config['manualSeed'] = random.randint(1, 10000)
    random.seed(config['manualSeed'])
    torch.manual_seed(config['manualSeed'])
    if config.get('cuda', False):
        torch.cuda.manual_seed_all(config['manualSeed'])


def prepare_testing_data(config):
    def get_test_data_loader(config, test_name):
        cfg = config.copy()
        cfg['test_dataset'] = test_name
        test_set = DeepfakeAbstractBaseDataset(config=cfg, mode='test')
        test_loader = torch.utils.data.DataLoader(
            dataset=test_set,
            batch_size=cfg['test_batchSize'],
            shuffle=False,
            num_workers=int(cfg['workers']),
            collate_fn=test_set.collate_fn,
            drop_last=False
        )
        return test_loader, test_set  # 返回 dataset 用于获取路径

    loaders = {}
    datasets = {}
    for one_test_name in config['test_dataset']:
        loader, dataset = get_test_data_loader(config, one_test_name)
        loaders[one_test_name] = loader
        datasets[one_test_name] = dataset
    return loaders, datasets


@torch.no_grad()
def inference(model, data_dict):
    return model(data_dict, inference=True)


def derive_video_id(img_path: str) -> str:
    """
    从图像路径提取视频 ID
    例如: .../UADFV/frames/fake/video_001/frame_0012.png -> fake/video_001
    """
    if not img_path:
        return "unknown"

    parts = img_path.replace('\\', '/').split('/')

    # 尝试找到 frames 目录后的路径
    try:
        frames_idx = parts.index('frames')
        # 取 frames 后面的两级作为视频 ID（类别/视频名）
        if len(parts) > frames_idx + 2:
            return '/'.join(parts[frames_idx + 1:frames_idx + 3])
    except (ValueError, IndexError):
        pass

    # 备选方案：取倒数第二级目录
    if len(parts) >= 2:
        return parts[-2]

    return "unknown"


def test_one_dataset(model, data_loader, dataset):
    prob_list, label_list, feat_list, img_list, vid_index = [], [], [], [], []
    sample_idx = 0

    for batch_idx, data_dict in tqdm(enumerate(data_loader), total=len(data_loader)):
        data, label, mask, landmark = data_dict['image'], data_dict['label'], data_dict['mask'], data_dict['landmark']
        # 二值化标签：>=1 视为 fake
        label_bin = torch.where(label != 0, 1, 0)

        data_dict['image'], data_dict['label'] = data.to(device), label_bin.to(device)
        if mask is not None:
            data_dict['mask'] = mask.to(device)
        if landmark is not None:
            data_dict['landmark'] = landmark.to(device)

        # ===== 统一推理接口 =====
        pred = inference(model, data_dict)

        # 1) 取得概率：
        #    - 若 detector 已经给了 'prob'（如 Xception/F3Net 等），直接用
        #    - 若没有（如 UCF），从 'cls' 里 softmax 计算
        if 'prob' in pred:
            prob_tensor = pred['prob']
        else:
            logits = pred['cls']          # shape: [B, 2]
            prob_tensor = torch.softmax(logits, dim=1)[:, 1]

        prob = prob_tensor.detach().cpu().numpy()

        # 2) 取得特征：
        feat_tensor = pred['feat']       # 所有 detector 都有 'feat'
        feat = feat_tensor.detach().cpu().numpy()

        prob_list.extend(list(prob))
        label_list.extend(list(label_bin.cpu().numpy()))
        feat_list.extend(list(feat))

        # === 路径与 video_id（你之前的逻辑，这里保持不变） ===
        batch_size = len(prob)
        for i in range(batch_size):
            if sample_idx < len(dataset.image_list):
                img_path = dataset.image_list[sample_idx]
                img_list.append(img_path)
                vid_index.append(derive_video_id(img_path))
            else:
                img_list.append(f"unknown_{sample_idx}")
                vid_index.append("unknown")
            sample_idx += 1

    return (
        np.array(prob_list).reshape(-1),
        np.array(label_list).reshape(-1),
        np.array(feat_list),
        img_list,
        vid_index
    )

def test_epoch(model, test_data_loaders, test_datasets, out_dir, options):
    """测试主函数"""
    model.eval()
    metrics_all, per_dataset_details = {}, {}
    os.makedirs(out_dir, exist_ok=True)

    for ds_name in test_data_loaders.keys():
        loader = test_data_loaders[ds_name]
        dataset = test_datasets[ds_name]

        print(f"\n{'=' * 60}")
        print(f"测试数据集: {ds_name}")
        print(f"{'=' * 60}")

        predictions, labels, feats, img_paths, video_ids = test_one_dataset(model, loader, dataset)

        ds_dir = join(out_dir, ds_name)
        os.makedirs(ds_dir, exist_ok=True)

        # 指标计算
        metrics = get_test_metrics(y_pred=predictions, y_true=labels, img_names=img_paths)
        metrics_all[ds_name] = metrics

        print(f"帧级 AUC: {metrics.get('frame_auc', 0):.4f}")
        print(f"视频级 AUC: {metrics.get('video_auc', 0):.4f}")

        # 可视化曲线
        cm_path = None
        if options['save_curves']:
            print("生成 ROC/PR 曲线和混淆矩阵...")
            plot_roc_pr_curves(labels, predictions, save_dir=ds_dir, prefix='curves')
            cm_path = plot_confusion(labels, predictions, save_dir=ds_dir, prefix='confusion')

        # t-SNE
        tsne_path = None
        if options['save_tsne'] and feats.shape[0] > 10:
            print("生成 t-SNE 特征可视化...")
            tsne_path = plot_tsne_features(feats, labels, save_dir=ds_dir, prefix='tsne')

        # 按视频聚合
        video_map = defaultdict(lambda: {'scores': [], 'frames': [], 'labels': []})
        for img_path, pred, label in zip(img_paths, predictions.tolist(), labels.tolist()):
            vid = derive_video_id(img_path)
            video_map[vid]['scores'].append(float(pred))
            video_map[vid]['frames'].append(img_path)
            video_map[vid]['labels'].append(int(label))

        print(f"检测到 {len(video_map)} 个视频")

        # 🆕 企业级可视化：视频分数分布图
        distribution_plot = None
        if options.get('save_distribution', True):  # 默认开启
            print("生成视频分数分布图...")
            distribution_plot = plot_video_score_distribution(video_map, save_dir=ds_dir, prefix='distribution')

        # 🔧 优化：只为 Top-K 视频生成时间轴
        timeline_paths = {}
        if options['save_timelines'] and len(video_map) > 0:
            print(f"生成 Top-{options.get('max_timeline_videos', 10)} 可疑视频的时间轴...")
            timeline_paths = plot_video_timelines(
                video_map,
                save_dir=ds_dir,
                prefix='timeline',
                max_videos=options.get('max_timeline_videos', 10)  # 默认 10 个
            )

        # 🔧 优化：只为 Top-K 视频提取关键帧
        keyframe_paths = {}
        if options['save_keyframes'] and len(video_map) > 0:
            print(f"提取 Top-{options.get('max_keyframe_videos', 10)} 可疑视频的关键帧...")
            keyframe_paths = save_keyframe_thumbnails(
                video_map,
                topk=options['topk'],
                save_dir=ds_dir,
                prefix='keyframes',
                max_videos=options.get('max_keyframe_videos', 10)  # 默认 10 个
            )

        # 组装数据集明细
        per_dataset_details[ds_name] = {
            'predictions': predictions.tolist(),
            'labels': labels.tolist(),
            'image_paths': img_paths,
            'video_map': video_map,
            'curves_dir': ds_dir if options['save_curves'] else None,
            'confusion_path': cm_path,
            'tsne_path': tsne_path,
            'distribution_plot': distribution_plot,  # 🆕 新增
            'timeline_paths': timeline_paths,
            'keyframe_paths': keyframe_paths,
            'metrics': metrics
        }

    return metrics_all, per_dataset_details

def main():
    t0 = time.time()

    detector_path = args.detector_path
    weights_path = args.weights_path

    if args.detector_name is not None:
        # 从映射表获取默认路径
        if detector_path is None:
            if args.detector_name not in DETECTOR_CONFIG_MAP:
                raise ValueError(f"未知的 detector_name: {args.detector_name}")
            detector_path = DETECTOR_CONFIG_MAP[args.detector_name]
        if weights_path is None:
            if args.detector_name not in DETECTOR_WEIGHTS_MAP:
                raise ValueError(f"没有为 {args.detector_name} 配置默认权重路径")
            weights_path = DETECTOR_WEIGHTS_MAP[args.detector_name]

    # 仍然允许老用法：必须保证最终有路径
    if detector_path is None or weights_path is None:
        raise ValueError(
            "必须至少满足以下两种方式之一：\n"
            "1) 仅指定 --detector_name（自动映射 config 和 weights）\n"
            "2) 手动指定 --detector_path 和 --weights_path"
        )

    print(f"[INFO] 使用 detector: {args.detector_name or 'from_yaml'}")
    print(f"[INFO] detector_config: {detector_path}")
    print(f"[INFO] weights_path:    {weights_path}")

    # =========================================================
    # 2) 加载配置 / 模型（使用上面解析出的路径）
    # =========================================================
    with open(detector_path, 'r') as f:
        config = yaml.safe_load(f)
    with open('./training/config/test_config.yaml', 'r') as f:
        cfg2 = yaml.safe_load(f)
    config.update(cfg2)
    if 'label_dict' in config:
        cfg2['label_dict'] = config['label_dict']

    # 覆盖测试集与权重
    config['test_dataset'] = args.test_dataset
    config['weights_path'] = weights_path

    init_seed(config)

    test_loaders, test_datasets = prepare_testing_data(config)

    # 加载模型
    model_class = DETECTOR[config['model_name']]
    model = model_class(config).to(device)

    # 加载权重
    ckpt = torch.load(weights_path, map_location=device)

    # ========== 🔧 处理权重 key 的各种情况 ==========
    print(f"原始权重 keys 示例: {list(ckpt.keys())[:3]}")

    # 1. 去掉 DataParallel 的 module. 前缀
    if list(ckpt.keys())[0].startswith('module.'):
        print("⚠️  检测到 DataParallel 权重（有 module. 前缀），正在转换...")
        new_ckpt = {}
        for k, v in ckpt.items():
            new_key = k.replace('module.', '', 1)
            new_ckpt[new_key] = v
        ckpt = new_ckpt
        print("✅ 去除 module. 前缀完成")

    # 2. 处理 Effort 的 vision_model 路径差异
    # 权重: backbone.embeddings.xxx
    # 模型: backbone.vision_model.embeddings.xxx
    if config['model_name'] == 'effort':
        first_key = list(ckpt.keys())[0]
        if first_key.startswith('backbone.') and 'vision_model' not in first_key:
            print("⚠️  检测到 Effort 权重路径不匹配，正在转换...")
            new_ckpt = {}
            for k, v in ckpt.items():
                if k.startswith('backbone.'):
                    # backbone.embeddings.xxx -> backbone.vision_model.embeddings.xxx
                    new_key = k.replace('backbone.', 'backbone.vision_model.', 1)
                    new_ckpt[new_key] = v
                else:
                    # head.weight 等保持不变
                    new_ckpt[k] = v
            ckpt = new_ckpt
            print("✅ Effort 权重路径转换完成")

    print(f"转换后权重 keys 示例: {list(ckpt.keys())[:3]}")
    # ========== 修复结束 ==========

    model.load_state_dict(ckpt, strict=False)
    print(f"✅ 模型加载成功: {config['model_name']}")

    # 输出目录
    os.makedirs(args.output_dir, exist_ok=True)

    # 可视化选项
    options = {
        'save_curves': args.save_curves,
        'save_tsne': args.save_tsne,
        'save_timelines': args.save_timelines,
        'save_keyframes': args.save_keyframes,
        'save_distribution': True,  # 🆕 默认开启分布图
        'topk': args.topk,
        'max_timeline_videos': 10,  # 🔧 只为 Top-10 视频生成时间轴
        'max_keyframe_videos': 10   # 🔧 只为 Top-10 视频提取关键帧
    }

    # 执行测试
    metrics_all, details = test_epoch(model, test_loaders, test_datasets, args.output_dir, options)

    print('\n' + '=' * 60)
    print('🎉 测试完成！')
    print('=' * 60)

    # 打印指标摘要
    for ds_name, metrics in metrics_all.items():
        print(f"\n{ds_name}:")
        print(f"  帧级 AUC: {metrics.get('frame_auc', 0):.4f}")
        print(f"  视频级 AUC: {metrics.get('video_auc', 0):.4f}")

    # 导出 JSON
    if args.save_json:
        print("\n生成 JSON 导出文件...")
        deploy_json_path = build_deploy_json_per_dataset(
            output_dir=args.output_dir,
            all_details=details,
            all_metrics=metrics_all,
            task_id=args.task_id or 'benchmark-task',
            model_version=args.model_version
        )
        print(f'✅ JSON 已保存: {deploy_json_path}')

        # 可选：回传后端
        if args.post_backend:
            print("\n发送结果到后端...")
            detection_json = build_detectionresponse_for_backend(
                all_details=details,
                task_id=args.task_id or 'benchmark-task',
                model_version=args.model_version
            )
            ok, resp_text = post_result_to_backend(args.backend_url, detection_json)
            if ok:
                print(f'✅ 后端接收成功')
            else:
                print(f'❌ 后端接收失败: {resp_text}')

    print(f'\n总耗时: {time.time() - t0:.2f}秒')


if __name__ == '__main__':
    main()
