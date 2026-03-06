<div align="center">
  <img src="https://github.com/user-attachments/assets/d4cb7e8e-abbb-41ea-8a3b-602bc272c360" width="180" alt="Logo APAE">
  <h1 style="margin-top: 10px;">Sistema de Atendimento da APAE</h1>
  <p>
    <img src="https://img.shields.io/badge/Status-Em%20Desenvolvimento-yellow?style=for-the-badge" alt="Status">
    <img src="https://img.shields.io/badge/Spring%20Boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white" alt="Spring Boot">
    <img src="https://img.shields.io/badge/React-20232A?style=for-the-badge&logo=react&logoColor=61DAFB" alt="React">
    <img src="https://img.shields.io/badge/Firebase-FFCA28?style=for-the-badge&logo=firebase&logoColor=black" alt="Firebase">
    <img src="https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white" alt="Docker">
    <img src="https://img.shields.io/badge/pnpm-F69220?style=for-the-badge&logo=pnpm&logoColor=white" alt="pnpm">
  </p>
</div>

---

## Sobre o Projeto
O **Sistema de Atendimento da APAE** busca modernizar e unificar o processo de acompanhamento dos pacientes. O foco principal é oferecer uma ferramenta eficiente para o gerenciamento dos atendimentos, garantindo acesso rápido, organizado e restrito às informações confidenciais.

## Funcionalidades Principais
- **Gestão de Pacientes:** Visualização de dados pessoais e histórico clínico.
- **Registro de Consultas:** Otimização do trabalho dos profissionais de saúde.
- **Relatórios e Prontuários:** Produção de relatórios integrados ao prontuário do paciente.
- **Anexos:** Inserção de documentos importantes diretamente na consulta.
- **Privacidade:** Cada especialista visualiza apenas os pacientes sob sua responsabilidade.

## Stack Tecnológica
| Camada | Tecnologia |
| :--- | :--- |
| **Frontend** | [React](https://react.dev) + [Shadcn/ui](https://ui.shadcn.com) |
| **Backend** | [Spring Boot](https://spring.io/) |
| **Autenticação** | [Firebase Auth](https://firebase.google.com/docs/auth) |
| **Estilização** | [Tailwind CSS](https://tailwindcss.com/) |

---

## Estrutura do Repositório
Para detalhes técnicos de implementação e pré-requisitos, acesse os módulos:

* **[Front-end](./frontend)**: Interface e componentes React.
* **[Back-end](./backend)**: API, regras de negócio e integrações.

---

## Como Rodar o Projeto

Este projeto utiliza **Docker** para infraestrutura e **pnpm** para o gerenciamento de dependências do frontend.

### Configuração das Variáveis de Ambiente
Antes de iniciar, crie um arquivo `.env` na raiz do projeto para configurar as variáveis necessárias:

### 1. Subir a Infraestrutura (Docker)
Certifique-se de ter o Docker instalado e execute o comando na raiz do projeto para subir o banco de dados e demais serviços:
```bash
docker-compose up --build -d
```

### 2. Iniciar o Back-end
Navegue até a pasta do servidor e execute a aplicação Spring Boot:
```bash
cd backend
./mvnw spring-boot:run
```

### 3. Iniciar o Front-end
Em um novo terminal, navegue até a pasta do cliente e inicie o servidor de desenvolvimento:
```bash
cd frontend
pnpm install
pnpm dev
```

## Como Contribuir
Mantemos um fluxo de trabalho rigoroso para garantir a qualidade do código e do histórico.

### 1. Convenção de Branches
Siga o padrão: `tipo/numero-issue-descricao-curta`.
- `feat/`: Novas funcionalidades.
- `fix/`: Correção de bugs.
- `chore/`: Pequenas alterações de manutenção.
- `refactor/`: Refatoração de código sem adicionar funcionalidades ou corrigir bugs.
- `style/`: Mudanças de formatação (lint, espaços, indentação, etc.).
- `docs/`: Modificação na documentação (exemplo: README).
- `test/`: Modificações ou adição de testes.
- `perf/`: Melhorias de desempenho.
- `build/`: Mudanças na build e dependências, sem afetar o código.
- `revert/`: Reversão de um commit anterior.
- `cleanup/`: Remoção de códigos comentados ou trechos desnecessários.
- `remove/`: Exclusão de arquivos, diretórios ou funcionalidades obsoletas.

### 2. Pull Requests (PRs)
- Utilize as **labels** apropriadas (`type:`, `area:`, `priority:`, `points:`) para categorizar seu PR.
- PRs para as branches `main` e `dev` exigem aprovação e não permitem *force push*.

---
<p align="center"><b>APAE - Eficiência, Transparência e Confidencialidade</b></p>
