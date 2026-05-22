import ru from '../locales/ru';

export function useTranslation() {
  const t = (key) => {
    return ru[key] || key;
  };
  return { t };
}
