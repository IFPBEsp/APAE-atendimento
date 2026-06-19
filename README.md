<div align="center">
  <img src="https://github.com/user-attachments/assets/d4cb7e8e-abbb-41ea-8a3b-602bc272c360" width="180" alt="Logo APAE">
  <h1 style="margin-top: 10px;">Sistema de Atendimento da APAE</h1>
  <p>
    <img src="https://img.shields.io/badge/Status-Em%20Desenvolvimento-yellow?style=for-the-badge" alt="Status">
    <img src="https://img.shields.io/badge/Spring%20Boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white" alt="Spring Boot">
    <img src="https://img.shields.io/badge/React-20232A?style=for-the-badge&logo=react&logoColor=61DAFB" alt="React">
    <img src="https://img.shields.io/badge/Flyway-CC0200?style=for-the-badge&logo=flyway&logoColor=white" alt="Flyway">
    <img src="https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white" alt="Docker">
    <img src="https://img.shields.io/badge/pnpm-F69220?style=for-the-badge&logo=pnpm&logoColor=white" alt="pnpm">
  </p>
</div>
---

# Sistema de Atendimento da APAE

O Sistema de Atendimento da APAE busca ser uma sistema seguro que busca otimizar o registro de atendimento dos pacientes atendidos pela instituição. Seu principal propósito é otimizar o trabalho dos profissionais de saúde, oferecendo uma ferramenta eficiente para o gerenciamento dos atendimentos.

A intenção do sistema é modernizar e unificar o processo de acompanhamento dos pacientes, garantindo acesso rápido, organizado e restrito às informações. Dessa forma, busca-se melhorar a qualidade do atendimento, agilizar a comunicação interna entre as especialidades e fortalecer a segurança dos dados dos pacientes. Em síntese, o sistema visa promover eficiência, transparência e confidencialidade no fluxo de atendimentos da APAE, contribuindo diretamente para a excelência no cuidado e acompanhamento das pessoas assistidas pela instituição.

# Funcionalidades presentes no sistema

- Visualização da lista de pacientes com dados pessoais;
- Consultar prontuário do paciente;
- Adicionar as consultas permitindo registrar o atendimento;
- Produção de relatórios e inseri-los no prontuário do paciente;
- Adicionar anexos importantes presentes na consulta.

# Observações

- Cada especialista **só vê pacientes pelos quais é responsável**, garantindo **privacidade e segurança**

- Permite ao profissional visualizar seus **dados pessoais cadastrados no sistema**

# Stack Tecnológico

