# Accounts - service for managing user accounts

---

### Environment Variables _(:default)_

* `SERVER_PORT` - server port run _(:8080)_
* `DB_HOST` - postgres database host _(:localhost)_
* `DB_PORT` - postgres database port _(:5432)_
* `DB_NAME` - postgres database name _(:accounts_db)_
* `DB_USER` - postgres database user _(:application)_
* `DB_PASSWORD` - postgres database password _(:secret)_
* `BOOTSTRAP_SERVERS` - kafka cluster servers _(:PLAINTEXT://kafka:9092,PLAINTEXT_HOST://localhost:29092)_
* `CONNECT_SERVER_URI` - kafka connect server uri _(:http://localhost:8083/connectors)_
* `KAFKA_OUTBOX_CONNECTOR_CONFIG_FILE` - outbox connector config file _(:./connector/accounts-outbox-connector.json)_
* `TOPIC_OUTBOX_EVENTS_REPLICAS` - topic outbox event replicas _(:1)_
* `TOPIC_OUTBOX_EVENTS_PARTITIONS` - topic outbox event partitions _(:3)_

### Health Check

`http://localhost:8080/api/actuator/health`

### Swagger REST API Docs

`http://localhost:8080/api/swagger-ui/index.html`

### Local Development Additional Tools:

#### Required Tools

* `Java 21`
* `Docker`
* `Docker-Compose`

#### Run locally using docker-compose (default environment variables)

* `./mvnw spring-boot:run`

#### Testing

* `./mvnw test` - unit tests
* `./mvnw verify` - unit + integration tests

#### Kafka Connect

`http://localhost:8086/`

#### Kafdrop (Kafka Cluster Overview)

`http://localhost:9000/`
