CREATE TABLE budget (
    id SERIAL PRIMARY KEY,
    account_id INTEGER REFERENCES account(id),
    owning_budget_id INTEGER REFERENCES budget(id),
    lifecycle_start_at TIMESTAMP,
    lifecycle_end_at TIMESTAMP,
    record_creation_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE budget_recurrence (
    id SERIAL PRIMARY KEY,
    interval_type VARCHAR(32),
    interval_length SMALLINT,
    record_creation_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE budget_recurrence_timeplot (
    budget_recurrence_id INTEGER REFERENCES budget_recurrence(id),
    occurrence_at TIMESTAMP,
    record_creation_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE budget_item (
    id SERIAL PRIMARY KEY,
    budget_id INTEGER REFERENCES budget(id),
    item_type VARCHAR(32), /* EXPENSE, INCOME */
    code VARCHAR(64),
    description VARCHAR(128),
    amount REAL,
    budget_recurrence_id INTEGER REFERENCES budget_recurrence(id),
    account_id INTEGER REFERENCES account(id),
    owning_budget_id INTEGER REFERENCES budget(id),
    lifecycle_start_at TIMESTAMP,
    lifecycle_end_at TIMESTAMP,
    record_creation_at TIMESTAMP DEFAULT NOW()
);