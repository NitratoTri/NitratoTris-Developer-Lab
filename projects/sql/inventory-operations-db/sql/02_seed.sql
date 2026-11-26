-- Sample catalogue and movement history.
-- Unique names, SKUs, codes, and movement references make this seed repeatable.

INSERT INTO categories (name)
VALUES
    ('Peripherals'),
    ('Displays'),
    ('Accessories')
ON CONFLICT (name) DO NOTHING;

INSERT INTO suppliers (name, contact_email)
VALUES
    ('Northstar Devices', 'orders@northstar.example'),
    ('ByteWorks Supply', 'sales@byteworks.example'),
    ('Acme Components', 'support@acme.example')
ON CONFLICT (name) DO NOTHING;

INSERT INTO warehouses (code, name)
VALUES
    ('WH-CENTRAL', 'Central Warehouse'),
    ('WH-STORE', 'Retail Store')
ON CONFLICT (code) DO NOTHING;

INSERT INTO products (sku, name, category_id, supplier_id, unit_price)
SELECT
    product_data.sku,
    product_data.name,
    categories.category_id,
    suppliers.supplier_id,
    product_data.unit_price
FROM (
    VALUES
        ('SKU-KEYBOARD', 'Mechanical Keyboard', 'Peripherals', 'Northstar Devices', 79.90::NUMERIC),
        ('SKU-MOUSE', 'Wireless Mouse', 'Peripherals', 'Northstar Devices', 34.50::NUMERIC),
        ('SKU-MONITOR', '27-inch Monitor', 'Displays', 'ByteWorks Supply', 219.00::NUMERIC),
        ('SKU-USBHUB', 'USB-C Hub', 'Accessories', 'Acme Components', 29.95::NUMERIC),
        ('SKU-WEBCAM', 'Full HD Webcam', 'Peripherals', 'ByteWorks Supply', 49.00::NUMERIC),
        ('SKU-HEADSET', 'USB Headset', 'Accessories', 'Acme Components', 59.00::NUMERIC)
) AS product_data(sku, name, category_name, supplier_name, unit_price)
JOIN categories ON categories.name = product_data.category_name
JOIN suppliers ON suppliers.name = product_data.supplier_name
ON CONFLICT (sku) DO NOTHING;

-- Opening receipts are inserted before sample sales so the trigger can
-- maintain the inventory projection from the movement ledger.
INSERT INTO stock_movements (
    product_id, warehouse_id, movement_type, quantity, reference, occurred_at, note
)
SELECT
    products.product_id,
    warehouses.warehouse_id,
    'IN',
    opening.quantity,
    opening.reference,
    now() - INTERVAL '20 days',
    'Initial sample stock'
FROM (
    VALUES
        ('SKU-KEYBOARD', 'WH-CENTRAL', 30, 'SEED-IN-KEYBOARD-CENTRAL'),
        ('SKU-MOUSE', 'WH-CENTRAL', 45, 'SEED-IN-MOUSE-CENTRAL'),
        ('SKU-MONITOR', 'WH-CENTRAL', 10, 'SEED-IN-MONITOR-CENTRAL'),
        ('SKU-USBHUB', 'WH-CENTRAL', 4, 'SEED-IN-USBHUB-CENTRAL'),
        ('SKU-WEBCAM', 'WH-CENTRAL', 14, 'SEED-IN-WEBCAM-CENTRAL'),
        ('SKU-HEADSET', 'WH-CENTRAL', 8, 'SEED-IN-HEADSET-CENTRAL'),
        ('SKU-KEYBOARD', 'WH-STORE', 6, 'SEED-IN-KEYBOARD-STORE'),
        ('SKU-MOUSE', 'WH-STORE', 10, 'SEED-IN-MOUSE-STORE')
) AS opening(sku, warehouse_code, quantity, reference)
JOIN products ON products.sku = opening.sku
JOIN warehouses ON warehouses.code = opening.warehouse_code
ON CONFLICT (reference) DO NOTHING;

INSERT INTO stock_movements (
    product_id, warehouse_id, movement_type, quantity, reference, occurred_at, note
)
SELECT
    products.product_id,
    warehouses.warehouse_id,
    'OUT',
    sales.quantity,
    sales.reference,
    now() - sales.age,
    'Sample customer sale'
FROM (
    VALUES
        ('SKU-KEYBOARD', 'WH-CENTRAL', 4, 'SEED-OUT-KEYBOARD-01', INTERVAL '5 days'),
        ('SKU-MOUSE', 'WH-CENTRAL', 8, 'SEED-OUT-MOUSE-01', INTERVAL '3 days'),
        ('SKU-MONITOR', 'WH-CENTRAL', 2, 'SEED-OUT-MONITOR-01', INTERVAL '2 days'),
        ('SKU-HEADSET', 'WH-CENTRAL', 3, 'SEED-OUT-HEADSET-01', INTERVAL '1 day')
) AS sales(sku, warehouse_code, quantity, reference, age)
JOIN products ON products.sku = sales.sku
JOIN warehouses ON warehouses.code = sales.warehouse_code
ON CONFLICT (reference) DO NOTHING;
