package com.tinyrye.dao;

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

import com.tinyrye.jdbc.DaoStatementLocator;
import com.tinyrye.jdbc.InsertResult;
import com.tinyrye.jdbc.RowConverter;
import com.tinyrye.jdbc.SQLInsert;
import com.tinyrye.jdbc.SQLQuery;
import com.tinyrye.jdbc.SQLUpdate;

import com.tinyrye.model.Account;
import com.tinyrye.model.AccountHolder;

public class AccountsDao
{
    private final SQLInsert holderInsert;
    private final SQLInsert accountInsert;
    
    private final SQLQuery holderByIdSelect;
    private final SQLQuery byIdSelect;
    private final SQLQuery byHolderIdSelect;
    
    private final RowConverter<AccountHolder> baseAccountHolderRowConverter = (resultSet, valueUtility) ->
        new AccountHolder()
            .id(valueUtility.convert(resultSet, Integer.class, "id"))
            .firstName(valueUtility.convert(resultSet, String.class, "firstName"))
            .intermediateNames((List<String>) valueUtility.convert(resultSet, List.class, "intermediateNames"))
            .lastName(valueUtility.convert(resultSet, String.class, "lastName"))
            .email(valueUtility.convert(resultSet, String.class, "email"));
    
    private final RowConverter<Account> baseAccountRowConverter = new AccountRowConverter();
    
    public AccountsDao(DataSource datasource)
    {
        holderInsert = new SQLInsert(datasource)
            .sql("INSERT INTO account_holder (first_name, intermediate_names, last_name, email)\n" +
                 "VALUES (?, ?, ?, ?)");
        
        accountInsert = new SQLInsert(datasource)
            .sql("INSERT INTO account (account_holder_id, institution_name, institution_account_id, institution_account_name, established_by_institution_at)\n" +
                 "VALUES (?, ?, ?, ?, ?)");
        
        holderByIdSelect = new SQLQuery(datasource)
            .sql(new DaoStatementLocator(getClass(), "holderByIdSelect"));
        
        byIdSelect = new SQLQuery(datasource)
            .sql(new DaoStatementLocator(getClass(), "byIdSelect"));
        
        byHolderIdSelect = new SQLQuery(datasource)
            .sql(new DaoStatementLocator(getClass(), "byHolderIdSelect"));
    }
    
    public void insert(AccountHolder holder)
    {
        holderInsert.callForFirstGeneratedKey(() ->
                Arrays.asList(holder.firstName,
                    holder.intermediateNames != null ? Joiner.on(",").join(holder.intermediateNames) : null,
                    holder.lastName, holder.email),
                holder, AccountHolder::id);
    }
    
    public void insert(Account account)
    {
        accountInsert.callForFirstGeneratedKey(() ->
                Arrays.asList((account.holder != null ? account.holder.id : null), account.institutionName, account.institutionAccountId,
                    account.institutionAccountName, account.establishedByInstitutionAt),
                account, Account::id);
    }
    
    public AccountHolder getHolderById(Integer id) {
        return holderByIdSelect.call(() -> Arrays.asList(id)).first(baseAccountHolderRowConverter)
                    .orElse(null);
    }
    
    public Account getById(Integer id) {
        return byIdSelect.call(() -> Arrays.asList(id)).first(baseAccountRowConverter)
                    .orElse(null);
    }
    
    public Account getActiveByHolderId(Integer id) {
        return byHolderIdSelect.call(() -> Arrays.asList(id)).first(baseAccountRowConverter)
                    .orElse(null);
    }
}