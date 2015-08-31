package com.tinyrye.service;

import javax.sql.DataSource;

import com.tinyrye.dao.AccountsDao;
import com.tinyrye.model.Account;
import com.tinyrye.model.AccountHolder;
import com.tinyrye.model.AccountSummary;
import com.tinyrye.model.EntityCreation;
import com.tinyrye.model.EntityId;

public class AccountService
{
    private final ServiceExchange serviceExchange;
    
    public AccountService(ServiceExchange serviceExchange) {
        this.serviceExchange = serviceExchange;
    }
    
    public EntityCreation create(AccountHolder user) {
        serviceExchange.get(AccountsDao.class).insert(user);
        return new EntityCreation().id(user.id).successful(true)
                    .message("Account holder was added to the system.");
    }
    
    public EntityCreation create(Account account)
    {
        if ((account.holder != null) && (account.holder.id == null)) {
            serviceExchange.get(AccountsDao.class).insert(account.holder);
        }
        serviceExchange.get(AccountsDao.class).insert(account);
        return new EntityCreation().id(account.id).successful(true)
                    .message("Your account was added to the system.");
    }
    
    public Account getById(Integer id) {
        return serviceExchange.get(AccountsDao.class).getById(id);
    }

    public AccountHolder getHolderById(Integer id) {
        return serviceExchange.get(AccountsDao.class).getHolderById(id);
    }

    public AccountManager managerFor(EntityId idPackager) {
        return new AccountManager(serviceExchange, idPackager);
    }

    public AccountManager managerForAccount(Integer id) {
        return new AccountManager(serviceExchange, EntityId.of(id, Account.class));
    }

    public AccountManager managerForHolder(Integer id) {
        return new AccountManager(serviceExchange, EntityId.of(id, AccountHolder.class));
    }
}