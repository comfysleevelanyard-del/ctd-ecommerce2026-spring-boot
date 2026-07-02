CREATE TABLE IF NOT EXISTS retailers
(
    id           UUID NOT NULL,
    name         VARCHAR(255),
    account_id   VARCHAR(255),
    products     TEXT[],
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

ALTER TABLE revchanges
    ADD CONSTRAINT fk_revchanges_on_default_tracking_modified_entities_changelog FOREIGN KEY (rev) REFERENCES revinfo (rev);