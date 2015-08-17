package com.tinyrye.service;

import com.tinyrye.dao.AccountsDao;
import com.tinyrye.model.Account;
import com.tinyrye.model.AccountHolder;
import com.tinyrye.model.AccountSummary;
import com.tinyrye.model.EntityCreation;

public class AccountManager
{
    private final AccountsDao accountsDao;

    public AccountManager(AccountsDao accountsDao) {
        this.accountsDao = accountsDao;
    }

    public EntityCreation addUser(AccountHolder user) {
        accountsDao.insert(user);
        return new EntityCreation().id(user.id).successful(true)
                    .message("Account holder was added to the system.");
    }

    public EntityCreation create(Account account) {
        accountsDao.insert(account);
        return new EntityCreation().id(account.id).successful(true)
                    .message("Your account was added to the system.");
    }

    public Account getById(Integer id) {
        return accountsDao.getById(id);
    }

    public AccountSummary getSummary(Integer id) {
        return new AccountSummary().account(accountsDao.getById(id));
    }
}