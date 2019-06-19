package com.wxy.service.impl;

import com.google.common.collect.Sets;
import com.wxy.beans.LogType;
import com.wxy.dto.SysAclDto;
import com.wxy.dto.SysAclModuleDto;
import com.wxy.entity.*;
import com.wxy.exception.ParamException;
import com.wxy.mapper.*;
import com.wxy.service.SysRoleAclService;
import com.wxy.tree.AclModuleTree;
import com.wxy.tree.RoleAclTree;
import com.wxy.util.IpUtil;
import com.wxy.util.JsonMapper;
import com.wxy.util.RequestHold;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
public class SysRoleAclServiceImpl implements SysRoleAclService {

    @Resource
    private SysRoleAclMapper mapper;

    @Resource
    private SysRoleMapper roleMapper;

    @Resource
    private SysAclMapper aclMapper;

    @Resource
    private SysAclModuleMapper aclModuleMapper;

    @Resource
    private SysRoleUserMapper roleUserMapper;

    @Resource
    SysLogMapper logMapper;

    /**
     * 获取角色权限树
     * @param roleId
     * @return
     */
    @Override
    public List<SysAclModuleDto> createRoleAclTree(Integer roleId) {
        // 检查角色存不存在
        SysRole sysRole = roleMapper.selectByPrimaryKey(roleId);
        if(sysRole==null){
            // 角色不存在
            return new ArrayList<SysAclModuleDto>();
        }else{
            // 获取角色所有的权限点
            List<SysAcl> roleAcls = getAclByRoleId(roleId);
            // 获取用户的所有权限点
            List<SysAcl> userAcls = getUserAcl();
            // 获取全部的权限点
            List<SysAcl> allAcls = aclMapper.findAllAcl();
            // 将全部的权限点转换成SysAclDto，并且设置hasAcl和checked，清除其中被冻结的权限点
            List<SysAclDto> aclDtos = aclToDto(allAcls, userAcls, roleAcls);
            // 获取权限模块树
            // 获取所有权限模块
            List<SysAclModule> allAclMoudle = aclModuleMapper.findAllAclMoudle();
            // 转换为Dto
            List<SysAclModuleDto> aclModuleList = AclModuleTree.getAclModuleList(allAclMoudle);
            // 创造权限模块树
            List<SysAclModuleDto> aclModuleTree = AclModuleTree.createAclModule(aclModuleList);
            // 生产角色权限树
            return RoleAclTree.createRoleTree(aclModuleTree, aclDtos);
        }
    }

    /**
     * 修改角色权限
     * @param aclIds
     * @return
     */
    @Override
    @Transactional
    public int changeAcls(Integer roleId, List<Integer> aclIds) {
        // 获取修改前角色的权限点id列表
        List<Integer> originalAclIds = getRoleAclIds(roleId);
        SysRole sysRole = roleMapper.selectByPrimaryKey(roleId);
        if(sysRole==null){
            // 角色不存在
            throw new ParamException("角色不存在");
        }
        if(aclIds.size() == originalAclIds.size()){
            Set<Integer> aclIds1 = Sets.newHashSet(aclIds);
            Set<Integer> originalAclIds1 = Sets.newHashSet(originalAclIds);
            for (Integer aclId : aclIds1) {
                originalAclIds1.remove(aclId);
            }
            if(originalAclIds1 == null || originalAclIds1.isEmpty()){
                return 1;
            }
        }
        // 删除修改前角色与权限点之间的关系
        mapper.deleteAclByRoleId(roleId);
        for (Integer aclId : aclIds) {
            // 添加修改后角色与权限点之间的关系
            SysRoleAcl sysRoleAcl = SysRoleAcl.builder().
                    aclId(aclId).
                    roleId(roleId).build();
            sysRoleAcl.setOperateIp(IpUtil.getUserIP(RequestHold.getRequest()));
            sysRoleAcl.setOperateTime(new Date());
            sysRoleAcl.setOperator(RequestHold.getUser().getUsername());
            mapper.insertSelective(sysRoleAcl);
        }
        // 保存修改日志
        saveRoleAclLog(roleId, originalAclIds, aclIds);
        return 1;
    }

    /**
     *  获取当前角色拥有的权限id
     * @return
     */
    @Override
    public List<Integer> getRoleAclIds(Integer roleId) {

        // 存储角色权限的id
        ArrayList<Integer> aclIds = new ArrayList<>();
        // 获取角色所有权限
        List<SysAcl> aclByRoleId = getAclByRoleId(roleId);
        if(aclByRoleId!=null && !aclByRoleId.isEmpty()){
            for (SysAcl acl :aclByRoleId) {
                if(acl.getStatus()==1){
                    // 权限点属于激活状态
                    aclIds.add(acl.getId());
                }
            }
            return aclIds;
        }
        // 如果权限点集合为空
        return aclIds;
    }

