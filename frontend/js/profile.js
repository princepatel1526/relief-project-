/**
 * profile.js — Self-injecting profile module.
 * Import this module from any dashboard page; it will:
 *  1. Inject a profile button next to the notification bell in the topbar.
 *  2. Inject a profile modal with "Edit Profile" and "Change Password" tabs.
 *  3. Wire up all API calls and show inline success/error messages.
 */

import api from './api.js';
import { getUser, saveSession } from './auth.js';

const LUCIDE_CANDIDATES = [
  'https://unpkg.com/lucide@0.469.0/dist/umd/lucide.min.js',
  'https://cdn.jsdelivr.net/npm/lucide@0.469.0/dist/umd/lucide.min.js'
];

async function ensureLucide() {
  if (window.lucide) return true;

  const tryLoad = (src) => new Promise((resolve, reject) => {
    const script = document.createElement('script');
    script.src = src;
    script.async = true;
    script.onload = () => resolve(true);
    script.onerror = reject;
    document.head.appendChild(script);
  });

  for (const src of LUCIDE_CANDIDATES) {
    try {
      await tryLoad(src);
      if (window.lucide) return true;
    } catch (_) {
      // try next source
    }
  }
  return false;
}

async function renderIcons() {
  const ok = await ensureLucide();
  if (!ok) return;

  const iconMap = {
    '🚨': 'shield-alert',
    '📊': 'layout-dashboard',
    '📈': 'line-chart',
    '🗺️': 'map',
    '📝': 'file-pen-line',
    '🌪️': 'cloud-lightning',
    '🆘': 'siren',
    '👥': 'users',
    '📦': 'package',
    '🙋': 'user-check',
    '💰': 'hand-coins',
    '🚪': 'log-out',
    '🔄': 'refresh-cw',
    '🔔': 'bell',
    '✅': 'check-circle-2',
    '🌤️': 'sun',
    '📍': 'locate-fixed',
    '⚠️': 'triangle-alert',
    '📋': 'list-checks'
  };

  const iconEmojiNodes = document.querySelectorAll('.icon, .logo-icon, .stat-icon, .empty-state .icon, .impact-icon, .success-icon');
  iconEmojiNodes.forEach(el => {
    const text = el.textContent.trim();
    if (iconMap[text]) el.innerHTML = `<i data-lucide="${iconMap[text]}"></i>`;
  });

  const stripIn = document.querySelectorAll('.topbar-title, .card-header h3, .mini-chart-card h4, .btn, .btn-icon, .map-hint');
  const emojiPattern = /\p{Extended_Pictographic}\uFE0F?/gu;
  stripIn.forEach(el => {
    el.childNodes.forEach(node => {
      if (node.nodeType === Node.TEXT_NODE) {
        node.textContent = node.textContent.replace(emojiPattern, '').replace(/\s{2,}/g, ' ').trimStart();
      }
    });
  });

  window.lucide.createIcons();
}

// ── Inject styles ─────────────────────────────────────────────────────────────
const style = document.createElement('style');
style.textContent = `
/* Profile button */
.profile-btn {
  display: flex; align-items: center; gap: 6px;
  padding: 6px 12px; border-radius: 8px; border: 1px solid var(--border);
  background: var(--surface); cursor: pointer; font-size: 13px;
  color: var(--text); font-weight: 500; transition: background var(--transition);
}
.profile-btn:hover { background: var(--bg); }

/* Modal overlay */
.profile-overlay {
  position: fixed; inset: 0; background: rgba(0,0,0,.5);
  display: flex; align-items: center; justify-content: center;
  z-index: 1000; opacity: 0; pointer-events: none;
  transition: opacity .2s ease;
}
.profile-overlay.open { opacity: 1; pointer-events: auto; }

/* Modal box */
.profile-modal {
  background: white; border-radius: 16px; width: 460px; max-width: 95vw;
  box-shadow: 0 25px 50px -12px rgba(0,0,0,.4);
  transform: translateY(-12px); transition: transform .2s ease;
  overflow: hidden;
}
.profile-overlay.open .profile-modal { transform: translateY(0); }

.profile-modal-header {
  display: flex; align-items: center; justify-content: space-between;
  padding: 20px 24px 0; border-bottom: 1px solid var(--border); padding-bottom: 16px;
}
.profile-modal-header h3 { font-size: 18px; font-weight: 700; color: var(--text); }
.profile-modal-close {
  background: none; border: none; cursor: pointer; font-size: 20px;
  color: var(--text-muted); line-height: 1; padding: 4px;
}
.profile-modal-close:hover { color: var(--text); }

/* Tabs */
.profile-tabs {
  display: flex; border-bottom: 1px solid var(--border);
  padding: 0 24px;
}
.profile-tab {
  padding: 12px 16px; font-size: 14px; font-weight: 500; cursor: pointer;
  border-bottom: 2px solid transparent; color: var(--text-muted);
  transition: color .15s, border-color .15s; background: none; border-top: none;
  border-left: none; border-right: none;
}
.profile-tab.active { color: var(--primary); border-bottom-color: var(--primary); }
.profile-tab:hover:not(.active) { color: var(--text); }

/* Tab panels */
.profile-panel { display: none; padding: 24px; }
.profile-panel.active { display: block; }

/* Inline alert inside modal */
.profile-alert {
  border-radius: 8px; padding: 10px 14px; font-size: 13px;
  font-weight: 500; margin-bottom: 16px; display: none;
}
.profile-alert.show { display: block; }
.profile-alert.success { background:#f0fdf4; color:#166534; border:1px solid #86efac; }
.profile-alert.error   { background:#fef2f2; color:#991b1b; border:1px solid #fca5a5; }

/* Reuse app form styles inside modal */
.profile-modal .form-group  { margin-bottom: 16px; }
.profile-modal .form-label  { display: block; font-size: 13px; font-weight: 600; color: var(--text); margin-bottom: 6px; }
.profile-modal .form-control { width: 100%; padding: 9px 12px; border: 1px solid var(--border); border-radius: 8px; font-size: 14px; outline: none; transition: border-color .15s; }
.profile-modal .form-control:focus { border-color: var(--primary); }

.profile-modal-footer {
  padding: 16px 24px; border-top: 1px solid var(--border);
  display: flex; justify-content: flex-end; gap: 10px;
}
`;
document.head.appendChild(style);

