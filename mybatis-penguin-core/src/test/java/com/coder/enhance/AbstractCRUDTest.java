package com.coder.enhance;

import com.coder.enhance.mybatis.PenguinConfiguration;
import org.apache.ibatis.datasource.pooled.PooledDataSourceFactory;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author jeffy
 * @date 2019/1/24
 **/
public abstract class AbstractCRUDTest {

    private static PooledDataSourceFactory pooledDataSourceFactory = null;

    private static TransactionFactory transactionFactory = null;

    private static SqlSessionFactory sqlSessionFactory = null;

    private static javax.sql.DataSource dataSource = null;

    private static Configuration configuration = null;

    private static Environment environment = null;

    private static SqlSession sqlSession;


    static {
        pooledDataSourceFactory = new PooledDataSourceFactory();
        transactionFactory = new JdbcTransactionFactory();
        pooledDataSourceFactory.setProperties(dataSourceProperties());
        dataSource = pooledDataSourceFactory.getDataSource();
        environment = new Environment("development", transactionFactory, dataSource);
        configuration = new PenguinConfiguration(environment);
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
    }

    protected static SqlSession getSqlSession() {
        sqlSession = sqlSessionFactory.openSession(true);
        return sqlSession;
    }

    protected static void addMapper(Class<?> mapper) {
        configuration.addMapper(mapper);
    }

    protected static void addInterceptor(Interceptor interceptor) {
        configuration.addInterceptor(interceptor);
    }

    protected static void closeSession() {
        sqlSession.close();
    }

    private static Properties dataSourceProperties() {
        Properties properties = new Properties();
        properties.setProperty("driver", "com.mysql.jdbc.Driver");
        properties.setProperty("url", "jdbc:mysql://127.0.0.1:3306/mybatis?characterEncoding=utf-8&autoReconnect=true&failOverReadOnly=false&allowMultiQueries=true");
        properties.setProperty("username", "root");
        properties.setProperty("password", "123456");
        return properties;
    }

    protected SqlSession getSqlSessionWithXML() throws IOException {
        InputStream inputStream = Resources.getResourceAsStream("configuration.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream, "development");
        SqlSession sqlSession = sqlSessionFactory.openSession();
        return sqlSession;
    }


}


