# Instruções de Uso e Conexão do Docker Compose

Este guia detalha como construir as imagens Docker do **Postgres** e do **Minio**, executar os containers configurados via **Docker Compose** e gerenciar suas operações.

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
cd APAE-ATENDIMENTO/backend/docker
```


### 2. Inicie os containers em modo destacado (background)

```bash
docker compose up -d
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

### 4. Execute a aplicação Spring

```bash
cd ../atendimento && mvn spring-boot:run -e -X
```


***

## Encerrando a Execução

### 1. Parar os containers

```bash
docker compose stop
```


### 2. Remover containers 

```bash
docker compose down 
```


***

## Serviços e Portas Utilizadas

| Serviço | Descrição | Porta |
| :-- | :-- | :-- |
| Postgres | Banco de dados relacional do backend | 5432 |
| MinIo | Armazenamento compatível com S3 | 9000 (API) / 9001 (Console) |


