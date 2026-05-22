import { useState, useEffect } from 'react';
import api from '../api';
import { useTranslation } from '../hooks/useTranslation';

function Ranking() {
  const { t } = useTranslation();
  const [rankings, setRankings] = useState([]);
  const [myRank, setMyRank] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    Promise.all([
      api.get('/ranking/my'),
      api.get('/ranking/overall')
    ])
      .then(([myRes, allRes]) => {
        if (myRes.data && myRes.data.studentName) setMyRank(myRes.data);
        setRankings(allRes.data || []);
      })
      .catch(() => {})
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <p className="loading">{t('ranking.loading')}</p>;

  return (
    <div className="page">
      <h2>{t('ranking.title')}</h2>

      {myRank && (
        <div className="my-rank-card">
          <span className="my-rank-label">{t('ranking.myRank')}</span>
          <span className="my-rank-value">
            {myRank.rank} / {myRank.totalStudents}
          </span>
          <span className="my-rank-detail">
            {t('ranking.myRankInfo').replace('{rank}', myRank.rank).replace('{total}', myRank.totalStudents)}
          </span>
        </div>
      )}

      {rankings.length === 0 ? (
        <p className="empty">{t('ranking.noData')}</p>
      ) : (
        <table className="data-table">
          <thead>
            <tr>
              <th>{t('ranking.rank')}</th>
              <th>{t('ranking.name')}</th>
              <th>{t('ranking.studentNo')}</th>
              <th>{t('ranking.className')}</th>
              <th>{t('ranking.gpa')}</th>
              <th>{t('ranking.rating')}</th>
            </tr>
          </thead>
          <tbody>
            {rankings.map((r) => (
              <tr key={r.studentNo} className={myRank && r.studentNo === myRank.studentNo ? 'my-rank-row' : ''}>
                <td className="rank-num">{r.rank}</td>
                <td>{r.studentName}</td>
                <td>{r.studentNo}</td>
                <td>{r.className}</td>
                <td className="score">{r.gpa}</td>
                <td><span className={`rating-badge rating-${r.rating}`}>{t(`rating.${r.rating}`)}</span></td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}

export default Ranking;
