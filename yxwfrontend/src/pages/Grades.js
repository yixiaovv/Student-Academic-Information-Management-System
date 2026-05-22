import { useEffect, useState } from 'react';
import api from '../api';
import { useTranslation } from '../hooks/useTranslation';

function Grades() {
  const { t } = useTranslation();
  const [grades, setGrades] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    api.get('/grades')
      .then(res => setGrades(res.data))
      .catch(() => setError(t('common.error')))
      .finally(() => setLoading(false));
  }, [t]);

  const statusClass = (status) => {
    const map = {
      draft: 'status-draft',
      submitted: 'status-submitted',
      approved: 'status-approved',
      published: 'status-published',
      archived: 'status-archived'
    };
    return map[status] || '';
  };

  return (
    <div className="page">
      <h2>{t('grades.title')}</h2>
      {loading ? (
        <p className="loading">{t('grades.loading')}</p>
      ) : error ? (
        <p className="empty">{error}</p>
      ) : grades.length === 0 ? (
        <p className="empty">{t('grades.empty')}</p>
      ) : (
        <table className="data-table">
          <thead>
            <tr>
              <th>{t('grades.courseName')}</th>
              <th>{t('grades.courseCode')}</th>
              <th>{t('grades.credits')}</th>
              <th>{t('grades.score')}</th>
              <th>{t('grades.semester')}</th>
              <th>{t('grades.academicYear')}</th>
              <th>{t('grades.status')}</th>
            </tr>
          </thead>
          <tbody>
            {grades.map((g) => (
              <tr key={g.id}>
                <td>{g.courseName}{g.isRetake ? ` (${t('common.retake')})` : ''}</td>
                <td>{g.courseCode}</td>
                <td>{g.credits}</td>
                <td className={`score ${g.score >= 60 ? (g.score >= 85 ? 'excellent' : 'pass') : 'fail'}`}>
                  {g.score}
                  {g.retakeExamScore && <span className="retake-score"> / {g.retakeExamScore}</span>}
                </td>
                <td>{g.semester}</td>
                <td>{g.academicYear}</td>
                <td><span className={`status-badge ${statusClass(g.status)}`}>{t(`status.${g.status}`)}</span></td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}

export default Grades;
