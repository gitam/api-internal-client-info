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
