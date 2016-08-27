package com.softwhistle.model;

import java.io.Serializable;

public class EntityAlreadyExistsException extends RuntimeException
{
    public final String criteriaName;
    public final Serializable criteriaValue;
    public final Integer entityId;

    public EntityAlreadyExistsException(String criteriaName, Serializable criteriaValue, Integer entityId) {
        this.criteriaName = criteriaName;
        this.criteriaValue = criteriaValue;
        this.entityId = entityId;
    }
}
