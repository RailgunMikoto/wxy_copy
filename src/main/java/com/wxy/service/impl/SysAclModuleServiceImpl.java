package com.wxy.service.impl;

import com.wxy.dto.SysAclModuleDto;
import com.wxy.entity.SysAclModule;
import com.wxy.exception.ParamException;
import com.wxy.mapper.SysAclMapper;
import com.wxy.mapper.SysAclModuleMapper;
import com.wxy.param.SysAclModuleParam;
import com.wxy.service.SysAclModuleService;
import com.wxy.service.SysLogService;
import com.wxy.tree.AclModuleTree;
import com.wxy.util.DeptLevelUtil;
import com.wxy.util.IpUtil;
import com.wxy.util.RequestHold;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class SysAclModuleServiceImpl implements SysAclModuleService {

    @Resource
    SysAclModuleMapper mapper;

    @Resource
    SysAclMapper aclMapper;

    @Resource
    SysLogService logService;

    /**
     * 获取权限模块树
     * @return
     */
    @Override
    public List<SysAclModuleDto> getAclModuleTree() {
        // 查找全部的权限模块
        List<SysAclModule> allAclModule = mapper.findAllAclMoudle();
        // 1、将所有SysAclModule转换成SysAclModuleDto
        List<SysAclModuleDto> dtos = AclModuleTree.getAclModuleList(allAclModule);
        // 2、获取权限模块树
        List<SysAclModuleDto> aclModuleTree = AclModuleTree.createAclModule(dtos);

        return aclModuleTree;
    }

    /**
     * 根据id查找权限模块
     * @param id
     * @return
     */
    @Override
    public SysAclModule findAclModuleById(Integer id) {
        return mapper.selectByPrimaryKey(id);
    }

    /**
     * 添加一个权限模块
     * @param param
     * @return
     */
    @Override
    public int insertAclModule(SysAclModuleParam param) {
        // 根据名字和上级部门id查询权限模块是否存在
        SysAclModule temp = mapper.checkAclModule(param.getName(), param.getParentId());
        if(temp!=null){
            throw new ParamException("此权限模块已存在");
        }
        // 封装SysAclModule
        SysAclModule aclModule = SysAclModule.builder().
                name(param.getName()).
                parentId(param.getParentId()).
                remark(param.getRemark()).
                status(param.getStatus()).
                seq(param.getSeq()).build();
        aclModule.setOperateIp(IpUtil.getUserIP(RequestHold.getRequest()));
        aclModule.setOperator(RequestHold.getUser().getUsername());
        aclModule.setOperateTime(new Date());
        String level = null;
        // 查询上级权限模块的权限
        SysAclModule module = findAclModuleById(aclModule.getParentId());
        if(module == null){
            level = "0";
        }else {
            level = DeptLevelUtil.getDeptLevel(module.getLevel(), module.getId());
        }
        aclModule.setLevel(level);
        int result = mapper.insertSelective(aclModule);
        // 保存操作日志
        logService.saveAclModuleLog(null, aclModule);
        return result;
    }

    /**
     * 修改权限模块
     * @param param
     * @return
     */
    @Override
    public int updateAclModule(SysAclModuleParam param) {
        // 判断这个权限模块存不存在
        SysAclModule aclModule = mapper.selectByPrimaryKey(param.getId());
        if(aclModule == null){
            throw new ParamException("权限模块不存在");
        }
        // 检查权限模块的上级权限模块是否存在
        SysAclModule aclModule2 = mapper.selectByPrimaryKey(param.getParentId());
        if(aclModule2 == null){
            throw new ParamException("修改的上级权限模块不存在");
        }
        // 检查修改的权限模块名在上级权限模块中是否已存在，除开自己
        SysAclModule aclModule1 = mapper.checkUpdateAclModule(param.getName(), param.getId(), param.getParentId());
        if(aclModule1 != null){
            // 存在，抛出异常
            throw new ParamException("权限模块名已存在");
        }
        SysAclModule module = SysAclModule.builder().
                name(param.getName()).
                parentId(param.getParentId()).
                remark(param.getRemark()).
                status(param.getStatus()).
                seq(param.getSeq()).
                id(param.getId()).build();
        module.setOperateIp(IpUtil.getUserIP(RequestHold.getRequest()));
        module.setOperator(RequestHold.getUser().getUsername());
        module.setOperateTime(new Date());
        String level = null;
        // 查询上级权限模块的权限
        SysAclModule temp = findAclModuleById(module.getParentId());
        if(temp == null){
            level = "0";
        }else {
            level = DeptLevelUtil.getDeptLevel(temp.getLevel(), temp.getId());
        }
        module.setLevel(level);
        // 检查权限模块不能修改到自己的子级权限模块中
        if(level.contains(DeptLevelUtil.getDeptLevel(aclModule.getLevel(), aclModule.getId()))){
            throw new ParamException("不能修改权限模块到自己的子级模块中");
        }
        // 不存在，修改
        // 查询此权限模块名的下级权限模块
        List<SysAclModule> childAclModule = mapper.findAclModuleByParentId(module.getId());
        // 修改下级权限模块的level
        recAclModuleLevel(childAclModule, module.getLevel());
        // 修改权限模块
        int result = mapper.updateByPrimaryKeySelective(module);
        // 保存修改日志
        logService.saveAclModuleLog(aclModule, module);
        return result;
    }

    @Override
    @Transactional
    public void recAclModuleLevel(List<SysAclModule> childAclModule, String parentLevel){
        if(!childAclModule.isEmpty()){
            // 循环取出所有权限模块
            for (SysAclModule module : childAclModule) {
                // 设置module中的属性值
                module.setOperator(RequestHold.getUser().getUsername());
                module.setOperateIp(IpUtil.getUserIP(RequestHold.getRequest()));
                module.setOperateTime(new Date());
                // 获取这个权限模块的新level
                String level = DeptLevelUtil.getDeptLevel(parentLevel, module.getParentId());
                module.setLevel(level);
                // 查询这个权限模块的下级权限模块
                List<SysAclModule> moduleByParentId = mapper.findAclModuleByParentId(module.getId());
                // 更新模块
                mapper.updateByPrimaryKeySelective(module);
                // 修改下级权限模块的level
                recAclModuleLevel(moduleByParentId, level);
            }
        }
    }

    /**
     * 删除权限模块
     * @param aclModule
     * @return
     */
    @Override
    public int deleteAclModule(SysAclModule aclModule) {
        // 查询待删除的权限模块
        SysAclModule module = mapper.selectByPrimaryKey(aclModule.getId());
        if(module == null){
            // 待删除的权限模块不存在
            throw new ParamException("待删除的权限模块不存在");
        }
        // 查询此权限模块是否存在下级权限模块和权限点
        List<SysAclModule> childModules = mapper.findAclModuleByParentId(aclModule.getId());
        int aclCount = aclMapper.getAclCount(aclModule.getId());
        if(!childModules.isEmpty() || aclCount != 0){
            throw new ParamException("需要先删除孩子");
        }
        // 删除权限模块
        int result = mapper.deleteByPrimaryKey(aclModule.getId());
        // 保存修改日志
        logService.saveAclModuleLog(aclModule, null);
        return result;
    }
}
