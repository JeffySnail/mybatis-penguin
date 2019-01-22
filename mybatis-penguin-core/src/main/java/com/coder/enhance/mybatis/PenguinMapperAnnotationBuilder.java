package com.coder.enhance.mybatis;

import com.coder.enhance.builder.*;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.binding.BindingException;
import org.apache.ibatis.builder.BuilderException;
import org.apache.ibatis.builder.IncompleteElementException;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.builder.annotation.ProviderSqlSource;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.ResultSetType;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.mapping.StatementType;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.session.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author jeffy
 * @date 2019/1/22
 **/
public class PenguinMapperAnnotationBuilder {
    private final Set<Class<? extends Annotation>> sqlAnnotationTypes = new HashSet<>();
    private final Set<Class<? extends Annotation>> sqlProviderAnnotationTypes = new HashSet<>();
    private final Map<SqlCommandType, SqlBuilder> commandBuilders = new HashMap<>();
    private final Map<String, Class<? extends Annotation>> methodPrefixTypes = new HashMap<>();

    private Configuration configuration;
    private MapperBuilderAssistant assistant;
    private Class<?> type;
    private EntityPortray entityPortray;

    public PenguinMapperAnnotationBuilder(Configuration configuration, Class<?> type) {
        String resource = type.getName().replace('.', '/') + ".java (best guess)";
        this.assistant = new MapperBuilderAssistant(configuration, resource);
        this.configuration = configuration;
        this.type = type;

        sqlAnnotationTypes.add(Select.class);
        sqlAnnotationTypes.add(Insert.class);
        sqlAnnotationTypes.add(Update.class);
        sqlAnnotationTypes.add(Delete.class);

        sqlProviderAnnotationTypes.add(SelectProvider.class);
        sqlProviderAnnotationTypes.add(InsertProvider.class);
        sqlProviderAnnotationTypes.add(UpdateProvider.class);
        sqlProviderAnnotationTypes.add(DeleteProvider.class);

        commandBuilders.put(SqlCommandType.SELECT, new SelectBuilder());
        commandBuilders.put(SqlCommandType.INSERT, new InsertBuilder());
        commandBuilders.put(SqlCommandType.UPDATE, new UpdateBuilder());
        commandBuilders.put(SqlCommandType.DELETE, new DeleteBuilder());

        methodPrefixTypes.put("select", Select.class);
        methodPrefixTypes.put("find", Select.class);
        methodPrefixTypes.put("load", Select.class);
        methodPrefixTypes.put("insert", Insert.class);
        methodPrefixTypes.put("update", Update.class);
        methodPrefixTypes.put("delete", Delete.class);
        methodPrefixTypes.put("remove", Delete.class);
    }

    public void parse() {
        String resource = type.toString();
        if (!configuration.isResourceLoaded(resource)) {
            this.entityPortray = MapperUtils.getEntityPortray(type, MapperUtils.getEntityType(type));
            loadXmlResource();
            configuration.addLoadedResource(resource);
            assistant.setCurrentNamespace(type.getName());
            parseCache();
            parseCacheRef();
            Method[] methods = type.getMethods();
            for (Method method : methods) {
                try {
                    if (!method.isBridge()) {
                        parseStatement(method);
                    }
                } catch (IncompleteElementException e) {
                    // ignore
                }
            }
            MapperUtils.destroyPortray(type);
        }
    }

    private void parseStatement(Method method) {
        Class<?> parameterTypeClass = MapperUtils.getParameterType(method);
        LanguageDriver languageDriver = getLanguageDriver(method);
        // build sql
        SqlSource sqlSource = getSqlSourceFromAnnotations(method, parameterTypeClass, languageDriver);
        if (sqlSource != null) {
            final String mappedStatementId = type.getName() + "." + method.getName();
            Options options = method.getAnnotation(Options.class);
            Integer fetchSize = null;
            Integer timeout = null;
            StatementType statementType = StatementType.PREPARED;
            ResultSetType resultSetType = ResultSetType.FORWARD_ONLY;
            SqlCommandType sqlCommandType = getSqlCommandType(method);
            boolean isSelect = sqlCommandType == SqlCommandType.SELECT;
            boolean flushCache = !isSelect;
            boolean useCache = isSelect;
            // build select key

            KeyBuilder keyBuilder = new KeyBuilder(configuration, assistant);
            keyBuilder.buildKey(sqlCommandType, method, mappedStatementId, entityPortray, options, languageDriver);

            if (options != null) {
                if (Options.FlushCachePolicy.TRUE.equals(options.flushCache())) {
                    flushCache = true;
                } else if (Options.FlushCachePolicy.FALSE.equals(options.flushCache())) {
                    flushCache = false;
                }
                useCache = options.useCache();
                fetchSize = options.fetchSize() > -1 || options.fetchSize() == Integer.MIN_VALUE ? options.fetchSize() : null; //issue #348
                timeout = options.timeout() > -1 ? options.timeout() : null;
                statementType = options.statementType();
                resultSetType = options.resultSetType();
            }
            // build result map
            String resultMapId = new ResultBuilder()
                    .buildResultMap(configuration, assistant, type, method, isSelect);
            // config
            assistant.addMappedStatement(
                    mappedStatementId,
                    sqlSource,
                    statementType,
                    sqlCommandType,
                    fetchSize,
                    timeout,
                    // ParameterMapID
                    null,
                    parameterTypeClass,
                    resultMapId,
                    MapperUtils.getReturnType(method, type),
                    resultSetType,
                    flushCache,
                    useCache,
                    false,
                    keyBuilder.getKeyGenerator(),
                    keyBuilder.getKeyProperty(),
                    keyBuilder.getKeyColumn(),
                    // DatabaseID
                    null,
                    languageDriver,
                    // ResultSets
                    options != null ? nullOrEmpty(options.resultSets()) : null);
        }
    }

