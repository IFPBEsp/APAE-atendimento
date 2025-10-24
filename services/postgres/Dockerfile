FROM postgres:18


# Define o novo diretório de dados do PostgreSQL 18
ENV PGDATA=/var/lib/postgresql/data

# Copia configs personalizadas
COPY ./config/postgresql.conf /etc/postgresql/postgresql.conf
COPY ./config/pg_hba.conf /etc/postgresql/pg_hba.conf

# Remove o entrypoint personalizado e usa o oficial do PostgreSQL
CMD ["postgres", "-c", "config_file=/etc/postgresql/postgresql.conf", "-c", "hba_file=/etc/postgresql/pg_hba.conf"]