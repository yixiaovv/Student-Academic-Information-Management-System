import { Link, useNavigate } from 'react-router-dom';
import { useTranslation } from '../hooks/useTranslation';

function Navbar({ user, onLogout }) {
  const { t } = useTranslation();
  const navigate = useNavigate();

  const handleLogout = () => {
    onLogout();
    navigate('/login');
  };

  const role = user?.role;

  return (
    <nav className="navbar">
      <div className="navbar-brand">
        <Link to="/">{t('nav.brand')}</Link>
      </div>
      <div className="navbar-menu">
        {role === 'STUDENT' && (
          <>
            <Link to="/grades">{t('nav.grades')}</Link>
            <Link to="/transcript">{t('nav.transcript')}</Link>
            <Link to="/scholarship">{t('nav.scholarship')}</Link>
            <Link to="/ranking">{t('nav.ranking')}</Link>
          </>
        )}
        {role === 'TEACHER' && (
          <>
            <Link to="/teacher/courses">{t('nav.myCourses')}</Link>
            <Link to="/teacher/grades/enter">{t('nav.gradeEntry')}</Link>
            <Link to="/ranking">{t('nav.ranking')}</Link>
          </>
        )}
        {role === 'ADMIN' && (
          <>
            <Link to="/grades">{t('nav.grades')}</Link>
            <Link to="/transcript">{t('nav.transcript')}</Link>
            <Link to="/scholarship">{t('nav.scholarship')}</Link>
            <Link to="/ranking">{t('nav.ranking')}</Link>
            <Link to="/admin/pending">{t('nav.pendingReview')}</Link>
            <Link to="/admin/students">Студенты(学生管理)</Link>
          </>
        )}
      </div>
      <div className="navbar-user">
        <span className="navbar-role">[{role}]</span>
        <span>{user?.displayName || user?.username}</span>
        <button onClick={handleLogout}>{t('auth.logout')}</button>
      </div>
    </nav>
  );
}

export default Navbar;
