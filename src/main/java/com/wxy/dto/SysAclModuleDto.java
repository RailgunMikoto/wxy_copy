package com.wxy.dto;

import com.wxy.entity.SysAclModule;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public class SysAclModuleDto extends SysAclModule {
    List<SysAclModuleDto> aclModuleList = new ArrayList<>();
    List<SysAclDto> aclList = new ArrayList<>();
}
