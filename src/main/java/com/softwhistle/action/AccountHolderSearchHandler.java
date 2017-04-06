package com.softwhistle.action;

import static com.softwhistle.util.JsonExchangeHelper.requestObject;
import static com.softwhistle.util.JsonExchangeHelper.renderObject;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ratpack.handling.Context;
import ratpack.handling.Handler;

import com.softwhistle.model.Account;
import com.softwhistle.model.AccountHolder;
import com.softwhistle.model.EntityId;
import com.softwhistle.service.AccountManager;
import com.softwhistle.service.AccountService;
import com.softwhistle.service.ServiceExchange;


public class AccountHolderSearchHandler implements Handler
{
    private static final Logger LOG = LoggerFactory.getLogger(AccountHolderSearchHandler.class);

	@Override
    public void handle(Context exchange) {
        try { exchange.byMethod(methodSpec -> methodSpec
                .post(() -> requestObject(exchange, AccountHolder.class).then(holder -> {
                    LOG.info("Searching holder: {}", holder);
                    renderObject(exchange, service(exchange).search(holder));
                }))
        ); } catch (Exception ex) { throw new RuntimeException(ex); }
    }

    protected AccountService service(Context exchange) {
        return new ActionContextBasicLookup(exchange).accountService();
    }
}