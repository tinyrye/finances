package com.tinyrye.model;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class AccountSummary implements Serializable
{
    public Account account;
    public Double balance;
    public List<AccountEntry> recentTransactions = new ArrayList<AccountEntry>();
    
    public AccountSummary account(Account account) { this.account = account; return this; }
    public AccountSummary balance(Double balance) { this.balance = balance; return this; }
    public AccountSummary recentTransactions(List<AccountEntry> recentTransactions) { this.recentTransactions = recentTransactions; return this; }
}