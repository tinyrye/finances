package com.tinyrye.model;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class AccountSummary implements Serializable
{
    public Account account;
    public Double balance;
    public List<Expense> recentExpenses = new ArrayList<Expense>();
    public OffsetDateTime lastTransactionAt;

    public AccountSummary account(Account account) { this.account = account; return this; }
    public AccountSummary balance(Double balance) { this.balance = balance; return this; }
    public AccountSummary recentExpenses(List<Expense> recentExpenses) { this.recentExpenses = recentExpenses; return this; }
    public AccountSummary lastTransactionAt(OffsetDateTime lastTransactionAt) { this.lastTransactionAt = lastTransactionAt; return this; }
}