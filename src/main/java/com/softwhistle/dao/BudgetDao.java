package com.softwhistle.dao;

import static java.util.Arrays.asList;
import static com.softwhistle.jdbc.StatementBuilder.eqCompIfNotNull;
import static com.softwhistle.jdbc.StatementBuilder.inClause;
import static com.softwhistle.util.Values.optMap;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.softwhistle.jdbc.DaoStatementLocator;
import com.softwhistle.jdbc.ParametersBuilder;
import com.softwhistle.jdbc.RowConverter;
import com.softwhistle.jdbc.StatementBuilder;
import com.softwhistle.jdbc.SQLInsert;
import com.softwhistle.jdbc.SQLQuery;
import com.softwhistle.jdbc.SQLUpdate;

import com.softwhistle.model.Account;
import com.softwhistle.model.Budget;
import com.softwhistle.model.BudgetItem;
import com.softwhistle.model.BudgetItemType;
import com.softwhistle.model.CustomOccurrenceSchedule;
import com.softwhistle.model.FixedFieldOccurrences;
import com.softwhistle.model.FixedUnitOccurrences;
import com.softwhistle.model.OccurrenceSchedule;
import com.softwhistle.model.OccurrenceScheduleIdHolder;

import com.softwhistle.util.CaseSupplier;

public class BudgetDao
{
    private static final Logger LOG = LoggerFactory.getLogger(BudgetDao.class);

    private final DataSource datasource;
    private final SQLInsert budgetInsert;
    private final SQLInsert budgetItemInsert;
    private final SQLInsert occurrenceScheduleInsert;
    private final SQLInsert fixedUnitOccurrencesInsert;
    private final SQLInsert fixedFieldOccurrencesInsert;
    private final SQLInsert customScheduleOccurrenceInsert;
    private final SQLQuery itemSelect;
    private final SQLQuery activeBudgetSelect;
    private final SQLQuery budgetByHolderAndNameSelect;
    private final SQLQuery occurrenceScheduleTypeByIdSelect;
    private final DaoStatementLocator occurrenceScheduleTypesSelectStmt;
    private final SQLQuery customOccurrenceScheduleByIdSelect;
    private final SQLQuery customOccurrenceSchedulesByIdsSelect;
    private final SQLQuery fixedUnitOccurrencesByIdSelect;
    private final DaoStatementLocator fixedUnitOccurrencesSelectStmt;
    private final SQLQuery fixedFieldOccurrencesByIdSelect;
    private final DaoStatementLocator fixedFieldOccurrencesSelectStmt;
    private final SQLUpdate allBudgetItemsDelete;
    private final SQLUpdate categorizedBudgetItemsDelete;

    private final RowConverter<Budget> baseBudgetRowConverter = (resultSet, valueUtility) ->
        new Budget().id(valueUtility.convert(resultSet, Integer.class, "id"))
            .active(valueUtility.convert(resultSet, Boolean.class, "active"))
            .name(valueUtility.convert(resultSet, String.class, "name"));

    private final RowConverter<FixedUnitOccurrences> baseFixedUnitOccurrencesConverter = (resultSet, valueUtility) ->
        new FixedUnitOccurrences().id(valueUtility.convert(resultSet, Integer.class, "id"))
            .startsAt(valueUtility.convert(resultSet, OffsetDateTime.class, "startsAt"))
            .endsAt(valueUtility.convert(resultSet, OffsetDateTime.class, "endsAt"))
            .magnitude(valueUtility.convert(resultSet, Integer.class, "magnitude"))
            .unit(valueUtility.convert(resultSet, ChronoUnit.class, "unit"));

    private final RowConverter<FixedFieldOccurrences> baseFixedFieldOccurrencesConverter = (resultSet, valueUtility) ->
        new FixedFieldOccurrences().id(valueUtility.convert(resultSet, Integer.class, "id"))
            .startsAt(valueUtility.convert(resultSet, OffsetDateTime.class, "startsAt"))
            .endsAt(valueUtility.convert(resultSet, OffsetDateTime.class, "endsAt"))
            .magnitude(valueUtility.convert(resultSet, Integer.class, "magnitude"))
            .field(valueUtility.convert(resultSet, ChronoField.class, "field"));    

