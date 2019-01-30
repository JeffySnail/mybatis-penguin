package com.coder.enhance.mybatis;

import com.coder.enhance.exception.MybatisSqlResourceException;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.type.TypeHandler;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.beans.Transient;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * @author jeffy
 * @date 2019/1/22
 **/
public class EntityPortray {
    private static final Log LOGGER = LogFactory.getLog(EntityPortray.class);
    private String name;
    private Map<String, String> columnMap = new HashMap<>();
    private Map<String, Class<?>> columnTypeMap = new HashMap<>();
    private Map<String, Class<? extends TypeHandler>> columnTypeHandlers = new HashMap<>();
    private Class<?> entityClass;
    private String primaryProperty;
    private String primaryColumn;
    private Class<?> primaryType;
    private GeneratedValue generatedValue;

    public EntityPortray(Class<?> entity) {
        if (entity.getAnnotation(Table.class) != null) {
            this.entityClass = entity;
        } else {
            this.entityClass = MapperUtils.getEntityType(entity);
        }
        if (entityClass == null) {
            throw new RuntimeException("Entity " + entity.getName() + " can not indicate table name.");
        }
        parse();
    }

    private void parse() {
        Table table = entityClass.getAnnotation(Table.class);
        if (table != null) {
            name = table.name();
        } else {
            name = entityClass.getSimpleName().toLowerCase();
        }
        List<Field> fields = new ArrayList<>();
        Class<?> superClass = entityClass;
        while (!superClass.equals(Object.class)) {
            Field[] superFields = superClass.getDeclaredFields();
            if (superFields != null) {
                Collections.addAll(fields, superFields);
            }
            superClass = superClass.getSuperclass();
        }
        boolean hasKey = false;
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers())
                    || Modifier.isFinal(field.getModifiers())
                    || field.getAnnotation(Transient.class) != null) {
                continue;
            }

            Column column = field.getAnnotation(Column.class);
            if (field.getAnnotation(Id.class) != null) {
                primaryColumn = column.name();
                primaryProperty = field.getName();
                primaryType = field.getType();
                generatedValue = field.getAnnotation(GeneratedValue.class);
                hasKey = true;
            }
            String columnName = field.getName();
            if (column != null) {
                columnName = column.name();
            }
            columnMap.put(field.getName(), columnName);
            columnTypeMap.put(field.getName(), field.getType());
        }
        if (!hasKey) {
            LOGGER.error("Table " + name + "has no primary key, please use @javax.persistence.Id to annotate one field of " + entityClass);
            System.err.println("Table " + name + "has no primary key, please use @javax.persistence.Id to annotate one field of " + entityClass);
            throw MybatisSqlResourceException.newException("Table " + name + "has no primary key, please use @javax.persistence.Id to annotate one field of " + entityClass);

        }
    }

    public String getPrimaryProperty() {
        return primaryProperty;
    }

    public void setPrimaryProperty(String primaryProperty) {
        this.primaryProperty = primaryProperty;
    }

    public String getPrimaryColumn() {
        return primaryColumn;
    }

    public void setPrimaryColumn(String primaryColumn) {
        this.primaryColumn = primaryColumn;
    }

    public Class<?> getPrimaryType() {
        return primaryType;
    }

    public void setPrimaryType(Class<?> primaryType) {
        this.primaryType = primaryType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getColumnMap() {
        return columnMap;
    }

    public void setColumnMap(Map<String, String> columnMap) {
        this.columnMap = columnMap;
    }

    public String getColumn(String property) {
        return columnMap.get(property);
    }

    public Map<String, Class<?>> getColumnTypeMap() {
        return columnTypeMap;
    }

    public void setColumnTypeMap(Map<String, Class<?>> columnTypeMap) {
        this.columnTypeMap = columnTypeMap;
    }

    public Class<?> columnType(String property) {
        return columnTypeMap.get(property);
    }

    public Class<?> getEntityClass() {
        return entityClass;
    }

    public void setEntityClass(Class<?> entityClass) {
        this.entityClass = entityClass;
    }

    public Map<String, Class<? extends TypeHandler>> getColumnTypeHandlers() {
        return columnTypeHandlers;
    }

    public void setColumnTypeHandlers(Map<String, Class<? extends TypeHandler>> columnTypeHandlers) {
        this.columnTypeHandlers = columnTypeHandlers;
    }

    public String getPrimaryToken() {
        return "PrimaryKey";
    }

    public String getUpdateTimeColumn() {
        return "UpdateTime";
    }

    public String getAddTimeColumn() {
        return "AddTime";
    }

    public GeneratedValue getGeneratedValue() {
        return generatedValue;
    }

    public void setGeneratedValue(GeneratedValue generatedValue) {
        this.generatedValue = generatedValue;
    }
}
