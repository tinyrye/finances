SELECT id, amount, description, budget_occurrence_schedule_id as "transactsOn.id",
       holder_account_id as "holderAccount.id", merchant
FROM budget_item
WHERE budget_id = ?