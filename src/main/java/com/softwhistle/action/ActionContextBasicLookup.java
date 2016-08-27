package com.softwhistle.action;

import ratpack.handling.Context;

import com.softwhistle.model.AccountHolder;
import com.softwhistle.model.EntityId;
import com.softwhistle.service.AccountManager;
import com.softwhistle.service.AccountService;
import com.softwhistle.service.BudgetService;
import com.softwhistle.service.HolderBudgetManager;
import com.softwhistle.service.ServiceExchange;

public class ActionContextBasicLookup
{
    private final Context exchange;

    public ActionContextBasicLookup(Context exchange) {
        this.exchange = exchange;
    }
    
    protected ServiceExchange serviceExchange() {
        return exchange.get(ServiceExchange.class);
    }
    
    public AccountService accountService() {
        return serviceExchange().get(AccountService.class);
    }
    
    public BudgetService budgetService() {
        return serviceExchange().get(BudgetService.class);
    }
    
    public AccountManager accountManagerFor(EntityId accountReferrer) {  
        return accountService().managerFor(accountReferrer);
    }
    
    public AccountManager accountManagerFor(Class accountReferrerEntityType) {  
        return accountManagerFor(entityIdFor(accountReferrerEntityType));
    }
    
    public HolderBudgetManager budgetManagerForHolder(EntityId holderReferrer) {  
        return budgetService().managerForAccountHolder(holderReferrer);
    }
    
    public HolderBudgetManager budgetManagerForHolder() {  
        return budgetManagerForHolder(entityIdFor(AccountHolder.class));
    }

    public HolderBudgetManager budgetManagerForHolderAndBudget(EntityId holderReferrer) {
        return thenLoadBudget(budgetManagerForHolder(holderReferrer));
    }

    public HolderBudgetManager budgetManagerForHolderAndBudget() {
        return thenLoadBudget(budgetManagerForHolder());
    }

    protected HolderBudgetManager thenLoadBudget(HolderBudgetManager manager)
    {
        if (exchange.getPathTokens().containsKey("budgetName")) {
            manager.loadBudget(exchange.getPathTokens().get("budgetName"));
        }
        else {
            manager.loadActiveBudget();
        }
        return manager.loadInFull();
    }
    
    public <T> EntityId<T> entityIdFor(Class<T> entityType)
    {
        return exchange.maybeGet(EntityId.class).map(i -> {
                if (i.entityType != null && ! i.entityType.equals(entityType)) {
                    throw new RuntimeException("Entity id in context for different entity type");
                }
                return i.entityType(entityType);
            })
            .orElseThrow(() -> new RuntimeException("Id is required in request."));
    }
}
