import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import client from '../api/client';
import { Upload, FileVideo, CheckCircle, AlertTriangle, Clock, Activity, ArrowRight, Shield, HelpCircle } from 'lucide-react';

export default function Dashboard() {
    const [videos, setVideos] = useState([]);
    const [loading, setLoading] = useState(true);
    const [statistics, setStatistics] = useState({
        totalDetections: 0,
        fakeCount: 0,
        authenticCount: 0,
        uncertainCount: 0
    });
    const navigate = useNavigate();
    const user = JSON.parse(localStorage.getItem('user') || '{}');

    useEffect(() => {
        // Fetch recent videos
        client.get('/videos/my?page=0&size=5')
            .then(res => setVideos(res.data.content || []))
            .catch(console.error)
            .finally(() => setLoading(false));

        // Fetch statistics
        client.get('/detections/statistics')
            .then(res => setStatistics(res.data))
            .catch(console.error);
    }, []);

    // Stats data from API
    const stats = [
        { label: '总检测任务', value: statistics.totalDetections, icon: <FileVideo size={24} color="#60a5fa" />, bg: 'rgba(37, 99, 235, 0.1)', border: 'rgba(37, 99, 235, 0.2)' },
        { label: '发现伪造', value: statistics.fakeCount, icon: <AlertTriangle size={24} color="#ef4444" />, bg: 'rgba(239, 68, 68, 0.1)', border: 'rgba(239, 68, 68, 0.2)' },
        { label: '检测通过', value: statistics.authenticCount, icon: <CheckCircle size={24} color="#10b981" />, bg: 'rgba(16, 185, 129, 0.1)', border: 'rgba(16, 185, 129, 0.2)' },
        { label: '结果存疑', value: statistics.uncertainCount, icon: <HelpCircle size={24} color="#f59e0b" />, bg: 'rgba(245, 158, 11, 0.1)', border: 'rgba(245, 158, 11, 0.2)' },
    ];

    return (
        <div className="animate-fade-in">
            {/* Hero Section */}
            <div style={{
                marginBottom: '48px',
                position: 'relative',
                borderRadius: '24px',
                overflow: 'hidden',
                background: 'linear-gradient(135deg, rgba(124, 58, 237, 0.1) 0%, rgba(37, 99, 235, 0.1) 100%)',
                border: '1px solid rgba(255, 255, 255, 0.05)',
                padding: '48px'
            }}>
                <div style={{ position: 'relative', zIndex: 10 }}>
                    <div style={{
                        display: 'inline-flex',
                        alignItems: 'center',
                        gap: '8px',
                        padding: '6px 16px',
                        borderRadius: '20px',
                        background: 'rgba(124, 58, 237, 0.1)',
                        border: '1px solid rgba(124, 58, 237, 0.2)',
                        marginBottom: '24px'
                    }}>
                        <span style={{ width: '8px', height: '8px', borderRadius: '50%', background: '#7c3aed', boxShadow: '0 0 10px #7c3aed' }}></span>
                        <span style={{ color: '#a78bfa', fontSize: '0.9rem', fontWeight: 500 }}>AI 驱动的核心引擎 V2.0 已上线</span>
                    </div>
                    <h1 style={{ fontSize: '3rem', fontWeight: 800, marginBottom: '16px', lineHeight: '1.2' }}>
                        欢迎回来, <span className="text-gradient">{user.username}</span>
                    </h1>
                    <p style={{ fontSize: '1.1rem', color: 'var(--text-secondary)', maxWidth: '600px', marginBottom: '32px', lineHeight: '1.6' }}>
                        您的全方位多模态内容安全检测平台已准备就绪。立即开始新的检测任务，或查看最近的分析报告。
                    </p>
                    <div style={{ display: 'flex', gap: '16px' }}>
                        <button className="btn-primary hover-lift btn-glow" onClick={() => navigate('/videos')} style={{ padding: '14px 32px', fontSize: '1.05rem' }}>
                            <Upload size={20} style={{ marginRight: '8px' }} />
                            开始视频检测
                        </button>
                        <button className="hover-lift" style={{
                            padding: '14px 32px',
                            background: 'rgba(255, 255, 255, 0.05)',
                            border: '1px solid rgba(255, 255, 255, 0.1)',
                            borderRadius: '12px',
                            color: 'var(--text-primary)',
                            fontSize: '1.05rem',
                            cursor: 'pointer',
                            display: 'flex',
                            alignItems: 'center',
                            transition: 'all 0.2s'
                        }}
                            onClick={() => navigate('/image-detection')}
                            onMouseEnter={e => e.target.style.background = 'rgba(255, 255, 255, 0.1)'}
                            onMouseLeave={e => e.target.style.background = 'rgba(255, 255, 255, 0.05)'}
                        >
                            <Shield size={20} style={{ marginRight: '8px' }} />
                            图片检测
                        </button>
                    </div>
                </div>

                {/* Decorative Background Elements */}
                <div style={{ position: 'absolute', top: '-20%', right: '-10%', width: '600px', height: '600px', background: 'radial-gradient(circle, rgba(124, 58, 237, 0.15) 0%, transparent 70%)', filter: 'blur(60px)' }}></div>
                <div style={{ position: 'absolute', bottom: '-20%', left: '20%', width: '400px', height: '400px', background: 'radial-gradient(circle, rgba(37, 99, 235, 0.1) 0%, transparent 70%)', filter: 'blur(40px)' }}></div>
            </div>

            {/* Stats Grid */}
            <h3 style={{ fontSize: '1.25rem', marginBottom: '24px', display: 'flex', alignItems: 'center', gap: '10px' }}>
                <Activity size={20} color="var(--accent-blue)" />
                系统概览
            </h3>
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(240px, 1fr))', gap: '24px', marginBottom: '48px' }}>
                {stats.map((stat, i) => (
                    <div key={i} className="glass-card hover-lift" style={{
                        display: 'flex',
                        alignItems: 'center',
                        gap: '20px',
                        cursor: 'default'
                    }}
                    >
                        <div style={{
                            width: '56px',
                            height: '56px',
                            borderRadius: '16px',
                            background: stat.bg,
                            border: `1px solid ${stat.border}`,
                            display: 'flex',
                            alignItems: 'center',
                            justifyContent: 'center',
                            boxShadow: `0 4px 12px ${stat.bg}`
                        }}>
                            {stat.icon}
                        </div>
                        <div>
                            <div style={{ fontSize: '1.75rem', fontWeight: 800, color: 'var(--text-primary)', lineHeight: 1 }}>{stat.value}</div>
                            <div style={{ fontSize: '0.9rem', color: 'var(--text-secondary)', marginTop: '4px' }}>{stat.label}</div>
                        </div>
                    </div>
                ))}
            </div>

            {/* Recent Activity Section */}
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '24px' }}>
                <h3 style={{ fontSize: '1.25rem', display: 'flex', alignItems: 'center', gap: '10px' }}>
                    <Clock size={20} color="var(--accent-violet)" />
                    最近任务
                </h3>
                <button
                    onClick={() => navigate('/history')}
                    style={{
                        background: 'transparent',
                        border: 'none',
                        color: 'var(--accent-blue)',
                        cursor: 'pointer',
                        display: 'flex',
                        alignItems: 'center',
                        gap: '4px',
                        fontSize: '0.9rem',
                        fontWeight: 500
                    }}
                >
                    查看全部 <ArrowRight size={16} />
                </button>
            </div>

            <div className="glass-card" style={{ padding: '0', overflow: 'hidden' }}>
                <div style={{
                    display: 'grid',
                    gridTemplateColumns: '2fr 1.5fr 1fr 1fr 1fr',
                    padding: '20px 32px',
                    background: 'rgba(255,255,255,0.02)',
                    borderBottom: '1px solid rgba(255,255,255,0.05)',
                    color: 'var(--text-muted)',
                    fontSize: '0.85rem',
                    fontWeight: 600,
                    textTransform: 'uppercase',
                    letterSpacing: '0.5px'
                }}>
                    <div>文件名称</div>
                    <div>提交时间</div>
                    <div>文件大小</div>
                    <div>状态</div>
                    <div style={{ textAlign: 'right' }}>操作</div>
                </div>

                {loading ? (
                    <div style={{ padding: '60px', textAlign: 'center' }}>
                        <div className="loading-spinner" style={{ margin: '0 auto 16px' }}></div>
                        <p style={{ color: 'var(--text-muted)' }}>加载数据中...</p>
                    </div>
                ) : videos.length === 0 ? (
                    <div style={{ padding: '60px', display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
                        <div style={{
                            width: '64px',
                            height: '64px',
                            borderRadius: '50%',
                            background: 'rgba(255,255,255,0.03)',
                            display: 'flex',
                            alignItems: 'center',
                            justifyContent: 'center',
                            marginBottom: '16px'
                        }}>
                            <FileVideo size={32} color="var(--text-muted)" />
                        </div>
                        <p style={{ color: 'var(--text-muted)', marginBottom: '16px' }}>暂无检测记录</p>
                        <button className="btn-primary" onClick={() => navigate('/videos')}>
                            <Upload size={16} style={{ marginRight: '8px' }} />
                            上传第一个视频
                        </button>
                    </div>
                ) : (
                    videos.map(video => (
                        <div key={video.id} style={{
                            display: 'grid',
                            gridTemplateColumns: '2fr 1.5fr 1fr 1fr 1fr',
                            padding: '20px 32px',
                            borderBottom: '1px solid rgba(255,255,255,0.02)',
                            alignItems: 'center',
                            fontSize: '0.95rem',
                            transition: 'background 0.2s'
                        }}
                            onMouseEnter={e => e.currentTarget.style.background = 'rgba(255,255,255,0.02)'}
                            onMouseLeave={e => e.currentTarget.style.background = 'transparent'}
                        >
                            <div style={{ display: 'flex', alignItems: 'center', gap: '12px', minWidth: 0 }}>
                                <div style={{
                                    width: '40px',
                                    height: '40px',
                                    borderRadius: '10px',
                                    background: 'rgba(37, 99, 235, 0.1)',
                                    display: 'flex',
                                    alignItems: 'center',
                                    justifyContent: 'center',
                                    color: 'var(--accent-blue)'
                                }}>
                                    <FileVideo size={20} />
                                </div>
                                <span style={{
                                    whiteSpace: 'nowrap',
                                    overflow: 'hidden',
                                    textOverflow: 'ellipsis',
                                    fontWeight: 500
                                }}>
                                    {video.fileName}
                                </span>
                            </div>
                            <div style={{ color: 'var(--text-muted)' }}>
                                {new Date(video.createdAt).toLocaleString()}
                            </div>
                            <div style={{ color: 'var(--text-muted)' }}>
                                {(video.fileSize / 1024 / 1024).toFixed(2)} MB
                            </div>
                            <div>
                                <span style={{
                                    display: 'inline-flex',
                                    alignItems: 'center',
                                    gap: '6px',
                                    padding: '6px 12px',
                                    borderRadius: '6px',
                                    background: 'rgba(16, 185, 129, 0.1)',
                                    color: 'var(--success)',
                                    fontSize: '0.8rem',
                                    fontWeight: 600,
                                    border: '1px solid rgba(16, 185, 129, 0.2)'
                                }}>
                                    <CheckCircle size={12} />
                                    已完成
                                </span>
                            </div>
                            <div style={{ textAlign: 'right' }}>
                                <button style={{
                                    background: 'transparent',
                                    border: '1px solid rgba(255,255,255,0.1)',
                                    color: 'var(--text-secondary)',
                                    padding: '6px 14px',
                                    borderRadius: '8px',
                                    fontSize: '0.85rem',
                                    cursor: 'pointer',
                                    transition: 'all 0.2s'
                                }}
                                    onMouseEnter={e => {
                                        e.target.style.borderColor = 'var(--accent-blue)';
                                        e.target.style.color = 'var(--accent-blue)';
                                    }}
                                    onMouseLeave={e => {
                                        e.target.style.borderColor = 'rgba(255,255,255,0.1)';
                                        e.target.style.color = 'var(--text-secondary)';
                                    }}
                                    onClick={() => navigate('/history')}
                                >
                                    查看详情
                                </button>
                            </div>
                        </div>
                    ))
                )}
            </div>
        </div>
    );
}
