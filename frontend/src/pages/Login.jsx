import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import client from '../api/client';
import { Lock, User, ArrowRight, Sparkles } from 'lucide-react';

export default function Login() {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const navigate = useNavigate();
    const [loading, setLoading] = useState(false);

    const handleLogin = async (e) => {
        e.preventDefault();
        setLoading(true);
        try {
            const res = await client.post('/auth/login', { username, password });
            localStorage.setItem('token', res.data.token);
            localStorage.setItem('user', JSON.stringify(res.data));
            navigate('/');
        } catch (err) {
            alert('登录失败: ' + (err.response?.data?.message || '请检查用户名或密码'));
        } finally {
            setLoading(false);
        }
    };

    return (
        <div style={{
            minHeight: '100vh',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            position: 'relative',
            overflow: 'hidden',
            background: 'var(--dark-gradient-primary)'
        }}>
            {/* Enhanced Background Effects */}
            <div style={{
                position: 'absolute',
                top: '-20%',
                left: '-10%',
                width: '600px',
                height: '600px',
                background: 'radial-gradient(circle, rgba(124, 58, 237, 0.15) 0%, transparent 70%)',
                filter: 'blur(60px)',
                animation: 'float 8s ease-in-out infinite'
            }}></div>
            <div style={{
                position: 'absolute',
                bottom: '-20%',
                right: '-10%',
                width: '600px',
                height: '600px',
                background: 'radial-gradient(circle, rgba(37, 99, 235, 0.15) 0%, transparent 70%)',
                filter: 'blur(60px)',
                animation: 'float 10s ease-in-out infinite reverse'
            }}></div>

            <div className="glass-card animate-fade-in" style={{
                width: '480px',
                maxWidth: '90%',
                padding: '48px',
                zIndex: 10,
                position: 'relative'
            }}>
                {/* Logo/Icon */}
                <div style={{
                    display: 'flex',
                    justifyContent: 'center',
                    marginBottom: '32px'
                }}>
                    <div style={{
                        width: '80px',
                        height: '80px',
                        background: 'linear-gradient(135deg, var(--accent-violet) 0%, var(--accent-blue) 100%)',
                        borderRadius: '20px',
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                        boxShadow: 'var(--shadow-glow)',
                        animation: 'pulse-glow 2s ease-in-out infinite'
                    }}>
                        <Sparkles size={40} color="white" />
                    </div>
                </div>

                <div style={{ textAlign: 'center', marginBottom: '40px' }}>
                    <h1 style={{
                        fontSize: '2.5rem',
                        marginBottom: '12px',
                        fontWeight: 800
                    }}>
                        智鉴系统
                    </h1>
                    <p style={{
                        color: 'var(--text-secondary)',
                        fontSize: '1.1rem',
                        lineHeight: '1.6'
                    }}>
                        AI驱动的视频内容安全检测平台
                    </p>
                </div>

                <form onSubmit={handleLogin} style={{
                    display: 'flex',
                    flexDirection: 'column',
                    gap: '24px'
                }}>
                    <div style={{ position: 'relative' }}>
                        <User size={20} style={{
                            position: 'absolute',
                            left: '18px',
                            top: '17px',
                            color: 'var(--text-dim)',
                            zIndex: 1
                        }} />
                        <input
                            className="input-field"
                            style={{ paddingLeft: '50px' }}
                            placeholder="用户名"
                            value={username}
                            onChange={e => setUsername(e.target.value)}
                            required
                        />
                    </div>

                    <div style={{ position: 'relative' }}>
                        <Lock size={20} style={{
                            position: 'absolute',
                            left: '18px',
                            top: '17px',
                            color: 'var(--text-dim)',
                            zIndex: 1
                        }} />
                        <input
                            className="input-field"
                            style={{ paddingLeft: '50px' }}
                            type="password"
                            placeholder="密码"
                            value={password}
                            onChange={e => setPassword(e.target.value)}
                            required
                        />
                    </div>

                    <button
                        className="btn-primary"
                        type="submit"
                        disabled={loading}
                        style={{
                            marginTop: '8px',
                            padding: '16px 32px',
                            fontSize: '1.1rem',
                            justifyContent: 'center'
                        }}
                    >
                        {loading ? (
                            <>
                                <span className="loading-spinner" style={{
                                    width: '18px',
                                    height: '18px',
                                    borderWidth: '2px'
                                }}></span>
                                登录中...
                            </>
                        ) : (
                            <>
                                进入系统
                                <ArrowRight size={20} />
                            </>
                        )}
                    </button>
                </form>

                <div style={{
                    marginTop: '32px',
                    textAlign: 'center',
                    fontSize: '1rem',
                    paddingTop: '24px',
                    borderTop: '1px solid var(--border-primary)'
                }}>
                    <span style={{ color: 'var(--text-secondary)' }}>还没有账号? </span>
                    <span
                        className="text-gradient"
                        style={{
                            cursor: 'pointer',
                            fontWeight: 600,
                            transition: 'opacity 0.3s ease'
                        }}
                        onClick={() => navigate('/register')}
                        onMouseEnter={(e) => e.target.style.opacity = '0.8'}
                        onMouseLeave={(e) => e.target.style.opacity = '1'}
                    >
                        立即注册
                    </span>
                </div>
            </div>
        </div>
    );
}
