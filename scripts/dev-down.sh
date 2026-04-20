#!/usr/bin/env bash
set -euo pipefail
pkill -f "ngrok http" >/dev/null 2>&1 || true
pkill -f "python3 -m http.server 5500" >/dev/null 2>&1 || true
docker compose down
