package com.coder.enhance.mybatis;

import com.code.enhance.PageModel;
import com.coder.enhance.BaseMapper;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.reflection.TypeParameterResolver;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;

import javax.persistence.Table;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;

/**
 * @author jeffy
 * @date 2019/1/22
 **/
public class MapperUtils {

    private static Map<Class<?>, Map<Class<?>, EntityPortray>> portrayMap = new HashMap();

    public static EntityPortray getEntityPortray(Class<?> mapper, Class<?> type) {
        Map<Class<?>, EntityPortray> entities = portrayMap.get(mapper);
        if (entities == null) {
            entities = new HashMap();
            portrayMap.put(mapper, entities);
        }
        EntityPortray entityPortray = entities.get(type);
        if (entityPortray == null) {
            entityPortray = new EntityPortray(type);
            entities.put(type, entityPortray);
        }
        return entityPortray;
    }

    public static void destroyPortray(Class<?> mapper) {
        portrayMap.remove(mapper);
    }

    public static Map<String, Class<?>> getParameters(Method method) {
        Map<String, Class<?>> parameterMap = new LinkedHashMap();
        Class<?>[] parameterTypes = method.getParameterTypes();
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameterTypes.length; ++i) {
            Parameter parameter = parameters[i];
            Param param = parameter.getAnnotation(Param.class);
            String key = parameterTypes[i].getSimpleName();
            if (param != null) {
                key = param.value();
            }
            parameterMap.put(key, parameterTypes[i]);
        }
        return parameterMap;
    }

    public static <A extends Annotation> List<A> getMethodAnnotations(Method method, Class<A> annoType) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        List<A> annotations = new ArrayList();
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameterTypes.length; ++i) {
            Parameter parameter = parameters[i];
            A annotation = parameter.getAnnotation(annoType);
            if (annotation != null) {
                annotations.add(annotation);
            }
        }
        return annotations;
    }

    public static Map<String, Parameter> getMethodParameters(Method method) {
        Map<String, Parameter> parameterMap = new LinkedHashMap();
        Class<?>[] parameterTypes = method.getParameterTypes();
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameterTypes.length; ++i) {
            Parameter parameter = parameters[i];
            Param param = parameter.getAnnotation(Param.class);
            String key = parameterTypes[i].getSimpleName();
            if (param != null) {
                key = param.value();
            }
            parameterMap.put(key, parameter);
        }
        return parameterMap;
    }

    public static Class<?> getReturnType(Method method, Class<?> type) {
        Class<?> returnType = method.getReturnType();
        Type resolvedReturnType = TypeParameterResolver.resolveReturnType(method, type);
        if (resolvedReturnType instanceof Class) {
            returnType = (Class<?>) resolvedReturnType;
            if (void.class.equals(returnType)) {
                ResultType rt = method.getAnnotation(ResultType.class);
                if (rt != null) {
                    returnType = rt.value();
                }
            }
        } else if (resolvedReturnType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) resolvedReturnType;
            Class<?> rawType = (Class<?>) parameterizedType.getRawType();
            if (Collection.class.isAssignableFrom(rawType) || PageModel.class.isAssignableFrom(rawType)) {
                Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                if (actualTypeArguments != null && actualTypeArguments.length == 1) {
                    Type returnTypeParameter = actualTypeArguments[0];
                    if (returnTypeParameter instanceof Class<?>) {
                        returnType = (Class<?>) returnTypeParameter;
                    } else if (returnTypeParameter instanceof ParameterizedType) {
                        returnType = (Class<?>) ((ParameterizedType) returnTypeParameter).getRawType();
                    } else if (returnTypeParameter instanceof GenericArrayType) {
                        Class<?> componentType = (Class<?>) ((GenericArrayType) returnTypeParameter).getGenericComponentType();
                        returnType = Array.newInstance(componentType, 0).getClass();
                    }
                }
            } else if (method.isAnnotationPresent(MapKey.class) && Map.class.isAssignableFrom(rawType)) {
                Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                if (actualTypeArguments != null && actualTypeArguments.length == 2) {
                    Type returnTypeParameter = actualTypeArguments[1];
                    if (returnTypeParameter instanceof Class<?>) {
                        returnType = (Class<?>) returnTypeParameter;
                    } else if (returnTypeParameter instanceof ParameterizedType) {
                        returnType = (Class<?>) ((ParameterizedType) returnTypeParameter).getRawType();
                    }
                }
            }
        }

        return returnType;
    }

    public static SqlSource buildSqlSourceFromStrings(Configuration configuration, String[] strings, Class<?> parameterTypeClass, LanguageDriver languageDriver) {
        final StringBuilder sql = new StringBuilder();
        for (String fragment : strings) {
            sql.append(fragment);
            sql.append(" ");
        }
        return languageDriver.createSqlSource(configuration, sql.toString().trim(), parameterTypeClass);
    }

    public static Class<?> getParameterType(Method method) {
        Class<?> parameterType = null;
        Class<?>[] parameterTypes = method.getParameterTypes();
        for (Class<?> type : parameterTypes) {
            if (!ResultHandler.class.isAssignableFrom(type)) {
                if (parameterType == null) {
                    parameterType = type;
                } else {
                    parameterType = MapperMethod.ParamMap.class;
                }
            }
        }
        return parameterType;
    }

    public static Class<?> getEntityType(Class<?> mapper) {
        Type[] types = mapper.getGenericInterfaces();
        for (Type type : types) {
            if (type instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) type;
                Class rawType = (Class) parameterizedType.getRawType();
                if (BaseMapper.class.isAssignableFrom(rawType)) {
                    Class entityType = (Class) parameterizedType.getActualTypeArguments()[0];
                    if (entityType.isAnnotationPresent(Table.class)) {
                        return entityType;
                    }
                }
            }
        }
        return null;
    }

    public static String buildTypeValue(String property, Class<?> type, String prefix, Class<?> handler) {
        StringBuilder builder = new StringBuilder();
        builder.append("#{")
                .append(prefix)
                .append(property)
                .append(",javaType=")
                .append(type.getCanonicalName());
        if (handler != null) {
            builder.append(",typeHandler=")
                    .append(handler.getCanonicalName());
        }
        builder.append("}");
        return builder.toString();
    }
}
