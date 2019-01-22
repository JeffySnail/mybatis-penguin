package com.coder.enhance.mybatis;

import org.apache.ibatis.binding.MapperProxy;
import org.apache.ibatis.binding.MapperProxyFactory;
import org.apache.ibatis.session.SqlSession;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author jeffy
 * @date 2019/1/22
 **/
public class PenguinMapperProxyFactory<T> extends MapperProxyFactory<T> {
    private final Map<Method, PenguinMapperMethod> methodCache = new ConcurrentHashMap();

    public PenguinMapperProxyFactory(Class<T> mapperInterface) {
        super(mapperInterface);
    }

    @Override
    protected T newInstance(MapperProxy<T> mapperProxy) {
        return null;
    }

    @SuppressWarnings("unchecked")
    private T newInstance(PenguinMapperProxy<T> mapperProxy) {
        return (T) Proxy.newProxyInstance(getMapperInterface().getClassLoader(), new Class[]{getMapperInterface()}, mapperProxy);
    }

    @Override
    public T newInstance(SqlSession sqlSession) {
        final PenguinMapperProxy<T> mapperProxy = new PenguinMapperProxy<T>(sqlSession, this.getMapperInterface(),
                this.methodCache);
        return newInstance(mapperProxy);
    }
}
