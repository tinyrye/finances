package com.tinyrye.action;

import ratpack.handling.Context;

import com.tinyrye.model.AccountHolder;
import com.tinyrye.service.AccountManager;

public class CreateAccountHolder extends JsonInOutBaseHandler
{
    @Override
    public void handle(Context exchange) {
        requestObject(exchange, AccountHolder.class).then(holder ->
            renderObject(exchange, exchange.get(AccountManager.class).addUser(holder))
        );
    }
}