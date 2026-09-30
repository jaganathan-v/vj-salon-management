// ============================
// VIJAYAN SALON — API CLIENT
// ============================
const API_BASE = `${window.location.origin}/api`;

function normalizeShopStatus(status) {
  return status ? { ...status, isOpen: status.isOpen ?? status.open ?? false } : status;
}

// Generic fetch wrapper
async function _call(endpoint, options = {}) {
  const { requireSuccess = false, ...requestOptions } = options;
  let responseStatus = null;
  let responseBody = '';
  try {
    const token = sessionStorage.getItem('vj_token');
    const headers = {
      'Content-Type': 'application/json',
      ...(token ? { 'Authorization': 'Bearer ' + token } : {}),
      ...(options.headers || {})
    };
    const res = await fetch(API_BASE + endpoint, { ...requestOptions, headers });
    responseStatus = res.status;
    if (!res.ok) {
      responseBody = await res.text();
      throw new Error('HTTP ' + res.status);
    }
    const text = await res.text();
    responseBody = text;
    return text ? JSON.parse(text) : {};
  } catch (e) {
    const method = (requestOptions.method || 'GET').toUpperCase();
    const isLoginRequest = endpoint === '/auth/stylist' || endpoint === '/auth/admin';
    const isWrite = method !== 'GET' && !isLoginRequest;
    console.error('[API request failed]', {
      method,
      url: API_BASE + endpoint,
      status: responseStatus,
      responseBody,
      error: e
    });
    console.warn('API offline for', endpoint, '—', e.message);
    if (isWrite && typeof showToast === 'function') {
      showToast(`Save failed. ${e.message || 'Please check your connection and try again.'}`, 'error', 'Unable to save');
    }
    if (requireSuccess || isWrite) throw e;
    return null;
  }
}

// Multipart upload wrapper
async function _upload(endpoint, formData, method = 'POST') {
  let responseStatus = null;
  let responseBody = '';
  try {
    const token = sessionStorage.getItem('vj_token');
    const res = await fetch(API_BASE + endpoint, {
      method,
      headers: token ? { 'Authorization': 'Bearer ' + token } : {},
      body: formData
    });
    responseStatus = res.status;
    responseBody = await res.text();
    if (!res.ok) throw new Error('HTTP ' + res.status);
    return responseBody ? JSON.parse(responseBody) : {};
  } catch (e) {
    console.error('[API upload failed]', { method, url: API_BASE + endpoint, status: responseStatus, responseBody, error: e });
    console.warn('Upload failed for', endpoint, '—', e.message);
    if (typeof showToast === 'function') showToast(`Upload failed. ${e.message || 'Please try again.'}`, 'error', 'Unable to save');
    return null;
  }
}