    public BudgetDao(DataSource datasource)
    {
        this.datasource = datasource;

        budgetInsert = new SQLInsert(datasource)
            .sql("INSERT INTO budget (name, active, account_holder_id, starts_at, ends_at)\n" +
                 "VALUES (?, ?, ?, ?, ?)");
        
        budgetItemInsert = new SQLInsert(datasource)
            .sql("INSERT INTO budget_item (budget_id, description, amount, budget_occurrence_schedule_id, holder_account_id, merchant_account_id, merchant) " +
                 "VALUES (?, ?, ?, ?, ?, ?, ?)");
        
        occurrenceScheduleInsert = new SQLInsert(datasource)
            .sql("INSERT INTO budget_occurrence_schedule (type) VALUES (?)");
        
        fixedUnitOccurrencesInsert = new SQLInsert(datasource)
            .sql("INSERT INTO budget_fixed_unit_occurrences (budget_occurrence_schedule_id, starts_at, ends_at, magnitude, unit) VALUES (?, ?, ?, ?, ?)");

        fixedFieldOccurrencesInsert = new SQLInsert(datasource)
            .sql("INSERT INTO budget_fixed_field_occurrences (budget_occurrence_schedule_id, starts_at, ends_at, magnitude, field) VALUES (?, ?, ?, ?, ?)");

        customScheduleOccurrenceInsert = new SQLInsert(datasource)
            .sql("INSERT INTO budget_custom_occurrences (budget_occurrence_schedule_id, occurs_at) VALUES (?, ?)");
        
        activeBudgetSelect = new SQLQuery(datasource)
            .sql(new DaoStatementLocator(BudgetDao.class, "activeBudgetSelect"));
        
        itemSelect = new SQLQuery(datasource)
            .sql(new DaoStatementLocator(BudgetDao.class, "itemSelect"));

        budgetByHolderAndNameSelect = new SQLQuery(datasource)
            .sql(new DaoStatementLocator(BudgetDao.class, "budgetByHolderAndNameSelect"));

        occurrenceScheduleTypeByIdSelect = new SQLQuery(datasource)
            .sql(new DaoStatementLocator(BudgetDao.class, "occurrenceScheduleTypeByIdSelect"));
        
        occurrenceScheduleTypesSelectStmt = new DaoStatementLocator(BudgetDao.class, "occurrenceScheduleTypesSelect");

        fixedUnitOccurrencesByIdSelect = new SQLQuery(datasource)
            .sql(new DaoStatementLocator(BudgetDao.class, "fixedUnitOccurrencesByIdSelect"));
        
        fixedFieldOccurrencesByIdSelect = new SQLQuery(datasource)
            .sql(new DaoStatementLocator(BudgetDao.class, "fixedFieldOccurrencesByIdSelect"));
        
        customOccurrenceScheduleByIdSelect = new SQLQuery(datasource)
            .sql(new DaoStatementLocator(BudgetDao.class, "customOccurrencesByIdSelect"));
        
        customOccurrenceSchedulesByIdsSelect = new SQLQuery(datasource)
            .sql(new DaoStatementLocator(BudgetDao.class, "customOccurrencesByIdsSelect"));
        
        fixedUnitOccurrencesSelectStmt = new DaoStatementLocator(BudgetDao.class, "fixedUnitOccurrencesSelect");
        fixedFieldOccurrencesSelectStmt = new DaoStatementLocator(BudgetDao.class, "fixedFieldOccurrencesSelect");

        allBudgetItemsDelete = new SQLUpdate(datasource).sql(new DaoStatementLocator(
            BudgetDao.class, "allBudgetItemsDelete"));
        categorizedBudgetItemsDelete = new SQLUpdate(datasource).sql(new DaoStatementLocator(
            BudgetDao.class, "categorizedBudgetItemsDelete"));
    }
    
    public void insert(Budget budget) {
        budgetInsert.call(() -> asList(budget.name, budget.active, budget.holder.id,
                budget.startsAt, budget.endsAt))
            .firstRowKey(budget, Budget::id);
    }

    /**
     * Each account holder/user is allowed one active budget
     */
    public Budget getActiveBudget(Integer holderId) {
        return activeBudgetSelect.call(() -> asList(holderId)).first(baseBudgetRowConverter)
            .orElse(null);
    }

