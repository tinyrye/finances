package com.softwhistle.model;

public class EntityNotFoundException extends RuntimeException
{
    public EntityId request;
    public Class targetEntity; // a requested entity is sometimes
        // not the direct entity but rather a parent of an entity
        // that the request seeks eg. a budget of a holder.

    public EntityNotFoundException(EntityId request) {
        this.request = request;
    }

    public EntityNotFoundException(EntityId request, Class targetEntity) {
        this.request = request;
        this.targetEntity = targetEntity;
    }
}