const API = {
  // ---- Public ----
  getShopInfo:        async () => (await _call('/shop/info'))         ?? {},
  getSitePreferences: async () => await _call('/site-preferences'),
  getShopStatus:      async () => normalizeShopStatus((await _call('/shop/status')) ?? { isOpen: false, openedAt: null }),
  getServices:        async () => (await _call('/services'))          ?? [],
  getOffers:          async () => (await _call('/offers'))            ?? [],
  getTodayEvents:     async () => (await _call('/events/today'))      ?? [],
  getAchievements:    async () => (await _call('/achievements'))      ?? [],
  getAdvertisements:  async () => (await _call('/advertisements'))    ?? [],
  getStylistsStatus:  async () => (await _call('/stylists/status'))   ?? [],
  getStylistBookingOptions: async () => (await _call('/stylists/booking-options')) ?? [],
  getPaymentQr:       async () => (await _call('/payment/qr'))        ?? null,

  createBooking: async (data) =>
    (await _call('/bookings', { method:'POST', body:JSON.stringify(data), requireSuccess:true })),

  submitFeedback: async (data) =>
    (await _call('/feedback', { method:'POST', body:JSON.stringify(data) }))
    ?? null,

  // ---- Auth ----
  loginStylist: async (stylistCode, password) => {
    const res = await _call('/auth/stylist', { method:'POST', body:JSON.stringify({ stylistCode, password }) });
    if (res) return res;
    return null;
  },
  loginAdmin: async (adminCode, password) => {
    const res = await _call('/auth/admin', { method:'POST', body:JSON.stringify({ adminCode, password }) });
    if (res) return res;
    return null;
  },
  changeAdminPassword: async (currentPassword, newPassword) =>
    (await _call('/auth/admin/password', { method:'PATCH', body:JSON.stringify({ currentPassword, newPassword }) })) ?? {},

  // ---- Stylist ----
  setShopStatus:   async (isOpen, note='') =>
    normalizeShopStatus((await _call('/shop/status', { method:'PATCH', body:JSON.stringify({ isOpen, note }) })) ?? { isOpen, note }),
  setStylistStatus: async (status, availableAt = null) =>
    (await _call('/stylist/me/status', { method:'PATCH', body:JSON.stringify({ status, availableAt }) })) ?? { status, availableAt },
  setHomeServiceStatus: async (homeServiceStatus) =>
    (await _call('/stylist/me/home-service-status', { method:'PATCH', body:JSON.stringify({ homeServiceStatus }) })) ?? { homeServiceStatus },
  getStylistBookings: async () => (await _call('/bookings')) ?? [],
  updateBookingStatus: async (id, status) =>
    (await _call('/bookings/'+id+'/status', { method:'PATCH', body:JSON.stringify({ status }) })) ?? null,
  deleteBooking: async (id) => (await _call('/bookings/'+id, { method:'DELETE' })) ?? {},
  addDailyLog:   async (data) =>
    (await _call('/daily-log', { method:'POST', body:JSON.stringify(data) })) ?? null,
  getDailyLog:   async () => (await _call('/daily-log')) ?? [],
  getInventory:  async () => (await _call('/inventory')) ?? [],
  addInventory:  async (data) =>
    (await _call('/inventory', { method:'POST', body:JSON.stringify(data) })) ?? null,
  updateInventory: async (id, data) =>
    (await _call('/inventory/'+id, { method:'PATCH', body:JSON.stringify(data) })) ?? null,
  deleteInventory: async (id) => (await _call('/inventory/'+id, { method:'DELETE' })) ?? {},

  // ---- Admin ----
  getAllBookings:  async () => (await _call('/admin/bookings'))  ?? [],
  getAllFeedback:  async () => (await _call('/admin/feedback'))  ?? [],
  deleteFeedback: async (id) => (await _call('/admin/feedback/'+id, { method:'DELETE' })) ?? {},
  getAnalytics:   async (period) => (await _call('/admin/analytics?period='+period)) ?? null,
  updateShopSettings: async (data) =>
    (await _call('/admin/shop-settings', { method:'PUT', body:JSON.stringify(data) })) ?? data,
  updateSitePreferences: async (data) =>
    await _call('/admin/site-preferences', { method:'PUT', body:JSON.stringify(data), requireSuccess:true }),

  // Generic admin CRUD factory
  crud: (resource) => ({
    list:   async ()       => (await _call('/admin/'+resource))                                             ?? [],
    create: async (data)   => {
      const result = await _call('/admin/'+resource, {
        method:'POST', body:JSON.stringify(data),
        requireSuccess: resource === 'offers' || resource === 'achievements' || resource === 'stylists'
      });
      return result ?? null;
    },
    update: async (id, d)  => (await _call('/admin/'+resource+'/'+id, { method:'PUT',  body:JSON.stringify(d)    })) ?? null,
    delete: async (id)     => (await _call('/admin/'+resource+'/'+id, { method:'DELETE' }))                 ?? {}
  }),

  uploadAdvertisementFile: async (formData) => {
    const result = await _upload('/admin/ads/upload', formData);
    if (!result) throw new Error('Advertisement upload failed');
    return result;
  },
  createAdvertisement: async (data) => _call('/admin/ads', { method:'POST', body:JSON.stringify(data), requireSuccess:true }),
  uploadPaymentQr:     async (formData) => (await _upload('/admin/payment-qr', formData, 'PUT')) ?? {},
  resetStylistPassword: async (id, newPassword) =>
    (await _call('/admin/stylists/'+id+'/reset-password', { method:'PATCH', body:JSON.stringify({ newPassword }) })) ?? {}
};
