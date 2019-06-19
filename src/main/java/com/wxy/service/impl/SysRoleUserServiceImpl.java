package com.wxy.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.wxy.beans.LogType;
import com.wxy.dto.SysRoleUserDto;
import com.wxy.entity.SysLogWithBLOBs;
import com.wxy.entity.SysRole;
import com.wxy.entity.SysRoleUser;
import com.wxy.entity.SysUser;
import com.wxy.exception.ParamException;
import com.wxy.mapper.SysLogMapper;
import com.wxy.mapper.SysRoleMapper;
import com.wxy.mapper.SysRoleUserMapper;
import com.wxy.mapper.SysUserMapper;
import com.wxy.service.SysLogService;
import com.wxy.service.SysRoleUserService;
import com.wxy.util.IpUtil;
import com.wxy.util.JsonMapper;
import com.wxy.util.RequestHold;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
public class SysRoleUserServiceImpl implements SysRoleUserService {

    @Resource
    SysRoleUserMapper mapper;

    @Resource
    SysUserMapper userMapper;

    @Resource
    SysRoleMapper roleMapper;

    @Resource
    SysLogMapper logMapper;

    /**
     * 获取角色用户列表
     * @param roleId
     * @return
     */
    @Override
    public SysRoleUserDto getRoleUserList(Integer roleId) {

        // 存储返回的数据
        SysRoleUserDto roleUserDto = new SysRoleUserDto();
        // 存储没有选择当前角色的用户
        List<SysUser> unselected = new ArrayList<>();
        // 存储选择当前角色的用户
        List<SysUser> selected = new ArrayList<>();
        // 获取所有用户信息
        List<SysUser> allUser = userMapper.findAllUser();

        // 判断：有用户时与没有用户时
        if(allUser!=null && !allUser.isEmpty()){
            // 有用户时
            // 根据roleId查询拥有此角色的用户id
            List<Integer> userIds = mapper.getUserIdByRoleId(roleId);
            // 判断：是否有用户拥有此角色
            if(userIds!=null && !userIds.isEmpty()){
                // 用户拥有当前角色
                // 查询用户
                List<SysUser> userByIds = userMapper.findUserByIds(userIds);
                // 判断：查看是否查询出了用户
                if(userByIds!=null && !userByIds.isEmpty()){
                    // 有用户
                    // 遍历所有用户
                    for (SysUser user : allUser) {
                        boolean flag = true;
                        // 判断：此用户是否拥有此角色，并且该用户处于激活状态
                        for (SysUser temp : userByIds) {
                            if(temp.getId()==user.getId() && user.getStatus()==1){
                                flag = false;
                                break;
                            }
                        }
                        if(!flag){
                            // 用户拥有此角色，并且该用户处于激活状态
                            selected.add(user);
                        }else {
                            if(user.getStatus()==1){
                                // 用户没有拥有此角色，并且该用户处于激活状态
                                unselected.add(user);
                            }
                        }
                    }
                }else{
                    // 没有用户
                    // 设置全部用户为没有选中的用户
                    unselected.addAll(allUser);
                }
            }else {
                // 没有用户拥有当前角色
                // 设置全部用户为没有选中的用户
                unselected.addAll(allUser);
            }
        }
        roleUserDto.setSelected(selected);
        roleUserDto.setUnselected(unselected);
        return roleUserDto;
    }
    /**
     * 获取当前拥有当前角色权限的用户Id
     * @param roleId
     * @return
     */
    @Override
    public List<Integer> getUserIds(Integer roleId) {
        // 存储返回值
        List<Integer> userIds = Lists.newArrayList();
        // 查询用户
        if(roleId!=null){
            return userIds;
        }
        List<Integer> ids = mapper.getUserIdByRoleId(roleId);
        if(ids == null || ids.isEmpty()){
            return userIds;
        }
        List<SysUser> users = userMapper.findUserByIds(ids);
        if(users == null || users.isEmpty()){
            return userIds;
        }
        for (SysUser user : users) {
            // 判断用户状态
            if (user.getStatus() == 1){
                userIds.add(user.getId());
            }
        }
        return userIds;
    }

    /**
     * 修改角色用户
     * @param roleId
     * @param userIds
     * @return
     */
    @Override
    @Transactional
    public int changeUsers(Integer roleId, List<Integer> userIds) {
        // 获取当前角色的用户id
        List<Integer> originalUserIds = getUserIds(roleId);
        SysRole role = roleMapper.selectByPrimaryKey(roleId);
        if(role==null){
            // 当前角色不存在
            throw new ParamException("当前角色不存在");
        }
        if(userIds.size() == originalUserIds.size()){
            Set<Integer> userIds1 = Sets.newHashSet(userIds);
            Set<Integer> original = Sets.newHashSet(originalUserIds);
            for (Integer temp: userIds1){
                original.remove(temp);
            }
            if(original.isEmpty()){
                // 角色用户列表不变，不修改
                return 1;
            }
        }
        // 删除当前角色所有的用户
        mapper.deleteUserByRoleId(roleId);
        for (Integer id : userIds) {
            SysRoleUser sysRoleUser = SysRoleUser.builder().
                    roleId(roleId).
                    userId(id).build();
            sysRoleUser.setOperateIp(IpUtil.getUserIP(RequestHold.getRequest()));
            sysRoleUser.setOperator(RequestHold.getUser().getUsername());
            sysRoleUser.setOperateTime(new Date());
            // 添加
            mapper.insertSelective(sysRoleUser);
        }
        // 保存修改日志
        saveRoleUserLog(roleId, originalUserIds, userIds);
        return 1;
    }

    /**
     * 保存角色用户日志
     * @param roleId
     * @param before
     * @param after
     */
    public void saveRoleUserLog(Integer roleId, List<Integer> before, List<Integer> after) {
        SysLogWithBLOBs sysLog = new SysLogWithBLOBs();
        sysLog.setType(LogType.TYPE_ROLE_USER);
        sysLog.setTargetId(roleId);
        sysLog.setOperateIp(IpUtil.getUserIP(RequestHold.getRequest()));
        sysLog.setOperateTime(new Date());
        sysLog.setOperator(RequestHold.getUser().getUsername());
        sysLog.setStatus(0);
        sysLog.setNewValue(after==null?"": JsonMapper.obj2String(after));
        sysLog.setOldValue(before==null?"":JsonMapper.obj2String(before));
        logMapper.insertSelective(sysLog);
    }
}
