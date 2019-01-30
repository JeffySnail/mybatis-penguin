package com.coder.enhance.service;

import com.coder.enhance.entity.BlogDetail;
import com.coder.enhance.entity.BlogUser;
import com.coder.enhance.mapper.BlogDetailMapper;
import com.coder.enhance.mapper.BlogUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author jeffy
 * @date 2019/1/29
 **/
@Service
@Transactional
public class BlogServiceImpl implements BlogService {

    @Autowired
    private BlogUserMapper blogUserMapper;

    @Autowired
    private BlogDetailMapper blogDetailMapper;

    @Override
    public boolean insert(String name, String content) {
        BlogUser blogUser = new BlogUser();
        blogUser.setUserName(name);
        blogUser.setUserAddress("");
        blogUser.setUserNation("中国");
        blogUser.setUserSex(4);
        blogUserMapper.insert(blogUser);
        BlogDetail blogDetail = new BlogDetail();
        blogDetail.setBlogContent(content);
        blogDetail.setUserId(blogUser.getUserId());
        blogDetailMapper.insert(blogDetail);
        return true;
    }
}
