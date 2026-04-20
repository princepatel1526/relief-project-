import { showToast } from './utils.js';

let stompClient = null;

export function connectWebSocket(onNotification) {
  const token = localStorage.getItem('accessToken');
  if (!token) return;

  const SockJS = window.SockJS;
  const Stomp = window.Stomp;
  if (!SockJS || !Stomp) {
    console.warn('SockJS/STOMP not loaded');
    return;
  }

  const socket = new SockJS('http://localhost:8080/api/ws');
  stompClient = Stomp.over(socket);
  stompClient.debug = null;

  stompClient.connect(
    { Authorization: `Bearer ${token}` },
    () => {
      const user = JSON.parse(localStorage.getItem('user') || '{}');

      stompClient.subscribe('/user/queue/notifications', (msg) => {
        const notification = JSON.parse(msg.body);
        onNotification?.(notification);
        showToast(notification.title, getToastType(notification.notificationType));
        updateNotifBadge();
      });

      stompClient.subscribe('/topic/disasters', (msg) => {
        console.log('New disaster broadcast:', msg.body);
        document.dispatchEvent(new CustomEvent('disaster-updated'));
      });
    },
    (err) => console.error('WebSocket error:', err)
  );
}

function getToastType(type) {
  const map = {
    DISASTER_ALERT: 'error',
    INVENTORY_ALERT: 'warning',
    ASSIGNMENT: 'info',
    DONATION: 'success',
  };
  return map[type] || 'info';
}

async function updateNotifBadge() {
  const badge = document.getElementById('notif-badge');
  if (!badge) return;
  try {
    const { count } = await import('./api.js').then(m => m.default.notifications.unreadCount());
    badge.textContent = count;
    badge.style.display = count > 0 ? 'block' : 'none';
  } catch {}
}

export function disconnectWebSocket() {
  stompClient?.disconnect();
}
