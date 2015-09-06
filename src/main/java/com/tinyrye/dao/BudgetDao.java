package com.tinyrye.dao;

import static com.tinyrye.jdbc.StatementBuilder.eqCompIfNotNull;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinyrye.jdbc.DaoStatementLocator;
import com.tinyrye.jdbc.ParametersBuilder;
import com.tinyrye.jdbc.RowConverter;
import com.tinyrye.jdbc.StatementBuilder;
import com.tinyrye.jdbc.SQLInsert;
import com.tinyrye.jdbc.SQLQuery;

import com.tinyrye.model.Budget;
import com.tinyrye.model.BudgetItem;
import com.tinyrye.model.CustomOccurrenceSchedule;
import com.tinyrye.model.FixedOccurrenceInterval;
import com.tinyrye.model.OccurrenceSchedule;

public class BudgetDao
{
    private static final Logger LOG = LoggerFactory.getLogger(BudgetDao.class);

    private final DataSource datasource;
    private final SQLInsert budgetInsert;
    private final SQLInsert budgetItemInsert;
    private final SQLInsert occurrenceScheduleInsert;
    private final SQLInsert fixedOccurrenceIntervalInsert;
    private final SQLInsert customScheduleOccurrenceInsert;
    private final SQLQuery activeBudgetSelect;
    private final SQLQuery budgetByHolderAndNameSelect;
    private final SQLQuery occurrenceScheduleTypeByIdSelect;
    private final SQLQuery customOccurrenceScheduleByIdSelect;
    private final SQLQuery fixedOccurrenceIntervalByIdSelect;
    private final DaoStatementLocator fixedOccurrenceIntervalSelectStmt;

    private final RowConverter<Budget> baseBudgetRowConverter = (resultSet, valueUtility) ->
        new Budget().id(valueUtility.convert(resultSet, Integer.class, "id"))
            .active(valueUtility.convert(resultSet, Boolean.class, "active"))
            .name(valueUtility.convert(resultSet, String.class, "name"));

    private final RowConverter<FixedOccurrenceInterval> baseFixedOccurrenceIntervalConverter = (resultSet, valueUtility) ->
        new FixedOccurrenceInterval().id(valueUtility.convert(resultSet, Integer.class, "id"))
            .startsAt(valueUtility.convert(resultSet, OffsetDateTime.class, "startsAt"))
            .endsAt(valueUtility.convert(resultSet, OffsetDateTime.class, "endsAt"))
            .magnitude(valueUtility.convert(resultSet, Integer.class, "magnitude"))
            .unit(valueUtility.convert(resultSet, ChronoUnit.class, "unit"));
    
