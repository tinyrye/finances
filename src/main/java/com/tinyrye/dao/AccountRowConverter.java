package com.tinyrye.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import java.time.OffsetDateTime;

import java.util.List;

import com.tinyrye.jdbc.ResultSetValueConverter;
import com.tinyrye.jdbc.RowConverter;

import com.tinyrye.model.Account;
import com.tinyrye.model.AccountHolder;

public class AccountRowConverter implements RowConverter<Account>
{
    @Override
    public Account convertRow(ResultSet resultSet, ResultSetValueConverter valueUtility)
    {
        return new Account()
            .id(valueUtility.convert(resultSet, Integer.class, "id"))
            .holder(new AccountHolder()
                .id(valueUtility.convert(resultSet, Integer.class, "holder.id"))
                .firstName(valueUtility.convert(resultSet, String.class, "holder.firstName"))
                .intermediateNames((List<String>) valueUtility.convert(resultSet, List.class, "holder.intermediateNames"))
                .lastName(valueUtility.convert(resultSet, String.class, "holder.lastName"))
                .email(valueUtility.convert(resultSet, String.class, "holder.email")))
            .institutionName(valueUtility.convert(resultSet, String.class, "institutionName"))
            .institutionAccountId(valueUtility.convert(resultSet, String.class, "institutionAccountId"))
            .institutionAccountName(valueUtility.convert(resultSet, String.class, "institutionAccountName"))
            .establishedByInstitutionAt(valueUtility.convert(resultSet, OffsetDateTime.class, "establishedByInstitutionAt"));
    }
}