CREATE TABLE budget (
    id SERIAL PRIMARY KEY,
    name VARCHAR(128),
    account_holder_id INTEGER REFERENCES account_holder(id),
    active BOOLEAN,
    record_creation_at TIMESTAMP DEFAULT NOW()
);

CREATE INDEX budget_byName ON budget(name);
CREATE INDEX budget_byHolder ON budget(account_holder_id);

CREATE TABLE budget_recurrence (
    id SERIAL PRIMARY KEY,
    starts_at TIMESTAMP,
    ends_at TIMESTAMP,
    method_type VARCHAR(128),
    record_creation_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE budget_recurrence_interval (
    budget_recurrence_id INTEGER REFERENCES budget_recurrence(id),
    magnitude SMALLINT,
    unit VARCHAR(32),
    record_creation_at TIMESTAMP DEFAULT NOW()
);

CREATE INDEX budget_recurrence_interval_by_recurrenceFk ON budget_recurrence_interval(budget_recurrence_id);
CREATE INDEX budget_recurrence_interval_byMagnitudeAndUnit ON budget_recurrence_interval(magnitude, unit);

CREATE TABLE budget_recurrence_timeplot (
    budget_recurrence_id INTEGER REFERENCES budget_recurrence(id),
    occurs_at TIMESTAMP,
    record_creation_at TIMESTAMP DEFAULT NOW()
);

CREATE INDEX budget_recurrence_timeplot_by_recurrenceFk ON budget_recurrence_timeplot(budget_recurrence_id);
CREATE INDEX budget_recurrence_timeplot_by_occurence ON budget_recurrence_timeplot(occurs_at);

CREATE TABLE budget_recurrence_schedule (
    id SERIAL PRIMARY KEY,
    starts_at TIMESTAMP,
    ends_at TIMESTAMP,
    budget_recurrence_id INTEGER NOT NULL REFERENCES budget_recurrence(id),
    record_creation_at TIMESTAMP DEFAULT NOW()
);

CREATE INDEX budget_recurrence_schedule_byRecurrenceId ON budget_recurrence_schedule(budget_recurrence_id);

CREATE TABLE budget_item_categorization (
    id SERIAL PRIMARY KEY,
    budget_id INTEGER REFERENCES budget(id),
    code VARCHAR(64) NOT NULL,
    category_order SMALLINT NOT NULL DEFAULT (0)
);

CREATE INDEX budget_item_categorization_byBudget ON budget_item_categorization(budget_id);
CREATE INDEX budget_item_categorization_byBudgetAndCode ON budget_item_categorization(budget_id, code, category_order);
CREATE INDEX budget_item_categorization_byCode ON budget_item_categorization(code);

CREATE TABLE budget_item (
    id SERIAL PRIMARY KEY,
    budget_id INTEGER REFERENCES budget(id),
    item_type VARCHAR(32), /* EXPENSE, INCOME */
    budget_item_categorization_id INTEGER REFERENCES budget_item_categorization(id),
    description VARCHAR(128),
    amount REAL,
    budget_recurrence_id INTEGER REFERENCES budget_recurrence(id),
    account_id INTEGER REFERENCES account(id),
    record_creation_at TIMESTAMP DEFAULT NOW()
);

CREATE INDEX budget_item_byBudget ON budget_item(budget_id);
CREATE INDEX budget_item_byItemType ON budget_item(item_type);
CREATE INDEX budget_item_byCategorization ON budget_item(budget_item_categorization_id);
CREATE INDEX budget_item_byAmount ON budget_item(amount);
CREATE INDEX budget_item_byRecurrence ON budget_item(budget_recurrence_id);
CREATE INDEX budget_item_byAccount ON budget_item(account_id);

CREATE TABLE budget_item_occurrence (
    budget_item_id INTEGER REFERENCES budget_item(id),
    account_entry_id INTEGER REFERENCES account_entry(id),
    record_creation_at TIMESTAMP DEFAULT NOW()
);

CREATE INDEX budget_item_occurrence_byBudgetItem ON budget_item_occurrence(budget_item_id);
CREATE INDEX budget_item_occurrence_byAccountEntry ON budget_item_occurrence(account_entry_id);