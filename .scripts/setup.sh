#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
ROOT_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"
ENV_FILE="$ROOT_DIR/.env"

cd "$ROOT_DIR"

if [ ! -f "$ENV_FILE" ]; then
  cp .env.example .env
  echo "[.env] Criado a partir de .env.example"
fi

set -a
# shellcheck disable=SC1090
. "$ENV_FILE"
set +a

MINIO_ROOT_USER="${MINIO_ROOT_USER:-atendimento_minio}"
MINIO_ROOT_PASSWORD="${MINIO_ROOT_PASSWORD:-atendimento_minio_123}"
BUCKET_NAME="${BUCKET_NAME:-apae-atendimento}"

echo "Subindo PostgreSQL e MinIO locais..."
docker compose up -d postgres-db minio

echo "Provisionando bucket '${BUCKET_NAME}' no MinIO..."
docker compose run --rm --entrypoint /bin/sh minio-setup -c "
  until mc alias set local http://minio:9000 \"${MINIO_ROOT_USER}\" \"${MINIO_ROOT_PASSWORD}\"; do
    echo 'Aguardando MinIO ficar disponivel...'
    sleep 2
  done
  mc mb --ignore-existing \"local/${BUCKET_NAME}\"
  mc anonymous set download \"local/${BUCKET_NAME}\"
"

echo "Criando contratos mockados de apae_geral e o schema gestao_escolar..."
docker compose --profile tools run --rm db-contract

echo "Aplicando migrations Flyway do schema atendimento..."
docker compose --profile tools run --rm --no-deps db-migrate

if [ "${1:-}" != "--no-seed" ]; then
  echo "Inserindo dados ficticios e idempotentes..."
  docker compose --profile tools run --rm --no-deps db-seed
fi

echo "Banco local do Atendimento preparado."
