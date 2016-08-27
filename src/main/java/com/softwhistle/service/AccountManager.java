package com.softwhistle.service;

import java.util.NoSuchElementException;
import java.util.function.Supplier;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.softwhistle.dao.AccountsDao;
import com.softwhistle.model.Account;
import com.softwhistle.model.AccountHolder;
import com.softwhistle.model.AccountSummary;
import com.softwhistle.model.EntityId;
import com.softwhistle.model.EntityNotFoundException;
import com.softwhistle.util.Loader;

public class AccountManager
{
    private static final Logger LOG = LoggerFactory.getLogger(AccountManager.class);

    private final ServiceExchange serviceExchange;
    private Supplier<AccountHolder> holder;
    private Account account;

    protected AccountManager(ServiceExchange serviceExchange) {
        this.serviceExchange = serviceExchange;
    }
    
    public AccountManager(ServiceExchange serviceExchange, Object accountReferrer)
    {
        this(serviceExchange);
        if (accountReferrer != null)
        {
            if (accountReferrer instanceof Account) {
                account = (Account) accountReferrer;
                holder = () -> account.holder;
            }
            else if (accountReferrer instanceof AccountHolder) {
                holder = () -> (AccountHolder) accountReferrer;
                account = holder.get().primaryAccount;
            }
            else if (accountReferrer instanceof EntityId) {
                loadById((EntityId) accountReferrer);
            }
            else {
                throw new UnsupportedOperationException("Not respecting account reference.");
            }
        }
    }

    public HolderBudgetManager managerForHolderBudget() {
        return new HolderBudgetManager(serviceExchange, holder.get());
    }
    
    public Account getAccount() {
        return account;
    }

    public AccountHolder getHolder() {
        return holder.get();
    }

    public AccountManager setAsPrimaryAccount() {
        serviceExchange.get(AccountsDao.class).setAsPrimaryAccountFor(holder.get().id, account.id);
        return this;
    }

    public AccountManager create(Account account) {
        serviceExchange.get(AccountsDao.class).insert(account);
        this.account = serviceExchange.get(AccountsDao.class).getById(account.id);
        return this;
    }

    public AccountManager loadPrimaryAccount() {
        account = serviceExchange.get(AccountsDao.class).getPrimaryByHolderId(holder.get().id);
        return this;
    }

    public AccountManager loadAccount(Integer id)
    {
        Account account = serviceExchange.get(AccountsDao.class).getById(id);
        if (account.holder.id != holder.get().id) {
            throw new RuntimeException("Manager can only use account that belongs to holder.");
        }
        return this;
    }

    public AccountManager loadAccountWithExternalId(String externalId) {
        // SOON
        throw new UnsupportedOperationException();
    }
    
    public AccountSummary summary() {
        return new AccountSummary().account(account).balance(balance());
    }
    
    public Double balance() {
        return new Double(0.0D);
    }
    
    protected void loadById(EntityId idPackager)
    {
        if (idPackager.entityType == null || idPackager.entityType.equals(Account.class)) {
            loadById(idPackager.id);
        }
        else if (idPackager.entityType.equals(AccountHolder.class)) {
            loadHolderById(idPackager.id);
        }
        else {
            throw new UnsupportedOperationException("Cannot infer relation between input id and the account to load.");
        }
    }

    protected AccountManager loadById(Integer id) {
        account = serviceExchange.get(AccountsDao.class).getById(id);
        holder = () -> account.holder;
        return this;
    }

    protected AccountManager loadHolderById(Integer id) {
        holder = Loader.of(() -> serviceExchange.get(AccountsDao.class).getHolderById(id));
        if (holder.get() == null) {
            throw new EntityNotFoundException(new EntityId().id(id).entityType(AccountHolder.class));
        }
        else {
            LOG.info("Manager set up for holder: {}", new Object[] { holder.get() });
            return this;
        }
    }
}
