import { Outlet, useNavigate, useLocation } from 'react-router-dom';
import client from '../api/client';
import { LogOut, Activity, LayoutDashboard, FileVideo, ShieldAlert, Image as ImageIcon, History as HistoryIcon, Sparkles, Upload } from 'lucide-react';

export default function Layout() {
    const navigate = useNavigate();
    const location = useLocation();
    const user = JSON.parse(localStorage.getItem('user') || '{}');

    const logout = () => {
        client.post('/auth/logout').finally(() => {
            localStorage.removeItem('token');
            localStorage.removeItem('user');
            navigate('/login');
        });
    };

    const navItems = [
        { label: '首页', path: '/', icon: <LayoutDashboard size={20} /> },
        { label: '图片检测', path: '/image-detection', icon: <ImageIcon size={20} /> },
        { label: '视频检测', path: '/videos', icon: <FileVideo size={20} /> },
        { label: '历史记录', path: '/history', icon: <HistoryIcon size={20} /> },
        { label: '审计日志', path: '/audit', icon: <ShieldAlert size={20} /> },
    ];

    return (
        <div style={{ minHeight: '100vh', display: 'flex', flexDirection: 'column' }}>
            {/* Enhanced Header */}
            <header style={{
                background: 'rgba(10, 10, 15, 0.8)',
                backdropFilter: 'blur(20px)',
                WebkitBackdropFilter: 'blur(20px)',
                borderBottom: '1px solid var(--border-primary)',
                position: 'sticky',
                top: 0,
                zIndex: 1000,
                boxShadow: '0 4px 24px rgba(0, 0, 0, 0.3)'
            }}>
                <div style={{
                    maxWidth: '1400px',
                    margin: '0 auto',
                    padding: '0 24px',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'space-between',
                    height: '72px'
                }}>
                    {/* Brand */}
                    <div style={{
                        display: 'flex',
                        alignItems: 'center',
                        gap: '12px',
                        cursor: 'pointer',
                        transition: 'all 0.3s ease'
                    }} onClick={() => navigate('/')}>
                        <div style={{
                            width: '40px',
                            height: '40px',
                            background: 'linear-gradient(135deg, var(--accent-violet) 0%, var(--accent-blue) 100%)',
                            borderRadius: '12px',
                            display: 'flex',
                            alignItems: 'center',
                            justifyContent: 'center',
                            boxShadow: 'var(--shadow-glow)',
                            transition: 'all 0.3s ease'
                        }}>
                            <Sparkles size={24} color="white" />
                        </div>
                        <h2 className="text-gradient" style={{
                            fontSize: '1.25rem',
                            fontWeight: 700,
                            margin: 0
                        }}>
                            智鉴
                        </h2>
                    </div>

                    {/* Navigation */}
                    <nav style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                        {navItems.map((item) => (
                            <NavItem
                                key={item.path}
                                icon={item.icon}
                                label={item.label}
                                active={location.pathname === item.path}
                                onClick={() => navigate(item.path)}
                            />
                        ))}
                    </nav>

                    {/* User Actions */}
                    <div style={{ display: 'flex', alignItems: 'center', gap: '16px' }}>
                        <div style={{
                            padding: '8px 16px',
                            background: 'var(--dark-bg-tertiary)',
                            borderRadius: '12px',
                            border: '1px solid var(--border-primary)'
                        }}>
                            <div style={{ fontSize: '0.9rem', fontWeight: 600, color: 'var(--text-primary)' }}>
                                {user.username}
                            </div>
                            <div className="text-gradient" style={{ fontSize: '0.75rem', fontWeight: 600 }}>
                                {user.roles?.[0] || 'User'}
                            </div>
                        </div>
                        <button
                            onClick={logout}
                            style={{
                                background: 'rgba(239, 68, 68, 0.1)',
                                color: 'var(--error-red)',
                                border: '1px solid rgba(239, 68, 68, 0.3)',
                                padding: '10px',
                                borderRadius: '12px',
                                display: 'flex',
                                alignItems: 'center',
                                justifyContent: 'center',
                                cursor: 'pointer',
                                transition: 'all 0.3s ease'
                            }}
                            onMouseEnter={(e) => {
                                e.target.style.background = 'rgba(239, 68, 68, 0.2)';
                                e.target.style.transform = 'translateY(-2px)';
                            }}
                            onMouseLeave={(e) => {
                                e.target.style.background = 'rgba(239, 68, 68, 0.1)';
                                e.target.style.transform = 'translateY(0)';
                            }}
                            title="退出登录"
                        >
                            <LogOut size={20} />
                        </button>
                    </div>
                </div>
            </header>

            {/* Main Content */}
            <main style={{
                flex: 1,
                minHeight: 'calc(100vh - 72px - 250px)'
            }}>
                <Outlet />
            </main>

            {/* Footer */}
            <footer style={{
                background: 'var(--dark-bg-secondary)',
                borderTop: '1px solid var(--border-primary)',
                color: 'var(--text-primary)',
                marginTop: 'auto',
                position: 'relative',
                overflow: 'hidden'
            }}>
                <div style={{
                    position: 'absolute',
                    top: 0,
                    left: 0,
                    right: 0,
                    height: '1px',
                    background: 'linear-gradient(90deg, transparent, var(--accent-violet), transparent)'
                }}></div>

                <div style={{
                    maxWidth: '1400px',
                    margin: '0 auto',
                    padding: '0 24px'
                }}>
                    <div style={{
                        display: 'grid',
                        gridTemplateColumns: 'repeat(auto-fit, minmax(280px, 1fr))',
                        gap: '48px',
                        padding: '48px 0'
                    }}>
                        <div>
                            <h3 className="text-gradient" style={{ marginBottom: '20px', fontSize: '18px', fontWeight: 600 }}>
                                关于平台
                            </h3>
                            <p style={{ margin: 0, color: 'var(--text-secondary)', fontSize: '14px', lineHeight: '1.7' }}>
                                基于先进AI技术的多模态内容检测平台，为您提供专业、准确的图片和视频真实性检测服务。
                            </p>
                        </div>

                        <div>
                            <h3 className="text-gradient" style={{ marginBottom: '20px', fontSize: '18px', fontWeight: 600 }}>
                                功能特性
                            </h3>
                            <ul style={{ listStyle: 'none', padding: 0, margin: 0 }}>
                                {['支持多种图片和视频格式', '实时检测进度反馈', '详细的检测结果分析', '安全的报告分享功能'].map((item, i) => (
                                    <li key={i} style={{
                                        color: 'var(--text-secondary)',
                                        fontSize: '14px',
                                        marginBottom: '12px',
                                        paddingLeft: '20px',
                                        position: 'relative'
                                    }}>
                                        <span style={{
                                            position: 'absolute',
                                            left: 0,
                                            color: 'var(--accent-violet)'
                                        }}>▸</span>
                                        {item}
                                    </li>
                                ))}
                            </ul>
                        </div>

                        <div>
                            <h3 className="text-gradient" style={{ marginBottom: '20px', fontSize: '18px', fontWeight: 600 }}>
                                技术支持
                            </h3>
                            <ul style={{ listStyle: 'none', padding: 0, margin: 0 }}>
                                {['深度学习算法', '多模态分析技术', '实时处理引擎', '端到端加密传输'].map((item, i) => (
                                    <li key={i} style={{
                                        color: 'var(--text-secondary)',
                                        fontSize: '14px',
                                        marginBottom: '12px',
                                        paddingLeft: '20px',
                                        position: 'relative'
                                    }}>
                                        <span style={{
                                            position: 'absolute',
                                            left: 0,
                                            color: 'var(--accent-violet)'
                                        }}>▸</span>
                                        {item}
                                    </li>
                                ))}
                            </ul>
                        </div>
                    </div>

                    <div style={{
                        borderTop: '1px solid var(--border-primary)',
                        padding: '24px 0',
                        textAlign: 'center'
                    }}>
                        <p style={{ margin: 0, color: 'var(--text-dim)', fontSize: '14px' }}>
                            © 2025 智鉴——新一代视频生成内容安全检测平台. 保留所有权利.
                        </p>
                    </div>
                </div>
            </footer>
        </div>
    );
}

