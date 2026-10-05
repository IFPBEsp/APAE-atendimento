# Execução Local do SonarQube

Este documento fornece as instruções para configurar e executar a análise estática de código com o SonarQube localmente, abrangendo tanto o frontend quanto o backend do repositório do Atendimento da APAE.

## 1. Pré-requisitos

Para rodar a análise, você precisará ter instalados:

* **Docker** (e Docker Compose)
* **JDK 21**

**Nota sobre o Backend:** O projeto utiliza o **Maven Wrapper (`mvnw`)**, portanto **não é necessário ter o Maven instalado** na sua máquina — o wrapper baixa e utiliza a versão correta do Maven automaticamente.

**Nota sobre o Frontend:** O scanner do frontend roda embutido em um container Docker, portanto **não é necessário ter o Node instalado** na sua máquina para esta etapa.

**Configuração do Sistema Operacional (Elasticsearch):**
O SonarQube utiliza o Elasticsearch por baixo dos panos, que exige um limite de áreas de memória virtual mapeadas de pelo menos `262144`.

Verifique o valor atual na sua máquina com o comando:

```bash
cat /proc/sys/vm/max_map_count
```

Caso o valor retornado seja menor que `262144`, você precisará aumentá-lo (em distribuições Linux, temporariamente com `sudo sysctl -w vm.max_map_count=262144` ou permanentemente editando o arquivo `/etc/sysctl.conf`). A maioria das distribuições já vem com um valor acima disso por padrão.

## 2. Subir o servidor

Antes de iniciar, verifique se a porta `9501` está livre:

```bash
docker ps
```

Caso exista alguma instância anterior do SonarQube em outra organização ou projeto do ecossistema APAE ocupando a porta `9501`, será necessário interromper esse container antes de prosseguir. Sem isso, o comando de subida falhará com conflito de bind na porta `9501`.

Para iniciar o SonarQube localmente, execute o comando abaixo a partir da raiz do repositório:

```bash
SONAR_PORT=9501 docker compose -f docker-compose.sonar.yml up
```

**Por que a porta `9501` é obrigatória neste repositório?**
O arquivo `docker-compose.sonar.yml` deste serviço publica a porta do SonarQube com o padrão `${SONAR_PORT:-9500}`. Nesse projeto do Atendimento da APAE, a porta `9500` já é usada pelo ambiente do SonarQube do repositório Geral, enquanto a porta `9502` é reservada para a instância da Gestão Escolar. Definir explicitamente `SONAR_PORT=9501` garante que este serviço fique isolado e seja possível rodar os três ambientes simultaneamente no mesmo ambiente local sem conflitos de porta.

*(Rodar sem a flag `-d` fará com que você veja os logs em tempo real).*

> ⚠️ **Aviso sobre containers órfãos:** Durante a inicialização, o Docker pode exibir o alerta `Found orphan containers` e sugerir a flag `--remove-orphans`. **Não utilize essa flag**, pois ela derrubaria containers de outros projetos e ambientes que podem estar em execução no mesmo host.

**Aguardando o servidor:** No log, a mensagem `Web Server is operational` aparecerá primeiro, mas você deve aguardar até que a mensagem final **`SonarQube is operational`** seja exibida.

Como o servidor ficará rodando e ocupando este terminal, **abra uma nova aba ou janela de terminal** para executar os passos seguintes.

O painel do SonarQube estará disponível em: <http://localhost:9501>

## 3. Gerar o token

Para que os scanners consigam enviar os relatórios de código para o servidor, você precisa realizar o primeiro acesso e gerar um token.

### Primeiro login e troca de senha

1. Acesse <http://localhost:9501> e faça login com o usuário e senha padrão: `admin` / `admin`.
2. O SonarQube exigirá **obrigatoriamente a troca de senha** no primeiro acesso:
   * No campo **Old Password**, digite `admin`.
   * Defina uma nova senha com **no mínimo 12 caracteres** e diferente de `admin`.
   *(Nota: se você executar `docker compose -f docker-compose.sonar.yml down -v` futuramente, os volumes serão removidos e a senha voltará ao padrão `admin/admin`.)*

### Gerando o token

1. Após logar com a nova senha, clique no ícone do seu perfil no canto superior direito e vá em **My Account** > **Security** > **Generate Tokens**.
2. Preencha os campos da seguinte forma:
   * **Name:** Dê um nome de sua escolha (ex: `atendimento-local-token`).
   * **Type:** O campo vem em branco por padrão. Use a opção **Token de usuário** (*User Token*), que é a opção preferencial para este fluxo de análise.
   * **Expires in:** Pode manter o padrão sugerido de 30 dias.
3. Clique em **Generate** e copie o token gerado.

> ⚠️ **Não use o *Project Analysis Token*.** Esse tipo de token só funciona para projetos que já existem no painel. Como esta será a primeira vez que a análise rodará, os projetos ainda não existem no SonarQube e o scanner falhará ao tentar criá-los.

Após gerar o token, exporte-o para a variável de ambiente `SONAR_TOKEN` no seu terminal (na nova aba que você abriu):

```bash
export SONAR_TOKEN="cole_seu_token_aqui"
```

> ⚠️ **Atenção:** O token deve ir para a variável de ambiente `SONAR_TOKEN` e **nunca** para dentro de um arquivo versionado no repositório.

## 4. Analisar o backend

O código do backend Java está contido no diretório `backend/atendimento`. Navegue até ele a partir da raiz do repositório:

