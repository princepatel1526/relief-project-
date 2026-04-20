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
  attachProfileActions();
}

function attachProfileActions() {
  const topbarActions = document.querySelector('.topbar-actions');
  if (!topbarActions || document.getElementById('profile-settings-btn')) return;

  const btn = document.createElement('button');
  btn.id = 'profile-settings-btn';
  btn.className = 'btn-icon';
  btn.title = 'Profile & Password';
  btn.textContent = '⚙️';
  btn.addEventListener('click', openProfileModal);
  topbarActions.appendChild(btn);
}

function ensureProfileModal() {
  if (document.getElementById('profile-modal')) return;
  const modal = document.createElement('div');
  modal.id = 'profile-modal';
  modal.className = 'modal-overlay hidden';
  modal.innerHTML = `
    <div class="modal">
      <div class="modal-header">
        <h3>👤 Profile & Security</h3>
        <button class="btn-close" id="profile-close-btn">✕</button>
      </div>
      <div class="modal-body">
        <form id="profile-form">
          <div class="form-group">
            <label class="form-label">Full Name</label>
            <input class="form-control" type="text" name="fullName" required />
          </div>
          <div class="form-group">
            <label class="form-label">Email</label>
            <input class="form-control" type="email" name="email" required />
          </div>
          <div class="form-group">
            <label class="form-label">Phone</label>
            <input class="form-control" type="text" name="phone" />
          </div>
          <button class="btn btn-primary" type="submit">Save Profile</button>
        </form>
        <hr style="margin:18px 0;border:none;border-top:1px solid #e2e8f0;" />
        <form id="password-form">
          <div class="form-group">
            <label class="form-label">Current Password</label>
            <input class="form-control" type="password" name="currentPassword" required />
          </div>
          <div class="form-group">
            <label class="form-label">New Password</label>
            <input class="form-control" type="password" name="newPassword" minlength="8" required />
          </div>
          <button class="btn btn-secondary" type="submit">Change Password</button>
        </form>
      </div>
    </div>`;
  document.body.appendChild(modal);
  document.getElementById('profile-close-btn').addEventListener('click', closeProfileModal);

  document.getElementById('profile-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    const formData = Object.fromEntries(new FormData(e.target));
    try {
      const updated = await api.users.updateProfile(formData);
      const user = getUser() || {};
      user.fullName = updated.fullName;
      user.email = updated.email;
      localStorage.setItem('user', JSON.stringify(user));
      renderUserInfo();
      showToast('Profile updated successfully', 'success');
    } catch (err) {
      showToast(err.message || 'Failed to update profile', 'error');
    }
  });

  document.getElementById('password-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    const formData = Object.fromEntries(new FormData(e.target));
    try {
      await api.users.changePassword(formData);
      e.target.reset();
      showToast('Password changed successfully', 'success');
    } catch (err) {
      showToast(err.message || 'Failed to change password', 'error');
    }
  });
}

function openProfileModal() {
  ensureProfileModal();
  const user = getUser();
  if (user) {
    const form = document.getElementById('profile-form');
    form.fullName.value = user.fullName || '';
    form.email.value = user.email || '';
    form.phone.value = user.phone || '';
  }
  document.getElementById('profile-modal').classList.remove('hidden');
}

function closeProfileModal() {
  document.getElementById('profile-modal')?.classList.add('hidden');
}

export function canManageOperations() {
  return hasRole('ADMIN') || hasRole('COORDINATOR') || hasRole('VOLUNTEER');
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
      showToast(err.message || 'Invalid credentials', 'error');
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
      await api.auth.register({
        username: form.username.value,
        email: form.email.value,
        password: form.password.value,
        fullName: form.fullName.value,
        phone: form.phone.value,
        role: form.role.value,
      });
      showToast('Registration successful! Please login with your credentials.', 'success');
      setTimeout(() => { window.location.href = '/index.html'; }, 1200);
    } catch (err) {
      document.getElementById('register-error').textContent = err.message;
      document.getElementById('register-error').classList.remove('hidden');
    } finally {
      btn.disabled = false;
      btn.textContent = 'Create Account';
    }
  });
}
