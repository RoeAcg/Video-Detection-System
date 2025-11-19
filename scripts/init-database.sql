-- ==================================================
-- 视频深伪检测系统 - 完整数据库初始化脚本
-- ==================================================
-- PostgreSQL 15+
-- 字符集: UTF-8
-- 生成时间: 2025-11-17
-- ==================================================

-- 设置客户端编码
SET client_encoding = 'UTF8';

-- 创建数据库（如果在Docker中，可以注释掉）
-- CREATE DATABASE video_detection WITH ENCODING 'UTF8';

-- 连接到数据库
\c video_detection;

-- ==================================================
-- 1. 删除已存在的表（仅用于重新初始化，生产环境请注释）
-- ==================================================
DROP TABLE IF EXISTS user_roles CASCADE;
DROP TABLE IF EXISTS audit_logs CASCADE;
DROP TABLE IF EXISTS reports CASCADE;
DROP TABLE IF EXISTS chunk_metadata CASCADE;
DROP TABLE IF EXISTS detection_results CASCADE;
DROP TABLE IF EXISTS detection_tasks CASCADE;
DROP TABLE IF EXISTS videos CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS roles CASCADE;

-- ==================================================
-- 2. 创建角色表
-- ==================================================
CREATE TABLE roles (
                       id BIGSERIAL PRIMARY KEY,
                       name VARCHAR(50) UNIQUE NOT NULL,
                       description VARCHAR(200),
                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                       CONSTRAINT chk_role_name CHECK (name IN ('ROLE_USER', 'ROLE_ADMIN', 'ROLE_MODERATOR'))
);

COMMENT ON TABLE roles IS '用户角色表';
COMMENT ON COLUMN roles.name IS '角色名称（枚举）';
COMMENT ON COLUMN roles.description IS '角色描述';

-- ==================================================
-- 3. 创建用户表
-- ==================================================
CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       username VARCHAR(50) UNIQUE NOT NULL,
                       email VARCHAR(100) UNIQUE NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       is_active BOOLEAN NOT NULL DEFAULT TRUE,
                       last_login_at TIMESTAMP,
                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                       CONSTRAINT chk_username_length CHECK (LENGTH(username) >= 3),
                       CONSTRAINT chk_email_format CHECK (email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$')
    );

COMMENT ON TABLE users IS '用户表';
COMMENT ON COLUMN users.username IS '用户名（3-50字符）';
COMMENT ON COLUMN users.email IS '邮箱地址';
COMMENT ON COLUMN users.password IS 'BCrypt加密密码';
COMMENT ON COLUMN users.is_active IS '账号是否激活';
COMMENT ON COLUMN users.last_login_at IS '最后登录时间';

-- 创建索引
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_is_active ON users(is_active);

