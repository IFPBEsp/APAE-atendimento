# Test-only MinIO image, built from the official RELEASE.2025-10-15T17-29-55Z commit.
FROM golang:1.24.8-bookworm@sha256:4ed690d6649d63c312b99a6120025ec79ce3b542968a37da53d6236c7c61a848 AS builder

ENV CGO_ENABLED=0 GOTOOLCHAIN=local GOMAXPROCS=2
RUN go install -p=2 github.com/minio/minio@9e49d5e7a648f00e26f2246f4dc28e6b07f8c84a

FROM scratch
COPY --from=builder /etc/ssl/certs/ca-certificates.crt /etc/ssl/certs/ca-certificates.crt
COPY --from=builder /go/bin/minio /minio
EXPOSE 9000
ENTRYPOINT ["/minio"]
