ALTER TABLE account ADD COLUMN account_holder_code VARCHAR(128);
ALTER TABLE account ADD UNIQUE(account_holder_id, account_holder_code);