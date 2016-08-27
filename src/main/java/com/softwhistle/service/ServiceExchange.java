package com.softwhistle.service;

public interface ServiceExchange
{
    <T> T get(Class<T> serviceClass);
}
