# syntax=docker/dockerfile:1.7

FROM maven:3.9.9-eclipse-temurin-21-alpine@sha256:5a8b906c4faa11d33f6c74758f67db8eac25441e14b0729f6d50ff78992be58a AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN --mount=type=cache,target=/root/.m2 \
    mvn -B package -DskipTests


FROM gcr.io/distroless/java21-debian13:nonroot@sha256:bb0b3c7edc4417acdf76ea0f52bb5fae28881fe05aae6cc55af4cc4cb0200d2d AS runtime

ARG APP_VERSION
ARG VCS_REF

LABEL org.opencontainers.image.title="APAE Atendimento" \
      org.opencontainers.image.description="Backend do APAE Atendimento" \
      org.opencontainers.image.source="https://github.com/IFPBEsp/APAE-atendimento" \
      org.opencontainers.image.licenses="MIT" \
      org.opencontainers.image.version=$APP_VERSION \
      org.opencontainers.image.revision=$VCS_REF

WORKDIR /app

COPY --from=build --chown=65532:65532 \
    /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["/usr/bin/java"]
CMD ["-jar", "app.jar"]