    /**
     * Each account holder/user is allowed one active budget
     */
    public Budget getHolderBudgetByName(Integer holderId, String name) {
        return budgetByHolderAndNameSelect.call(() -> asList(holderId, name))
            .first(baseBudgetRowConverter).orElse(null);
    }
    
    public void insert(Integer budgetId, BudgetItem budgetItem) {
        budgetItemInsert.call(
            () -> asList(budgetId, budgetItem.description, budgetItem.amount,
                budgetItem.transactsOn.id(), optMap(budgetItem.holderAccount, a -> a.id),
                optMap(budgetItem.merchantAccount, a -> a.id),
                budgetItem.merchant))
            .firstRowKey(budgetItem, BudgetItem::id);
    }
    
    public List<BudgetItem> getItems(Integer budgetId) {
        return itemSelect.call(() -> asList(budgetId))
            .map((rs, valUtil) -> new BudgetItem()
                .id(valUtil.convert(rs, Integer.class, "id"))
                .amount(valUtil.convert(rs, Double.class, "amount"))
                .description(valUtil.convert(rs, String.class, "description"))
                .transactsOn(optMap(valUtil.convert(rs, Integer.class, "transactsOn.id"),
                    transactsOnId -> new OccurrenceScheduleIdHolder()
                        .id(transactsOnId)))
                .holderAccount(optMap(valUtil.convert(rs, Integer.class, "holderAccount.id"),
                    holderAccountId -> new Account().id(holderAccountId)))
                .merchant(valUtil.convert(rs, String.class, "merchant")));
    }
    
    public OccurrenceSchedule getOccurrenceScheduleById(Integer id) {
        return occurrenceScheduleTypeByIdSelect.call(() -> asList(id))
            .first((rs, valUtil) ->
                new CaseSupplier<String,OccurrenceSchedule>()
                    .on(type -> type.equals(CustomOccurrenceSchedule.class.getName()))
                        .give(() -> getCustomOccurrenceScheduleById(id))
                    .on(type -> type.equals(FixedUnitOccurrences.class.getName()))
                        .give(() -> getFixedUnitOccurrencesById(id))
                    .on(type -> type.equals(FixedFieldOccurrences.class.getName()))
                        .give(() -> getFixedFieldOccurrencesById(id))
                    .otherwise(type -> {
                        LOG.warn("Invalid occurrence schedule type: id={}; invalidType={}", new Object[] { id, type });
                        return null;
                    })
                .apply(valUtil.convert(rs, String.class, "type")))
            .orElse(null);
    }

    public Map<Integer,OccurrenceSchedule> getOccurrenceSchedulesByIds(List<Integer> ids)
    {
        if (ids == null || ids.isEmpty()) {
            return new HashMap<Integer,OccurrenceSchedule>();
        }
        List parameterValues = new ArrayList();
        final Map<String,List<IdAndTypeIntermediate>> idsByType =
            new SQLQuery(datasource).sql(new StatementBuilder(occurrenceScheduleTypesSelectStmt)
                .andWhereClauseLine(inClause("id", ids, parameterValues, 0))
                .toString())
            .call(() -> parameterValues)
            .map((rs, valUtil) -> new IdAndTypeIntermediate().id(valUtil.convert(rs, Integer.class, "id"))
                .type(valUtil.convert(rs, String.class, "type")))
            .stream().collect(Collectors.groupingBy(idAndType -> idAndType.type));
        final Map<Integer,OccurrenceSchedule> occurrences = new HashMap<Integer,OccurrenceSchedule>();
        idsByType.forEach((type, idsOfType) -> {
            LOG.info("Mapping schedules of type: type={}; idsByType={}", new Object[] {
                type, idsOfType
            });
            if (type.equals(CustomOccurrenceSchedule.class.getName())) {
                occurrences.putAll(getCustomOccurrenceSchedulesByIds(idsOfType.stream().map(idAndType -> idAndType.id).collect(Collectors.toList())));
            }
            else if (type.equals(FixedUnitOccurrences.class.getName())) {
                occurrences.putAll(getFixedUnitOccurrencesByIds(idsOfType.stream().map(idAndType -> idAndType.id).collect(Collectors.toList())));
            }
            else if (type.equals(FixedFieldOccurrences.class.getName())) {
                occurrences.putAll(getFixedFieldOccurrencesByIds(idsOfType.stream().map(idAndType -> idAndType.id).collect(Collectors.toList())));
            }
            else {
                LOG.warn("Ignoring invalid occurrence schedule type: ids={}; invalidType={}", new Object[] {
                    idsOfType, type
                });
            }
        });
        return occurrences;
    }

