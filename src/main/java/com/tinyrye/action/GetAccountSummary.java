package com.tinyrye.action;

import javax.sql.DataSource;

import ratpack.handling.Context;
import ratpack.handling.Handler;

import com.tinyrye.model.EntityId;
import com.tinyrye.service.AccountService;

public class GetAccountSummary extends JsonInOutBaseHandler
{
    @Override
    public void handle(Context exchange) {
        renderObject(exchange, exchange.get(AccountService.class)
            .managerFor(exchange.maybeGet(EntityId.class)
            .orElseThrow(() -> new RuntimeException("Account id is required.")))
            .summary());
    }
}