package com.coder.enhance.builder;

import com.coder.enhance.mybatis.EntityPortray;

import java.lang.reflect.Method;

/**
 * @author jeffy
 * @date 2019/1/22
 */
public interface SqlBuilder {
    
    /**
     * 根据方法生成sql
     *
     * @param mapper
     * @param method
     * @param entityPortray
     * @return
     */
    String buildSql(Class<?> mapper, Method method, EntityPortray entityPortray);
}
