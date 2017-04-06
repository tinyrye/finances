package com.softwhistle.model;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Objects;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import com.softwhistle.serialization.DateTimeJsonSerializer;
import com.softwhistle.serialization.DateTimeJsonDeserializer;

public class Account implements Serializable
{
    public Integer id;
    public AccountHolder holder;
    public String holderCode;
    public String institutionName;
    public String institutionAccountId;
    public String institutionAccountName;
    
    @JsonSerialize(using=DateTimeJsonSerializer.class)
    @JsonDeserialize(using=DateTimeJsonDeserializer.class)
    public OffsetDateTime establishedByInstitutionAt;

    public Account id(Integer id) { this.id = id; return this; }
    public Account holder(AccountHolder holder) { this.holder = holder; return this; }
    public Account holderCode(String holderCode) { this.holderCode = holderCode; return this; }
    public Account institutionName(String institutionName) { this.institutionName = institutionName; return this; }
    public Account institutionAccountId(String institutionAccountId) { this.institutionAccountId = institutionAccountId; return this; }
    public Account institutionAccountName(String institutionAccountName) { this.institutionAccountName = institutionAccountName; return this; }
    public Account establishedByInstitutionAt(OffsetDateTime establishedByInstitutionAt) { this.establishedByInstitutionAt = establishedByInstitutionAt; return this; }

    @Override
    public boolean equals(Object that) {
        if (this == that) return true;
        else if (that == null) return false;
        else if (Account.class.isAssignableFrom(that.getClass())) return equals((Account) that);
        else return false;
    }

    @Override
    public String toString() {
        return String.format("holder=%d; institutionName=%s; institutionAccountId=%s; establishedByInstitutionAt=%s", holder != null ? holder.id : null,
            institutionName, institutionAccountId, establishedByInstitutionAt);
    }

    public boolean equals(Account that) {
        return Objects.equals(this.id, that.id)
            && Objects.equals(this.holder, that.holder)
            && Objects.equals(this.holderCode, that.holderCode)
            && Objects.equals(this.institutionName, that.institutionName)
            && Objects.equals(this.institutionAccountId, that.institutionAccountId)
            && Objects.equals(this.institutionAccountName, that.institutionAccountName);
    }
}
