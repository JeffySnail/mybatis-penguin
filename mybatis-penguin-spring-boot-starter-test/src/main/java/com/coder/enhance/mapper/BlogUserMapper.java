package com.coder.enhance.mapper;

import com.code.enhance.PageModel;
import com.coder.enhance.BaseMapper;
import com.coder.enhance.entity.BlogUser;
import com.coder.enhance.plugin.Pager;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author jeffy
 * @date 2019/1/25
 **/
public interface BlogUserMapper extends BaseMapper<BlogUser, Integer> {


    List<BlogUser> getUserByUserName(@Param("userName") String userName);

    PageModel getUserByUserNameByPage(@Param("userName") String userName, @Param("pager") Pager pager);

}
