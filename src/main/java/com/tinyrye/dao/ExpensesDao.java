package com.tinyrye.dao;

import javax.sql.DataSource;

public class ExpensesDao
{
    private final DataSource datasource;

    public ExpensesDao(DataSource datasource) {
        this.datasource = datasource;
    }
}