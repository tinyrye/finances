package com.softwhistle.util;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public class Values
{
    public static boolean isNotBlank(String value) {
        return value != null && ! value.trim().isEmpty();
    }
    
    public static boolean isNotBlank(Supplier<String> value) {
        return isNotBlank(value.get());
    }

    public static Optional<String> notBlankOpt(String value) {
        if (isNotBlank(value)) return Optional.of(value);
        else return Optional.<String>empty();
    }

    /**
     * Short-cut/util macro for frequent call to
     * <code>Optional.ofNullable(obj).map(o -> someDerivOfO).orElse(null)</code>
     */
    public static <T,O> O optMap(T optSource, Function<T,O> mapper) {
        return Optional.ofNullable(optSource).map(mapper).orElse(null);
    }

    /**
     * Short-cut/util macro for frequent call to
     * <code>Optional.ofNullable(obj).map(o -> someDerivOfO).orElse(orElseValue)</code>
     */
    public static <T,O> O optMap(T optSource, Function<T,O> mapper, O orElseValue) {
        return Optional.ofNullable(optSource).map(mapper).orElse(orElseValue);
    }
}