// ── Build HTML ────────────────────────────────────────────────────────────────
const overlay = document.createElement('div');
overlay.id = 'profile-overlay';
overlay.className = 'profile-overlay';
overlay.innerHTML = `
  <div class="profile-modal" role="dialog" aria-modal="true" aria-labelledby="profile-title">
    <div class="profile-modal-header">
      <h3 id="profile-title">My Profile</h3>
      <button class="profile-modal-close" id="profile-close" aria-label="Close">✕</button>
    </div>

    <div class="profile-tabs">
      <button class="profile-tab active" data-tab="edit">Edit Profile</button>
      <button class="profile-tab"        data-tab="password">Change Password</button>
    </div>

    <!-- Edit Profile Panel -->
    <div class="profile-panel active" id="panel-edit">
      <div class="profile-alert" id="edit-alert"></div>
      <div class="form-group">
        <label class="form-label">Full Name</label>
        <input class="form-control" id="edit-fullName" type="text" placeholder="Your name" required>
      </div>
      <div class="form-group">
        <label class="form-label">Email</label>
        <input class="form-control" id="edit-email" type="email" placeholder="you@example.com" required>
      </div>
      <div class="form-group">
        <label class="form-label">Phone</label>
        <input class="form-control" id="edit-phone" type="tel" placeholder="9876543210">
      </div>
      <div class="form-group">
        <label class="form-label">Username</label>
        <input class="form-control" id="edit-username" type="text" disabled style="background:#f8fafc; color:var(--text-muted);">
      </div>
      <div class="profile-modal-footer" style="padding:0; border:none; margin-top:8px;">
        <button class="btn btn-outline" id="edit-cancel">Cancel</button>
        <button class="btn btn-primary" id="edit-save">Save Changes</button>
      </div>
    </div>

    <!-- Change Password Panel -->
    <div class="profile-panel" id="panel-password">
      <div class="profile-alert" id="pw-alert"></div>
      <div class="form-group">
        <label class="form-label">Current Password</label>
        <input class="form-control" id="pw-current" type="password" placeholder="Enter current password">
      </div>
      <div class="form-group">
        <label class="form-label">New Password</label>
        <input class="form-control" id="pw-new" type="password" placeholder="Min 8 characters">
      </div>
      <div class="form-group">
        <label class="form-label">Confirm New Password</label>
        <input class="form-control" id="pw-confirm" type="password" placeholder="Repeat new password">
      </div>
      <div class="profile-modal-footer" style="padding:0; border:none; margin-top:8px;">
        <button class="btn btn-outline" id="pw-cancel">Cancel</button>
        <button class="btn btn-primary" id="pw-save">Change Password</button>
      </div>
    </div>
  </div>
`;
document.body.appendChild(overlay);

// ── Profile button in topbar ──────────────────────────────────────────────────
function injectProfileButton() {
  const topbarActions = document.querySelector('.topbar-actions');
  if (!topbarActions) return;

  const profileBtn = document.createElement('button');
  profileBtn.className = 'profile-btn';
  profileBtn.id = 'profile-open-btn';
  profileBtn.innerHTML = '<i data-lucide="user-circle-2"></i><span id="profile-btn-name">Profile</span>';
  profileBtn.title = 'Edit Profile';

  // Insert before .user-menu
  const userMenu = topbarActions.querySelector('.user-menu');
  if (userMenu) {
    topbarActions.insertBefore(profileBtn, userMenu);
  } else {
    topbarActions.appendChild(profileBtn);
  }

  profileBtn.addEventListener('click', openProfile);
}

// ── Open / Close ──────────────────────────────────────────────────────────────
function openProfile() {
  populateEditForm();
  switchTab('edit');
  overlay.classList.add('open');
}

function closeProfile() {
  overlay.classList.remove('open');
  clearAlerts();
}

