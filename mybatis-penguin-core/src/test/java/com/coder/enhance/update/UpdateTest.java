package com.coder.enhance.update;

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
public class UpdateTest extends AbstractCRUDTest {
    @Before
    public void before() {
        addMapper(BlogUserMapper.class);
        addInterceptor(new PagerInterceptor());
    }

    @Test
    public void updateTest() {
        BlogUserMapper blogUserMapper = getSqlSession().getMapper(BlogUserMapper.class);
        BlogUser updateBlogUser = new BlogUser();
        updateBlogUser.setUserName("new Name");
        updateBlogUser.setUserAddress("new address");
        updateBlogUser.setUserId(36);
        int updateRows = blogUserMapper.update(updateBlogUser);
        System.err.println("affect rows = " + updateRows);
    }

    @Test
    public void updateSelectiveTest() {
        BlogUserMapper blogUserMapper = getSqlSession().getMapper(BlogUserMapper.class);
        BlogUser updateBlogUser = new BlogUser();
        updateBlogUser.setUserId(37);
        updateBlogUser.setUserSex(2);
        int updateRows = blogUserMapper.updateSelective(updateBlogUser);
        System.err.println("affect rows = " + updateRows);
    }

    @Test
    public void updateSelectiveAllTest() {
        BlogUserMapper blogUserMapper = getSqlSession().getMapper(BlogUserMapper.class);
        BlogUser updateBlogUser1 = new BlogUser();
        updateBlogUser1.setUserId(37);
        updateBlogUser1.setUserSex(2);
        BlogUser updateBlogUser2 = new BlogUser();
        updateBlogUser2.setUserId(38);
        updateBlogUser2.setUserName("newbloguser2");

        int updateRows = blogUserMapper.updateSelectiveAll(Arrays.asList(updateBlogUser1, updateBlogUser2));
        System.err.println("affect rows = " + updateRows);
    }

    @Test
    public void updateAllTest() {
        BlogUserMapper blogUserMapper = getSqlSession().getMapper(BlogUserMapper.class);
        BlogUser updateBlogUser1 = new BlogUser();
        updateBlogUser1.setUserId(37);
        updateBlogUser1.setUserSex(2);
        BlogUser updateBlogUser2 = new BlogUser();
        updateBlogUser2.setUserId(38);
        updateBlogUser2.setUserSex(4);
        updateBlogUser2.setUserName("newbloguser2");
        int updateRows = blogUserMapper.updateAll(Arrays.asList(updateBlogUser1, updateBlogUser2));
        System.err.println("affect rows = " + updateRows);
    }

    @After
    public void after() {
        closeSession();
    }
}
