package com.softwhistle.model;

import java.io.Serializable;

public class EntityCreation implements Serializable
{
    public Integer id;
    public boolean successful;
    public String message;
    
    public EntityCreation id(Integer id) { this.id = id; return this; }
    public EntityCreation successful(boolean successful) { this.successful = successful; return this; }
    public EntityCreation message(String message) { this.message = message; return this; }
}
