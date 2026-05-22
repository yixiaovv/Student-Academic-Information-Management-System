import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import api from '../api';
import { useTranslation } from '../hooks/useTranslation';

function TeacherDashboard() {
  const { t } = useTranslation();
  const [courses, setCourses] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    api.get('/teacher/courses')
      .then(res => setCourses(res.data || []))
      .catch(() => {})
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <p className="loading">{t('common.loading')}</p>;

  return (
    <div className="page">
      <h2>{t('teacher.myCourses')}</h2>
      {courses.length === 0 ? (
        <p className="empty">{t('teacher.noCourses')}</p>
      ) : (
        <table className="data-table">
          <thead>
            <tr>
              <th>{t('teacher.courseName')}</th>
              <th>{t('grades.courseCode')}</th>
              <th>{t('grades.credits')}</th>
              <th>{t('teacher.className')}</th>
              <th>{t('teacher.actions')}</th>
            </tr>
          </thead>
          <tbody>
            {courses.map((c) => (
              <tr key={c.courseTeacherId}>
                <td>{c.courseName}</td>
                <td>{c.courseCode}</td>
                <td>{c.credits}</td>
                <td>{c.className}</td>
                <td>
                  <Link to={`/teacher/grades/course/${c.courseId}`} className="btn-small">
                    {t('teacher.viewGrades')}
                  </Link>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}

export default TeacherDashboard;
