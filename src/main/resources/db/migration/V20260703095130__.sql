ALTER TABLE retailers
    ADD CONSTRAINT uc_retailers_accountid UNIQUE (account_id);

ALTER TABLE retailers
    ADD CONSTRAINT uc_retailers_userid UNIQUE (user_id);