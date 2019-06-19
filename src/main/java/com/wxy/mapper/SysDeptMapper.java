package com.wxy.mapper;

import com.wxy.entity.SysDept;
import com.wxy.entity.SysUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysDeptMapper {
    // 根据id删除部门
    int deleteByPrimaryKey(Integer id);

    // 添加一个部门
    int insert(SysDept record);

    int insertSelective(SysDept record);

    // 根据部门id查询部门
    SysDept selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysDept record);

    // 根据id更新部门
    int updateByPrimaryKey(SysDept record);

    // 根据id查找level
    String getMyselfLevel(@Param("id") Integer id);

    // 查找全部的数据
    List<SysDept> findAllDept();

    // 根据部门名字查找部门
    SysDept findDeptByName(@Param("name") String name);

    // 根据parentId查找部门
    List<SysDept> findDeptByParentId(@Param("parentId") Integer parentId);

    // 查询除开自己之外有没有名字相同的部门
    SysDept checkUpdateDept(@Param("name") String name, @Param("id") Integer id);
}