package com.wxy.param;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PageQueryAclParam {

    @NotNull(message = "必须提供查询部门")
    Integer aclModuleId;

    @NotNull(message = "当前页不能为空")
    @Min(value = 1)
    Integer pageNo;

    @Min(value = 1)
    Integer pageSize;

    Integer offset;

}
