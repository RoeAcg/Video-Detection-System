import { useState, useRef } from 'react';
import client from '../api/client';
import { Upload, Image as ImageIcon, Search, AlertCircle, CheckCircle, X, Sparkles } from 'lucide-react';

export default function ImageDetection() {
    const [selectedFile, setSelectedFile] = useState(null);
    const [preview, setPreview] = useState(null);
    const [analyzing, setAnalyzing] = useState(false);
    const [uploadProgress, setUploadProgress] = useState(0);
    const [result, setResult] = useState(null);
    const [detectionMode, setDetectionMode] = useState('standard');
    const fileInputRef = useRef(null);

    const handleFileSelect = (e) => {
        const file = e.target.files[0];
        if (file) {
            if (!file.type.startsWith('image/')) {
                alert('请上传图片文件');
                return;
            }
            setSelectedFile(file);
            const reader = new FileReader();
            reader.onloadend = () => {
                setPreview(reader.result);
                setResult(null);
            };
            reader.readAsDataURL(file);
        }
    };

    const handleDetection = async () => {
        if (!selectedFile) return;

        const formData = new FormData();
        formData.append('file', selectedFile);
        formData.append('description', selectedFile.name);
        formData.append('mode', detectionMode);

        try {
            setAnalyzing(true);
            setUploadProgress(0);

            const response = await client.post('/videos/upload', formData, {
                headers: { 'Content-Type': 'multipart/form-data' },
                onUploadProgress: (progressEvent) => {
                    const total = progressEvent.total || progressEvent.loaded || 1;
                    const percentCompleted = Math.round((progressEvent.loaded * 100) / total);
                    setUploadProgress(percentCompleted);
                }
            });

            // Show success message
            setResult({
                success: true,
                taskId: response.data.taskId,
                message: '图片已上传，检测任务已创建'
            });

        } catch (err) {
            console.error("Detection failed:", err);
            setResult({
                success: false,
                message: '检测失败: ' + (err.response?.data?.message || err.message)
            });
        } finally {
            setAnalyzing(false);
            setUploadProgress(0);
        }
    };

    const resetForm = () => {
        setSelectedFile(null);
        setPreview(null);
        setResult(null);
        setDetectionMode('standard');
        if (fileInputRef.current) fileInputRef.current.value = '';
    };

    return (
        <div className="animate-fade-in" style={{ maxWidth: '1400px', margin: '0 auto', padding: '0 20px' }}>
            <div style={{ marginBottom: '40px' }}>
                <h2 style={{ fontSize: '2.25rem', marginBottom: '12px', fontWeight: 800 }}>
                    <span className="text-gradient">图片检测</span>
                </h2>
                <p style={{ color: 'var(--text-secondary)', fontSize: '1.05rem' }}>上传图片进行深度伪造（Deepfake）或 AIGC 生成内容检测</p>
            </div>

            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '32px' }}>

                {/* Upload Area */}
                <div className="glass-card" style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', minHeight: '500px', border: '2px dashed rgba(255,255,255,0.1)' }}>
                    {preview ? (
                        <div style={{ width: '100%', height: '100%', position: 'relative', display: 'flex', alignItems: 'center', justifyContent: 'center', padding: '20px' }}>
                            <img src={preview} alt="Preview" style={{ maxWidth: '100%', maxHeight: '450px', borderRadius: '8px' }} />
                            <button
                                onClick={resetForm}
                                style={{
                                    position: 'absolute',
                                    top: '20px',
                                    right: '20px',
                                    background: 'rgba(0,0,0,0.6)',
                                    color: '#fff',
                                    border: 'none',
                                    borderRadius: '50%',
                                    width: '36px',
                                    height: '36px',
                                    cursor: 'pointer',
                                    display: 'flex',
                                    alignItems: 'center',
                                    justifyContent: 'center'
                                }}
                            >
                                <X size={20} />
                            </button>
                        </div>
                    ) : (
                        <>
                            <div style={{ width: '80px', height: '80px', borderRadius: '50%', background: 'rgba(6, 182, 212, 0.1)', display: 'flex', alignItems: 'center', justifyContent: 'center', marginBottom: '24px' }}>
                                <ImageIcon size={40} color="var(--accent-violet)" />
                            </div>
                            <h3 style={{ marginBottom: '12px' }}>点击或拖拽上传图片</h3>
                            <p style={{ color: 'var(--text-muted)', marginBottom: '24px', fontSize: '0.9rem' }}>支持 JPG, PNG, BMP, GIF 格式 (最大 10MB)</p>

                            <label className="btn-primary" style={{ cursor: 'pointer', display: 'flex', alignItems: 'center', gap: '8px' }}>
                                <Upload size={18} />
                                <span>选择图片</span>
                                <input type="file" hidden accept="image/*" ref={fileInputRef} onChange={handleFileSelect} />
                            </label>
                        </>
                    )}
                </div>

                {/* Analysis Area */}
                <div style={{ display: 'flex', flexDirection: 'column', gap: '24px' }}>

                    {/* Detection Mode Selector */}
                    <div className="glass-card">
                        <h3 style={{ marginBottom: '20px', fontSize: '1.1rem', fontWeight: 700 }}>检测模式</h3>
                        <div style={{ display: 'flex', gap: '12px' }}>
                            <button
                                type="button"
                                onClick={() => setDetectionMode('standard')}
                                disabled={analyzing}
                                style={{
                                    flex: 1,
                                    padding: '16px 20px',
                                    borderRadius: '12px',
                                    border: detectionMode === 'standard' ? 'none' : '1px solid rgba(255,255,255,0.1)',
                                    background: detectionMode === 'standard'
                                        ? 'linear-gradient(135deg, var(--accent-violet) 0%, var(--accent-blue) 100%)'
                                        : 'rgba(255,255,255,0.02)',
                                    color: detectionMode === 'standard' ? 'white' : 'var(--text-secondary)',
                                    cursor: analyzing ? 'not-allowed' : 'pointer',
                                    transition: 'all 0.3s',
                                    fontWeight: detectionMode === 'standard' ? 700 : 500,
                                    opacity: analyzing ? 0.5 : 1,
                                    boxShadow: detectionMode === 'standard' ? 'var(--shadow-glow)' : 'none'
                                }}
                            >
                                <div style={{ fontSize: '1.05rem', marginBottom: '6px' }}>Standard</div>
                                <div style={{ fontSize: '0.8rem', opacity: 0.9 }}>人脸伪造检测</div>
                            </button>
                            <button
                                type="button"
                                onClick={() => setDetectionMode('aigc')}
                                disabled={analyzing}
                                style={{
                                    flex: 1,
                                    padding: '16px 20px',
                                    borderRadius: '12px',
                                    border: detectionMode === 'aigc' ? 'none' : '1px solid rgba(255,255,255,0.1)',
                                    background: detectionMode === 'aigc'
                                        ? 'linear-gradient(135deg, var(--accent-violet) 0%, var(--accent-blue) 100%)'
                                        : 'rgba(255,255,255,0.02)',
                                    color: detectionMode === 'aigc' ? 'white' : 'var(--text-secondary)',
                                    cursor: analyzing ? 'not-allowed' : 'pointer',
                                    transition: 'all 0.3s',
                                    fontWeight: detectionMode === 'aigc' ? 700 : 500,
                                    opacity: analyzing ? 0.5 : 1,
                                    boxShadow: detectionMode === 'aigc' ? 'var(--shadow-glow)' : 'none'
                                }}
                            >
                                <div style={{ fontSize: '1.05rem', marginBottom: '6px' }}>AIGC</div>
                                <div style={{ fontSize: '0.8rem', opacity: 0.9 }}>AI 生成检测</div>
                            </button>
                        </div>
                        <div style={{
                            marginTop: '16px',
                            padding: '12px 16px',
                            background: 'rgba(124, 58, 237, 0.05)',
                            border: '1px solid rgba(124, 58, 237, 0.1)',
                            borderRadius: '8px'
                        }}>
                            <p style={{ margin: 0, fontSize: '0.9rem', color: 'var(--text-secondary)', lineHeight: '1.5' }}>
                                <Sparkles size={14} style={{ verticalAlign: 'middle', marginRight: '6px', color: 'var(--accent-violet)' }} />
                                {detectionMode === 'standard'
                                    ? '适用于包含人脸的图片，检测 Deepfake 换脸或表情操纵'
                                    : '适用于任何 AI 生成的图片（如 Midjourney, Stable Diffusion）'}
                            </p>
                        </div>
                    </div>

                    {/* Action Card */}
                    <div className="glass-card" style={{ textAlign: 'center', padding: '32px' }}>
                        <h3 style={{ marginBottom: '16px' }}>分析控制台</h3>

                        {analyzing && (
                            <div style={{ marginBottom: '16px' }}>
                                <div style={{ fontSize: '1.5rem', fontWeight: 700, color: 'var(--accent-violet)', marginBottom: '8px' }}>{uploadProgress}%</div>
                                <div style={{ width: '100%', height: '6px', background: 'rgba(255,255,255,0.1)', borderRadius: '3px', overflow: 'hidden' }}>
                                    <div style={{ width: `${uploadProgress}%`, height: '100%', background: 'var(--accent-violet)', transition: 'width 0.2s' }}></div>
                                </div>
                            </div>
                        )}

                        <button
                            className="btn-primary"
                            style={{ width: '100%', padding: '16px', fontSize: '1.1rem', justifyContent: 'center' }}
                            disabled={!selectedFile || analyzing}
                            onClick={handleDetection}
                        >
                            {analyzing ? (
                                <>
                                    <div className="spinner" style={{ width: '20px', height: '20px', border: '2px solid rgba(255,255,255,0.3)', borderTopColor: '#fff', borderRadius: '50%', animation: 'spin 1s linear infinite', marginRight: '10px' }}></div>
                                    正在上传并分析...
                                </>
                            ) : (
                                <>
                                    <Search size={20} style={{ marginRight: '8px' }} />
                                    开始检测
                                </>
                            )}
                        </button>
                        {!selectedFile && <p style={{ marginTop: '16px', color: 'var(--text-muted)', fontSize: '0.9rem' }}>请先上传图片以激活检测功能</p>}
                    </div>

                    {/* Result Card */}
                    {result && (
                        <div className="glass-card animate-fade-in" style={{
                            borderLeft: `4px solid ${result.success ? 'var(--success)' : 'var(--danger)'}`,
                            background: result.success ? 'rgba(16, 185, 129, 0.05)' : 'rgba(239, 68, 68, 0.05)'
                        }}>
                            <div style={{ display: 'flex', alignItems: 'center', gap: '16px', marginBottom: '16px' }}>
                                {result.success ? <CheckCircle size={32} color="var(--success)" /> : <AlertCircle size={32} color="var(--danger)" />}
                                <div>
                                    <h3 style={{ color: result.success ? 'var(--success)' : 'var(--danger)' }}>
                                        {result.success ? '任务创建成功' : '检测失败'}
                                    </h3>
                                    <div style={{ color: 'var(--text-muted)', fontSize: '0.9rem' }}>{result.message}</div>
                                </div>
                            </div>

                            {result.success && result.taskId && (
                                <div style={{ background: 'rgba(0,0,0,0.2)', padding: '16px', borderRadius: '8px' }}>
                                    <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '8px' }}>
                                        <span style={{ color: 'var(--text-muted)' }}>任务 ID</span>
                                        <span style={{ fontWeight: 600, fontSize: '0.85rem', fontFamily: 'monospace' }}>{result.taskId}</span>
                                    </div>
                                    <div style={{ marginTop: '12px', padding: '12px', background: 'rgba(6, 182, 212, 0.1)', borderRadius: '6px', fontSize: '0.9rem' }}>
                                        <p style={{ margin: 0 }}>💡 检测任务已提交，请稍后在"历史记录"页面查看结果</p>
                                    </div>
                                </div>
                            )}
                        </div>
                    )}
                </div>
            </div>
            <style>{`
        @keyframes spin {
          to { transform: rotate(360deg); }
        }
      `}</style>
        </div>
    );
}