    public BudgetDao(DataSource datasource)
    {
        this.datasource = datasource;

        budgetInsert = new SQLInsert(datasource)
            .sql("INSERT INTO budget (name, active, account_holder_id) VALUES (?, ?, ?)");
        
        budgetItemInsert = new SQLInsert(datasource)
            .sql("INSERT INTO budget_item (budget_id, item_type, description, amount, budget_recurrence_id, account_id, merchant) " +
                 "VALUES (?, ?, ?, ?, ?, ?, ?)");
        
        occurrenceScheduleInsert = new SQLInsert(datasource)
            .sql("INSERT INTO budget_occurrence (type) VALUES (?)");
        
        fixedOccurrenceIntervalInsert = new SQLInsert(datasource)
            .sql("INSERT INTO budget_fixed_interval_occurrence (budget_occurrence_id, starts_at, ends_at, magnitude, unit) VALUES (?, ?, ?, ?, ?)");
        
        customScheduleOccurrenceInsert = new SQLInsert(datasource)
            .sql("INSERT INTO budget_custom_occurrence (budget_occurrence_id, occurs_at) VALUES (?, ?)");
        
        activeBudgetSelect = new SQLQuery(datasource)
            .sql(new DaoStatementLocator(BudgetDao.class, "activeBudgetSelect"));
        
        budgetByHolderAndNameSelect = new SQLQuery(datasource)
            .sql(new DaoStatementLocator(BudgetDao.class, "budgetByHolderAndNameSelect"));
        
        occurrenceScheduleTypeByIdSelect = new SQLQuery(datasource)
            .sql(new DaoStatementLocator(BudgetDao.class, "occurrenceScheduleTypeByIdSelect"));
        
        fixedOccurrenceIntervalByIdSelect = new SQLQuery(datasource)
            .sql(new DaoStatementLocator(BudgetDao.class, "fixedOccurrenceIntervalByIdSelect"));
        
        customOccurrenceScheduleByIdSelect = new SQLQuery(datasource)
            .sql(new DaoStatementLocator(BudgetDao.class, "customOccurrenceScheduleByIdSelect"));
        
        fixedOccurrenceIntervalSelectStmt = new DaoStatementLocator(BudgetDao.class, "fixedOccurrenceIntervalSelect");
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
                    budgetItem.transactsOn.id(), budgetItem.intendedAccount.id, budgetItem.merchant),
            budgetItem, BudgetItem::id);
    }
    
    public List<BudgetItem> getItemsInRange(OffsetDateTime from, OffsetDateTime to) {
        return new ArrayList<BudgetItem>();
    }
    
    public OccurrenceSchedule getOccurrenceScheduleById(Integer id)
    {
        /* return occurrenceScheduleTypeByIdSelect.call(() -> Arrays.asList(id))
                .first((rs, valUtil) ->
                    new CaseSupplier<String,OccurrenceSchedule>()
                        .on(type -> type.equals(CustomOccurrenceSchedule.class.getName()))
                            .give(() -> getCustomOccurrenceScheduleById(id))
                        .on(type -> type.equals(FixedOccurrenceInterval.class.getName()))
                            .give(() -> getFixedOccurrenceIntervalById(id))
                        .otherwise(type -> {
                            LOG.warn("Invalid occurrence schedule type: id={}; invalidType={}", new Object[] { id, type });
                            return null;
                        })
                    .apply(valUtil.convert(rs, String.class, "type")))
                .orElse(null); */
        final String occurrenceScheduleType =
            occurrenceScheduleTypeByIdSelect.call(() -> Arrays.asList(id))
                .first((rs, valUtil) -> valUtil.convert(rs, String.class, "type"))
                .orElse(null);
        if (occurrenceScheduleType == null) {
            return null;
        }
        else if (occurrenceScheduleType.equals(CustomOccurrenceSchedule.class.getName())) {
            return getCustomOccurrenceScheduleById(id);
        }
        else if (occurrenceScheduleType.equals(FixedOccurrenceInterval.class.getName())) {
            return getFixedOccurrenceIntervalById(id);
        }
        else {
            LOG.warn("Invalid occurrence schedule type: id={}; invalidType={}", new Object[] {
                id, occurrenceScheduleType
            });
            return null;
        }
    }

    public FixedOccurrenceInterval findFixedOccurrenceInterval(FixedOccurrenceInterval occurrence) {
        return new SQLQuery(datasource).sql(new StatementBuilder(fixedOccurrenceIntervalSelectStmt)
                .andWhereClauseLine(eqCompIfNotNull("starts_at", Optional.ofNullable(occurrence.startsAt)),
                    () -> "starts_at IS NULL")
                .andWhereClauseLine(eqCompIfNotNull("ends_at", Optional.ofNullable(occurrence.endsAt)),
                    () -> "ends_at IS NULL")
                .andWhereClauseLine(eqCompIfNotNull("magnitude", Optional.ofNullable(occurrence.magnitude)),
                    () -> "magnitude IS NULL")
                .andWhereClauseLine(eqCompIfNotNull("unit", Optional.ofNullable(occurrence.magnitude)),
                    () -> "unit IS NULL"))
            .call(new ParametersBuilder().add(Optional.ofNullable(occurrence.startsAt))
                .add(Optional.ofNullable(occurrence.endsAt))
                .add(Optional.ofNullable(occurrence.magnitude))
                .add(Optional.ofNullable(occurrence.unit)))
            .first(baseFixedOccurrenceIntervalConverter)
            .orElse(null);
    }

    protected OccurrenceSchedule getFixedOccurrenceIntervalById(Integer id) {
        return fixedOccurrenceIntervalByIdSelect.call(() -> Arrays.asList(id))
            .first(baseFixedOccurrenceIntervalConverter)
            .orElse(null);
    }

    protected OccurrenceSchedule getCustomOccurrenceScheduleById(Integer id)
    {
        CustomOccurrenceSchedule schedule = new CustomOccurrenceSchedule();
        customOccurrenceScheduleByIdSelect.call(() -> Arrays.asList(id))
            .process((rs, valUtil) -> {
                schedule.id(valUtil.convert(rs, Integer.class, "id"));
                schedule.occurrences.add(valUtil.convert(rs, OffsetDateTime.class, "occursAt"));
            });
        return schedule;
    }

    public void insert(OccurrenceSchedule occurrence)
    {
        occurrenceScheduleInsert.callForFirstGeneratedKey(
                () -> Arrays.asList(occurrence.getClass().getName()))
            .ifPresent(key -> {
                if (occurrence instanceof FixedOccurrenceInterval) {
                    insert(key, (FixedOccurrenceInterval) occurrence);
                }
                else if (occurrence instanceof CustomOccurrenceSchedule) {
                    insert(key, (CustomOccurrenceSchedule) occurrence);
                }
            });
    }
    
    protected void insert(Integer occurrenceId, FixedOccurrenceInterval occurrence)
    {
        occurrence.id(occurrenceId);
        fixedOccurrenceIntervalInsert.call(
            () -> Arrays.asList(occurrenceId, occurrence.startsAt, occurrence.endsAt,
                occurrence.magnitude, occurrence.unit));
    }
    
    protected void insert(Integer occurrenceId, CustomOccurrenceSchedule occurrence)
    {
        occurrence.id(occurrenceId);
        occurrence.occurrences.forEach(occurence ->
            customScheduleOccurrenceInsert.call(
                () -> Arrays.asList(occurrenceId, occurrence))
        );
    }
}