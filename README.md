# QuAntiL Gateway

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Gateway CI](https://github.com/UST-QuAntiL/Gateway/actions/workflows/build.yml/badge.svg?branch=main)](https://github.com/UST-QuAntiL/Gateway/actions/workflows/build.yml)

This project provides a facade to be used as a reverse proxy between the QuAntiL user interfaces and the backend
services.
It is built upon the [Spring Cloud Gateway](https://spring.io/projects/spring-cloud-gateway) to intercept and forward
requests coming from outside the network.

## Build

1. Run `mvn package -DskipTests` inside the root folder using [OpenJdk 17](https://openjdk.java.net/projects/jdk/17/) or above.
2. When completed, the product can be found in the `target` folder.

## Usage

The Gateway listens per default on port `6473`.
Hence, if you send a request to <http://localhost:6473> you can reach it. However, it will return a 404 response, as the root resource is currently not mapped.

### Gateways

Currently, the following gateways are implemented:

#### NISQ-Analyzer Gateway

This gateway routes requests following the schema ``/nisq-analyzer*`` to the [NISQ-Analyzer](https://github.com/QuAntiL/nisq-analyzer) service that is configured in the `org.planqk.gateway.nisq.analyzer.uri` property, i.e., `NISQ_ANALYZER_URI` in Docker.
Additionally, there is a special handling of `POST` requests that may contain a Quantum Computing Provider token.
Hence, the following requests are automatically enriched with a token if its not already set:

- `/nisq-analyzer/compiler-selection`
- `/nisq-analyzer/qpu-selection`
- `/nisq-analyzer/selection`


## Configuration

To configure the gateway to automatically inject an access-token for IBM, add your token to
the [application.yml](src/main/resources/application.yaml) as follows:

```yaml
org:
  planqk:
    gateway:
      tokens:
        ibm: myIbmQuantumToken
```

As an alternative, you can also pass it directly to the application during startup by passing the corresponding property
as environment variable:

````shell
java -jar gateway.jar --org.planqk.gateway.tokens.ibm=myIbmQuantumToken
````

This is also possible in IntelliJ.
Simply configure the GatewayApplication run configuration to pass arguments to the program and enter:
``--org.planqk.gateway.tokens.ibm=myIbmQuantumToken``


## Run with Docker

To start the QuAntiL Gateway as a Docker container, simply run the following command:

```shell
docker run -p'6473:6473' -e IBM_QUANTUM_TOKEN=myIbmQuantumToken planqk/gateway
```
