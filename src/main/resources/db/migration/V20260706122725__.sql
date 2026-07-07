CREATE TABLE cart
(
    id         UUID NOT NULL,
    user_id    VARCHAR(255),
    product_id UUID,
    CONSTRAINT pk_cart PRIMARY KEY (id)
);

CREATE TABLE products
(
    id             UUID NOT NULL,
    name           VARCHAR(255),
    description    VARCHAR(255),
    price_in_cents INTEGER,
    owner_id       UUID,
    CONSTRAINT pk_products PRIMARY KEY (id)
);

CREATE TABLE retailers
(
    id           UUID NOT NULL,
    name         VARCHAR(255),
    account_id   VARCHAR(255),
    user_id      VARCHAR(255),
    date_created TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_retailers PRIMARY KEY (id)
);

CREATE TABLE revchanges
(
    rev        BIGINT NOT NULL,
    entityname VARCHAR(255)
);

CREATE TABLE revinfo
(
    rev      BIGINT NOT NULL,
    revtstmp BIGINT,
    CONSTRAINT pk_revinfo PRIMARY KEY (rev)
);

ALTER TABLE retailers
    ADD CONSTRAINT uc_retailers_accountid UNIQUE (account_id);

ALTER TABLE retailers
    ADD CONSTRAINT uc_retailers_userid UNIQUE (user_id);

ALTER TABLE cart
    ADD CONSTRAINT FK_CART_ON_PRODUCT FOREIGN KEY (product_id) REFERENCES products (id);

ALTER TABLE products
    ADD CONSTRAINT FK_PRODUCTS_ON_OWNER FOREIGN KEY (owner_id) REFERENCES retailers (id);

ALTER TABLE revchanges
    ADD CONSTRAINT fk_revchanges_on_default_tracking_modified_entities_changelog FOREIGN KEY (rev) REFERENCES revinfo (rev);