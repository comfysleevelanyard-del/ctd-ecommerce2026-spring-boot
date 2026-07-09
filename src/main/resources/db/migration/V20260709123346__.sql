ALTER TABLE orders_cart
    ALTER COLUMN cart_id DROP NOT NULL;

ALTER TABLE orders_cart
    ADD CONSTRAINT pk_orders_cart PRIMARY KEY (orders_id);