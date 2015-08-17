package com.tinyrye.action;

import ratpack.handling.Context;
import ratpack.handling.Handler;

import com.tinyrye.model.EntityId;
import com.tinyrye.service.AccountManager;

public class GetAccount extends JsonInOutBaseHandler
{
    @Override
    public void handle(Context exchange) {
        renderObject(exchange, exchange.get(AccountManager.class).getById(exchange.getRequest().get(EntityId.class).id));
    }
}