- **Front-end:** [React](https://react.dev) com [Shadcn/ui](https://ui.shadcn.com)
- **Back-end:** [Spring Boot](https://spring.io/projects/spring-boot)
- **Banco de Dados:** [PostgreSQL](https://www.postgresql.org/)
- **Armazenamento de Arquivos:** [MinIO](https://min.io/)
- **Autenticação e Autorização:** Geração própria e verificação de tokens JWT nativa via [Spring Security](https://spring.io/projects/spring-security)

### Front-end

[**🧩 Caminho referente ao Front-end do sistema**](https://github.com/IFPBEsp/APAE-atendimento/tree/dev/frontend#-tecnologias)

### Back-end

[**🧩 Caminho referente ao Back-end do sistema**](https://github.com/IFPBEsp/APAE-atendimento/tree/dev/backend#-pré-requisitos)

# Como Rodar o Projeto

Para executar o sistema completo em sua máquina, siga os passos abaixo:

### Pré-requisitos

- [Docker](https://www.docker.com/) e [Docker Compose](https://docs.docker.com/compose/)
- [Node.js](https://nodejs.org/) (recomendado v20+) e [PNPM](https://pnpm.io/)
- [Java JDK 21](https://adoptium.net/temurin/releases/?version=21)

---

### 1. Preparação do Ambiente

Clone o repositório e crie os arquivos de ambiente necessários:

```bash
# Clone o repositório
git clone https://github.com/IFPBEsp/APAE-atendimento.git
cd APAE-atendimento

# No root do projeto, crie o .env baseado no exemplo
cp .env.example .env

# No diretório do backend, crie o local-secrets.properties
cp backend/docker/local-secrets.properties.example backend/docker/local-secrets.properties
```

> **Nota:** Certifique-se de preencher as variáveis sensíveis no `.env` e `local-secrets.properties` antes de prosseguir.

---

### 2. Executando com Docker Compose (Recomendado)

A forma mais simples de rodar toda a stack (Banco, Storage, Backend e Frontend) é via Docker Compose:

```bash
# Sobe todos os serviços em modo produção
docker compose --profile PROD up -d --build
```

O sistema estará disponível em:

- **Frontend:** `http://localhost:80` (ou porta configurada)
- **Backend API:** `http://localhost:8080`
- **MinIO Console:** `http://localhost:9001`

---

### 3. Execução em Desenvolvimento (Manual)

Se desejar rodar os serviços separadamente para desenvolvimento:

#### Infraestrutura (Banco e MinIO)

```bash
docker compose up postgres-db minio -d
```

#### Backend (Spring Boot)

```bash
cd backend/atendimento
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

#### Frontend (Next.js)

```bash
cd frontend/atendimento-app
pnpm install
pnpm dev
```

# Integração com o Sistema Geral (apae-geral)

A agenda do Sistema de Atendimento pode exibir, junto com os agendamentos locais, os **agendamentos gerados no Sistema Geral da APAE** (projeto `apae-geral`). A integração é feita pelo backend de atendimento, que se autentica no apae-geral e consulta os agendamentos do profissional logado.

> **Componente responsável:** `backend/atendimento/src/main/java/br/org/apae/atendimento/services/integration/AgendamentoExternoClient.java`

### Como funciona o fluxo

1. Ao listar a agenda (`GET /agendamento`), o backend de atendimento busca os agendamentos **locais** no seu próprio banco.
2. Em seguida, autentica no apae-geral (`POST /apae-geral/api/auth/signin`) e consulta os **agendamentos gerados** do profissional (`GET /apae-geral/api/appointments/professional/{profissionalId}/generated`).
3. As duas listas são mescladas e retornadas. Os itens externos vêm com a flag `externo: true`.

### Pré-requisitos

- O projeto **apae-geral** clonado e rodando localmente (backend + banco PostgreSQL + MinIO).
- O backend do apae-geral acessível em `http://localhost:8090/apae-geral`.

> ⚠️ **Atenção ao caminho:** o apae-geral usa `spring.mvc.servlet.path: /api`, então **todos os endpoints REST ficam sob `/apae-geral/api/...`** (e não `/apae-geral/...`). Chamar o caminho sem o `/api` resulta em **HTTP 403** (a requisição cai na página de erro, que é protegida).

### Passo a passo

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

> Garanta que esse usuário existe no banco do apae-geral e que a senha confere (a senha é validada via BCrypt).

**3. Alinhe o ID do profissional entre os dois sistemas**

A integração busca a agenda usando o **ID do profissional logado** no atendimento. Para que os agendamentos externos apareçam, esse ID precisa ser **o mesmo** de um profissional que tenha agenda no apae-geral. Ou seja: o registro em `vw_profissionais` (atendimento) deve ter o mesmo `id` do profissional correspondente em `profissionais_da_saude` (apae-geral).

**4. Suba o backend de atendimento e faça login**

```bash
cd backend/atendimento
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

Faça login no frontend com um profissional cujo ID esteja alinhado (passo 3) e abra a **Agenda**. Os agendamentos do sistema geral devem aparecer junto com os locais.

### Solução de problemas

- **Nada aparece / lista só com os locais:** confira o log do backend de atendimento. O `AgendamentoExternoClient` registra avisos (`log.warn`) com a causa — token nulo, 403, 401 etc.
- **`Erro ao obter token do sistema geral: 403`:** a URL está sem o `/api` ou o apae-geral não está no ar.
- **`401 - E-mail ou senha incorretos`:** o usuário/senha da integração não confere com o banco do apae-geral.
- **Login OK mas lista vazia:** o ID do profissional logado não corresponde a nenhum profissional com agenda no apae-geral (ver passo 3).

# Contribuições

**1. Clone o repositório**

`https://github.com/IFPBEsp/APAE-atendimento.git`

**2. Crie uma nova branch**

`git checkout -b minha-nova-feature`

**3. Realize um commit**

`git commit -m "Descrição da sua alteração"`

**4. Envie suas alterações para o repositório remoto**

`git push -u origin minha-nova-feature`

**5. Crie um Pull Request**: Vá até o repositório remoto e crie um novo Pull Request.
