package com.wxy.param;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class SysDeptParam {

    private Integer id;

    @NotBlank(message = "名字不可以为空")
    @Length(min = 2, max = 15, message = "名字需要在2-15之间")
    private String name;


    private Integer parentId = 0;

    private String level;

    @NotNull(message = "seq不可以为空")
    private Integer seq;

    @Length(max = 150, message = "长度需要在150个字以内")
    private String remark;
}
