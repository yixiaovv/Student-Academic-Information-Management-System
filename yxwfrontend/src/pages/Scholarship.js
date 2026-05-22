import { useState, useEffect } from 'react';
import api from '../api';
import { useTranslation } from '../hooks/useTranslation';

function Scholarship() {
  const { t } = useTranslation();
  const [scholarship, setScholarship] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    api.get('/scholarship/my')
      .then(res => setScholarship(res.data))
      .catch(() => {})
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <p className="loading">{t('common.loading')}</p>;
  if (!scholarship) return <p className="empty">{t('scholarship.noData')}</p>;

  return (
    <div className="page">
      <h2>{t('scholarship.title')}</h2>

      <div className="scholarship-card">
        <table className="info-table">
          <tbody>
            <tr><td>{t('scholarship.name')}</td><td>{scholarship.studentName}</td></tr>
            <tr><td>{t('scholarship.studentNo')}</td><td>{scholarship.studentNo}</td></tr>
            <tr><td>{t('scholarship.gpa')}</td><td>{scholarship.gpa}</td></tr>
            <tr><td>{t('scholarship.rating')}</td><td className={`rating-${scholarship.rating}`}>{t(`rating.${scholarship.rating}`)}</td></tr>
            <tr><td>{t('scholarship.type')}</td><td className="scholarship-type">{t(`scholarshipType.${scholarship.type}`)}</td></tr>
            <tr><td>{t('scholarship.amount')}</td><td className="scholarship-amount">
              {scholarship.amount > 0 ? `${t('scholarship.yuan')}${scholarship.amount}` : t('scholarship.noneText')}
            </td></tr>
            <tr><td>{t('scholarship.status')}</td><td>{scholarship.status}</td></tr>
          </tbody>
        </table>
      </div>

      <div className="scholarship-rules">
        <h3>{t('scholarship.rulesTitle')}</h3>
        <ul>
          <li>{t('scholarship.first')}</li>
          <li>{t('scholarship.second')}</li>
          <li>{t('scholarship.third')}</li>
          <li>{t('scholarship.none')}</li>
        </ul>
      </div>
    </div>
  );
}

export default Scholarship;
