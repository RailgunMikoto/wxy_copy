package com.wxy.mapper;

import com.wxy.entity.SysRole;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysRoleMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SysRole record);

    // 添加角色
    int insertSelective(SysRole record);

    // 查询角色，根据角色id
    SysRole selectByPrimaryKey(Integer id);

    // 修改角色
    int updateByPrimaryKeySelective(SysRole record);

    int updateByPrimaryKey(SysRole record);

    // 根据名字查询角色
    SysRole findRoleByName(@Param("name") String name);

    // 查询全部角色
    List<SysRole> findAll();
}