import { Routes, Route, Navigate } from 'react-router-dom';
import { useAuth } from './hooks/useAuth';
import Navbar from './components/Navbar';
import ProtectedRoute from './components/ProtectedRoute';
import RoleRoute from './components/RoleRoute';
import Login from './pages/Login';
import Register from './pages/Register';
import Dashboard from './pages/Dashboard';
import Grades from './pages/Grades';
import Transcript from './pages/Transcript';
import Scholarship from './pages/Scholarship';
import Ranking from './pages/Ranking';
import TeacherDashboard from './pages/TeacherDashboard';
import GradeManagement from './pages/GradeManagement';
import AdminStudents from './pages/AdminStudents';

function App() {
  const { user, login, register, logout } = useAuth();

  return (
    <div className="app">
      {user && <Navbar user={user} onLogout={logout} />}
      <main className={user ? 'main-content' : 'main-full'}>
        <Routes>
          <Route path="/login" element={<Login onLogin={login} />} />
          <Route path="/register" element={<Register onRegister={register} />} />
          <Route path="/" element={
            <ProtectedRoute user={user}>
              <Dashboard user={user} />
            </ProtectedRoute>
          } />
          <Route path="/grades" element={
            <ProtectedRoute user={user}>
              <Grades />
            </ProtectedRoute>
          } />
          <Route path="/transcript" element={
            <ProtectedRoute user={user}>
              <Transcript />
            </ProtectedRoute>
          } />
          <Route path="/scholarship" element={
            <ProtectedRoute user={user}>
              <Scholarship />
            </ProtectedRoute>
          } />
          <Route path="/ranking" element={
            <ProtectedRoute user={user}>
              <Ranking />
            </ProtectedRoute>
          } />
          <Route path="/teacher/courses" element={
            <RoleRoute user={user} allowedRoles={['TEACHER', 'ADMIN']}>
              <TeacherDashboard />
            </RoleRoute>
          } />
          <Route path="/teacher/grades/enter" element={
            <RoleRoute user={user} allowedRoles={['TEACHER', 'ADMIN']}>
              <GradeManagement />
            </RoleRoute>
          } />
          <Route path="/teacher/grades/course/:courseId" element={
            <RoleRoute user={user} allowedRoles={['TEACHER', 'ADMIN']}>
              <GradeManagement />
            </RoleRoute>
          } />
          <Route path="/admin/pending" element={
            <RoleRoute user={user} allowedRoles={['ADMIN']}>
              <GradeManagement />
            </RoleRoute>
          } />
          <Route path="/admin/students" element={
            <RoleRoute user={user} allowedRoles={['ADMIN']}>
              <AdminStudents />
            </RoleRoute>
          } />
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </main>
    </div>
  );
}

export default App;
