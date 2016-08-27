SELECT budget_occurrence_schedule_id as id, starts_at as startsAt,
       ends_at as endsAt, magnitude, unit
FROM budget_fixed_unit_occurrences
WHERE budget_occurrence_schedule_id = ?