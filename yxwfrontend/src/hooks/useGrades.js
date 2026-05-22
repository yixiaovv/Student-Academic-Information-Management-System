import { useState, useCallback } from 'react';
import api from '../api';

export function useGrades() {
  const [grades, setGrades] = useState([]);
  const [transcript, setTranscript] = useState(null);
  const [loading, setLoading] = useState(false);

  const fetchGrades = useCallback(async () => {
    setLoading(true);
    try {
      const res = await api.get('/grades');
      setGrades(res.data);
    } finally {
      setLoading(false);
    }
  }, []);

  const fetchTranscript = useCallback(async () => {
    setLoading(true);
    try {
      const res = await api.get('/grades/transcript');
      setTranscript(res.data);
    } finally {
      setLoading(false);
    }
  }, []);

  const fetchScholarship = useCallback(async () => {
    setLoading(true);
    try {
      const res = await api.get('/scholarship/my');
      return res.data;
    } finally {
      setLoading(false);
    }
  }, []);

  return { grades, transcript, loading, fetchGrades, fetchTranscript, fetchScholarship };
}
