package com.softwhistle.action;

import static com.softwhistle.util.JsonExchangeHelper.requestObject;
import static com.softwhistle.util.JsonExchangeHelper.renderObject;

import ratpack.handling.Context;
import ratpack.handling.Handler;

import com.softwhistle.model.EntityId;
import com.softwhistle.service.AccountService;

public class AccountSummaryHandler implements Handler
{
    @Override
    public void handle(Context exchange) {
        renderObject(exchange, exchange.get(AccountService.class)
            .managerFor(exchange.maybeGet(EntityId.class)
            .orElseThrow(() -> new RuntimeException("Account id is required.")))
            .summary());
    }
}
