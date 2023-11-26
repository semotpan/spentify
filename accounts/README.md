# Accounts - service for managing user accounts

---

### Environment Variables _(:default)_

* `SERVER_PORT` - server port run _(:8080)_
* `POSTGRES_HOST` - postgres database host _(:localhost)_
* `POSTGRES_PORT` - postgres database port _(:5432)_
* `POSTGRES_DB_NAME` - postgres database name _(:accounts_db)_
* `POSTGRES_DB_USER` - postgres database user _(:application)_
* `POSTGRES_DB_PASSWORD` - postgres database password _(:secret)_
* `KAFKA_SERVERS` - kafka cluster servers _(:PLAINTEXT://kafka:9092,PLAINTEXT_HOST://localhost:29092)_
* `CONNECT_SERVER_URI` - kafka connect server uri _(:http://localhost:8083/connectors)_

---

### Docker Image Build

#### Docker image destination

* It builds image taged with _:latest_ and git _:sha_ 

* Configure the plugin by setting the image to push to:

##### Using [Docker Hub Registry](https://hub.docker.com/)

Make sure you have a [docker-credential-helper](https://github.com/docker/docker-credential-helpers#available-programs)
set up. See [jib-doc](https://github.com/GoogleContainerTools/jib/tree/master/jib-maven-plugin#configuration)

### Build your image

`./mvnw compile jib:build -Dimage=docker.io/motpansergiu/spentify-accounts`

### Build to Docker (local) daemon

`./mvnw compile jib:dockerBuild -Dimage=docker.io/motpansergiu/spentify-accounts`

---

### Health Check

`http://localhost:8080/api/actuator/health`

---

### Swagger REST API Docs

`http://localhost:8080/api/swagger-ui/index.html`

---

### Local Development Additional Tools:

#### Required Tools

* `Java 21`
* `Docker`, `Docker-Compose`

#### Run locally using mvn and docker-compose (default environment variables)

* `./mvnw spring-boot:run -Dspring-boot.run.arguments="--spring.docker.compose.enabled=true --kafka.topic.outbox.events.replicas=1"`
  - it starts the docker-compose using _compose.yaml_ or attaches to the existing service started from _compose.yaml_

#### Testing

* `./mvnw test` - unit tests
* `./mvnw verify` - unit + integration tests

#### Docker-Compose

`docker-compose -f compose.yaml up -d`

    docker run --name accounts \
      -p 8080:8080 \
      --network accounts_default \
      -e POSTGRES_HOST=postgres \
      -e KAFKA_SERVERS=http://kafka:9092 \
      -e CONNECT_SERVER_URI=http://debezium:8083/connectors \
      -e kafka.topic.outbox.events.replicas=1
      -e kafka.topic.outbox.events.partitions=3
      motpansergiu/spentify-accounts:latest

#### Kafka Connect

`http://localhost:8086/`

#### Kafdrop (Kafka Cluster Overview)

`http://localhost:9000/`
