package com.tinyrye.dao;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.sql.DataSource;

import com.tinyrye.jdbc.DaoStatementLocator;
import com.tinyrye.jdbc.RowConverter;
import com.tinyrye.jdbc.SQLInsert;
import com.tinyrye.jdbc.SQLQuery;

import com.tinyrye.model.Budget;
import com.tinyrye.model.BudgetItem;
import com.tinyrye.model.CustomRecurrenceSchedule;
import com.tinyrye.model.FixedRecurrenceInterval;
import com.tinyrye.model.Recurrence;
import com.tinyrye.model.RecurrenceMethod;

public class BudgetDao
{
    private final SQLInsert budgetInsert;
    private final SQLInsert budgetItemInsert;
    private final SQLInsert recurrenceInsert;
    private final SQLInsert fixedIntervalRecurrenceInsert;
    private final SQLInsert customScheduleRecurrenceInsert;
    private final SQLQuery activeBudgetSelect;
    private final SQLQuery budgetByHolderAndNameSelect;
    private final SQLQuery recurrenceByIdSelect;
    private final SQLQuery recurrenceByFixedIntervalSelect;
    private final SQLQuery customScheduleByRecurrenceIdSelect;
    private final SQLQuery fixedIntervalByRecurrenceIdSelect;

    private final RowConverter<Budget> baseBudgetRowConverter = (resultSet, valueUtility) ->
        new Budget().id(valueUtility.convert(resultSet, Integer.class, "id"))
            .active(valueUtility.convert(resultSet, Boolean.class, "active"))
            .name(valueUtility.convert(resultSet, String.class, "name"));
    
    public BudgetDao(DataSource datasource)
    {
        budgetInsert = new SQLInsert(datasource)
            .sql("INSERT INTO budget (name, active, account_holder_id) VALUES (?, ?, ?)");
        
        budgetItemInsert = new SQLInsert(datasource)
            .sql("INSERT INTO budget_item (budget_id, item_type, description, amount, budget_recurrence_id, account_id, merchant) " +
                 "VALUES (?, ?, ?, ?, ?, ?, ?)");
        
        recurrenceInsert = new SQLInsert(datasource)
            .sql("INSERT INTO budget_recurrence (starts_at, ends_at, method_type) VALUES (?, ?, ?)");
        
        fixedIntervalRecurrenceInsert = new SQLInsert(datasource)
            .sql("INSERT INTO budget_recurrence_interval (budget_recurrence_id, magnitude, unit) VALUES (?, ?, ?)");
        
        customScheduleRecurrenceInsert = new SQLInsert(datasource)
            .sql("INSERT INTO budget_recurrence_timeplot (budget_recurrence_id, occurs_at) VALUES (?, ?)");
        
        activeBudgetSelect = new SQLQuery(datasource)
            .sql(new DaoStatementLocator(BudgetDao.class, "activeBudgetSelect"));
        
        budgetByHolderAndNameSelect = new SQLQuery(datasource)
            .sql(new DaoStatementLocator(BudgetDao.class, "budgetByHolderAndNameSelect"));
        
        recurrenceByIdSelect = new SQLQuery(datasource)
            .sql(new DaoStatementLocator(BudgetDao.class, "recurrenceByIdSelect"));
        
        recurrenceByFixedIntervalSelect = new SQLQuery(datasource)
            .sql(new DaoStatementLocator(BudgetDao.class, "recurrenceByFixedIntervalSelect"));
        
        fixedIntervalByRecurrenceIdSelect = new SQLQuery(datasource)
            .sql(new DaoStatementLocator(BudgetDao.class, "fixedIntervalByRecurrenceIdSelect"));

        customScheduleByRecurrenceIdSelect = new SQLQuery(datasource)
            .sql(new DaoStatementLocator(BudgetDao.class, "customScheduleByRecurrenceIdSelect"));
    }
    
    public void insert(Budget budget) {
        budgetInsert.callForFirstGeneratedKey(
            () -> Arrays.asList(budget.name, budget.active, budget.holder.id),
            budget, Budget::id);
    }

    /**
     * Each account holder/user is allowed one active budget
     */
    public Budget getActiveBudget(Integer holderId) {
        return activeBudgetSelect.call(() -> Arrays.asList(holderId))
                    .first(baseBudgetRowConverter).orElse(null);
    }

    /**
     * Each account holder/user is allowed one active budget
     */
    public Budget getHolderBudgetByName(Integer holderId, String name) {
        return budgetByHolderAndNameSelect.call(() -> Arrays.asList(holderId, name))
                    .first(baseBudgetRowConverter).orElse(null);
    }
    
