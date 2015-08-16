package com.tinyrye.action;

import ratpack.handling.Context;
import ratpack.jackson.Jackson;
import static ratpack.jackson.Jackson.fromJson;
import static ratpack.jackson.Jackson.json;

import com.tinyrye.model.Account;
import com.tinyrye.service.AccountManager;

public class CreateAccount extends JsonInOutBaseHandler
{
    @Override
    public void handle(Context exchange) {
        requestObject(exchange, Account.class).then(account ->
            renderObject(exchange, exchange.get(AccountManager.class).create(account))
        );
    }
}