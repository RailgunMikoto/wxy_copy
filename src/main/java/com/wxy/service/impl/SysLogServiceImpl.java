package com.wxy.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.wxy.beans.LogType;
import com.wxy.beans.PageBean;
import com.wxy.beans.PageQuery;
import com.wxy.entity.*;
import com.wxy.exception.ParamException;
import com.wxy.mapper.*;
import com.wxy.service.*;
import com.wxy.util.DeptLevelUtil;
import com.wxy.util.IpUtil;
import com.wxy.util.JsonMapper;
import com.wxy.util.RequestHold;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class SysLogServiceImpl implements SysLogService {

    @Resource
    SysLogMapper mapper;

    @Resource
    SysDeptMapper deptMapper;

    @Resource
    SysUserMapper userMapper;

    @Resource
    SysAclModuleMapper aclModuleMapper;

    @Resource
    SysAclMapper aclMapper;

    @Resource
    SysRoleMapper roleMapper;

    @Resource
    SysRoleAclService roleAclService;

    @Resource
    SysRoleUserService roleUserService;


    /**
     * 分页显示数据
     * @param query
     * @return
     */
    @Override
    public PageBean<SysLogWithBLOBs> getPage(PageQuery query) {
        PageBean<SysLogWithBLOBs> pageBean = new PageBean<>();
        pageBean.setData(mapper.findPage(query));
        pageBean.setTotal(mapper.getCount(query));
        pageBean.setOffset(query.getOffset());
        pageBean.setPageNo(query.getPageNo());
        pageBean.setPageSize(query.getPageSize());

        return pageBean;
    }

    /**
     * 保存数据
     * @param before
     * @param after
     */
    @Override
    public void saveDeptLog(SysDept before, SysDept after) {
        SysLogWithBLOBs sysLog = new SysLogWithBLOBs();
        sysLog.setType(LogType.TYPE_DEPT);
        sysLog.setTargetId(after==null ? before.getId():after.getId());
        sysLog.setOperateIp(IpUtil.getUserIP(RequestHold.getRequest()));
        sysLog.setOperateTime(new Date());
        sysLog.setOperator(RequestHold.getUser().getUsername());
        sysLog.setStatus(0);
        sysLog.setNewValue(after==null?"": JsonMapper.obj2String(after));
        sysLog.setOldValue(before==null?"":JsonMapper.obj2String(before));
        mapper.insertSelective(sysLog);
    }

    @Override
    public void saveUserLog(SysUser before, SysUser after) {
        SysLogWithBLOBs sysLog = new SysLogWithBLOBs();
        sysLog.setType(LogType.TYPE_USER);
        sysLog.setTargetId(after==null ? before.getId():after.getId());
        sysLog.setOperateIp(IpUtil.getUserIP(RequestHold.getRequest()));
        sysLog.setOperateTime(new Date());
        sysLog.setOperator(RequestHold.getUser().getUsername());
        sysLog.setStatus(0);
        sysLog.setNewValue(after==null?"": JsonMapper.obj2String(after));
        sysLog.setOldValue(before==null?"":JsonMapper.obj2String(before));
        mapper.insertSelective(sysLog);
    }

    @Override
    public void saveAclModuleLog(SysAclModule before, SysAclModule after) {
        SysLogWithBLOBs sysLog = new SysLogWithBLOBs();
        sysLog.setType(LogType.TYPE_ACL_MODULE);
        sysLog.setTargetId(after==null ? before.getId():after.getId());
        sysLog.setOperateIp(IpUtil.getUserIP(RequestHold.getRequest()));
        sysLog.setOperateTime(new Date());
        sysLog.setOperator(RequestHold.getUser().getUsername());
        sysLog.setStatus(0);
        sysLog.setNewValue(after==null?"": JsonMapper.obj2String(after));
        sysLog.setOldValue(before==null?"":JsonMapper.obj2String(before));
        mapper.insertSelective(sysLog);
    }

    @Override
    public void saveAclLog(SysAcl before, SysAcl after) {
        SysLogWithBLOBs sysLog = new SysLogWithBLOBs();
        sysLog.setType(LogType.TYPE_ACL);
        sysLog.setTargetId(after==null ? before.getId():after.getId());
        sysLog.setOperateIp(IpUtil.getUserIP(RequestHold.getRequest()));
        sysLog.setOperateTime(new Date());
        sysLog.setOperator(RequestHold.getUser().getUsername());
        sysLog.setStatus(0);
        sysLog.setNewValue(after==null?"": JsonMapper.obj2String(after));
        sysLog.setOldValue(before==null?"":JsonMapper.obj2String(before));
        mapper.insertSelective(sysLog);
    }

    @Override
    public void saveRoleLog(SysRole before, SysRole after) {
        SysLogWithBLOBs sysLog = new SysLogWithBLOBs();
        sysLog.setType(LogType.TYPE_ROLE);
        sysLog.setTargetId(after==null ? before.getId():after.getId());
        sysLog.setOperateIp(IpUtil.getUserIP(RequestHold.getRequest()));
        sysLog.setOperateTime(new Date());
        sysLog.setOperator(RequestHold.getUser().getUsername());
        sysLog.setStatus(0);
        sysLog.setNewValue(after==null?"": JsonMapper.obj2String(after));
        sysLog.setOldValue(before==null?"":JsonMapper.obj2String(before));
        mapper.insertSelective(sysLog);
    }

    /**
     * 恢复操作
     * @param id
     */
    @Override
    public void recover(Integer id) {
        SysLogWithBLOBs sysLog = mapper.selectByPrimaryKey(id);
        if(sysLog != null){
            if(sysLog.getStatus()!=1){
                int type = sysLog.getType();
                switch (type){
                    case LogType.TYPE_DEPT:
                        SysDept sysDept = deptMapper.selectByPrimaryKey(sysLog.getTargetId());
                        if(sysDept==null){
                            throw new ParamException("待还原的部门不存在");
                        }
                        // 新增和删除不做还原处理
                        if(StringUtils.isBlank(sysLog.getOldValue()) || StringUtils.isBlank(sysLog.getNewValue())){
                            throw new ParamException("新增和删除无法还原");
                        }
                        SysDept dept = JsonMapper.string2Obj(sysLog.getOldValue(), new TypeReference<SysDept>() {});
                        dept.setOperateTime(new Date());
                        dept.setOperator(RequestHold.getUser().getUsername());
                        dept.setOperateIp(IpUtil.getUserIP(RequestHold.getRequest()));
                        updateDeptById(dept);
                        break;
                    case LogType.TYPE_USER:
                        SysUser sysUser = userMapper.selectByPrimaryKey(sysLog.getTargetId());
                        if(sysUser == null){
                            throw new ParamException("待还原的用户不存在");
                        }
                        SysDept sysDept1 = deptMapper.selectByPrimaryKey(sysUser.getDeptId());
                        if(sysDept1 == null){
                            throw new ParamException("用户所属部门不存在");
                        }
                        // 新增和删除不做还原处理
                        if(StringUtils.isBlank(sysLog.getOldValue()) || StringUtils.isBlank(sysLog.getNewValue())){
                            throw new ParamException("新增和删除无法还原");
                        }
                        SysUser user = JsonMapper.string2Obj(sysLog.getOldValue(), new TypeReference<SysUser>() {});
                        user.setOperator(RequestHold.getUser().getUsername());
                        user.setOperateTime(new Date());
                        user.setOperateIp(IpUtil.getUserIP(RequestHold.getRequest()));
                        userMapper.updateByPrimaryKeySelective(user);
                        // 保存操作日志
                        saveUserLog(sysUser, user);
                        break;
                    case LogType.TYPE_ACL_MODULE:
                        SysAclModule sysAclModule = aclModuleMapper.selectByPrimaryKey(sysLog.getTargetId());
                        if(sysAclModule == null){
                            throw new ParamException("待还原的权限模块不存在");
                        }
                        SysAclModule parent = aclModuleMapper.selectByPrimaryKey(sysAclModule.getParentId());
                        if(parent == null){
                            throw new ParamException("权限模块所属上级权限模块不存在");
                        }
                        // 新增和删除不做还原处理
                        if(StringUtils.isBlank(sysLog.getOldValue()) || StringUtils.isBlank(sysLog.getNewValue())){
                            throw new ParamException("新增和删除无法还原");
                        }
                        SysAclModule aclModule = JsonMapper.string2Obj(sysLog.getOldValue(), new TypeReference<SysAclModule>() {});
                        aclModule.setOperateIp(IpUtil.getUserIP(RequestHold.getRequest()));
                        aclModule.setOperator(RequestHold.getUser().getUsername());
                        aclModule.setOperateTime(new Date());
                        updateAclModule(aclModule);
                        break;
                    case LogType.TYPE_ACL:
                        SysAcl sysAcl = aclMapper.selectByPrimaryKey(sysLog.getTargetId());
                        if(sysAcl == null){
                            throw new ParamException("待还原的权限点不存在");
                        }
                        SysAclModule aclModule1 = aclModuleMapper.selectByPrimaryKey(sysAcl.getAclModuleId());
                        if(aclModule1 == null){
                            throw new ParamException("权限点所属权限模块不存在");
                        }
                        // 新增和删除不做还原处理
                        if(StringUtils.isBlank(sysLog.getOldValue()) || StringUtils.isBlank(sysLog.getNewValue())){
                            throw new ParamException("新增和删除无法还原");
                        }
                        SysAcl acl = JsonMapper.string2Obj(sysLog.getOldValue(), new TypeReference<SysAcl>() {});
                        acl.setOperateTime(new Date());
                        acl.setOperateIp(IpUtil.getUserIP(RequestHold.getRequest()));
                        acl.setOperator(RequestHold.getUser().getUsername());
                        aclMapper.updateByPrimaryKeySelective(acl);
                        // 保存操作日志
                        saveAclLog(sysAcl, acl);
                        break;
                    case LogType.TYPE_ROLE:
                        SysRole sysRole = roleMapper.selectByPrimaryKey(sysLog.getTargetId());
                        if(sysRole==null){
                            throw new ParamException("待还原的角色不存在");
                        }
                        // 新增和删除不做还原处理
                        if(StringUtils.isBlank(sysLog.getOldValue()) || StringUtils.isBlank(sysLog.getNewValue())){
                            throw new ParamException("新增和删除无法还原");
                        }
                        SysRole role = JsonMapper.string2Obj(sysLog.getOldValue(), new TypeReference<SysRole>() {
                        });
                        role.setOperateTime(new Date());
                        role.setOperateIp(IpUtil.getUserIP(RequestHold.getRequest()));
                        role.setOperator(RequestHold.getUser().getUsername());
                        roleMapper.updateByPrimaryKeySelective(role);
                        // 保存操作日志
                        saveRoleLog(sysRole, role);
                        break;
                    case LogType.TYPE_ROLE_ACL:
                        SysRole roleAcl = roleMapper.selectByPrimaryKey(sysLog.getTargetId());
                        if(roleAcl == null){
                            throw new ParamException("待还原的角色不存在");
                        }
                        roleAclService.changeAcls(sysLog.getTargetId(), JsonMapper.string2Obj(sysLog.getOldValue(), new TypeReference<List<Integer>>() {}));
                        break;
                    case LogType.TYPE_ROLE_USER:
                        SysRole roleUser = roleMapper.selectByPrimaryKey(sysLog.getTargetId());
                        if(roleUser == null){
                            throw new ParamException("待还原的角色不存在");
                        }
                        roleUserService.changeUsers(sysLog.getTargetId(), JsonMapper.string2Obj(sysLog.getOldValue(), new TypeReference<List<Integer>>() {}));
                        break;
                }
                sysLog.setStatus(1);
                mapper.updateByPrimaryKeySelective(sysLog);
            }
        }
    }

    /**
     * 更新部门
     * @param dept
     * @return
     */
    private int updateDeptById(SysDept dept) {
        // 查询这个部门存不存在
        SysDept sysDept = deptMapper.selectByPrimaryKey(dept.getId());
        if(sysDept == null){
            // 需要修改的部门不存在
            throw new ParamException("部门不存在");
        }
        // 查询除开自己之外有没有名字相同的部门
        SysDept deptByName = deptMapper.checkUpdateDept(dept.getName(), dept.getId());
        if(deptByName != null){
            throw new ParamException("部门已存在");
        }
        // 查寻下级部门
        List<SysDept> deptByParentId = deptMapper.findDeptByParentId(dept.getId());
        // 获取上级部门的level
        String parentLevel = dept.getLevel();
        // 修改下级部门的level
        recUpdateDept(deptByParentId, parentLevel);
        // 修改原有部门
        int result = deptMapper.updateByPrimaryKey(dept);
        // 保存操作日志
        saveDeptLog(sysDept, dept);
        return result;
    }
    /**
     * 递归更新部门level
     * @param deptList
     * @param parentLevel
     */
    @Transactional
    public  void recUpdateDept(List<SysDept> deptList, String parentLevel){
        if(!deptList.isEmpty()){
            for (SysDept dept : deptList) {
                // 循环取出SysDept
                // 修改dept的数据
                dept.setOperateIp(IpUtil.getUserIP(RequestHold.getRequest()));
                dept.setOperator(RequestHold.getUser().getUsername());
                dept.setOperateTime(new Date());
                // 计算部门的等级
                // 将部门等级封装到对象中
                dept.setLevel(DeptLevelUtil.getDeptLevel(parentLevel, dept.getParentId()));
                // 获取当前部门的等级
                String parentIdNow = dept.getLevel();
                // 查寻下级部门
                List<SysDept> deptByParentId = deptMapper.findDeptByParentId(dept.getId());
                // 递归更新下级部门
                recUpdateDept(deptByParentId, parentIdNow);
                // 更新这个部门
                deptMapper.updateByPrimaryKey(dept);
            }
        }
    }
    /**
     * 修改权限模块
     * @param module
     * @return
     */
    public int updateAclModule(SysAclModule module) {
        // 判断这个权限模块存不存在
        SysAclModule aclModule = aclModuleMapper.selectByPrimaryKey(module.getId());
        if(aclModule == null){
            throw new ParamException("此权限模块不存在");
        }
        // 检查修改的权限模块名在上级权限模块中是否已存在，除开自己
        SysAclModule aclModule1 = aclModuleMapper.checkUpdateAclModule(module.getName(), module.getId(), module.getParentId());
        if(aclModule1 == null){
            // 不存在，修改
            // 查询此权限模块名的下级权限模块
            List<SysAclModule> childAclModule = aclModuleMapper.findAclModuleByParentId(module.getId());
            // 修改下级权限模块的level
            recAclModuleLevel(childAclModule, module.getLevel());
            // 修改权限模块
            int result = aclModuleMapper.updateByPrimaryKeySelective(module);
            // 保存修改日志
            saveAclModuleLog(aclModule, module);
            return result;
        }
        throw new ParamException("权限模块名已存在");
    }
    @Transactional
    public void recAclModuleLevel(List<SysAclModule> childAclModule, String parentLevel){
        if(!childAclModule.isEmpty()){
            // 循环取出所有权限模块
            for (SysAclModule module : childAclModule) {
                // 设置module中的属性值
                module.setOperateTime(new Date());
                module.setOperator(RequestHold.getUser().getUsername());
                module.setOperateIp(IpUtil.getUserIP(RequestHold.getRequest()));
                // 获取这个权限模块的新level
                String level = DeptLevelUtil.getDeptLevel(parentLevel, module.getParentId());
                module.setLevel(level);
                // 查询这个权限模块的下级权限模块
                List<SysAclModule> moduleByParentId = aclModuleMapper.findAclModuleByParentId(module.getId());
                // 更新模块
                aclModuleMapper.updateByPrimaryKeySelective(module);
                // 修改下级权限模块的level
                recAclModuleLevel(moduleByParentId, level);
            }
        }
    }
}
