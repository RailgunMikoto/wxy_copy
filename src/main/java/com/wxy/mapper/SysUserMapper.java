package com.wxy.mapper;

import com.wxy.entity.SysDept;
import com.wxy.entity.SysUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysUserMapper {
    int deleteByPrimaryKey(Integer id);

    // 添加一个用户
    int insert(SysUser record);

    int insertSelective(SysUser record);

    SysUser selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysUser record);

    int updateByPrimaryKey(SysUser record);

    // 根据名字查询用户
    SysUser findUserByName(@Param("username") String name);

    // 查询所有用户
    List<SysUser> findAllUser();

    // 根据部门id查询用户
    List<SysUser> findUserByDeptId(@Param("deptId") Integer deptId);

    // 根据部门id和分页查询用户
    List<SysUser> findUserPageBean(@Param("deptId") Integer deptId, @Param("begin") Integer begin, @Param("pageSize") Integer pageSize);

    // 查询一个部门的人数
    int findUserCountByDeptId(@Param("deptId") Integer deptId);

    // 根据用户名和密码查询用户
    SysUser userLogin(@Param("username") String username, @Param("password") String password);

    // 查询除开自己之外是否存在用户名相同的用户
    SysUser checkUpdateUser(@Param("username") String username, @Param("id") Integer id);

    // 根据用户id列表查询用户
    List<SysUser> findUserByIds(@Param("ids") List<Integer> ids);
}