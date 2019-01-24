package com.coder.enhance.xml;

import com.code.enhance.PageModel;
import com.coder.enhance.AbstractCRUDTest;
import com.coder.enhance.BlogUser;
import com.coder.enhance.BlogUserMapper;
import com.coder.enhance.plugin.Pager;
import org.junit.After;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * @author jeffy
 * @date 2019/1/25
 **/
public class XmlSelectTest extends AbstractCRUDTest {


    @Test
    public void Test() throws IOException {
        BlogUserMapper blogUserMapper = getSqlSessionWithXML().getMapper(BlogUserMapper.class);
        List<BlogUser> users = blogUserMapper.getUserByUserName("jeffy");
        Optional.ofNullable(users).ifPresent(e -> e.forEach(System.err::println));
        System.err.println("^^^^^^^^^^^^^^");
        List<BlogUser> userList = blogUserMapper.findByIds(Arrays.asList(18));
        Optional.ofNullable(userList).ifPresent(e -> e.forEach(System.err::println));
    }

    @Test
    public void pagerTest() throws IOException {
        BlogUserMapper blogUserMapper = getSqlSessionWithXML().getMapper(BlogUserMapper.class);
        Pager pager = new Pager(1, 2);
        PageModel pageModel = blogUserMapper.getUserByUserNameByPage("jeffy", pager);
        Optional.ofNullable(pageModel).ifPresent(e -> {
            System.err.println("pageCount = " + e.getPageCount());
            System.err.println("currentPage = " + e.getPage());
            System.err.println("pageSize = " + e.getPageSize());
            System.err.println("totalCount = " + e.getTotalCount());
            pageModel.getRecords().forEach(System.err::println);
        });
    }

    @After
    public void after() {
        closeSession();
    }

}
