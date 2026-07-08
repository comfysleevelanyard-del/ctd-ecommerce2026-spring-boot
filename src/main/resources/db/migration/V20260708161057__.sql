ALTER TABLE orders
    ADD completed BOOLEAN;

ALTER TABLE orders
DROP
COLUMN status;