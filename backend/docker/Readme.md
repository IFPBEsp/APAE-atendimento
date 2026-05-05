# Instruções de Uso e Conexão do Docker Compose

Este guia detalha como construir e executar a stack da aplicação via **Docker Compose**, incluindo **Frontend**, **Backend**, **Postgres** e **Minio**.

***

## Pré-requisitos

Antes de iniciar, garanta que os seguintes softwares estejam instalados e configurados em seu sistema:


| Requisito | Descrição | Link para Instalação |
| :-- | :-- | :-- |
| **Docker** | Plataforma para desenvolver, enviar e executar containers. | [https://docs.docker.com/get-docker/](https://docs.docker.com/get-docker/) |
| **Docker Compose** | Ferramenta para definir e executar apps Docker multi-container. | [https://docs.docker.com/compose/install/](https://docs.docker.com/compose/install/) |

### Verificação da Instalação

Execute no terminal para confirmar a instalação:

```bash
docker --version
docker compose version
```


***

## Conexão e Execução dos Containers

### 1. Navegue até o diretório do Compose

```bash
cd APAE-ATENDIMENTO
```


### 2. Inicie os containers em modo destacado (background)

```bash
docker compose --profile PROD up -d --build
```


### 3. Verifique se os containers estão em execução

```bash
docker container ps
```

Exemplo esperado:


| CONTAINER ID | IMAGE | STATUS |
| :-- | :-- | :-- |
| 896e5f8a5e1c | postgres:16 | Up 2 minutes |
| 98ecd313fc61 | minio/minio:latest | Up 2 minutes |

### 4. Verifique os serviços

```bash
docker compose ps
```


***

## Encerrando a Execução


```bash
docker compose down 
```


***

## Serviços e Portas Utilizadas

| Serviço | Descrição | Porta |
| :-- | :-- | :-- |
| Postgres | Banco de dados relacional do backend | 5432 |
| MinIo | Armazenamento compatível com S3 | 9000 (API) / 9001 (Console) |
| Backend | API Spring Boot | 8080 |
| Frontend | Aplicação Next.js | 80 |


