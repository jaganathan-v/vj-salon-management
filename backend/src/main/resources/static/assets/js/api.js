// ============================
// VIJAYAN SALON — API CLIENT
// ============================
const API_BASE = 'https://vj-salon-management.onrender.com/api';

// --- Demo Data (used when backend is offline) ---
const DEMO = {
  shopInfo: {
    shopName: 'VIJAYAN SALON',
    tagline: 'Excellence in Every Cut',
    address: 'No.2 pillayar kovil street, Ponnammapet, Salem - 636001 (near Mariyamman kovil)',
    phone: '6374402014',
    sinceYear: 2010,
    mapsLink: 'https://maps.google.com/maps?q=Ponnammapet+Salem&output=embed'
  },
  shopStatus: { isOpen: false, openedAt: null },
  services: [
    { id:1, nameEn:'Haircut',       nameTa:'முடி வெட்டு',        nameHi:'बाल कटाई',        price:80,  offerPct:0,  category:'HAIR'  },
    { id:2, nameEn:'Beard Trim',    nameTa:'தாடி கத்திரி',        nameHi:'दाढ़ी ट्रिम',      price:50,  offerPct:0,  category:'BEARD' },
    { id:3, nameEn:'Shave',         nameTa:'ஷேவ்',               nameHi:'शेव',             price:60,  offerPct:0,  category:'BEARD' },
    { id:4, nameEn:'Hair Colour',   nameTa:'முடி நிறம்',          nameHi:'बाल रंग',         price:300, offerPct:10, category:'HAIR'  },
    { id:5, nameEn:'Facial',        nameTa:'முகப்பூச்சு',         nameHi:'फेशियल',          price:200, offerPct:0,  category:'SKIN'  },
    { id:6, nameEn:'Head Massage',  nameTa:'தலை மசாஜ்',           nameHi:'सिर मालिश',       price:100, offerPct:0,  category:'HAIR'  },
    { id:7, nameEn:'Kids Haircut',  nameTa:'குழந்தை முடி வெட்டு', nameHi:'बच्चों का कटाई',  price:60,  offerPct:0,  category:'HAIR'  },
    { id:8, nameEn:'Threading',     nameTa:'நூல் நீக்கம்',         nameHi:'थ्रेडिंग',        price:30,  offerPct:0,  category:'SKIN'  }
  ],
  stylists: [
    { id:1, name:'Vijayan', status:'FREE'  },
    { id:2, name:'Kumar',   status:'BUSY'  }
  ],
  offers: [
    { id:1, titleEn:'Festival Special', titleTa:'திருவிழா சிறப்பு', titleHi:'त्योहार विशेष',
      discountPct:20, description:'20% off all hair services this season', validUntil:'2026-12-31' }
  ],
  achievements: [
    { id:1, icon:'🏆', year:2018, titleEn:'Best Salon Salem',    description:'Recognized by Salem Business Council'      },
    { id:2, icon:'⭐', year:2020, titleEn:'500+ Happy Clients',  description:'Milestone of 500 regular clients'          },
    { id:3, icon:'🎖', year:2023, titleEn:'Google 4.8★ Rating',  description:'Top rating maintained on Google Maps'     }
  ],
  advertisements: [],
  events: [],
  bookings: [
    { id:1, clientName:'Rajesh Kumar',  contact:'9876543210', serviceNames:'Haircut, Beard Trim', bookingDate:'2026-08-28', bookingTime:'10:00', status:'PENDING',   notes:'' },
    { id:2, clientName:'Muthu Selvam',  contact:'8765432109', serviceNames:'Hair Colour',         bookingDate:'2026-08-28', bookingTime:'11:30', status:'CONFIRMED', notes:'' }
  ],
  feedback: [
    { id:1, clientName:'Anand', serviceRating:5, shopRating:4, workerRating:5, timingRating:4, overallRating:5, comments:'Excellent service!', createdAt:'2026-08-27' }
  ],
  inventory: [
    { id:1, itemName:'Gillette Razor',       category:'Razor',   currentCount:15, unit:'pcs',     lowThreshold:5 },
    { id:2, itemName:'Shampoo (Clinic Plus)', category:'Shampoo', currentCount:3,  unit:'bottles', lowThreshold:5 },
    { id:3, itemName:'Shaving Cream',         category:'Cream',   currentCount:8,  unit:'pcs',     lowThreshold:3 },
    { id:4, itemName:'Hair Colour (Black)',    category:'Colour',  currentCount:6,  unit:'packs',   lowThreshold:3 }
  ],
  paymentQr: {
    upiId: '6374402014@okbizaxis',
    qrImagePath: 'assets/uploads/qr/payment_qr.jpg',
    displayName: 'VIJAYAN SALOON'
  },
  dailyLog: [
    { id:1, serviceName:'Haircut',    quantity:2, paymentType:'CASH',   amount:160, logDate:'2026-08-28' },
    { id:2, serviceName:'Beard Trim', quantity:1, paymentType:'ONLINE', amount:50,  logDate:'2026-08-28' }
  ]
};

