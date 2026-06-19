# Estudo de Infraestrutura e Deploy - Oracle Cloud

Este documento consolida o estudo e a configuração prática que realizei na Oracle Cloud Infrastructure (OCI) para hospedar o Produto de Atendimento.

Nesta etapa inicial, todo o laboratório foi montado utilizando exclusivamente os recursos do nível *Always Free* (Sempre Grátis). O objetivo aqui não é manter a aplicação de produção nessa infraestrutura limitada para sempre, mas sim usar esse ambiente como uma "sandbox" (ambiente de testes). Isso me permitiu homologar o processo de deploy, entender os gargalos da nossa aplicação (especialmente o consumo de RAM do Java) e validar configurações de rede e segurança.

Toda a base de conhecimento estruturada aqui — desde a criação da VM, mapeamento de dependências, até a decisão entre deploy nativo ou em contêineres e rotinas de backup — servirá como o roteiro exato para quando formos provisionar o servidor definitivo na VPS paga que será fornecida pelos professores.

# **Como criar uma máquina virtual na Oracle?**

A criação de uma máquina virtual (chamada na plataforma de *Compute Instance*) vai um pouco além de só escolher o sistema operacional, exigindo a configuração atrelada de rede e segurança. O processo que segui no painel foi:

1. **Definição Inicial:** Acessei `Compute > Instances` e cliquei em `Create Instance`. Defini um nome claro para o servidor (ex: `api-atendimento-testes`) para facilitar a identificação.
2. **Imagem e Hardware (Shape):** Para o sistema operacional (Imagem), optei pelo **Ubuntu 22.04**, que é o padrão mais estável para servidores web.
    - Para o hardware, selecionei a instância base do plano gratuito (ex: `VM.Standard.E2.1.Micro` com 1 OCPU e 1GB de RAM). Para a futura máquina paga, esse será o principal ponto de alteração no painel, escolhendo um *shape* com mais memória.
3. **Configuração de Rede:** Na seção *Networking*, vinculei a máquina a uma VCN (Virtual Cloud Network) e a uma sub-rede pública. Um detalhe crítico que marquei foi a opção **"Assign a public IPv4 address"** — sem isso, a máquina fica sem IP externo e inacessível pela internet.
4. **Segurança (SSH):** A Oracle não usa login por senha para acesso root. Na seção de chaves, mandei gerar um novo par de chaves SSH e baixei a chave privada `.key` para o meu computador. É com ela que consigo acessar o terminal do servidor.
5. **Armazenamento:** Aloquei 80GB de armazenamento total e configurei um arquivo de swap com 4GB para a RAM, mas a plataforma permite alocar até 200GB no plano free, o que é útil se o banco de dados for rodar na mesma máquina.
6. **Provisionamento:** Ao confirmar, o status fica em *Provisioning* enquanto o servidor é montado pela Oracle. Assim que muda para *Running*, a máquina está no ar.

# **Quais dependências tem na criação da máquina virtual?**

Para que a plataforma permita a criação da nossa instância (e para que possamos acessá-la remotamente), ela **depende** de uma infraestrutura de rede e segurança pré-existente ou criada no momento do provisionamento.

Durante o laboratório, mapeei que a criação da VM possui as seguintes dependências vitais:

1. **Compartimento (Compartment):** Antes de qualquer rede existir, a Oracle exige que a VM seja alocada em um Compartimento. É uma fronteira lógica de isolamento usada para organizar recursos e controlar quem tem permissão para acessá-los.
2. **VCN (Virtual Cloud Network):** É a fundação de tudo. Funciona como o nosso data center privado e virtual dentro da Oracle. A máquina virtual precisa obrigatoriamente de uma VCN para existir e ter um escopo de rede.
3. **Sub-rede Pública (Public Subnet):** A VCN é dividida em sub-redes. Dependemos de uma sub-rede configurada como "Pública" para que a API do sistema de atendimento possa receber requisições da internet externa.
4. **Endereço IPv4 Público (IP Efêmero ou Reservado):** A interface de rede virtual da máquina (VNIC) depende da atribuição de um IP roteável na internet para não ficar isolada.
5. **Internet Gateway (IGW):** É a ponte de comunicação. Sem ele, a sub-rede pública é inútil. O IGW é a dependência que conecta a nossa VCN com a internet global, permitindo que o servidor consiga baixar pacotes (como o OpenJDK) e executar o `git pull` do nosso repositório no GitHub.
6. **Security Lists (Regras de Firewall):** A máquina depende de regras de tráfego explícitas. Por padrão, a Oracle bloqueia quase tudo. Tive que configurar regras de entrada (*Ingress Rules*) liberando a porta **22** (essencial para eu acessar o terminal via SSH) e a porta **8080** (para testar o Spring Boot).
7. **Chaves Criptográficas (SSH Keys):** A Oracle Cloud é estrita com segurança e não permite a criação de servidores Linux baseados em login por senha tradicional. É uma dependência rigorosa gerar e anexar uma Chave Pública na criação da VM. Sem a Chave Privada correspondente guardada na minha máquina local, o acesso remoto ao servidor seria impossível.

# Vai ser necessário o uso de containers?

## **1. O Cenário Atual (Free Tier e Deploy Nativo):**

O motivo de não usar contêineres agora é puramente matemático. A instância Micro da Oracle possui apenas **1 GB de RAM**. O Docker Engine (o serviço que roda e gerencia os contêineres) consome memória apenas para se manter ativo no sistema operacional (conhecido como *overhead*).

Se eu tentasse empacotar o nosso backend em Java (Spring Boot) — que já é "guloso" por natureza — dentro de um contêiner nessa máquina minúscula, o sistema operacional ativaria o *OOM Killer* (Out of Memory) e mataria o processo, derrubando a API.

## **2. O Cenário Definitivo (VPS Paga e Docker):**

Quando os professores liberarem a VPS paga (que presumidamente terá mais núcleos e memória RAM), o uso do Docker deixará de ser um gargalo para se tornar a nossa **maior vantagem arquitetural**. A adoção de contêineres será necessária pelos seguintes motivos:

- **Isolamento de Processos:** O ecossistema do Atendimento APAE tem peças distintas: o Backend (API), o Frontend (Web) e o Banco de Dados. Rodar tudo isso solto no sistema operacional pode gerar conflitos de portas ou de versões. Com o Docker, cada serviço roda em sua própria "caixa" blindada.
- **Fim da Síndrome do "Funciona na minha máquina":** Ao conteinerizar, nós empacotamos o sistema junto com a versão exata do Java 21 e do sistema operacional interno. Quando o código for para a VPS paga, não precisarei instalar pacotes de linguagem na máquina; ela só precisará ter o Docker instalado e o sistema rodará perfeitamente.
- **Orquestração e Agilidade:** Com a introdução de um arquivo `docker-compose.yml` no nosso repositório, qualquer membro da equipe conseguirá baixar atualizações, recriar o banco de dados e levantar toda a arquitetura com um único comando no terminal da nuvem (`docker-compose up -d -build`).

# O plano free vai aguentar as necessidades?

Para o cenário atual do projeto (focado em testes e homologação), o plano *Free Tier* — com os devidos ajustes de Swap — aguenta rodar a nossa aplicação. No entanto, olhando para o contexto geral da arquitetura definitiva, que exigirá múltiplos serviços e o banco de dados rodando de forma unificada e conteinerizada, a transição para a VPS paga será inevitável.