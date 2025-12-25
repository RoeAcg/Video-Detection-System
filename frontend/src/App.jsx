import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import Login from './pages/Login';
import Register from './pages/Register';
import Dashboard from './pages/Dashboard';
import ImageDetection from './pages/ImageDetection';
import AuditLog from './pages/AuditLog';
import MyVideos from './pages/MyVideos';
import History from './pages/History';
import Layout from './components/Layout';
import { WebSocketProvider } from './context/WebSocketContext';

function App() {
  const isAuthenticated = !!localStorage.getItem('token');

  return (
    <Router>
      <WebSocketProvider>
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />

          <Route path="/" element={isAuthenticated ? <Layout /> : <Navigate to="/login" />}>
            <Route index element={<Dashboard />} />
            <Route path="image-detection" element={<ImageDetection />} />
            <Route path="videos" element={<MyVideos />} />
            <Route path="history" element={<History />} />
            <Route path="audit" element={<AuditLog />} />
          </Route>
        </Routes>
      </WebSocketProvider>
    </Router>
  );
}

export default App;
