package com.wxy.mapper;

import com.wxy.entity.SysAcl;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysAclMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SysAcl record);

    // 添加一个权限点
    int insertSelective(SysAcl record);

    // 根据id查询权限点是否存在
    SysAcl selectByPrimaryKey(Integer id);

    // 修改权限点
    int updateByPrimaryKeySelective(SysAcl record);

    int updateByPrimaryKey(SysAcl record);

    // 查询分页显示的数据
    List<SysAcl> findAclPage(@Param("aclModuleId") Integer aclModuleId, @Param("begin") Integer begin, @Param("pageSize") Integer pageSize);

    // 查询一个权限模块下面有多少条权限
    int getAclCount(@Param("aclModuleId") Integer aclModuleId);

    // 根据名字和权限模块id查询权限点
    SysAcl checkAcl(@Param("name") String name, @Param("aclModuleId") Integer aclModuleId);

    // 根据名字和权限模块id查询权限点
    SysAcl checkAclId(@Param("name") String name, @Param("aclModuleId") Integer aclModuleId, @Param("id") Integer id);

    // 根据权限模块id查询权限模块下的权限点
    List<SysAcl> findAclByAclModuleId(@Param("aclModuleId") Integer aclModuleId);

    // 查询所有的权限点
    List<SysAcl> findAllAcl();

    // 根据权限点id查询权限点
    List<SysAcl> findAclByIds(@Param("ids") List<Integer> ids);

    // 根据url查询权限点
    SysAcl findAclByUrl(@Param("url") String url);

    // 查询某个用户拥有的权限点
    List<SysAcl> findUserAcl(@Param("userId") Integer userId);
}