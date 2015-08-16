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
    account_holder_id INTEGER NOT NULL REFERENCES account_holder(id),
    institution_name VARCHAR(128),
    institution_account_id VARCHAR(128),
    institution_account_name VARCHAR(128),
    established_by_institution_at TIMESTAMP,
    record_creation_at TIMESTAMP DEFAULT NOW()
);

CREATE INDEX account_by_holder ON account (account_holder_id);
CREATE INDEX account_by_institution_name ON account (institution_name);
CREATE INDEX account_by_institution_account_id ON account (institution_account_id);

CREATE TABLE account_transaction (
    id SERIAL PRIMARY KEY,
    name VARCHAR(128),
    posted BOOLEAN,
    occurrence_at TIMESTAMP,
    record_creation_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE account_entry (
    id SERIAL PRIMARY KEY,
    account_id INTEGER REFERENCES account(id),
    external_account_id VARCHAR(128),
    external_transaction_id VARCHAR(128),
    operating_institution_name VARCHAR(128),
    transaction_id INTEGER REFERENCES account(id),
    amount REAL,
    name TEXT,
    posted BOOLEAN,
    occurrence_at TIMESTAMP,
    record_creation_at TIMESTAMP DEFAULT NOW()
);