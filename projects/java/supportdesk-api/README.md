# SupportDesk API

A small customer-support REST API built with Java 21, Spring Boot, Spring Data JPA, Bean Validation, and PostgreSQL.

## Features

- Create, retrieve, filter, paginate, update status, and delete tickets.
- Validate incoming request bodies and return consistent JSON errors.
- Filter by status and priority; newest tickets appear first.
- Use PostgreSQL locally through Docker Compose and H2 for integration tests.
- Run automated Maven verification through GitHub Actions.

## Requirements

- Java 21 and Maven 3.9+
- Docker Engine with the Compose plugin (recommended)

## Start the application

From this directory:

```bash
docker compose up --build
```

The API listens on `http://localhost:8080`. PostgreSQL data is kept in the `postgres-data` volume. Stop with `docker compose down`; add `-v` to remove the database volume.

To run without Docker, start PostgreSQL and set:

```bash
export DB_URL=jdbc:postgresql://localhost:5432/supportdesk
export DB_USERNAME=supportdesk
export DB_PASSWORD=supportdesk
mvn spring-boot:run
```

## Endpoints

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/tickets` | Create a ticket (starts as OPEN) |
| GET | `/api/tickets` | List tickets; optional `status`, `priority`, `page`, `size` |
| GET | `/api/tickets/{id}` | Retrieve one ticket |
| PATCH | `/api/tickets/{id}/status` | Change status |
| DELETE | `/api/tickets/{id}` | Delete a ticket |

Allowed priorities: `LOW`, `MEDIUM`, `HIGH`, `URGENT`. Allowed statuses: `OPEN`, `IN_PROGRESS`, `RESOLVED`. Page numbering starts at zero; page size is limited to 1–100.

### Create a ticket

```bash
curl -i -X POST http://localhost:8080/api/tickets \
  -H "Content-Type: application/json" \
  -d '{"title":"Cannot sign in","description":"Reset link expired","requesterEmail":"alex@example.com","priority":"HIGH"}'
```

### Filter tickets

```bash
curl "http://localhost:8080/api/tickets?status=OPEN&priority=HIGH&page=0&size=20"
```

### Change status

```bash
curl -X PATCH http://localhost:8080/api/tickets/1/status \
  -H "Content-Type: application/json" \
  -d '{"status":"IN_PROGRESS"}'
```

Errors have a consistent structure containing `timestamp`, `status`, `error`, `message`, and `path`. Invalid input returns HTTP 400 and missing tickets return HTTP 404.

## Tests

```bash
mvn verify
```

Integration tests use MockMvc and an isolated in-memory H2 database. The CI workflow runs the Maven verification with Java 21.

## Design notes and limitations

The entity stores enum values as strings and includes indexes for status/priority filtering and creation-time ordering. The API intentionally keeps a small scope: it has no authentication, authorization, rate limiting, audit history, or notification service. Compose credentials are for local development only; a real deployment needs managed secrets, schema migrations, access control, backups, and monitoring.

## AI assistance

This project was prepared with AI assistance. Review the implementation, run the tests, and make changes you understand before presenting it in a CV or interview.
