SELECT id, starts_at as startsAt, ends_at as endsAt,
       method_type as methodType
FROM budget_recurrence
WHERE id = ?