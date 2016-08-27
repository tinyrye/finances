package com.softwhistle.action;

import static com.softwhistle.util.JsonExchangeHelper.requestObject;
import static com.softwhistle.util.JsonExchangeHelper.renderObject;

import ratpack.handling.Context;
import ratpack.handling.Handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.softwhistle.model.EntityId;
import com.softwhistle.model.OccurrenceSchedule;
import com.softwhistle.service.BudgetService;
import com.softwhistle.service.ServiceExchange;

public class BudgetOccurrenceHandler implements Handler
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
