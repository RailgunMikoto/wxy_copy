package com.wxy.dto;

import com.wxy.entity.SysUser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SysRoleUserDto {
    private List<SysUser> unselected = new ArrayList<>();

    private List<SysUser> selected = new ArrayList<>();
}
