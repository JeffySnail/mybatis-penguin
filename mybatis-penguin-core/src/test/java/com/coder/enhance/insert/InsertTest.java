package com.coder.enhance.insert;

import com.coder.enhance.AbstractCRUDTest;
import com.coder.enhance.BlogUser;
import com.coder.enhance.BlogUserMapper;
import com.coder.enhance.intercepter.PagerInterceptor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

/**
 * @author jeffy
 * @date 2019/1/24
 **/
public class InsertTest extends AbstractCRUDTest {
    @Before
    public void before() {
        addMapper(BlogUserMapper.class);
        addInterceptor(new PagerInterceptor());
    }

    @Test
    public void insertTest() {
        BlogUser blogUser = new BlogUser();
        blogUser.setUserSex(2);
        blogUser.setUserNation("中国");
        blogUser.setUserAddress("上海 虹桥机场");
        blogUser.setUserName("刘士余");
        BlogUserMapper blogUserMapper = getSqlSession().getMapper(BlogUserMapper.class);
        int affectRows = blogUserMapper.insert(blogUser);
        System.err.println("affect rows = " + affectRows);
        System.err.println("new row of userId = " + blogUser.getUserId());
    }

    @Test
    public void insertAllTest() {
        BlogUser blogUser1 = new BlogUser();
        blogUser1.setUserSex(2);
        blogUser1.setUserNation("中国");
        blogUser1.setUserAddress("上海 虹桥机场");
        blogUser1.setUserName("刘士余");
        BlogUser blogUser2 = new BlogUser();
        blogUser2.setUserSex(1);
        blogUser2.setUserNation("巴基斯坦");
        blogUser2.setUserAddress("伊斯兰堡");
        blogUser2.setUserName("伊姆兰·汗");
        BlogUserMapper blogUserMapper = getSqlSession().getMapper(BlogUserMapper.class);
        int affectRows = blogUserMapper.insertAll(Arrays.asList(blogUser1, blogUser2));
        System.err.println("affect rows = " + affectRows);
    }

    @After
    public void after() {
        closeSession();
    }
}
