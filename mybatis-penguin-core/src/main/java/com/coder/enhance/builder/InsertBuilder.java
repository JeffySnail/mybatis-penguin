package com.coder.enhance.builder;

import com.coder.enhance.mybatis.EntityPortray;
import com.coder.enhance.mybatis.MapperUtils;
import org.apache.ibatis.type.TypeHandler;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

/**
 * @author jeffy
 * @date 2019/1/22
 */
public class InsertBuilder implements SqlBuilder {

    @Override
    public String buildSql(Class<?> mapper, Method method, EntityPortray entityPortray) {
        StringBuilder builder = new StringBuilder();
        Map<String, Class<?>> typeMap = entityPortray.getColumnTypeMap();
        Map<String, String> columnMap = entityPortray.getColumnMap();
        Map<String, Class<? extends TypeHandler>> typeHandlers = entityPortray.getColumnTypeHandlers();
        Map<String, Class<?>> parameterMap = MapperUtils.getParameters(method);
        builder.append("<script>insert into ")
            .append(entityPortray.getName())
            .append("(")
            .append(SelectBuilder.Joiner.on(",").join(columnMap.values()))
            .append(") values ");
        Map.Entry<String, Class<?>> parameter = parameterMap.entrySet().iterator().next();
        if (parameter == null) {
            throw new RuntimeException("Insert needs parameter");
        }
        if (Collection.class.isAssignableFrom(parameter.getValue())) {
            builder.append("<foreach collection=\"collection\" item=\"item\" index=\"index\" separator=\",\">")
                .append("(");
            int index = 0;
            for (Map.Entry<String, Class<?>> entry : typeMap.entrySet()) {
                if (entityPortray.getUpdateTimeColumn().equalsIgnoreCase(entry.getKey())
                    || entityPortray.getAddTimeColumn().equalsIgnoreCase(entry.getKey())) {
                    builder.append("Now()");
                } else {
                    builder.append(MapperUtils.buildTypeValue(entry.getKey(), entry.getValue(), "item.",
                        typeHandlers.get(entry.getKey())));
                }
                if (index < typeMap.size() - 1) {
                    builder.append(",");
                }
                ++index;
            }
            builder.append(")</foreach>");
        } else {
            int index = 0;
            builder.append("(");
            for (Map.Entry<String, Class<?>> entry : typeMap.entrySet()) {
                if (entityPortray.getUpdateTimeColumn().equalsIgnoreCase(entry.getKey())
                    || entityPortray.getAddTimeColumn().equalsIgnoreCase(entry.getKey())) {
                    builder.append("Now()");
                } else {
                    builder.append(MapperUtils.buildTypeValue(entry.getKey(), entry.getValue(), "",
                        typeHandlers.get(entry.getKey())));
                }
                if (index < typeMap.size() - 1) {
                    builder.append(",");
                }
                ++index;
            }
            builder.append(")");
        }
        builder.append("</script>");
        return builder.toString();
    }
}
