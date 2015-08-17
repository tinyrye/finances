package com.tinyrye.model;

import java.io.Serializable;
import java.time.OffsetDateTime;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import com.tinyrye.serialization.DateTimeJsonSerializer;
import com.tinyrye.serialization.DateTimeJsonDeserializer;

public class Account implements Serializable
{
    public Integer id;
    public AccountHolder holder;
    public String institutionName;
    public String institutionAccountId;
    public String institutionAccountName;

    @JsonSerialize(using=DateTimeJsonSerializer.class)
    @JsonDeserialize(using=DateTimeJsonDeserializer.class)
    public OffsetDateTime establishedByInstitutionAt;

    public Account id(Integer id) { this.id = id; return this; }
    public Account holder(AccountHolder holder) { this.holder = holder; return this; }
    public Account institutionName(String institutionName) { this.institutionName = institutionName; return this; }
    public Account institutionAccountId(String institutionAccountId) { this.institutionAccountId = institutionAccountId; return this; }
    public Account institutionAccountName(String institutionAccountName) { this.institutionAccountName = institutionAccountName; return this; }
    public Account establishedByInstitutionAt(OffsetDateTime establishedByInstitutionAt) { this.establishedByInstitutionAt = establishedByInstitutionAt; return this; }
}