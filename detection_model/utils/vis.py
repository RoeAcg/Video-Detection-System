# training/utils/vis.py
"""
可视化工具模块
包含 ROC/PR 曲线、混淆矩阵、t-SNE、时间轴热力图、关键帧提取等
"""

import os
import sys
import numpy as np
import matplotlib

matplotlib.use('Agg')
import matplotlib.pyplot as plt
from matplotlib.font_manager import FontProperties
import platform
import logging


# =====================================================
# 🔧 中文字体配置（解决中文乱码问题）
# =====================================================

def setup_chinese_font():
    """
    配置 matplotlib 中文字体
    自动检测系统并设置合适的中文字体
    """
    system = platform.system()

    try:
        if system == 'Windows':
            # Windows 系统：优先使用微软雅黑
            plt.rcParams['font.sans-serif'] = ['Microsoft YaHei', 'SimHei', 'SimSun', 'KaiTi']
        elif system == 'Darwin':  # macOS
            # macOS 系统：使用苹方或黑体
            plt.rcParams['font.sans-serif'] = ['PingFang SC', 'Heiti SC', 'STHeiti']
        else:  # Linux
            # Linux 系统：使用文泉驿或 Noto
            plt.rcParams['font.sans-serif'] = ['WenQuanYi Micro Hei', 'WenQuanYi Zen Hei', 'Noto Sans CJK SC', 'SimHei']

        # 用来正常显示负号
        plt.rcParams['axes.unicode_minus'] = False

        # 设置默认字体大小
        plt.rcParams['font.size'] = 10

        logging.info(f"✅ 中文字体配置成功 (系统: {system})")

    except Exception as e:
        logging.warning(f"⚠️ 中文字体配置失败: {str(e)}，图表中文可能显示为方框")
        # 即使失败也继续运行
        pass


# 初始化中文字体（模块加载时自动执行）
setup_chinese_font()

# =====================================================
# 其他导入
# =====================================================

from sklearn.metrics import (
    roc_curve, auc, precision_recall_curve,
    average_precision_score, confusion_matrix
)
from sklearn.manifold import TSNE
import seaborn as sns
import cv2
from collections import Counter

logger = logging.getLogger(__name__)


# =====================================================
# 可视化函数
# =====================================================

def plot_roc_pr_curves(y_true, y_score, save_dir, prefix='curves'):
    """
    生成 ROC 和 PR 曲线

    Args:
        y_true: 真实标签
        y_score: 预测分数
        save_dir: 保存目录
        prefix: 文件名前缀
    """
    os.makedirs(save_dir, exist_ok=True)

    try:
        fpr, tpr, _ = roc_curve(y_true, y_score)
        roc_auc = auc(fpr, tpr)
        prec, rec, _ = precision_recall_curve(y_true, y_score)
        ap = average_precision_score(y_true, y_score)

        plt.figure(figsize=(12, 5))

        # ROC 曲线
        plt.subplot(1, 2, 1)
        plt.plot(fpr, tpr, linewidth=2, label=f'AUC={roc_auc:.4f}')
        plt.plot([0, 1], [0, 1], 'k--', linewidth=1)
        plt.xlabel('假阳性率 (FPR)', fontsize=12)
        plt.ylabel('真阳性率 (TPR)', fontsize=12)
        plt.title('ROC 曲线', fontsize=14, fontweight='bold')
        plt.legend(loc='lower right')
        plt.grid(alpha=0.3)

        # PR 曲线
        plt.subplot(1, 2, 2)
        plt.plot(rec, prec, linewidth=2, label=f'AP={ap:.4f}')
        plt.xlabel('召回率 (Recall)', fontsize=12)
        plt.ylabel('精确率 (Precision)', fontsize=12)
        plt.title('精确率-召回率曲线', fontsize=14, fontweight='bold')
        plt.legend(loc='lower left')
        plt.grid(alpha=0.3)

        out = os.path.join(save_dir, f'{prefix}_roc_pr.png')
        plt.tight_layout()
        plt.savefig(out, dpi=150, bbox_inches='tight')
        plt.close()

        logger.info(f"✅ ROC/PR 曲线已保存: {out}")
        return out

    except Exception as e:
        logger.error(f"❌ ROC/PR 曲线生成失败: {str(e)}")
        return None


