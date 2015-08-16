package com.tinyrye.model;

import java.util.List;

public class AccountHolder
{
    public Integer id;
    public String firstName;
    public List<String> intermediateNames;
    public String lastName;
    public String email;

    public AccountHolder id(Integer id) { this.id = id; return this; }
    public AccountHolder firstName(String firstName) { this.firstName = firstName; return this; }
    public AccountHolder intermediateNames(List<String> intermediateNames) { this.intermediateNames = intermediateNames; return this; }
    public AccountHolder lastName(String lastName) { this.lastName = lastName; return this; }
    public AccountHolder email(String email) { this.email = email; return this; }
}