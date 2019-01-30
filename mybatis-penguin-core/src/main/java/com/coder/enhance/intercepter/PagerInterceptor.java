package com.coder.enhance.intercepter;

import com.coder.enhance.plugin.Pager;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;
import org.apache.ibatis.session.RowBounds;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @author jeffy
 * @date 2019/1/23
 **/
@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})
public class PagerInterceptor implements Interceptor {
    private static final Log LOGGER = LogFactory.getLog(PagerInterceptor.class);

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        if (invocation.getTarget() instanceof StatementHandler) {
            StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
            MetaObject metaStatementHandler = SystemMetaObject.forObject(statementHandler);

            while (metaStatementHandler.hasGetter("h")) {
                Object object = metaStatementHandler.getValue("h");
                metaStatementHandler = SystemMetaObject.forObject(object);
            }
            while (metaStatementHandler.hasGetter("target")) {
                Object object = metaStatementHandler.getValue("target");
                metaStatementHandler = SystemMetaObject.forObject(object);
            }
            RowBounds rowBounds = (RowBounds) metaStatementHandler.getValue("delegate.rowBounds");
            if (rowBounds instanceof Pager) {
                Pager pager = (Pager) rowBounds;
                MappedStatement mappedStatement = (MappedStatement) metaStatementHandler.getValue("delegate.mappedStatement");
                BoundSql boundSql = (BoundSql) metaStatementHandler.getValue("delegate.boundSql");
                String originSelectSql = boundSql.getSql();
                // 重写sql
                String pageSql = buildPageSql(originSelectSql, pager);
                metaStatementHandler.setValue("delegate.boundSql.sql", pageSql);
                // 采用物理分页后，就不需要mybatis的内存分页了，所以重置下面的两个参数
                metaStatementHandler.setValue("delegate.rowBounds.offset",
                        RowBounds.NO_ROW_OFFSET);
                metaStatementHandler.setValue("delegate.rowBounds.limit", RowBounds.NO_ROW_LIMIT);

                Connection connection = (Connection) invocation.getArgs()[0];
                setPageParameter(originSelectSql, connection, mappedStatement, boundSql, pager);


            }
        }
        return invocation.proceed();
    }

    /**
     * 修改原SQL为分页SQL
     *
     * @param sql
     * @param pager
     * @return
     */
    private String buildPageSql(String sql, Pager pager) {
        StringBuilder pageSql = new StringBuilder(200);
        pageSql.append(sql).append(" limit ")
                .append((pager.getPage() - 1) * pager.getPageSize())
                .append(", ")
                .append(pager.getPageSize());
        return pageSql.toString();
    }

    private void setPageParameter(String sql, Connection connection, MappedStatement mappedStatement,
                                  BoundSql boundSql, Pager page) {
        // 记录总记录数
        String countSql = new StringBuilder("select count(0) from (").append(sql).append(")$_paging_table").toString();
        PreparedStatement countStmt = null;
        ResultSet rs = null;
        try {
            countStmt = connection.prepareStatement(countSql);
            BoundSql countBS = new BoundSql(mappedStatement.getConfiguration(), countSql,
                    boundSql.getParameterMappings(), boundSql.getParameterObject());
            setParameters(countStmt, mappedStatement, countBS, boundSql.getParameterObject());
            rs = countStmt.executeQuery();
            int totalCount = 0;
            if (rs.next()) {
                totalCount = rs.getInt(1);
            }
            page.setTotalCount(totalCount);
            int totalPage = totalCount / page.getPageSize() + ((totalCount % page.getPageSize() == 0) ? 0 : 1);
            page.setTotalPages(totalPage);
        } catch (SQLException e) {
            LOGGER.error("Ignore this exception", e);
        } finally {
            closeResult(rs, countStmt);
        }
    }

    /**
     * 代入参数值
     *
     * @param ps
     * @param mappedStatement
     * @param boundSql
     * @param parameterObject
     * @throws SQLException
     */
    private void setParameters(PreparedStatement ps, MappedStatement mappedStatement, BoundSql boundSql,
                               Object parameterObject) throws SQLException {
        ParameterHandler parameterHandler = new DefaultParameterHandler(mappedStatement, parameterObject, boundSql);
        parameterHandler.setParameters(ps);
    }

    /***
     * close result and prepareStatement
     * @param rs
     * @param countStmt
     * @return
     */
    private boolean closeResult(ResultSet rs, PreparedStatement countStmt) {
        try {
            if (null != rs)
                rs.close();
        } catch (SQLException e) {
            LOGGER.error("Ignore this exception", e);
        }
        try {
            if (null != countStmt)
                countStmt.close();
        } catch (SQLException e) {
            LOGGER.error("Ignore this exception", e);
        }
        return true;
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }
}