    public void insert(BudgetItem budgetItem) {
        budgetItemInsert.callForFirstGeneratedKey(
            () -> Arrays.asList(budgetItem.budget.id, budgetItem.type, budgetItem.description, budgetItem.amount,
                    budgetItem.transactsOn.id, budgetItem.intendedAccount.id, budgetItem.merchant),
            budgetItem, BudgetItem::id);
    }
    
    public List<BudgetItem> getItemsInRange(OffsetDateTime from, OffsetDateTime to) {
        return new ArrayList<BudgetItem>();
    }
    
    public Recurrence getRecurrenceById(Integer id) {
        return recurrenceByIdSelect.call(() -> Arrays.asList(id))
                .first((rs, valUtil) ->
                    new Recurrence()
                        .id(valUtil.convert(rs, Integer.class, "id"))
                        .startsAt(valUtil.convert(rs, OffsetDateTime.class, "startsAt"))
                        .endsAt(valUtil.convert(rs, OffsetDateTime.class, "endsAt"))
                        .method(getRecurrenceMethodByIdAndType(id, valUtil.convert(rs, String.class, "methodType")))
                ).orElse(null);
    }
    
    public RecurrenceMethod getRecurrenceMethodByIdAndType(Integer recurrenceId, String methodType)
    {
        if (methodType.equals(CustomRecurrenceSchedule.class.getName())) {
            return getCustomScheduleByRecurrenceId(recurrenceId);
        }
        else if (methodType.equals(FixedRecurrenceInterval.class.getName())) {
            return getFixedIntervalByRecurrenceId(recurrenceId);
        }
        else {
            throw new IllegalArgumentException("No recurrence method by type constant");
        }
    }
    
    public Recurrence findRecurrenceByFixedInterval(Recurrence recurrence) {
        return recurrenceByFixedIntervalSelect.call(() ->
                    Arrays.asList(recurrence.startsAt, recurrence.endsAt, ((FixedRecurrenceInterval) recurrence.method).magnitude,
                        ((FixedRecurrenceInterval) recurrence.method).unit)
                ).first((rs, valUtil) ->
                    new Recurrence()
                        .id(valUtil.convert(rs, Integer.class, "id"))
                        .startsAt(valUtil.convert(rs, OffsetDateTime.class, "startsAt"))
                        .endsAt(valUtil.convert(rs, OffsetDateTime.class, "endsAt"))
                        .method(new FixedRecurrenceInterval()
                            .magnitude(valUtil.convert(rs, Integer.class, "magnitude"))
                            .unit(valUtil.convert(rs, ChronoUnit.class, "unit"))))
                .orElse(null);
    }
    
    public FixedRecurrenceInterval getFixedIntervalByRecurrenceId(Integer recurrenceId) {
        return fixedIntervalByRecurrenceIdSelect.call(() -> Arrays.asList(recurrenceId))
                .first((rs, valUtil) -> new FixedRecurrenceInterval()
                    .magnitude(valUtil.convert(rs, Integer.class, "magnitude"))
                    .unit(valUtil.convert(rs, ChronoUnit.class, "unit")))
                .orElse(null);
    }

    public CustomRecurrenceSchedule getCustomScheduleByRecurrenceId(Integer recurrenceId) {
        return customScheduleByRecurrenceIdSelect.call(() -> Arrays.asList(recurrenceId))
                .mapInto(
                    () -> new CustomRecurrenceSchedule(),
                    (rs, valUtil) -> valUtil.convert(rs, OffsetDateTime.class, "occursAt"),
                    (occurrence, recurrence) -> recurrence.addOccurrence(occurrence)
                );
    }
    
    public void insert(Recurrence recurrence)
    {
        recurrenceInsert.callForFirstGeneratedKey(
            () -> Arrays.asList(recurrence.startsAt, recurrence.endsAt, recurrence.method.getClass().getName()),
            recurrence, Recurrence::id);

        if (recurrence.method instanceof FixedRecurrenceInterval) {
            insert(recurrence.id, (FixedRecurrenceInterval) recurrence.method);
        }
        else if (recurrence.method instanceof CustomRecurrenceSchedule) {
            insert(recurrence.id, (CustomRecurrenceSchedule) recurrence.method);
        }
    }
    
    protected void insert(Integer recurrenceId, FixedRecurrenceInterval recurrence) {
        fixedIntervalRecurrenceInsert.call(
            () -> Arrays.asList(recurrenceId, recurrence.magnitude, recurrence.unit));
    }
    
    protected void insert(Integer recurrenceId, CustomRecurrenceSchedule recurrence) {
        recurrence.occurences.forEach(occurence ->
            customScheduleRecurrenceInsert.call(
                () -> Arrays.asList(recurrenceId, occurence))
        );
    }
}