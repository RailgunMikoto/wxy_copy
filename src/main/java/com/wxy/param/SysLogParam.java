package com.wxy.param;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.Date;

@Getter
@Setter
public class SysLogParam {

    @Max(value = 7, message = "请选择查询类型")
    @Min(value = 0, message = "请选择查询类型")
    Integer type;

    String beforeSeg;

    String afterSeg;

    String operator;

    String fromTime;

    String toTime;

    Integer pageNo;

    Integer pageSize;

    Integer offset;
}
