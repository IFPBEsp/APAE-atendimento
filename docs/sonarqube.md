Execução Local do SonarQube

Este documento fornece as instruções para configurar e executar a análise estática de código com o SonarQube localmente, abrangendo tanto o frontend quanto o backend do repositório de Atendimento da APAE.

## 1. Pré-requisitos

Para rodar a análise, você precisará ter instalados:
* **Docker** (e Docker Compose)
* **JDK 21**

*(O projeto utiliza o Maven Wrapper (`mvnw`), o que dispensa a instalação prévia do Maven na sua máquina).*

**Nota sobre o Frontend:** O scanner do frontend roda embutido em um container Docker, portanto **não é necessário ter o Node instalado** na sua máquina para esta etapa.

**Configuração do Sistema Operacional (Elasticsearch):**
O SonarQube utiliza o Elasticsearch por baixo dos panos, que exige um limite de áreas de memória virtual mapeadas de pelo menos `262144`.
Verifique o valor atual na sua máquina com o comando:

```
cat /proc/sys/vm/max_map_count
```

Caso o valor retornado seja menor que `262144`, você precisará aumentá-lo (em distribuições Linux, temporariamente com `sudo sysctl -w vm.max_map_count=262144` ou permanentemente editando o arquivo `/etc/sysctl.conf`). A maioria das distribuições já vem com um valor acima disso por padrão.

## 2. Subir o servidor

**Antes de inicializar, verifique se a porta 9501 está livre:**
Execute o comando abaixo e confira se algum container já está ocupando a porta `9501`:

```
docker ps
```

Caso você já tenha rodado o SonarQube de outro repositório do ecossistema APAE (ou exista um container antigo publicado nesta mesma porta), será necessário **parar esse container anterior** (ex: `docker stop <nome_do_container>`) antes de prosseguir. Sem isso, o `docker compose up` falhará com erro de conflito de *bind* da porta `9501`, pois ela já estará ocupada pela instância anterior.

Para iniciar o SonarQube localmente, utilize o arquivo de composição do repositório executando o comando abaixo a partir da raiz:

```
SONAR_PORT=9501 docker compose -f docker-compose.sonar.yml up
```

**Por que a variável `SONAR_PORT=9501` é obrigatória?**
O arquivo `docker-compose.sonar.yml` deste repositório publica a porta do serviço com o padrão `${SONAR_PORT:-9500}`. Se o comando for executado sem a variável, o servidor subirá na porta **9500** — a mesma utilizada pelo SonarQube do repositório APAE Geral — gerando conflito de porta. Definir `SONAR_PORT=9501` explicitamente antes do comando garante que este serviço fique isolado na porta 9501, permitindo que o desenvolvedor tenha os três servidores do ecossistema APAE (Geral, Atendimento e Gestão Escolar) de pé ao mesmo tempo na sua máquina local.

*(Rodar sem a flag `-d` fará com que você veja os logs em tempo real).*

> ⚠️ **Aviso sobre containers órfãos:** Durante a inicialização, o Docker pode exibir o alerta `Found orphan containers` e sugerir a flag `--remove-orphans`. **Não utilize essa flag**, pois ela derrubaria os containers de banco de dados e armazenamento de outros projetos.

**Aguardando o servidor:** No log, a mensagem `Web Server is operational` aparecerá primeiro, mas você deve aguardar até que a mensagem final **`SonarQube is operational`** seja exibida.

Como o servidor ficará rodando e ocupando este terminal, **abra uma nova aba ou janela de terminal** para executar os passos seguintes.

O painel do SonarQube estará disponível no endereço: <http://localhost:9501>

## 3. Gerar o token

Para que os scanners consigam enviar os relatórios de código para o servidor, você precisa realizar o primeiro acesso e gerar um token:

### Primeiro login e troca de senha
1. Acesse <http://localhost:9501> e faça login com o usuário e senha padrão: `admin` / `admin`.
2. O SonarQube exigirá **obrigatoriamente a troca de senha** no primeiro acesso:
    * No campo **Old Password**, digite `admin`.
    * Defina uma nova senha que tenha **no mínimo 12 caracteres** e seja diferente de `admin`.
      *(Nota: Caso você execute o comando de limpeza com perda de volumes `down -v` futuramente, a senha voltará ao padrão `admin/admin`).*

### Gerando o token
1. Após logar com a nova senha, clique no ícone do seu perfil no canto superior direito e vá em **My Account** > **Security** > **Generate Tokens**.
2. Preencha os campos da seguinte forma:
    * **Name:** Dê um nome de sua escolha (ex: `local-token`).
    * **Type:** O campo vem em branco por padrão. Adicione a preferência para a opção **Token de usuário** (ou *User Token* / *Token de análise global*).
    * **Expires in:** Pode manter o padrão sugerido de 30 dias.
3. Clique em **Generate** e copie o token gerado.

> ⚠️ **Não use o *Project Analysis Token*.** Esse tipo de token só funciona para projetos que já existem no painel. Como esta será a primeira vez que a análise rodará, os projetos ainda não existem e o scanner falhará ao tentar criá-los.

Após gerar o token, exporte-o para a variável de ambiente `SONAR_TOKEN` no seu terminal (na nova aba que você abriu):
```
export SONAR_TOKEN="cole_seu_token_aqui"
```

> ⚠️ **Atenção:** O token deve ir para a variável de ambiente `SONAR_TOKEN` e **nunca** para dentro de um arquivo versionado no repositório.

## 4. Analisar o backend

O código do backend Java está contido no diretório `backend/atendimento`. Navegue até ele a partir da raiz do repositório:

