package com.softwhistle.util;

import java.util.Optional;

import ratpack.handling.Context;

public class ContextValues
{
    public static Optional<String> optQueryParam(Context exchange, String name) {
        return Optional.ofNullable(exchange.getRequest().getQueryParams().get(name));
    }
}
