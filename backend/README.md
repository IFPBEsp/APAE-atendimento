# ☕ Back-end - Sistema de Atendimento da APAE

Este diretório contém a API RESTful do Sistema de Atendimento da APAE, desenvolvida em **Java 21** utilizando o framework **Spring Boot 3.x**. A API gerencia de forma centralizada as regras de negócio, persistência de dados em banco relacional, armazenamento de arquivos digitais em nuvem (Object Storage) e segurança baseada em tokens.

---

## Tecnologias e Dependências Principais

A infraestrutura e as bibliotecas utilizadas no projeto baseiam-se em:
- **Linguagem:** Java 21
- **Framework Core:** Spring Boot 3.5.6 (Web, Data JPA, Validation, Actuator)
- **Banco de Dados:** PostgreSQL (Desenvolvimento/Produção) e H2 Database (Ambiente de Testes rápido)
- **Armazenamento de Arquivos:** MinIO
- **Autenticação & Segurança:** Autenticação robusta própria e verificação de tokens JWT nativa via **Spring Security**
- **Cache:** Caffeine Cache
- **Testes:** JUnit 5 e Testcontainers

---

## Pré-requisitos

**Antes de executar o projeto localmente, verifique se as seguintes ferramentas estão instaladas:**

- **Java JDK:** versão mínima 21
- **Maven:** versão mínima 3.9.x (ou utilize o Maven Wrapper `./mvnw` embutido)
- **Docker e Docker Compose:** Essencial para levantar o banco e o storage localmente
- **Git**: para clonar o repositório

Você pode verificar as versões instaladas com os comandos:

```bash
java -version
```
```bash
mvn -v
```
```bash
docker compose version
```
```bash
git --version
```

## Configuração do Ambiente

1. Clone o repositório

```bash
git clone https://github.com/IFPBEsp/APAE-atendimento.git
```

2. Acesse a branch dev

```bash
git checkout dev
```

3. Acesse o diretório do backend

```bash
cd backend/atendimento
```

4. Instale as dependências

O Maven gerencia as dependências automaticamente. Para garantir que todas sejam baixadas corretamente, execute:

```bash
mvn clean install
```

## Perfis e Configurações

O projeto utiliza perfis do Spring Boot para diferenciar ambientes:

* dev → Desenvolvimento com PostgreSQL local
* test → Testes com H2 em memória
* prod → Produção (configuração futura)

Os arquivos correspondentes são:

- application-dev.properties
- application-test.properties
- application-prod.properties

O parâmetro -Dprofile define qual perfil será carregado ao iniciar o backend.

## Variáveis de ambiente (modo dev)

O perfil `dev` carrega configurações a partir de dois arquivos situados em `backend/docker/`:

- `docker-compose.properties` — configurações do banco de dados (versionado)
- `local-secrets.properties` — segredos locais **(não versionado, não commitar)**
  Copie o arquivo de exemplo e preencha com seus valores:

```bash
cp backend/docker/local-secrets.properties.example backend/docker/local-secrets.properties
```

| Variável | Descrição | Padrão (dev) |
|---|---|---|
| `JWT_SECRET` | Chave secreta em Base64 (mínimo 256 bits / 32 bytes) | — |
| `JWT_EXPIRATION` | Tempo de expiração do token em minutos | `30` |
| `JWT_COOKIE_SECURE` | Cookie somente via HTTPS | `false` |
| `CORS_ALLOWED_ORIGINS` | Origins permitidas pelo CORS | `http://localhost:3000` |
| `MINIO_ENDPOINT` | URL do servidor MinIO | `http://localhost:9000` |
| `MINIO_ROOT_USER` | Usuário do MinIO | `minio` |
| `MINIO_ROOT_PASSWORD` | Senha do MinIO | — |
| `BUCKET_NAME` | Nome do bucket de armazenamento | `apae` |
 
---


## Executando o Backend 

> “O parâmetro -Dprofile define qual perfil do Spring Boot será carregado (dev, test ou prod).

