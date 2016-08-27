package com.softwhistle.action;

import static com.softwhistle.util.JsonExchangeHelper.requestObjects;
import static com.softwhistle.util.JsonExchangeHelper.renderObject;
import static com.softwhistle.util.ContextValues.optQueryParam;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ratpack.handling.Context;
import ratpack.handling.Handler;

import com.softwhistle.model.BudgetItem;
import com.softwhistle.model.BudgetItemType;
import com.softwhistle.service.HolderBudgetManager;
import com.softwhistle.service.ServiceExchange;

public class HolderBudgetItemsHandler implements Handler
{
    private static final Logger LOG = LoggerFactory.getLogger(HolderBudgetItemsHandler.class);
    
    @Override
    public void handle(Context exchange)
    {
        try { exchange.byMethod(methodSpec -> methodSpec
                .post(() -> requestObjects(exchange, BudgetItem.class).then(budgetItems -> {
                    applyGlobals(exchange, budgetItems);
                    renderObject(exchange, managerIn(exchange).addAll(budgetItems));
                }))
                .get(() -> renderObject(exchange, managerIn(exchange).listItems()))
                .delete(() -> {
                    managerIn(exchange).clearItems();
                    exchange.getResponse().status(200).send();
                })
        ); } catch (Exception ex) { throw new RuntimeException(ex); }
    }
    
    protected void applyGlobals(Context exchange, List<BudgetItem> budgetItems) {
        optQueryParam(exchange, "type").map(p -> BudgetItemType.valueOf(p)).ifPresent(type ->
            budgetItems.forEach(budgetItem -> {
                if (type == BudgetItemType.EXPENSE) {
                    budgetItem.amount(budgetItem.amount * -1.00D);
                }
            }));
    }
    
    protected HolderBudgetManager managerIn(Context exchange) {
        return new ActionContextBasicLookup(exchange).budgetManagerForHolderAndBudget();
    }
}
