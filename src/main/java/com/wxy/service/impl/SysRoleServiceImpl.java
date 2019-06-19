package com.wxy.service.impl;

import com.wxy.entity.SysRole;
import com.wxy.exception.ParamException;
import com.wxy.mapper.SysRoleMapper;
import com.wxy.param.SysRoleParam;
import com.wxy.service.SysLogService;
import com.wxy.service.SysRoleService;
import com.wxy.util.IpUtil;
import com.wxy.util.RequestHold;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class SysRoleServiceImpl implements SysRoleService {

    @Resource
    private SysRoleMapper mapper;

    @Resource
    SysLogService logService;

    /**
     * 添加一个角色
     * @param param
     * @return
     */
    @Override
    public int insertRole(SysRoleParam param) {
        SysRole sysRole = mapper.findRoleByName(param.getName());
        if(sysRole != null){
            return 0;
        }
        // 封装SysRole
        SysRole role = SysRole.builder().
                name(param.getName()).
                remark(param.getRemark()).
                status(param.getStatus()).
                type(param.getType()).build();
        role.setOperateTime(new Date());
        role.setOperateIp(IpUtil.getUserIP(RequestHold.getRequest()));
        role.setOperator(RequestHold.getUser().getUsername());
        // 添加角色
        int result = mapper.insertSelective(role);
        // 保存修改日志
        logService.saveRoleLog(null, role);
        return result;
    }

    /**
     * 修改角色
     * @param param
     * @return
     */
    @Override
    public int updateRole(SysRoleParam param) {
        SysRole sysRole = mapper.selectByPrimaryKey(param.getId());
        if(sysRole == null){
            // 角色不存在
            throw new ParamException("待修改的角色不存在");
        }
        // 判断角色名是否重复
        SysRole roleByName = mapper.findRoleByName(param.getName());
        if(roleByName != null){
            throw new ParamException("角色名重复");
        }
        // 封装SysRole
        SysRole role = SysRole.builder().
                type(param.getType()).
                status(param.getStatus()).
                remark(param.getRemark()).
                name(param.getName()).
                id(param.getId()).build();
        role.setOperateTime(new Date());
        role.setOperateIp(IpUtil.getUserIP(RequestHold.getRequest()));
        role.setOperator(RequestHold.getUser().getUsername());
        // 角色名不重复，修改
        int result = mapper.updateByPrimaryKeySelective(role);
        // 保存修改日志
        logService.saveRoleLog(sysRole, role);
        return result;

    }
    /**
     * 获取角色列表
     * @return
     */
    @Override
    public List<SysRole> roleList() {
        return mapper.findAll();
    }
}
