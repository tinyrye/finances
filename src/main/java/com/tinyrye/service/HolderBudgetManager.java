package com.tinyrye.service;

import java.time.OffsetDateTime;

import java.util.ArrayList;
import java.util.List;

import com.tinyrye.dao.AccountsDao;
import com.tinyrye.dao.BudgetDao;

import com.tinyrye.model.Account;
import com.tinyrye.model.AccountHolder;
import com.tinyrye.model.Budget;
import com.tinyrye.model.BudgetItem;
import com.tinyrye.model.EntityId;

public class HolderBudgetManager
{
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

    public HolderBudgetManager startActive()
    {
        loadActiveBudget();
        if (budget != null) {
            throw new IllegalArgumentException("You already have an active budget.");
        }
        serviceExchange.get(BudgetDao.class).insert(
            (budget = new Budget().holder(holder)
                .active(Boolean.TRUE)));
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
    
    public Budget getBudget() {
        return budget;
    }
    
    public boolean loaded() {
        return (budget != null);
    }
    
    public HolderBudgetManager loadActiveBudget() {
        budget = serviceExchange.get(BudgetDao.class).getActiveBudget(holder.id);
        return this;
    }
    
    public HolderBudgetManager loadBudget(String name) {
        budget = serviceExchange.get(BudgetDao.class).getHolderBudgetByName(holder.id, name);
        return this;
    }
    
    public HolderBudgetManager loadFullActiveBudget() {
        budget = fillInFullBudgetDetails(serviceExchange.get(BudgetDao.class).getActiveBudget(holder.id));
        return this;
    }
    
    public HolderBudgetManager loadFullBudget(String name) {
        budget = fillInFullBudgetDetails(serviceExchange.get(BudgetDao.class).getHolderBudgetByName(holder.id, name));
        return this;
    }
    
    /**
     * Adds budget to existing budget.
     */
    public BudgetItem add(BudgetItem item)
    {
        checkBudgetLoaded();
        if ((item.budget != null) && (item.budget.id != budget.id)) {
            throw new IllegalArgumentException("Cannot add to different budget than the selected budget.");
        }
        else {
            if ((item.budget == null)) item.newBudget().id = budget.id;
            item.transactsOn = serviceExchange.get(BudgetService.class).getOrCreate(item.transactsOn);
            serviceExchange.get(BudgetDao.class).insert(item);
        }
        return item;
    }
    
    public List<BudgetItem> listItems() {
        checkBudgetLoaded();
        return new ArrayList<BudgetItem>();
    }
    
    public List<BudgetItem> listItemsDueWithinRange(OffsetDateTime from, OffsetDateTime to) {
        checkBudgetLoaded();
        return new ArrayList<BudgetItem>();
    }
    
    protected void checkBudgetLoaded() {
        if (budget == null) {
            throw new IllegalStateException("Cannot add item when no budget is selected.");
        }
    }
    
    protected Budget fillInFullBudgetDetails(Budget budget) {
        return budget;
    }
    
    protected void loadById(EntityId idPackager)
    {
        if ((idPackager.entityType == null) || (idPackager.entityType.equals(AccountHolder.class))) {
            loadById(idPackager.id);
        }
        else if (idPackager.entityType.equals(Account.class)) {
            // TODO: load by account's holder.
        }
        else {
            throw new UnsupportedOperationException("Not supported.");
        }
    }
    
    protected void loadById(Integer holderId) {
        holder = serviceExchange.get(AccountsDao.class).getHolderById(holderId);
    }
}