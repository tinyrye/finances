package com.tinyrye.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Budget implements Serializable
{
    public Integer id;
    public AccountHolder holder;
    public String name;
    public Boolean active;
    public List<BudgetItem> items = new ArrayList<BudgetItem>();
    
    public Budget id(Integer id) { this.id = id; return this; }
    public Budget holder(AccountHolder holder) { this.holder = holder; return this; }
    public Budget name(String name) { this.name = name; return this; }
    public Budget active(Boolean active) { this.active = active; return this; }
    public Budget items(List<BudgetItem> items) { this.items = items; return this; }
}