// Generic fetch wrapper
async function _call(endpoint, options = {}) {
  try {
    const token = localStorage.getItem('vj_token');
    const headers = {
      'Content-Type': 'application/json',
      ...(token ? { 'Authorization': 'Bearer ' + token } : {}),
      ...(options.headers || {})
    };
    const res = await fetch(API_BASE + endpoint, { ...options, headers });
    if (!res.ok) throw new Error('HTTP ' + res.status);
    const text = await res.text();
    return text ? JSON.parse(text) : {};
  } catch (e) {
    console.warn('API offline for', endpoint, '—', e.message);
    return null;
  }
}

// Multipart upload wrapper
async function _upload(endpoint, formData, method = 'POST') {
  try {
    const token = localStorage.getItem('vj_token');
    const res = await fetch(API_BASE + endpoint, {
      method,
      headers: token ? { 'Authorization': 'Bearer ' + token } : {},
      body: formData
    });
    if (!res.ok) throw new Error('HTTP ' + res.status);
    return await res.json();
  } catch (e) {
    console.warn('Upload failed for', endpoint, '—', e.message);
    return null;
  }
}

const API = {
  // ---- Public ----
  getShopInfo:        async () => (await _call('/shop/info'))         ?? DEMO.shopInfo,
  getShopStatus:      async () => (await _call('/shop/status'))       ?? DEMO.shopStatus,
  getServices:        async () => (await _call('/services'))          ?? DEMO.services,
  getOffers:          async () => (await _call('/offers'))            ?? DEMO.offers,
  getTodayEvents:     async () => (await _call('/events/today'))      ?? DEMO.events,
  getAchievements:    async () => (await _call('/achievements'))      ?? DEMO.achievements,
  getAdvertisements:  async () => (await _call('/advertisements'))    ?? DEMO.advertisements,
  getStylistsStatus:  async () => (await _call('/stylists/status'))   ?? DEMO.stylists,
  getPaymentQr:       async () => (await _call('/payment/qr'))        ?? DEMO.paymentQr,

  createBooking: async (data) =>
    (await _call('/bookings', { method:'POST', body:JSON.stringify(data) }))
    ?? { id: Math.floor(Math.random()*9000+1000), ...data, status:'PENDING' },

  submitFeedback: async (data) =>
    (await _call('/feedback', { method:'POST', body:JSON.stringify(data) }))
    ?? { id: Date.now(), ...data },

  // ---- Auth ----
  loginStylist: async (stylistCode, password) => {
    const res = await _call('/auth/stylist', { method:'POST', body:JSON.stringify({ stylistCode, password }) });
    if (res) return res;
    if (stylistCode === 'VJS001' && password === 'stylist1') return { token:'demo_token', name:'Vijayan', stylistId:1, stylistCode:'VJS001' };
    if (stylistCode === 'VJS002' && password === 'stylist2') return { token:'demo_token', name:'Kumar',   stylistId:2, stylistCode:'VJS002' };
    return null;
  },
  loginAdmin: async (adminCode, password) => {
    const res = await _call('/auth/admin', { method:'POST', body:JSON.stringify({ adminCode, password }) });
    if (res) return res;
    if (adminCode === 'VJADMIN' && password === 'vj@admin2024') return { token:'demo_token_admin' };
    return null;
  },
  changeAdminPassword: async (currentPassword, newPassword) =>
    (await _call('/auth/admin/password', { method:'PATCH', body:JSON.stringify({ currentPassword, newPassword }) })) ?? {},

  // ---- Stylist ----
  setShopStatus:   async (isOpen, note='') =>
    (await _call('/shop/status', { method:'PATCH', body:JSON.stringify({ isOpen, note }) })) ?? { isOpen, note },
  setStylistStatus: async (status) =>
    (await _call('/stylist/me/status', { method:'PATCH', body:JSON.stringify({ status }) })) ?? { status },
  getStylistBookings: async () => (await _call('/stylist/bookings')) ?? DEMO.bookings,
  updateBookingStatus: async (id, status) =>
    (await _call('/bookings/'+id+'/status', { method:'PATCH', body:JSON.stringify({ status }) })) ?? { id, status },
  deleteBooking: async (id) => (await _call('/bookings/'+id, { method:'DELETE' })) ?? {},
  addDailyLog:   async (data) =>
    (await _call('/daily-log', { method:'POST', body:JSON.stringify(data) })) ?? { id:Date.now(), ...data },
  getDailyLog:   async () => (await _call('/daily-log')) ?? DEMO.dailyLog,
  getInventory:  async () => (await _call('/inventory')) ?? DEMO.inventory,
  addInventory:  async (data) =>
    (await _call('/inventory', { method:'POST', body:JSON.stringify(data) })) ?? { id:Date.now(), ...data },
  updateInventory: async (id, data) =>
    (await _call('/inventory/'+id, { method:'PATCH', body:JSON.stringify(data) })) ?? { id, ...data },
  deleteInventory: async (id) => (await _call('/inventory/'+id, { method:'DELETE' })) ?? {},

  // ---- Admin ----
  getAllBookings:  async () => (await _call('/admin/bookings'))  ?? DEMO.bookings,
  getAllFeedback:  async () => (await _call('/admin/feedback'))  ?? DEMO.feedback,
  deleteFeedback: async (id) => (await _call('/admin/feedback/'+id, { method:'DELETE' })) ?? {},
  getAnalytics:   async (period) => (await _call('/admin/analytics?period='+period)) ?? null,
  updateShopSettings: async (data) =>
    (await _call('/admin/shop-settings', { method:'PUT', body:JSON.stringify(data) })) ?? data,

  // Generic admin CRUD factory
  crud: (resource) => ({
    list:   async ()       => (await _call('/admin/'+resource))                                             ?? [],
    create: async (data)   => (await _call('/admin/'+resource,      { method:'POST', body:JSON.stringify(data) })) ?? { id:Date.now(), ...data },
    update: async (id, d)  => (await _call('/admin/'+resource+'/'+id, { method:'PUT',  body:JSON.stringify(d)    })) ?? { id, ...d },
    delete: async (id)     => (await _call('/admin/'+resource+'/'+id, { method:'DELETE' }))                 ?? {}
  }),

  uploadAdvertisement: async (formData) => (await _upload('/admin/advertisements', formData)) ?? { id:Date.now() },
  uploadPaymentQr:     async (formData) => (await _upload('/admin/payment-qr', formData, 'PUT')) ?? {},
  resetStylistPassword: async (id, newPassword) =>
    (await _call('/admin/stylists/'+id+'/reset-password', { method:'PATCH', body:JSON.stringify({ newPassword }) })) ?? {}
};