-- ==================================================
-- 4. 创建用户角色关联表
-- ==================================================
CREATE TABLE user_roles (
                            user_id BIGINT NOT NULL,
                            role_id BIGINT NOT NULL,

                            PRIMARY KEY (user_id, role_id),
                            CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                            CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

COMMENT ON TABLE user_roles IS '用户角色关联表';

-- 创建索引
CREATE INDEX idx_user_roles_user_id ON user_roles(user_id);
CREATE INDEX idx_user_roles_role_id ON user_roles(role_id);

-- ==================================================
-- 5. 创建视频表
-- ==================================================
CREATE TABLE videos (
                        id BIGSERIAL PRIMARY KEY,
                        user_id BIGINT NOT NULL,
                        file_name VARCHAR(255) NOT NULL,
                        file_hash VARCHAR(64) UNIQUE NOT NULL,
                        file_path VARCHAR(500) NOT NULL,
                        file_size BIGINT NOT NULL,
                        mime_type VARCHAR(100),
                        duration_seconds INTEGER,
                        description VARCHAR(500),
                        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                        CONSTRAINT fk_videos_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                        CONSTRAINT chk_file_size_positive CHECK (file_size > 0),
                        CONSTRAINT chk_duration_positive CHECK (duration_seconds IS NULL OR duration_seconds > 0)
);

COMMENT ON TABLE videos IS '视频文件表';
COMMENT ON COLUMN videos.file_hash IS 'SHA-256文件哈希（用于去重）';
COMMENT ON COLUMN videos.file_path IS '文件存储路径';
COMMENT ON COLUMN videos.file_size IS '文件大小（字节）';
COMMENT ON COLUMN videos.mime_type IS 'MIME类型（如video/mp4）';
COMMENT ON COLUMN videos.duration_seconds IS '视频时长（秒）';

-- 创建索引
CREATE INDEX idx_videos_user_id ON videos(user_id);
CREATE INDEX idx_videos_file_hash ON videos(file_hash);
CREATE INDEX idx_videos_created_at ON videos(created_at DESC);

-- ==================================================
-- 6. 创建检测任务表
-- ==================================================
CREATE TABLE detection_tasks (
                                 id BIGSERIAL PRIMARY KEY,
                                 task_id VARCHAR(36) UNIQUE NOT NULL,
                                 video_id BIGINT NOT NULL,
                                 user_id BIGINT NOT NULL,
                                 status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
                                 progress INTEGER NOT NULL DEFAULT 0,
                                 estimated_time_seconds INTEGER,
                                 started_at TIMESTAMP,
                                 completed_at TIMESTAMP,
                                 error_message VARCHAR(1000),
                                 retry_count INTEGER NOT NULL DEFAULT 0,
                                 created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                 updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                 CONSTRAINT fk_tasks_video FOREIGN KEY (video_id) REFERENCES videos(id) ON DELETE CASCADE,
                                 CONSTRAINT fk_tasks_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                                 CONSTRAINT chk_task_status CHECK (status IN ('PENDING', 'PROCESSING', 'COMPLETED', 'FAILED')),
                                 CONSTRAINT chk_progress_range CHECK (progress >= 0 AND progress <= 100),
                                 CONSTRAINT chk_retry_count CHECK (retry_count >= 0)
);

COMMENT ON TABLE detection_tasks IS '检测任务表';
COMMENT ON COLUMN detection_tasks.task_id IS '任务唯一标识（UUID）';
COMMENT ON COLUMN detection_tasks.status IS '任务状态：PENDING/PROCESSING/COMPLETED/FAILED';
COMMENT ON COLUMN detection_tasks.progress IS '任务进度（0-100）';
COMMENT ON COLUMN detection_tasks.retry_count IS '重试次数';

-- 创建索引
CREATE INDEX idx_tasks_task_id ON detection_tasks(task_id);
CREATE INDEX idx_tasks_user_id ON detection_tasks(user_id);
CREATE INDEX idx_tasks_video_id ON detection_tasks(video_id);
CREATE INDEX idx_tasks_status ON detection_tasks(status);
CREATE INDEX idx_tasks_created_at ON detection_tasks(created_at DESC);

-- ==================================================
-- 7. 创建检测结果表
-- ==================================================
CREATE TABLE detection_results (
                                   id BIGSERIAL PRIMARY KEY,
                                   task_id VARCHAR(36) NOT NULL,
                                   video_id BIGINT NOT NULL,
                                   user_id BIGINT NOT NULL,
                                   prediction VARCHAR(20) NOT NULL,
                                   confidence NUMERIC(5, 4),
                                   model_version VARCHAR(50),
                                   processing_time_ms BIGINT,
                                   frames_analyzed INTEGER,
                                   features JSONB,
                                   artifacts_detected VARCHAR(1000),
                                   created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                   updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                   CONSTRAINT fk_results_video FOREIGN KEY (video_id) REFERENCES videos(id) ON DELETE CASCADE,
                                   CONSTRAINT fk_results_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                                   CONSTRAINT chk_prediction CHECK (prediction IN ('AUTHENTIC', 'FAKE', 'UNCERTAIN')),
                                   CONSTRAINT chk_confidence_range CHECK (confidence >= 0 AND confidence <= 1)
);

COMMENT ON TABLE detection_results IS '检测结果表';
COMMENT ON COLUMN detection_results.prediction IS '预测结果：AUTHENTIC/FAKE/UNCERTAIN';
COMMENT ON COLUMN detection_results.confidence IS '置信度（0.0000-1.0000）';
COMMENT ON COLUMN detection_results.features IS 'JSON格式的特征向量';
COMMENT ON COLUMN detection_results.artifacts_detected IS '检测到的伪造迹象（逗号分隔）';

-- 创建索引
CREATE INDEX idx_results_task_id ON detection_results(task_id);
CREATE INDEX idx_results_video_id ON detection_results(video_id);
CREATE INDEX idx_results_user_id ON detection_results(user_id);
CREATE INDEX idx_results_prediction ON detection_results(prediction);
CREATE INDEX idx_results_created_at ON detection_results(created_at DESC);

-- JSONB索引（用于特征查询）
CREATE INDEX idx_results_features ON detection_results USING GIN(features);

-- ==================================================
-- 8. 创建分块上传元数据表
-- ==================================================
CREATE TABLE chunk_metadata (
                                id BIGSERIAL PRIMARY KEY,
                                file_id VARCHAR(36) NOT NULL,
                                chunk_index INTEGER NOT NULL,
                                chunk_hash VARCHAR(64),
                                chunk_path VARCHAR(500),
                                chunk_size BIGINT,
                                uploaded_at TIMESTAMP,
                                verified BOOLEAN NOT NULL DEFAULT FALSE,
                                created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                CONSTRAINT uq_file_chunk UNIQUE (file_id, chunk_index),
                                CONSTRAINT chk_chunk_index CHECK (chunk_index >= 0)
);

COMMENT ON TABLE chunk_metadata IS '分块上传元数据表';
COMMENT ON COLUMN chunk_metadata.file_id IS '文件标识（UUID）';
COMMENT ON COLUMN chunk_metadata.chunk_index IS '分块索引（从0开始）';
COMMENT ON COLUMN chunk_metadata.verified IS '分块是否已验证';

-- 创建索引
CREATE INDEX idx_chunk_file_id ON chunk_metadata(file_id);
CREATE INDEX idx_chunk_uploaded_at ON chunk_metadata(uploaded_at);

-- ==================================================
-- 9. 创建举报表
-- ==================================================
CREATE TABLE reports (
                         id BIGSERIAL PRIMARY KEY,
                         detection_id BIGINT NOT NULL,
                         reporter_id BIGINT NOT NULL,
                         reason VARCHAR(50),
                         evidence VARCHAR(2000),
                         status VARCHAR(20) NOT NULL DEFAULT 'SUBMITTED',
                         reviewed_by BIGINT,
                         reviewed_at TIMESTAMP,
                         review_notes VARCHAR(1000),
                         created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                         CONSTRAINT fk_reports_detection FOREIGN KEY (detection_id) REFERENCES detection_results(id) ON DELETE CASCADE,
                         CONSTRAINT fk_reports_reporter FOREIGN KEY (reporter_id) REFERENCES users(id) ON DELETE CASCADE,
                         CONSTRAINT fk_reports_reviewer FOREIGN KEY (reviewed_by) REFERENCES users(id) ON DELETE SET NULL,
                         CONSTRAINT chk_report_status CHECK (status IN ('SUBMITTED', 'UNDER_REVIEW', 'RESOLVED', 'REJECTED'))
);

COMMENT ON TABLE reports IS '用户举报表';
COMMENT ON COLUMN reports.status IS '举报状态：SUBMITTED/UNDER_REVIEW/RESOLVED/REJECTED';

-- 创建索引
CREATE INDEX idx_reports_detection_id ON reports(detection_id);
CREATE INDEX idx_reports_reporter_id ON reports(reporter_id);
CREATE INDEX idx_reports_status ON reports(status);
CREATE INDEX idx_reports_created_at ON reports(created_at DESC);

-- ==================================================
-- 10. 创建审计日志表
-- ==================================================
CREATE TABLE audit_logs (
                            id BIGSERIAL PRIMARY KEY,
                            user_id BIGINT,
                            action VARCHAR(100) NOT NULL,
                            resource_type VARCHAR(50),
                            resource_id BIGINT,
                            old_value VARCHAR(2000),
                            new_value VARCHAR(2000),
                            ip_address VARCHAR(45),
                            user_agent VARCHAR(500),
                            request_method VARCHAR(10),
                            request_uri VARCHAR(500),
                            status_code INTEGER,
                            created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                            CONSTRAINT fk_audit_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

COMMENT ON TABLE audit_logs IS '审计日志表';
COMMENT ON COLUMN audit_logs.action IS '操作名称（如USER_LOGIN）';
COMMENT ON COLUMN audit_logs.resource_type IS '资源类型（如USER、VIDEO）';
COMMENT ON COLUMN audit_logs.ip_address IS '客户端IP地址（支持IPv6）';

-- 创建索引
CREATE INDEX idx_audit_user_id ON audit_logs(user_id);
CREATE INDEX idx_audit_action ON audit_logs(action);
CREATE INDEX idx_audit_resource_type ON audit_logs(resource_type);
CREATE INDEX idx_audit_created_at ON audit_logs(created_at DESC);
CREATE INDEX idx_audit_ip_address ON audit_logs(ip_address);

-- ==================================================
-- 11. 创建触发器：自动更新 updated_at
-- ==================================================
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- 为所有表添加触发器
CREATE TRIGGER update_roles_updated_at BEFORE UPDATE ON roles
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_videos_updated_at BEFORE UPDATE ON videos
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_tasks_updated_at BEFORE UPDATE ON detection_tasks
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_results_updated_at BEFORE UPDATE ON detection_results
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_chunks_updated_at BEFORE UPDATE ON chunk_metadata
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_reports_updated_at BEFORE UPDATE ON reports
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_audit_updated_at BEFORE UPDATE ON audit_logs
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- ==================================================
-- 12. 插入初始数据
-- ==================================================

-- 插入默认角色
INSERT INTO roles (name, description) VALUES
                                          ('ROLE_USER', '普通用户，可以上传视频和查看自己的检测结果'),
                                          ('ROLE_ADMIN', '管理员，拥有所有权限'),
                                          ('ROLE_MODERATOR', '版主，可以审核举报和管理内容')
    ON CONFLICT (name) DO NOTHING;

-- 插入默认管理员用户
-- 用户名: admin
-- 密码: Admin123 (BCrypt加密后的值)
INSERT INTO users (username, email, password, is_active) VALUES
    ('admin', 'admin@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye1lULqQGFGMGEOqDEq.Mx.tGhDYcxCJi', true)
    ON CONFLICT (username) DO NOTHING;

-- 为管理员分配所有角色
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
         CROSS JOIN roles r
WHERE u.username = 'admin'
    ON CONFLICT DO NOTHING;

-- 插入测试用户（可选，生产环境可以删除）
INSERT INTO users (username, email, password, is_active) VALUES
    ('testuser', 'test@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye1lULqQGFGMGEOqDEq.Mx.tGhDYcxCJi', true)
    ON CONFLICT (username) DO NOTHING;

-- 为测试用户分配普通用户角色
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.username = 'testuser' AND r.name = 'ROLE_USER'
    ON CONFLICT DO NOTHING;

-- ==================================================
-- 13. 创建有用的视图
-- ==================================================

-- 用户统计视图
CREATE OR REPLACE VIEW user_statistics AS
SELECT
    u.id AS user_id,
    u.username,
    u.email,
    COUNT(DISTINCT v.id) AS total_videos,
    COUNT(DISTINCT dt.id) AS total_tasks,
    COUNT(DISTINCT dr.id) AS total_detections,
    COUNT(DISTINCT CASE WHEN dr.prediction = 'AUTHENTIC' THEN dr.id END) AS authentic_count,
    COUNT(DISTINCT CASE WHEN dr.prediction = 'FAKE' THEN dr.id END) AS fake_count,
    COUNT(DISTINCT CASE WHEN dr.prediction = 'UNCERTAIN' THEN dr.id END) AS uncertain_count,
    AVG(dr.confidence) AS avg_confidence,
    MAX(v.created_at) AS last_upload_at,
    MAX(u.last_login_at) AS last_login_at
FROM users u
         LEFT JOIN videos v ON u.id = v.user_id
         LEFT JOIN detection_tasks dt ON u.id = dt.user_id
         LEFT JOIN detection_results dr ON u.id = dr.user_id
GROUP BY u.id, u.username, u.email;

COMMENT ON VIEW user_statistics IS '用户统计视图';

-- 系统概览视图
CREATE OR REPLACE VIEW system_overview AS
SELECT
    (SELECT COUNT(*) FROM users WHERE is_active = true) AS active_users,
    (SELECT COUNT(*) FROM videos) AS total_videos,
    (SELECT COUNT(*) FROM detection_tasks) AS total_tasks,
    (SELECT COUNT(*) FROM detection_tasks WHERE status = 'PENDING') AS pending_tasks,
    (SELECT COUNT(*) FROM detection_tasks WHERE status = 'PROCESSING') AS processing_tasks,
    (SELECT COUNT(*) FROM detection_tasks WHERE status = 'COMPLETED') AS completed_tasks,
    (SELECT COUNT(*) FROM detection_tasks WHERE status = 'FAILED') AS failed_tasks,
    (SELECT COUNT(*) FROM detection_results) AS total_detections,
    (SELECT COUNT(*) FROM detection_results WHERE prediction = 'FAKE') AS fake_detected,
    (SELECT AVG(confidence) FROM detection_results) AS avg_confidence,
    (SELECT COUNT(*) FROM reports WHERE status = 'SUBMITTED') AS pending_reports,
    (SELECT SUM(file_size) FROM videos) AS total_storage_bytes;

COMMENT ON VIEW system_overview IS '系统概览统计视图';

-- ==================================================
-- 14. 创建分区表（可选，用于大数据量场景）
-- ==================================================

-- 如果审计日志量很大，可以按月分区
-- CREATE TABLE audit_logs_2025_11 PARTITION OF audit_logs
--     FOR VALUES FROM ('2025-11-01') TO ('2025-12-01');

-- ==================================================
-- 15. 性能优化建议
-- ==================================================

-- 调整PostgreSQL配置（需要在postgresql.conf中设置）
-- shared_buffers = 256MB
-- effective_cache_size = 1GB
-- maintenance_work_mem = 128MB
-- checkpoint_completion_target = 0.9
-- wal_buffers = 16MB
-- default_statistics_target = 100
-- random_page_cost = 1.1
-- effective_io_concurrency = 200
-- work_mem = 4MB
-- min_wal_size = 1GB
-- max_wal_size = 4GB

-- ==================================================
-- 16. 授权（可选）
-- ==================================================

-- 创建应用用户（如果需要）
-- CREATE USER app_user WITH PASSWORD 'secure_password';
-- GRANT CONNECT ON DATABASE video_detection TO app_user;
-- GRANT USAGE ON SCHEMA public TO app_user;
-- GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO app_user;
-- GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO app_user;

-- ==================================================
-- 17. 完成信息
-- ==================================================
DO $$
DECLARE
table_count INTEGER;
    index_count INTEGER;
    view_count INTEGER;
BEGIN
    -- 统计表数量
SELECT COUNT(*) INTO table_count
FROM information_schema.tables
WHERE table_schema = 'public' AND table_type = 'BASE TABLE';

-- 统计索引数量
SELECT COUNT(*) INTO index_count
FROM pg_indexes
WHERE schemaname = 'public';

-- 统计视图数量
SELECT COUNT(*) INTO view_count
FROM information_schema.views
WHERE table_schema = 'public';

RAISE NOTICE '';
    RAISE NOTICE '================================================================';
    RAISE NOTICE '           视频深伪检测系统 - 数据库初始化完成！';
    RAISE NOTICE '================================================================';
    RAISE NOTICE '';
    RAISE NOTICE '数据库名称: video_detection';
    RAISE NOTICE '字符集: UTF8';
    RAISE NOTICE '数据表数量: %', table_count;
    RAISE NOTICE '索引数量: %', index_count;
    RAISE NOTICE '视图数量: %', view_count;
    RAISE NOTICE '';
    RAISE NOTICE '----------------------------------------------------------------';
    RAISE NOTICE '默认账号信息：';
    RAISE NOTICE '----------------------------------------------------------------';
    RAISE NOTICE '管理员账号:';
    RAISE NOTICE '  用户名: admin';
    RAISE NOTICE '  密码: Admin123';
    RAISE NOTICE '  角色: ROLE_ADMIN, ROLE_MODERATOR, ROLE_USER';
    RAISE NOTICE '';
    RAISE NOTICE '测试账号:';
    RAISE NOTICE '  用户名: testuser';
    RAISE NOTICE '  密码: Admin123';
    RAISE NOTICE '  角色: ROLE_USER';
    RAISE NOTICE '----------------------------------------------------------------';
    RAISE NOTICE '';
    RAISE NOTICE '⚠️  重要提示：';
    RAISE NOTICE '  1. 请立即更改默认管理员密码！';
    RAISE NOTICE '  2. 生产环境请删除测试账号';
    RAISE NOTICE '  3. 建议配置定期备份';
    RAISE NOTICE '  4. 根据实际负载调整PostgreSQL配置';
    RAISE NOTICE '';
    RAISE NOTICE '================================================================';
    RAISE NOTICE '              祝您使用愉快！🎉';
    RAISE NOTICE '================================================================';
    RAISE NOTICE '';
END $$;
