import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useTranslation } from '../hooks/useTranslation';

function Register({ onRegister }) {
  const { t } = useTranslation();
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [displayName, setDisplayName] = useState('');
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    try {
      await onRegister(username, password, displayName || username);
      navigate('/');
    } catch (err) {
      setError(err.response?.data?.error || t('auth.registerFailed'));
    }
  };

  return (
    <div className="auth-container">
      <form className="auth-form" onSubmit={handleSubmit}>
        <h2>{t('auth.registerTitle')}</h2>
        {error && <div className="error-msg">{error}</div>}
        <div className="form-group">
          <label>{t('auth.username')}</label>
          <input value={username} onChange={(e) => setUsername(e.target.value)} required />
        </div>
        <div className="form-group">
          <label>{t('auth.displayName')}</label>
          <input value={displayName} onChange={(e) => setDisplayName(e.target.value)} />
        </div>
        <div className="form-group">
          <label>{t('auth.password')}</label>
          <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} required />
        </div>
        <button type="submit" className="btn-primary">{t('auth.register')}</button>
        <p className="auth-link">
          {t('auth.hasAccount')}<Link to="/login">{t('auth.loginNow')}</Link>
        </p>
      </form>
    </div>
  );
}

export default Register;
