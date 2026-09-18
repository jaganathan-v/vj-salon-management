// ============================
// VJ SALON — i18n ENGINE
// ============================
let translations = {};
let currentLang = 'en';

async function loadTranslations(lang) {
  try {
    const res = await fetch(`assets/i18n/${lang}.json`);
    translations = await res.json();
    currentLang = lang;
  } catch {
    console.warn('Could not load translations for:', lang);
    translations = {};
  }
}

function t(key) {
  return translations[key] || key;
}

function applyI18n() {
  document.querySelectorAll('[data-i18n]').forEach(el => {
    const key = el.dataset.i18n;
    el.textContent = t(key);
  });
  document.querySelectorAll('[data-i18n-placeholder]').forEach(el => {
    el.placeholder = t(el.dataset['i18n-placeholder']);
  });
  document.querySelectorAll('[data-i18n-title]').forEach(el => {
    el.title = t(el.dataset['i18n-title']);
  });
}

function getServiceName(service) {
  const langMap = { en: service.nameEn, ta: service.nameTa, hi: service.nameHi };
  return langMap[currentLang] || service.nameEn || '';
}

async function initI18n() {
  const lang = localStorage.getItem('vj_lang') || 'en';
  await loadTranslations(lang);
  applyI18n();
}

// Auto-init
document.addEventListener('DOMContentLoaded', initI18n);
