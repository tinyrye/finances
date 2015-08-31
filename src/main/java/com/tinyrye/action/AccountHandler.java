package com.tinyrye.action;

import java.util.Optional;

import ratpack.handling.Context;
import ratpack.handling.Handler;

import com.tinyrye.model.Account;
import com.tinyrye.model.EntityId;
import com.tinyrye.service.AccountManager;
import com.tinyrye.service.AccountService;

public class AccountHandler extends JsonInOutBaseHandler
{
    @Override
    public void handle(Context exchange) {
        try { exchange.byMethod(methodSpec -> methodSpec
                .get(() -> renderObject(exchange, managerFor(exchange).getAccount()))
                .post(() -> requestObject(exchange, Account.class).then(account -> 
                                renderObject(exchange, exchange.get(AccountService.class)
                                    .create(account))))
        ); } catch (Exception ex) { throw new RuntimeException(ex); }
    }
    
    protected AccountManager managerFor(Context exchange) {
        return exchange.get(AccountService.class)
                    .managerFor(exchange.maybeGet(EntityId.class)
                        .orElseThrow(() -> new RuntimeException("Id is required in request.")
                    ));
    }
}