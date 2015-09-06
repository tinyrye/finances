package com.tinyrye.action;

import ratpack.handling.Context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinyrye.model.EntityId;
import com.tinyrye.model.OccurrenceSchedule;
import com.tinyrye.service.BudgetService;
import com.tinyrye.service.ServiceExchange;

public class BudgetOccurrenceHandler extends JsonInOutBaseHandler
{
    private static final Logger LOG = LoggerFactory.getLogger(BudgetOccurrenceHandler.class);

    @Override
    public void handle(Context exchange)
    {
        try {
            exchange.byMethod(methodSpec -> methodSpec
                .get(() -> {
                    LOG.debug("Get occurrence by id: id={}", exchange.get(EntityId.class).id);
                    renderObject(exchange,
                        exchange.get(ServiceExchange.class).get(BudgetService.class)
                            .getOccurrenceScheduleById(exchange.get(EntityId.class).id));
                })
                .post(() -> {
                    requestObject(exchange, OccurrenceSchedule.class).then(occurrence -> {
                        LOG.debug("Create occurrence: occurrence={}", occurrence);
                        renderObject(exchange,
                            exchange.get(ServiceExchange.class).get(BudgetService.class)
                                .getOrCreate(occurrence));
                    });
                }));
        } catch (Exception ex) { throw new RuntimeException(ex); }
    }
}