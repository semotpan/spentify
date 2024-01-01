# Expenses - service for managing expenses

---

### Environment Variables _(:default)_

* `SERVER_PORT` - server port run _(:8080)_
* `POSTGRES_HOST` - postgres database host _(:localhost)_
* `POSTGRES_PORT` - postgres database port _(:5432)_
* `POSTGRES_DB_NAME` - postgres database name _(:expensesdb)_
* `POSTGRES_DB_USER` - postgres database user _(:application)_
* `POSTGRES_DB_PASSWORD` - postgres database password _(:secret)_

---
### Local Development Additional Tools:

#### Required Tools

* `Java 21`
* `Docker`, `Docker-Compose`


### Run locally using mvn and docker-compose (default environment variables) 
```console
/infra-local % docker-compose up --build
```

* Using `spring-boot:run`
```console
/expenses % ./mvnw spring-boot:run -Dspring-boot.run.arguments="--SERVER_PORT=8081 --POSTGRES_PORT=5433"
```

### Health Check
```console
% http://localhost:8081/api/actuator/health
```

---

### Swagger REST API Docs
```console
% http://localhost:8081/api/swagger-ui/index.html
```
---

#### Kafka Connect
```console
% http://localhost:8086/
```

#### Kafdrop (Kafka Cluster Overview)
```console
% http://localhost:9000/
```
