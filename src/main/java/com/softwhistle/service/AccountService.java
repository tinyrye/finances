package com.softwhistle.service;

import javax.sql.DataSource;

import com.softwhistle.dao.AccountsDao;
import com.softwhistle.model.Account;
import com.softwhistle.model.AccountHolder;
import com.softwhistle.model.AccountSummary;
import com.softwhistle.model.EntityAlreadyExistsException;
import com.softwhistle.model.EntityCreation;
import com.softwhistle.model.EntityId;

public class AccountService
{
    private final ServiceExchange serviceExchange;
    
    public AccountService(ServiceExchange serviceExchange) {
        this.serviceExchange = serviceExchange;
    }
    
    public EntityCreation create(AccountHolder user)
    {
        if (serviceExchange.get(AccountsDao .class).holderExistsByEmail(user.email)) {
            throw new EntityAlreadyExistsException("email", user.email, serviceExchange.get(AccountsDao.class).getHolderByEmail(user.email).id);
        }
        else {
            serviceExchange.get(AccountsDao.class).insert(user);
            return new EntityCreation().id(user.id).successful(true)
                        .message("Account holder was added to the system.");
        }
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
