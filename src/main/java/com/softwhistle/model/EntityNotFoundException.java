package com.softwhistle.model;

public class EntityNotFoundException extends RuntimeException
{
    public EntityId request;
    public Class targetEntity; // a requested entity is sometimes
        // not the direct entity but rather a parent of an entity
        // that the request seeks eg. a budget of a holder.

    public EntityNotFoundException(EntityId request) {
        super(String.format("Target does not exist under %s", request));
        this.request = request;
    }

    public EntityNotFoundException(EntityId request, Class targetEntity) {
        super(String.format("Target %s does not exist under %s", targetEntity, request));
        this.request = request;
        this.targetEntity = targetEntity;
    }
}
