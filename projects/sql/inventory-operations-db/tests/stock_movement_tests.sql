-- Run after 01_schema.sql and 02_seed.sql.
-- All test writes are rolled back so the sample database remains unchanged.

BEGIN;

DO $$
DECLARE
    v_product_id BIGINT;
    v_warehouse_id BIGINT;
    v_before_quantity INTEGER;
    v_after_quantity INTEGER;
    v_movement_count BIGINT;
BEGIN
    SELECT product_id INTO STRICT v_product_id
    FROM products
    WHERE sku = 'SKU-KEYBOARD';

    SELECT warehouse_id INTO STRICT v_warehouse_id
    FROM warehouses
    WHERE code = 'WH-CENTRAL';

    SELECT quantity INTO STRICT v_before_quantity
    FROM inventory
    WHERE product_id = v_product_id
      AND warehouse_id = v_warehouse_id;

    -- A receipt must increase available stock.
    INSERT INTO stock_movements (
        product_id, warehouse_id, movement_type, quantity, reference
    )
    VALUES (
        v_product_id, v_warehouse_id, 'IN', 5, 'TEST-RECEIPT-001'
    );

    SELECT quantity INTO STRICT v_after_quantity
    FROM inventory
    WHERE product_id = v_product_id
      AND warehouse_id = v_warehouse_id;

    IF v_after_quantity <> v_before_quantity + 5 THEN
        RAISE EXCEPTION 'Receipt did not increase inventory as expected';
    END IF;

    -- A dispatch must decrease available stock.
    INSERT INTO stock_movements (
        product_id, warehouse_id, movement_type, quantity, reference
    )
    VALUES (
        v_product_id, v_warehouse_id, 'OUT', 3, 'TEST-DISPATCH-001'
    );

    SELECT quantity INTO STRICT v_after_quantity
    FROM inventory
    WHERE product_id = v_product_id
      AND warehouse_id = v_warehouse_id;

    IF v_after_quantity <> v_before_quantity + 2 THEN
        RAISE EXCEPTION 'Dispatch did not decrease inventory as expected';
    END IF;

    SELECT COUNT(*) INTO v_movement_count
    FROM stock_movements
    WHERE reference = 'TEST-OVERDRAW-001';

    -- The trigger must reject an outbound movement larger than available stock.
    BEGIN
        INSERT INTO stock_movements (
            product_id, warehouse_id, movement_type, quantity, reference
        )
        VALUES (
            v_product_id, v_warehouse_id, 'OUT', 999999, 'TEST-OVERDRAW-001'
        );

        RAISE EXCEPTION 'Expected insufficient-stock rejection'
            USING ERRCODE = 'P0002';
    EXCEPTION
        WHEN SQLSTATE 'P0001' THEN
            NULL;
    END;

    SELECT quantity INTO STRICT v_after_quantity
    FROM inventory
    WHERE product_id = v_product_id
      AND warehouse_id = v_warehouse_id;

    IF v_after_quantity <> v_before_quantity + 2 THEN
        RAISE EXCEPTION 'Rejected dispatch changed inventory';
    END IF;

    IF (SELECT COUNT(*) FROM stock_movements
        WHERE reference = 'TEST-OVERDRAW-001') <> v_movement_count THEN
        RAISE EXCEPTION 'Rejected dispatch left a movement ledger row';
    END IF;

    -- A movement reference cannot be used twice.
    BEGIN
        INSERT INTO stock_movements (
            product_id, warehouse_id, movement_type, quantity, reference
        )
        VALUES (
            v_product_id, v_warehouse_id, 'IN', 1, 'TEST-RECEIPT-001'
        );

        RAISE EXCEPTION 'Expected duplicate-reference rejection'
            USING ERRCODE = 'P0002';
    EXCEPTION
        WHEN unique_violation THEN
            NULL;
    END;

    -- Zero is not a valid movement quantity.
    BEGIN
        INSERT INTO stock_movements (
            product_id, warehouse_id, movement_type, quantity, reference
        )
        VALUES (
            v_product_id, v_warehouse_id, 'IN', 0, 'TEST-ZERO-QUANTITY'
        );

        RAISE EXCEPTION 'Expected non-positive quantity rejection'
            USING ERRCODE = 'P0002';
    EXCEPTION
        WHEN check_violation THEN
            NULL;
    END;

    RAISE NOTICE 'All inventory movement assertions passed';
END;
$$;

ROLLBACK;
