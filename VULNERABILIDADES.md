# Relatorio de vulnerabilidades

## Escopo e estado

O Trivy identificou vulnerabilidades HIGH e CRITICAL no POM do backend e no lockfile do frontend. Foram configuradas atualizacoes de versao para as dependencias vulneraveis, mas a aplicacao completa das atualizacoes ainda depende de acesso ao Maven Central e ao registry do npm para baixar os artefatos e regenerar o lockfile. Portanto, este relatorio nao declara as vulnerabilidades como corrigidas nem o scan como limpo.

## Backend

O parent Spring Boot foi atualizado para 3.5.16. Foram definidos os seguintes overrides no `backend/atendimento/pom.xml`, alinhados as versoes corrigidas indicadas pelo Trivy:

| Dependencia | Versao configurada | CVEs representativas observadas |
|---|---:|---|
| Jackson | 2.21.7 | CVE-2026-68494, CVE-2026-68497, CVE-2026-89407, CVE-2026-89425, CVE-2026-91776, CVE-2026-91777 |
| Guava | 33.7.2-jre | CVE-2026-102554 |
| Netty | 4.1.137.Final | CVE-2026-33870, CVE-2026-42583, CVE-2026-55831, CVE-2026-55833, CVE-2026-56745, CVE-2026-56819, CVE-2026-59901, CVE-2026-75595 |
| Apache Tomcat | 10.1.59 | CVE-2026-53404, CVE-2026-55276, CVE-2026-65182, CVE-2026-65637, CVE-2026-65905, CVE-2026-68525 |
| Bouncy Castle | 1.85 | CVE-2025-14813, CVE-2026-5598, CVE-2026-8763, CVE-2026-13506 |
| PostgreSQL JDBC | 42.7.12 | CVE-2026-54291 |
| jsoup | 1.23.2 | CVE-2026-75140 |

As versoes fixadas atualizam as dependencias dentro das linhas compativeis indicadas pelo scanner; o parent Spring Boot e mantido na linha 3.5.

## Frontend

O `package.json` foi ajustado para adotar as seguintes versoes corrigidas e para substituir dependencias transitivas vulneraveis:

| Dependencia | Versao configurada |
|---|---:|
| axios | 1.20.0 |
| brace-expansion | 1.1.20 e 2.1.6 |
| browserslist | 4.28.7 |
| form-data | 4.0.6 |
| minimatch | 3.1.3 e 9.0.7 |
| nanoid | 3.3.18 |
| postcss | 8.5.18 |
| sharp | 0.35.5 |
| source-map-js | 1.2.2 |
| tar | 7.5.21 |

Tambem foram selecionadas atualizacoes do Next.js (16.4.1), pnpm (10.34.2) e react-pdf (9.2.1, com pdfjs-dist 4.4.168), pois o scan encontrou vulnerabilidades HIGH/CRITICAL nessas dependencias alem das listadas inicialmente.

## Excecoes temporarias

`CVE-2026-47884` e `CVE-2026-47890`, associadas ao `spring-webmvc`, foram adicionadas ao `.trivyignore`. A excecao foi solicitada porque, na linha Spring Boot adotada, nao ha correcao disponivel sem breaking changes. Essas excecoes nao substituem uma atualizacao futura: devem ser revistas quando houver uma versao compativel corrigida.

## Validacao e pendencias

- `mvn clean test` nao concluiu: a resolucao de plugins/dependencias do Maven Central ficou indisponivel. A tentativa offline tambem nao pode resolver os BOMs Jackson 2.21.7 e Netty 4.1.137.Final ainda ausentes do cache local.
- `pnpm install` nao concluiu porque as requisicoes ao `registry.npmjs.org` expiraram. Assim, `pnpm-lock.yaml` ainda nao foi regenerado e continua apontando para as versoes vulneraveis.
- `pnpm test` passou: 3 suites e 11 testes. `pnpm build` passou com os pacotes que ja estavam instalados antes das atualizacoes; ambos nao validam as novas versoes.
- O scan Trivy mais recente reportou 30 vulnerabilidades no POM e 72 no lockfile. O scanner tambem informou que nao conseguiu determinar versoes de algumas dependencias do backend. A validacao final de zero vulnerabilidades permanece pendente da resolucao das dependencias e da regeneracao do lockfile.
