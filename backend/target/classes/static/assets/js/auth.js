// ============================
// VIJAYAN SALON — AUTH UTILITIES
// ============================
const AUTH_SESSION_KEYS = ['vj_token','vj_user_name','vj_stylist_id','vj_admin_id','vj_role'];
AUTH_SESSION_KEYS.forEach(key => localStorage.removeItem(key));

// sessionStorage is copied when a tab is duplicated or opened by another page.
// A small shared heartbeat lets the new tab detect and discard a copied login.
const AUTH_TAB_KEY = 'vj_auth_tab_id';
const AUTH_TAB_REGISTRY_KEY = 'vj_auth_tab_registry';
const authNow = Date.now();
let authTabRegistry = {};
try { authTabRegistry = JSON.parse(localStorage.getItem(AUTH_TAB_REGISTRY_KEY) || '{}'); } catch {}
Object.keys(authTabRegistry).forEach(id => {
  if (authNow - authTabRegistry[id] > 30000) delete authTabRegistry[id];
});
let authTabId = sessionStorage.getItem(AUTH_TAB_KEY);
if (authTabId && authTabRegistry[authTabId] && authNow - authTabRegistry[authTabId] <= 30000) {
  AUTH_SESSION_KEYS.forEach(key => sessionStorage.removeItem(key));
  authTabId = null;
}
if (!authTabId) authTabId = globalThis.crypto?.randomUUID?.() || `${authNow}-${Math.random().toString(36).slice(2)}`;
sessionStorage.setItem(AUTH_TAB_KEY, authTabId);
function refreshAuthTabHeartbeat() {
  let registry = {};
  try { registry = JSON.parse(localStorage.getItem(AUTH_TAB_REGISTRY_KEY) || '{}'); } catch {}
  registry[authTabId] = Date.now();
  localStorage.setItem(AUTH_TAB_REGISTRY_KEY, JSON.stringify(registry));
}
function releaseAuthTabHeartbeat() {
  let registry = {};
  try { registry = JSON.parse(localStorage.getItem(AUTH_TAB_REGISTRY_KEY) || '{}'); } catch {}
  delete registry[authTabId];
  localStorage.setItem(AUTH_TAB_REGISTRY_KEY, JSON.stringify(registry));
}
refreshAuthTabHeartbeat();
window.setInterval(refreshAuthTabHeartbeat, 10000);
window.addEventListener('pagehide', releaseAuthTabHeartbeat);

const Auth = {
  inactivityTimer: null,
  activityHandler: null,
  getInactivityLimit: () => sessionStorage.getItem('vj_role') === 'stylist' ? 10 * 60 * 1000 : 60 * 60 * 1000,
  getToken:     () => sessionStorage.getItem('vj_token'),
  getName:      () => sessionStorage.getItem('vj_user_name') || 'User',
  getStylistId: () => sessionStorage.getItem('vj_stylist_id'),
  getRole:      () => sessionStorage.getItem('vj_role'), // 'stylist' | 'admin'
  isLoggedIn() {
    const token = this.getToken();
    return !!token && !this.isTokenExpired(token);
  },

  saveStylistSession(token, name, stylistId) {
    sessionStorage.setItem('vj_token',      token);
    sessionStorage.setItem('vj_user_name',  name);
    sessionStorage.setItem('vj_stylist_id', String(stylistId));
    sessionStorage.setItem('vj_role',       'stylist');
  },
  saveAdminSession(token, adminId = 'VJADMIN') {
    sessionStorage.setItem('vj_token',     token);
    sessionStorage.setItem('vj_user_name', adminId);
    sessionStorage.setItem('vj_admin_id', adminId);
    sessionStorage.setItem('vj_role',      'admin');
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
    ['vj_token','vj_user_name','vj_stylist_id','vj_admin_id','vj_role'].forEach(k => {
      sessionStorage.removeItem(k);
      localStorage.removeItem(k);
    });
  },
  decodeToken(token) {
    try {
      const payload = token.split('.')[1];
      return JSON.parse(atob(payload.replace(/-/g,'+').replace(/_/g,'/')));
    } catch { return null; }
  },
  isTokenExpired(token) {
    const d = this.decodeToken(token);
    if (!d || !Number.isFinite(Number(d.exp))) return true;
    return Date.now() / 1000 > d.exp;
  }
};
