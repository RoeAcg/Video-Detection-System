/*
 Navicat Premium Data Transfer

 Source Server         : localhost_5432
 Source Server Type    : PostgreSQL
 Source Server Version : 180001 (180001)
 Source Host           : localhost:5432
 Source Catalog        : video_detection
 Source Schema         : public

 Target Server Type    : PostgreSQL
 Target Server Version : 180001 (180001)
 File Encoding         : 65001

 Date: 25/12/2025 17:26:19
*/


-- ----------------------------
-- Sequence structure for audit_logs_id_seq
-- ----------------------------
CREATE SEQUENCE "audit_logs_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for chunk_metadata_id_seq
-- ----------------------------
CREATE SEQUENCE "chunk_metadata_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for detection_results_id_seq
-- ----------------------------
CREATE SEQUENCE "detection_results_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for detection_tasks_id_seq
-- ----------------------------
CREATE SEQUENCE "detection_tasks_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for reports_id_seq
-- ----------------------------
CREATE SEQUENCE "reports_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for roles_id_seq
-- ----------------------------
CREATE SEQUENCE "roles_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for users_id_seq
-- ----------------------------
CREATE SEQUENCE "users_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for videos_id_seq
-- ----------------------------
CREATE SEQUENCE "videos_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Table structure for audit_logs
-- ----------------------------
CREATE TABLE "audit_logs" (
  "id" int8 NOT NULL DEFAULT nextval('audit_logs_id_seq'::regclass),
  "user_id" int8,
  "action" varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
  "resource_type" varchar(50) COLLATE "pg_catalog"."default",
  "resource_id" int8,
  "old_value" varchar(2000) COLLATE "pg_catalog"."default",
  "new_value" varchar(2000) COLLATE "pg_catalog"."default",
  "ip_address" varchar(45) COLLATE "pg_catalog"."default",
  "user_agent" varchar(500) COLLATE "pg_catalog"."default",
  "request_method" varchar(10) COLLATE "pg_catalog"."default",
  "request_uri" varchar(500) COLLATE "pg_catalog"."default",
  "status_code" int4,
  "created_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updated_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP
)
;
COMMENT ON COLUMN "audit_logs"."action" IS '操作名称（如USER_LOGIN）';
COMMENT ON COLUMN "audit_logs"."resource_type" IS '资源类型（如USER、VIDEO）';
COMMENT ON COLUMN "audit_logs"."ip_address" IS '客户端IP地址（支持IPv6）';
COMMENT ON TABLE "audit_logs" IS '审计日志表';

