FROM minio/minio:latest

# Define o diretório de armazenamento do MinIO
ENV MINIO_VOLUMES=/var/lib/minio/data

# Porta da API e Console já são padrão
EXPOSE 9000
EXPOSE 9001

# Comando para iniciar o servidor MinIO
ENTRYPOINT ["minio"]
CMD ["server", "/var/lib/minio/data", "--console-address", ":9001"]
