import api from './api.js';
import { showToast } from './utils.js';

export function getUser() {
  try {
    return JSON.parse(localStorage.getItem('user'));
  } catch { return null; }
}

export function isLoggedIn() {
  return !!localStorage.getItem('accessToken');
}

export function hasRole(role) {
  const user = getUser();
  return user?.roles?.includes(`ROLE_${role.toUpperCase()}`);
}

export function requireAuth() {
  if (!isLoggedIn()) {
    window.location.href = '/index.html';
    return false;
  }
  return true;
}

export function logout() {
  localStorage.clear();
  window.location.href = '/index.html';
}

export function saveSession(authResponse) {
  localStorage.setItem('accessToken', authResponse.accessToken);
  localStorage.setItem('refreshToken', authResponse.refreshToken);
  localStorage.setItem('user', JSON.stringify({
    id: authResponse.userId,
    username: authResponse.username,
    email: authResponse.email,
    fullName: authResponse.fullName,
    roles: authResponse.roles,
  }));
}

export function renderUserInfo() {
  const user = getUser();
  if (!user) return;
  const nameEl = document.getElementById('user-name');
  const roleEl = document.getElementById('user-role');
  const avatarEl = document.getElementById('user-avatar');
  if (nameEl) nameEl.textContent = user.fullName;
  if (roleEl) roleEl.textContent = user.roles?.[0]?.replace('ROLE_', '') || '';
  if (avatarEl) avatarEl.textContent = user.fullName.charAt(0).toUpperCase();
}

// Login page handler
if (document.getElementById('login-form')) {
  const form = document.getElementById('login-form');
  form.addEventListener('submit', async (e) => {
    e.preventDefault();
    const btn = form.querySelector('[type="submit"]');
    btn.disabled = true;
    btn.textContent = 'Signing in...';

    try {
      const response = await fetch('http://localhost:8080/api/auth/login', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          email: form.usernameOrEmail.value,
          password: form.password.value,
        }),
      });

      const data = await response.json();
      if (!response.ok) {
        throw new Error(data?.message || 'Login failed');
      }
      saveSession(data);
      window.location.href = '/admin-dashboard.html';
    } catch (err) {
      document.getElementById('login-error').textContent = err.message;
      document.getElementById('login-error').classList.remove('hidden');
    } finally {
      btn.disabled = false;
      btn.textContent = 'Sign In';
    }
  });
}

// Register page handler
if (document.getElementById('register-form')) {
  const form = document.getElementById('register-form');
  form.addEventListener('submit', async (e) => {
    e.preventDefault();
    const btn = form.querySelector('[type="submit"]');
    btn.disabled = true;
    btn.textContent = 'Creating account...';

    if (form.password.value !== form.confirmPassword.value) {
      document.getElementById('register-error').textContent = 'Passwords do not match';
      document.getElementById('register-error').classList.remove('hidden');
      btn.disabled = false;
      btn.textContent = 'Create Account';
      return;
    }

    try {
      const data = await api.auth.register({
        username: form.username.value,
        email: form.email.value,
        password: form.password.value,
        fullName: form.fullName.value,
        phone: form.phone.value,
        role: form.role.value,
      });
      saveSession(data);
      window.location.href = '/admin-dashboard.html';
    } catch (err) {
      document.getElementById('register-error').textContent = err.message;
      document.getElementById('register-error').classList.remove('hidden');
    } finally {
      btn.disabled = false;
      btn.textContent = 'Create Account';
    }
  });
}