    private SqlCommandType getSqlCommandType(Method method) {
        Class<? extends Annotation> type = getSqlAnnotationType(method);
        if (type == null) {
            type = getSqlProviderAnnotationType(method);
            if (type == null) {
                String name = method.getName();
                for (Map.Entry<String, Class<? extends Annotation>> entry : methodPrefixTypes.entrySet()) {
                    if (name.startsWith(entry.getKey())) {
                        type = entry.getValue();
                        break;
                    }
                }
                if (type == null) {
                    return SqlCommandType.UNKNOWN;
                }
            } else {
                if (type == SelectProvider.class) {
                    type = Select.class;
                } else if (type == InsertProvider.class) {
                    type = Insert.class;
                } else if (type == UpdateProvider.class) {
                    type = Update.class;
                } else if (type == DeleteProvider.class) {
                    type = Delete.class;
                }
            }
        }
        return SqlCommandType.valueOf(type.getSimpleName().toUpperCase(Locale.ENGLISH));
    }

    private Class<? extends Annotation> getSqlAnnotationType(Method method) {
        return chooseAnnotationType(method, sqlAnnotationTypes);
    }

    private Class<? extends Annotation> getSqlProviderAnnotationType(Method method) {
        return chooseAnnotationType(method, sqlProviderAnnotationTypes);
    }

    private Class<? extends Annotation> chooseAnnotationType(Method method, Set<Class<? extends Annotation>> types) {
        for (Class<? extends Annotation> type : types) {
            Annotation annotation = method.getAnnotation(type);
            if (annotation != null) {
                return type;
            }
        }
        return null;
    }

    private LanguageDriver getLanguageDriver(Method method) {
        Lang lang = method.getAnnotation(Lang.class);
        Class<?> langClass = null;
        if (lang != null) {
            langClass = lang.value();
        }
        return assistant.getLanguageDriver(langClass);
    }

    private SqlSource getSqlSourceFromAnnotations(Method method, Class<?> parameterType, LanguageDriver languageDriver) {
        try {
            Class<? extends Annotation> sqlAnnotationType = getSqlAnnotationType(method);
            Class<? extends Annotation> sqlProviderAnnotationType = getSqlProviderAnnotationType(method);
            if (sqlAnnotationType != null) {
                if (sqlProviderAnnotationType != null) {
                    throw new BindingException("You cannot supply both a static SQL and SqlProvider to method named " + method.getName());
                }
                Annotation sqlAnnotation = method.getAnnotation(sqlAnnotationType);
                final String[] strings = (String[]) sqlAnnotation.getClass().getMethod("value").invoke(sqlAnnotation);
                return MapperUtils.buildSqlSourceFromStrings(configuration, strings, parameterType, languageDriver);
            } else if (sqlProviderAnnotationType != null) {
                Annotation sqlProviderAnnotation = method.getAnnotation(sqlProviderAnnotationType);
                return new ProviderSqlSource(assistant.getConfiguration(), sqlProviderAnnotation);
            } else {
                final String mappedStatementId = type.getName() + "." + method.getName();
                if (configuration.hasStatement(mappedStatementId)) {
                    return null;
                }
                return MapperUtils.buildSqlSourceFromStrings(configuration, new String[]{buildSqlFromMethodName(method)}, parameterType, languageDriver);
            }
        } catch (Exception e) {
            throw new BuilderException("Could not find value method on SQL annotation.  Cause: " + e, e);
        }
    }

    private String buildSqlFromMethodName(Method method) {
        SqlCommandType commandType = getSqlCommandType(method);
        SqlBuilder commandBuilder = commandBuilders.get(commandType);
        if (commandBuilder != null) {
            return commandBuilder.buildSql(type, method, entityPortray);
        }
        return null;
    }

    private void loadXmlResource() {
        if (!configuration.isResourceLoaded("namespace:" + type.getName())) {
            String xmlResource = type.getName().replace('.', '/') + ".xml";
            InputStream inputStream = null;
            try {
                inputStream = Resources.getResourceAsStream(type.getClassLoader(), xmlResource);
            } catch (IOException e) {
                // ignore, resource is not required
            }
            if (inputStream != null) {
                XMLMapperBuilder xmlParser = new XMLMapperBuilder(inputStream, assistant.getConfiguration(), xmlResource, configuration.getSqlFragments(), type.getName());
                xmlParser.parse();
            }
        }
    }

    private void parseCache() {
        CacheNamespace cacheDomain = type.getAnnotation(CacheNamespace.class);
        if (cacheDomain != null) {
            Integer size = cacheDomain.size() == 0 ? null : cacheDomain.size();
            Long flushInterval = cacheDomain.flushInterval() == 0 ? null : cacheDomain.flushInterval();
            assistant.useNewCache(cacheDomain.implementation(), cacheDomain.eviction(), flushInterval, size, cacheDomain.readWrite(), cacheDomain.blocking(), null);
        }
    }

    private void parseCacheRef() {
        CacheNamespaceRef cacheDomainRef = type.getAnnotation(CacheNamespaceRef.class);
        if (cacheDomainRef != null) {
            assistant.useCacheRef(cacheDomainRef.value().getName());
        }
    }

    private String nullOrEmpty(String value) {
        return value == null || value.trim().length() == 0 ? null : value;
    }
}