package com.wxy.service;

import com.wxy.dto.SysDeptDto;
import com.wxy.entity.SysDept;
import com.wxy.param.SysDeptParam;

import java.util.List;

public interface SysDeptService {
    // 添加一个部门
    int insert(SysDeptParam param);

    // 根据id查找level
    String getMyselfLevel(Integer id);

    // 查找全部部门
    List<SysDept> findAllDept();

    // 根据id查找部门
    SysDept findDeptById(Integer id);

    // 获取部门树
    List<SysDeptDto> createDeptTree();

    // 根据id更新部门
    int updateDeptById(SysDeptParam param);

    // 递归更新部门
    public  void recUpdateDept(List<SysDept> deptList, String parentLevel);

    // 根据id删除部门
    int deleteDeptById(Integer id);

    // 根据parentId查找部门
}
