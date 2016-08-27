package com.softwhistle.model;

import java.io.Serializable;

public class EntityId<T> implements Serializable
{
    public static <T> EntityId<T> of(Integer id, Class<T> entityType) {
        return new EntityId<T>(id, entityType);
    }
    
    public Integer id;
    public Class<T> entityType;
    
    public EntityId() { }
    public EntityId(Integer id) { this.id = id; entityType = null; }
    public EntityId(Integer id, Class<T> entityType) { this.id = id; this.entityType = entityType; }
    
    public EntityId id(Integer id) { this.id = id; return this; }
    public EntityId entityType(Class<T> entityType) { this.entityType = entityType; return this; }

    @Override
    public String toString() {
        return new ObjectPrinter()
            .printProperties("EntityId", builder -> builder
                .add("id", id).add("entityType", entityType));
    }
}
