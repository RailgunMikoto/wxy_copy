package com.wxy.mapper;

import com.wxy.entity.SysRoleUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysRoleUserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SysRoleUser record);

    int insertSelective(SysRoleUser record);

    SysRoleUser selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysRoleUser record);

    int updateByPrimaryKey(SysRoleUser record);

    // 根据用户id获取角色id
    List<Integer> getRoleIdByUserId(@Param("userId") Integer userId);

    // 获取所有数据
    List<SysRoleUser> findAll();

    // 根据角色id获取角色用户
    List<SysRoleUser> getRoleUserByRoleId(@Param("roleId") Integer roleId);

    // 根据角色id获取用户id
    List<Integer> getUserIdByRoleId(@Param("roleId") Integer roleId);

    // 根据角色id删除用户
    int deleteUserByRoleId(@Param("roleId") Integer roleId);
}