# client-info

Mock of the internal client info service. It returns customer details from an in-memory data set.

## Run

Requires JDK 17+ (built with 21).

```sh
./mvnw spring-boot:run
```

## API

| Method | Path                    | Description                                                       |
|--------|-------------------------|-------------------------------------------------------------------|
| GET    | `/api/v1/clients/{id}`  | One client; `404` with a problem-detail body if the id is unknown |
| GET    | `/api/v1/clients`       | All clients; optional filters `email`, `status`, `segment`        |
| GET    | `/actuator/health`      | Health check                                                      |

`status` is one of `ACTIVE`, `SUSPENDED` or `CLOSED`. `email` and `segment` match case-insensitively.

Each client also has a `locked` boolean, which is separate from `status`: an `ACTIVE` client can be locked. If a record in the data file has no `locked` value, it defaults to `false`.

```sh
curl localhost:8080/api/v1/clients/C-1001
curl 'localhost:8080/api/v1/clients?status=ACTIVE&segment=PREMIUM'
```

## Mock data

The seed data is in `src/main/resources/clients.json` (ids `C-1001` to `C-1005`). To use your own file instead:

```sh
./mvnw spring-boot:run -Dspring-boot.run.arguments=--client-info.data=file:./my-clients.json
```

## Tests

All tests are under `src/test/java/com/internal/clientinfo`:

| Package | What | Runs with |
|---|---|---|
| `client` | Unit tests (in-process, MockMvc) | `./mvnw verify` |
| `integration` | API tests (`*IT`) against a running instance | `./mvnw verify -DskipITs=false -Dapi.baseUrl=...` |
| `pact` | Pact consumer tests and provider verification (`ClientInfoPactIT`) | consumer tests with `verify`, provider verification with the API tests |

## Contract tests (Pact)

Consumer contracts live in `src/test/resources/pacts`. `ClientInfoPactIT` verifies this service against every file there, calling a running instance:

```sh
./mvnw spring-boot:run   # in another terminal
./mvnw verify -DskipITs=false -Dapi.baseUrl=http://localhost:8080
```

CI runs this against the sandbox on every PR.

- **New or changed consumer contract:** copy the consumer's pact JSON into `src/test/resources/pacts`. If it uses a new provider state (`given(...)`), add a matching `@State` method to `ClientInfoPactIT`.
- **Example consumer:** `ExampleConsumerPactTests` writes its contract to `target/pacts` during `./mvnw verify`. Copy it into `src/test/resources/pacts` after changing it.
