// src/services/api.js
import axios from 'axios';

const API = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: { 'Content-Type': 'application/json' },
});

// Attach JWT token to every request
API.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

// Handle 401 globally
API.interceptors.response.use(
  (res) => res,
  (err) => {
    if (err.response?.status === 401) {
      localStorage.clear();
      window.location.href = '/login';
    }
    return Promise.reject(err);
  }
);

// ─── AUTH ───────────────────────────────────────────
export const loginUser    = (data) => API.post('/auth/login', data);
export const registerUser = (data) => API.post('/auth/register', data);

// ─── SEARCH ─────────────────────────────────────────
export const searchTravel = (params) => API.get('/search', { params });
  // params: { from, to, date, type? }

// ─── USER ───────────────────────────────────────────
export const getUserProfile   = ()     => API.get('/users/profile');
export const updateUserProfile = (data) => API.put('/users/profile', data);
export const getSearchHistory  = ()    => API.get('/users/history');

// ─── FEEDBACK ───────────────────────────────────────
export const submitFeedback  = (data) => API.post('/feedback', data);
export const getAllFeedback   = ()     => API.get('/feedback');

// ─── ADMIN ──────────────────────────────────────────
export const getAllUsers     = ()      => API.get('/admin/users');
export const getDashboardStats = ()   => API.get('/admin/stats');
export const deleteUser     = (id)    => API.delete(`/admin/users/${id}`);

export default API;