```
cd backend/atendimento
```

Com o token na variável de ambiente e o servidor rodando, execute a análise com o comando completo do Maven Wrapper:

```
./mvnw clean verify sonar:sonar -DskipTests -Dsonar.host.url=http://localhost:9501
```

**Por que cada parâmetro é obrigatório?**

* **`verify`**: garante a compilação do código e a geração dos arquivos `.class` em `target/classes`, exigidos pelo scanner de Java. Sem essa fase, o SonarQube não encontra o bytecode e a análise não acontece — o comando `sonar:sonar` sozinho não serve para analisar o projeto.
* **`-DskipTests`**: a suíte unitária do backend (em especial a classe `AtendimentoServiceTest`) apresenta falhas pré-existentes que interromperiam a build antes da fase de análise. Este parâmetro contorna essas falhas sem travar o pipeline do SonarQube.
* **`-Dsonar.host.url=http://localhost:9501`**: força o scanner a enviar a análise para a porta 9501 configurada para este serviço, em vez do endereço padrão do plugin (`http://localhost:9000`).

Após o término da execução com sucesso (`BUILD SUCCESS`), retorne para a raiz do repositório:

```
cd ../..
```

## 5. Analisar o frontend

Certifique-se de que está na raiz do projeto e execute o script preparado, garantindo o apontamento da porta correta:

```
SONAR_HOST_URL=http://localhost:9501 ./.scripts/sonar-scan-frontend.sh
```

**Por que a variável `SONAR_HOST_URL` é obrigatória?**
O script utiliza `http://localhost:9500` como valor padrão de `SONAR_HOST_URL`. Como este serviço roda na porta 9501, a variável precisa ser definida explicitamente antes do comando para que o relatório de análise seja enviado ao servidor correto.

*(Certifique-se de que a variável `SONAR_TOKEN` continua exportada no ambiente desse terminal).*

**O que o script faz por baixo?**
Ele roda o scanner em container. O script baixa e executa a imagem Docker do `sonar-scanner-cli`, montando o diretório do módulo atual para dentro do container e enviando os dados para o servidor definido em `SONAR_HOST_URL` (porta 9501) sem exigir Node instalado ou configurações locais complexas na sua máquina.

## 6. Ler o painel

Após os dois comandos finalizarem com sucesso, volte ao navegador em <http://localhost:9501> e clique na aba **Projects** no menu superior (ou acesse diretamente <http://localhost:9501/projects>).

Você verá as duas chaves de projeto criadas listadas em cards:
* `apae-atendimento-backend`
* `apae-atendimento-frontend`

Ao clicar em qualquer um deles, a aba principal exibe o **Quality Gate**, categorizado pelas seguintes métricas:
* **Security:** Vulnerabilidades de segurança no código.
* **Reliability:** Confiabilidade do software, indicando presença de Bugs.
* **Maintainability:** Manutenibilidade do código (Code Smells e dívida técnica).
* **Security Hotspot:** Pontos de atenção de segurança que exigem revisão manual.
* **Duplications:** Porcentagem e linhas de código duplicado.

A aba **Issues** lista os detalhes específicos de cada problema encontrado no código analisado.

## 7. Cobertura

Atualmente, é esperado que a cobertura de código apareça baixa ou zerada:

* **Backend (`apae-atendimento-backend`):** O relatório de cobertura (`jacoco.xml`) seria gerado durante a fase `verify` do Maven. Como o comando local utiliza `-DskipTests` para contornar as falhas pré-existentes da suíte unitária, os testes não são executados e nenhuma cobertura é coletada — a porcentagem reportada reflete o volume de testes que rodarem de fato durante a análise.
* **Frontend (`apae-atendimento-frontend`):** O arquivo de configuração `sonar-project.properties` do módulo espera o relatório de testes no caminho `coverage/lcov.info`. Como os runners de testes do frontend ainda não estão configurados para gerar esse arquivo durante o fluxo local, a cobertura aparece como 0.0%.

Isso deve ser lido como uma etapa futura de implementação das suítes de testes nos repositórios, e **não como um erro de configuração** do seu ambiente local do SonarQube.

**Por que o Quality Gate aparece como "Passed" mesmo com cobertura 0.0%?**
Você pode notar que, mesmo com a cobertura exibindo **0.0%**, o **Quality Gate** do projeto apresentará o status **"Passed"** (verde). Isso é o **comportamento esperado no momento**: o Quality Gate padrão do SonarQube não possui, neste estágio, nenhuma condição que bloqueie a análise pela falta da métrica de cobertura global. Ou seja, a análise não é reprovada pela ausência de cobertura — portanto, não estranhe ao ver o status verde com a cobertura zerada. Quando as suítes de testes forem implementadas e a métrica passar a ser de fato coletada, condições de cobertura poderão ser adicionadas ao Quality Gate para que ele passe a validar (ou reprovar) esse critério.

## 8. Encerrar e limpar

Quando finalizar seu trabalho e quiser derrubar o container, você tem duas opções:

Para derrubar o container mantendo o histórico de análises salvo para a próxima vez:

```
docker compose -f docker-compose.sonar.yml down
```

Para derrubar e **apagar os volumes e o histórico**:

```
docker compose -f docker-compose.sonar.yml down -v
```

> ⚠️ O segundo comando (com o `-v`) apaga os volumes do banco de dados e Elasticsearch. Isso apagará todo o histórico de análises e **resetará a senha do admin de volta para o padrão `admin/admin`**. Use apenas se precisar limpar configurações corrompidas e recomeçar do zero.**
