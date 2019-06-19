package com.wxy.dto;

import com.wxy.entity.SysAcl;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SysAclDto extends SysAcl {

    private boolean checked = false;
    private boolean hasAcl = false;

}
