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
import com.softwhistle.model.EntityPropertyMissing;
import com.softwhistle.util.Loader;
import com.softwhistle.util.CaseSupplier;

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
        if (account.holder == null) {
            account.holder = holder.get();
        }
        final AccountHolder resolvedHolder;
        if (account.holder.id == null) {
            AccountHolder holderByEmail = serviceExchange.get(AccountsDao.class).getHolderByEmail(account.holder.email);
            if (holderByEmail != null) {
                resolvedHolder = holderByEmail;
            }
            else {
                serviceExchange.get(AccountsDao.class).insert(account.holder);
                resolvedHolder = account.holder;
            }
        }
        else {
            if (serviceExchange.get(AccountsDao.class).getHolderById(account.holder.id) == null) {
                throw new EntityNotFoundException(EntityId.of(account.holder.id, AccountHolder.class));
            }
            resolvedHolder = account.holder;
        }
        account.holder = resolvedHolder;

        // Avoid record duplication if user is specifying some unique values
        final Account existingAccount = new CaseSupplier<Account,Account>().on(a ->
                a.holderCode != null
            ).give(() -> {
                LOG.info("Looking up existing account under holder: holderId={}; holderCode={}", new Object[] {
                    account.holder.id, account.holderCode
                });
                return serviceExchange.get(AccountsDao.class).getByHolderIdAndCode(account.holder.id, account.holderCode);
            }).on(a ->
                a.institutionName != null && a.institutionAccountId != null
            ).give(() -> {
                LOG.info("Looking up existing account under holder: holderId={}; institutionName={}; institutionAccountId={}", new Object[] {
                    account.holder.id, account.institutionName, account.institutionAccountId
                });
                return serviceExchange.get(AccountsDao.class).getByHolderIdAndInstitution(account.holder.id, account.institutionName, account.institutionAccountId);
            }).requireNotNull().apply(account);

        if (existingAccount == null) {
            LOG.info("Did not find account under holder: account={}", account);
            serviceExchange.get(AccountsDao.class).insert(account);
        }
        else {
            account.id = existingAccount.id;
            LOG.info("Did not find account under holder: account={}", account);
            serviceExchange.get(AccountsDao.class).update(account);
        }
        this.account = account;
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