overlay.addEventListener('click', (e) => {
  if (e.target === overlay) closeProfile();
});
document.getElementById('profile-close').addEventListener('click', closeProfile);
document.getElementById('edit-cancel').addEventListener('click', closeProfile);
document.getElementById('pw-cancel').addEventListener('click', closeProfile);

// Close on Escape
document.addEventListener('keydown', (e) => {
  if (e.key === 'Escape' && overlay.classList.contains('open')) closeProfile();
});

// ── Tabs ──────────────────────────────────────────────────────────────────────
document.querySelectorAll('.profile-tab').forEach(tab => {
  tab.addEventListener('click', () => switchTab(tab.dataset.tab));
});

function switchTab(name) {
  document.querySelectorAll('.profile-tab').forEach(t =>
    t.classList.toggle('active', t.dataset.tab === name));
  document.querySelectorAll('.profile-panel').forEach(p =>
    p.classList.toggle('active', p.id === `panel-${name}`));
  clearAlerts();
}

// ── Populate edit form from localStorage ─────────────────────────────────────
function populateEditForm() {
  const user = getUser();
  if (!user) return;
  document.getElementById('edit-fullName').value = user.fullName || '';
  document.getElementById('edit-email').value    = user.email    || '';
  document.getElementById('edit-phone').value    = user.phone    || '';
  document.getElementById('edit-username').value = user.username || '';
}

// ── Save profile ──────────────────────────────────────────────────────────────
document.getElementById('edit-save').addEventListener('click', async () => {
  const btn = document.getElementById('edit-save');
  const alert = document.getElementById('edit-alert');

  const fullName = document.getElementById('edit-fullName').value.trim();
  const email    = document.getElementById('edit-email').value.trim();
  const phone    = document.getElementById('edit-phone').value.trim();

  if (!fullName || !email) {
    showAlert(alert, 'error', 'Full name and email are required.');
    return;
  }

  btn.disabled = true;
  btn.textContent = 'Saving…';

  try {
    const updated = await api.users.updateMe({ fullName, email, phone });

    // Update localStorage so renderUserInfo() shows fresh data
    const stored = getUser() || {};
    stored.fullName = updated.fullName;
    stored.email    = updated.email;
    stored.phone    = updated.phone;
    localStorage.setItem('user', JSON.stringify(stored));

    // Refresh topbar display
    const nameEl   = document.getElementById('user-name');
    const avatarEl = document.getElementById('user-avatar');
    if (nameEl)   nameEl.textContent   = updated.fullName;
    if (avatarEl) avatarEl.textContent = updated.fullName?.charAt(0).toUpperCase() || '?';
    const profileBtnName = document.getElementById('profile-btn-name');
    if (profileBtnName) profileBtnName.textContent = updated.fullName?.split(' ')[0] || 'Profile';

    showAlert(alert, 'success', 'Profile updated successfully.');
  } catch (err) {
    showAlert(alert, 'error', err.message || 'Failed to update profile.');
  } finally {
    btn.disabled = false;
    btn.textContent = 'Save Changes';
  }
});

// ── Change password ───────────────────────────────────────────────────────────
document.getElementById('pw-save').addEventListener('click', async () => {
  const btn     = document.getElementById('pw-save');
  const alert   = document.getElementById('pw-alert');
  const current = document.getElementById('pw-current').value;
  const newPw   = document.getElementById('pw-new').value;
  const confirm = document.getElementById('pw-confirm').value;

  if (!current || !newPw || !confirm) {
    showAlert(alert, 'error', 'All password fields are required.');
    return;
  }
  if (newPw.length < 8) {
    showAlert(alert, 'error', 'New password must be at least 8 characters.');
    return;
  }
  if (newPw !== confirm) {
    showAlert(alert, 'error', 'New passwords do not match.');
    return;
  }

  btn.disabled = true;
  btn.textContent = 'Changing…';

  try {
    await api.users.changePassword({
      currentPassword: current,
      newPassword:     newPw,
      confirmPassword: confirm,
    });

    // Clear fields on success
    document.getElementById('pw-current').value = '';
    document.getElementById('pw-new').value     = '';
    document.getElementById('pw-confirm').value = '';

    showAlert(alert, 'success', 'Password changed. Please use the new password next time you log in.');
  } catch (err) {
    showAlert(alert, 'error', err.message || 'Failed to change password.');
  } finally {
    btn.disabled = false;
    btn.textContent = 'Change Password';
  }
});

// ── Helpers ───────────────────────────────────────────────────────────────────
function showAlert(el, type, msg) {
  el.textContent = msg;
  el.className   = `profile-alert show ${type}`;
}

function clearAlerts() {
  document.querySelectorAll('.profile-alert').forEach(el => {
    el.className = 'profile-alert';
    el.textContent = '';
  });
}

// ── Init ──────────────────────────────────────────────────────────────────────
// Wait for DOM then inject the button (this module may load before body is ready)
if (document.readyState === 'loading') {
  document.addEventListener('DOMContentLoaded', async () => {
    injectProfileButton();
    await renderIcons();
  });
} else {
  injectProfileButton();
  renderIcons();
}