function NavItem({ icon, label, active, onClick }) {
    return (
        <div
            onClick={onClick}
            style={{
                display: 'flex',
                alignItems: 'center',
                gap: '8px',
                padding: '12px 20px',
                borderRadius: '12px',
                textDecoration: 'none',
                color: active ? 'white' : 'var(--text-secondary)',
                fontWeight: 500,
                transition: 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
                position: 'relative',
                border: '1px solid transparent',
                background: active ? 'linear-gradient(135deg, var(--accent-violet) 0%, var(--accent-blue) 100%)' : 'transparent',
                boxShadow: active ? 'var(--shadow-glow)' : 'none',
                cursor: 'pointer'
            }}
            onMouseEnter={(e) => {
                if (!active) {
                    e.currentTarget.style.background = 'rgba(124, 58, 237, 0.1)';
                    e.currentTarget.style.color = 'var(--accent-violet)';
                    e.currentTarget.style.borderColor = 'rgba(124, 58, 237, 0.2)';
                    e.currentTarget.style.transform = 'translateY(-2px)';
                }
            }}
            onMouseLeave={(e) => {
                if (!active) {
                    e.currentTarget.style.background = 'transparent';
                    e.currentTarget.style.color = 'var(--text-secondary)';
                    e.currentTarget.style.borderColor = 'transparent';
                    e.currentTarget.style.transform = 'translateY(0)';
                }
            }}
        >
            {icon}
            <span style={{ fontSize: '0.95rem' }}>{label}</span>
        </div>
    );
}
