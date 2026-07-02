CREATE TABLE IF NOT EXISTS products
(
    id             UUID NOT NULL,
    name           VARCHAR(255),
    description    VARCHAR(255),
    price_in_cents INTEGER,
    owner          VARCHAR(255),
    CONSTRAINT pk_products PRIMARY KEY (id)
);