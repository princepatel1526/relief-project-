export function showToast(message, type = 'info') {
  const container = document.getElementById('toast-container');
  if (!container) return;

  const icons = { success: 'Success', error: 'Error', warning: 'Warning', info: 'Info' };
  const toast = document.createElement('div');
  toast.className = `toast ${type}`;
  toast.innerHTML = `<span>${icons[type] || ''}</span> ${message}`;
  container.appendChild(toast);
  setTimeout(() => toast.remove(), 3200);
}

export function formatDate(dateStr) {
  if (!dateStr) return '-';
  return new Date(dateStr).toLocaleDateString('en-IN', {
    day: '2-digit', month: 'short', year: 'numeric', hour: '2-digit', minute: '2-digit'
  });
}

export function formatCurrency(amount) {
  if (amount == null) return '-';
  return '₹' + Number(amount).toLocaleString('en-IN', { minimumFractionDigits: 2 });
}

export function formatCurrencyCompactIndian(amount) {
  if (amount == null || Number.isNaN(Number(amount))) return '-';
  const value = Number(amount);
  const abs = Math.abs(value);
  const sign = value < 0 ? '-' : '';

  if (abs >= 10000000) { // Crore
    const crore = abs / 10000000;
    const digits = crore >= 100 ? 0 : crore >= 10 ? 1 : 2;
    return `${sign}₹${Number(crore.toFixed(digits)).toLocaleString('en-IN')}Cr`;
  }

  if (abs >= 100000) { // Lakh
    const lakh = abs / 100000;
    const digits = lakh >= 100 ? 0 : lakh >= 10 ? 1 : 2;
    return `${sign}₹${Number(lakh.toFixed(digits)).toLocaleString('en-IN')}L`;
  }

  return `${sign}₹${abs.toLocaleString('en-IN')}`;
}

export function severityBadge(severity) {
  const map = {
    CRITICAL: 'badge-critical', HIGH: 'badge-high',
    MEDIUM: 'badge-medium', LOW: 'badge-low',
  };
  return `<span class="badge ${map[severity] || 'badge-gray'}">${severity}</span>`;
}

export function statusBadge(status) {
  const map = {
    // Disaster statuses
    ACTIVE: 'badge-active', REPORTED: 'badge-warning',
    CONTAINED: 'badge-info', RESOLVED: 'badge-success', CLOSED: 'badge-gray',
    // Request statuses (8-stage workflow)
    SUBMITTED: 'badge-info',
    PENDING_VERIFICATION: 'badge-warning',
    VERIFIED: 'badge-info',
    PENDING: 'badge-pending',
    ASSIGNED: 'badge-info',
    EN_ROUTE: 'badge-active',
    IN_PROGRESS: 'badge-active',
    FULFILLED: 'badge-fulfilled',
    REJECTED: 'badge-danger',
    CANCELLED: 'badge-gray',
    // Payment / other
    CAPTURED: 'badge-success', FAILED: 'badge-danger',
    AVAILABLE: 'badge-success', BUSY: 'badge-warning',
    COMPLETED: 'badge-success',
  };
  const label = status?.replace(/_/g, ' ') || status;
  return `<span class="badge ${map[status] || 'badge-gray'}">${label}</span>`;
}

export function urgencyBar(level) {
  let dots = '';
  for (let i = 1; i <= 5; i++) {
    dots += `<div class="urgency-dot ${i <= level ? `filled-${level}` : ''}"></div>`;
  }
  return `<div class="urgency-bar" title="Urgency: ${level}/5">${dots}</div>`;
}

export function buildPagination(container, currentPage, totalPages, onChange) {
  container.innerHTML = '';
  if (totalPages <= 1) return;
  const prev = document.createElement('button');
  prev.className = 'page-btn';
  prev.textContent = '←';
  prev.disabled = currentPage === 0;
  prev.onclick = () => onChange(currentPage - 1);
  container.appendChild(prev);

  const start = Math.max(0, currentPage - 2);
  const end   = Math.min(totalPages - 1, currentPage + 2);
  for (let i = start; i <= end; i++) {
    const btn = document.createElement('button');
    btn.className = `page-btn${i === currentPage ? ' active' : ''}`;
    btn.textContent = i + 1;
    btn.onclick = () => onChange(i);
    container.appendChild(btn);
  }

  const next = document.createElement('button');
  next.className = 'page-btn';
  next.textContent = '→';
  next.disabled = currentPage >= totalPages - 1;
  next.onclick = () => onChange(currentPage + 1);
  container.appendChild(next);
}

export function showModal(id) {
  document.getElementById(id)?.classList.remove('hidden');
}

export function hideModal(id) {
  document.getElementById(id)?.classList.add('hidden');
}

export function loading(container, msg = 'Loading...') {
  container.innerHTML = `
    <div class="loading-overlay">
      <div class="spinner"></div>
      <p>${msg}</p>
    </div>`;
}

export function empty(container, title = 'No data', msg = '') {
  container.innerHTML = `
    <div class="empty-state">
      <div class="icon"><i data-lucide="list-checks"></i></div>
      <h3>${title}</h3>
      <p>${msg}</p>
    </div>`;
  if (window.lucide) window.lucide.createIcons();
}