def plot_confusion(y_true, y_score, save_dir, prefix='confusion', thresh=0.5):
    """
    生成混淆矩阵

    Args:
        y_true: 真实标签
        y_score: 预测分数
        save_dir: 保存目录
        prefix: 文件名前缀
        thresh: 分类阈值
    """
    os.makedirs(save_dir, exist_ok=True)

    try:
        y_pred = (np.array(y_score) >= thresh).astype(int)
        cm = confusion_matrix(y_true, y_pred, labels=[0, 1])

        plt.figure(figsize=(6, 5))
        sns.heatmap(
            cm, annot=True, fmt='d', cmap='Blues',
            xticklabels=['真实 (Real)', '伪造 (Fake)'],
            yticklabels=['真实 (Real)', '伪造 (Fake)'],
            cbar_kws={'label': '数量'}
        )
        plt.xlabel('预测标签', fontsize=12)
        plt.ylabel('真实标签', fontsize=12)
        plt.title(f'混淆矩阵 (阈值={thresh})', fontsize=14, fontweight='bold')

        out = os.path.join(save_dir, f'{prefix}.png')
        plt.tight_layout()
        plt.savefig(out, dpi=150, bbox_inches='tight')
        plt.close()

        logger.info(f"✅ 混淆矩阵已保存: {out}")
        return out

    except Exception as e:
        logger.error(f"❌ 混淆矩阵生成失败: {str(e)}")
        return None


