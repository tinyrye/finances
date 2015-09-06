package com.tinyrye.model;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class BudgetItem implements Serializable
{
    public Integer id;
    public Budget budget;
    public BudgetItemType type;
    public List<String> categorization = new ArrayList<String>();
    public Double amount;
    public String description;
    public OccurrenceSchedule transactsOn;
    public Account intendedAccount;
    public String merchant;
    
    public BudgetItem id(Integer id) { this.id = id; return this; }
    public BudgetItem budget(Budget budget) { this.budget = budget; return this; }
    public BudgetItem newBudget() { return budget(new Budget()); }
    public BudgetItem type(BudgetItemType type) { this.type = type; return this; }
    public BudgetItem categorization(List<String> categorization) { this.categorization = categorization; return this; }
    public BudgetItem amount(Double amount) { this.amount = amount; return this; }
    public BudgetItem description(String description) { this.description = description; return this; }
    public BudgetItem transactsOn(OccurrenceSchedule transactsOn) { this.transactsOn = transactsOn; return this; }
    public BudgetItem intendedAccount(Account intendedAccount) { this.intendedAccount = intendedAccount; return this; }
    public BudgetItem merchant(String merchant) { this.merchant = merchant; return this; }
}