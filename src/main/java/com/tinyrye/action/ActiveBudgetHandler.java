package com.tinyrye.action;

import javax.sql.DataSource;

import ratpack.handling.Context;

import com.tinyrye.model.EntityId;
import com.tinyrye.service.HolderBudgetManager;
import com.tinyrye.service.BudgetService;

public class ActiveBudgetHandler extends JsonInOutBaseHandler
{
    @Override
    public void handle(Context exchange) {
        try { exchange.byMethod(methodSpec -> methodSpec
                .get(() -> renderObject(exchange, managerFor(exchange)
                                .loadFullActiveBudget()
                                .getBudget()))
                .post(() -> renderObject(exchange, managerFor(exchange)
                                .startActive()
                                .getBudget()))
            ); } catch (Exception ex) { throw new RuntimeException(ex); }
    }
    
    protected HolderBudgetManager managerFor(Context exchange) {
        return exchange.get(BudgetService.class)
                    .managerForAccountHolder(exchange
                        .get(EntityId.class).id);
    }
}