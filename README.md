<div align="center">
  <img src="https://github.com/user-attachments/assets/d4cb7e8e-abbb-41ea-8a3b-602bc272c360" width="220" alt="Logo APAE" />

  <h1>APAE Atendimento</h1>

  <p>
    <strong>Sistema completo para gestão de atendimentos da APAE</strong>
  </p>

  <p>
    <img src="https://img.shields.io/badge/Status-Em%20Desenvolvimento-yellow?style=for-the-badge" alt="Status" height="25"/>
    <img src="https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white" alt="Spring Boot" height="25"/>
    <img src="https://img.shields.io/badge/React-20232A?style=for-the-badge&logo=react&logoColor=61DAFB" alt="React" height="25"/>
    <img src="https://img.shields.io/badge/Flyway-CC0200?style=for-the-badge&logo=flyway&logoColor=white" alt="Flyway" height="25"/>
    <img src="https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white" alt="Docker" height="25"/>
    <img src="https://img.shields.io/badge/pnpm-F69220?style=for-the-badge&logo=pnpm&logoColor=white" alt="pnpm" height="25"/>
    <img src="https://img.shields.io/badge/Git-F05032?style=for-the-badge&logo=git&logoColor=white" alt="Git" height="25"/>
  </p>

  <br />

  <a href="https://github.com/IFPBEsp/APAE-atendimento/commits/dev">
    <img alt="GitHub last commit" src="https://img.shields.io/github/last-commit/IFPBEsp/APAE-atendimento/dev?style=for-the-badge" height="22">
  </a>
  <a href="https://github.com/IFPBEsp/APAE-atendimento/issues">
    <img alt="GitHub issues" src="https://img.shields.io/github/issues/IFPBEsp/APAE-atendimento?style=for-the-badge" height="22">
  </a>
  <a href="https://github.com/IFPBEsp/APAE-atendimento/blob/dev/LICENSE">
    <img alt="GitHub license" src="https://img.shields.io/github/license/IFPBEsp/APAE-atendimento?style=for-the-badge" height="22">
  </a>
</div>

<br />

## Sumário

