package com.tinyrye.action;

import javax.sql.DataSource;

import ratpack.handling.Context;

import com.tinyrye.model.BudgetItem;
import com.tinyrye.model.EntityId;
import com.tinyrye.service.BudgetService;
import com.tinyrye.service.HolderBudgetManager;

public class HolderActiveBudgetItemHandler extends JsonInOutBaseHandler
{
    @Override
    public void handle(Context exchange)
    {
        try { exchange.byMethod(methodSpec -> methodSpec
                .post(() -> requestObject(exchange, BudgetItem.class).then(budgetItem -> {
                    renderObject(exchange, managerFor(exchange).add(budgetItem));
                }))
                .get(() -> renderObject(exchange, managerFor(exchange).listItems()))
        ); } catch (Exception ex) { throw new RuntimeException(ex); }
    }
    
    protected HolderBudgetManager managerFor(Context exchange) {
        return exchange.get(BudgetService.class)
                    .managerForAccountHolder(exchange
                        .get(EntityId.class).id);
    }
}