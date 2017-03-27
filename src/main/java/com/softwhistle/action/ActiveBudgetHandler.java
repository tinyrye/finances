package com.softwhistle.action;

import static com.softwhistle.util.JsonExchangeHelper.requestObject;
import static com.softwhistle.util.JsonExchangeHelper.renderObject;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ratpack.handling.Context;
import ratpack.handling.Handler;

import com.softwhistle.model.AccountHolder;
import com.softwhistle.model.Budget;
import com.softwhistle.model.EntityId;
import com.softwhistle.model.EntityNotFoundException;
import com.softwhistle.service.HolderBudgetManager;
import com.softwhistle.service.BudgetService;
import com.softwhistle.service.ServiceExchange;

public class ActiveBudgetHandler implements Handler
{
    private static final Logger LOG = LoggerFactory.getLogger(ActiveBudgetHandler.class);
    
    @Override
    public void handle(Context exchange) {
        try { exchange.byMethod(methodSpec -> methodSpec
                .get(() -> renderObject(exchange, managerFor(exchange)
                    .loadActiveBudget().loadInFull()
                    .requireBudget((manager) ->
                        new EntityNotFoundException(new EntityId()
                                .id(manager.getHolder().id)
                                .entityType(AccountHolder.class),
                            Budget.class
                        ))))
                .post(() -> requestObject(exchange, Budget.class).then(budget -> {
                    LOG.info("Requested Budget to Set to Active: {}", budget);
                    renderObject(exchange, managerFor(exchange)
                        .startActive(budget).loadInFull()
                        .getHolder());
                }))
        ); } catch (Exception ex) { throw new RuntimeException(ex); }
    }
    
    protected HolderBudgetManager managerFor(Context exchange) {
        return new ActionContextBasicLookup(exchange).budgetManagerForHolder();
    }
}
