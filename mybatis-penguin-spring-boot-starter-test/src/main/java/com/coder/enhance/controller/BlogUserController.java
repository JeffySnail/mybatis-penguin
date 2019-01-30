package com.coder.enhance.controller;

import com.coder.enhance.entity.BlogUser;
import com.coder.enhance.mapper.BlogUserMapper;
import com.coder.enhance.plugin.Pager;
import com.coder.enhance.service.BlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author jeffy
 * @date 2019/1/26
 **/
@RestController
public class BlogUserController {

    @Autowired
    private BlogUserMapper blogUserMapper;

    @Autowired
    private BlogService blogService;

    @RequestMapping("/blog/user")
    public BlogUser user() {
        return blogUserMapper.loadOne(18);

    }

    @RequestMapping("/blog/username")
    public List<BlogUser> users(String name) {
        return blogUserMapper.getUserByUserName(name);
    }

    @RequestMapping("blog/user/add")
    public boolean addUserAndContent() {
        return blogService.insert("jeffy", "这是要给测试");
    }

    @RequestMapping("/blog/username/page")
    public List<BlogUser> pagerBlogUser(String name, int page) {
        Pager pager = new Pager(page, 2);
        return blogUserMapper.getUserByUserNameByPage(name, pager).getRecords();
    }
}
