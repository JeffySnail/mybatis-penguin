package com.coder.enhance.select;

import com.code.enhance.PageModel;
import com.coder.enhance.AbstractCRUDTest;
import com.coder.enhance.BlogUser;
import com.coder.enhance.BlogUserMapper;
import com.coder.enhance.intercepter.PagerInterceptor;
import com.coder.enhance.plugin.Pager;
import org.apache.ibatis.session.RowBounds;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * @author jeffy
 * @date 2019/1/24
 **/

public class SelectTest extends AbstractCRUDTest {


    @Before
    public void before() {
        addMapper(BlogUserMapper.class);
        addInterceptor(new PagerInterceptor());
    }

    @Test
    public void loadOneTest() {
        BlogUserMapper blogUserMapper = getSqlSession().getMapper(BlogUserMapper.class);
        BlogUser blogUser = blogUserMapper.loadOne(25);
        System.err.println(blogUser.toString());
    }


    @Test
    public void findByIdsTest() {
        BlogUserMapper blogUserMapper = getSqlSession().getMapper(BlogUserMapper.class);
        List<BlogUser> list = blogUserMapper.findByIds(Arrays.asList(18, 24));
        Optional.ofNullable(list).ifPresent(e -> e.forEach(System.err::println));

    }

    @Test
    public void findAllTest() {
        BlogUserMapper blogUserMapper = getSqlSession().getMapper(BlogUserMapper.class);
        List<BlogUser> list = blogUserMapper.findAll();
        Optional.ofNullable(list).ifPresent(e -> e.forEach(System.err::println));
    }

    @Test
    public void findByPageTest() {
        BlogUserMapper blogUserMapper = getSqlSession().getMapper(BlogUserMapper.class);
        Pager pager = new Pager(1, 3);
        PageModel pageModel = blogUserMapper.findByPage(pager);
        Optional.ofNullable(pageModel).ifPresent(e -> {
            System.err.println("pageCount = " + e.getPageCount());
            System.err.println("currentPage = " + e.getPage());
            System.err.println("pageSize = " + e.getPageSize());
            System.err.println("totalCount = " + e.getTotalCount());
            pageModel.getRecords().forEach(System.err::println);
        });
    }


    @Test
    public void findLimitTest() {
        BlogUserMapper blogUserMapper = getSqlSession().getMapper(BlogUserMapper.class);
        RowBounds rowBounds = new RowBounds(2, 5);

        List<BlogUser> list = blogUserMapper.findLimit(rowBounds);
        Optional.ofNullable(list).ifPresent(e -> e.forEach(System.err::println));
    }

    @After
    public void after() {
        closeSession();
    }
}
