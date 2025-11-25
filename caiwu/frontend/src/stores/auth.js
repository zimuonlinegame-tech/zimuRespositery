import { defineStore } from 'pinia';
import api from '../services/api';

const AUTH_TOKEN_KEY = 'caiwu_token';
const AUTH_USER_KEY = 'caiwu_user';

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem(AUTH_TOKEN_KEY),
    user: JSON.parse(localStorage.getItem(AUTH_USER_KEY) || 'null'),
    loading: false
  }),
  getters: {
    isAuthenticated: (state) => Boolean(state.token)
  },
  actions: {
    setAuth(payload) {
      this.token = payload.token;
      this.user = payload.user;
      localStorage.setItem(AUTH_TOKEN_KEY, this.token);
      localStorage.setItem(AUTH_USER_KEY, JSON.stringify(this.user));
    },
    clearAuth() {
      this.token = null;
      this.user = null;
      localStorage.removeItem(AUTH_TOKEN_KEY);
      localStorage.removeItem(AUTH_USER_KEY);
    },
    async login(credentials) {
      this.loading = true;
      try {
        const response = await api.post('/api/auth/login', credentials);
        this.setAuth(response.data.data);
        return response.data;
      } finally {
        this.loading = false;
      }
    },
    async register(payload) {
      this.loading = true;
      try {
        const response = await api.post('/api/auth/register', payload);
        this.setAuth(response.data.data);
        return response.data;
      } finally {
        this.loading = false;
      }
    },
    async fetchProfile() {
      if (!this.token) {
        return null;
      }
      const response = await api.get('/api/auth/me');
      this.user = response.data.data;
      localStorage.setItem(AUTH_USER_KEY, JSON.stringify(this.user));
      return response.data.data;
    },
    logout() {
      this.clearAuth();
    }
  }
});
