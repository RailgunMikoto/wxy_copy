package com.wxy.tree;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.wxy.dto.SysDeptDto;
import com.wxy.entity.SysDept;
import com.wxy.util.EntityToDto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DeptTreeUtil {

    // 1、将所有SysDept封装到SysDeptDto中
    public static List<SysDeptDto> getDtoList(List<SysDept> allDept) {
        if(allDept.isEmpty()){
            return null;
        }
        // allDept不为空
        // 1、将SysDept封装成SysDeptDto
        List<SysDeptDto> allDeptDto = new ArrayList<>();
        for (SysDept dept : allDept) {
            SysDeptDto deptDto = new SysDeptDto();
            EntityToDto.toDto(dept, deptDto);
            allDeptDto.add(deptDto);
        }

        return allDeptDto;
    }

    // 2、获取根节点和子节点
    // 3、根据parentId调节部门的位置
    public static List<SysDeptDto> createDeptTree(List<SysDeptDto> allDeptDto){
        // 创建map集合
        Multimap<Integer, SysDeptDto> map = ArrayListMultimap.create();
        // 创建最高级部门集合
        List<SysDeptDto> rootDept = new ArrayList<>();
        if(!allDeptDto.isEmpty()){
            // 遍历allDeptDto
            for (SysDeptDto dto : allDeptDto) {
                if(dto.getParentId()==0){
                    // 如果父id等于零，即此节点为根节点
                    rootDept.add(dto);
                }else{
                    // 如果不是父节点，则将父节点的值设为key将SysDeptDto存入map集合中
                    map.put(dto.getParentId(), dto);
                }
            }
            Collections.sort(rootDept, new Comparator<SysDeptDto>() {
                @Override
                public int compare(SysDeptDto o1, SysDeptDto o2) {
                    return o1.getSeq()-o2.getSeq();
                }
            });
        }
        recursion(rootDept, map);

        return rootDept;
    }

    // 3、利用递归设置部门的位置  recursion
    public static void recursion(List<SysDeptDto> rootDept, Multimap<Integer, SysDeptDto> map){
        // 循环处理根节点
        if(!rootDept.isEmpty()){
            for (SysDeptDto dto:rootDept) {
                // 获取map的key值
                Integer key = dto.getId();
                // 根据key值获取SysDeptDtos
                List<SysDeptDto> sysDeptDtos = (List<SysDeptDto>)map.get(key);
                if(!sysDeptDtos.isEmpty()){
                    Collections.sort(sysDeptDtos, new Comparator<SysDeptDto>() {
                        @Override
                        public int compare(SysDeptDto o1, SysDeptDto o2) {
                            return o1.getSeq()-o2.getSeq();
                        }
                    });
                    // 将sysDeptDtos存入dto中的deptDtoList
                    dto.setDeptList(sysDeptDtos);
                    // 使用递归处理部门分层问题
                    recursion(sysDeptDtos, map);
                }
            }
        }
    }
}
