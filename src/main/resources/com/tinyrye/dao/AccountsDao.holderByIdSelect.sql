SELECT id, first_name as "firstName", intermediate_names as "intermediateNames",
       last_name as "lastName", email as "email"
FROM account_holder
WHERE id = ?