```bash
cd backend/atendimento
```

Com o token na variável de ambiente e o servidor rodando, execute a análise com o comando completo do Maven Wrapper:

```bash
./mvnw clean verify sonar:sonar -DskipTests -Dsonar.host.url=http://localhost:9501
```

Depois do término da execução com sucesso (`BUILD SUCCESS`), retorne para a raiz do repositório:

```bash
cd ../..
```

**Por que cada parâmetro é obrigatório?**

* **`verify`**: garante a compilação do código e a geração dos arquivos `.class` em `target/classes`, exigidos pelo scanner de Java. Sem essa etapa, o SonarQube não encontra o bytecode e a análise não acontece. O comando `sonar:sonar` sozinho não é suficiente para analisar corretamente o módulo.
* **`-DskipTests`**: a suíte de testes do backend ainda pode apresentar falhas pré-existentes que interromperiam a build antes da fase de análise. Este parâmetro contorna essa interrupção para permitir o envio do relatório ao SonarQube sem bloquear a execução local de análise.
* **`-Dsonar.host.url=http://localhost:9501`**: força o scanner a enviar a análise para a porta deste serviço, em vez do endereço padrão do plugin (`http://localhost:9000`), evitando que o relatório seja entregue ao servidor errado.

## 5. Analisar o frontend

Certifique-se de que está na raiz do projeto e execute o script preparado apontando o SonarQube correto:

```bash
SONAR_HOST_URL=http://localhost:9501 ./.scripts/sonar-scan-frontend.sh
```

**Por que a variável `SONAR_HOST_URL` é obrigatória?**
O script usa `http://localhost:9500` como valor padrão, mas neste ambiente o serviço do Atendimento da APAE roda na porta `9501`. Definir `SONAR_HOST_URL` explicitamente garante que o relatório de análise seja enviado ao servidor correto.

*(Certifique-se de que a variável `SONAR_TOKEN` continua exportada no ambiente do terminal em uso).*

**O que o script faz por baixo?**
Ele executa o scanner em container. O script baixa e executa a imagem Docker do `sonar-scanner-cli`, monta o diretório do módulo atual dentro do container e envia os dados para o servidor definido em `SONAR_HOST_URL` (porta 9501), sem exigir Node instalado nem configurações locais adicionais.

## 6. Ler o painel

Após os dois comandos finalizarem com sucesso, volte ao navegador em <http://localhost:9501> e clique na aba **Projects** no menu superior, ou acesse diretamente <http://localhost:9501/projects>.

Você verá as duas chaves de projeto criadas listadas em cards:

* `apae-atendimento-backend`
* `apae-atendimento-frontend`

Ao clicar em qualquer um deles, a aba principal exibe o **Quality Gate** e as métricas de qualidade. As principais categorias são:

* **Security:** vulnerabilidades de segurança no código.
* **Reliability:** confiabilidade do software, indicando presença de bugs.
* **Maintainability:** manutenibilidade do código, incluindo Code Smells e dívida técnica.
* **Security Hotspot:** pontos de atenção de segurança que exigem revisão manual.
* **Duplications:** porcentagem e quantidade de linhas de código duplicado.

A aba **Issues** lista os detalhes específicos de cada problema encontrado no código analisado.

## 7. Cobertura e encerramento

Atualmente, é esperado que a cobertura de código apareça baixa ou zerada:

* **Backend (`apae-atendimento-backend`):** o relatório de cobertura (`jacoco.xml`) seria gerado durante a fase `verify` do Maven. Como o comando local usa `-DskipTests` para contornar falhas pré-existentes da suíte de testes, a execução dos testes é pulada e, por consequência, a cobertura não é coletada. Isso explica por que a métrica pode aparecer zerada ou muito baixa.
* **Frontend (`apae-atendimento-frontend`):** o arquivo de configuração do módulo espera o relatório no caminho `coverage/lcov.info`. Como os runners de testes do frontend ainda não estão gerando esse arquivo no fluxo local, a cobertura aparece como `0.0%`.

Isso deve ser entendido como um estado temporário da automação de testes e **não como erro de configuração do ambiente local do SonarQube**.

**Por que o Quality Gate pode aparecer como "Passed" mesmo com cobertura zerada?**
Isso é comportamento esperado no momento: o Quality Gate padrão do SonarQube ainda não contém uma regra que bloqueie a análise somente pela ausência de cobertura. Em outras palavras, a análise não é reprovada automaticamente por essa condição. Quando as suítes de testes forem implementadas e a métrica começar a ser coletada de fato, novas regras de cobertura poderão ser adicionadas ao gate para validação mais rígida.

## 8. Encerrar e limpar

Quando finalizar o trabalho e quiser derrubar o container, você tem duas opções:

### Encerramento seguro sem limpar dados

```bash
docker compose -f docker-compose.sonar.yml down
```

Esse comando derruba o container e mantém o histórico de análises salvo para a próxima vez, sem apagar os dados do SonarQube.

### Encerramento completo com reset de senha

```bash
docker compose -f docker-compose.sonar.yml down -v
```

Esse comando também remove os volumes do banco de dados e do Elasticsearch. Isso apaga o histórico de análises e **reseta a senha do usuário `admin` para o padrão `admin/admin`**. Use essa opção somente se for necessário limpar configurações corrompidas ou recomeçar do zero.

> ⚠️ O comando com `-v` deve ser usado com cuidado porque remove toda a persistência local do SonarQube, incluindo a senha inicial do primeiro login.

