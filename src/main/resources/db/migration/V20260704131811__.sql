ALTER TABLE carts_products
DROP
CONSTRAINT fk_carpro_on_cart_model;

ALTER TABLE carts_products
DROP
CONSTRAINT fk_carpro_on_product_model;

ALTER TABLE products
DROP
CONSTRAINT fk_products_on_user;

ALTER TABLE products
    ADD owner_id UUID;

ALTER TABLE products
    ADD CONSTRAINT FK_PRODUCTS_ON_OWNER FOREIGN KEY (owner_id) REFERENCES retailers (id);

DROP TABLE carts CASCADE;

DROP TABLE carts_products CASCADE;

ALTER TABLE products
DROP
COLUMN user_id;