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

  function showLoginError(msg) {
    errorDiv.textContent = msg;
    errorDiv.classList.add('show');
    form.classList.add('shake');
    setTimeout(() => form.classList.remove('shake'), 500);
  }

  form.addEventListener('submit', async (e) => {
    e.preventDefault();
    const btn = document.getElementById('login-btn') || form.querySelector('[type="submit"]');
    btn.disabled    = true;
    btn.textContent = 'Signing in…';
    errorDiv.classList.remove('show');
    errorDiv.textContent = '';

    try {
      const data = await api.auth.login({
        email:    form.usernameOrEmail.value.trim(),
        password: form.password.value,
      });

      saveSession(data);
      window.location.href = '/admin-dashboard.html';

    } catch (err) {
      showLoginError(err.message || 'Invalid credentials. Please try again.');
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
  const form       = document.getElementById('register-form');
  const errorDiv   = document.getElementById('register-error');
  const successDiv = document.getElementById('register-success');

  form.addEventListener('submit', async (e) => {
    e.preventDefault();
    const btn = document.getElementById('register-btn') || form.querySelector('[type="submit"]');
    btn.disabled    = true;
    btn.textContent = 'Creating account…';
    errorDiv.classList.remove('show');
    errorDiv.textContent = '';

    if (form.password.value !== form.confirmPassword.value) {
      errorDiv.textContent = 'Passwords do not match.';
      errorDiv.classList.add('show');
      btn.disabled    = false;
      btn.textContent = 'Create Account';
      return;
    }

    try {
      await api.auth.register({
        username: form.username.value.trim(),
        email:    form.email.value.trim(),
        password: form.password.value,
        fullName: form.fullName.value.trim(),
        phone:    form.phone.value.trim(),
        role:     form.role.value,
      });

      // Show success banner, hide form, redirect to login
      form.style.display = 'none';
      successDiv.innerHTML =
        '✅ <strong>Account created successfully!</strong><br>' +
        '<span style="font-weight:400;opacity:.8">Redirecting to sign in page in 3 seconds…</span>';
      successDiv.classList.add('show');
      setTimeout(() => { window.location.href = '/index.html'; }, 3000);

    } catch (err) {
      errorDiv.textContent = err.message || 'Registration failed. Please try again.';
      errorDiv.classList.add('show');
    } finally {
      btn.disabled    = false;
      btn.textContent = 'Create Account';
    }
  });
}
