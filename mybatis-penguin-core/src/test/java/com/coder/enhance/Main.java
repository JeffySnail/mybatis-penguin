package com.coder.enhance;

import com.code.enhance.PageModel;
import com.coder.enhance.intercepter.PagerInterceptor;
import com.coder.enhance.mybatis.PenguinConfiguration;
import com.coder.enhance.plugin.Pager;
import org.apache.ibatis.datasource.pooled.PooledDataSourceFactory;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author jeffy
 * @date 2019/1/22
 **/
public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);


    public static void main(String[] args) {
        SqlSession sqlSession = null;
        try {
            sqlSession = getSqlSessionWithOutXMl();
            BlogUserMapper mapper = sqlSession.getMapper(BlogUserMapper.class);
//            mapper.findByUserId(12);
//            BlogUser blogUser = new BlogUser();
//            blogUser.setUserAddress("address");
//            blogUser.setUserName("jeffy");
//            blogUser.setUserNation("nation");
//            blogUser.setUserSex(1);
//            mapper.insertOne(blogUser);

//            BlogUser blog = mapper.findByUserId(25);
//            System.err.println(blog.toString());
            Pager pager = new Pager(2, 3);

            BlogUser blogUser = new BlogUser();
            PageModel<BlogUser> pageModel = mapper.findByPage(pager);
            System.err.println(pageModel.getPage());
            System.err.println(pageModel.getPageSize());
            System.err.println(pageModel.getSortField());
            System.err.println(pageModel.getTotalCount());
            System.err.println(pageModel.getPageCount());
            pageModel.getRecords().forEach(System.err::println);
//            List<BlogUser> users = mapper.findAll();
//            users.forEach(System.out::print);
            System.err.println("");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            sqlSession.commit();

            sqlSession.close();
        }
    }

    private static SqlSession getSqlSessionWithOutXMl() {
        Properties properties = new Properties();
        properties.setProperty("driver", "com.mysql.jdbc.Driver");
        properties.setProperty("url", "jdbc:mysql://127.0.0.1:3306/mybatis");
        properties.setProperty("username", "root");
        properties.setProperty("password", "123456");
        PooledDataSourceFactory pooledDataSourceFactory = new PooledDataSourceFactory();
        pooledDataSourceFactory.setProperties(properties);
        javax.sql.DataSource dataSource = pooledDataSourceFactory.getDataSource();
        TransactionFactory transactionFactory = new JdbcTransactionFactory();
        Environment environment = new Environment("development", transactionFactory, dataSource);
        Configuration configuration = new PenguinConfiguration(environment);
//        configuration.addMapper(BlogUserDAO.class);
        configuration.addMapper(BlogUserMapper.class);
        configuration.addInterceptor(new PagerInterceptor());
//        configuration.addInterceptor(new ResultHandlerInterceptor());
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
        return sqlSessionFactory.openSession(true);
    }

    private static SqlSession getSqlSessionWithXML() throws IOException {
        InputStream inputStream = Resources.getResourceAsStream("configuration.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream, "development");
        SqlSession sqlSession = sqlSessionFactory.openSession();
        return sqlSession;
    }
}
