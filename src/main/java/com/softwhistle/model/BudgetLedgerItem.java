package com.softwhistle.model;

import java.time.OffsetDateTime;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import com.softwhistle.serialization.DateTimeJsonDeserializer;
import com.softwhistle.serialization.DateTimeJsonSerializer;

public class BudgetLedgerItem
{
	public BudgetItem item;
	@JsonDeserialize(using=DateTimeJsonDeserializer.class)
    @JsonSerialize(using=DateTimeJsonSerializer.class)
    public OffsetDateTime transactsOn;
    public Double ledgerTotal;

	public BudgetLedgerItem item(BudgetItem item) { this.item = item; return this; }
	public BudgetLedgerItem transactsOn(OffsetDateTime transactsOn) { this.transactsOn = transactsOn; return this; }
}