    public FixedUnitOccurrences findFixedUnitOccurrences(FixedUnitOccurrences occurrence) {
        return new SQLQuery(datasource).sql(new StatementBuilder(fixedUnitOccurrencesSelectStmt)
                .andWhereClauseLine(eqCompIfNotNull("starts_at", Optional.ofNullable(occurrence.startsAt)),
                    () -> "starts_at IS NULL")
                .andWhereClauseLine(eqCompIfNotNull("ends_at", Optional.ofNullable(occurrence.endsAt)),
                    () -> "ends_at IS NULL")
                .andWhereClauseLine(eqCompIfNotNull("magnitude", Optional.ofNullable(occurrence.magnitude)),
                    () -> "magnitude IS NULL")
                .andWhereClauseLine(eqCompIfNotNull("unit", Optional.ofNullable(occurrence.unit)),
                    () -> "unit IS NULL"))
            .call(new ParametersBuilder().add(Optional.ofNullable(occurrence.startsAt))
                .add(Optional.ofNullable(occurrence.endsAt))
                .add(Optional.ofNullable(occurrence.magnitude))
                .add(Optional.ofNullable(occurrence.unit)))
            .first(baseFixedUnitOccurrencesConverter)
            .orElse(null);
    }

    protected FixedUnitOccurrences getFixedUnitOccurrencesById(Integer id) {
        return fixedUnitOccurrencesByIdSelect.call(() -> asList(id))
            .first(baseFixedUnitOccurrencesConverter)
            .orElse(null);
    }

    protected Map<Integer,FixedUnitOccurrences> getFixedUnitOccurrencesByIds(List<Integer> ids)
    {
        List parameterValues = new ArrayList();
        return mapSingleBy(new SQLQuery(datasource).sql(new StatementBuilder(fixedUnitOccurrencesSelectStmt)
                    .andWhereClauseLine(inClause("budget_occurrence_schedule_id", ids, parameterValues, 0)))
                .call(() -> parameterValues)
                .map(baseFixedUnitOccurrencesConverter),
            occurrence -> occurrence.id);
    }
    
    public FixedFieldOccurrences findFixedFieldOccurrences(FixedFieldOccurrences occurrence) {
        return new SQLQuery(datasource).sql(new StatementBuilder(fixedFieldOccurrencesSelectStmt)
                .andWhereClauseLine(eqCompIfNotNull("starts_at", Optional.ofNullable(occurrence.startsAt)),
                    () -> "starts_at IS NULL")
                .andWhereClauseLine(eqCompIfNotNull("ends_at", Optional.ofNullable(occurrence.endsAt)),
                    () -> "ends_at IS NULL")
                .andWhereClauseLine(eqCompIfNotNull("magnitude", Optional.ofNullable(occurrence.magnitude)),
                    () -> "magnitude IS NULL")
                .andWhereClauseLine(eqCompIfNotNull("field", Optional.ofNullable(occurrence.field)),
                    () -> "field IS NULL"))
            .call(new ParametersBuilder().add(Optional.ofNullable(occurrence.startsAt))
                .add(Optional.ofNullable(occurrence.endsAt))
                .add(Optional.ofNullable(occurrence.magnitude))
                .add(Optional.ofNullable(occurrence.field)))
            .first(baseFixedFieldOccurrencesConverter)
            .orElse(null);
    }

    protected FixedFieldOccurrences getFixedFieldOccurrencesById(Integer id) {
        return fixedFieldOccurrencesByIdSelect.call(() -> asList(id))
            .first(baseFixedFieldOccurrencesConverter)
            .orElse(null);
    }

    protected Map<Integer,FixedFieldOccurrences> getFixedFieldOccurrencesByIds(List<Integer> ids)
    {
        List parameterValues = new ArrayList();
        return mapSingleBy(new SQLQuery(datasource).sql(new StatementBuilder(fixedFieldOccurrencesSelectStmt)
                    .andWhereClauseLine(inClause("budget_occurrence_schedule_id", ids, parameterValues, 0)))
                .call(() -> parameterValues)
                .map(baseFixedFieldOccurrencesConverter),
            occurrence -> occurrence.id);
    }

