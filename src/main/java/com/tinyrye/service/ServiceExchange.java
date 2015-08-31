package com.tinyrye.service;

public interface ServiceExchange
{
    <T> T get(Class<T> serviceClass);
}