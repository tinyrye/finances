package com.tinyrye.serialization;

import static java.lang.annotation.ElementType.TYPE;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;

@Retention(value=RUNTIME)
@Target(value=TYPE)
public @interface NamedType
{
    public String value();
}