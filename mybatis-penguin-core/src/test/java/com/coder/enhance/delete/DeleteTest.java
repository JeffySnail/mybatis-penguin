package com.coder.enhance.delete;

import com.coder.enhance.AbstractCRUDTest;
import com.coder.enhance.BlogUserMapper;
import com.coder.enhance.intercepter.PagerInterceptor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jeffy
 * @date 2019/1/24
 **/
public class DeleteTest extends AbstractCRUDTest {
    @Before
    public void before() {
        addMapper(BlogUserMapper.class);
        addInterceptor(new PagerInterceptor());
    }

    @Test
    public void deleteTest() {
        BlogUserMapper mapper = getSqlSession().getMapper(BlogUserMapper.class);
        int affectRows = mapper.deleteOne(36);
        System.err.println("affect rows = " + affectRows);
    }

    @Test
    public void deleteAllTest() {
        BlogUserMapper mapper = getSqlSession().getMapper(BlogUserMapper.class);
        List<Integer> list = new ArrayList(2);
        list.add(38);
        list.add(39);
        int affectRows = mapper.deleteAll(list);
        System.err.println("affect rows = " + affectRows);
    }


    @After
    public void after() {
        closeSession();
    }

}