-- ----------------------------
-- Records of audit_logs
-- ----------------------------
BEGIN;
INSERT INTO "audit_logs" ("id", "user_id", "action", "resource_type", "resource_id", "old_value", "new_value", "ip_address", "user_agent", "request_method", "request_uri", "status_code", "created_at", "updated_at") VALUES (74, 5, 'USER_LOGIN', 'user', 5, NULL, '用户登录: testuser', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36 Edg/142.0.0.0', 'POST', '/api/auth/login', 200, '2025-11-20 18:31:37.948966', '2025-11-20 18:31:37.948966'), (75, 5, 'VIDEO_UPLOAD', 'video', 23, NULL, '上传视频: 中华农耕(去除人声).mp4 (32.84 MB)', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36 Edg/142.0.0.0', 'POST', '/api/videos/upload', 200, '2025-11-20 18:32:10.9178', '2025-11-20 18:32:10.9178'), (76, 5, 'VIDEO_DELETE', 'video', 23, '视频: 中华农耕(去除人声).mp4', '已删除', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36 Edg/142.0.0.0', 'DELETE', '/api/videos/23', 200, '2025-11-20 18:32:20.382231', '2025-11-20 18:32:20.382231'), (77, 5, 'USER_LOGIN', 'user', 5, NULL, '用户登录: testuser', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36 Edg/142.0.0.0', 'POST', '/api/auth/login', 200, '2025-11-20 19:10:14.348079', '2025-11-20 19:10:14.348079'), (78, 5, 'USER_LOGIN', 'user', 5, NULL, '用户登录: testuser', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36 Edg/142.0.0.0', 'POST', '/api/auth/login', 200, '2025-11-20 19:10:44.224275', '2025-11-20 19:10:44.224275'), (79, 5, 'USER_LOGIN', 'user', 5, NULL, '用户登录: testuser', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36 Edg/142.0.0.0', 'POST', '/api/auth/login', 200, '2025-11-20 19:24:37.222861', '2025-11-20 19:24:37.222861'), (80, 5, 'USER_LOGIN', 'user', 5, NULL, '用户登录: testuser', '0:0:0:0:0:0:0:1', 'PostmanRuntime/7.49.1', 'POST', '/api/auth/login', 200, '2025-11-20 19:38:12.984347', '2025-11-20 19:38:12.984347'), (81, 5, 'USER_LOGIN', 'user', 5, NULL, '用户登录: testuser', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36 Edg/142.0.0.0', 'POST', '/api/auth/login', 200, '2025-11-20 19:58:11.910991', '2025-11-20 19:58:11.910991'), (83, 6, 'USER_LOGIN', 'user', 6, NULL, '用户登录: qweq', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36 Edg/142.0.0.0', 'POST', '/api/auth/login', 200, '2025-11-20 20:00:13.595566', '2025-11-20 20:00:13.595566'), (84, 4, 'USER_LOGIN', 'user', 4, NULL, '用户登录: admin', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36 Edg/142.0.0.0', 'POST', '/api/auth/login', 200, '2025-11-20 20:00:46.346503', '2025-11-20 20:00:46.346503'), (85, 7, 'USER_REGISTER', 'user', 7, NULL, '用户注册: jack (jack@example.com)', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36 Edg/142.0.0.0', 'POST', '/api/auth/register', 200, '2025-11-20 20:11:02.185232', '2025-11-20 20:11:02.185232'), (86, 7, 'USER_LOGIN', 'user', 7, NULL, '用户登录: jack', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36 Edg/142.0.0.0', 'POST', '/api/auth/login', 200, '2025-11-20 20:11:03.353774', '2025-11-20 20:11:03.353774'), (87, 4, 'USER_LOGIN', 'user', 4, NULL, '用户登录: admin', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36 Edg/142.0.0.0', 'POST', '/api/auth/login', 200, '2025-11-20 20:11:14.929645', '2025-11-20 20:11:14.929645'), (88, 4, 'VIDEO_UPLOAD', 'video', 24, NULL, '上传视频: 中华农耕(去除人声).mp4 (32.84 MB)', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36 Edg/142.0.0.0', 'POST', '/api/videos/upload', 200, '2025-11-20 20:31:02.602329', '2025-11-20 20:31:02.602329'), (89, 5, 'USER_LOGIN', 'user', 5, NULL, '用户登录: testuser', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36 Edg/142.0.0.0', 'POST', '/api/auth/login', 200, '2025-11-20 20:39:00.512464', '2025-11-20 20:39:00.512464'), (90, 5, 'VIDEO_UPLOAD', 'video', 25, NULL, '上传视频: 裁剪版.mp4 (106.04 MB)', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36 Edg/142.0.0.0', 'POST', '/api/videos/upload', 200, '2025-11-20 20:39:22.733057', '2025-11-20 20:39:22.733057'), (91, 4, 'USER_LOGIN', 'user', 4, NULL, '用户登录: admin', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36 Edg/142.0.0.0', 'POST', '/api/auth/login', 200, '2025-11-20 20:54:52.508734', '2025-11-20 20:54:52.508734'), (92, 4, 'VIDEO_DELETE', 'video', 24, '视频: 中华农耕(去除人声).mp4', '已删除', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36 Edg/142.0.0.0', 'DELETE', '/api/videos/24', 200, '2025-11-20 20:55:17.929423', '2025-11-20 20:55:17.929423'), (93, 4, 'VIDEO_UPLOAD', 'video', 26, NULL, '上传视频: 中华农耕(去除人声).mp4 (32.84 MB)', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36 Edg/142.0.0.0', 'POST', '/api/videos/upload', 200, '2025-11-20 20:55:29.307951', '2025-11-20 20:55:29.307951'), (94, 4, 'DETECTION_START', 'detection_task', 26, NULL, '开始检测任务: f2f2346e-a430-42c6-a00b-4b1769ca03c5', 'SYSTEM', 'Worker Service', 'ASYNC', '/worker/detection', 200, '2025-11-20 20:55:29.650266', '2025-11-20 20:55:29.650266'), (95, 4, 'DETECTION_COMPLETE', 'detection_result', 22, '任务ID: f2f2346e-a430-42c6-a00b-4b1769ca03c5', '检测完成 - 任务: f2f2346e-a430-42c6-a00b-4b1769ca03c5, 结果: AUTHENTIC, 置信度: 30.11%, 处理时间: nullms, 分析帧数: null', 'SYSTEM', 'Worker Service', 'ASYNC', '/worker/detection', 200, '2025-11-20 20:55:30.713137', '2025-11-20 20:55:30.713137'), (96, 5, 'USER_LOGIN', 'user', 5, NULL, '用户登录: testuser', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36 Edg/142.0.0.0', 'POST', '/api/auth/login', 200, '2025-11-20 21:42:15.719834', '2025-11-20 21:42:15.719834'), (97, 5, 'VIDEO_UPLOAD', 'video', 27, NULL, '上传视频: 成片一版.mp4 (32.72 MB)', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36 Edg/142.0.0.0', 'POST', '/api/videos/upload', 200, '2025-11-20 21:42:29.844253', '2025-11-20 21:42:29.844253'), (98, 5, 'DETECTION_START', 'detection_task', 27, NULL, '开始检测视频: 成片一版.mp4', 'SYSTEM', 'Worker Service', 'ASYNC', '/worker/detection', 200, '2025-11-20 21:42:30.185078', '2025-11-20 21:42:30.185078'), (99, 5, 'DETECTION_COMPLETE', 'detection_result', 23, '任务ID: 0b5094f2-1a74-4883-a89a-913bb0ab00e0', '检测完成 - 视频: 成片一版.mp4 | 结果: 真实视频 | 置信度: 55.47% | 处理时间: nullms | 分析帧数:null', 'SYSTEM', 'Worker Service', 'ASYNC', '/worker/detection', 200, '2025-11-20 21:42:31.469141', '2025-11-20 21:42:31.469141'), (100, 5, 'USER_LOGIN', 'user', 5, NULL, '用户登录: testuser', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36 Edg/142.0.0.0', 'POST', '/api/auth/login', 200, '2025-11-20 22:12:01.456627', '2025-11-20 22:12:01.456627'), (101, 5, 'VIDEO_DELETE', 'video', 27, '视频: 成片一版.mp4', '已删除', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36 Edg/142.0.0.0', 'DELETE', '/api/videos/27', 200, '2025-11-20 22:14:15.179401', '2025-11-20 22:14:15.179401'), (102, 5, 'VIDEO_DELETE', 'video', 25, '视频: 裁剪版.mp4', '已删除', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36 Edg/142.0.0.0', 'DELETE', '/api/videos/25', 200, '2025-11-20 22:14:16.799619', '2025-11-20 22:14:16.799619'), (103, 5, 'VIDEO_UPLOAD', 'video', 28, NULL, '上传视频: video_only.mp4 (27.99 MB)', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36 Edg/142.0.0.0', 'POST', '/api/videos/upload', 200, '2025-11-20 22:14:29.535151', '2025-11-20 22:14:29.535151'), (104, 5, 'DETECTION_START', 'detection_task', 28, NULL, '开始检测视频: video_only.mp4', 'SYSTEM', 'Worker Service', 'ASYNC', '/worker/detection', 200, '2025-11-20 22:14:29.74785', '2025-11-20 22:14:29.74785'), (105, 5, 'DETECTION_COMPLETE', 'detection_result', 24, '任务ID: 8e40f126-df3c-4ad8-8363-3ca81392717f', '检测完成 - 视频: video_only.mp4 | 结果: 深度伪造 | 置信度: 91.86% | 处理时间: nullms | 分析帧数:null', 'SYSTEM', 'Worker Service', 'ASYNC', '/worker/detection', 200, '2025-11-20 22:14:30.992', '2025-11-20 22:14:30.992'), (106, 5, 'USER_LOGIN', 'user', 5, NULL, '用户登录: testuser', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36 Edg/142.0.0.0', 'POST', '/api/auth/login', 200, '2025-11-20 22:20:29.971083', '2025-11-20 22:20:29.971083'), (107, 5, 'USER_LOGIN', 'user', 5, NULL, '用户登录: testuser', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36 Edg/142.0.0.0', 'POST', '/api/auth/login', 200, '2025-11-20 22:25:36.723327', '2025-11-20 22:25:36.723327'), (108, 5, 'VIDEO_UPLOAD', 'video', 29, NULL, '上传视频: 成片一版.mp4 (32.72 MB)', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36 Edg/142.0.0.0', 'POST', '/api/videos/upload', 200, '2025-11-20 22:25:57.20469', '2025-11-20 22:25:57.20469'), (109, 5, 'DETECTION_START', 'detection_task', 29, NULL, '开始检测视频: 成片一版.mp4', 'SYSTEM', 'Worker Service', 'ASYNC', '/worker/detection', 200, '2025-11-20 22:25:57.569076', '2025-11-20 22:25:57.569076'), (110, 5, 'DETECTION_COMPLETE', 'detection_result', 25, '视频名称: 成片一版.mp4', '检测完成 - 视频: 成片一版.mp4 | 结果: 真实视频 | 置信度: 49.33% | 处理时间: nullms | 分析帧数:null', 'SYSTEM', 'Worker Service', 'ASYNC', '/worker/detection', 200, '2025-11-20 22:25:58.645167', '2025-11-20 22:25:58.645167'), (111, 5, 'VIDEO_UPLOAD', 'video', 30, NULL, '上传视频: 成片二版.mp4 (102.80 MB)', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36 Edg/142.0.0.0', 'POST', '/api/videos/upload', 200, '2025-11-20 22:26:12.892938', '2025-11-20 22:26:12.892938'), (112, 5, 'DETECTION_START', 'detection_task', 30, NULL, '开始检测视频: 成片二版.mp4', 'SYSTEM', 'Worker Service', 'ASYNC', '/worker/detection', 200, '2025-11-20 22:26:12.904649', '2025-11-20 22:26:12.904649'), (113, 5, 'DETECTION_COMPLETE', 'detection_result', 26, '视频名称: 成片二版.mp4', '检测完成 - 视频: 成片二版.mp4 | 结果: 真实视频 | 置信度: 40.25% | 处理时间: nullms | 分析帧数:null', 'SYSTEM', 'Worker Service', 'ASYNC', '/worker/detection', 200, '2025-11-20 22:26:13.923791', '2025-11-20 22:26:13.923791'), (114, 4, 'USER_LOGIN', 'user', 4, NULL, '用户登录: admin', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36 Edg/142.0.0.0', 'POST', '/api/auth/login', 200, '2025-11-20 22:27:34.085958', '2025-11-20 22:27:34.085958'), (115, 4, 'USER_LOGIN', 'user', 4, NULL, '用户登录: admin', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36 Edg/142.0.0.0', 'POST', '/api/auth/login', 200, '2025-11-20 22:32:02.283843', '2025-11-20 22:32:02.283843'), (116, 4, 'VIDEO_DELETE', 'video', 26, '视频: 中华农耕(去除人声).mp4', '已删除', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36 Edg/142.0.0.0', 'DELETE', '/api/videos/26', 200, '2025-11-20 22:32:16.660702', '2025-11-20 22:32:16.660702'), (117, 4, 'VIDEO_UPLOAD', 'video', 31, NULL, '上传视频: 裁剪版.mp4 (106.04 MB)', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36 Edg/142.0.0.0', 'POST', '/api/videos/upload', 200, '2025-11-20 22:32:31.865171', '2025-11-20 22:32:31.865171'), (118, 4, 'DETECTION_START', 'detection_task', 31, NULL, '开始检测视频: 裁剪版.mp4', 'SYSTEM', 'Worker Service', 'ASYNC', '/worker/detection', 200, '2025-11-20 22:32:32.137237', '2025-11-20 22:32:32.137237'), (119, 4, 'DETECTION_COMPLETE', 'detection_result', 27, '视频名称: 裁剪版.mp4', '检测完成 - 视频: 裁剪版.mp4 | 结果: 深度伪造 | 置信度: 70.92% | 处理时间: nullms | 分析帧数:null', 'SYSTEM', 'Worker Service', 'ASYNC', '/worker/detection', 200, '2025-11-20 22:32:33.206913', '2025-11-20 22:32:33.206913'), (120, 4, 'VIDEO_UPLOAD', 'video', 32, NULL, '上传视频: 中华农耕(去除人声).mp4 (32.84 MB)', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36 Edg/142.0.0.0', 'POST', '/api/videos/upload', 200, '2025-11-20 22:32:59.528069', '2025-11-20 22:32:59.528069'), (121, 4, 'DETECTION_START', 'detection_task', 32, NULL, '开始检测视频: 中华农耕(去除人声).mp4', 'SYSTEM', 'Worker Service', 'ASYNC', '/worker/detection', 200, '2025-11-20 22:32:59.541732', '2025-11-20 22:32:59.541732'), (122, 4, 'DETECTION_COMPLETE', 'detection_result', 28, '视频名称: 中华农耕(去除人声).mp4', '检测完成 - 视频: 中华农耕(去除人声).mp4 | 结果: 深度伪造 | 置信度: 82.08% | 处理时间: nullms | 分析帧数:null', 'SYSTEM', 'Worker Service', 'ASYNC', '/worker/detection', 200, '2025-11-20 22:33:00.563289', '2025-11-20 22:33:00.563289'), (153, 5, 'USER_LOGIN', 'user', 5, NULL, '用户登录: testuser', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36 Edg/142.0.0.0', 'POST', '/api/auth/login', 200, '2025-11-23 21:26:38.789238', '2025-11-23 21:26:38.789238'), (154, 5, 'VIDEO_UPLOAD', 'video', 65, NULL, '上传视频: Desktop 2025.10.30 - 16.59.01.01.mp4 (151.75 MB)', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36 Edg/142.0.0.0', 'POST', '/api/videos/upload', 200, '2025-11-23 21:27:07.769099', '2025-11-23 21:27:07.769099'), (155, 5, 'DETECTION_START', 'detection_task', 65, NULL, '开始检测视频: Desktop 2025.10.30 - 16.59.01.01.mp4', 'SYSTEM', 'Worker Service', 'ASYNC', '/worker/detection', 200, '2025-11-23 21:27:08.251166', '2025-11-23 21:27:08.251166'), (156, 5, 'DETECTION_COMPLETE', 'detection_result', 61, '视频名称: Desktop 2025.10.30 - 16.59.01.01.mp4', '检测完成 - 视频: Desktop 2025.10.30 - 16.59.01.01.mp4 | 结果: 深度伪造 | 置信度: 82.51% | 处理时间: nullms | 分析帧数:null', 'SYSTEM', 'Worker Service', 'ASYNC', '/worker/detection', 200, '2025-11-23 21:27:09.339214', '2025-11-23 21:27:09.339214'), (157, 5, 'VIDEO_DELETE', 'video', 65, '视频: Desktop 2025.10.30 - 16.59.01.01.mp4', '已删除', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36 Edg/142.0.0.0', 'DELETE', '/api/videos/65', 200, '2025-11-23 21:28:18.40555', '2025-11-23 21:28:18.40555'), (158, 5, 'VIDEO_DELETE', 'video', 30, '视频: 成片二版.mp4', '已删除', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36 Edg/142.0.0.0', 'DELETE', '/api/videos/30', 200, '2025-11-23 21:28:19.611853', '2025-11-23 21:28:19.611853'), (159, 5, 'VIDEO_DELETE', 'video', 29, '视频: 成片一版.mp4', '已删除', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36 Edg/142.0.0.0', 'DELETE', '/api/videos/29', 200, '2025-11-23 21:28:20.906028', '2025-11-23 21:28:20.906028'), (160, 5, 'VIDEO_DELETE', 'video', 28, '视频: video_only.mp4', '已删除', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36 Edg/142.0.0.0', 'DELETE', '/api/videos/28', 200, '2025-11-23 21:28:22.034618', '2025-11-23 21:28:22.034618'), (161, 5, 'USER_LOGIN', 'user', 5, NULL, '用户登录: testuser', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36 Edg/142.0.0.0', 'POST', '/api/auth/login', 200, '2025-11-23 22:02:42.5985', '2025-11-23 22:02:42.5985'), (162, 5, 'USER_LOGIN', 'user', 5, NULL, '用户登录: testuser', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36 Edg/143.0.0.0', 'POST', '/api/auth/login', 200, '2025-12-22 18:11:53.552726', '2025-12-22 18:11:53.552726'), (163, 5, 'VIDEO_UPLOAD', 'video', 66, NULL, '上传视频: Desktop 2025.10.30 - 16.59.01.01.mp4 (151.75 MB)', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36 Edg/143.0.0.0', 'POST', '/api/videos/upload', 200, '2025-12-22 18:18:48.599595', '2025-12-22 18:18:48.599595'), (164, 5, 'DETECTION_START', 'detection_task', 66, NULL, '开始检测视频: Desktop 2025.10.30 - 16.59.01.01.mp4', 'SYSTEM', 'Worker Service', 'ASYNC', '/worker/detection', 200, '2025-12-22 18:18:49.268344', '2025-12-22 18:18:49.268344'), (165, 5, 'DETECTION_COMPLETE', 'detection_result', 62, '视频名称: Desktop 2025.10.30 - 16.59.01.01.mp4', '检测完成 - 视频: Desktop 2025.10.30 - 16.59.01.01.mp4 | 结果: 真实视频 | 置信度: 32.54% | 处理时间: nullms | 分析帧数:null', 'SYSTEM', 'Worker Service', 'ASYNC', '/worker/detection', 200, '2025-12-22 18:18:50.357892', '2025-12-22 18:18:50.357892'), (166, 5, 'USER_LOGIN', 'user', 5, NULL, '用户登录: testuser', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36 Edg/143.0.0.0', 'POST', '/api/auth/login', 200, '2025-12-22 18:25:03.031419', '2025-12-22 18:25:03.031419'), (167, 5, 'VIDEO_DELETE', 'video', 66, '视频: Desktop 2025.10.30 - 16.59.01.01.mp4', '已删除', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36 Edg/143.0.0.0', 'DELETE', '/api/videos/66', 200, '2025-12-22 18:26:15.182254', '2025-12-22 18:26:15.182254'), (181, 8, 'LOGOUT', 'USER', NULL, NULL, 'Result: {"headers":{},"body":"登出成功","statusCode":"OK","statusCodeValue":200}', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36 Edg/143.0.0.0', 'POST', '/api/auth/logout', 200, '2025-12-24 22:01:07.941333', '2025-12-24 22:01:07.941333'), (182, NULL, 'LOGIN', 'USER', NULL, NULL, 'Result: {"headers":{},"body":{"token":"eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJyb2UiLCJ1c2VySWQiOjgsInVzZXJuYW1lIjoicm9lIiwicm9sZXMiOlsiUk9MRV9BRE1JTiJdLCJpYXQiOjE3NjY1ODQ4NjksImV4cCI6MTc2NjY3MTI2OX0.8sVx6arkdEVv9jo1cafFRJmgd5RHPe-SYVkSiXpTzzXxeXd9COs2AtxlzKd43wMjPINktJib0n3vTGEiQAVdlQ","tokenType":"Bearer","expiresIn":86400000,"userId":8,"username":"roe","email":"aa@gmail.com","roles":["ROLE_ADMIN"]},"statusCode":"OK","statusCodeValue":200}', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36 Edg/143.0.0.0', 'POST', '/api/auth/login', 200, '2025-12-24 22:01:09.557127', '2025-12-24 22:01:09.557127'), (183, 8, 'USER_LOGOUT', 'USER', NULL, NULL, 'User logged out', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36 Edg/143.0.0.0', 'POST', '/api/auth/logout', 200, '2025-12-24 22:06:36.9125', '2025-12-24 22:06:36.9125'), (184, NULL, 'USER_LOGIN', 'USER', NULL, NULL, 'User roe logged in', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36 Edg/143.0.0.0', 'POST', '/api/auth/login', 200, '2025-12-24 22:06:38.237397', '2025-12-24 22:06:38.237397'), (185, 8, 'DELETE_VIDEO', 'VIDEO', NULL, NULL, 'Deleted video with ID: 109', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36 Edg/143.0.0.0', 'DELETE', '/api/videos/109', 200, '2025-12-24 22:06:57.988588', '2025-12-24 22:06:57.988588'), (186, 8, 'USER_LOGOUT', 'USER', NULL, NULL, 'User logged out', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36 Edg/143.0.0.0', 'POST', '/api/auth/logout', 200, '2025-12-24 22:15:28.671503', '2025-12-24 22:15:28.671503'), (187, NULL, 'USER_LOGIN', 'USER', NULL, NULL, 'User roe logged in', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36 Edg/143.0.0.0', 'POST', '/api/auth/login', 200, '2025-12-24 22:15:29.692858', '2025-12-24 22:15:29.692858'), (188, 8, 'USER_LOGOUT', 'USER', NULL, NULL, 'User logged out', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36 Edg/143.0.0.0', 'POST', '/api/auth/logout', 200, '2025-12-24 22:16:15.349295', '2025-12-24 22:16:15.349295'), (189, NULL, 'USER_REGISTER', 'USER', NULL, NULL, 'User ROE_USER registered', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36 Edg/143.0.0.0', 'POST', '/api/auth/register', 200, '2025-12-24 22:16:51.265601', '2025-12-24 22:16:51.265601'), (190, NULL, 'USER_LOGIN', 'USER', NULL, NULL, 'User ROE_USER logged in', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36 Edg/143.0.0.0', 'POST', '/api/auth/login', 200, '2025-12-24 22:16:57.638642', '2025-12-24 22:16:57.638642'), (191, 9, 'USER_LOGOUT', 'USER', NULL, NULL, 'User logged out', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36 Edg/143.0.0.0', 'POST', '/api/auth/logout', 200, '2025-12-24 22:17:08.012516', '2025-12-24 22:17:08.012516'), (192, NULL, 'USER_LOGIN', 'USER', NULL, NULL, 'User ROE_USER logged in', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36 Edg/143.0.0.0', 'POST', '/api/auth/login', 200, '2025-12-24 22:17:09.07357', '2025-12-24 22:17:09.07357'), (193, 9, 'USER_LOGOUT', 'USER', NULL, NULL, 'User logged out', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36 Edg/143.0.0.0', 'POST', '/api/auth/logout', 200, '2025-12-24 22:17:13.344977', '2025-12-24 22:17:13.344977'), (194, NULL, 'USER_LOGIN', 'USER', NULL, NULL, 'User roe logged in', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36 Edg/143.0.0.0', 'POST', '/api/auth/login', 200, '2025-12-24 22:17:16.499856', '2025-12-24 22:17:16.499856'), (195, 8, 'UPLOAD', 'VIDEO', NULL, NULL, 'Result: {"headers":{},"body":{"taskId":"f6c58cb3-5125-4531-86fc-ef546519c65f","uploadStatus":"SUCCESS","uploadProgress":100,"estimatedTime":null,"createdAt":"2025-12-24T22:17:59.1831505"},"statusCode":"CREATED","statusCodeValue":201}', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36 Edg/143.0.0.0', 'POST', '/api/videos/upload', 200, '2025-12-24 22:17:59.228545', '2025-12-24 22:17:59.228545'), (196, 8, 'DELETE_VIDEO', 'VIDEO', NULL, NULL, 'Deleted video with ID: 110', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36 Edg/143.0.0.0', 'DELETE', '/api/videos/110', 200, '2025-12-24 22:18:25.562071', '2025-12-24 22:18:25.562071'), (197, 8, 'USER_LOGOUT', 'USER', NULL, NULL, 'User logged out', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36 Edg/143.0.0.0', 'POST', '/api/auth/logout', 200, '2025-12-24 22:26:02.50462', '2025-12-24 22:26:02.50462'), (198, 8, 'USER_LOGIN', 'USER', NULL, NULL, 'User roe logged in', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36 Edg/143.0.0.0', 'POST', '/api/auth/login', 200, '2025-12-24 22:26:03.980656', '2025-12-24 22:26:03.980656'), (199, 8, 'UPLOAD', 'VIDEO', NULL, NULL, 'Result: {"headers":{},"body":{"taskId":"58cd237e-6a38-461a-81c3-4d8e0ac61b89","uploadStatus":"SUCCESS","uploadProgress":100,"estimatedTime":null,"createdAt":"2025-12-24T22:26:44.946521"},"statusCodeValue":201,"statusCode":"CREATED"}', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36 Edg/143.0.0.0', 'POST', '/api/videos/upload', 200, '2025-12-24 22:26:45.008973', '2025-12-24 22:26:45.008973'), (200, 8, 'UPLOAD_VIDEO', 'VIDEO', NULL, NULL, 'Uploaded video: default_avatar.png (Mode: aigc)', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36 Edg/143.0.0.0', 'POST', '/api/videos/upload', 200, '2025-12-24 22:33:45.016153', '2025-12-24 22:33:45.016153'), (201, 8, 'DELETE_VIDEO', 'VIDEO', NULL, NULL, 'Deleted video with ID: 112', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36 Edg/143.0.0.0', 'DELETE', '/api/videos/112', 200, '2025-12-24 22:34:00.994242', '2025-12-24 22:34:00.994242'), (202, 8, 'USER_LOGOUT', 'USER', NULL, NULL, 'User logged out', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36 Edg/143.0.0.0', 'POST', '/api/auth/logout', 200, '2025-12-24 22:37:27.077241', '2025-12-24 22:37:27.077241'), (203, 9, 'USER_LOGIN', 'USER', NULL, NULL, 'User ROE_USER logged in', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36 Edg/143.0.0.0', 'POST', '/api/auth/login', 200, '2025-12-24 22:37:30.035345', '2025-12-24 22:37:30.035345'), (204, 9, 'UPLOAD_VIDEO', 'VIDEO', NULL, NULL, 'Uploaded video: 17AB90FA620E232E08D0B6B8494FD073.png (Mode: standard)', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36 Edg/143.0.0.0', 'POST', '/api/videos/upload', 200, '2025-12-24 22:37:45.402735', '2025-12-24 22:37:45.402735'), (205, 9, 'USER_LOGOUT', 'USER', NULL, NULL, 'User logged out', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36 Edg/143.0.0.0', 'POST', '/api/auth/logout', 200, '2025-12-24 22:38:32.140593', '2025-12-24 22:38:32.140593'), (206, 8, 'USER_LOGIN', 'USER', NULL, NULL, 'User roe logged in', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36 Edg/143.0.0.0', 'POST', '/api/auth/login', 200, '2025-12-24 22:38:34.395922', '2025-12-24 22:38:34.395922'), (207, 8, 'USER_LOGOUT', 'USER', NULL, NULL, 'User logged out', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36 Edg/143.0.0.0', 'POST', '/api/auth/logout', 200, '2025-12-24 22:54:12.492777', '2025-12-24 22:54:12.492777'), (208, 8, 'USER_LOGIN', 'USER', NULL, NULL, 'User roe logged in', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36 Edg/143.0.0.0', 'POST', '/api/auth/login', 200, '2025-12-24 22:54:40.150563', '2025-12-24 22:54:40.150563'), (209, 8, 'USER_LOGIN', 'USER', NULL, NULL, 'User roe logged in', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36 Edg/143.0.0.0', 'POST', '/api/auth/login', 200, '2025-12-24 22:54:41.356851', '2025-12-24 22:54:41.356851'), (210, 8, 'USER_LOGIN', 'USER', NULL, NULL, 'User roe logged in', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36 Edg/143.0.0.0', 'POST', '/api/auth/login', 200, '2025-12-24 22:54:42.127573', '2025-12-24 22:54:42.127573'), (211, 8, 'USER_LOGIN', 'USER', NULL, NULL, 'User roe logged in', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36 Edg/143.0.0.0', 'POST', '/api/auth/login', 200, '2025-12-24 22:54:44.611503', '2025-12-24 22:54:44.611503'), (212, 8, 'DELETE_VIDEO', 'VIDEO', NULL, NULL, 'Deleted video with ID: 111', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36 Edg/143.0.0.0', 'DELETE', '/api/videos/111', 200, '2025-12-24 22:59:05.606928', '2025-12-24 22:59:05.606928'), (213, 8, 'UPLOAD_VIDEO', 'VIDEO', NULL, NULL, 'Uploaded video: Desktop 2025.10.30 - 16.59.01.01.mp4 (Mode: standard)', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36 Edg/143.0.0.0', 'POST', '/api/videos/upload', 200, '2025-12-24 22:59:50.235745', '2025-12-24 22:59:50.235745'), (214, 8, 'USER_LOGOUT', 'USER', NULL, NULL, 'User logged out', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36 Edg/143.0.0.0', 'POST', '/api/auth/logout', 200, '2025-12-25 15:34:08.85202', '2025-12-25 15:34:08.85202'), (215, 8, 'USER_LOGIN', 'USER', NULL, NULL, 'User roe logged in', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36 Edg/143.0.0.0', 'POST', '/api/auth/login', 200, '2025-12-25 15:34:10.035004', '2025-12-25 15:34:10.035004'), (216, 8, 'UPLOAD_VIDEO', 'VIDEO', NULL, NULL, 'Uploaded video: 17AB90FA620E232E08D0B6B8494FD073.png (Mode: standard)', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36 Edg/143.0.0.0', 'POST', '/api/videos/upload', 200, '2025-12-25 15:35:36.784686', '2025-12-25 15:35:36.784686'), (217, 8, 'UPLOAD_VIDEO', 'VIDEO', NULL, NULL, 'Uploaded video: 17AB90FA620E232E08D0B6B8494FD073.png (Mode: standard)', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36 Edg/143.0.0.0', 'POST', '/api/videos/upload', 200, '2025-12-25 15:35:54.82826', '2025-12-25 15:35:54.82826'), (218, 8, 'UPLOAD_VIDEO', 'VIDEO', NULL, NULL, 'Uploaded video: default_avatar.jpg (Mode: aigc)', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36 Edg/143.0.0.0', 'POST', '/api/videos/upload', 200, '2025-12-25 15:36:54.863368', '2025-12-25 15:36:54.863368'), (219, 8, 'UPLOAD_VIDEO', 'VIDEO', NULL, NULL, 'Uploaded video: 17AB90FA620E232E08D0B6B8494FD073.png (Mode: standard)', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36 Edg/143.0.0.0', 'POST', '/api/videos/upload', 200, '2025-12-25 15:37:53.761315', '2025-12-25 15:37:53.761315'), (220, 8, 'UPLOAD_VIDEO', 'VIDEO', NULL, NULL, 'Uploaded video: 考试系统.drawio.png (Mode: standard)', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36 Edg/143.0.0.0', 'POST', '/api/videos/upload', 200, '2025-12-25 15:38:14.444444', '2025-12-25 15:38:14.444444'), (221, 8, 'UPLOAD_VIDEO', 'VIDEO', NULL, NULL, 'Uploaded video: QQ20251225-154959.mp4 (Mode: standard)', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36 Edg/143.0.0.0', 'POST', '/api/videos/upload', 200, '2025-12-25 15:51:04.6188', '2025-12-25 15:51:04.6188'), (222, 8, 'UPLOAD_VIDEO', 'VIDEO', NULL, NULL, 'Uploaded video: QQ20251225-154959.mp4 (Mode: standard)', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36 Edg/143.0.0.0', 'POST', '/api/videos/upload', 200, '2025-12-25 15:52:11.518253', '2025-12-25 15:52:11.518253'), (223, 8, 'DELETE_VIDEO', 'VIDEO', NULL, NULL, 'Deleted video with ID: 107', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36 Edg/143.0.0.0', 'DELETE', '/api/videos/107', 200, '2025-12-25 15:52:57.881981', '2025-12-25 15:52:57.881981'), (224, 8, 'DELETE_VIDEO', 'VIDEO', NULL, NULL, 'Deleted video with ID: 108', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36 Edg/143.0.0.0', 'DELETE', '/api/videos/108', 200, '2025-12-25 15:52:59.364498', '2025-12-25 15:52:59.364498'), (225, 8, 'DELETE_VIDEO', 'VIDEO', NULL, NULL, 'Deleted video with ID: 117', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36 Edg/143.0.0.0', 'DELETE', '/api/videos/117', 200, '2025-12-25 15:57:03.615482', '2025-12-25 15:57:03.615482'), (226, 8, 'UPLOAD_VIDEO', 'VIDEO', NULL, NULL, 'Uploaded video: QQ20251225-154959.mp4 (Mode: standard)', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36 Edg/143.0.0.0', 'POST', '/api/videos/upload', 200, '2025-12-25 15:57:10.045033', '2025-12-25 15:57:10.045033'), (227, 8, 'UPLOAD_VIDEO', 'VIDEO', NULL, NULL, 'Uploaded video: 4047BC8FE0DDCA90922E55FEF53CF8F9.png (Mode: aigc)', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36 Edg/143.0.0.0', 'POST', '/api/videos/upload', 200, '2025-12-25 15:57:54.575696', '2025-12-25 15:57:54.575696'), (228, 8, 'DELETE_VIDEO', 'VIDEO', NULL, NULL, 'Deleted video with ID: 118', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36 Edg/143.0.0.0', 'DELETE', '/api/videos/118', 200, '2025-12-25 16:02:35.612585', '2025-12-25 16:02:35.612585'), (229, 8, 'UPLOAD_VIDEO', 'VIDEO', NULL, NULL, 'Uploaded video: QQ20251225-154959.mp4 (Mode: standard)', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36 Edg/143.0.0.0', 'POST', '/api/videos/upload', 200, '2025-12-25 16:02:43.035605', '2025-12-25 16:02:43.035605'), (230, 8, 'UPLOAD_VIDEO', 'VIDEO', NULL, NULL, 'Uploaded video: QQ20251225-1678.mp4 (Mode: standard)', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36 Edg/143.0.0.0', 'POST', '/api/videos/upload', 200, '2025-12-25 16:08:08.484668', '2025-12-25 16:08:08.484668'), (231, 8, 'DELETE_VIDEO', 'VIDEO', NULL, NULL, 'Deleted video with ID: 121', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36 Edg/143.0.0.0', 'DELETE', '/api/videos/121', 200, '2025-12-25 16:09:03.636448', '2025-12-25 16:09:03.636448'), (232, 8, 'DELETE_VIDEO', 'VIDEO', NULL, NULL, 'Deleted video with ID: 119', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36 Edg/143.0.0.0', 'DELETE', '/api/videos/119', 200, '2025-12-25 16:09:05.935268', '2025-12-25 16:09:05.935268'), (233, 8, 'USER_LOGOUT', 'USER', NULL, NULL, 'User logged out', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36 Edg/143.0.0.0', 'POST', '/api/auth/logout', 200, '2025-12-25 16:10:38.609473', '2025-12-25 16:10:38.609473'), (234, 8, 'USER_LOGIN', 'USER', NULL, NULL, 'User roe logged in', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36 Edg/143.0.0.0', 'POST', '/api/auth/login', 200, '2025-12-25 16:12:26.705979', '2025-12-25 16:12:26.705979'), (235, 8, 'UPLOAD_VIDEO', 'VIDEO', NULL, NULL, 'Uploaded video: 171B6EC7B27917EB2E7B9FFE1D6AE506.jpg (Mode: standard)', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36 Edg/143.0.0.0', 'POST', '/api/videos/upload', 200, '2025-12-25 16:13:02.047635', '2025-12-25 16:13:02.047635'), (236, 8, 'UPLOAD_VIDEO', 'VIDEO', NULL, NULL, 'Uploaded video: 4047BC8FE0DDCA90922E55FEF53CF8F9.png (Mode: aigc)', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36 Edg/143.0.0.0', 'POST', '/api/videos/upload', 200, '2025-12-25 16:13:37.660389', '2025-12-25 16:13:37.660389'), (237, 8, 'UPLOAD_VIDEO', 'VIDEO', NULL, NULL, 'Uploaded video: QQ20251225-1678.mp4 (Mode: standard)', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36 Edg/143.0.0.0', 'POST', '/api/videos/upload', 200, '2025-12-25 16:14:40.384355', '2025-12-25 16:14:40.384355'), (238, 8, 'DELETE_VIDEO', 'VIDEO', NULL, NULL, 'Deleted video with ID: 124', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36 Edg/143.0.0.0', 'DELETE', '/api/videos/124', 200, '2025-12-25 16:21:10.648338', '2025-12-25 16:21:10.648338'), (239, 8, 'DELETE_VIDEO', 'VIDEO', NULL, NULL, 'Deleted video with ID: 123', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36 Edg/143.0.0.0', 'DELETE', '/api/videos/123', 200, '2025-12-25 16:21:11.836783', '2025-12-25 16:21:11.836783'), (240, 8, 'DELETE_VIDEO', 'VIDEO', NULL, NULL, 'Deleted video with ID: 122', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36 Edg/143.0.0.0', 'DELETE', '/api/videos/122', 200, '2025-12-25 16:21:13.057664', '2025-12-25 16:21:13.057664'), (241, 8, 'USER_LOGOUT', 'USER', NULL, NULL, 'User logged out', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36 Edg/143.0.0.0', 'POST', '/api/auth/logout', 200, '2025-12-25 16:21:37.335424', '2025-12-25 16:21:37.335424'), (242, 8, 'USER_LOGIN', 'USER', NULL, NULL, 'User roe logged in', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36 Edg/143.0.0.0', 'POST', '/api/auth/login', 200, '2025-12-25 16:22:10.216144', '2025-12-25 16:22:10.216144'), (243, 8, 'UPLOAD_VIDEO', 'VIDEO', NULL, NULL, 'Uploaded video: 171B6EC7B27917EB2E7B9FFE1D6AE506.jpg (Mode: standard)', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36 Edg/143.0.0.0', 'POST', '/api/videos/upload', 200, '2025-12-25 16:22:41.826687', '2025-12-25 16:22:41.826687'), (244, 8, 'UPLOAD_VIDEO', 'VIDEO', NULL, NULL, 'Uploaded video: 4047BC8FE0DDCA90922E55FEF53CF8F9.png (Mode: aigc)', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36 Edg/143.0.0.0', 'POST', '/api/videos/upload', 200, '2025-12-25 16:23:16.458636', '2025-12-25 16:23:16.458636'), (245, 8, 'UPLOAD_VIDEO', 'VIDEO', NULL, NULL, 'Uploaded video: QQ20251225-1678.mp4 (Mode: standard)', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36 Edg/143.0.0.0', 'POST', '/api/videos/upload', 200, '2025-12-25 16:24:23.21114', '2025-12-25 16:24:23.21114');
COMMIT;

-- ----------------------------
-- Table structure for chunk_metadata
-- ----------------------------
CREATE TABLE "chunk_metadata" (
  "id" int8 NOT NULL DEFAULT nextval('chunk_metadata_id_seq'::regclass),
  "file_id" varchar(36) COLLATE "pg_catalog"."default" NOT NULL,
  "chunk_index" int4 NOT NULL,
  "chunk_hash" varchar(64) COLLATE "pg_catalog"."default",
  "chunk_path" varchar(500) COLLATE "pg_catalog"."default",
  "chunk_size" int8,
  "uploaded_at" timestamp(6),
  "verified" bool NOT NULL DEFAULT false,
  "created_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updated_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP
)
;
COMMENT ON COLUMN "chunk_metadata"."file_id" IS '文件标识（UUID）';
COMMENT ON COLUMN "chunk_metadata"."chunk_index" IS '分块索引（从0开始）';
COMMENT ON COLUMN "chunk_metadata"."verified" IS '分块是否已验证';
COMMENT ON TABLE "chunk_metadata" IS '分块上传元数据表';

-- ----------------------------
-- Records of chunk_metadata
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for detection_results
-- ----------------------------
CREATE TABLE "detection_results" (
  "id" int8 NOT NULL DEFAULT nextval('detection_results_id_seq'::regclass),
  "task_id" varchar(36) COLLATE "pg_catalog"."default" NOT NULL,
  "video_id" int8 NOT NULL,
  "user_id" int8 NOT NULL,
  "prediction" varchar(20) COLLATE "pg_catalog"."default" NOT NULL,
  "confidence" numeric(5,4),
  "model_version" varchar(50) COLLATE "pg_catalog"."default",
  "processing_time_ms" int8,
  "frames_analyzed" int4,
  "features" jsonb,
  "artifacts_detected" varchar(1000) COLLATE "pg_catalog"."default",
  "created_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updated_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP
)
;
COMMENT ON COLUMN "detection_results"."prediction" IS '预测结果：AUTHENTIC/FAKE/UNCERTAIN';
COMMENT ON COLUMN "detection_results"."confidence" IS '置信度（0.0000-1.0000）';
COMMENT ON COLUMN "detection_results"."features" IS 'JSON格式的特征向量';
COMMENT ON COLUMN "detection_results"."artifacts_detected" IS '检测到的伪造迹象（逗号分隔）';
COMMENT ON TABLE "detection_results" IS '检测结果表';

-- ----------------------------
-- Records of detection_results
-- ----------------------------
BEGIN;
INSERT INTO "detection_results" ("id", "task_id", "video_id", "user_id", "prediction", "confidence", "model_version", "processing_time_ms", "frames_analyzed", "features", "artifacts_detected", "created_at", "updated_at") VALUES (106, '4302d632-cc1a-42a3-b6cc-df77d6d38d65', 113, 9, 'AUTHENTIC', 0.7131, 'effort', 1406, 1, '{"optical_flow_score": 0.9, "temporal_consistency": 1.0, "color_histogram_score": 0.85, "compression_artifacts": 0.0, "facial_landmarks_score": 0.32974979400634763}', '', '2025-12-24 22:37:46.823301', '2025-12-24 22:37:46.823301'), (107, '0ac55152-0de8-405b-8b08-6224e2d4f39e', 114, 8, 'FAKE', 0.5723, 'effort', 67895, 300, '{"optical_flow_score": 0.9, "temporal_consistency": 0.9736483097076416, "color_histogram_score": 0.85, "compression_artifacts": 0.263516902923584, "facial_landmarks_score": 0.32853729248046876}', '', '2025-12-24 23:00:59.075279', '2025-12-24 23:00:59.075279'), (108, 'ca9c1d55-71fd-4833-bc1a-528275653355', 115, 8, 'FAKE', 0.9991, 'drct', 17478, 1, '{"optical_flow_score": 0.9, "temporal_consistency": 1.0, "color_histogram_score": 0.85, "compression_artifacts": 0.0, "facial_landmarks_score": 0.0}', '', '2025-12-25 15:37:13.79554', '2025-12-25 15:37:13.79554'), (109, '17ddfa72-c3de-4127-9ea7-041e9b0d08f0', 116, 8, 'UNCERTAIN', 0.0000, 'effort', 36649, 0, '{"optical_flow_score": 0, "temporal_consistency": 0, "color_histogram_score": 0, "compression_artifacts": 0, "facial_landmarks_score": 0}', 'No valid frames/faces detected', '2025-12-25 15:38:51.131252', '2025-12-25 15:38:51.131252'), (113, 'd564aa65-57aa-47bc-8378-4efb24d49ebb', 120, 8, 'AUTHENTIC', 0.7451, 'effort', 44713, 42, '{"optical_flow_score": 0.9, "temporal_consistency": 0.8525749922615902, "color_histogram_score": 0.85, "compression_artifacts": 0.7137621094783148, "facial_landmarks_score": 0.32994029998779295}', 'compression_artifacts', '2025-12-25 16:03:28.242108', '2025-12-25 16:03:28.242108'), (118, 'a37aa1be-c17f-4b71-b816-62f55c1f66da', 125, 8, 'AUTHENTIC', 0.8652, 'effort', 2235, 1, '{"optical_flow_score": 0.9, "temporal_consistency": 1.0, "color_histogram_score": 0.85, "compression_artifacts": 0.0, "facial_landmarks_score": 0.3325091552734375}', '', '2025-12-25 16:22:44.933836', '2025-12-25 16:22:44.933836'), (119, '7c1e25ff-0154-4670-affd-d517d343d772', 126, 8, 'FAKE', 0.9812, 'drct', 896, 1, '{"optical_flow_score": 0.9, "temporal_consistency": 1.0, "color_histogram_score": 0.85, "compression_artifacts": 0.0, "facial_landmarks_score": 0.0}', '', '2025-12-25 16:23:17.36534', '2025-12-25 16:23:17.36534'), (120, '52f708d2-2383-49a1-aa06-cfa6eedb3b3c', 127, 8, 'FAKE', 0.6947, 'effort', 46437, 198, '{"optical_flow_score": 0.9, "temporal_consistency": 0.8530782845254846, "color_histogram_score": 0.85, "compression_artifacts": 0.5903192893745973, "facial_landmarks_score": 0.33835643768310547}', '', '2025-12-25 16:25:09.687772', '2025-12-25 16:25:09.687772');
COMMIT;

-- ----------------------------
-- Table structure for detection_tasks
-- ----------------------------
CREATE TABLE "detection_tasks" (
  "id" int8 NOT NULL DEFAULT nextval('detection_tasks_id_seq'::regclass),
  "task_id" varchar(36) COLLATE "pg_catalog"."default" NOT NULL,
  "video_id" int8 NOT NULL,
  "user_id" int8 NOT NULL,
  "status" varchar(20) COLLATE "pg_catalog"."default" NOT NULL DEFAULT 'PENDING'::character varying,
  "progress" int4 NOT NULL DEFAULT 0,
  "estimated_time_seconds" int4,
  "started_at" timestamp(6),
  "completed_at" timestamp(6),
  "error_message" varchar(1000) COLLATE "pg_catalog"."default",
  "retry_count" int4 NOT NULL DEFAULT 0,
  "created_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updated_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "mode" varchar(20) COLLATE "pg_catalog"."default"
)
;
COMMENT ON COLUMN "detection_tasks"."task_id" IS '任务唯一标识（UUID）';
COMMENT ON COLUMN "detection_tasks"."status" IS '任务状态：PENDING/PROCESSING/COMPLETED/FAILED';
COMMENT ON COLUMN "detection_tasks"."progress" IS '任务进度（0-100）';
COMMENT ON COLUMN "detection_tasks"."retry_count" IS '重试次数';
COMMENT ON TABLE "detection_tasks" IS '检测任务表';

-- ----------------------------
-- Records of detection_tasks
-- ----------------------------
BEGIN;
INSERT INTO "detection_tasks" ("id", "task_id", "video_id", "user_id", "status", "progress", "estimated_time_seconds", "started_at", "completed_at", "error_message", "retry_count", "created_at", "updated_at", "mode") VALUES (110, '4302d632-cc1a-42a3-b6cc-df77d6d38d65', 113, 9, 'COMPLETED', 100, NULL, '2025-12-24 22:37:45.40711', '2025-12-24 22:37:46.826703', NULL, 0, '2025-12-24 22:37:45.404912', '2025-12-24 22:37:45.403948', 'standard'), (111, '0ac55152-0de8-405b-8b08-6224e2d4f39e', 114, 8, 'COMPLETED', 100, NULL, '2025-12-24 22:59:50.688974', '2025-12-24 23:00:59.101792', NULL, 0, '2025-12-24 22:59:50.59052', '2025-12-24 22:59:50.500534', 'standard'), (112, 'ca9c1d55-71fd-4833-bc1a-528275653355', 115, 8, 'COMPLETED', 100, NULL, '2025-12-25 15:36:55.9257', '2025-12-25 15:37:13.883318', NULL, 0, '2025-12-25 15:36:55.788363', '2025-12-25 15:36:55.646897', 'standard'), (113, '17ddfa72-c3de-4127-9ea7-041e9b0d08f0', 116, 8, 'COMPLETED', 100, NULL, '2025-12-25 15:38:14.463439', '2025-12-25 15:38:51.136723', NULL, 0, '2025-12-25 15:38:14.460546', '2025-12-25 15:38:14.457735', 'standard'), (117, 'd564aa65-57aa-47bc-8378-4efb24d49ebb', 120, 8, 'COMPLETED', 100, NULL, '2025-12-25 16:02:43.396957', '2025-12-25 16:03:28.27196', NULL, 0, '2025-12-25 16:02:43.315264', '2025-12-25 16:02:43.261389', 'standard'), (122, 'a37aa1be-c17f-4b71-b816-62f55c1f66da', 125, 8, 'COMPLETED', 100, NULL, '2025-12-25 16:22:42.288305', '2025-12-25 16:22:44.963555', NULL, 0, '2025-12-25 16:22:42.221358', '2025-12-25 16:22:42.138754', 'standard'), (123, '7c1e25ff-0154-4670-affd-d517d343d772', 126, 8, 'COMPLETED', 100, NULL, '2025-12-25 16:23:16.457565', '2025-12-25 16:23:17.36781', NULL, 0, '2025-12-25 16:23:16.455452', '2025-12-25 16:23:16.454186', 'standard'), (124, '52f708d2-2383-49a1-aa06-cfa6eedb3b3c', 127, 8, 'COMPLETED', 100, NULL, '2025-12-25 16:24:23.233459', '2025-12-25 16:25:09.703878', NULL, 0, '2025-12-25 16:24:23.225544', '2025-12-25 16:24:23.215681', 'standard');
COMMIT;

-- ----------------------------
-- Table structure for reports
-- ----------------------------
CREATE TABLE "reports" (
  "id" int8 NOT NULL DEFAULT nextval('reports_id_seq'::regclass),
  "detection_id" int8 NOT NULL,
  "reporter_id" int8 NOT NULL,
  "reason" varchar(50) COLLATE "pg_catalog"."default",
  "evidence" varchar(2000) COLLATE "pg_catalog"."default",
  "status" varchar(20) COLLATE "pg_catalog"."default" NOT NULL DEFAULT 'SUBMITTED'::character varying,
  "reviewed_by" int8,
  "reviewed_at" timestamp(6),
  "review_notes" varchar(1000) COLLATE "pg_catalog"."default",
  "created_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updated_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP
)
;
COMMENT ON COLUMN "reports"."status" IS '举报状态：SUBMITTED/UNDER_REVIEW/RESOLVED/REJECTED';
COMMENT ON TABLE "reports" IS '用户举报表';

-- ----------------------------
-- Records of reports
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for roles
-- ----------------------------
CREATE TABLE "roles" (
  "id" int8 NOT NULL DEFAULT nextval('roles_id_seq'::regclass),
  "name" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "description" varchar(200) COLLATE "pg_catalog"."default",
  "created_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updated_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP
)
;
COMMENT ON COLUMN "roles"."name" IS '角色名称（枚举）';
COMMENT ON COLUMN "roles"."description" IS '角色描述';
COMMENT ON TABLE "roles" IS '用户角色表';

-- ----------------------------
-- Records of roles
-- ----------------------------
BEGIN;
INSERT INTO "roles" ("id", "name", "description", "created_at", "updated_at") VALUES (1, 'ROLE_USER', '普通用户，可以上传视频和查看自己的检测结果', '2025-11-17 22:33:50.661643', '2025-11-17 22:33:50.661643'), (2, 'ROLE_ADMIN', '管理员，拥有所有权限', '2025-11-17 22:33:50.661643', '2025-11-17 22:33:50.661643'), (3, 'ROLE_MODERATOR', '版主，可以审核举报和管理内容', '2025-11-17 22:33:50.661643', '2025-11-17 22:33:50.661643');
COMMIT;

-- ----------------------------
-- Table structure for user_roles
-- ----------------------------
CREATE TABLE "user_roles" (
  "user_id" int8 NOT NULL,
  "role_id" int8 NOT NULL
)
;
COMMENT ON TABLE "user_roles" IS '用户角色关联表';

-- ----------------------------
-- Records of user_roles
-- ----------------------------
BEGIN;
INSERT INTO "user_roles" ("user_id", "role_id") VALUES (4, 2), (5, 1), (6, 1), (7, 1), (8, 2), (9, 1);
COMMIT;

-- ----------------------------
-- Table structure for users
-- ----------------------------
CREATE TABLE "users" (
  "id" int8 NOT NULL DEFAULT nextval('users_id_seq'::regclass),
  "username" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "email" varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
  "password" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "is_active" bool NOT NULL DEFAULT true,
  "last_login_at" timestamp(6),
  "created_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updated_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP
)
;
COMMENT ON COLUMN "users"."username" IS '用户名（3-50字符）';
COMMENT ON COLUMN "users"."email" IS '邮箱地址';
COMMENT ON COLUMN "users"."password" IS 'BCrypt加密密码';
COMMENT ON COLUMN "users"."is_active" IS '账号是否激活';
COMMENT ON COLUMN "users"."last_login_at" IS '最后登录时间';
COMMENT ON TABLE "users" IS '用户表';

-- ----------------------------
-- Records of users
-- ----------------------------
BEGIN;
INSERT INTO "users" ("id", "username", "email", "password", "is_active", "last_login_at", "created_at", "updated_at") VALUES (9, 'ROE_USER', '123456789@qq.com', '$2a$10$rjSF4C4r3yasvMFsSvJ0v.TorDAiMqMav91hoS7vuql.wuhdmI3Im', 'f', '2025-12-24 22:37:29.92942', '2025-12-24 22:16:51.178697', '2025-12-24 22:37:29.996423'), (5, 'testuser', 'test@example.com', '$2a$10$IhlVqP66r9/Iy/XKCMqyr.KWRAgE4wJd8bSIcWHr54Re0naYigrSS', 'f', '2025-12-22 19:04:41.860203', '2025-11-19 10:06:50.481633', '2025-12-22 19:04:41.882746'), (8, 'roe', 'aa@gmail.com', '$2a$10$QAUWICj6625m45YatVolJOwdcZff5IDWlMwgkuD9Y0cTwKMUpV1MG', 'f', '2025-12-25 16:22:10.155871', '2025-12-23 10:53:18.333846', '2025-12-25 16:22:10.186331'), (6, 'qweq', 'qweq@example.com', '$2a$10$wh2v5X9BiTZ0s/FxFYuP2.oEFcG5MVtOGEa7V2.1N1qCQLucueZve', 'f', '2025-11-20 20:00:13.586802', '2025-11-20 20:00:12.467442', '2025-11-20 20:00:13.58842'), (7, 'jack', 'jack@example.com', '$2a$10$xGmpk51yX/VzFzkEmXekCOLOR/.2o0/04IYatqtqHtsBgs8dA0H2e', 'f', '2025-11-20 20:11:03.336398', '2025-11-20 20:11:01.692857', '2025-11-20 20:11:03.341338'), (4, 'admin', 'admin@example.com', '$2a$10$eoJCtYXY2BRkaoV4.CkTv.K9Ji8Qv.VOSe/gNayO3P6fl15M9.LhS', 'f', '2025-11-20 22:32:01.774637', '2025-11-18 21:18:54.701074', '2025-11-20 22:32:01.815705');
COMMIT;

-- ----------------------------
-- Table structure for videos
-- ----------------------------
CREATE TABLE "videos" (
  "id" int8 NOT NULL DEFAULT nextval('videos_id_seq'::regclass),
  "user_id" int8 NOT NULL,
  "file_name" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "file_hash" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "file_path" varchar(500) COLLATE "pg_catalog"."default" NOT NULL,
  "file_size" int8 NOT NULL,
  "mime_type" varchar(100) COLLATE "pg_catalog"."default",
  "duration_seconds" int4,
  "description" varchar(500) COLLATE "pg_catalog"."default",
  "created_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updated_at" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP
)
;
COMMENT ON COLUMN "videos"."file_hash" IS 'SHA-256文件哈希（用于去重）';
COMMENT ON COLUMN "videos"."file_path" IS '文件存储路径';
COMMENT ON COLUMN "videos"."file_size" IS '文件大小（字节）';
COMMENT ON COLUMN "videos"."mime_type" IS 'MIME类型（如video/mp4）';
COMMENT ON COLUMN "videos"."duration_seconds" IS '视频时长（秒）';
COMMENT ON TABLE "videos" IS '视频文件表';

-- ----------------------------
-- Records of videos
-- ----------------------------
BEGIN;
INSERT INTO "videos" ("id", "user_id", "file_name", "file_hash", "file_path", "file_size", "mime_type", "duration_seconds", "description", "created_at", "updated_at") VALUES (113, 9, '17AB90FA620E232E08D0B6B8494FD073.png', '622a1dc0917ae4b575ce0e99bfa352120b4f4ae3bd90fa23da5858455a373788', 'D:\BaiduSyncdisk\IDECode\video-detection-system\.\uploads\9\98d18bf1b7ce4cae8da7e296cc58d378.png', 3819294, 'image/png', NULL, '17AB90FA620E232E08D0B6B8494FD073.png', '2025-12-24 22:37:45.390259', '2025-12-24 22:37:45.390259'), (114, 8, 'Desktop 2025.10.30 - 16.59.01.01.mp4', '5eb32febdcfded740530cbea052dde5f61830f9aac517adf4b91c2dbf2e373d4', 'D:\BaiduSyncdisk\IDECode\video-detection-system\.\uploads\8\87df13e844ba48b4b6db273af4ac4ceb.mp4', 159125564, 'video/mp4', NULL, 'Desktop 2025.10.30 - 16.59.01.01.mp4', '2025-12-24 22:59:50.141108', '2025-12-24 22:59:50.141108'), (115, 8, 'default_avatar.jpg', '39e2e6bc48e896c6ebaaf47061303e7bd8665a47fa6c00d4a76960cfc9464fc2', 'D:\BaiduSyncdisk\IDECode\video-detection-system\.\uploads\8\ca7700298d244ea3ad95d04a2551811b.jpg', 81857, 'image/jpeg', NULL, 'default_avatar.jpg', '2025-12-25 15:36:54.779051', '2025-12-25 15:36:54.779051'), (116, 8, '考试系统.drawio.png', '3ef913c1f87da36773dad56f4b52c4441ed1541755b7698e4f1ac97c1b0c025e', 'D:\BaiduSyncdisk\IDECode\video-detection-system\.\uploads\8\1832726b805a4ef683090b8a9ed7d3dc.png', 158149, 'image/png', NULL, '考试系统.drawio.png', '2025-12-25 15:38:14.423413', '2025-12-25 15:38:14.423413'), (120, 8, 'QQ20251225-154959.mp4', '0d0dd792a1815e1509ae1c920765a452f2a8c5342d22ecf968807556f6ad92b3', 'D:\BaiduSyncdisk\IDECode\video-detection-system\.\uploads\8\7df19362799e4436b170477cbca35c12.mp4', 2731776, 'video/mp4', NULL, 'QQ20251225-154959.mp4', '2025-12-25 16:02:42.89563', '2025-12-25 16:02:42.89563'), (125, 8, '171B6EC7B27917EB2E7B9FFE1D6AE506.jpg', '302ac71afa7d979e3fee93faf99d9d4025d563f23b2f88c6b4de3e99007271d0', 'D:\BaiduSyncdisk\IDECode\video-detection-system\.\uploads\8\b1d4dbc63af64aa48423015f7fc3c0ef.jpg', 30284, 'image/jpeg', NULL, '171B6EC7B27917EB2E7B9FFE1D6AE506.jpg', '2025-12-25 16:22:41.787836', '2025-12-25 16:22:41.787836'), (126, 8, '4047BC8FE0DDCA90922E55FEF53CF8F9.png', '7fb2e80df37c43224cf58c571a9851676a962e54b2b0d5e91f342a8041da68ad', 'D:\BaiduSyncdisk\IDECode\video-detection-system\.\uploads\8\b14f7a29276547dbb425323fd88f84fc.png', 367399, 'image/png', NULL, '4047BC8FE0DDCA90922E55FEF53CF8F9.png', '2025-12-25 16:23:16.441536', '2025-12-25 16:23:16.441536'), (127, 8, 'QQ20251225-1678.mp4', 'b75efa1f29a7fdd53afb82d17739184562530a126e635d53c72f436dced3d79c', 'D:\BaiduSyncdisk\IDECode\video-detection-system\.\uploads\8\0ae839f4f1fd41ebb6d618a2abd0601c.mp4', 13372354, 'video/mp4', NULL, 'QQ20251225-1678.mp4', '2025-12-25 16:24:23.201111', '2025-12-25 16:24:23.201111');
COMMIT;

-- ----------------------------
-- Function structure for update_updated_at_column
-- ----------------------------
CREATE OR REPLACE FUNCTION "update_updated_at_column"()
  RETURNS "pg_catalog"."trigger" AS $BODY$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;

-- ----------------------------
-- View structure for user_statistics
-- ----------------------------
CREATE VIEW "user_statistics" AS  SELECT u.id AS user_id,
    u.username,
    u.email,
    count(DISTINCT v.id) AS total_videos,
    count(DISTINCT dt.id) AS total_tasks,
    count(DISTINCT dr.id) AS total_detections,
    count(DISTINCT
        CASE
            WHEN dr.prediction::text = 'AUTHENTIC'::text THEN dr.id
            ELSE NULL::bigint
        END) AS authentic_count,
    count(DISTINCT
        CASE
            WHEN dr.prediction::text = 'FAKE'::text THEN dr.id
            ELSE NULL::bigint
        END) AS fake_count,
    count(DISTINCT
        CASE
            WHEN dr.prediction::text = 'UNCERTAIN'::text THEN dr.id
            ELSE NULL::bigint
        END) AS uncertain_count,
    avg(dr.confidence) AS avg_confidence,
    max(v.created_at) AS last_upload_at,
    max(u.last_login_at) AS last_login_at
   FROM users u
     LEFT JOIN videos v ON u.id = v.user_id
     LEFT JOIN detection_tasks dt ON u.id = dt.user_id
     LEFT JOIN detection_results dr ON u.id = dr.user_id
  GROUP BY u.id, u.username, u.email;
COMMENT ON VIEW "user_statistics" IS '用户统计视图';

-- ----------------------------
-- View structure for system_overview
-- ----------------------------
CREATE VIEW "system_overview" AS  SELECT ( SELECT count(*) AS count
           FROM users
          WHERE users.is_active = true) AS active_users,
    ( SELECT count(*) AS count
           FROM videos) AS total_videos,
    ( SELECT count(*) AS count
           FROM detection_tasks) AS total_tasks,
    ( SELECT count(*) AS count
           FROM detection_tasks
          WHERE detection_tasks.status::text = 'PENDING'::text) AS pending_tasks,
    ( SELECT count(*) AS count
           FROM detection_tasks
          WHERE detection_tasks.status::text = 'PROCESSING'::text) AS processing_tasks,
    ( SELECT count(*) AS count
           FROM detection_tasks
          WHERE detection_tasks.status::text = 'COMPLETED'::text) AS completed_tasks,
    ( SELECT count(*) AS count
           FROM detection_tasks
          WHERE detection_tasks.status::text = 'FAILED'::text) AS failed_tasks,
    ( SELECT count(*) AS count
           FROM detection_results) AS total_detections,
    ( SELECT count(*) AS count
           FROM detection_results
          WHERE detection_results.prediction::text = 'FAKE'::text) AS fake_detected,
    ( SELECT avg(detection_results.confidence) AS avg
           FROM detection_results) AS avg_confidence,
    ( SELECT count(*) AS count
           FROM reports
          WHERE reports.status::text = 'SUBMITTED'::text) AS pending_reports,
    ( SELECT sum(videos.file_size) AS sum
           FROM videos) AS total_storage_bytes;
COMMENT ON VIEW "system_overview" IS '系统概览统计视图';

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "audit_logs_id_seq"
OWNED BY "audit_logs"."id";
SELECT setval('"audit_logs_id_seq"', 245, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "chunk_metadata_id_seq"
OWNED BY "chunk_metadata"."id";
SELECT setval('"chunk_metadata_id_seq"', 1, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "detection_results_id_seq"
OWNED BY "detection_results"."id";
SELECT setval('"detection_results_id_seq"', 120, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "detection_tasks_id_seq"
OWNED BY "detection_tasks"."id";
SELECT setval('"detection_tasks_id_seq"', 124, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "reports_id_seq"
OWNED BY "reports"."id";
SELECT setval('"reports_id_seq"', 1, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "roles_id_seq"
OWNED BY "roles"."id";
SELECT setval('"roles_id_seq"', 3, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "users_id_seq"
OWNED BY "users"."id";
SELECT setval('"users_id_seq"', 9, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "videos_id_seq"
OWNED BY "videos"."id";
SELECT setval('"videos_id_seq"', 127, true);

-- ----------------------------
-- Indexes structure for table audit_logs
-- ----------------------------
CREATE INDEX "idx_audit_action" ON "audit_logs" USING btree (
  "action" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);
CREATE INDEX "idx_audit_created_at" ON "audit_logs" USING btree (
  "created_at" "pg_catalog"."timestamp_ops" DESC NULLS FIRST
);
CREATE INDEX "idx_audit_ip_address" ON "audit_logs" USING btree (
  "ip_address" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);
CREATE INDEX "idx_audit_resource_type" ON "audit_logs" USING btree (
  "resource_type" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);
CREATE INDEX "idx_audit_user_id" ON "audit_logs" USING btree (
  "user_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);
CREATE INDEX "idx_created_at" ON "audit_logs" USING btree (
  "created_at" "pg_catalog"."timestamp_ops" ASC NULLS LAST
);
CREATE INDEX "idx_resource_type" ON "audit_logs" USING btree (
  "resource_type" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);
CREATE INDEX "idx_user_id" ON "audit_logs" USING btree (
  "user_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);

-- ----------------------------
-- Triggers structure for table audit_logs
-- ----------------------------
CREATE TRIGGER "update_audit_updated_at" BEFORE UPDATE ON "audit_logs"
FOR EACH ROW
EXECUTE PROCEDURE "public"."update_updated_at_column"();

-- ----------------------------
-- Primary Key structure for table audit_logs
-- ----------------------------
ALTER TABLE "audit_logs" ADD CONSTRAINT "audit_logs_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table chunk_metadata
-- ----------------------------
CREATE INDEX "idx_chunk_file_id" ON "chunk_metadata" USING btree (
  "file_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);
CREATE INDEX "idx_chunk_uploaded_at" ON "chunk_metadata" USING btree (
  "uploaded_at" "pg_catalog"."timestamp_ops" ASC NULLS LAST
);
CREATE INDEX "idx_file_id" ON "chunk_metadata" USING btree (
  "file_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);

-- ----------------------------
-- Triggers structure for table chunk_metadata
-- ----------------------------
CREATE TRIGGER "update_chunks_updated_at" BEFORE UPDATE ON "chunk_metadata"
FOR EACH ROW
EXECUTE PROCEDURE "public"."update_updated_at_column"();

-- ----------------------------
-- Uniques structure for table chunk_metadata
-- ----------------------------
ALTER TABLE "chunk_metadata" ADD CONSTRAINT "uq_file_chunk" UNIQUE ("file_id", "chunk_index");
ALTER TABLE "chunk_metadata" ADD CONSTRAINT "ukcw3ypnul3orf501i3h03hin35" UNIQUE ("file_id", "chunk_index");

-- ----------------------------
-- Checks structure for table chunk_metadata
-- ----------------------------
ALTER TABLE "chunk_metadata" ADD CONSTRAINT "chk_chunk_index" CHECK (chunk_index >= 0);

-- ----------------------------
-- Primary Key structure for table chunk_metadata
-- ----------------------------
ALTER TABLE "chunk_metadata" ADD CONSTRAINT "chunk_metadata_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table detection_results
-- ----------------------------
CREATE INDEX "idx_results_created_at" ON "detection_results" USING btree (
  "created_at" "pg_catalog"."timestamp_ops" DESC NULLS FIRST
);
CREATE INDEX "idx_results_features" ON "detection_results" USING gin (
  "features" "pg_catalog"."jsonb_ops"
);
CREATE INDEX "idx_results_prediction" ON "detection_results" USING btree (
  "prediction" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);
CREATE INDEX "idx_results_task_id" ON "detection_results" USING btree (
  "task_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);
CREATE INDEX "idx_results_user_id" ON "detection_results" USING btree (
  "user_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);
CREATE INDEX "idx_results_video_id" ON "detection_results" USING btree (
  "video_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);
CREATE INDEX "idx_task_id" ON "detection_results" USING btree (
  "task_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);
CREATE INDEX "idx_video_id" ON "detection_results" USING btree (
  "video_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);

-- ----------------------------
-- Triggers structure for table detection_results
-- ----------------------------
CREATE TRIGGER "update_results_updated_at" BEFORE UPDATE ON "detection_results"
FOR EACH ROW
EXECUTE PROCEDURE "public"."update_updated_at_column"();

-- ----------------------------
-- Checks structure for table detection_results
-- ----------------------------
ALTER TABLE "detection_results" ADD CONSTRAINT "chk_confidence_range" CHECK (confidence >= 0::numeric AND confidence <= 1::numeric);
ALTER TABLE "detection_results" ADD CONSTRAINT "chk_prediction" CHECK (prediction::text = ANY (ARRAY['AUTHENTIC'::character varying, 'FAKE'::character varying, 'UNCERTAIN'::character varying]::text[]));

-- ----------------------------
-- Primary Key structure for table detection_results
-- ----------------------------
ALTER TABLE "detection_results" ADD CONSTRAINT "detection_results_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table detection_tasks
-- ----------------------------
CREATE INDEX "idx_tasks_created_at" ON "detection_tasks" USING btree (
  "created_at" "pg_catalog"."timestamp_ops" DESC NULLS FIRST
);
CREATE INDEX "idx_tasks_status" ON "detection_tasks" USING btree (
  "status" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);
CREATE INDEX "idx_tasks_task_id" ON "detection_tasks" USING btree (
  "task_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);
CREATE INDEX "idx_tasks_user_id" ON "detection_tasks" USING btree (
  "user_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);
CREATE INDEX "idx_tasks_video_id" ON "detection_tasks" USING btree (
  "video_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);

-- ----------------------------
-- Triggers structure for table detection_tasks
-- ----------------------------
CREATE TRIGGER "update_tasks_updated_at" BEFORE UPDATE ON "detection_tasks"
FOR EACH ROW
EXECUTE PROCEDURE "public"."update_updated_at_column"();

-- ----------------------------
-- Uniques structure for table detection_tasks
-- ----------------------------
ALTER TABLE "detection_tasks" ADD CONSTRAINT "detection_tasks_task_id_key" UNIQUE ("task_id");

-- ----------------------------
-- Checks structure for table detection_tasks
-- ----------------------------
ALTER TABLE "detection_tasks" ADD CONSTRAINT "chk_retry_count" CHECK (retry_count >= 0);
ALTER TABLE "detection_tasks" ADD CONSTRAINT "chk_task_status" CHECK (status::text = ANY (ARRAY['PENDING'::character varying, 'PROCESSING'::character varying, 'COMPLETED'::character varying, 'FAILED'::character varying]::text[]));
ALTER TABLE "detection_tasks" ADD CONSTRAINT "chk_progress_range" CHECK (progress >= 0 AND progress <= 100);

-- ----------------------------
-- Primary Key structure for table detection_tasks
-- ----------------------------
ALTER TABLE "detection_tasks" ADD CONSTRAINT "detection_tasks_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table reports
-- ----------------------------
CREATE INDEX "idx_detection_id" ON "reports" USING btree (
  "detection_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);
CREATE INDEX "idx_reporter_id" ON "reports" USING btree (
  "reporter_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);
CREATE INDEX "idx_reports_created_at" ON "reports" USING btree (
  "created_at" "pg_catalog"."timestamp_ops" DESC NULLS FIRST
);
CREATE INDEX "idx_reports_detection_id" ON "reports" USING btree (
  "detection_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);
CREATE INDEX "idx_reports_reporter_id" ON "reports" USING btree (
  "reporter_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);
CREATE INDEX "idx_reports_status" ON "reports" USING btree (
  "status" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);
CREATE INDEX "idx_status" ON "reports" USING btree (
  "status" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);

-- ----------------------------
-- Triggers structure for table reports
-- ----------------------------
CREATE TRIGGER "update_reports_updated_at" BEFORE UPDATE ON "reports"
FOR EACH ROW
EXECUTE PROCEDURE "public"."update_updated_at_column"();

-- ----------------------------
-- Checks structure for table reports
-- ----------------------------
ALTER TABLE "reports" ADD CONSTRAINT "chk_report_status" CHECK (status::text = ANY (ARRAY['SUBMITTED'::character varying, 'UNDER_REVIEW'::character varying, 'RESOLVED'::character varying, 'REJECTED'::character varying]::text[]));

-- ----------------------------
-- Primary Key structure for table reports
-- ----------------------------
ALTER TABLE "reports" ADD CONSTRAINT "reports_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Triggers structure for table roles
-- ----------------------------
CREATE TRIGGER "update_roles_updated_at" BEFORE UPDATE ON "roles"
FOR EACH ROW
EXECUTE PROCEDURE "public"."update_updated_at_column"();

-- ----------------------------
-- Uniques structure for table roles
-- ----------------------------
ALTER TABLE "roles" ADD CONSTRAINT "roles_name_key" UNIQUE ("name");

-- ----------------------------
-- Checks structure for table roles
-- ----------------------------
ALTER TABLE "roles" ADD CONSTRAINT "chk_role_name" CHECK (name::text = ANY (ARRAY['ROLE_USER'::character varying, 'ROLE_ADMIN'::character varying, 'ROLE_MODERATOR'::character varying]::text[]));

-- ----------------------------
-- Primary Key structure for table roles
-- ----------------------------
ALTER TABLE "roles" ADD CONSTRAINT "roles_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table user_roles
-- ----------------------------
CREATE INDEX "idx_user_roles_role_id" ON "user_roles" USING btree (
  "role_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);
CREATE INDEX "idx_user_roles_user_id" ON "user_roles" USING btree (
  "user_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);

-- ----------------------------
-- Primary Key structure for table user_roles
-- ----------------------------
ALTER TABLE "user_roles" ADD CONSTRAINT "user_roles_pkey" PRIMARY KEY ("user_id", "role_id");

-- ----------------------------
-- Indexes structure for table users
-- ----------------------------
CREATE INDEX "idx_email" ON "users" USING btree (
  "email" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);
CREATE INDEX "idx_username" ON "users" USING btree (
  "username" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);
CREATE INDEX "idx_users_email" ON "users" USING btree (
  "email" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);
CREATE INDEX "idx_users_is_active" ON "users" USING btree (
  "is_active" "pg_catalog"."bool_ops" ASC NULLS LAST
);
CREATE INDEX "idx_users_username" ON "users" USING btree (
  "username" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);

-- ----------------------------
-- Triggers structure for table users
-- ----------------------------
CREATE TRIGGER "update_users_updated_at" BEFORE UPDATE ON "users"
FOR EACH ROW
EXECUTE PROCEDURE "public"."update_updated_at_column"();

-- ----------------------------
-- Uniques structure for table users
-- ----------------------------
ALTER TABLE "users" ADD CONSTRAINT "users_username_key" UNIQUE ("username");
ALTER TABLE "users" ADD CONSTRAINT "users_email_key" UNIQUE ("email");

-- ----------------------------
-- Checks structure for table users
-- ----------------------------
ALTER TABLE "users" ADD CONSTRAINT "chk_username_length" CHECK (length(username::text) >= 3);
ALTER TABLE "users" ADD CONSTRAINT "chk_email_format" CHECK (email::text ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$'::text);

-- ----------------------------
-- Primary Key structure for table users
-- ----------------------------
ALTER TABLE "users" ADD CONSTRAINT "users_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table videos
-- ----------------------------
CREATE INDEX "idx_file_hash" ON "videos" USING btree (
  "file_hash" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);
CREATE INDEX "idx_videos_created_at" ON "videos" USING btree (
  "created_at" "pg_catalog"."timestamp_ops" DESC NULLS FIRST
);
CREATE INDEX "idx_videos_file_hash" ON "videos" USING btree (
  "file_hash" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);
CREATE INDEX "idx_videos_user_id" ON "videos" USING btree (
  "user_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);

-- ----------------------------
-- Triggers structure for table videos
-- ----------------------------
CREATE TRIGGER "update_videos_updated_at" BEFORE UPDATE ON "videos"
FOR EACH ROW
EXECUTE PROCEDURE "public"."update_updated_at_column"();

-- ----------------------------
-- Uniques structure for table videos
-- ----------------------------
ALTER TABLE "videos" ADD CONSTRAINT "videos_file_hash_key" UNIQUE ("file_hash");

-- ----------------------------
-- Checks structure for table videos
-- ----------------------------
ALTER TABLE "videos" ADD CONSTRAINT "chk_file_size_positive" CHECK (file_size > 0);
ALTER TABLE "videos" ADD CONSTRAINT "chk_duration_positive" CHECK (duration_seconds IS NULL OR duration_seconds > 0);

-- ----------------------------
-- Primary Key structure for table videos
-- ----------------------------
ALTER TABLE "videos" ADD CONSTRAINT "videos_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Foreign Keys structure for table detection_results
-- ----------------------------
ALTER TABLE "detection_results" ADD CONSTRAINT "fk_results_user" FOREIGN KEY ("user_id") REFERENCES "users" ("id") ON DELETE CASCADE ON UPDATE NO ACTION;
ALTER TABLE "detection_results" ADD CONSTRAINT "fk_results_video" FOREIGN KEY ("video_id") REFERENCES "videos" ("id") ON DELETE CASCADE ON UPDATE NO ACTION;

-- ----------------------------
-- Foreign Keys structure for table detection_tasks
-- ----------------------------
ALTER TABLE "detection_tasks" ADD CONSTRAINT "fk_tasks_user" FOREIGN KEY ("user_id") REFERENCES "users" ("id") ON DELETE CASCADE ON UPDATE NO ACTION;
ALTER TABLE "detection_tasks" ADD CONSTRAINT "fk_tasks_video" FOREIGN KEY ("video_id") REFERENCES "videos" ("id") ON DELETE CASCADE ON UPDATE NO ACTION;

-- ----------------------------
-- Foreign Keys structure for table reports
-- ----------------------------
ALTER TABLE "reports" ADD CONSTRAINT "fk_reports_detection" FOREIGN KEY ("detection_id") REFERENCES "detection_results" ("id") ON DELETE CASCADE ON UPDATE NO ACTION;
ALTER TABLE "reports" ADD CONSTRAINT "fk_reports_reporter" FOREIGN KEY ("reporter_id") REFERENCES "users" ("id") ON DELETE CASCADE ON UPDATE NO ACTION;
ALTER TABLE "reports" ADD CONSTRAINT "fk_reports_reviewer" FOREIGN KEY ("reviewed_by") REFERENCES "users" ("id") ON DELETE SET NULL ON UPDATE NO ACTION;

-- ----------------------------
-- Foreign Keys structure for table user_roles
-- ----------------------------
ALTER TABLE "user_roles" ADD CONSTRAINT "fk_user_roles_role" FOREIGN KEY ("role_id") REFERENCES "roles" ("id") ON DELETE CASCADE ON UPDATE NO ACTION;
ALTER TABLE "user_roles" ADD CONSTRAINT "fk_user_roles_user" FOREIGN KEY ("user_id") REFERENCES "users" ("id") ON DELETE CASCADE ON UPDATE NO ACTION;

-- ----------------------------
-- Foreign Keys structure for table videos
-- ----------------------------
ALTER TABLE "videos" ADD CONSTRAINT "fk_videos_user" FOREIGN KEY ("user_id") REFERENCES "users" ("id") ON DELETE CASCADE ON UPDATE NO ACTION;
