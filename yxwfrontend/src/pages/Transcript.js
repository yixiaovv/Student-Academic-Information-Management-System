import { useEffect, useState } from 'react';
import api from '../api';
import { useTranslation } from '../hooks/useTranslation';

function Transcript() {
  const { t } = useTranslation();
  const [transcript, setTranscript] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    api.get('/grades/transcript')
      .then(res => setTranscript(res.data))
      .catch(() => {})
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <p className="loading">{t('common.loading')}</p>;
  if (!transcript || !transcript.studentName) return <p className="empty">{t('transcript.noData')}</p>;

  return (
    <div className="page">
      <h2>{t('transcript.title')}</h2>
      <div className="transcript-header">
        <div className="info-row"><span>{t('transcript.name')}：</span>{transcript.studentName}</div>
        <div className="info-row"><span>{t('transcript.studentNo')}：</span>{transcript.studentNo}</div>
        <div className="info-row"><span>{t('transcript.class')}：</span>{transcript.className}</div>
      </div>

      <table className="data-table">
        <thead>
          <tr>
            <th>{t('transcript.course')}</th>
            <th>{t('grades.credits')}</th>
            <th>{t('grades.score')}</th>
            <th>{t('grades.semester')}</th>
          </tr>
        </thead>
        <tbody>
          {transcript.grades?.map((g) => (
            <tr key={g.id}>
              <td>{g.courseName}</td>
              <td>{g.credits}</td>
              <td className={`score ${g.score >= 60 ? (g.score >= 85 ? 'excellent' : 'pass') : 'fail'}`}>{g.score}</td>
              <td>{g.semester}</td>
            </tr>
          ))}
        </tbody>
      </table>

      <div className="gpa-section">
        <div className="gpa-item">
          <span className="gpa-label">{t('transcript.gpa')}</span>
          <span className="gpa-value">{transcript.gpa}</span>
        </div>
        <div className="gpa-item">
          <span className="gpa-label">{t('transcript.rating')}</span>
          <span className={`gpa-value rating-${transcript.rating}`}>{t(`rating.${transcript.rating}`)}</span>
        </div>
      </div>
    </div>
  );
}

export default Transcript;
