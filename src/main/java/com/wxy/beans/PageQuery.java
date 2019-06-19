package com.wxy.beans;

import lombok.*;

import java.util.Date;


@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PageQuery {

    //通用
    @Getter
    @Setter
    Integer pageNo = 1;

    @Getter
    @Setter
    Integer pageSize;

    @Setter
    Integer offset;
    public Integer getOffset(){
        return pageSize*(pageNo - 1);
    }

    // 部门查询
    @Getter
    @Setter
    Integer deptId;

    // 权限模块查询
    @Getter
    @Setter
    Integer aclModuleId;


    // 权限更新日志查询
    @Getter
    @Setter
    Integer type;

    @Getter
    @Setter
    String beforeSeg;

    @Getter
    @Setter
    String afterSeg;

    @Getter
    @Setter
    String operator;

    @Getter
    @Setter
    Date fromTime;

    @Getter
    @Setter
    Date toTime;

}
