package com.tinyrye.model;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class AccountEntry implements Serializable
{
    public Integer id;
    public Account account;
    public Double amount;
    public Boolean posted;
    public OffsetDateTime transactedOn;
    public BudgetItem budgetItem;
    
    public AccountEntry id(Integer id) { this.id = id; return this; }
    public AccountEntry account(Account account) { this.account = account; return this; }
    public AccountEntry amount(Double amount) { this.amount = amount; return this; }
    public AccountEntry posted(Boolean posted) { this.posted = posted; return this; }
    public AccountEntry transactedOn(OffsetDateTime transactedOn) { this.transactedOn = transactedOn; return this; }
    public AccountEntry budgetItem(BudgetItem budgetItem) { this.budgetItem = budgetItem; return this; }
}