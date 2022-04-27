# QuAntiL Gateway

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

This project provides a facade to be used as a reverse proxy between the QuAntiL user interfaces and the backend services.
It is built upon the [Spring Cloud Gateway](https://spring.io/projects/spring-cloud-gateway) to intercept and forward requests coming from outside the network. 

## Build

1. Run `mvn package -DskipTests` inside the root folder.
2. When completed, the product can be found in the `target` folder.
