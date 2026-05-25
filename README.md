![Logo da APAE](https://seeklogo.com/images/A/apae-logo-30E7C409C6-seeklogo.com.png)

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

#  Como Rodar o Projeto

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

# Contribuições

**1. Clone o repositório**

``https://github.com/IFPBEsp/APAE-atendimento.git``

**2. Crie uma nova branch**

``git checkout -b minha-nova-feature``

**3. Realize um commit**

``git commit -m "Descrição da sua alteração"``

**4. Envie suas alterações para o repositório remoto**

``git push -u origin minha-nova-feature``

**5. Crie um Pull Request**: Vá até o repositório remoto e crie um novo Pull Request.
