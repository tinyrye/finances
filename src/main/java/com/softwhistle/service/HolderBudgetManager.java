package com.softwhistle.service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.concurrent.atomic.DoubleAccumulator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.softwhistle.dao.AccountsDao;
import com.softwhistle.dao.BudgetDao;

import com.softwhistle.model.Account;
import com.softwhistle.model.AccountHolder;
import com.softwhistle.model.Budget;
import com.softwhistle.model.BudgetItem;
import com.softwhistle.model.BudgetItemType;
import com.softwhistle.model.BudgetLedgerItem;
import com.softwhistle.model.EntityId;
import com.softwhistle.model.EntityNotFoundException;
import com.softwhistle.model.OccurrenceSchedule;

public class HolderBudgetManager
{
    private static final Logger LOG = LoggerFactory.getLogger(HolderBudgetManager.class);

    private final ServiceExchange serviceExchange;
    private AccountHolder holder;
    private Budget budget;
    
    protected HolderBudgetManager(ServiceExchange serviceExchange) {
        this.serviceExchange = serviceExchange;
    }
    
    public HolderBudgetManager(ServiceExchange serviceExchange, Object holderReference)
    {
        this(serviceExchange);
        if (holderReference instanceof Integer) {
            loadById((Integer) holderReference);
        }
        else if (holderReference instanceof EntityId) {
            loadById((EntityId) holderReference);
        }
        else if (holderReference instanceof AccountHolder) {
            this.holder = (AccountHolder) holder;
        }
    }
    
    public HolderBudgetManager startActive() {
        return startActive(new Budget().startsAt(OffsetDateTime.now()));
    }
    
    public HolderBudgetManager startActive(Budget referenceBudget)
    {
        loadActiveBudget();
        if (budget != null) {
            throw new IllegalArgumentException("You already have an active budget.");
        }
        serviceExchange.get(BudgetDao.class).insert(
            (budget = referenceBudget.active(Boolean.TRUE)
                .holder(holder)));
        return this;
    }
    
    public HolderBudgetManager startAlternative(String name)
    {
        loadBudget(name);
        if (budget != null) {
            throw new IllegalArgumentException("You already have a budget by name.");
        }
        serviceExchange.get(BudgetDao.class).insert(
            (budget = new Budget().holder(holder).active(Boolean.FALSE)
                .name(name)));
        return this;
    }
    
    public AccountHolder getHolder() {
        return holder;
    }
    
    public Budget getBudget() {
        return budget;
    }

    public Budget requireBudget(Function<HolderBudgetManager,EntityNotFoundException> orElseThrow) {
        if (budget == null) throw orElseThrow.apply(this);
        else return budget;
    } 
    
    public boolean loaded() {
        return (budget != null);
    }
    
    public HolderBudgetManager loadActiveBudget() {
        budget = serviceExchange.get(BudgetDao.class).getActiveBudget(holder.id);
        return this;
    }
    
    public HolderBudgetManager loadInFull() {
        holder = loadAllMeta(holder);
        if (budget != null) budget = loadAllMeta(budget);
        return this;
    }

    public HolderBudgetManager loadBudget(String name) {
        budget = serviceExchange.get(BudgetDao.class).getHolderBudgetByName(holder.id, name);
        return this;
    }
    
    /**
     * Adds budget item to existing budget.
     */
    public HolderBudgetManager add(BudgetItem item)
    {
        if (item.holderAccount == null) {
            item.holderAccount = budget.holder.primaryAccount;
        }
        item.transactsOn = serviceExchange.get(BudgetService.class).getOrCreate(item.transactsOn);
        serviceExchange.get(BudgetDao.class).insert(budget.id, item);
        budget.items.add(item);
        return this;
    }

    /**
     * Adds budget items to existing budget.
     */
    public HolderBudgetManager addAll(List<BudgetItem> items) {
        items.forEach(item -> add(item));
        return this;
    }
    
    public List<BudgetItem> listItems()
    {
        List<BudgetItem> items = serviceExchange.get(BudgetDao.class).getItems(budget.id);
        serviceExchange.get(BudgetDao.class)
            .getOccurrenceSchedulesByIds(items.stream().map((item) -> item.transactsOn.id())
                .collect(Collectors.toList()))
            .forEach((id, schedule) -> items.forEach(item -> {
                if (item.transactsOn.id().equals(id)) item.transactsOn = schedule;
            }));
        return items;
    }
    
    public void processItems(Consumer<BudgetItem> processor) {
        listItems().stream().forEach(processor);
    }
    
    public List<BudgetLedgerItem> projectOccurrences(OffsetDateTime from, OffsetDateTime to) {
        final List<BudgetLedgerItem> transactions = new ArrayList<BudgetLedgerItem>();
        final DoubleAccumulator sum = new DoubleAccumulator((curr, next) -> (curr + next), 0.0d);
        processItems(item ->
            item.transactsOn.occurrences(from, to).forEachRemaining(occurrence ->
                transactions.add(new BudgetLedgerItem().item(item).transactsOn(occurrence))));
        final List<BudgetLedgerItem> transactionsInOrder = transactions.stream().sorted((tx1, tx2) -> tx1.transactsOn.compareTo(tx2.transactsOn))
            .collect(Collectors.toList());
        transactionsInOrder.stream().forEach(transaction -> {
            sum.accumulate(transaction.item.amount);
            transaction.ledgerTotal = sum.get();
        });
        return transactionsInOrder;
    }

    public Double projectExpenditures(OffsetDateTime from, OffsetDateTime to)
    {
        DoubleAccumulator sum = new DoubleAccumulator((curr, next) -> (curr + next), 0.0d);
        processItems(item ->
            item.transactsOn.occurrences(from, to).forEachRemaining(occurrence -> {
                LOG.info("Processing occurrence of budget item: id={}; description={}; amount={}; occurrence={}; scheduleType={}", new Object[] {
                    item.id, item.description, item.amount, occurrence, item.transactsOn.getClass().getName()
                });
                sum.accumulate(item.amount);
            }));
        return sum.get();
    }
    
    public void clearItems() {
        serviceExchange.get(BudgetDao.class).deleteAllItems(budget.id);
    }
    
    public void clearItems(List<String> jointCategorizations) {
        serviceExchange.get(BudgetDao.class).deleteCategorizedItems(
            budget.id, jointCategorizations);
    }
    
    protected AccountHolder loadAllMeta(AccountHolder holder) {
        holder.primaryAccount = serviceExchange.get(AccountsDao.class).getPrimaryByHolderId(holder.id);
        return holder;
    }
    
    protected Budget loadAllMeta(Budget budget) {
        budget.holder = holder;
        budget.items = listItems();
        return budget;
    }
    
    protected void loadById(EntityId idPackager)
    {
        if ((idPackager.entityType == null) || (idPackager.entityType.equals(AccountHolder.class))) {
            loadById(idPackager.id);
        }
        else if (idPackager.entityType.equals(Account.class)) {
            // TODO: load by account's holder.
            throw new UnsupportedOperationException();
        }
        else {
            throw new UnsupportedOperationException();
        }
    }
    
    protected void loadById(Integer holderId) {
        holder = serviceExchange.get(AccountsDao.class).getHolderById(holderId);
        if (holder == null) throw new EntityNotFoundException(new EntityId().id(holderId).entityType(AccountHolder.class));
    }
}
