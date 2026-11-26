-- Inventory Operations Database
-- PostgreSQL 16

CREATE TABLE categories (
    category_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name TEXT NOT NULL UNIQUE,
    CONSTRAINT categories_name_not_blank CHECK (btrim(name) <> '')
);

CREATE TABLE suppliers (
    supplier_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name TEXT NOT NULL UNIQUE,
    contact_email TEXT,
    CONSTRAINT suppliers_name_not_blank CHECK (btrim(name) <> '')
);

CREATE TABLE warehouses (
    warehouse_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    code TEXT NOT NULL UNIQUE,
    name TEXT NOT NULL,
    CONSTRAINT warehouses_code_not_blank CHECK (btrim(code) <> ''),
    CONSTRAINT warehouses_name_not_blank CHECK (btrim(name) <> '')
);

CREATE TABLE products (
    product_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    sku TEXT NOT NULL UNIQUE,
    name TEXT NOT NULL,
    category_id BIGINT NOT NULL REFERENCES categories(category_id),
    supplier_id BIGINT NOT NULL REFERENCES suppliers(supplier_id),
    unit_price NUMERIC(12, 2) NOT NULL CHECK (unit_price >= 0),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT products_sku_not_blank CHECK (btrim(sku) <> ''),
    CONSTRAINT products_name_not_blank CHECK (btrim(name) <> '')
);

CREATE TABLE inventory (
    product_id BIGINT NOT NULL REFERENCES products(product_id),
    warehouse_id BIGINT NOT NULL REFERENCES warehouses(warehouse_id),
    quantity INTEGER NOT NULL DEFAULT 0 CHECK (quantity >= 0),
    reorder_point INTEGER NOT NULL DEFAULT 5 CHECK (reorder_point >= 0),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    PRIMARY KEY (product_id, warehouse_id)
);

CREATE TABLE stock_movements (
    movement_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    product_id BIGINT NOT NULL REFERENCES products(product_id),
    warehouse_id BIGINT NOT NULL REFERENCES warehouses(warehouse_id),
    movement_type TEXT NOT NULL CHECK (movement_type IN ('IN', 'OUT')),
    quantity INTEGER NOT NULL CHECK (quantity > 0),
    reference TEXT NOT NULL UNIQUE,
    occurred_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    note TEXT,
    CONSTRAINT stock_movements_reference_not_blank CHECK (btrim(reference) <> '')
);

CREATE INDEX idx_products_category ON products(category_id);
CREATE INDEX idx_products_supplier ON products(supplier_id);
CREATE INDEX idx_inventory_warehouse ON inventory(warehouse_id);
CREATE INDEX idx_movements_product_date
    ON stock_movements(product_id, occurred_at DESC);
CREATE INDEX idx_movements_out_date
    ON stock_movements(occurred_at DESC)
    WHERE movement_type = 'OUT';

CREATE OR REPLACE FUNCTION apply_stock_movement()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
    IF NEW.movement_type = 'IN' THEN
        INSERT INTO inventory (product_id, warehouse_id, quantity, reorder_point)
        VALUES (NEW.product_id, NEW.warehouse_id, NEW.quantity, 5)
        ON CONFLICT (product_id, warehouse_id)
        DO UPDATE SET
            quantity = inventory.quantity + EXCLUDED.quantity,
            updated_at = now();
    ELSE
        UPDATE inventory
        SET quantity = quantity - NEW.quantity,
            updated_at = now()
        WHERE product_id = NEW.product_id
          AND warehouse_id = NEW.warehouse_id
          AND quantity >= NEW.quantity;

        IF NOT FOUND THEN
            RAISE EXCEPTION
                'Insufficient stock for product % in warehouse %',
                NEW.product_id,
                NEW.warehouse_id
                USING ERRCODE = 'P0001';
        END IF;
    END IF;

    RETURN NEW;
END;
$$;

CREATE TRIGGER trg_apply_stock_movement
AFTER INSERT ON stock_movements
FOR EACH ROW
EXECUTE FUNCTION apply_stock_movement();

CREATE VIEW v_inventory_status AS
SELECT
    p.sku,
    p.name AS product_name,
    c.name AS category_name,
    w.code AS warehouse_code,
    w.name AS warehouse_name,
    i.quantity,
    i.reorder_point,
    p.unit_price,
    (i.quantity * p.unit_price)::NUMERIC(14, 2) AS stock_value,
    CASE
        WHEN i.quantity = 0 THEN 'OUT_OF_STOCK'
        WHEN i.quantity <= i.reorder_point THEN 'REORDER'
        ELSE 'HEALTHY'
    END AS stock_status,
    i.updated_at
FROM inventory AS i
JOIN products AS p ON p.product_id = i.product_id
JOIN categories AS c ON c.category_id = p.category_id
JOIN warehouses AS w ON w.warehouse_id = i.warehouse_id
WHERE p.active = TRUE;
