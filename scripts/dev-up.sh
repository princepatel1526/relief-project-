#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

BACKEND_PORT="${BACKEND_PORT:-8080}"
FRONTEND_PORT="${FRONTEND_PORT:-5500}"

require() {
  command -v "$1" >/dev/null 2>&1 || { echo "[ERROR] Missing required command: $1"; exit 1; }
}

require docker
require ngrok
require python3

cleanup() {
  echo "\n[INFO] Shutting down local development stack..."
  [[ -n "${NGROK_PID:-}" ]] && kill "$NGROK_PID" >/dev/null 2>&1 || true
  [[ -n "${FE_PID:-}" ]] && kill "$FE_PID" >/dev/null 2>&1 || true
  docker compose down >/dev/null 2>&1 || true
  echo "[INFO] Shutdown complete."
}
trap cleanup EXIT INT TERM

echo "[INFO] Starting MySQL + Backend via docker compose..."
docker compose up -d mysql backend

echo "[INFO] Starting frontend static server on http://localhost:${FRONTEND_PORT} ..."
python3 -m http.server "$FRONTEND_PORT" --directory frontend >/tmp/relief-frontend.log 2>&1 &
FE_PID=$!

echo "[INFO] Starting ngrok tunnel for backend port ${BACKEND_PORT} ..."
ngrok http "$BACKEND_PORT" --log=stdout >/tmp/relief-ngrok.log 2>&1 &
NGROK_PID=$!

PUBLIC_URL=""
for _ in {1..20}; do
  sleep 1
  PUBLIC_URL="$(python3 - <<'PY'
import json, urllib.request
try:
    data = json.loads(urllib.request.urlopen('http://127.0.0.1:4040/api/tunnels', timeout=1).read().decode())
    tunnels = data.get('tunnels', [])
    https = next((t.get('public_url') for t in tunnels if t.get('proto') == 'https'), '')
    print(https)
except Exception:
    print('')
PY
)"
  [[ -n "$PUBLIC_URL" ]] && break
done

echo ""
echo "============================================================"
echo " Disaster Relief local stack is running"
echo "============================================================"
echo "Frontend : http://localhost:${FRONTEND_PORT}/index.html"
echo "Backend  : http://localhost:${BACKEND_PORT}/api"
if [[ -n "$PUBLIC_URL" ]]; then
  echo "ngrok    : ${PUBLIC_URL}"
  echo "Webhook  : ${PUBLIC_URL}/api/payments/webhook"
  echo ""
  echo "[ACTION] Update Razorpay webhook URL to:"
  echo "         ${PUBLIC_URL}/api/payments/webhook"
else
  echo "[WARN] ngrok URL not detected yet. Check: http://127.0.0.1:4040/status"
fi

echo ""
echo "Press Ctrl+C to stop backend, frontend server, and ngrok together."

tail -f /tmp/relief-ngrok.log
