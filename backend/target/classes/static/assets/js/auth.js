// ============================
// VIJAYAN SALON — AUTH UTILITIES
// ============================
const Auth = {
  inactivityTimer: null,
  activityHandler: null,
  getInactivityLimit: () => localStorage.getItem('vj_role') === 'stylist' ? 10 * 60 * 1000 : 60 * 60 * 1000,
  getToken:     () => localStorage.getItem('vj_token'),
  getName:      () => localStorage.getItem('vj_user_name') || 'User',
  getStylistId: () => localStorage.getItem('vj_stylist_id'),
  getRole:      () => localStorage.getItem('vj_role'), // 'stylist' | 'admin'
  isLoggedIn:   () => !!localStorage.getItem('vj_token'),

  saveStylistSession(token, name, stylistId) {
    localStorage.setItem('vj_token',      token);
    localStorage.setItem('vj_user_name',  name);
    localStorage.setItem('vj_stylist_id', String(stylistId));
    localStorage.setItem('vj_role',       'stylist');
  },
  saveAdminSession(token, adminId = 'VJADMIN') {
    localStorage.setItem('vj_token',     token);
    localStorage.setItem('vj_user_name', adminId);
    localStorage.setItem('vj_admin_id', adminId);
    localStorage.setItem('vj_role',      'admin');
  },
  startInactivityTimeout(onTimeout) {
    this.stopInactivityTimeout();
    const reset = () => {
      clearTimeout(this.inactivityTimer);
      this.inactivityTimer = setTimeout(onTimeout, this.getInactivityLimit());
    };
    this.activityHandler = reset;
    ['pointerdown', 'keydown', 'mousemove', 'touchstart', 'scroll'].forEach(type =>
      document.addEventListener(type, reset, { passive: true })
    );
    reset();
  },
  stopInactivityTimeout() {
    clearTimeout(this.inactivityTimer);
    if (this.activityHandler) {
      ['pointerdown', 'keydown', 'mousemove', 'touchstart', 'scroll'].forEach(type =>
        document.removeEventListener(type, this.activityHandler)
      );
      this.activityHandler = null;
    }
  },
  clear() {
    this.stopInactivityTimeout();
    ['vj_token','vj_user_name','vj_stylist_id','vj_admin_id','vj_role'].forEach(k => localStorage.removeItem(k));
  },
  decodeToken(token) {
    try {
      const payload = token.split('.')[1];
      return JSON.parse(atob(payload.replace(/-/g,'+').replace(/_/g,'/')));
    } catch { return null; }
  },
  isTokenExpired(token) {
    if (token === 'demo_token' || token === 'demo_token_admin') return false;
    const d = this.decodeToken(token);
    if (!d || !d.exp) return false;
    return Date.now() / 1000 > d.exp;
  }
};
