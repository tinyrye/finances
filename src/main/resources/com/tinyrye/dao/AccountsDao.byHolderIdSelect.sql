SELECT account.id, account_holder.id as "holder.id", account.institution_name as institutionName,
       account.institution_account_id as institutionAccountId, account.institution_account_name as institutionAccountName,
       account_holder.first_name as "holder.firstName", account_holder.intermediate_names as "holder.intermediateNames",
       account_holder.last_name as "holder.lastName", account_holder.email as "holder.email",
       account.established_by_institution_at establishedByInstitutionAt
FROM account
JOIN account_holder ON account.account_holder_id = account_holder.id
WHERE account_holder.id = ?
AND account.active = true