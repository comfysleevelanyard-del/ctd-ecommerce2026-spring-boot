ALTER TABLE orders_cart
    ADD CONSTRAINT uc_orders_cart_cart UNIQUE (cart_id);