CREATE TABLE budget
(
    id SERIAL PRIMARY KEY,
    name VARCHAR(128),
    account_holder_id INTEGER REFERENCES account_holder(id),
    active BOOLEAN,
    starts_at TIMESTAMP,
    ends_at TIMESTAMP,
    record_creation_at TIMESTAMP DEFAULT NOW()
);

CREATE INDEX budget_byName ON budget(name);
CREATE INDEX budget_byHolder ON budget(account_holder_id);

CREATE TABLE budget_occurrence_schedule
(
    id SERIAL PRIMARY KEY,
    type VARCHAR(128),
    record_creation_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE budget_fixed_unit_occurrences
(
    budget_occurrence_schedule_id INTEGER REFERENCES budget_occurrence_schedule(id),
    starts_at TIMESTAMP,
    ends_at TIMESTAMP,
    magnitude SMALLINT,
    unit VARCHAR(32),
    record_creation_at TIMESTAMP DEFAULT NOW()
);

CREATE INDEX budget_fixed_unit_occurrence_by_occurrenceFk ON budget_fixed_unit_occurrences(budget_occurrence_schedule_id);
CREATE INDEX budget_fixed_unit_occurrence_byStartEndMagnitudeAndUnit ON budget_fixed_unit_occurrences(starts_at, ends_at, magnitude, unit);

CREATE TABLE budget_fixed_field_occurrences
(
    budget_occurrence_schedule_id INTEGER REFERENCES budget_occurrence_schedule(id),
    starts_at TIMESTAMP,
    ends_at TIMESTAMP,
    magnitude SMALLINT,
    field VARCHAR(32),
    record_creation_at TIMESTAMP DEFAULT NOW()
);

CREATE INDEX budget_fixed_field_occurrence_by_occurrenceFk ON budget_fixed_field_occurrences(budget_occurrence_schedule_id);
CREATE INDEX budget_fixed_field_occurrence_byStartEndMagnitudeAndUnit ON budget_fixed_field_occurrences(starts_at, ends_at, magnitude, field);

CREATE TABLE budget_custom_occurrences
(
    budget_occurrence_schedule_id INTEGER REFERENCES budget_occurrence_schedule(id),
    occurs_at TIMESTAMP,
    record_creation_at TIMESTAMP DEFAULT NOW()
);

CREATE INDEX budget_custom_occurrence_by_occurrenceFk ON budget_custom_occurrences(budget_occurrence_schedule_id);
CREATE INDEX budget_custom_occurrence_by_occursAt ON budget_custom_occurrences(occurs_at);

CREATE TABLE budget_item_categorization
(
    id SERIAL PRIMARY KEY,
    budget_id INTEGER REFERENCES budget(id),
    code VARCHAR(64) NOT NULL,
    category_order SMALLINT NOT NULL DEFAULT (0)
);

CREATE INDEX budget_item_categorization_byBudget ON budget_item_categorization(budget_id);
CREATE INDEX budget_item_categorization_byBudgetAndCode ON budget_item_categorization(budget_id, code, category_order);
CREATE INDEX budget_item_categorization_byCode ON budget_item_categorization(code);

CREATE TABLE budget_item
(
    id SERIAL PRIMARY KEY,
    budget_id INTEGER NOT NULL REFERENCES budget(id),
    budget_item_categorization_id INTEGER REFERENCES budget_item_categorization(id),
    description VARCHAR(128),
    amount REAL NOT NULL,
    budget_occurrence_schedule_id INTEGER NOT NULL REFERENCES budget_occurrence_schedule(id),
    holder_account_id INTEGER REFERENCES account(id),
    merchant_account_id INTEGER REFERENCES account(id),
    merchant VARCHAR(64),
    record_creation_at TIMESTAMP DEFAULT NOW()
);

CREATE INDEX budget_item_byBudget ON budget_item(budget_id);
CREATE INDEX budget_item_byCategorization ON budget_item(budget_item_categorization_id);
CREATE INDEX budget_item_byAmount ON budget_item(amount);
CREATE INDEX budget_item_byOccurrence ON budget_item(budget_occurrence_schedule_id);
CREATE INDEX budget_item_byHolderAccount ON budget_item(holder_account_id);
CREATE INDEX budget_item_byMerchantAccount ON budget_item(merchant_account_id);

CREATE TABLE budget_item_occurrence
(
    budget_item_id INTEGER NOT NULL REFERENCES budget_item(id),
    account_entry_id INTEGER NOT NULL REFERENCES account_entry(id),
    occurred_at TIMESTAMP NOT NULL,
    record_creation_at TIMESTAMP DEFAULT NOW()
);

CREATE INDEX budget_item_occurrence_byBudgetItem ON budget_item_occurrence(budget_item_id);
CREATE INDEX budget_item_occurrence_byAccountEntry ON budget_item_occurrence(account_entry_id);