    protected OccurrenceSchedule getCustomOccurrenceScheduleById(Integer id)
    {
        CustomOccurrenceSchedule schedule = new CustomOccurrenceSchedule();
        customOccurrenceScheduleByIdSelect.call(() -> asList(id))
            .process((rs, valUtil) -> {
                schedule.id(valUtil.convert(rs, Integer.class, "id"));
                schedule.occurrences.add(valUtil.convert(rs, OffsetDateTime.class, "occursAt"));
            });
        return schedule;
    }
    
    protected Map<Integer,CustomOccurrenceSchedule> getCustomOccurrenceSchedulesByIds(List<Integer> ids)
    {
        Map<Integer,CustomOccurrenceSchedule> byIds = new HashMap<Integer,CustomOccurrenceSchedule>();
        customOccurrenceSchedulesByIdsSelect.call(() -> asList(ids))
            .process((rs, valUtil) -> {
                Integer id = valUtil.convert(rs, Integer.class, "id");
                if (! byIds.containsKey(id)) byIds.put(id, new CustomOccurrenceSchedule().id(id));
                byIds.get(id).occurrences.add(valUtil.convert(rs, OffsetDateTime.class, "occursAt"));
            });
        return byIds;
    }
    
    protected Optional<Integer> insertBase(OccurrenceSchedule occurrenceSchedule) {
        return occurrenceScheduleInsert.call(() -> asList(occurrenceSchedule.getClass().getName()))
            .firstRowKey();
    }
    
    public void insert(FixedUnitOccurrences occurrenceSchedule) {
        insertBase(occurrenceSchedule).ifPresent(key -> {
            occurrenceSchedule.id = key;
            fixedUnitOccurrencesInsert.call(
                () -> asList(occurrenceSchedule.id, occurrenceSchedule.startsAt, occurrenceSchedule.endsAt,
                    occurrenceSchedule.magnitude, occurrenceSchedule.unit));
        });
    }

    public void insert(FixedFieldOccurrences occurrenceSchedule) {
        insertBase(occurrenceSchedule).ifPresent(key -> {
            occurrenceSchedule.id = key;
            fixedFieldOccurrencesInsert.call(
                () -> asList(occurrenceSchedule.id, occurrenceSchedule.startsAt, occurrenceSchedule.endsAt,
                    occurrenceSchedule.magnitude, occurrenceSchedule.field));
        });
    }
    
    public void insert(CustomOccurrenceSchedule occurrenceSchedule) {
        insertBase(occurrenceSchedule).ifPresent(key -> {
            occurrenceSchedule.id = key;
            occurrenceSchedule.occurrences.forEach(occurrence -> {
                System.out.println(
                    String.format("Inserting occurrence in schedule: scheduleId=%s; occurrence=%s", 
                        occurrenceSchedule.id, occurrence));
                customScheduleOccurrenceInsert.call(() -> asList(occurrenceSchedule.id, occurrence));
            });
        });
    }

    public void deleteAllItems(Integer budgetId) {
        allBudgetItemsDelete.call(() -> asList(budgetId));
    }

    public void deleteCategorizedItems(Integer budgetId, List<String> jointCategorizations) {
        categorizedBudgetItemsDelete.call(() -> asList(budgetId, jointCategorizations));
    }

    private static class IdAndTypeIntermediate implements Serializable {
        public Integer id;
        public String type;
        public IdAndTypeIntermediate id(Integer id) { this.id = id; return this; }
        public IdAndTypeIntermediate type(String type) { this.type = type; return this; }
    }

    /**
     * Takes the place of <code>values.stream().collect(Collectors.groupingBy(classifier))</code>
     * because that method returns a <code>Map<K,List<T>></code> and rightfully so since 
     * it should not assume classifier produces unique key for each value.  In this DAO
     * there are circumstances where it is reasonably known beforehand since keys are database
     * ids.
     * WARNING: if classifier would map two of the same elements of values to the same
     * key then the last value shows up in map.
     */
    private static <T,K> Map<K,T> mapSingleBy(List<T> values, Function<T,K> classifier) {
        Map<K,T> byClassifier = new HashMap<K,T>();
        values.forEach(value -> byClassifier.put(classifier.apply(value), value));
        return byClassifier;
    }
}
