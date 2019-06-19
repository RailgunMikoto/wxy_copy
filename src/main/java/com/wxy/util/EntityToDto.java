package com.wxy.util;

import org.springframework.beans.BeanUtils;

public class EntityToDto {

    /**
     * 将实体类中的数据拷贝到dto中
     * @param entity
     * @param dto
     * @param <T>
     * @return
     */

    public static <T>T toDto(T entity, T dto){
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}
