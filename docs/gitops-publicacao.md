# Publicacao e notificacao GitOps

O caller `.github/workflows/docker-publish.yml` publica somente em pushes e execucoes
manuais na `main`. PRs que alteram o caller executam as validacoes, sem publicar
imagens nem acessar o App de notificacao.

A publicacao depende de lint, build, verificacao de tipos e dos testes
existentes do frontend, alem de `mvnw verify` no backend.
Os testes sao bloqueantes e nao usam `continue-on-error`.
O workflow reutilizavel de publicacao usa `@main` do APAE-INFRA, que precisa
receber as correcoes da issue #106 antes do merge deste caller na `main` do
produto. Essa referencia seleciona a automacao; o codigo publicado tambem
precisa vir da `main` do produto.

As permissoes de pacotes, OIDC e atestacoes ficam somente nos jobs de
publicacao. A notificacao usa o environment `gitops-dispatch`, com a variavel
`GITOPS_DISPATCH_APP_ID` e o secret `GITOPS_DISPATCH_APP_PRIVATE_KEY` de um App
com `contents: write` somente no APAE-INFRA. O App de abertura de PRs e sua
chave permanecem exclusivamente na INFRA.

Frontend e backend sao publicados com o mesmo commit e execucao. O job `prepare-release` rejeita outputs incompletos antes do evento `gitops-release-published`.
A execucao manual na `main` tambem prepara e envia a notificacao. Uma falha
nas validacoes, publicacao ou preparacao impede a notificacao.

## Configuracao dos frontends

Os argumentos de build sao fixos e iguais nos dois ambientes. Nao usar
variaveis de deploy para reconstruir a imagem durante a promocao.
`NEXT_PUBLIC_API_URL=/atendimento/api` e relativo ao mesmo dominio publico.
`BACKEND_URL=http://atendimento:8080` usa o Service Kubernetes no namespace do frontend, com o mesmo nome nos dois ambientes.

O receptor da INFRA verifica origem, digest e proveniencia antes do PR de
homologacao. A aprovacao, promocao e rollback reutilizam os mesmos digests,
com revisao humana e sync manual. O caller nao atualiza producao diretamente.

## Validacao

```bash
actionlint .github/workflows/docker-publish.yml
git diff --check
```

Checks de PR nao comprovam publicacao no GHCR. O aceite da #106 exige uma
execucao real na `main` e evidencias de notificacao, homologacao, promocao
sem rebuild e rollback.

## Imagem MinIO dos testes de integracao

O teste `MinioStorageIntegrationTest` compila uma imagem exclusiva de teste a
partir do repositorio oficial `minio/minio`, no commit
`9e49d5e7a648f00e26f2246f4dc28e6b07f8c84a`
(`RELEASE.2025-10-15T17-29-55Z`). A imagem oficial de Go tambem esta fixada
por digest no Dockerfile de teste em `src/test/resources/minio-test.Dockerfile`.
Isso evita depender das imagens publicas de MinIO que deixaram de estar
acessiveis nos registros consultados. Nao se trata de uma imagem de producao.

O Testcontainers compila uma vez e utiliza o mesmo binario nas duas instancias
que compartilham um volume temporario. O teste continua verificando criacao
do bucket, gravacao e leitura do arquivo depois de recriar o container.
A primeira execucao precisa de acesso ao Docker Hub e ao proxy/checksum
publico de modulos Go; as seguintes podem reutilizar o cache do Docker.

```bash
cd backend/atendimento
bash ./mvnw --batch-mode --no-transfer-progress verify
```
