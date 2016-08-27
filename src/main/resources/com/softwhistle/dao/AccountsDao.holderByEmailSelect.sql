SELECT id, first_name as "firstName", intermediate_names as "intermediateNames",
       last_name as "lastName", email as "email", primary_account_id as "primaryAccount.id"
FROM account_holder
WHERE email = ?