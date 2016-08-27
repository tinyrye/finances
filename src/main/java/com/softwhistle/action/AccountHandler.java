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

public class AccountHandler implements Handler
{
    private static final Logger LOG = LoggerFactory.getLogger(AccountHandler.class);

    @Override
    public void handle(Context exchange)
    {
        try { exchange.byMethod(methodSpec -> methodSpec
                .get(() -> renderObject(exchange, managerForAccount(exchange).getAccount()))
                .post(() -> requestObject(exchange, Account.class).then(account -> 
                                renderObject(exchange, checkSetToPrimary(managerForHolder(
                                    exchange, account.holder.id)
                                        .create(account), exchange))))
        ); } catch (Exception ex) { throw new RuntimeException(ex); }
    }
    
    protected AccountManager managerForAccount(Context exchange) {
        return new ActionContextBasicLookup(exchange).accountManagerFor(Account.class);
    }

    protected AccountManager managerForHolder(Context exchange, Integer holderId) {
        return new ActionContextBasicLookup(exchange).accountManagerFor(new EntityId().id(holderId).entityType(AccountHolder.class));
    }

    protected Account checkSetToPrimary(AccountManager manager, Context exchange) {
        Optional.ofNullable(exchange.getRequest().getQueryParams().get("primary"))
            .map(qsv -> new Boolean(qsv))
            .ifPresent(primary -> { if (primary) manager.setAsPrimaryAccount(); });
        return manager.getAccount();
    }
}
