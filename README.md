# AWS Localstack Integration testing

Testing AWS services through TestContainers -> LocalStack (Docker).

# Prerequisites

- Java: openjdk version "11" (or similar)
- Maven: Apache Maven 3.8.3 (or similar)
- Working docker environment - Docker Desktop 4.6.1 (76265) (or similar)

# Execution

```
mvn clean test -P it-tests
```
