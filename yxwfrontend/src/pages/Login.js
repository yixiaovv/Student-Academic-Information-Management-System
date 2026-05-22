import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useTranslation } from '../hooks/useTranslation';

function Login({ onLogin }) {
  const { t } = useTranslation();
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    try {
      await onLogin(username, password);
      navigate('/');
    } catch (err) {
      setError(err.response?.data?.error || t('auth.loginFailed'));
    }
  };

  return (
    <div className="auth-container">
      <form className="auth-form" onSubmit={handleSubmit}>
        <h2>{t('auth.loginTitle')}</h2>
        {error && <div className="error-msg">{error}</div>}
        <div className="form-group">
          <label>{t('auth.username')}</label>
          <input value={username} onChange={(e) => setUsername(e.target.value)} required />
        </div>
        <div className="form-group">
          <label>{t('auth.password')}</label>
          <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} required />
        </div>
        <button type="submit" className="btn-primary">{t('auth.login')}</button>
        <p className="auth-link">
          {t('auth.noAccount')}<Link to="/register">{t('auth.registerNow')}</Link>
        </p>
        <p className="auth-hint">{t('auth.demoHint')}</p>
      </form>
    </div>
  );
}

export default Login;
