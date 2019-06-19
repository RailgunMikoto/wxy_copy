package com.wxy.util;

import org.springframework.beans.BeanUtils;

import java.util.List;

public class ToDtosUtil {
    
    
    public static <T>List<T> toDtos(List<T> source, List<T> target, T dto){
        if(!source.isEmpty()&&!target.isEmpty()){
            for (T t : source) {
                BeanUtils.copyProperties(t, dto);
                target.add(dto);
            }
        }
        return target;
    }
}
