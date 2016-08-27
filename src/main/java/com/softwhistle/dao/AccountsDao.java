package com.softwhistle.dao;

import static java.util.Arrays.asList;
import static com.softwhistle.util.Values.optMap;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import javax.sql.DataSource;

import com.google.common.base.Joiner;

import com.softwhistle.jdbc.DaoStatementLocator;
import com.softwhistle.jdbc.InsertResult;
import com.softwhistle.jdbc.RowConverter;
import com.softwhistle.jdbc.SQLInsert;
import com.softwhistle.jdbc.SQLQuery;
import com.softwhistle.jdbc.SQLUpdate;

import com.softwhistle.model.Account;
import com.softwhistle.model.AccountHolder;

public class AccountsDao
{
    private final SQLInsert holderInsert;
    private final SQLInsert accountInsert;
    private final SQLUpdate setAsPrimaryAccountForUpdate;

    private final SQLQuery holderExistsByEmailSelect;
    private final SQLQuery holderByEmailSelect;
    private final SQLQuery holderByIdSelect;
    private final SQLQuery byIdSelect;
    private final SQLQuery byHolderIdSelect;
    
    private final RowConverter<AccountHolder> baseAccountHolderRowConverter = (resultSet, valueUtility) ->
        new AccountHolder()
            .id(valueUtility.convert(resultSet, Integer.class, "id"))
            .firstName(valueUtility.convert(resultSet, String.class, "firstName"))
            .intermediateNames((List<String>) valueUtility.convert(resultSet, List.class, "intermediateNames"))
            .lastName(valueUtility.convert(resultSet, String.class, "lastName"))
            .email(valueUtility.convert(resultSet, String.class, "email"))
            .primaryAccount(valueUtility.convertToOptional(resultSet, Integer.class, "primaryAccount.id")
                .map(id -> new Account().id(id)).orElse(null));
    
    private final RowConverter<Account> baseAccountRowConverter = new AccountRowConverter();
    
    public AccountsDao(DataSource datasource)
    {
        holderInsert = new SQLInsert(datasource)
            .sql("INSERT INTO account_holder (first_name, intermediate_names, last_name, email, primary_account_id)\n" +
                 "VALUES (?, ?, ?, ?, ?)");
        
        accountInsert = new SQLInsert(datasource)
            .sql("INSERT INTO account (account_holder_id, institution_name, institution_account_id, institution_account_name, established_by_institution_at)\n" +
                 "VALUES (?, ?, ?, ?, ?)");
        
        setAsPrimaryAccountForUpdate = new SQLUpdate(datasource)
            .sql(new DaoStatementLocator(getClass(), "setAsPrimaryAccountForUpdate"));

        holderExistsByEmailSelect = new SQLQuery(datasource)
            .sql(new DaoStatementLocator(getClass(), "holderExistsByEmailSelect"));
        
        holderByEmailSelect = new SQLQuery(datasource)
            .sql(new DaoStatementLocator(getClass(), "holderByEmailSelect"));

        holderByIdSelect = new SQLQuery(datasource)
            .sql(new DaoStatementLocator(getClass(), "holderByIdSelect"));
        
        byIdSelect = new SQLQuery(datasource)
            .sql(new DaoStatementLocator(getClass(), "byIdSelect"));
        
        byHolderIdSelect = new SQLQuery(datasource)
            .sql(new DaoStatementLocator(getClass(), "byHolderIdSelect"));
    }
    
    public void insert(AccountHolder holder) {
        holderInsert.call(() -> asList(holder.firstName,
                optMap(holder.intermediateNames, names -> Joiner.on(",").join(names)),
                holder.lastName, holder.email, optMap(holder.primaryAccount, a -> a.id)))
            .firstRowKey(holder, AccountHolder::id);
    }
    
    public void insert(Account account) {
        accountInsert.call(() -> asList(optMap(account.holder, h -> h.id), account.institutionName, account.institutionAccountId,
                account.institutionAccountName, account.establishedByInstitutionAt))
            .firstRowKey(account, Account::id);
    }
    
    public void setAsPrimaryAccountFor(Integer holderId, Integer accountId) {
        setAsPrimaryAccountForUpdate.call(() -> asList(accountId, holderId));
    }

    public boolean holderExistsByEmail(String email) {
        return holderExistsByEmailSelect.call(() -> asList(email))
            .first((rs, valUtil) -> valUtil.convert(rs, Boolean.class, 1))
            .orElse(false);
    }

    public AccountHolder getHolderByEmail(String email) {
        return holderByEmailSelect.call(() -> asList(email)).first(baseAccountHolderRowConverter)
                    .orElse(null);
    }

    public AccountHolder getHolderById(Integer id) {
        return holderByIdSelect.call(() -> asList(id)).first(baseAccountHolderRowConverter)
                    .orElse(null);
    }
    
    public Account getById(Integer id) {
        return byIdSelect.call(() -> asList(id)).first(baseAccountRowConverter).orElse(null);
    }
    
    public Account getPrimaryByHolderId(Integer id) {
        return byHolderIdSelect.call(() -> asList(id)).first(baseAccountRowConverter).orElse(null);
    }
}
