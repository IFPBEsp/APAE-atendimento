# Backend - Produto Atendimento

Este diretório contém o backend do produto atendimento, desenvolvido em Java 21 utilizando o Spring Boot.
Atualmente, o backend possui um único endpoint (/hello) que retorna uma mensagem de teste ("Hello World"), servindo como base para o desenvolvimento das próximas funcionalidades.

# Pré-requisitos

**Antes de executar o projeto localmente, verifique se as seguintes ferramentas estão instaladas:**

- Java: versão mínima 21
- Maven: versão mínima 3.9.x
- Git: para clonar o repositório

Você pode verificar as versões instaladas com os comandos:

```bash
java -version
```
```bash
mvn -v
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

## Executando o Backend 

> “O parâmetro -Dprofile define qual perfil do Spring Boot será carregado (dev, test ou prod).

### Localmente em modo Test

Para iniciar o servidor local em modo teste, execute o comando:

```bash
mvn spring-boot:run -Dprofile=test
```

### Localmente em modo Dev (desenvolvedor)

Para iniciar o servidor local em modo dev, execute o comando:

```bash
mvn spring-boot:run -Dprofile=dev
```

Após a inicialização, o backend estará acessível em:

http://localhost:8080

Endpoints Disponíveis

| Método | Endpoint | Descrição | Resposta |
|--------|----------|-----------|----------|
| GET    |  /hello  | Retorna uma mensagem de teste	"Hello World" | Hello World! |

