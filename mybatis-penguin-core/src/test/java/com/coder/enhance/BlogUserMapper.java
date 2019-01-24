package com.coder.enhance;

import com.code.enhance.PageModel;
import com.coder.enhance.plugin.Pager;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author jeffy
 * @date 2019/1/22
 **/
//@Mapper
public interface BlogUserMapper extends BaseMapper<BlogUser, Integer> {

    List<BlogUser> getUserByUserName(@Param("userName") String userName);

    PageModel getUserByUserNameByPage(@Param("userName") String userName, @Param("pager") Pager pager);
}
