// Admin settings update shared defaults; visitor choices stay in this tab's session.
localStorage.removeItem('vj_theme');
localStorage.removeItem('vj_lang');

const SitePreferences = {
  getTheme: () => sessionStorage.getItem('vj_theme') || localStorage.getItem('vj_site_theme') || 'dark',
  getLanguage: () => sessionStorage.getItem('vj_lang') || localStorage.getItem('vj_site_language') || 'en',
  async saveAsAdmin(changes) {
    try {
      const saved = await API.updateSitePreferences(changes);
      localStorage.setItem('vj_site_theme', saved.theme);
      localStorage.setItem('vj_site_language', saved.language);
      sessionStorage.removeItem('vj_theme');
      sessionStorage.removeItem('vj_lang');
      location.reload();
    } catch (error) {
      console.error('[Site preference update failed]', error);
      if (typeof showToast === 'function') showToast('Could not save the site-wide preference.', 'error');
    }
  },
  ready: null
};

SitePreferences.ready = (async () => {
  const previousTheme = localStorage.getItem('vj_site_theme') || 'dark';
  const previousLanguage = localStorage.getItem('vj_site_language') || 'en';
  const prefs = await API.getSitePreferences();
  if (!prefs || !['dark', 'light', 'sepia'].includes(prefs.theme) || !['en', 'ta', 'hi'].includes(prefs.language)) return;
  localStorage.setItem('vj_site_theme', prefs.theme);
  localStorage.setItem('vj_site_language', prefs.language);
  if ((!sessionStorage.getItem('vj_theme') && previousTheme !== prefs.theme)
      || (!sessionStorage.getItem('vj_lang') && previousLanguage !== prefs.language)) {
    location.reload();
  }
  document.documentElement.setAttribute('data-theme', SitePreferences.getTheme());
})();
