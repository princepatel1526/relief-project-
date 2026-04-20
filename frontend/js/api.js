// =============================================
// API CLIENT
// =============================================
const API_BASE = 'http://localhost:8080/api';

const api = {
  getToken: () => localStorage.getItem('accessToken'),

  headers(extra = {}) {
    const h = { 'Content-Type': 'application/json', ...extra };
    const token = this.getToken();
    if (token) h['Authorization'] = `Bearer ${token}`;
    return h;
  },

  async request(method, path, body = null, params = null) {
    let url = `${API_BASE}${path}`;
    if (params) {
      const qs = new URLSearchParams(Object.fromEntries(
        Object.entries(params).filter(([, v]) => v !== null && v !== undefined)
      )).toString();
      if (qs) url += '?' + qs;
    }

    const options = { method, headers: this.headers() };
    if (body) options.body = JSON.stringify(body);

    const res = await fetch(url, options);

    if (res.status === 401) {
      // Don't redirect on auth endpoints — let the caller handle the error
      if (!path.startsWith('/auth/')) {
        localStorage.clear();
        window.location.href = '/index.html';
        return;
      }
    }

    const data = res.headers.get('content-type')?.includes('application/json')
      ? await res.json()
      : await res.text();

    if (!res.ok) {
      const msg = data?.message || data?.error || 'An error occurred';
      throw new Error(msg);
    }

    return data;
  },

  get:    (path, params) => api.request('GET',    path, null, params),
  post:   (path, body)   => api.request('POST',   path, body),
  put:    (path, body)   => api.request('PUT',     path, body),
  patch:  (path, body)   => api.request('PATCH',   path, body),
  delete: (path)         => api.request('DELETE',  path),

  // Auth
  auth: {
    login:    (body) => api.post('/auth/login', body),
    register: (body) => api.post('/auth/register', body),
  },

  // Current user profile
  users: {
    me:             ()     => api.get('/users/me'),
    updateMe:       (body) => api.put('/users/me', body),
    changePassword: (body) => api.post('/users/me/change-password', body),
  },

  // Disasters
  disasters: {
    list:      (params) => api.get('/disasters', params),
    mine:      (params) => api.get('/disasters/my', params),
    get:       (id) => api.get(`/disasters/${id}`),
    create:    (body) => api.post('/disasters', body),
    update:    (id, body) => api.put(`/disasters/${id}`, body),
    setStatus: (id, status) => api.patch(`/disasters/${id}/status`, { status }),
    nearby:    (lat, lng, radiusKm = 50) => api.get('/disasters/nearby', { lat, lng, radiusKm }),
    types:     () => api.get('/disaster-types'),
  },

  // Volunteers
  volunteers: {
    list:       (params) => api.get('/volunteers', params),
    get:        (id) => api.get(`/volunteers/${id}`),
    register:   (body) => api.post('/volunteers/register', body),
    update:     (id, body) => api.put(`/volunteers/${id}`, body),
    setAvail:   (id, availability) => api.patch(`/volunteers/${id}/availability`, { availability }),
    nearby:     (lat, lng, skill, limit = 10) => api.get('/volunteers/nearby', { lat, lng, skill, limit }),
  },

  // Relief Requests
  requests: {
    list:      (params) => api.get('/requests', params),
    priority:  (limit = 20) => api.get('/requests/priority', { limit }),
    create:    (body) => api.post('/requests', body),
    setStatus: (id, status) => api.patch(`/requests/${id}/status`, { status }),
    timeline:  (id) => api.get(`/requests/${id}/timeline`),
  },

  // Inventory
  inventory: {
    list:          (params) => api.get('/inventory', params),
    lowStock:      () => api.get('/inventory/low-stock'),
    create:        (body) => api.post('/inventory', body),
    update:        (id, body) => api.put(`/inventory/${id}`, body),
    updateQty:     (id, body) => api.patch(`/inventory/${id}/quantity`, body),
    delete:        (id) => api.delete(`/inventory/${id}`),
  },

  // Assignments
  assignments: {
    create:     (body) => api.post('/assignments', body),
    byDisaster: (disasterId, params) => api.get(`/assignments/disaster/${disasterId}`, params),
    mine:       (params) => api.get('/assignments/me', params),
    setStatus:  (id, status, hoursLogged) => api.patch(`/assignments/${id}/status`, { status, hoursLogged }),
  },

  // Donations / Payments
  payments: {
    createOrder: (body) => api.post('/payments/create-order', body),
    verify:      (body) => api.post('/payments/verify', body),
  },
  donations: {
    list: (params) => api.get('/donations', params),
  },

  // Notifications
  notifications: {
    list:        (params) => api.get('/notifications', params),
    unreadCount: () => api.get('/notifications/unread-count'),
    markAllRead: () => api.patch('/notifications/mark-all-read'),
  },

  // Dashboard
  dashboard: {
    stats:     () => api.get('/admin/stats'),
    analytics: () => api.get('/admin/stats'), // stats endpoint now includes analytics data
  },

  // Disaster map data (public)
  map: {
    active:  () => api.get('/disasters', { status: 'ACTIVE', size: 200, page: 0 }),
    all:     (params) => api.get('/disasters', { size: 200, page: 0, ...params }),
    nearby:  (lat, lng, radiusKm = 100) => api.get('/disasters/nearby', { lat, lng, radiusKm }),
  },

  // News / Live incident feed
  news: {
    list:    (params) => api.get('/news', params),
    get:     (id) => api.get(`/news/${id}`),
    create:  (body) => api.post('/news', body),
    update:  (id, body) => api.put(`/news/${id}`, body),
    delete:  (id) => api.delete(`/news/${id}`),
  },
};

export default api;
