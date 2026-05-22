import { Link } from 'react-router-dom';
import { useTranslation } from '../hooks/useTranslation';

function Dashboard({ user }) {
  const { t } = useTranslation();
  const role = user?.role;

  return (
    <div className="dashboard">
      <h1>{t('dashboard.welcome')}，{user?.displayName || user?.username}！</h1>
      <p className="dashboard-subtitle">{t('dashboard.subtitle')}</p>

      <div className="dashboard-cards">
        {role === 'STUDENT' && (
          <>
            <Link to="/grades" className="card">
              <h3>{t('dashboard.grades')}</h3>
              <p>{t('dashboard.gradesDesc')}</p>
            </Link>
            <Link to="/transcript" className="card">
              <h3>{t('dashboard.transcript')}</h3>
              <p>{t('dashboard.transcriptDesc')}</p>
            </Link>
            <Link to="/scholarship" className="card">
              <h3>{t('dashboard.scholarship')}</h3>
              <p>{t('dashboard.scholarshipDesc')}</p>
            </Link>
            <Link to="/ranking" className="card">
              <h3>{t('dashboard.ranking')}</h3>
              <p>{t('dashboard.rankingDesc')}</p>
            </Link>
          </>
        )}
        {role === 'TEACHER' && (
          <>
            <Link to="/teacher/courses" className="card">
              <h3>{t('dashboard.grades')}</h3>
              <p>{t('dashboard.gradesDesc')}</p>
            </Link>
            <Link to="/teacher/grades/enter" className="card">
              <h3>{t('dashboard.gradeEntry')}</h3>
              <p>{t('dashboard.gradeEntryDesc')}</p>
            </Link>
            <Link to="/ranking" className="card">
              <h3>{t('dashboard.ranking')}</h3>
              <p>{t('dashboard.rankingDesc')}</p>
            </Link>
          </>
        )}
        {role === 'ADMIN' && (
          <>
            <Link to="/grades" className="card">
              <h3>{t('dashboard.grades')}</h3>
              <p>{t('dashboard.gradesDesc')}</p>
            </Link>
            <Link to="/transcript" className="card">
              <h3>{t('dashboard.transcript')}</h3>
              <p>{t('dashboard.transcriptDesc')}</p>
            </Link>
            <Link to="/scholarship" className="card">
              <h3>{t('dashboard.scholarship')}</h3>
              <p>{t('dashboard.scholarshipDesc')}</p>
            </Link>
            <Link to="/ranking" className="card">
              <h3>{t('dashboard.ranking')}</h3>
              <p>{t('dashboard.rankingDesc')}</p>
            </Link>
            <Link to="/admin/pending" className="card">
              <h3>{t('dashboard.pendingReview')}</h3>
              <p>{t('dashboard.pendingReviewDesc')}</p>
            </Link>
          </>
        )}
      </div>
    </div>
  );
}

export default Dashboard;