    /**
     * 根据角色id获取角色所有权限点
     * @param roleId
     * @return
     */
    public List<SysAcl> getAclByRoleId(Integer roleId){
        // 根据RoleId查询角色拥有的权限点id
        ArrayList<Integer> roleIds = new ArrayList<>();
        roleIds.add(roleId);
        List<Integer> aclId = mapper.getAclIdByRoleId(roleIds);
        // 如果权限点id不为空,则根据权限点id查询权限点
        if(!aclId.isEmpty()){
            List<SysAcl> aclByIds = aclMapper.findAclByIds(aclId);
            if(aclByIds == null){
                // 权限点为空
                return new ArrayList<SysAcl>();
            }
            return aclByIds;
        }
        return new ArrayList<SysAcl>();
    }

    /**
     * 根据用户id获取所有权限点
     */
    public List<SysAcl> getUserAcl(){
        // 存储属于用户权限点
        List<SysAcl> userAcls = new ArrayList<>();
        // 获取用户id
        Integer id = RequestHold.getUser().getId();
        // 检查用户是否为超级用户
        if(isSuperUser(RequestHold.getUser())){
            userAcls = aclMapper.findAllAcl();
            return userAcls;
        }
        // 根据用户id，获取角色id
        List<Integer> roleIds = roleUserMapper.getRoleIdByUserId(id);
        if(!roleIds.isEmpty()){
            // 根据角色id，获取权限点id
            List<Integer> aclIds = mapper.getAclIdByRoleId(roleIds);
            if(!aclIds.isEmpty()){
                // 根据权限点id获取权限点
                userAcls = aclMapper.findAclByIds(aclIds);
            }
        }
        return userAcls;
    }

    /**
     * 将所有权限点转换为Dto
     * @param allAcls       所有权限点
     * @param userAcls      属于角色的权限点
     * @param roleAcls      属于用户的权限点
     * @return
     */
    public List<SysAclDto> aclToDto(List<SysAcl> allAcls, List<SysAcl> userAcls, List<SysAcl> roleAcls){
        List<SysAclDto> aclDtos = new ArrayList<>();
        if(!allAcls.isEmpty()){
            for (SysAcl acl : allAcls) {
                // 将acl转换成aclDto
                SysAclDto dto = new SysAclDto();
                BeanUtils.copyProperties(acl, dto);
                if(!roleAcls.isEmpty()){
                    for (SysAcl roleAcl : roleAcls) {
                        if(roleAcl.getId()==dto.getId()){
                            // 如果角色拥有此权限
                            dto.setChecked(true);
                            break;
                        }
                    }
                }
                if(!userAcls.isEmpty()){
                    for (SysAcl userAcl : userAcls) {
                        if(userAcl.getId()==dto.getId()){
                            // 如果用户拥有此权限
                            dto.setHasAcl(true);
                            break;
                        }
                    }
                }
                //超级用户不应该清除被冻结的节点
                if(isSuperUser(RequestHold.getUser())){
                    // 是超级用户直接添加
                    aclDtos.add(dto);
                }else{
                    if(dto.getStatus()==1){
                        // 不是超级用户
                        // 添加处于有效状态的权限点
                        aclDtos.add(dto);
                    }
                }
            }
        }
        return aclDtos;
    }

    /**
     * 保存角色权限日志
     * @param roleId
     * @param before
     * @param after
     */
    public void saveRoleAclLog(Integer roleId, List<Integer> before, List<Integer> after) {
        SysLogWithBLOBs sysLog = new SysLogWithBLOBs();
        sysLog.setType(LogType.TYPE_ROLE_ACL);
        sysLog.setTargetId(roleId);
        sysLog.setOperateIp(IpUtil.getUserIP(RequestHold.getRequest()));
        sysLog.setOperateTime(new Date());
        sysLog.setOperator(RequestHold.getUser().getUsername());
        sysLog.setStatus(0);
        sysLog.setNewValue(after==null?"": JsonMapper.obj2String(after));
        sysLog.setOldValue(before==null?"":JsonMapper.obj2String(before));
        logMapper.insertSelective(sysLog);
    }

    /**
     * 判断用户是否为超级用户
     * @param user
     * @return
     */
    private boolean isSuperUser(SysUser user) {

        if(user.getUsername().equals("Admin") && user.getId()==1){
            return true;
        }else{
            return false;
        }
    }
}