- [Sobre o Projeto](#-sobre-o-projeto)
- [Stack Tecnológica](#-stack-tecnológica)
- [Estrutura do Projeto](#-estrutura-do-projeto)
- [Quick Start](#-quick-start)
- [Infraestrutura e Deploy](#-infraestrutura-e-deploy)
- [Integração com o Sistema Geral (apae-geral)](#-integração-com-o-sistema-geral-apae-geral)
- [Git Flow](#-git-flow)
- [Conventional Commits](#-conventional-commits)
- [Como Contribuir](#-como-contribuir)

---

## Sobre o Projeto

A **APAE (Associação de Pais e Amigos dos Excepcionais)** é uma das maiores redes de atenção à pessoa com deficiência no Brasil. Este sistema foi desenvolvido para **otimizar o registro e o acompanhamento dos atendimentos realizados pela APAE**, oferecendo uma plataforma segura e unificada para os profissionais de saúde da instituição.

O objetivo é **modernizar e unificar o processo de acompanhamento dos pacientes**, garantindo acesso rápido, organizado e restrito às informações — melhorando a qualidade do atendimento, agilizando a comunicação interna entre as especialidades e fortalecendo a segurança dos dados dos pacientes.

### Funcionalidades Principais

- **Gestão de Pacientes** — visualização da lista de pacientes com dados pessoais e histórico.
- **Prontuário Eletrônico** — consulta e registro completo do prontuário de cada paciente.
- **Registro de Consultas** — adição de atendimentos e registro detalhado de cada consulta.
- **Relatórios Clínicos** — produção e inserção de relatórios no prontuário do paciente.
- **Gestão de Anexos** — adição de documentos e arquivos relevantes à consulta.
- **Privacidade por Especialidade** — cada profissional vê apenas os pacientes pelos quais é responsável, garantindo confidencialidade.
- **Perfil do Profissional** — visualização dos dados pessoais cadastrados no sistema.

---

## Stack Tecnológica

### Frontend

| Tecnologia | Descrição |
|------------|-----------|
| [React](https://react.dev/) | Biblioteca para interfaces de usuário |
| [shadcn/ui](https://ui.shadcn.com/) | Componentes de UI acessíveis e customizáveis |
| [pnpm](https://pnpm.io/) | Gerenciador de pacotes eficiente |

### Backend

| Tecnologia | Versão | Descrição |
|------------|--------|-----------|
| [Spring Boot](https://spring.io/projects/spring-boot) | 3.x | Framework Java para APIs REST |
| [Java](https://adoptium.net/) | 21 | Linguagem de programação do backend |
| [Spring Security](https://spring.io/projects/spring-security) | — | Autenticação e autorização (JWT nativo) |
| [Spring Data JPA](https://spring.io/projects/spring-data-jpa) | — | Persistência e acesso a dados |
| [Flyway](https://flywaydb.org/) | — | Versionamento e migração do banco de dados (V1–V9) |

### Infraestrutura e Deploy

| Tecnologia | Descrição |
|------------|-----------|
| [Docker](https://www.docker.com/) | Containerização do PostgreSQL e MinIO (desenvolvimento) |
| [PostgreSQL](https://www.postgresql.org/) | Banco de dados relacional |
| [MinIO](https://min.io/) | Armazenamento de objetos (arquivos e anexos) |

### Ferramentas

| Tecnologia | Descrição |
|------------|-----------|
| [Git](https://git-scm.com/) | Controle de versão |
| [IntelliJ IDEA](https://www.jetbrains.com/idea/) / [VS Code](https://code.visualstudio.com/) | Editores recomendados |

---

## Estrutura do Projeto

```
APAE-atendimento/
├── .env.example                          # Variáveis de ambiente (raiz)
├── .github/
│   ├── ISSUE_TEMPLATE/                   # Templates de issues
│   ├── workflows/
│   │   └── ci.yml                        # Pipeline de CI (GitHub Actions)
│   └── pull_request_template.md          # Template de PR
├── .husky/                               # Hooks de Git (pre-commit, commit-msg)
├── .scripts/                             # Scripts auxiliares do monorepo
├── backend/
│   ├── docker/                           # Configurações auxiliares do Docker
│   │   ├── docker-compose.properties     # Propriedades de conexão local
│   │   └── local-secrets.properties.example
│   └── atendimento/                      # Backend — Spring Boot (Java 21)
│       ├── Dockerfile                    # Imagem Docker do backend (multi-stage)
│       ├── mvnw / mvnw.cmd               # Maven Wrapper (Linux/Windows)
│       ├── pom.xml                       # Dependências Maven
│       └── src/
│           ├── main/
│           │   ├── java/br/org/apae/atendimento/
│           │   │   ├── AtendimentoApplication.java  # Entrypoint da aplicação
│           │   │   ├── config/           # Segurança, CORS, OpenAPI, MinIO
│           │   │   ├── controllers/      # Endpoints REST
│           │   │   ├── dtos/             # Data Transfer Objects
│           │   │   ├── entities/         # Entidades JPA
│           │   │   ├── exceptions/       # Tratamento global de erros
│           │   │   ├── mappers/          # Conversão entidade ↔ DTO
│           │   │   ├── repositories/     # Repositórios Spring Data
│           │   │   ├── security/         # Filtro JWT e configuração de segurança
│           │   │   ├── services/         # Lógica de negócio
│           │   │   │   └── integration/  # Cliente de integração apae-geral
│           │   │   └── utils/            # Funções auxiliares
│           │   └── resources/
│           │       ├── application.properties
│           │       ├── application-dev.properties
│           │       ├── application-prod.properties
│           │       ├── application-test.properties
│           │       └── db/migration/     # Scripts Flyway (V1–V9)
│           └── test/                     # Testes de integração e unitários
├── frontend/
│   └── atendimento-app/                  # Frontend — React + Next.js (TypeScript)
│       ├── Dockerfile                    # Imagem Docker do frontend (multi-stage)
│       ├── next.config.ts                # Configuração do Next.js
│       ├── tsconfig.json                 # Aliases de importação
│       ├── components.json               # Configuração shadcn/ui
│       ├── eslint.config.mjs             # Configuração ESLint
│       ├── jest.config.mjs               # Configuração de testes
│       ├── package.json                  # Dependências pnpm
│       └── src/
│           ├── app/                      # Páginas e rotas (App Router)
│           │   ├── (public)/             # Rotas públicas (sem autenticação)
│           │   │   ├── login/            # Página de login
│           │   │   └── auth/             # Callbacks de autenticação
│           │   └── (private)/            # Rotas protegidas (autenticadas)
│           │       ├── layout.tsx        # Layout com sidebar/navbar
│           │       ├── home/             # Dashboard principal
│           │       ├── agenda/           # Agenda de atendimentos
│           │       ├── atendimento/      # Registro e consulta de atendimentos
│           │       ├── relatorio/        # Relatórios clínicos
│           │       └── anexo/            # Gestão de anexos
│           ├── components/               # Componentes reutilizáveis (shadcn/ui)
│           ├── features/                 # Módulos de funcionalidade isolados
│           │   ├── agenda/               # Lógica e componentes da agenda
│           │   ├── atendimento/          # Lógica e componentes de atendimento
│           │   ├── anexo/                # Lógica e componentes de anexos
│           │   ├── arquivo/              # Gestão de arquivos
│           │   ├── home/                 # Componentes do dashboard
│           │   ├── profissional/         # Perfil do profissional
│           │   └── relatorio/            # Lógica e componentes de relatórios
│           ├── lib/                      # Utilitários e configuração de libs externas
│           ├── services/                 # Camada de comunicação com a API
│           ├── types/                    # Tipos TypeScript globais
│           └── utils/                   # Funções auxiliares
├── docs/                                 # Documentação auxiliar
│   ├── diagrama_de_classes.puml          # Diagrama de classes (PlantUML)
│   ├── diagrama_de_classes.svg           # Diagrama de classes (SVG)
│   ├── modelo_banco_de_dados_main.svg    # Modelo do banco de dados
│   ├── historia.md                       # Histórico de decisões do projeto
│   └── Estudo de Infraestrutura e Deploy - Oracle Cloud.md
├── docker-compose.yaml                   # PostgreSQL + MinIO (desenvolvimento local)
├── package.json                          # Scripts do monorepo (pnpm workspaces)
└── commitlint.config.js                  # Regras de Conventional Commits
```

---

## Quick Start

### Desenvolvimento Local Autônomo

O Atendimento pode ser executado sem iniciar o APAE-Geral ou o Gestão Escolar.
O PostgreSQL local reproduz os três schemas do Neon:

- `atendimento`: schema real do produto, versionado pelas migrations Flyway V1–V9;
- `apae_geral`: contrato mínimo mockado com usuários, profissionais, pacientes e agenda externa;
- `gestao_escolar`: schema presente, mas vazio, pois o Atendimento não o consulta.

Na raiz do repositório:

```bash
cp .env.example .env
pnpm --dir frontend/atendimento-app install
pnpm db:prepare
pnpm dev
```

Serviços locais:

| Serviço | URL |
|---------|-----|
| **Frontend** | `http://localhost:3001` |
| **Backend** | `http://localhost:8082/atendimento` |
| **Health check** | `http://localhost:8082/atendimento/actuator/health` |
| **PostgreSQL** | `localhost:5300` |
| **MinIO** | `http://localhost:9100` (console em `http://localhost:9101`) |

Credenciais fictícias:

| Campo | Valor |
|-------|-------|
| **E-mail** | `profissional@teste.local` |
| **Senha** | `12345678` |

Comandos úteis:

```bash
pnpm db:prepare    # contratos, migrations, seed e MinIO
pnpm db:migrate    # reaplica apenas as migrations pendentes
pnpm db:seed       # reaplica o seed idempotente
pnpm docker:down   # para containers e preserva volumes
pnpm docker:drop   # apaga os volumes e todos os dados locais
```

> Os objetos de `apae_geral` são contratos locais de desenvolvimento, não uma cópia do schema pertencente ao APAE-Geral. Alterações reais desse contrato devem ser sincronizadas manualmente quando o produto de origem mudar.

---

### Pré-requisitos

| Ferramenta | Versão | Finalidade |
|------------|--------|------------|
| **Node.js** | 20+ | Executar o frontend React |
| **pnpm** | — | Gerenciar pacotes do frontend |
| **Docker** | 20+ | Subir PostgreSQL e MinIO locais |
| **Java** | 21+ | Compilar e executar o backend Spring Boot |
| **Git** | — | Clonar o repositório |

### 1. Clone o repositório

```bash
git clone https://github.com/IFPBEsp/APAE-atendimento.git
cd APAE-atendimento
```

### 2. Configure as variáveis de ambiente

```bash
# Na raiz do projeto, crie o .env baseado no exemplo
cp .env.example .env
# Edite o .env com as credenciais do seu ambiente local
```

> **Nota:** Certifique-se de preencher as variáveis sensíveis no `.env` (credenciais do banco e JWT) antes de prosseguir. O backend lê automaticamente o `.env` da raiz — não é necessário configurar variáveis manualmente no IntelliJ.

### 3. Suba a infraestrutura (Docker)

```bash
# Sobe PostgreSQL e MinIO
docker compose up minio postgres-db -d

# Se você usa banco em nuvem (ex.: Neon), suba apenas o MinIO:
docker compose up minio -d
```

### 4. Suba o backend

```bash
cd backend/atendimento

# Linux/Mac
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Windows
mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=dev
```

> **No IntelliJ:** basta rodar a classe `AtendimentoApplication`. O profile `dev` lerá o `.env` automaticamente.

### 5. Suba o frontend

Em um **novo terminal**:

```bash
cd frontend/atendimento-app
pnpm install
pnpm dev
```

Acesse `http://localhost:3001`.

### 6. Execução completa via Docker Compose (produção)

```bash
# Sobe toda a stack (Banco, Storage, Backend e Frontend)
docker compose --profile PROD up -d --build
```

O sistema estará disponível em:

| Serviço | URL |
|---------|-----|
| **Frontend** | `http://localhost:80` |
| **Backend API** | `http://localhost:8080` |
| **MinIO Console** | `http://localhost:9001` |

### 7. Build para produção (opcional)

```bash
# Backend — gera o .jar em backend/atendimento/target/
cd backend/atendimento && ./mvnw clean package -DskipTests

# Frontend — gera build otimizado
cd frontend/atendimento-app && pnpm build
```

---

## Infraestrutura e Deploy

### Ambiente de Desenvolvimento (Docker)

O `docker-compose.yml` na raiz sobe os serviços de infraestrutura necessários para o desenvolvimento local:

| Serviço | Imagem | Portas | Finalidade |
|---------|--------|--------|------------|
| `postgres-db` | `postgres:latest` | `5300:5432` | Banco de dados local |
| `minio` | `minio/minio:latest` | `9100:9000`, `9101:9001` | Armazenamento de objetos (arquivos e anexos) |

Credenciais padrão de desenvolvimento (definidas no `.env`):

| Variável | Descrição |
|----------|-----------|
| `POSTGRES_DB` | Nome do banco de dados |
| `POSTGRES_USER` | Usuário do banco |
| `POSTGRES_PASSWORD` | Senha do banco |
| `MINIO_ROOT_USER` | Usuário admin do MinIO |
| `MINIO_ROOT_PASSWORD` | Senha admin do MinIO |

### Dockerfile do Backend

O backend utiliza **multi-stage build** para otimizar a imagem final:

1. **Stage `build`** — `maven:3.9-eclipse-temurin-21`: compila o projeto e gera o `.jar`.
2. **Stage final** — `eclipse-temurin:21-jre`: imagem leve (apenas JRE) que executa o `.jar`.

### Migrações de Banco

O **Flyway** executa automaticamente os scripts em `backend/atendimento/src/main/resources/db/migration/` ao iniciar o backend. As migrations V1–V9 versionam o schema `atendimento`.

---

## Integração com o Sistema Geral (apae-geral)

A agenda do Sistema de Atendimento pode exibir, junto com os agendamentos locais, os **agendamentos gerados no Sistema Geral da APAE** (projeto `apae-geral`). A integração é feita pelo backend de atendimento, que se autentica no apae-geral e consulta os agendamentos do profissional logado.

> **Componente responsável:** `backend/atendimento/src/main/java/br/org/apae/atendimento/services/integration/AgendamentoExternoClient.java`

### Como Funciona o Fluxo

1. Ao listar a agenda (`GET /agendamento`), o backend de atendimento busca os agendamentos **locais** no seu próprio banco.
2. Em seguida, autentica no apae-geral (`POST /apae-geral/api/auth/signin`) e consulta os **agendamentos gerados** do profissional (`GET /apae-geral/api/appointments/professional/{profissionalId}/generated`).
3. As duas listas são mescladas e retornadas. Os itens externos vêm com a flag `externo: true`.

### Pré-requisitos

- O projeto **apae-geral** clonado e rodando localmente (backend + banco PostgreSQL + MinIO).
- O backend do apae-geral acessível em `http://localhost:8090/apae-geral`.

> ⚠️ **Atenção ao caminho:** o apae-geral usa `spring.mvc.servlet.path: /api`, então **todos os endpoints REST ficam sob `/apae-geral/api/...`** (e não `/apae-geral/...`). Chamar o caminho sem o `/api` resulta em **HTTP 403**.

### Passo a Passo

**1. Suba o apae-geral**

No diretório do projeto apae-geral (ex.: `../APAE/apps/api`), com o `.env` preenchido (`DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `MINIO_*`, `API_PORT=8090`):

```bash
cd apps/api
./mvnw spring-boot:run
```

Valide que subiu:

```bash
curl -X POST http://localhost:8090/apae-geral/api/auth/signin \
  -H "Content-Type: application/json" \
  -d '{"username":"admin@teste.com","password":"senha123"}'
# Deve retornar 200 com {"token":"..."}
```

**2. Configure as credenciais/URL da integração (opcional)**

Por padrão o cliente já aponta para `http://localhost:8090/apae-geral/api` e usa o usuário `admin@teste.com` / `senha123`. Para sobrescrever sem recompilar, adicione ao `backend/docker/docker-compose.properties`:

```properties
api.geral.url=http://localhost:8090/apae-geral/api
api.geral.username=admin@teste.com
api.geral.password=senha123
```

> Garanta que esse usuário existe no banco do apae-geral e que a senha confere (validada via BCrypt).

**3. Alinhe o ID do profissional entre os dois sistemas**

A integração busca a agenda usando o **ID do profissional logado** no atendimento. Para que os agendamentos externos apareçam, esse ID precisa ser **o mesmo** de um profissional que tenha agenda no apae-geral. Ou seja: o registro em `vw_profissionais` (atendimento) deve ter o mesmo `id` do profissional correspondente em `profissionais_da_saude` (apae-geral).

**4. Suba o backend de atendimento e faça login**

```bash
cd backend/atendimento
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

Faça login no frontend com um profissional cujo ID esteja alinhado (passo 3) e abra a **Agenda**. Os agendamentos do sistema geral devem aparecer junto com os locais.

### Solução de Problemas

| Sintoma | Causa Provável |
|---------|----------------|
| Nada aparece / lista só com os locais | Verifique o log do backend — o `AgendamentoExternoClient` registra avisos (`log.warn`) com a causa (token nulo, 403, 401 etc.) |
| `Erro ao obter token do sistema geral: 403` | A URL está sem o `/api` ou o apae-geral não está no ar |
| `401 - E-mail ou senha incorretos` | O usuário/senha da integração não confere com o banco do apae-geral |
| Login OK mas lista vazia | O ID do profissional logado não corresponde a nenhum profissional com agenda no apae-geral (ver passo 3) |

---

## Git Flow

O projeto utiliza a branch `dev` como branch principal e única branch protegida. O trabalho é orientado por issues do [board do projeto](https://github.com/orgs/IFPBEsp/projects/).

```
dev ──────────────────────────────────────────────────────▶ (branch principal)
  │
  ├── 10-feat-prontuario-eletronico ──────── PR ── review ── merge em dev
  ├── 25-fix-corrigir-listagem-pacientes ─── PR ── review ── merge em dev
  ├── 42-refactor-cliente-integracao ─────── PR ── review ── merge em dev
  └── 98-docs-padronizar-readme ──────────── PR ── review ── merge em dev
```

### Branches

| Branch | Finalidade | Protegida? |
|--------|------------|------------|
| `dev` | Branch principal (default), base de todo o desenvolvimento | Sim — somente via PR revisado |
| `{numero}-feat-*` | Novas funcionalidades | Não |
| `{numero}-fix-*` | Correções de bugs | Não |
| `{numero}-docs-*` | Alterações de documentação | Não |
| `{numero}-refactor-*` | Refatorações sem mudança de comportamento | Não |

> O nome da branch é gerado automaticamente pelo GitHub ao clicar em **"Create a branch"** na seção **Development** da issue (menu lateral direito). Isso vincula a branch à issue automaticamente.

### Fluxo de Trabalho

#### 1. Pegar a issue

1. Escolher uma issue na coluna **Ready** do board do projeto.
2. No menu lateral direito da issue:
   - **Assignees** — assinar a issue para si.
   - **Estimate** — preencher com o número de dias estimado para conclusão.
   - **Start date** — preencher com a data de início do trabalho.
3. Na seção **Development** (menu lateral direito), clicar em **"Create a branch"** — o GitHub gera a branch a partir de `dev` com nome no formato `{numero}-{tipo}-{descricao}` e vincula automaticamente.
4. Mover o card da issue de **Ready** para **In Progress** no board.
5. Fazer checkout da branch localmente:
   ```bash
   git fetch origin
   git checkout 42-refactor-cliente-integracao
   ```

#### 2. Desenvolver

6. Realizar os commits seguindo o padrão de [Conventional Commits](#-conventional-commits).

#### 3. Solicitar revisão

7. Ao concluir, abrir um **Pull Request** para `dev`.
8. Na aba do PR, adicionar o **PO** e o **Scrum Master** como **Reviewers**.
9. Mover o card da issue de **In Progress** para **Code Review** no board.
10. Enviar o link do PR no canal de Pull Requests do Discord, marcando o PO e o Scrum Master.

#### 4. Revisão

11. **Se aprovado** — o revisor realiza o merge em `dev` e move o card para **Weekly Review**.
12. **Se alterações forem solicitadas** — o revisor move o card para **Changes Requested** e comunica via Discord. O dev corrige e volta ao passo 7.

#### 5. Finalização

13. Na **reunião semanal** do time, as issues em **Weekly Review** são apresentadas ao grupo.
14. Após a apresentação, o card é movido para **Done**.

### Regras

1. **Nunca** faça commit diretamente em `dev`.
2. Cada issue deve ter **sua própria branch**, criada via GitHub para manter o vínculo.
3. O autor do PR **não pode** aprovar e mergear seu próprio código.
4. O merge só é feito após **revisão e aprovação** por PO ou Scrum Master.
5. Sempre **assinar a issue** e preencher **Estimate** e **Start date** antes de começar a trabalhar.
6. Sempre **comunicar via Discord** ao abrir um PR.

---

## Conventional Commits

Todos os commits devem seguir o padrão [Conventional Commits](https://www.conventionalcommits.org/):

```
tipo: descrição do commit
```

### Tipos

| Tipo | Quando usar | Exemplo |
|------|-------------|---------|
| `feat` | Nova funcionalidade | `feat: adiciona registro de anexos no prontuário` |
| `fix` | Correção de bug | `fix: corrige listagem de pacientes por especialidade` |
| `docs` | Documentação | `docs: padroniza readme com estrutura do gestao escolar` |
| `style` | Formatação (sem mudança de lógica) | `style: aplica formatação pnpm no frontend` |
| `refactor` | Refatoração (sem mudança de comportamento) | `refactor: extrai lógica de integração para AgendamentoExternoClient` |
| `test` | Testes | `test: adiciona teste unitário para AgendamentoService` |
| `chore` | Tarefas de manutenção | `chore: atualiza dependências do Spring Boot` |
| `perf` | Melhoria de performance | `perf: otimiza query de listagem de agenda` |
| `ci` | Integração contínua | `ci: adiciona workflow de build no GitHub Actions` |

### Regras

- Formato: **`tipo: descrição`** (sem escopo entre parênteses).
- Descrição em **português**.
- Primeira letra **minúscula** na descrição.
- Sem ponto final na primeira linha.
- Corpo opcional para explicar o **porquê** da mudança.

---

## Como Contribuir

O fluxo completo está detalhado na seção [Git Flow](#-git-flow). Em resumo:

1. Escolha uma issue na coluna **Ready** do board do projeto.
2. Assine a issue, preencha **Estimate** e **Start date**, crie a branch via GitHub e mova para **In Progress**.
3. Implemente as alterações e faça commits seguindo os [Conventional Commits](#-conventional-commits).
4. Abra um **Pull Request** para `dev`, adicione PO e Scrum Master como revisores, mova para **Code Review** e envie o link no Discord.
5. Aguarde revisão — o autor não realiza o merge.

---

<div align="center">
  <sub>Desenvolvido com dedicação para a comunidade APAE</sub>
</div>
