import { useState, useRef } from 'react';
import client from '../api/client';
import { Upload, FileVideo, X, Sparkles } from 'lucide-react';

export default function VideoDetection() {
    const [uploading, setUploading] = useState(false);
    const [uploadProgress, setUploadProgress] = useState(0);
    const [showUploadModal, setShowUploadModal] = useState(false);
    const fileInputRef = useRef(null);

    // Handle Upload - Video only with standard mode
    const handleFileSelect = async (e) => {
        const file = e.target.files[0];
        if (!file) return;

        // Video-only validation
        if (!file.type.startsWith('video/')) {
            alert('请选择视频文件');
            return;
        }

        const formData = new FormData();
        formData.append('file', file);
        formData.append('description', file.name);
        formData.append('mode', 'standard'); // Videos use standard mode

        try {
            setUploading(true);
            setUploadProgress(0);
            await client.post('/videos/upload', formData, {
                headers: { 'Content-Type': 'multipart/form-data' },
                onUploadProgress: (progressEvent) => {
                    const total = progressEvent.total || progressEvent.loaded || 1;
                    const percentCompleted = Math.round((progressEvent.loaded * 100) / total);
                    setUploadProgress(percentCompleted);
                }
            });
            alert('上传成功，检测任务已开始！请前往"历史记录"查看结果');
            setShowUploadModal(false);
        } catch (err) {
            console.error("Upload failed:", err);
            alert('上传失败: ' + (err.response?.data?.message || err.message));
        } finally {
            setUploading(false);
            setUploadProgress(0);
            if (fileInputRef.current) fileInputRef.current.value = '';
        }
    };

    return (
        <div className="animate-fade-in" style={{ maxWidth: '1200px', margin: '0 auto', padding: '0 20px' }}>
            <div style={{ marginBottom: '40px' }}>
                <h2 style={{ fontSize: '2.25rem', marginBottom: '12px', fontWeight: 800 }}>
                    <span className="text-gradient">视频检测</span>
                </h2>
                <p style={{ color: 'var(--text-secondary)', fontSize: '1.05rem' }}>上传视频进行人脸伪造检测（Deepfake）</p>
            </div>

            {/* Upload Card */}
            <div className="glass-card" style={{
                minHeight: '500px',
                display: 'flex',
                flexDirection: 'column',
                alignItems: 'center',
                justifyContent: 'center',
                border: '2px dashed rgba(124, 58, 237, 0.2)',
                position: 'relative',
                overflow: 'hidden'
            }}>
                {/* Background decoration */}
                <div style={{
                    position: 'absolute',
                    top: '-50%',
                    right: '-20%',
                    width: '400px',
                    height: '400px',
                    background: 'radial-gradient(circle, rgba(124, 58, 237, 0.1) 0%, transparent 70%)',
                    filter: 'blur(60px)',
                    pointerEvents: 'none'
                }}></div>

                <div style={{ position: 'relative', zIndex: 10, textAlign: 'center' }}>
                    <div style={{
                        width: '140px',
                        height: '140px',
                        borderRadius: '50%',
                        background: 'linear-gradient(135deg, rgba(124, 58, 237, 0.1) 0%, rgba(37, 99, 235, 0.1) 100%)',
                        border: '2px solid rgba(124, 58, 237, 0.2)',
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                        marginBottom: '32px',
                        margin: '0 auto 32px',
                        boxShadow: '0 8px 32px rgba(124, 58, 237, 0.15)'
                    }}>
                        <FileVideo size={70} style={{ color: 'var(--accent-violet)' }} />
                    </div>

                    <h3 style={{ marginBottom: '16px', fontSize: '1.75rem', fontWeight: 700 }}>上传视频进行检测</h3>
                    <p style={{ color: 'var(--text-secondary)', marginBottom: '8px', fontSize: '1.05rem' }}>
                        支持 MP4, AVI, MKV, WEBM 格式
                    </p>
                    <p style={{ color: 'var(--text-dim)', marginBottom: '40px', fontSize: '0.95rem' }}>
                        最大文件大小: 500MB
                    </p>

                    <button
                        className="btn-primary"
                        style={{
                            display: 'flex',
                            alignItems: 'center',
                            gap: '8px',
                            padding: '14px 32px',
                            fontSize: '1.1rem'
                        }}
                        onClick={() => setShowUploadModal(true)}
                    >
                        <Upload size={20} />
                        <span>选择视频文件</span>
                    </button>

                    <div style={{
                        marginTop: '32px',
                        padding: '20px 28px',
                        background: 'rgba(124, 58, 237, 0.05)',
                        borderRadius: '12px',
                        border: '1px solid rgba(124, 58, 237, 0.15)',
                        maxWidth: '650px'
                    }}>
                        <p style={{ margin: 0, fontSize: '0.95rem', lineHeight: '1.7', color: 'var(--text-secondary)' }}>
                            <Sparkles size={16} style={{ verticalAlign: 'middle', marginRight: '8px', color: 'var(--accent-violet)' }} />
                            <strong style={{ color: 'var(--text-primary)' }}>提示：</strong>本功能使用 Standard 模式进行人脸伪造检测，
                            适用于包含人脸的视频。检测完成后，请前往"历史记录"页面查看结果。
                        </p>
                    </div>
                </div>
            </div>

            {/* Upload Modal */}
            {showUploadModal && (
                <div style={{
                    position: 'fixed', top: 0, left: 0, right: 0, bottom: 0,
                    background: 'rgba(0,0,0,0.7)',
                    backdropFilter: 'blur(5px)',
                    display: 'flex', alignItems: 'center', justifyContent: 'center',
                    zIndex: 100
                }}>
                    <div className="glass-card" style={{ width: '500px', maxWidth: '90%', position: 'relative' }}>
                        <button
                            onClick={() => setShowUploadModal(false)}
                            style={{ position: 'absolute', top: '16px', right: '16px', background: 'transparent', border: 'none', color: '#fff', cursor: 'pointer' }}
                        >
                            <X size={24} />
                        </button>

                        <h3 style={{ marginBottom: '24px' }}>上传视频</h3>

                        {uploading ? (
                            <div style={{ textAlign: 'center', padding: '40px 0' }}>
                                <div style={{ fontSize: '2rem', fontWeight: 700, color: 'var(--accent-violet)', marginBottom: '16px' }}>{uploadProgress}%</div>
                                <div style={{ width: '100%', height: '8px', background: 'rgba(255,255,255,0.1)', borderRadius: '4px', overflow: 'hidden' }}>
                                    <div style={{ width: `${uploadProgress}%`, height: '100%', background: 'var(--accent-violet)', transition: 'width 0.2s' }}></div>
                                </div>
                                <p style={{ marginTop: '16px', color: 'var(--text-muted)' }}>正在上传并创建检测任务...</p>
                            </div>
                        ) : (
                            <div
                                style={{
                                    border: '2px dashed rgba(255,255,255,0.2)',
                                    borderRadius: '12px',
                                    padding: '40px',
                                    textAlign: 'center',
                                    cursor: 'pointer',
                                    transition: 'border-color 0.2s'
                                }}
                                onClick={() => fileInputRef.current?.click()}
                                onDragOver={(e) => { e.preventDefault(); e.currentTarget.style.borderColor = 'var(--accent-violet)'; }}
                                onDragLeave={(e) => { e.preventDefault(); e.currentTarget.style.borderColor = 'rgba(255,255,255,0.2)'; }}
                                onDrop={(e) => {
                                    e.preventDefault();
                                    e.currentTarget.style.borderColor = 'rgba(255,255,255,0.2)';
                                    const file = e.dataTransfer.files[0];
                                    if (file) {
                                        fileInputRef.current.files = e.dataTransfer.files;
                                        handleFileSelect({ target: { files: [file] } });
                                    }
                                }}
                            >
                                <div style={{ width: '64px', height: '64px', borderRadius: '50%', background: 'rgba(6, 182, 212, 0.1)', display: 'flex', alignItems: 'center', justifyContent: 'center', margin: '0 auto 16px' }}>
                                    <Upload size={32} color="var(--accent-violet)" />
                                </div>
                                <p style={{ fontSize: '1.1rem', marginBottom: '8px' }}>点击或拖拽上传视频</p>
                                <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem' }}>支持 MP4, AVI, MKV, WEBM 格式</p>
                                <p style={{ color: 'var(--text-muted)', fontSize: '0.85rem', marginTop: '4px' }}>最大 500MB</p>
                                <input
                                    type="file"
                                    hidden
                                    accept="video/*"
                                    ref={fileInputRef}
                                    onChange={handleFileSelect}
                                />
                            </div>
                        )}
                    </div>
                </div>
            )}
        </div>
    );
}
