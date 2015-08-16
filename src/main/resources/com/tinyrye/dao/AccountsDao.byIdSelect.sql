SELECT account.id, account_holder.id 'holder.id', account.institution_name institutionName,
       account.institution_account_id 'institutionAccountId', account.institution_account_name 'institutionAccountName',
       account_holder.first_name 'holder.firstName', account_holder.intermediate_names 'holder.intermediateNames'
       account_holder.last_name 'holder.lastName', account_holder.email email,
       account.established_by_institution_at establishedByInstitutionAt
FROM account
JOIN account_holder ON account.account_holder_id
WHERE id = ?