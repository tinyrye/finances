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
import com.tinyrye.jdbc.ResultSetValueConverter;
import com.tinyrye.jdbc.SQLInsert;
import com.tinyrye.jdbc.SQLQuery;
import com.tinyrye.jdbc.SQLUpdate;

import com.tinyrye.model.Account;
import com.tinyrye.model.AccountHolder;

public class AccountsDao
{
    private final SQLInsert holderInsert;
    private final SQLInsert accountInsert;
    private final SQLQuery<Account> byIdSelect;

    public AccountsDao(DataSource datasource)
    {
        DaoStatementLocator statementLocator = new DaoStatementLocator(getClass());
        final ResultSetValueConverter resultSetConverter = new ResultSetValueConverter();

        holderInsert = new SQLInsert(datasource)
            .sql("INSERT INTO account_holder (first_name, intermediate_names, last_name, email)\n" +
                 "VALUES (?, ?, ?, ?)");

        accountInsert = new SQLInsert(datasource)
            .sql("INSERT INTO account (account_holder_id, institution_name, institution_account_id, institution_account_name, established_by_institution_at)\n" +
                 "VALUES (?, ?, ?, ?, ?)");

        byIdSelect = new SQLQuery<Account>(datasource)
            .rowConverter(resultSet ->
                new Account()
                    .id((Integer) resultSet.getInt("id"))
                    .holder(new AccountHolder()
                        .id(resultSet.getInt("holder.id"))
                        .firstName(resultSet.getString("holder.firstName"))
                        .intermediateNames((List<String>) resultSetConverter.convert(resultSet, List.class, "holder.intermediateNames"))
                        .lastName(resultSet.getString("holder.lastName"))
                        .email(resultSet.getString("holder.email")))
                    .institutionName(resultSet.getString("institutionName"))
                    .institutionAccountId(resultSet.getString("institutionAccountId"))
                    .institutionAccountName(resultSet.getString("institutionAccountName"))
                    .establishedByInstitutionAt(resultSetConverter.convert(
                        resultSet, OffsetDateTime.class, "establishedByInstitutionAt"))
            )
            .sql(statementLocator.get("byIdSelect"));
    }
    
    public void insert(AccountHolder holder)
    {
        holderInsert.call(() ->
                Arrays.asList(holder.firstName,
                    holder.intermediateNames != null ? Joiner.on(",").join(holder.intermediateNames) : null,
                    holder.lastName, holder.email))
            .generatedKeys.stream().forEachOrdered(rowGeneratedKey ->
                holder.id = rowGeneratedKey.get(0)
            );
    }
    
    public void insert(Account account)
    {
        if (account.holder.id == null) {
            insert(account.holder);
        }
        accountInsert.call(() ->
                Arrays.asList(account.holder.id, account.institutionName, account.institutionAccountId,
                    account.institutionAccountName, account.establishedByInstitutionAt))
            .generatedKeys.stream().forEachOrdered(rowGeneratedKey ->
                account.id = rowGeneratedKey.get(0)
            );
    }
    
    public Account getById(Integer id) {
        System.out.println(String.format("Going with SQL: %s", new DaoStatementLocator(getClass()).get("byIdSelect")));
        return byIdSelect.callForFirst(() -> Arrays.asList(id));
    }
}