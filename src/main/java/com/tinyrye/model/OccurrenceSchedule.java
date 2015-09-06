package com.tinyrye.model;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Iterator;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public interface OccurrenceSchedule extends Serializable
{
    Integer id();

    /**
     * Iterate all occurrences that start on or after <code>referenceStart</code>.
     */
    Iterator<OffsetDateTime> occurrences(OffsetDateTime referenceStart);
}