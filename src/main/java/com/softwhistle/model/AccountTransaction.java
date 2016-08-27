package com.softwhistle.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AccountTransaction implements Serializable
{
    public Integer id;
    public List<AccountEntry> entries = new ArrayList<AccountEntry>();
    
    public AccountTransaction id(Integer id) { this.id = id; return this; }
    public AccountTransaction entries(List<AccountEntry> entries) { this.entries = entries; return this; }
}
