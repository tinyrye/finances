package com.softwhistle.service;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.softwhistle.dao.AccountsDao;
import com.softwhistle.model.Account;
import com.softwhistle.model.AccountHolder;
import com.softwhistle.model.AccountSummary;
import com.softwhistle.model.EntityAlreadyExistsException;
import com.softwhistle.model.EntityNotFoundException;
import com.softwhistle.model.EntityCreation;
import com.softwhistle.model.EntityId;

public class AccountService
{
    private static final Logger LOG = LoggerFactory.getLogger(AccountService.class);

    private final ServiceExchange serviceExchange;
    
    public AccountService(ServiceExchange serviceExchange) {
        this.serviceExchange = serviceExchange;
    }
    
    public EntityCreation create(AccountHolder user)
    {
        if (serviceExchange.get(AccountsDao.class).holderExistsByEmail(user.email)) {
            throw new EntityAlreadyExistsException("email", user.email, serviceExchange.get(AccountsDao.class).getHolderByEmail(user.email).id);
        }
        else {
            serviceExchange.get(AccountsDao.class).insert(user);
            return new EntityCreation().id(user.id).successful(true)
                        .message("Account holder was added to the system.");
        }
    }
    
    public Account getById(Integer id) {
        return serviceExchange.get(AccountsDao.class).getById(id);
    }

    public AccountHolder getHolderById(Integer id) {
        return serviceExchange.get(AccountsDao.class).getHolderById(id);
    }

    public AccountHolder search(AccountHolder holder) {
        return serviceExchange.get(AccountsDao.class).getHolderByEmail(holder.email);
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
