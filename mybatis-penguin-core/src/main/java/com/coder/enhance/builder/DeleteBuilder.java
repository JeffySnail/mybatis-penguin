package com.coder.enhance.builder;

import com.coder.enhance.mybatis.EntityPortray;
import com.coder.enhance.mybatis.MapperUtils;
import org.apache.ibatis.type.TypeHandler;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collection;
import java.util.Map;

/**
 * @author jeffy
 * @date 2019/1/22
 */
public class DeleteBuilder implements SqlBuilder {

    @Override
    public String buildSql(Class<?> mapper, Method method, EntityPortray entityPortray) {
        StringBuilder builder = new StringBuilder();
        Map<String, String> columnMap = entityPortray.getColumnMap();
        Map<String, Class<? extends TypeHandler>> typeHandlers = entityPortray.getColumnTypeHandlers();
        Map<String, Parameter> parameterMap = MapperUtils.getMethodParameters(method);
        builder.append("<script>delete from ")
                .append(entityPortray.getName())
                .append("<where>");
        for (Map.Entry<String, Parameter> entry : parameterMap.entrySet()) {
            String param = entry.getKey();
            Class<?> type = entry.getValue().getType();
            String columnKey = param;
            if (param.equals(entityPortray.getPrimaryToken())) {
                columnKey = entityPortray.getPrimaryProperty();
                type = entityPortray.getPrimaryType();
            }

            if (Collection.class.isAssignableFrom(type)) {
                builder.append(" and ")
                        .append(entityPortray.getColumn(columnKey))
                        .append(" in ")
                        .append("<foreach collection=\"")
                        .append(param)
                        .append("\" item=\"item\" index=\"index\" open=\"(\" separator=\",\" close=\")\" >")
                        .append("#{item}")
                        .append("</foreach>");
            } else {
                builder.append(" and ")
                        .append(columnMap.get(columnKey))
                        .append("=")
                        .append(MapperUtils.buildTypeValue(param, type, "", typeHandlers.get(columnKey)));
            }
        }
        builder.append("</where></script>");
        return builder.toString();
    }
}
