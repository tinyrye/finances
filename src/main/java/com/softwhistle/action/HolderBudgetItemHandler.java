package com.softwhistle.action;

import static com.softwhistle.util.JsonExchangeHelper.requestObject;
import static com.softwhistle.util.JsonExchangeHelper.renderObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ratpack.handling.Context;
import ratpack.handling.Handler;

import com.softwhistle.model.BudgetItem;
import com.softwhistle.model.EntityId;
import com.softwhistle.service.BudgetService;
import com.softwhistle.service.HolderBudgetManager;
import com.softwhistle.service.ServiceExchange;

public class HolderBudgetItemHandler implements Handler
{
    private static final Logger LOG = LoggerFactory.getLogger(HolderBudgetItemHandler.class);

    @Override
    public void handle(Context exchange) {
        requestObject(exchange, BudgetItem.class).then(budgetItem -> {
            renderObject(exchange, managerFor(exchange).add(budgetItem));
        });
    }
    
    protected HolderBudgetManager managerFor(Context exchange) {
        return new ActionContextBasicLookup(exchange).budgetManagerForHolderAndBudget();
    }
}
