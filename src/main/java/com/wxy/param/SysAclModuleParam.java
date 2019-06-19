package com.wxy.param;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Setter
@Getter
public class SysAclModuleParam {

    private Integer id;

    @NotBlank(message = "权限模块名不能为空")
    @Length(min = 2, max = 15, message = "长度在2-15")
    private String name;

    private Integer parentId;

    private String level;

    @NotNull(message = "不能为空")
    private Integer seq;

    @NotNull(message = "状态不能为空")
    @Min(0)
    @Max(2)
    private Integer status;

    @Length(max = 150, message = "备注最多只能有150个字")
    private String remark;
}
