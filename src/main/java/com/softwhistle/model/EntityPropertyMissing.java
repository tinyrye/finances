package com.softwhistle.model;

public class EntityPropertyMissing extends RuntimeException {
	public EntityPropertyMissing(EntityId entity, String propertyName) {
		super(String.format("Required property, %s, is missing in entity: %s", propertyName, entity));
	}
}