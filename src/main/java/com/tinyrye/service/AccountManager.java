package com.tinyrye.service;

import java.util.NoSuchElementException;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinyrye.dao.AccountsDao;
import com.tinyrye.model.Account;
import com.tinyrye.model.AccountHolder;
import com.tinyrye.model.AccountSummary;
import com.tinyrye.model.EntityId;

public class AccountManager
{
    private static final Logger LOG = LoggerFactory.getLogger(AccountManager.class);

    private final ServiceExchange serviceExchange;
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
            }
            else if (accountReferrer instanceof AccountHolder) {
                account = ((AccountHolder) accountReferrer).primaryAccount;
                account.holder = (AccountHolder) accountReferrer;
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
        return new HolderBudgetManager(serviceExchange, account.holder);
    }
    
    public Account getAccount() {
        return account;
    }

    public AccountHolder getHolder() {
        return account.holder;
    }

    public AccountSummary summary() {
        return new AccountSummary().account(account)
                    .balance(balance());
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
            loadActiveForHolder(idPackager.id);
        }
        else {
            throw new UnsupportedOperationException("Cannot infer relation between input id and the account to load.");
        }
    }
    
    protected void loadById(Integer id) {
        LOG.debug("Loading account by id: {}", id);
        account = serviceExchange.get(AccountsDao.class).getById(id);
        if (account == null) throw new NoSuchElementException("No account exists by id.");
    }
    
    protected void loadActiveForHolder(Integer id) {
        LOG.debug("Loading account by holderId: {}", id);
        account = serviceExchange.get(AccountsDao.class).getActiveByHolderId(id);
        if (account == null) throw new NoSuchElementException("No account exists for holder.");
    }
}