# Instruções de Uso e Conexão do Docker Compose

Este guia detalhado descreve o processo para construir as imagens Docker do **Postgres** e do **Minio**, e como executar seus respectivos containers configurados usando o **Docker Compose**. 

---

## Pré-requisitos

Antes de iniciar a execução dos containers, certifique-se de que os seguintes softwares estão instalados e configurados em seu sistema:

| Requisito | Descrição | Link para Instalação |
| --- | --- | --- |
| **Docker** | Plataforma para desenvolver, enviar e executar aplicativos em containers. | [https://docs.docker.com/get-docker/](https://docs.docker.com/get-docker/) |
| **Docker Compose** | Ferramenta para definir e executar aplicativos Docker multi-container. | [https://docs.docker.com/compose/install/](https://docs.docker.com/compose/install/) |

### Verificação da Instalação

Para confirmar se o Docker e o Docker Compose estão corretamente instalados, execute os comandos abaixo em seu terminal:

```bash
docker --version
docker compose version
```

---

## Conexão e Execução

### 1. Execução dos Containers

Para iniciar os containers em segundo plano (modo *detached*), utilize o comando `docker-compose up -d`.

```bash
docker-compose up -d 
```

### 2. Verificação do Status

Após a execução, verifique se os containers foram iniciados corretamente e estão em execução. O comando `docker ps` listará todos os containers ativos:

```bash
docker ps
```

**Exemplo de Saída Esperada:**

| CONTAINER ID | IMAGE | STATUS |
| --- | --- | --- |
| `896e5f8a5e1c` | `postgres:16` | `Up 2 minutes` |
| `98ecd313fc61` | `minio/minio:RELEASE.2024-09-22T00-33-43Z` | `Up 2 minutes` |

Se a coluna `STATUS` mostrar `Up` seguido de um tempo, isso indica que os containers estão funcionando conforme o esperado.

---
