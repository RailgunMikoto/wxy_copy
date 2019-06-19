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
public class SysUserParam {

    private Integer id;

    @NotBlank(message = "用户名不能为空")
    @Length(min = 2, max = 20, message = "用户名长度为2-20个字符")
    private String username;

    @NotBlank(message = "电话不能为空")
    @Length(max = 13, message = "电话长度需要在13个字符以内")
    private String telephone;

    @NotBlank(message = "电话不能为空")
    @Length(max = 50, message = "邮箱长度需要在50个字符以内")
    private String mail;

    private String password;

    @NotNull(message = "必须提供用户所在的部门")
    private Integer deptId;

    @NotNull(message = "必须指定用户的状态")
    @Min(value = 0)
    @Max(value = 2)
    private Integer status;

    @Length(max = 200, message = "备注长度需要在200个字符以内")
    private String remark;
}
