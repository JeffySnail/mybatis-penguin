package com.coder.enhance.builder;

import com.coder.enhance.annotation.UpdateBy;
import com.coder.enhance.exception.MybatisSqlResourceException;
import com.coder.enhance.mybatis.EntityPortray;
import com.coder.enhance.mybatis.MapperUtils;
import org.apache.ibatis.type.TypeHandler;

import java.lang.reflect.Method;
import java.util.*;

/**
 * @author jeffy
 * @date 2019/1/22
 */
public class UpdateBuilder implements SqlBuilder {

    @Override
    public String buildSql(Class<?> mapper, Method method, EntityPortray entityPortray) {
        StringBuilder builder = new StringBuilder();
        Map<String, Class<?>> parameterMap = MapperUtils.getParameters(method);
        Map.Entry<String, Class<?>> parameter = parameterMap.entrySet().iterator().next();
        List<UpdateBy> updateByList = MapperUtils.getMethodAnnotations(method, UpdateBy.class);
        if (updateByList.size() > 1) {
            throw new MybatisSqlResourceException(entityPortray.getEntityClass().getName() + "." + method.getName() + " has error parameters");
        }
        List<String> updateKeys;
        if (updateByList.size() == 1) {
            updateKeys = Arrays.asList(updateByList.get(0).value());
        } else {
            updateKeys = Collections.singletonList(entityPortray.getPrimaryProperty());
        }
        doUpdate(method, builder, parameter, updateKeys, entityPortray);
        return builder.toString();
    }

    private void doUpdate(Method method, StringBuilder builder, Map.Entry<String, Class<?>> parameter, List<String> updateKeys, EntityPortray entityPortray) {
        Map<String, Class<?>> typeMap = entityPortray.getColumnTypeMap();
        Map<String, String> columnMap = entityPortray.getColumnMap();
        Map<String, Class<? extends TypeHandler>> typeHandlers = entityPortray.getColumnTypeHandlers();
        if (Collection.class.isAssignableFrom(parameter.getValue())) {
            boolean needCheck = "updateSelectiveAll".equals(method.getName());
            builder.append("<script><foreach collection=\"collection\" item=\"item\" index=\"index\" open=\"\" close=\"\" separator=\";\">update ")
                .append(entityPortray.getName())
                .append("<set>");
            for (Map.Entry<String, String> entry : columnMap.entrySet()) {
                String param = entry.getKey();
                if (param.equals(entityPortray.getPrimaryToken())) {
                    param = entityPortray.getPrimaryProperty();
                }
                if (entityPortray.getPrimaryProperty().equals(param)
                    || entityPortray.getAddTimeColumn().equalsIgnoreCase(entry.getKey())) {
                    continue;
                }
                // hard code for update time.
                if (entityPortray.getUpdateTimeColumn().equalsIgnoreCase(entry.getKey())) {
                    builder.append(entry.getValue()).append("=Now(),");
                } else {
                    builder.append(buildUpdate(needCheck, "item.", entry, typeMap.get(param), typeHandlers.get(param)));
                }
            }
            builder.append("</set> where ");
            for (String updateKey : updateKeys) {
                builder.append(columnMap.get(updateKey))
                    .append("=")
                    .append(MapperUtils.buildTypeValue(updateKey, typeMap.get(updateKey), "item.", typeHandlers.get(updateKey)));
            }
            builder.append("</foreach></script>");
        } else {
            boolean needCheck = "updateSelective".equals(method.getName());
            builder.append("<script>update ")
                .append(entityPortray.getName())
                .append("<set>");
            for (Map.Entry<String, String> entry : columnMap.entrySet()) {
                String param = entry.getKey();
                if (param.equals(entityPortray.getPrimaryToken())) {
                    param = entityPortray.getPrimaryProperty();
                }
                // ignore primary key and add time
                if (entityPortray.getPrimaryProperty().equals(param)
                    || entityPortray.getAddTimeColumn().equalsIgnoreCase(entry.getKey())) {
                    continue;
                }
                // hard code for update time.
                if (entityPortray.getUpdateTimeColumn().equalsIgnoreCase(entry.getKey())) {
                    builder.append(entry.getValue()).append("=Now(),");
                } else {
                    builder.append(buildUpdate(needCheck, "", entry, typeMap.get(param), typeHandlers.get(param)));
                }
            }
            builder.append("</set> where ");
            for (String updateKey : updateKeys) {
                builder.append(columnMap.get(updateKey))
                    .append("=")
                    .append(MapperUtils.buildTypeValue(updateKey, typeMap.get(updateKey), "", typeHandlers.get(updateKey)));
            }
            builder.append("</script>");
        }
    }

    private String buildUpdate(boolean needCheck, String prefix, Map.Entry<String, String> entry, Class<?> type, Class<? extends TypeHandler> typeHandler) {
        StringBuilder builder = new StringBuilder();
        if (needCheck) {
            builder.append("<if test=\"").append(prefix).append(entry.getKey()).append(" != null\">");
        }
        builder.append(entry.getValue())
            .append("=")
            .append(MapperUtils.buildTypeValue(prefix + entry.getKey(), type, "", typeHandler))
            .append(",");
        if (needCheck) {
            builder.append("</if>");
        }
        return builder.toString();
    }
}
