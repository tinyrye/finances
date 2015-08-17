package com.tinyrye.model;

import java.io.Serializable;

public class EntityId implements Serializable
{
    public final Integer id;
    public EntityId(Integer id) { this.id = id; }
}