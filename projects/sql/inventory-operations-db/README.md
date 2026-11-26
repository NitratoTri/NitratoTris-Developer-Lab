# Inventory Operations Database

A small PostgreSQL project for a retailer that needs reliable stock tracking across multiple warehouses. It demonstrates relational modelling, constraints, indexes, reporting queries, and transactional stock movements enforced by a database trigger.

## What it covers

- A normalized model for categories, suppliers, products, warehouses, inventory, and stock movements.
- A movement ledger: stock is changed by recording an `IN` or `OUT` movement, not by editing quantities directly.
- A PostgreSQL trigger that updates inventory in the same transaction as the movement.
- A guard that rejects an outbound movement when available stock is insufficient.
- A reporting view for stock health and inventory value.
- Practical reporting queries for replenishment, stock valuation, sales velocity, and movement history.
- Repeatable seed data, database assertions, Docker Compose, and a GitHub Actions workflow.

## Requirements

- Docker Engine and Docker Compose v2, or PostgreSQL 16 with `psql`.
- No third-party application dependencies are required.

## Run with Docker

From this directory:

```bash
docker compose up -d --wait
```

On the first start, PostgreSQL automatically runs the scripts in `sql/` in filename order:

1. `01_schema.sql` creates the schema, indexes, view, and stock-movement trigger.
2. `02_seed.sql` inserts sample catalogue, warehouses, opening stock, and a few sales.

Connect to the database:

```bash
docker compose exec db psql -U inventory_app -d inventory_ops
```

Run the database checks:

```bash
docker compose exec -T db psql -U inventory_app -d inventory_ops -v ON_ERROR_STOP=1 -f /tests/stock_movement_tests.sql
```

Run the reporting examples:

```bash
docker compose exec -T db psql -U inventory_app -d inventory_ops -f /sql/03_reporting_queries.sql
```

Stop the database:

```bash
docker compose down
```

To discard the local database and replay schema plus seed scripts from scratch:

```bash
docker compose down -v
docker compose up -d --wait
```

**Warning:** `docker compose down -v` deletes the local database volume.

## Run with an existing PostgreSQL 16 instance

Create a database named `inventory_ops`, then run:

```bash
psql -d inventory_ops -v ON_ERROR_STOP=1 -f sql/01_schema.sql
psql -d inventory_ops -v ON_ERROR_STOP=1 -f sql/02_seed.sql
psql -d inventory_ops -v ON_ERROR_STOP=1 -f tests/stock_movement_tests.sql
psql -d inventory_ops -f sql/03_reporting_queries.sql
```

## Model and business rules

| Table | Responsibility |
|---|---|
| `categories` | Product classification |
| `suppliers` | Supplier catalogue |
| `products` | SKU, category, supplier, and unit price |
| `warehouses` | Physical or logical stock locations |
| `inventory` | Current quantity and reorder point per product/warehouse |
| `stock_movements` | Immutable record of incoming and outgoing stock |

The `stock_movements` table is the audit ledger. An `AFTER INSERT` trigger updates `inventory` atomically. If an outbound movement would make stock negative, the trigger raises an exception and PostgreSQL rolls back the statement, including the attempted ledger row.

The unique movement reference is an idempotency safeguard for imports: a repeated reference cannot be recorded twice.

## Useful SQL

The view `v_inventory_status` classifies each stocked SKU/location pair as:

- `OUT_OF_STOCK`: quantity is zero.
- `REORDER`: quantity is at or below its reorder point.
- `HEALTHY`: quantity is above its reorder point.

The reporting script includes examples of low-stock monitoring, valuation by category, sales velocity, supplier exposure, and movement history.

## Configuration and security

The Compose file uses a deliberately simple local development username and password. It binds PostgreSQL to localhost and is **not** a production deployment configuration. Do not reuse these credentials or expose this database publicly.

## Verification

The SQL assertions are provided in `tests/stock_movement_tests.sql`. They check receipt and dispatch updates, insufficient-stock rollback, duplicate references, and invalid quantities. The GitHub Actions workflow runs the schema, seed, and assertions against PostgreSQL 16.

A test result should only be described as passing after the SQL has actually been executed. The initial contribution has not been reported as locally executed unless a PostgreSQL runtime is available.

## AI assistance

This project was prepared with AI assistance. Review the schema, trigger behaviour, queries, and tests; run it locally and be prepared to explain the transaction and data-integrity decisions before presenting it in a CV or interview.
