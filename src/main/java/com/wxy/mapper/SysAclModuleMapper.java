package com.wxy.mapper;

import com.wxy.entity.SysAclModule;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysAclModuleMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SysAclModule record);

    // 添加一个用户
    int insertSelective(SysAclModule record);

    // 根据id查找权限模块
    SysAclModule selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysAclModule record);

    int updateByPrimaryKey(SysAclModule record);

    // 查询全部权限模块
    List<SysAclModule> findAllAclMoudle();

    // 根据权限模块的名字和上级部门id查询权限模块，用于检查新增模块是否相同
    SysAclModule checkAclModule(@Param("name") String name, @Param("parentId") Integer parentId);

    // 查询除开自己是否存在权限模块名相同的模块,在上级权限模块中
    SysAclModule checkUpdateAclModule(@Param("name") String name, @Param("id") Integer id, @Param("parentId") Integer parentId);

    // 根据parent_id查询权限模块，用于查询一个权限模块的下级权限模块
    List<SysAclModule> findAclModuleByParentId(@Param("parentId") Integer parentId);
}