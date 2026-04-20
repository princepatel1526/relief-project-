import api from './api.js';

// =============================================
// SESSION HELPERS
// =============================================

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
    id:       authResponse.userId,
    username: authResponse.username,
    email:    authResponse.email,
    fullName: authResponse.fullName,
    roles:    Array.isArray(authResponse.roles)
                ? authResponse.roles
                : Array.from(authResponse.roles ?? []),
  }));
}

export function renderUserInfo() {
  const user = getUser();
  if (!user) return;
  const nameEl   = document.getElementById('user-name');
  const roleEl   = document.getElementById('user-role');
  const avatarEl = document.getElementById('user-avatar');
  if (nameEl)   nameEl.textContent   = user.fullName;
  if (roleEl)   roleEl.textContent   = user.roles?.[0]?.replace('ROLE_', '') || '';
  if (avatarEl) avatarEl.textContent = user.fullName?.charAt(0).toUpperCase() || '?';
}

// =============================================
// LOGIN PAGE
// =============================================

if (document.getElementById('login-form')) {
  const form     = document.getElementById('login-form');
  const errorDiv = document.getElementById('login-error');

  form.addEventListener('submit', async (e) => {
    e.preventDefault();
    const btn = form.querySelector('[type="submit"]');
    btn.disabled    = true;
    btn.textContent = 'Signing in…';
    errorDiv.classList.add('hidden');
    errorDiv.textContent = '';

    try {
      // Send as 'email' — backend principal() checks email first then username,
      // so typing either "admin" or "admin@example.com" works.
      const data = await api.auth.login({
        email:    form.usernameOrEmail.value.trim(),
        password: form.password.value,
      });

      saveSession(data);

      // Redirect based on role
      const roles = data.roles ?? [];
      const isAdminOrCoord = roles.some(r =>
        r === 'ROLE_ADMIN' || r === 'ROLE_COORDINATOR'
      );
      window.location.href = isAdminOrCoord
        ? '/admin-dashboard.html'
        : '/admin-dashboard.html'; // all roles go to dashboard for now

    } catch (err) {
      errorDiv.textContent = err.message || 'Login failed. Check your credentials.';
      errorDiv.classList.remove('hidden');
    } finally {
      btn.disabled    = false;
      btn.textContent = 'Sign In';
    }
  });
}

// =============================================
// REGISTER PAGE
// =============================================

if (document.getElementById('register-form')) {
  const form     = document.getElementById('register-form');
  const errorDiv = document.getElementById('register-error');

  form.addEventListener('submit', async (e) => {
    e.preventDefault();
    const btn = form.querySelector('[type="submit"]');
    btn.disabled    = true;
    btn.textContent = 'Creating account…';
    errorDiv.classList.add('hidden');
    errorDiv.textContent = '';

    if (form.password.value !== form.confirmPassword.value) {
      errorDiv.textContent = 'Passwords do not match.';
      errorDiv.classList.remove('hidden');
      btn.disabled    = false;
      btn.textContent = 'Create Account';
      return;
    }

    try {
      const data = await api.auth.register({
        username: form.username.value.trim(),
        email:    form.email.value.trim(),
        password: form.password.value,
        fullName: form.fullName.value.trim(),
        phone:    form.phone.value.trim(),
        role:     form.role.value,
      });

      saveSession(data);
      window.location.href = '/admin-dashboard.html';

    } catch (err) {
      errorDiv.textContent = err.message || 'Registration failed. Please try again.';
      errorDiv.classList.remove('hidden');
    } finally {
      btn.disabled    = false;
      btn.textContent = 'Create Account';
    }
  });
}
