package com.tinyrye.action;

import ratpack.handling.Context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinyrye.model.EntityId;
import com.tinyrye.model.Recurrence;
import com.tinyrye.service.BudgetService;
import com.tinyrye.service.ServiceExchange;

public class BudgetRecurrenceHandler extends JsonInOutBaseHandler
{
    private static final Logger LOG = LoggerFactory.getLogger(BudgetRecurrenceHandler.class);

    @Override
    public void handle(Context exchange)
    {
        try {
            exchange.byMethod(methodSpec -> methodSpec
                .get(() -> {
                    LOG.debug("Get recurrence by id: id={}", exchange.get(EntityId.class).id);
                    renderObject(exchange,
                        exchange.get(ServiceExchange.class).get(BudgetService.class)
                            .getRecurrenceById(exchange.get(EntityId.class).id));
                })
                .post(() -> {
                    requestObject(exchange, Recurrence.class).then(recurrence -> {
                        LOG.debug("Create recurrence: recurrence={}", recurrence);
                        renderObject(exchange,
                            exchange.get(ServiceExchange.class).get(BudgetService.class)
                                .getOrCreate(recurrence));
                    });
                }));
        } catch (Exception ex) { throw new RuntimeException(ex); }
    }
}