# DeepfakeBench 检测服务 API 对接文档

本文档描述了 DeepfakeBench 本地检测服务的 API 接口规范，用于后端系统集成。

## 1. 服务信息

- **Base URL**: `http://127.0.0.1:5000`
- **协议**: HTTP/1.1
- **数据格式**: JSON

---

## 2. 接口列表

### 2.1 健康检查 (Health Check)

用于检测服务是否存活以及 GPU 状态。

- **URL**: `/health`
- **Method**: `GET`

**响应示例**:
```json
{
    "service": "AI Detection Service",
    "status": "running",
    "gpu_available": true,
    "timestamp": "2025-12-23T17:50:00"
}
```

---

### 2.2 视频/图片检测 (Video Detection)

提交视频或图片进行深伪检测。支持多种检测模式。

- **URL**: `/api/detect`
- **Method**: `POST`
- **Content-Type**: `application/json`

#### 请求参数 (Request Body)

| 字段名 | 类型 | 必选 | 描述 | 默认值 |
| :--- | :--- | :--- | :--- | :--- |
| `taskId` | string | 是 | 任务唯一标识 ID (后端生成) | - |
| `videoPath` | string | 是 | 待检测文件的绝对路径 (支持视频和图片) | - |
| `mode` | string | 否 | **检测模式**。<br>`standard`: 人脸伪造检测 (Effort)<br>`aigc`: 通用生成检测 (DRCT) | `standard` |
| `fileHash` | string | 否 | 文件哈希 (用于校验) | "" |
| `frameRate` | int | 否 | 视频抽帧频率 (每秒几帧) | 5 |
| `maxFrames` | int | 否 | 最大检测帧数 | 300 |

#### 模式说明 (`mode`)

1.  **`standard` (默认)**
    *   **适用场景**: 视频/图片中包含人脸，检测是否为 Deepfake 换脸或表情操纵。
    *   **底层模型**: `Effort` (基于 Dlib 人脸检测 + Xception/CLIP)。
    *   **注意**: 如果画面中从未检测到人脸，将返回错误或无法判定。

2.  **`aigc`**
    *   **适用场景**: 纯生成的视频/图片（如 Sora, Midjourney, Stable Diffusion），或者不一定包含人脸的伪造。
    *   **底层模型**: `DRCT` (基于 CLIP + ViT)。
    *   **特点**: 对画面整体进行分析，不依赖人脸检测。

#### 响应参数 (Response Body)

| 字段名 | 类型 | 描述 |
| :--- | :--- | :--- |
| `taskId` | string | 对应的任务 ID |
| `result` | string | 最终判定结果: `FAKE` (伪造) 或 `REAL` (真实) |
| `confidence` | float | 置信度 (0.01.0)，越高越可信 |
| `features` | object | 多维度特征分数 (详见下方) |
| `artifacts` | list[string] | 检测到的伪造痕迹列表 |
| `processingTimeMs` | int | 处理耗时 (毫秒) |
| `modelVersion` | string | 使用的模型名称 (`effort` 或 `drct`) |

**`features` 对象结构**:

| 字段名 | 类型 | 描述 |
| :--- | :--- | :--- |
| `temporal_consistency` | float | 时序一致性 (视频专用) |
| `facial_landmarks_score` | float | 面部关键点自然度 |
| `compression_artifacts` | float | 压缩痕迹异常度 |

#### 示例

**请求 (检测 Deepfake 视频)**:
```json
{
    "taskId": "task_1001",
    "videoPath": "G:\\uploads\\video_01.mp4",
    "mode": "standard",
    "frameRate": 2
}
```

**请求 (检测 AIGC 生成图片/视频)**:
```json
{
    "taskId": "task_1002",
    "videoPath": "G:\\uploads\\sora_demo.mp4",
    "mode": "aigc"
}
```

**成功响应**:
```json
{
    "taskId": "task_1001",
    "result": "FAKE",
    "confidence": 0.985,
    "modelVersion": "effort",
    "processingTimeMs": 2350,
    "framesAnalyzed": 20,
    "features": {
        "temporal_consistency": 0.45,
        "facial_landmarks_score": 0.32,
        "compression_artifacts": 0.88,
        "color_histogram_score": 0.85,
        "optical_flow_score": 0.90
    },
    "artifacts": [
        "temporal_inconsistencies",
        "unnatural_facial_movements"
    ],
    "metadata": {
        "resolution": "1920x1080",
        "duration": 15.4,
        "codec": "h264",
        "bitrate": 5000000
    }
}
```

**失败响应 (如文件不存在)**:
```json
{
    "error": "无法打开视频文件: G:\\uploads\\missing.mp4"
}
```
(HTTP 状态码通常为 500)

---

## 3. 错误码

- **200**: 成功
- **404**: 路径未找到
- **500**: 服务器内部错误 (如模型加载失败、文件读取错误)

## 4. 常见问题

- **Q: 为什么 `standard` 模式下提示 "No valid frames/faces detected"?**
  - A: 可以在视频中未检测到清晰的人脸。请确认视频中有人脸，或者尝试切换到 `aigc` 模式。

- **Q: 首次请求很慢？**
  - A: 模型在第一次请求时会进行懒加载并加载到 GPU/CPU 内存中，后续请求会变快。
