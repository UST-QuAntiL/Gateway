FROM maven:3-openjdk-17 as builder

COPY . /tmp/gateway
WORKDIR /tmp/gateway
RUN mvn package -B -DskipTests

FROM openjdk:17-buster
LABEL maintainer = "Lukas Harzenetter <lharzenetter@gmx.de>"

ARG DOCKERIZE_VERSION=v0.6.1

ENV PORT 6473
ENV LOGGING_LEVEL INFO

ENV NISQ_ANALYZER_URI http://host.docker.internal:5010
ENV IBM_QUANTUM_TOKEN ''

COPY --from=builder /tmp/gateway/target/gateway-0.0.1-SNAPSHOT.jar /var/gateway/gateway.jar
COPY .docker /var/.docker

RUN wget https://github.com/jwilder/dockerize/releases/download/$DOCKERIZE_VERSION/dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz \
    && tar -C /usr/local/bin -xzvf dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz \
    && rm dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz

EXPOSE 6473

CMD dockerize -template /var/.docker/application.yaml.tpl:/var/gateway/application.yaml \
    java -jar /var/gateway/gateway.jar
