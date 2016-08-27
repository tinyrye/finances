package com.softwhistle.util;

import static com.softwhistle.util.JsonExchangeHelper.renderObject;

import java.util.function.Consumer;

import ratpack.handling.Context;
import ratpack.http.Request;
import ratpack.http.Response;

import com.softwhistle.model.BadParameterInRequest;

@FunctionalInterface
public interface ParameterTransform<T>
{
    /**
     * Emit to consumer a valid transform of request value.
     * @return Error message if request value is invalid; null if value is valid
     */
    String parseTo(String requestValue, Consumer<T> receiver);
    
    public default boolean hasQueryParam(Request request, String name) {
        return request.getQueryParams().containsKey(name);
    }
    
    public default String queryParam(Request request, String name) {
        return request.getQueryParams().get(name);
    }
    
    public default void emitRequiredParameterResponse(Context exchange, String name) {
        exchange.getResponse().status(400);
        renderObject(exchange, new BadParameterInRequest().parameterName(name)
            .addProblem("required"));
    }
    
    public default void emitInvalidParameterResponse(Context exchange, String name, String problem) {
        exchange.getResponse().status(400);
        renderObject(exchange, new BadParameterInRequest().parameterName(name)
            .addProblem(problem));
    }
    
    public default boolean queryParamTo(Context exchange, String name, boolean required, Consumer<T> transformReceiver)
    {
        if (! hasQueryParam(exchange.getRequest(), name)) {
            if (required) emitRequiredParameterResponse(exchange, name);
            return required;
        }
        else
        {
            String failure = parseTo(queryParam(exchange.getRequest(), name), transformReceiver);
            if (failure != null) {
                emitInvalidParameterResponse(exchange, name, failure);
                return false;
            }
            else {
                return true;
            }
        }
    }
}
