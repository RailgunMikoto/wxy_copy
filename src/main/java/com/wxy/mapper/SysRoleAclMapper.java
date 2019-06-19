package com.wxy.mapper;

import com.wxy.entity.SysAcl;
import com.wxy.entity.SysRoleAcl;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysRoleAclMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SysRoleAcl record);

    int insertSelective(SysRoleAcl record);

    SysRoleAcl selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysRoleAcl record);

    int updateByPrimaryKey(SysRoleAcl record);

    // 根据角色roleId，查询角色拥有的权限
    List<SysAcl> getAllAclByRoleId(@Param("roleId") Integer roleId);

    // 根据角色id， 查询角色拥有的权限点id
    List<Integer> getAclIdByRoleId(@Param("roleIds") List<Integer> roleIds);

    // 根据roleId删除aclIds
    void deleteAclByRoleId(@Param("roleId") Integer roleId);

    // 修改角色与权限点之间的关系
    int insertRoleAcl(@Param("roleId") Integer roleId, @Param("aclIds") List<Integer> aclIds);
}