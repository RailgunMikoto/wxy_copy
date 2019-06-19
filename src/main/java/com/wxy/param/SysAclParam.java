package com.wxy.param;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SysAclParam {

    private Integer id;

    private String code;

    @NotBlank(message = "名字不能为空")
    @Length(min = 2, max = 20, message = "长度需要在2-20之间")
    private String name;

    @NotNull(message = "权限模块不能为空")
    private Integer aclModuleId;

    @Length(min = 6, max = 100, message = "长度需要在6-100个字符之间")
    private String url;

    @Min(1)
    @Max(3)
    private Integer type;

    @Min(0)
    @Max(1)
    private Integer status;

    @NotNull(message = "顺序不能为空")
    private Integer seq;

    @Length(max = 150, message = "备注长度需要在150以内")
    private String remark;
}
