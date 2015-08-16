package com.tinyrye.model;

import java.time.Instant;

public class Account
{
    public Integer id;
    public AccountHolder holder;
    public String institutionName;
    public String institutionAccountId;
    public String institutionAccountName;
    public Instant establishedByInstitutionAt;

    public Account id(Integer id) { this.id = id; return this; }
    public Account holder(AccountHolder holder) { this.holder = holder; return this; }
    public Account institutionName(String institutionName) { this.institutionName = institutionName; return this; }
    public Account institutionAccountId(String institutionAccountId) { this.institutionAccountId = institutionAccountId; return this; }
    public Account institutionAccountName(String institutionAccountName) { this.institutionAccountName = institutionAccountName; return this; }
    public Account establishedByInstitutionAt(Instant establishedByInstitutionAt) { this.establishedByInstitutionAt = establishedByInstitutionAt; return this; }
}