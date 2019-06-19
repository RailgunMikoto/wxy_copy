package com.wxy.tree;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.wxy.dto.SysAclDto;
import com.wxy.dto.SysAclModuleDto;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RoleAclTree {

    public static List<SysAclModuleDto> createRoleTree(List<SysAclModuleDto> aclModuleTree, List<SysAclDto> aclList){
        // 以权限点所属权限模块id为key，权限点为value保存数据
        Multimap<Integer, SysAclDto> map = ArrayListMultimap.create();
        if (!aclList.isEmpty()){
            for (SysAclDto aclDto : aclList) {
                map.put(aclDto.getAclModuleId(), aclDto);
            }
        }
        // 递归绑定权限点
        recRoleTree(aclModuleTree, map);

        return aclModuleTree;
    }

    private static void recRoleTree(List<SysAclModuleDto> aclModuleTree, Multimap<Integer, SysAclDto> map){
        if(!aclModuleTree.isEmpty()){
            for (SysAclModuleDto aclModuleDto : aclModuleTree) {
                List<SysAclDto> sysAclDtos = (List<SysAclDto>) map.get(aclModuleDto.getId());
                // 对权限点进行排序
                Collections.sort(sysAclDtos, new Comparator<SysAclDto>() {
                    @Override
                    public int compare(SysAclDto o1, SysAclDto o2) {
                        return o1.getSeq()-o2.getSeq();
                    }
                });
                aclModuleDto.setAclList(sysAclDtos);

                recRoleTree(aclModuleDto.getAclModuleList(), map);
            }
        }
    }
}
