# 📘 Projeto Atendimento Apae — Frontend

Documentação sobre protótipos, as tecnologias, configuração e execução local do projeto **Atendimento Apae**.

---

## Protótipos

- [Figma - mobile](https://www.figma.com/design/HP3scnI5pVRtEkoP6yi2Q0/Prot%C3%B3tipo-mobile---APAE?node-id=0-1&t=SN2yHzp7JcgWuhz4-1)
- [Figma - web](https://www.figma.com/design/B0uUlvpSw7oZdAxvGDt4tS/APAE---Prot%C3%B3tipo-web?node-id=0-1&t=gRGm4Lea76zWpdnx-1)

---

##  Tecnologias

- **TypeScript** — Tipagem estática para JavaScript
- **Next.js** — Framework React com App Router para aplicações modernas
- **pnpm 10.33.0** — Gerenciador de pacotes eficiente e leve
- **Node.js 24** — Ambiente de execução JavaScript
- **Tailwind CSS** — Estilização utilitária
- **shadcn/ui** — Componentes de UI baseados em Radix UI
- **TanStack Query** — Gerenciamento de estado e cache de dados assíncronos
- **Axios** — Cliente HTTP com interceptors para autenticação
- **React Hook Form** — Gerenciamento de formulários
- **@react-pdf/renderer + react-pdf** — Geração e visualização de PDFs
- **Sonner** — Notificações toast
- **Lucide React** — Biblioteca de ícones

---

## Variáveis de Ambiente

Crie um arquivo `.env.local` na raiz do projeto `frontend/atendimento-app/` com o seguinte conteúdo:

```env
NEXT_PUBLIC_API_URL=http://localhost:8080
```

| Variável | Descrição | Padrão |
|---|---|---|
| `NEXT_PUBLIC_API_URL` | URL base da API do backend | `http://localhost:8080` |
 
---

#  Configuração do Ambiente

##  Instalação das Dependências

Instale o pnpm globalmente (caso ainda não tenha):

```bash
npm i -g pnpm@10.33.0 -y
```


Depois, instale as dependências do projeto:

```bash
pnpm i
```

> **Dica:**  
> Para adicionar novas dependências:
> `pnpm add <dependência>` ou `pnpm i <dependência>`

---

##  Inicialização do Projeto

Inicialização do projeto a partir dos comandos presentes no arquivo `package.json`:

### Desenvolvimento

```bash
pnpm dev # inicialização da aplicação em fase de desenvolvimento
```

### Produção

```bash
pnpm build # Gera o build da aplicação para produção
```

---

##  Acesso à Aplicação Frontend

Por fim, pode-se acessá-la localmente pelo localhost na porta 3000.

[Acessar Aplicação](http://localhost:3000/)

## Estrutura de Rotas

O projeto utiliza o **App Router** do Next.js com separação entre rotas públicas e privadas.

### Rotas Públicas

| Rota | Descrição |
|------|-----------|
| `/login` | Página de autenticação do profissional |

### Rotas Privadas

Todas as rotas privadas validam a sessão via cookie JWT antes de renderizar. Usuários não autenticados são redirecionados para `/login`.

| Rota | Descrição |
|------|-----------|
| `/home` | Lista paginada de pacientes com busca e filtros |
| `/agenda` | Gerenciamento de agendamentos por dia |
| `/atendimento/[id]` | Atendimentos de um paciente agrupados por mês |
| `/relatorio/[id]` | Relatórios de um paciente (upload e geração por template) |
| `/anexo/[id]` | Anexos de um paciente (upload de arquivos) |
 
---

## Via Docker Compose (stack completa)

Para subir toda a stack com Docker, consulte `backend/docker/Readme.md`.

A variável `NEXT_PUBLIC_API_URL` é injetada em tempo de build via `ARG` no `Dockerfile`. Configure-a no `docker-compose.yml` conforme o ambiente.

