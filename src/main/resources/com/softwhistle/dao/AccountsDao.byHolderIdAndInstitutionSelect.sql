SELECT account.id, account.account_holder_id as "holder.id", account.institution_name as institutionName,
       account.institution_account_id as institutionAccountId, account.institution_account_name as institutionAccountName,
       account.established_by_institution_at establishedByInstitutionAt
FROM account
WHERE account.account_holder_id = ?
AND account.institution_name = ?
AND account.institution_account_id = ?