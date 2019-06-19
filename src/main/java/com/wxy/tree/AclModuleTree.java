package com.wxy.tree;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.wxy.dto.SysAclModuleDto;
import com.wxy.entity.SysAclModule;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 用于获取权限模块树
 */
public class AclModuleTree {

    // 1、将所有的AclModule转换成AclModuleDto
    public static List<SysAclModuleDto> getAclModuleList(List<SysAclModule> aclModules){
        if(aclModules.isEmpty()){
            return null;
        }else{
            List<SysAclModuleDto> list = new ArrayList<>();
            // 循环赋值
            for (SysAclModule aclModule:aclModules) {
                SysAclModuleDto aclModuleDto = new SysAclModuleDto();
                BeanUtils.copyProperties(aclModule, aclModuleDto);
                list.add(aclModuleDto);
            }
            return list;
        }
    }

    // 2、获取所有的根元素和下级元素
    // 3、递归将下级元素添加到上级节点中
    public static List<SysAclModuleDto> createAclModule(List<SysAclModuleDto> acls){
        if(acls.isEmpty()){
            return null;
        }else{
            // 存储根节点
            List<SysAclModuleDto> rootLists = new ArrayList<>();
            // 使用谷歌map集合，存储下级元素
            Multimap<Integer, SysAclModuleDto> map = ArrayListMultimap.create();
            for (SysAclModuleDto dto: acls) {
                if(dto.getParentId() == 0){
                    // 如果是根节点
                    rootLists.add(dto);
                }else{
                    // 不是根节点
                    map.put(dto.getParentId(), dto);
                }
            }
            // 对根节点排序
            Collections.sort(rootLists, new Comparator<SysAclModuleDto>() {
                @Override
                public int compare(SysAclModuleDto o1, SysAclModuleDto o2) {
                    return o1.getSeq()-o2.getSeq();
                }
            });
            recAclMoudle(rootLists, map);
            return rootLists;
        }
    }

    // 3、递归生成部门模块树
    // rec(递归)
    private static void recAclMoudle(List<SysAclModuleDto> rootList, Multimap<Integer, SysAclModuleDto> map){
        if(rootList!=null){
            // 循环取出rootList中的值
            for (SysAclModuleDto dto : rootList) {
                // 取出dto的id值
                Integer id = dto.getId();
                // 取出dto的下级权限模块
                List<SysAclModuleDto> sysAclModuleDtos = (List<SysAclModuleDto>)map.get(id);
                // 对下级权限模块进行排序
                Collections.sort(sysAclModuleDtos, new Comparator<SysAclModuleDto>() {
                    @Override
                    public int compare(SysAclModuleDto o1, SysAclModuleDto o2) {
                        return o1.getSeq()-o2.getSeq();
                    }
                });
                // 设置dto与其下级元素之间的关系
                dto.setAclModuleList(sysAclModuleDtos);
                // 递归
                recAclMoudle(sysAclModuleDtos, map);
            }
        }
    }
}
