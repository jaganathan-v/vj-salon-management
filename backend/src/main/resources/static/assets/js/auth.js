// ============================
// VIJAYAN SALON — AUTH UTILITIES
// ============================
const Auth = {
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
  saveAdminSession(token) {
    localStorage.setItem('vj_token',     token);
    localStorage.setItem('vj_user_name', 'Admin');
    localStorage.setItem('vj_role',      'admin');
  },
  clear() {
    ['vj_token','vj_user_name','vj_stylist_id','vj_role'].forEach(k => localStorage.removeItem(k));
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
