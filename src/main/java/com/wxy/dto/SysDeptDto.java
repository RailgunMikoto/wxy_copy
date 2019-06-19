package com.wxy.dto;

import com.wxy.entity.SysDept;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class SysDeptDto extends SysDept {

    List<SysDeptDto> deptList = new ArrayList<>();
}
