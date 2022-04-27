# QuAntiL Gateway

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Gateway CI](https://github.com/UST-QuAntiL/Gateway/actions/workflows/build.yml/badge.svg?branch=main)](https://github.com/UST-QuAntiL/Gateway/actions/workflows/build.yml)

This project provides a facade to be used as a reverse proxy between the QuAntiL user interfaces and the backend
services.
It is built upon the [Spring Cloud Gateway](https://spring.io/projects/spring-cloud-gateway) to intercept and forward
requests coming from outside the network.

## Build

1. Run `mvn package -DskipTests` inside the root folder.
2. When completed, the product can be found in the `target` folder.

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

This is also possible to configure in IntelliJ.
Simply configure the GatewayApplication run configuration to pass arguments to the program and enter:
``--org.planqk.gateway.tokens.ibm=myIbmQuantumToken``
