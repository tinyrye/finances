package com.softwhistle.model;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import com.softwhistle.serialization.DateTimeJsonDeserializer;
import com.softwhistle.serialization.DateTimeJsonSerializer;

public class Budget implements Serializable
{
    public Integer id;
    public String name;
    public Boolean active;
    public AccountHolder holder;

    @JsonDeserialize(using=DateTimeJsonDeserializer.class)
    @JsonSerialize(using=DateTimeJsonSerializer.class)
    public OffsetDateTime startsAt;

    @JsonDeserialize(using=DateTimeJsonDeserializer.class)
    @JsonSerialize(using=DateTimeJsonSerializer.class)
    public OffsetDateTime endsAt;
    
    public List<BudgetItem> items = new ArrayList<BudgetItem>();
    
    public Budget id(Integer id) { this.id = id; return this; }
    public Budget name(String name) { this.name = name; return this; }
    public Budget active(Boolean active) { this.active = active; return this; }
    public Budget holder(AccountHolder holder) { this.holder = holder; return this; }
    public Budget startsAt(OffsetDateTime startsAt) { this.startsAt = startsAt; return this; }
    public Budget endsAt(OffsetDateTime endsAt) { this.endsAt = endsAt; return this; }
    public Budget items(List<BudgetItem> items) { this.items = items; return this; }

    @Override
    public boolean equals(Object that) {
        if (this == that) return true;
        else if (that == null) return false;
        else if (that instanceof Budget) return equals((Budget) that);
        else return false;
    }

    @Override
    public String toString() {
        return new ObjectPrinter().printProperties("Budget", builder -> builder
            .add("id", id).add("name", name).add("active", active)
            .add("holder", holder).add("startsAt", startsAt)
            .add("endsAt", endsAt).add("items", items));
    }
    
    public boolean equals(Budget that) {
        return Objects.equals(this.id, that.id)
            && Objects.equals(this.name, that.name)
            && Objects.equals(this.active, that.active)
            && Objects.equals(this.holder, that.holder)
            && Objects.equals(this.startsAt, that.startsAt)
            && Objects.equals(this.endsAt, that.endsAt)
            && Objects.equals(this.items, that.items);
    }
}
