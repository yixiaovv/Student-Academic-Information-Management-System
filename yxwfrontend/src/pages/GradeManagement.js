import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import api from '../api';
import { useTranslation } from '../hooks/useTranslation';

function GradeManagement() {
  const { t } = useTranslation();
  const { courseId } = useParams();
  const navigate = useNavigate();
  const [mode, setMode] = useState(courseId ? 'view' : 'list');
  const [courses, setCourses] = useState([]);
  const [grades, setGrades] = useState([]);
  const [pendingGrades, setPendingGrades] = useState([]);
  const [studentList, setStudentList] = useState([]);
  const [loading, setLoading] = useState(true);
  const [message, setMessage] = useState('');

  // 表单状态
  const [form, setForm] = useState({
    studentId: '', courseId: courseId || '', score: '', semester: '第一学期',
    academicYear: '2024-2025', isRetake: false, originalScore: ''
  });

  useEffect(() => {
    const path = window.location.pathname;
    if (path.includes('/admin/pending')) {
      loadPending();
    } else if (courseId) {
      loadCourseGrades(courseId);
      loadStudents(courseId);
    } else {
      loadCourses();
    }
  }, [courseId]);

  const loadCourses = async () => {
    try {
      const res = await api.get('/teacher/courses');
      setCourses(res.data || []);
      setMode('list');
    } catch (e) { /* ignore */ }
    finally { setLoading(false); }
  };

  const loadCourseGrades = async (id) => {
    try {
      const res = await api.get(`/teacher/grades/${id}`);
      setGrades(res.data || []);
      setMode('view');
    } catch (e) { /* ignore */ }
    finally { setLoading(false); }
  };

  const loadStudents = async (id) => {
    try {
      const res = await api.get(`/teacher/students/${id}`);
      setStudentList(res.data || []);
    } catch (e) { /* ignore */ }
  };

  const loadPending = async () => {
    try {
      const res = await api.get('/grades/pending');
      console.log('加载待审核成绩:', res.data); // 调试信息，方便看返回了什么
      setPendingGrades(res.data || []);
      setMode('pending');
    } catch (e) { 
      console.error('加载出错:', e);
    }
    finally { setLoading(false); }
  };

  const handleWorkflow = async (gradeId, action) => {
    try {
      const res = await api.post('/grades/workflow', { gradeId, action });
      if (res.data) {
        setMessage(t('common.operationSuccess'));
        loadPending();
      }
    } catch (e) {
      setMessage(e.response?.data?.error || t('common.operationFailed'));
    }
  };

  const handleCourseSelect = (e) => {
    const id = e.target.value;
    setForm({ ...form, courseId: id, studentId: '' });
    if (id) {
      loadStudents(id);
    } else {
      setStudentList([]);
    }
  };

  const handleSubmitGrade = async (gradeId) => {
    try {
      const res = await api.post('/grades/workflow', { gradeId, action: 'submit' });
      if (res.data) {
        setMessage(t('common.operationSuccess'));
        // 刷新成绩列表
        if (courseId) loadCourseGrades(parseInt(courseId));
      }
    } catch (e) {
      setMessage(e.response?.data?.error || t('common.operationFailed'));
    }
  };

  const handleEnterGrade = async (e) => {
    e.preventDefault();
    if (!form.courseId || !form.studentId) {
      setMessage('Пожалуйста, выберите курс и студента(请选择课程和学生)');
      return;
    }
    try {
      await api.post('/grades/enter', {
        ...form,
        courseId: parseInt(form.courseId),
        studentId: parseInt(form.studentId),
        score: parseFloat(form.score),
        originalScore: form.originalScore ? parseFloat(form.originalScore) : null
      });
      setMessage(t('teacher.submitSuccess'));
      setForm({ ...form, score: '', originalScore: '', studentId: '' });
    } catch (err) {
      setMessage(err.response?.data?.error || t('teacher.submitFailed'));
    }
  };

  if (loading) return <p className="loading">{t('common.loading')}</p>;

  // ===== 教师课程列表 =====
  if (mode === 'list') {
    return (
      <div className="page">
        <h2>{t('teacher.myCourses')}</h2>
        <div style={{ marginBottom: 16 }}>
          <button className="btn-small" onClick={() => setMode('enter')}>
            {t('teacher.enterGrade')}
          </button>
        </div>
        {courses.length === 0 ? <p className="empty">{t('teacher.noCourses')}</p> : (
          <table className="data-table">
            <thead>
              <tr>
                <th>{t('teacher.courseName')}</th>
                <th>{t('teacher.className')}</th>
                <th>{t('teacher.actions')}</th>
              </tr>
            </thead>
            <tbody>
              {courses.map((c) => (
                <tr key={c.courseTeacherId}>
                  <td>{c.courseName}</td>
                  <td>{c.className}</td>
                  <td>
                    <button className="btn-small" onClick={() => {
                      navigate(`/teacher/grades/course/${c.courseId}`);
                    }}>
                      {t('teacher.viewGrades')}
                    </button>
                    <button className="btn-small btn-secondary"
                      onClick={() => {
                        setForm({ ...form, courseId: String(c.courseId) });
                        loadStudents(c.courseId);
                        setMode('enter');
                      }}>
                      {t('teacher.enterGrade')}
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    );
  }

  // ===== 成绩录入表单 =====
  if (mode === 'enter') {
    const uniqueCourses = courses.filter((c, i, arr) =>
      arr.findIndex(x => x.courseId === c.courseId) === i
    );

    return (
      <div className="page">
        <h2>{t('teacher.gradeEntry')}</h2>
        {message && <div className="info-msg">{message}</div>}
        <form className="enter-grade-form" onSubmit={handleEnterGrade}>

          {/* 课程下拉 */}
          {!courseId && (
            <div className="form-group">
              <label>{t('teacher.courseName')}</label>
              <select value={form.courseId} onChange={handleCourseSelect} required>
                <option value="">-- {t('teacher.selectCourse')} --</option>
                {uniqueCourses.map((c) => (
                  <option key={c.courseId} value={c.courseId}>
                    {c.courseName} ({c.courseCode})
                  </option>
                ))}
              </select>
            </div>
          )}

          {/* 学生下拉 */}
          <div className="form-group">
            <label>{t('teacher.selectStudent')}</label>
            <select value={form.studentId}
              onChange={e => setForm({...form, studentId: e.target.value})} required>
              <option value="">-- {t('teacher.selectStudent')} --</option>
              {studentList.map((s) => (
                <option key={s.id} value={s.id}>
                  {s.name} ({s.studentNo}) - {s.className}
                </option>
              ))}
            </select>
          </div>

          <div className="form-group">
            <label>{t('teacher.score')}</label>
            <input type="number" step="0.01" value={form.score}
              onChange={e => setForm({...form, score: e.target.value})} required />
          </div>
          <div className="form-group">
            <label>{t('teacher.semester')}</label>
            <select value={form.semester}
              onChange={e => setForm({...form, semester: e.target.value})}>
              <option>第一学期</option>
              <option>第二学期</option>
            </select>
          </div>
          <div className="form-group">
            <label>{t('teacher.academicYear')}</label>
            <input value={form.academicYear}
              onChange={e => setForm({...form, academicYear: e.target.value})} />
          </div>
          <div className="form-group">
            <label>
              <input type="checkbox" checked={form.isRetake}
                onChange={e => setForm({...form, isRetake: e.target.checked})} />
              {' '}{t('grades.retake')}
            </label>
          </div>
          {form.isRetake && (
            <div className="form-group">
              <label>{t('grades.originalScore')}</label>
              <input type="number" step="0.01" value={form.originalScore}
                onChange={e => setForm({...form, originalScore: e.target.value})} />
            </div>
          )}
          <button type="submit" className="btn-primary">{t('teacher.submit')}</button>
          <button type="button" className="btn-secondary"
            onClick={() => setMode('list')}
            style={{ marginTop: 8, width: '100%' }}>{t('common.back')}</button>
        </form>
      </div>
    );
  }

  // ===== 查看课程成绩 =====
  if (mode === 'view') {
    return (
      <div className="page">
        <h2>{t('grades.title')}</h2>
        <button className="btn-small" onClick={() => navigate('/teacher/courses')}
          style={{ marginBottom: 16 }}>{t('common.back')}</button>
        {message && <div className="info-msg">{message}</div>}
        {grades.length === 0 ? <p className="empty">{t('grades.empty')}</p> : (
          <table className="data-table">
            <thead>
              <tr>
                <th>{t('ranking.name')}</th>
                <th>{t('grades.courseName')}</th>
                <th>{t('grades.score')}</th>
                <th>{t('grades.semester')}</th>
                <th>{t('grades.status')}</th>
                <th>{t('teacher.actions')}</th>
              </tr>
            </thead>
            <tbody>
              {grades.map((g) => (
                <tr key={g.id}>
                  <td>{g.studentName}</td>
                  <td>{g.courseName}</td>
                  <td className={`score ${g.score >= 60 ? (g.score >= 85 ? 'excellent' : 'pass') : 'fail'}`}>{g.score}</td>
                  <td>{g.semester}</td>
                  <td><span className={`status-badge status-${g.status}`}>{t(`status.${g.status}`)}</span></td>
                  <td className="action-cell">
                    {g.status === 'draft' && (
                      <button className="btn-small btn-submit"
                        onClick={() => handleSubmitGrade(g.id)}>
                        {t('grades.submitReview')}
                      </button>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
        {/* 从课程成绩页也可以进入录入 */}
        <button className="btn-small" style={{ marginTop: 16 }}
          onClick={() => {
            loadStudents(parseInt(courseId));
            setForm({ ...form, courseId: courseId });
            setMode('enter');
          }}>
          {t('teacher.enterGrade')}
        </button>
      </div>
    );
  }

  // ===== 待审核（管理员） =====
  if (mode === 'pending') {
    return (
      <div className="page">
        <h2>{t('teacher.pendingTitle')}</h2>
        {message && <div className="info-msg">{message}</div>}
        {pendingGrades.length === 0 ? <p className="empty">{t('teacher.emptyPending')}</p> : (
          <table className="data-table">
            <thead>
              <tr>
                <th>{t('ranking.name')}</th>
                <th>{t('grades.courseName')}</th>
                <th>{t('grades.score')}</th>
                <th>{t('grades.semester')}</th>
                <th>{t('grades.status')}</th>
                <th>{t('teacher.actions')}</th>
              </tr>
            </thead>
            <tbody>
              {pendingGrades.map((g) => (
                <tr key={g.id}>
                  <td>{g.studentName}</td>
                  <td>{g.courseName}</td>
                  <td className="score">{g.score}</td>
                  <td>{g.semester}</td>
                  <td><span className={`status-badge status-${g.status}`}>{t(`status.${g.status}`)}</span></td>
                  <td className="action-cell">
                    {g.status === 'submitted' && (
                      <button className="btn-small btn-approve" onClick={() => handleWorkflow(g.id, 'approve')}>
                        {t('common.approve')}
                      </button>
                    )}
                    {g.status === 'approved' && (
                      <button className="btn-small btn-publish" onClick={() => handleWorkflow(g.id, 'publish')}>
                        {t('grades.publish')}
                      </button>
                    )}
                    {g.status === 'published' && (
                      <button className="btn-small btn-archive" onClick={() => handleWorkflow(g.id, 'archive')}>
                        {t('grades.archive')}
                      </button>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    );
  }

  return null;
}

export default GradeManagement;
