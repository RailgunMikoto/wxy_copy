package com.wxy.beans;

import com.wxy.entity.SysUser;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageBean<T> {
    private List<T> data = new ArrayList<>();

    private Integer total;

    private Integer pageSize;

    private Integer pageNo;

    private Integer offset;
}
