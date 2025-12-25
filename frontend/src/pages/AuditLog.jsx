import { useEffect, useState } from 'react';
import client from '../api/client';
import { Shield, Clock, MousePointer, Monitor, AlertOctagon, User } from 'lucide-react';

export default function AuditLog() {
    const [logs, setLogs] = useState([]);
    const [loading, setLoading] = useState(true);
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [userInfo, setUserInfo] = useState({});

    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchLogs = async () => {
            try {
                const userStr = localStorage.getItem('user');
                if (!userStr) return;

                const user = JSON.parse(userStr);
                setUserInfo(user);
                setError(null);

                // Backend requires ADMIN for /logs/user/{id}.
                // If this fails with 403, it means normal users can't see audit logs (as per backend code).
                // We will try to fetch and handle errors gracefully.

                let url = `/audit/logs/user/${user.userId}?page=${page}&size=10`;
                // If Admin, view ALL logs
                if (user.roles && user.roles.includes('ROLE_ADMIN')) {
                    url = `/audit/logs?page=${page}&size=10`;
                }

                const res = await client.get(url);
                setLogs(res.data.content || []);
                setTotalPages(res.data.totalPages || 0);
            } catch (err) {
                console.error("Failed to fetch audit logs:", err);
                if (err.response && err.response.status === 403) {
                    setError('403');
                } else {
                    setError('error');
                }
                setLogs([]); // Access denied or other error
            } finally {
                setLoading(false);
            }
        };

        fetchLogs();
    }, [page]);

    const getActionColor = (action) => {
        if (action.includes('LOGIN')) return '#10b981'; // Success
        if (action.includes('FAILED')) return '#ef4444'; // Error
        if (action.includes('UPLOAD')) return '#3b82f6'; // Blue
        if (action.includes('DELETE')) return '#ef4444'; // Red
        return '#f59e0b'; // Default Orange
    };

    const formatAction = (action) => {
        const map = {
            'USER_REGISTER': 'User Register',
            'USER_LOGIN': 'User Login',
            'USER_LOGOUT': 'User Logout',
            'UPLOAD_VIDEO': 'Upload Video',
            'UPLOAD_COMPLETE': 'Upload Completed',
            'DELETE_VIDEO': 'Delete Video',
            'USER_LOGIN_FAILED': 'Login Failed'
        };
        // Fallback: Replace underscores with spaces and Title Case
        return map[action] || action.replace(/_/g, ' ').replace(/\b\w/g, c => c.toUpperCase());
    };

    return (
        <div className="animate-fade-in" style={{ maxWidth: '1400px', margin: '0 auto', padding: '0 20px' }}>
            <div style={{ marginBottom: '40px' }}>
                <h2 style={{ fontSize: '2.25rem', marginBottom: '12px', fontWeight: 800 }}>
                    <span className="text-gradient">审计日志</span>
                </h2>
                <p style={{ color: 'var(--text-secondary)', fontSize: '1.05rem' }}>查看您的系统操作历史记录</p>
            </div>

            <div className="glass-card" style={{ overflow: 'hidden', padding: 0 }}>
                {/* Table Header */}
                <div style={{
                    display: 'grid',
                    gridTemplateColumns: userInfo.roles?.includes('ROLE_ADMIN') ? '2fr 1fr 1fr 1fr 2fr 1fr' : '2fr 1fr 1fr 2fr 1fr',
                    padding: '16px 24px',
                    background: 'rgba(255,255,255,0.02)',
                    borderBottom: '1px solid rgba(255,255,255,0.05)',
                    color: 'var(--text-muted)',
                    fontSize: '0.9rem',
                    fontWeight: 600
                }}>
                    <div><Clock size={14} style={{ marginRight: '6px', verticalAlign: 'middle' }} /> 时间</div>
                    {userInfo.roles?.includes('ROLE_ADMIN') && (
                        <div><User size={14} style={{ marginRight: '6px', verticalAlign: 'middle' }} /> 用户ID</div>
                    )}
                    <div><Shield size={14} style={{ marginRight: '6px', verticalAlign: 'middle' }} /> 动作</div>
                    <div><Monitor size={14} style={{ marginRight: '6px', verticalAlign: 'middle' }} /> IP地址</div>
                    <div><MousePointer size={14} style={{ marginRight: '6px', verticalAlign: 'middle' }} /> 详情</div>
                    <div>状态</div>
                </div>

                {/* Table Body */}
                {loading ? (
                    <div style={{ padding: '60px', textAlign: 'center', color: 'var(--text-muted)' }}>
                        加载记录中...
                    </div>
                ) : logs.length === 0 ? (
                    <div style={{ padding: '60px', textAlign: 'center', color: 'var(--text-muted)', display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '16px' }}>
                        <AlertOctagon size={40} color={error ? "var(--danger)" : "var(--text-muted)"} />
                        <p>{error === '403' ? '无权限查看 (Access Denied)' : error ? '获取数据失败' : '暂无审计记录'}</p>
                    </div>
                ) : (
                    logs.map((log) => (
                        <div key={log.id} style={{
                            display: 'grid',
                            gridTemplateColumns: userInfo.roles?.includes('ROLE_ADMIN') ? '2fr 1fr 1fr 1fr 2fr 1fr' : '2fr 1fr 1fr 2fr 1fr',
                            padding: '16px 24px',
                            borderBottom: '1px solid rgba(255,255,255,0.02)',
                            fontSize: '0.9rem',
                            alignItems: 'center',
                            transition: 'background 0.2s',
                        }}
                            className="table-row-hover"
                        >
                            <div style={{ color: 'var(--text-muted)' }}>
                                {new Date(log.createdAt).toLocaleString()}
                            </div>
                            {userInfo.roles?.includes('ROLE_ADMIN') && (
                                <div style={{ fontWeight: 500, color: 'var(--accent-violet)' }}>
                                    {log.userId}
                                </div>
                            )}
                            <div style={{ fontWeight: 500, color: 'var(--text-main)' }}>
                                {formatAction(log.action)}
                            </div>
                            <div style={{ fontFamily: 'monospace', color: 'var(--text-muted)' }}>
                                {log.ipAddress || 'Unknown'}
                            </div>
                            <div style={{
                                whiteSpace: 'nowrap',
                                overflow: 'hidden',
                                textOverflow: 'ellipsis',
                                color: 'var(--text-muted)'
                            }} title={log.details || log.newValue}>
                                {log.details || log.newValue || '-'}
                            </div>
                            <div>
                                <span style={{
                                    display: 'inline-block',
                                    padding: '4px 8px',
                                    borderRadius: '4px',
                                    fontSize: '0.75rem',
                                    background: `rgba(${parseInt(getActionColor(log.action).slice(1, 3), 16)}, ${parseInt(getActionColor(log.action).slice(3, 5), 16)}, ${parseInt(getActionColor(log.action).slice(5, 7), 16)}, 0.1)`,
                                    color: getActionColor(log.action)
                                }}>
                                    {log.action.includes('FAILED') ? '失败' : '成功'}
                                </span>
                            </div>
                        </div>
                    ))
                )}

                {/* Pagination (Simple) */}
                {totalPages > 1 && (
                    <div style={{ padding: '16px 24px', display: 'flex', justifyContent: 'flex-end', gap: '12px' }}>
                        <button
                            className="btn-primary"
                            style={{ padding: '6px 16px', fontSize: '0.8rem', background: page === 0 ? 'gray' : undefined }}
                            disabled={page === 0}
                            onClick={() => setPage(p => p - 1)}
                        >
                            上一页
                        </button>
                        <span style={{ display: 'flex', alignItems: 'center', fontSize: '0.9rem' }}>
                            {page + 1} / {totalPages}
                        </span>
                        <button
                            className="btn-primary"
                            style={{ padding: '6px 16px', fontSize: '0.8rem', background: page >= totalPages - 1 ? 'gray' : undefined }}
                            disabled={page >= totalPages - 1}
                            onClick={() => setPage(p => p + 1)}
                        >
                            下一页
                        </button>
                    </div>
                )}
            </div>

            <style>{`
                .table-row-hover:hover {
                    background: rgba(255, 255, 255, 0.03) !important;
                }
            `}</style>
        </div>
    );
}
