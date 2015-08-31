package com.tinyrye.model;

import java.io.Serializable;
import java.util.List;

public class AccountHolder implements Serializable
{
    public Integer id;
    public Account primaryAccount;
    public String firstName;
    public List<String> intermediateNames;
    public String lastName;
    public String email;

    public AccountHolder id(Integer id) { this.id = id; return this; }
    public AccountHolder primaryAccount(Account primaryAccount) { this.primaryAccount = primaryAccount; return this; }
    public AccountHolder firstName(String firstName) { this.firstName = firstName; return this; }
    public AccountHolder intermediateNames(List<String> intermediateNames) { this.intermediateNames = intermediateNames; return this; }
    public AccountHolder lastName(String lastName) { this.lastName = lastName; return this; }
    public AccountHolder email(String email) { this.email = email; return this; }
}