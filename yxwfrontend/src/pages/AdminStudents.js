import { useState, useEffect } from 'react';
import api from '../api';
import { useTranslation } from '../hooks/useTranslation';

function AdminStudents() {
  const { t } = useTranslation();
  const [students, setStudents] = useState([]);
  const [loading, setLoading] = useState(true);
  const [editingId, setEditingId] = useState(null);
  const [editForm, setEditForm] = useState({ className: '', gender: '', name: '' });
  const [message, setMessage] = useState('');

  useEffect(() => {
    loadStudents();
  }, []);

  const loadStudents = async () => {
    try {
      const res = await api.get('/admin/students');
      setStudents(res.data || []);
    } catch (e) { /* ignore */ }
    finally { setLoading(false); }
  };

  const startEdit = (s) => {
    setEditingId(s.id);
    setEditForm({
      name: s.name || '',
      gender: s.gender || '',
      className: s.className || ''
    });
    setMessage('');
  };

  const cancelEdit = () => {
    setEditingId(null);
    setEditForm({ className: '', gender: '', name: '' });
  };

  const saveEdit = async (id) => {
    try {
      const res = await api.put(`/admin/students/${id}`, editForm);
      if (res.data) {
        setMessage(t('common.operationSuccess'));
        setEditingId(null);
        loadStudents();
      }
    } catch (e) {
      setMessage(e.response?.data?.error || t('common.operationFailed'));
    }
  };

  if (loading) return <p className="loading">{t('common.loading')}</p>;

  return (
    <div className="page">
      <h2>Управление студентами(学生管理)</h2>
      {message && <div className="info-msg">{message}</div>}
      {students.length === 0 ? (
        <p className="empty">{t('common.noData')}</p>
      ) : (
        <table className="data-table">
          <thead>
            <tr>
              <th>№</th>
              <th>Студент(学号)</th>
              <th>Имя(姓名)</th>
              <th>Пол(性别)</th>
              <th>Группа(班级)</th>
              <th>{t('teacher.actions')}</th>
            </tr>
          </thead>
          <tbody>
            {students.map((s) => (
              <tr key={s.id}>
                <td>{s.id}</td>
                <td>{s.studentNo}</td>
                {editingId === s.id ? (
                  <>
                    <td>
                      <input value={editForm.name}
                        onChange={e => setEditForm({...editForm, name: e.target.value})}
                        className="edit-input" />
                    </td>
                    <td>
                      <select value={editForm.gender}
                        onChange={e => setEditForm({...editForm, gender: e.target.value})}
                        className="edit-input">
                        <option value="">--</option>
                        <option value="男">男</option>
                        <option value="女">女</option>
                      </select>
                    </td>
                    <td>
                      <input value={editForm.className}
                        onChange={e => setEditForm({...editForm, className: e.target.value})}
                        className="edit-input" placeholder="Например: 计算机科学2024-1班" />
                    </td>
                    <td className="action-cell">
                      <button className="btn-small btn-approve" onClick={() => saveEdit(s.id)}>
                        {t('common.confirm')}
                      </button>
                      <button className="btn-small btn-secondary" onClick={cancelEdit}>
                        {t('common.cancel')}
                      </button>
                    </td>
                  </>
                ) : (
                  <>
                    <td>{s.name}</td>
                    <td>{s.gender || '—'}</td>
                    <td>{s.className || <span style={{ color: '#fa8c16' }}>Не назначено(未分配)</span>}</td>
                    <td>
                      <button className="btn-small" onClick={() => startEdit(s)}>
                        Редактировать(编辑)
                      </button>
                    </td>
                  </>
                )}
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}

export default AdminStudents;
