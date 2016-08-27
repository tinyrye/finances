CREATE TABLE account_holder
(
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(32),
    intermediate_names VARCHAR(256),
    last_name VARCHAR(32),
    email VARCHAR(64),
    record_creation_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE account
(
    id SERIAL PRIMARY KEY,
    institution_name VARCHAR(128),
    institution_account_id VARCHAR(128),
    institution_account_name VARCHAR(128),
    established_by_institution_at TIMESTAMP,
    record_creation_at TIMESTAMP DEFAULT NOW()
);

CREATE INDEX account_by_institution_name ON account(institution_name);
CREATE INDEX account_by_institution_account ON account(institution_account_id);

ALTER TABLE account_holder ADD COLUMN primary_account_id INTEGER REFERENCES account(id);
CREATE INDEX account_holder_by_primary_account ON account_holder(primary_account_id);
ALTER TABLE account ADD COLUMN account_holder_id INTEGER REFERENCES account_holder(id);
CREATE INDEX account_by_holder ON account (account_holder_id);

CREATE TABLE account_transaction
(
    id SERIAL PRIMARY KEY,
    transaction_details VARCHAR(128),
    external_transaction_id VARCHAR(128),
    operating_institution_name VARCHAR(128),
    posted BOOLEAN,
    occurrence_at TIMESTAMP,
    record_creation_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE account_entry
(
    id SERIAL PRIMARY KEY,
    account_id INTEGER REFERENCES account(id),
    account_transaction_id INTEGER REFERENCES account_transaction(id),
    amount REAL,
    posted BOOLEAN,
    external_account_id VARCHAR(128),
    occurrence_at TIMESTAMP,
    record_creation_at TIMESTAMP DEFAULT NOW()
);

CREATE INDEX account_entry_by_account_id ON account_entry(account_id);
CREATE INDEX account_entry_by_account_transaction_id ON account_entry(account_transaction_id);
CREATE INDEX account_entry_by_external_account_id ON account_entry(external_account_id);
CREATE INDEX account_transaction_by_external_account_transaction_id ON account_transaction(external_transaction_id);
CREATE INDEX account_transaction_by_operating_institution_name ON account_transaction(operating_institution_name);
