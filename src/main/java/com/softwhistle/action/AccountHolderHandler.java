package com.softwhistle.action;

import static com.softwhistle.util.JsonExchangeHelper.requestObject;
import static com.softwhistle.util.JsonExchangeHelper.renderObject;

import java.util.Optional;

import ratpack.handling.Context;
import ratpack.handling.Handler;

import com.softwhistle.model.AccountHolder;
import com.softwhistle.model.EntityId;
import com.softwhistle.model.EntityNotFoundException;
import com.softwhistle.service.AccountManager;
import com.softwhistle.service.AccountService;
import com.softwhistle.service.ServiceExchange;

public class AccountHolderHandler implements Handler
{
    @Override
    public void handle(Context exchange)
    {
        try { exchange.byMethod(methodSpec -> methodSpec
                .post(() -> requestObject(exchange, AccountHolder.class).then(holder ->
                    renderObject(exchange, service(exchange).create(holder))))
                .get(() -> renderObject(exchange, Optional.ofNullable(service(exchange).getHolderById(holderId(exchange).id))
                    .orElseThrow(() -> new EntityNotFoundException(exchange.get(EntityId.class)))))
        ); } catch (Exception ex) { throw new RuntimeException(ex); }
    }

    protected AccountService service(Context exchange) {
        return new ActionContextBasicLookup(exchange).accountService();
    }

    protected EntityId<AccountHolder> holderId(Context exchange) {
        return new ActionContextBasicLookup(exchange).entityIdFor(AccountHolder.class);
    }
}
