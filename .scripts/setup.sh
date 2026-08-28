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

echo "Subindo PostgreSQL e MinIO locais..."
docker compose up -d postgres-db minio

echo "Criando contratos mockados de apae_geral e o schema gestao_escolar..."
docker compose --profile tools run --rm db-contract

echo "Aplicando migrations Flyway do schema atendimento..."
docker compose --profile tools run --rm db-migrate

if [ "${1:-}" != "--no-seed" ]; then
  echo "Inserindo dados ficticios e idempotentes..."
  docker compose --profile tools run --rm db-seed
fi

echo "Banco local do Atendimento preparado."
