package com.coder.enhance.builder;


import com.code.enhance.PageModel;
import com.coder.enhance.annotation.OrderBy;
import com.coder.enhance.mybatis.EntityPortray;
import com.coder.enhance.mybatis.MapperUtils;
import com.coder.enhance.plugin.Pager;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.TypeHandler;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collection;
import java.util.Map;

/**
 * @author jeffy
 * @date 2019/1/22
 */
public class SelectBuilder implements SqlBuilder {

    @Override
    public String buildSql(Class<?> mapper, Method method, EntityPortray entityPortray) {
        StringBuilder builder = new StringBuilder();
        Class<?> returnRawType = MapperUtils.getReturnType(method, mapper);
        EntityPortray returnPortray = MapperUtils.getEntityPortray(mapper, returnRawType);
        Map<String, String> columnMap = entityPortray.getColumnMap();
        Map<String, Class<? extends TypeHandler>> typeHandlers = entityPortray.getColumnTypeHandlers();
        Map<String, Parameter> parameterMap = MapperUtils.getMethodParameters(method);
        builder.append("<script>select ")
                .append(Joiner.on(",").join(returnPortray.getColumnMap().values()))
                .append(" from ")
                .append(entityPortray.getName())
                .append("<where>");
        for (Map.Entry<String, Parameter> entry : parameterMap.entrySet()) {
            String param = entry.getKey();
            Class<?> paramType = entry.getValue().getType();
            boolean primaryToken = param.equals(entityPortray.getPrimaryToken());
            String columnKey = param;
            Class<?> columnType = paramType;
            if (primaryToken) {
                columnKey = entityPortray.getPrimaryProperty();
                columnType = entityPortray.getPrimaryType();
            }

            if (Pager.class.isAssignableFrom(paramType)
                    || RowBounds.class.isAssignableFrom(paramType)) {
                continue;
            }
            if (Collection.class.isAssignableFrom(paramType)) {
                builder.append(" and ")
                        .append(columnMap.get(columnKey))
                        .append(" in ")
                        .append("<foreach item=\"item\" index=\"index\" collection=\"")
                        .append(param)
                        .append("\" open=\"(\" separator=\",\" close=\")\">")
                        .append("#{item}</foreach>");
            } else {
                builder.append(" and ")
                        .append(columnMap.get(columnKey))
                        .append("=")
                        .append(MapperUtils.buildTypeValue(param, columnType, "", typeHandlers.get(columnKey)));
            }
        }
        builder.append("</where>");
        OrderBy orderBy = method.getAnnotation(OrderBy.class);
        if (orderBy != null && null != orderBy.value() && "".equals(orderBy.value())) {
            builder.append(orderBy.value());
        }

        Class<?> returnType = method.getReturnType();
        if (!Collection.class.isAssignableFrom(returnType) && !returnType.isArray()
                && !PageModel.class.isAssignableFrom(returnType)) {
            builder.append(" limit 1");
        }
        builder.append("</script>");
        return builder.toString();
    }


    static class Joiner {

        private final String separator;
        private StringBuilder stringBuilder;

        public Joiner(String separator) {
            this.separator = separator;
        }

        public static Joiner on(String separator) {
            return new Joiner(separator);
        }

        public String join(Collection collection) {
            if (null == stringBuilder) {
                stringBuilder = new StringBuilder();
            }
            for (Object o : collection) {
                stringBuilder.append(separator).append(String.valueOf(o));
            }
            return stringBuilder.deleteCharAt(0).toString();
        }
    }
}
