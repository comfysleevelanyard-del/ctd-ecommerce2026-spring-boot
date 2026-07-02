ALTER TABLE products
    ADD CONSTRAINT uc_products_user UNIQUE (user_id)