def plot_tsne_features(feats, labels, save_dir, prefix='tsne'):
    """
    生成 t-SNE 特征可视化

    Args:
        feats: 特征向量
        labels: 标签
        save_dir: 保存目录
        prefix: 文件名前缀
    """
    os.makedirs(save_dir, exist_ok=True)

    try:
        feats = np.array(feats)
        labels = np.array(labels)

        logger.info(f"特征形状: {feats.shape}, 标签形状: {labels.shape}")

        # 处理多维特征
        if feats.ndim > 2:
            logger.info(f"检测到 {feats.ndim}D 特征，正在 flatten...")
            feats = feats.reshape(feats.shape[0], -1)
            logger.info(f"Flatten 后形状: {feats.shape}")

        # 样本数检查
        if feats.shape[0] < 10:
            logger.warning(f"样本数太少 ({feats.shape[0]} < 10)，跳过 t-SNE")
            return None

        # PCA 预处理（高维特征）
        if feats.shape[1] > 50:
            from sklearn.decomposition import PCA
            logger.info(f"特征维度较高 ({feats.shape[1]})，先用 PCA 降到 50 维")
            pca = PCA(n_components=50)
            feats = pca.fit_transform(feats)

        # t-SNE
        perplexity = min(30, max(5, feats.shape[0] // 3))
        logger.info(f"运行 t-SNE (perplexity={perplexity})...")

        tsne = TSNE(
            n_components=2, init='pca', learning_rate='auto',
            perplexity=perplexity, n_iter=1000, random_state=42
        )
        emb = tsne.fit_transform(feats)

        # 绘制散点图
        plt.figure(figsize=(8, 7))
        real_mask = labels == 0
        fake_mask = labels == 1

        if real_mask.any():
            plt.scatter(
                emb[real_mask, 0], emb[real_mask, 1],
                c='green', s=30, alpha=0.6, label='真实视频',
                edgecolors='k', linewidth=0.5
            )
        if fake_mask.any():
            plt.scatter(
                emb[fake_mask, 0], emb[fake_mask, 1],
                c='red', s=30, alpha=0.6, label='伪造视频',
                edgecolors='k', linewidth=0.5
            )

        plt.xlabel('t-SNE 维度 1', fontsize=12)
        plt.ylabel('t-SNE 维度 2', fontsize=12)
        plt.title('特征空间可视化 (t-SNE)', fontsize=14, fontweight='bold')
        plt.legend(loc='best')
        plt.grid(alpha=0.3)

        out = os.path.join(save_dir, f'{prefix}.png')
        plt.tight_layout()
        plt.savefig(out, dpi=150, bbox_inches='tight')
        plt.close()

        logger.info(f"✅ t-SNE 可视化已保存: {out}")
        return out

    except Exception as e:
        logger.error(f"❌ t-SNE 可视化失败: {str(e)}")
        return None


def plot_video_score_distribution(video_map, save_dir, prefix='distribution'):
    """
    🆕 企业级可视化：视频分数分布图
    展示所有视频的平均伪造概率分布

    Args:
        video_map: {video_id: {'scores': [float], 'labels': [int]}}
        save_dir: 保存目录
        prefix: 文件名前缀
    """
    os.makedirs(save_dir, exist_ok=True)

    try:
        video_scores = []
        video_labels = []

        for vid, obj in video_map.items():
            scores = obj['scores']
            labels = obj.get('labels', [])

            if len(scores) > 0:
                avg_score = np.mean(scores)
                video_scores.append(avg_score)

                # 视频真实标签（多数投票）
                if len(labels) > 0:
                    video_label = 1 if np.mean(labels) > 0.5 else 0
                    video_labels.append(video_label)

        if len(video_scores) == 0:
            return None

        video_scores = np.array(video_scores)

        # 创建图表
        fig, axes = plt.subplots(2, 2, figsize=(14, 10))

        # 1. 分数直方图
        ax = axes[0, 0]
        if len(video_labels) > 0:
            real_scores = video_scores[np.array(video_labels) == 0]
            fake_scores = video_scores[np.array(video_labels) == 1]

            ax.hist(real_scores, bins=30, alpha=0.6, label='真实视频', color='green', edgecolor='black')
            ax.hist(fake_scores, bins=30, alpha=0.6, label='伪造视频', color='red', edgecolor='black')
            ax.legend()
        else:
            ax.hist(video_scores, bins=50, alpha=0.7, color='blue', edgecolor='black')

        ax.axvline(x=0.5, color='gray', linestyle='--', linewidth=2, label='阈值=0.5')
        ax.set_xlabel('平均伪造概率', fontsize=11)
        ax.set_ylabel('视频数量', fontsize=11)
        ax.set_title('视频分数分布', fontsize=12, fontweight='bold')
        ax.grid(alpha=0.3)
        ax.legend()

        # 2. 箱线图
        ax = axes[0, 1]
        if len(video_labels) > 0:
            data_to_plot = [real_scores, fake_scores]
            bp = ax.boxplot(data_to_plot, labels=['真实', '伪造'], patch_artist=True)
            for patch in bp['boxes']:
                patch.set_facecolor('lightblue')
                patch.set_alpha(0.6)
        else:
            ax.boxplot([video_scores], labels=['所有视频'], patch_artist=True)

        ax.axhline(y=0.5, color='gray', linestyle='--', linewidth=1.5)
        ax.set_ylabel('伪造概率', fontsize=11)
        ax.set_title('分数分布 (箱线图)', fontsize=12, fontweight='bold')
        ax.grid(alpha=0.3, axis='y')

        # 3. CDF 累积分布
        ax = axes[1, 0]
        sorted_scores = np.sort(video_scores)
        cdf = np.arange(1, len(sorted_scores) + 1) / len(sorted_scores)
        ax.plot(sorted_scores, cdf, linewidth=2, color='purple')
        ax.axvline(x=0.5, color='gray', linestyle='--', linewidth=1.5, label='阈值')
        ax.set_xlabel('伪造概率', fontsize=11)
        ax.set_ylabel('累积概率', fontsize=11)
        ax.set_title('累积分布函数 (CDF)', fontsize=12, fontweight='bold')
        ax.grid(alpha=0.3)
        ax.legend()

        # 4. 统计摘要
        ax = axes[1, 1]
        ax.axis('off')

        stats_text = f"""
📊 统计摘要
━━━━━━━━━━━━━━━━━━━━
总视频数: {len(video_scores)}

分数统计:
  • 均值: {np.mean(video_scores):.4f}
  • 中位数: {np.median(video_scores):.4f}
  • 标准差: {np.std(video_scores):.4f}
  • 最小值: {np.min(video_scores):.4f}
  • 最大值: {np.max(video_scores):.4f}

阈值 0.5 分类:
  • 伪造 (>0.5): {np.sum(video_scores > 0.5)} ({np.sum(video_scores > 0.5) / len(video_scores) * 100:.1f}%)
  • 真实 (≤0.5): {np.sum(video_scores <= 0.5)} ({np.sum(video_scores <= 0.5) / len(video_scores) * 100:.1f}%)
        """

        if len(video_labels) > 0:
            real_count = np.sum(np.array(video_labels) == 0)
            fake_count = np.sum(np.array(video_labels) == 1)
            stats_text += f"""
真实标签:
  • 真实: {real_count}
  • 伪造: {fake_count}
            """

        ax.text(0.1, 0.5, stats_text, fontsize=10, family='monospace',
                verticalalignment='center')

        plt.tight_layout()
        out = os.path.join(save_dir, f'{prefix}_video_scores.png')
        plt.savefig(out, dpi=150, bbox_inches='tight')
        plt.close()

        logger.info(f"✅ 视频分数分布图已保存: {out}")
        return out

    except Exception as e:
        logger.error(f"❌ 视频分数分布图生成失败: {str(e)}")
        return None


def plot_video_timelines(video_map, save_dir, prefix='timeline', max_videos=10):
    """
    生成视频时间轴热力图

    🔧 优化：只为 Top-K 最可疑视频生成

    Args:
        video_map: {video_id: {'scores': [float], 'frames': [path]}}
        save_dir: 保存目录
        prefix: 文件名前缀
        max_videos: 最多生成多少个视频的时间轴
    """
    os.makedirs(save_dir, exist_ok=True)
    out_paths = {}

    try:
        # 按平均分数排序，只取 Top-K
        video_scores = {}
        for vid, obj in video_map.items():
            scores = obj['scores']
            if len(scores) > 0:
                video_scores[vid] = np.mean(scores)

        # 选择最可疑的 K 个视频
        top_videos = sorted(video_scores.items(), key=lambda x: x[1], reverse=True)[:max_videos]

        logger.info(f"从 {len(video_map)} 个视频中选择 Top-{len(top_videos)} 生成时间轴")

        for vid, avg_score in top_videos:
            obj = video_map[vid]
            scores = np.array(obj['scores'], dtype=float)

            if len(scores) == 0:
                continue

            # 创建图表
            fig, ax = plt.subplots(figsize=(14, 3))

            # 绘制热力图
            heatmap = scores.reshape(1, -1)
            im = ax.imshow(heatmap, cmap='RdYlGn_r', aspect='auto', vmin=0, vmax=1)

            # 添加颜色条
            cbar = plt.colorbar(im, ax=ax, label='伪造概率')

            # 标题和标签
            ax.set_xlabel('帧索引', fontsize=11)
            ax.set_title(f'时间轴: {vid} (平均分数: {avg_score:.3f})', fontsize=12, fontweight='bold')
            ax.set_yticks([])

            # 保存
            safe_vid = vid.replace('/', '_').replace('\\', '_').replace(':', '_')
            out = os.path.join(save_dir, f'{prefix}_{safe_vid}.png')
            plt.tight_layout()
            plt.savefig(out, dpi=120, bbox_inches='tight')
            plt.close()

            out_paths[vid] = out

        logger.info(f"✅ 生成了 {len(out_paths)} 个时间轴热力图")

    except Exception as e:
        logger.error(f"❌ 时间轴热力图生成失败: {str(e)}")

    return out_paths


def save_keyframe_thumbnails(video_map, topk, save_dir, prefix='keyframes', max_videos=10):
    """
    提取并保存关键帧

    🔧 优化：只为 Top-K 最可疑视频提取关键帧

    Args:
        video_map: {video_id: {'scores': [float], 'frames': [path]}}
        topk: 每个视频提取多少关键帧
        save_dir: 保存目录
        prefix: 文件名前缀
        max_videos: 最多处理多少个视频
    """
    os.makedirs(save_dir, exist_ok=True)
    out = {}

    try:
        # 按平均分数排序
        video_scores = {}
        for vid, obj in video_map.items():
            scores = obj['scores']
            if len(scores) > 0:
                video_scores[vid] = np.mean(scores)

        # 选择最可疑的视频
        top_videos = sorted(video_scores.items(), key=lambda x: x[1], reverse=True)[:max_videos]

        logger.info(f"从 {len(video_map)} 个视频中选择 Top-{len(top_videos)} 提取关键帧")

        total_frames_saved = 0

        for vid, avg_score in top_videos:
            obj = video_map[vid]
            scores = np.array(obj['scores'])
            frames = obj['frames']

            if len(scores) == 0:
                continue

            # 选择 Top-K 最可疑的帧
            k = min(topk, len(scores))
            idx = np.argsort(-scores)[:k]

            thumbs = []
            for rank, i in enumerate(idx):
                fp = frames[i]

                # 检查文件是否存在
                if not os.path.exists(fp):
                    continue

                # 读取图像
                img = cv2.imread(fp)
                if img is None:
                    continue

                # 添加标注
                s = float(scores[i])
                overlay = img.copy()

                # 半透明背景
                cv2.rectangle(overlay, (5, 5), (300, 60), (0, 0, 0), -1)
                img = cv2.addWeighted(overlay, 0.6, img, 0.4, 0)

                # 文字（使用 OpenCV 的默认字体）
                text = f'Fake: {s:.3f} (#{rank + 1})'
                color = (0, 0, 255) if s > 0.5 else (0, 255, 0)
                cv2.putText(
                    img, text, (10, 35),
                    cv2.FONT_HERSHEY_SIMPLEX, 0.9, color, 2, cv2.LINE_AA
                )

                # 保存
                safe_vid = vid.replace('/', '_').replace('\\', '_').replace(':', '_')
                out_path = os.path.join(save_dir, f'{prefix}_{safe_vid}_{rank + 1:02d}.jpg')
                cv2.imwrite(out_path, img)

                thumbs.append({
                    'frame_path': fp,
                    'thumb_path': out_path,
                    'score': float(s),
                    'rank': rank + 1
                })

                total_frames_saved += 1

            if thumbs:
                out[vid] = thumbs

        logger.info(f"✅ 提取了 {total_frames_saved} 个关键帧（{len(out)} 个视频）")

    except Exception as e:
        logger.error(f"❌ 关键帧提取失败: {str(e)}")

    return out
