package com.softwhistle.util;

import java.util.List;

import ratpack.exec.Promise;
import ratpack.func.Function;
import ratpack.handling.Context;
import ratpack.handling.Handler;
import static ratpack.jackson.Jackson.fromJson;
import static ratpack.jackson.Jackson.json;
import static ratpack.util.Types.listOf;

/**
 * Typical use case: perform a service/action and produce a response object:
 * <code>
 *   public void handle(Context exchange) {
 *       Object serviceResult = ...; // perform service; this is the result of service
 *       renderObject(exchange, serviceResult);
 *   }
 * </code>
 * 
 * Another typical use case: same as previous but there is a request object from body:
 * <code>
 *   public void handle(Context exchange) {
 *       requestObject(exchange, FooRequestType.class).then(fooObject ->
 *           Object serviceResult = ...; // perform service; this time the service is a function
 *               // of the request object, fooObject.
 *           renderObject(exchange, serviceResult);
 *       );
 *       Object serviceResult = ... perform service; this is the result of service
 *       renderObject(exchange, serviceResult);
 *   }
 * </code>
 */
public class JsonExchangeHelper
{
    public static void contentType(Context exchange) {
        exchange.getResponse().contentType("application/json");
    }
    
    public static <T> Promise<T> requestObject(Context exchange, Class<T> requestObjectClass) {
        return exchange.parse(fromJson(requestObjectClass));
    }
    
    public static <T> Promise<List<T>> requestObjects(Context exchange, Class<T> requestObjectClass) {
        return exchange.parse(fromJson(listOf(requestObjectClass)));
    }

    public static void renderObject(Context exchange, Object responseObject) {
        contentType(exchange);
        exchange.render(json(responseObject));
    }
}
