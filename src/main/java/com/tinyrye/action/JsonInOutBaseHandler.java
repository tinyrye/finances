package com.tinyrye.action;

import ratpack.exec.Promise;
import ratpack.func.Function;
import ratpack.handling.Context;
import ratpack.handling.Handler;
import static ratpack.jackson.Jackson.fromJson;
import static ratpack.jackson.Jackson.json;

/**
 * This class remains abstract: to use it you use the support methods to
 * reduce the request object extraction and response object serialization
 * code work.
 * 
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
public abstract class JsonInOutBaseHandler implements Handler
{
    protected void contentType(Context exchange) {
        exchange.getResponse().contentType("application/json");
    }
    
    protected <T> Promise<T> requestObject(Context exchange, Class<T> requestObjectClass) {
        return exchange.parse(fromJson(requestObjectClass));
    }

    protected void renderObject(Context exchange, Object responseObject) {
        contentType(exchange);
        exchange.render(json(responseObject));
    }
}