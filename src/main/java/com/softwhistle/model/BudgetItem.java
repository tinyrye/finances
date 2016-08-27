package com.softwhistle.model;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class BudgetItem implements Serializable
{
    public Integer id;
    public List<String> categorization = new ArrayList<String>();
    public Double amount;
    public String description;
    public OccurrenceSchedule transactsOn;
    public Account holderAccount;
    public Account merchantAccount;
    public String merchant;
    
    public BudgetItem id(Integer id) { this.id = id; return this; }
    public BudgetItem categorization(List<String> categorization) { this.categorization = categorization; return this; }
    public BudgetItem amount(Double amount) { this.amount = amount; return this; }
    public BudgetItem description(String description) { this.description = description; return this; }
    public BudgetItem transactsOn(OccurrenceSchedule transactsOn) { this.transactsOn = transactsOn; return this; }
    public BudgetItem holderAccount(Account holderAccount) { this.holderAccount = holderAccount; return this; }
    public BudgetItem merchantAccount(Account merchantAccount) { this.merchantAccount = merchantAccount; return this; }
    public BudgetItem merchant(String merchant) { this.merchant = merchant; return this; }
}
