package com.wxy.service;

import com.wxy.beans.PageBean;
import com.wxy.entity.SysUser;
import com.wxy.param.PageQueryParam;
import com.wxy.param.SysUserParam;

import java.util.List;

public interface SysUserService {
    // 查询所有用户
    List<SysUser> findAllUser();

    // 添加一个用户
    int insertUser(SysUserParam param);

    // 根据分页查询用户
    PageBean getPageBean(PageQueryParam param, PageBean<SysUser> page);

    // 用户登录，根据用户名和密码查询用户
    SysUser userLogin(String username, String password);

    // 修改用户
    int updateUser(SysUserParam param);

    // 删除用户
    int deleteUser(Integer id);
}
