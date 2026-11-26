-- 1. Current stock health by warehouse.
SELECT
    warehouse_code,
    sku,
    product_name,
    quantity,
    reorder_point,
    stock_status
FROM v_inventory_status
ORDER BY warehouse_code, stock_status, product_name;

-- 2. Items that need replenishment.
SELECT
    warehouse_code,
    sku,
    product_name,
    quantity,
    reorder_point
FROM v_inventory_status
WHERE stock_status IN ('OUT_OF_STOCK', 'REORDER')
ORDER BY quantity ASC, sku;

-- 3. Current inventory value by product category.
SELECT
    category_name,
    SUM(stock_value)::NUMERIC(14, 2) AS total_stock_value
FROM v_inventory_status
GROUP BY category_name
ORDER BY total_stock_value DESC;

-- 4. Units sold by SKU during the last 30 days.
SELECT
    p.sku,
    p.name,
    SUM(m.quantity) AS units_sold
FROM stock_movements AS m
JOIN products AS p ON p.product_id = m.product_id
WHERE m.movement_type = 'OUT'
  AND m.occurred_at >= now() - INTERVAL '30 days'
GROUP BY p.sku, p.name
ORDER BY units_sold DESC, p.sku;

-- 5. Supplier exposure: current units and stock value.
SELECT
    s.name AS supplier,
    SUM(i.quantity) AS units_in_stock,
    SUM(i.quantity * p.unit_price)::NUMERIC(14, 2) AS stock_value
FROM inventory AS i
JOIN products AS p ON p.product_id = i.product_id
JOIN suppliers AS s ON s.supplier_id = p.supplier_id
GROUP BY s.supplier_id, s.name
ORDER BY stock_value DESC;

-- 6. Auditable movement history for a single SKU.
SELECT
    m.occurred_at,
    p.sku,
    w.code AS warehouse,
    m.movement_type,
    m.quantity,
    m.reference,
    m.note
FROM stock_movements AS m
JOIN products AS p ON p.product_id = m.product_id
JOIN warehouses AS w ON w.warehouse_id = m.warehouse_id
WHERE p.sku = 'SKU-KEYBOARD'
ORDER BY m.occurred_at DESC, m.movement_id DESC;
