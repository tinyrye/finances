package com.tinyrye.action;

import java.util.Optional;

import ratpack.handling.Context;

import com.tinyrye.model.AccountHolder;
import com.tinyrye.model.EntityId;
import com.tinyrye.service.AccountManager;
import com.tinyrye.service.AccountService;

public class AccountHolderHandler extends JsonInOutBaseHandler
{
    @Override
    public void handle(Context exchange)
    {
        try { exchange.byMethod(methodSpec -> methodSpec
                .post(() -> requestObject(exchange, AccountHolder.class).then(holder ->
                                    renderObject(exchange, exchange.get(AccountService.class).create(holder))))
                .get(() -> renderObject(exchange, managerFor(exchange).getHolder()))
        ); } catch (Exception ex) { throw new RuntimeException(ex); }
    }

    protected AccountManager managerFor(Context exchange) {
        return exchange.get(AccountService.class)
                    .managerFor(exchange.maybeGet(EntityId.class)
                        .orElseThrow(() -> new RuntimeException("Id is required in request.")));
    }
}