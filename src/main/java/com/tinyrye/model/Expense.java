package com.tinyrye.model;

import java.io.Serializable;
import java.time.OffsetDateTime;

public class Expense implements Serializable
{
    public Integer id;
    public Double amount;
    public Boolean posted;
    public OffsetDateTime transactedOn;
    public String budgetCode;
    public String merchant;

    public Expense id(Integer id) { this.id = id; return this; }
    public Expense amount(Double amount) { this.amount = amount; return this; }
    public Expense posted(Boolean posted) { this.posted = posted; return this; }
    public Expense transactedOn(OffsetDateTime transactedOn) { this.transactedOn = transactedOn; return this; }
    public Expense budgetCode(String budgetCode) { this.budgetCode = budgetCode; return this; }
    public Expense merchant(String merchant) { this.merchant = merchant; return this; }
}