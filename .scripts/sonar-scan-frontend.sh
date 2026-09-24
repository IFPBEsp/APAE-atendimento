#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
FRONTEND_DIR="$(cd "${SCRIPT_DIR}/../frontend/atendimento-app" && pwd)"

SONAR_HOST_URL="${SONAR_HOST_URL:-http://localhost:9501}"
SCANNER_IMAGE="sonarsource/sonar-scanner-cli:12.1.0.3233_8.0.1"

if [ -z "${SONAR_TOKEN:-}" ]; then
  echo "Erro: a variavel de ambiente SONAR_TOKEN precisa estar definida." >&2
  echo "Gere um token em ${SONAR_HOST_URL}/account/security e rode:" >&2
  echo "  SONAR_TOKEN=<seu_token> $0" >&2
  exit 1
fi

docker run --rm \
  --network=host \
  -e SONAR_HOST_URL="${SONAR_HOST_URL}" \
  -e SONAR_TOKEN="${SONAR_TOKEN}" \
  -v "${FRONTEND_DIR}:/usr/src" \
  "${SCANNER_IMAGE}"
