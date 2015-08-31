package com.tinyrye.model;

import java.io.Serializable;
import java.time.OffsetDateTime;

public interface RecurrenceMethod extends Serializable
{
    OffsetDateTime next(OffsetDateTime current);
}