### Localmente em modo Test

Não requer nenhuma configuração externa. Utiliza banco H2 em memória e autenticação mock com usuário fixo (`44444444-4444-4444-4444-444444444444`).

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=test
```

### Localmente em modo Dev

Requer PostgreSQL e MinIO em execução. Utilize o Docker Compose disponível em `backend/docker/` e certifique-se de ter preenchido o `local-secrets.properties`.

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Via Docker Compose (stack completa)

Para subir toda a stack (frontend, backend, PostgreSQL e MinIO) com Docker:

```bash
cd APAE-atendimento
docker compose --profile PROD up -d --build
```

Consulte `backend/docker/Readme.md` para mais detalhes sobre o Docker Compose e as portas utilizadas.

Após a inicialização, o backend estará acessível em:

```
http://localhost:8080
```
 
---

## Endpoints Disponíveis

> Todos os endpoints (exceto `/auth/login`, `/auth/logout` e `/actuator/health`) exigem autenticação via cookie JWT.

### Autenticação — `/auth`

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/auth/login` | Autentica o profissional e define o cookie JWT |
| POST | `/auth/logout` | Invalida o token e limpa o cookie |
| GET | `/auth/me` | Verifica se o usuário atual está autenticado |

### Agendamentos — `/agendamento`

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/agendamento` | Cria um novo agendamento |
| GET | `/agendamento` | Lista agendamentos do profissional agrupados por dia |
| DELETE | `/agendamento/{pacienteId}/{agendamentoId}` | Remove um agendamento |

### Atendimentos — `/atendimentos`

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/atendimentos` | Registra um novo atendimento |
| GET | `/atendimentos/{pacienteId}` | Lista atendimentos do paciente agrupados por mês |
| PUT | `/atendimentos/{atendimentoId}` | Edita os tópicos de um atendimento |
| DELETE | `/atendimentos/{pacienteId}/{atendimentoId}` | Remove um atendimento |

### Pacientes — `/pacientes`

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/pacientes/{id}` | Busca paciente por ID |
| GET | `/pacientes/{id}/nome-completo` | Retorna o nome completo do paciente |
| GET | `/pacientes/search` | Busca paginada por nome, CPF ou cidade |
| POST | `/pacientes/{pacienteId}` | Adiciona ou substitui a foto do paciente |
| GET | `/pacientes/dropdown` | Lista pacientes ativos para uso em dropdowns |

### Profissionais de Saúde — `/profissionais`

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/profissionais` | Retorna os dados do profissional autenticado |
| GET | `/profissionais/pacientes` | Lista os pacientes vinculados ao profissional |
| GET | `/profissionais/primeiro-nome` | Retorna o primeiro nome do profissional |
| GET | `/profissionais/pacientes-option` | Lista pacientes para seleção (id + nome) |
| GET | `/profissionais/dropdown` | Lista profissionais ativos para uso em dropdowns |

### Arquivos — `/arquivo`

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/arquivo` | Faz upload de um arquivo (`multipart/form-data`: `file` + `metadata`) |
| GET | `/arquivo/{pacienteId}/{tipoId}` | Lista arquivos do paciente por tipo (1 = Anexo, 2 = Relatório) |
| GET | `/arquivo/date/{pacienteId}/{tipoId}/{data}` | Lista arquivos do paciente por tipo e data (`dd-MM-yyyy`) |
| DELETE | `/arquivo/delete?objectName=` | Remove um arquivo do storage e do banco |

### URLs Pré-assinadas — `/pressigned`

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/pressigned?objectName=` | Gera uma URL pré-assinada para acesso temporário a um arquivo no storage |

### Monitoramento — `/actuator`

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/actuator/health` | Verifica a saúde da aplicação (acesso público) |
 
---

## Executando os Testes

O projeto possui testes unitários, de integração e de repositório, todos executados com o perfil `test` (H2 em memória).

```bash
mvn test
```

