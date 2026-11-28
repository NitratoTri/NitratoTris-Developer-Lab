# SupportDesk Dashboard

Angular frontend for the [SupportDesk API](../../java/supportdesk-api/README.md). It provides a small operations panel for creating, filtering, updating, and deleting support tickets.

## Features

- Standalone Angular component architecture and typed API models.
- Reactive form with client-side validation aligned with the API contract.
- Ticket listing with server-side status/priority filters and pagination.
- Status updates and deletion with confirmation.
- Loading, empty, success, and error states.
- Responsive Spanish-language interface.
- Development proxy to the Spring Boot API, avoiding a separate CORS configuration locally.

## Requirements

- Node.js 22.12+ and npm 10+
- Angular CLI 20 (installed through the project dependencies)
- The SupportDesk API running on `http://localhost:8080`

## Run locally

Start the API first. From `projects/angular/supportdesk-dashboard`:

```bash
npm install
npm start
```

Open `http://localhost:4200`. The development server proxies `/api` to `http://localhost:8080` using `proxy.conf.json`.

## Production build

```bash
npm run build
```

The production bundle is generated under `dist/`. In production, configure the web server or reverse proxy to forward `/api` to the Spring Boot service. The development proxy is not a production server.

## Checks

```bash
npm run test:helpers
npm run build
```

The helper test suite uses Node's built-in test runner and checks status/priority labels and date formatting. The Angular production build validates component templates and dependency integration.

## API contract

The frontend consumes the existing SupportDesk API:

- `GET /api/tickets?page=0&size=10&status=OPEN&priority=HIGH`
- `POST /api/tickets`
- `PATCH /api/tickets/{id}/status`
- `DELETE /api/tickets/{id}`

It expects Spring Data's paginated response (`content`, `totalElements`, `totalPages`, `number`, `size`) and the `TicketResponse` fields exposed by the API.

## Design notes and limitations

- Summary cards count tickets on the currently loaded page; the total result count comes from the server. They are not global analytics.
- The API currently has no authentication or authorization. Do not expose this dashboard or API publicly with real customer data until access control is implemented.
- The frontend does not include optimistic updates; it waits for the API response before changing a ticket.
- A production deployment needs a real reverse-proxy/API URL configuration, secure transport, authentication, and authorization.

## AI assistance

This project was prepared with AI assistance. Review the API integration, run the app against the backend, and adapt the interface before presenting it in a CV or interview.
