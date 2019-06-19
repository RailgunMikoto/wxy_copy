package com.wxy.dto;

import com.wxy.entity.SysAcl;
import com.wxy.entity.SysRoleAcl;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SysRoleAclDto extends SysRoleAcl {
    private List<SysAcl> aclList;
}
