package com.tinyrye.model;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import com.tinyrye.serialization.DateTimeJsonDeserializer;
import com.tinyrye.serialization.DateTimeJsonSerializer;
import com.tinyrye.serialization.RecurrenceJsonDeserializer;

public class Recurrence implements Serializable
{
    public Integer id;
    
    @JsonSerialize(using=DateTimeJsonSerializer.class)
    @JsonDeserialize(using=DateTimeJsonDeserializer.class)
    public OffsetDateTime startsAt;
    
    @JsonSerialize(using=DateTimeJsonSerializer.class)
    @JsonDeserialize(using=DateTimeJsonDeserializer.class)
    public OffsetDateTime endsAt;

    @JsonDeserialize(using=RecurrenceJsonDeserializer.class)
    public RecurrenceMethod method;
    
    public Recurrence id(Integer id) { this.id = id; return this; }
    public Recurrence startsAt(OffsetDateTime startsAt) { this.startsAt = startsAt; return this; }
    public Recurrence endsAt(OffsetDateTime endsAt) { this.endsAt = endsAt; return this; }
    public Recurrence method(RecurrenceMethod method) { this.method = method; return this; }
}