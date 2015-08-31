SELECT id, starts_at as startsAt, ends_at as endsAt, magnitude, unit
FROM budget_recurrence 
INNER JOIN budget_recurrence_interval
ON budget_recurrence.id = budget_recurrence_interval.budget_recurrence_id
WHERE starts_at = ? AND ends_at = ?
AND magnitude = ? AND unit = ?