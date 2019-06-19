package com.wxy.param;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SysRoleParam {

    private Integer id;

    @NotBlank(message = "名字不能为空")
    @Length(min = 2, max = 20, message = "长度在2-20之间")
    private String name;

    @Min(value = 1, message = "类型错误")
    @Max(value = 2, message = "类型错误")
    private Integer type;

    @Min(value = 0, message = "状态错误")
    @Max(value = 1, message = "状态错误")
    private Integer status;

    @Length(max = 150, message = "长度在150之内")
    private String remark;
}