# Execução Local do SonarQube

Este documento fornece as instruções para configurar e executar a análise estática de código com o SonarQube localmente, abrangendo tanto o frontend quanto o backend do repositório Atendimento da APAE.

## 1. Pré-requisitos

Para rodar a análise, você precisará ter instalados:

* Docker (e Docker Compose)
* JDK 21

Nota sobre o Backend: o projeto utiliza o Maven Wrapper (`mvnw`), portanto não é necessário ter o Maven instalado — o wrapper baixa e utiliza a versão correta automaticamente.

Nota sobre o Frontend: o scanner do frontend roda em um container Docker; não é necessário ter o Node instalado para esta etapa.

Configuração do sistema (Elasticsearch):

O SonarQube usa Elasticsearch e exige `vm.max_map_count >= 262144`.

Verifique com:

```
cat /proc/sys/vm/max_map_count
```

Se necessário, aumente temporariamente com `sudo sysctl -w vm.max_map_count=262144` ou permanentemente em `/etc/sysctl.conf`.

## 2. Subir o servidor

A partir da raiz do repositório, inicie o SonarQube com:

```
docker compose -f docker-compose.sonar.yml up
```

(Rodar sem `-d` mostra os logs em tempo real.)

⚠️ Aviso sobre containers órfãos: se o Docker sugerir `--remove-orphans`, não use — isso pode interromper outros serviços em execução no host.

Aguarde nos logs até a linha `SonarQube is operational`.

Abra uma nova aba/janela de terminal para os passos seguintes, pois o processo ocupará este terminal.

O painel do SonarQube estará disponível em: <http://localhost:9500>

## 3. Gerar o token

Para que os scanners enviem relatórios ao servidor, faça o primeiro acesso e gere um token:

1. Acesse <http://localhost:9500> e faça login com `admin` / `admin`.
2. O SonarQube exigirá troca obrigatória de senha no primeiro acesso. Defina uma nova senha com no mínimo 12 caracteres.

Após trocar a senha, gere um token em **My Account** > **Security** > **Generate Tokens**:

* Name: escolha um nome (ex: atendimento-local-token)
* Type: Token de usuário (User Token)
* Expires in: conforme desejar

Não use o Project Analysis Token — ele não funciona para a criação inicial de projetos.

Exporte o token para a variável de ambiente no terminal que executará os scanners:

```
export SONAR_TOKEN="cole_seu_token_aqui"
```

Nunca mantenha o token em arquivos versionados.

## 4. Analisar o backend

O código do backend Java está contido no diretório `backend/atendimento`. A partir da raiz do repositório:

```
cd backend/atendimento
```

Com `SONAR_TOKEN` exportado e o servidor rodando, execute a análise com o Maven Wrapper:

```
./mvnw clean verify sonar:sonar 
```

Após `BUILD SUCCESS`, retorne à raiz:

```
cd ../..
```

Por que esses parâmetros são necessários?

* `verify`: garante a compilação do código e a geração dos arquivos `.class` em `target/classes`, necessários para a análise de bytecode do SonarQube.

## 5. Analisar o frontend

A partir da raiz do projeto, execute:

```
./.scripts/sonar-scan-frontend.sh
```

(Certifique-se de que `SONAR_TOKEN` continua exportado no terminal em uso.)

O script executa o `sonar-scanner` em container e monta o diretório do frontend dentro do container para envio ao servidor.

## 6. Ler o painel

Após as análises concluírem, acesse <http://localhost:9500/projects> e abra os projetos:

* `apae-atendimento-backend`
* `apae-atendimento-frontend`

As principais métricas exibidas no painel são:

* Security — vulnerabilidades de segurança
* Reliability — bugs e problemas que afetam execução
* Maintainability — code smells e dívida técnica
* Security Hotspot — pontos que exigem revisão manual
* Duplications — linhas duplicadas

A aba **Issues** lista os detalhes específicos de cada problema encontrado.

## 7. Cobertura e observações

É esperado que a cobertura apareça baixa ou zerada:

* **Frontend (`apae-atendimento-frontend`)**: O scanner local procura por `coverage/lcov.info`. Se esse arquivo não existir, a cobertura será reportada como `0.0%`.

Isso reflete o estado atual das suítes de testes locais e não indica erro de configuração do SonarQube.

## 8. Encerrar e limpar

Para derrubar o ambiente mantendo os dados:

```
docker compose -f docker-compose.sonar.yml down
```

Para derrubar e remover volumes (reset completo — senha admin volta para `admin/admin`):

```
docker compose -f docker-compose.sonar.yml down -v
```

Use o `-v` com cuidado — ele remove toda a persistência local do SonarQube.
