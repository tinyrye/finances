SELECT budget_occurrence_schedule_id as id, occurs_at as occursAt
FROM budget_custom_occurrences
WHERE budget_occurrence_schedule_id in (?)