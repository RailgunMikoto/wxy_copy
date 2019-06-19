package com.wxy.service.impl;

import com.wxy.dto.SysDeptDto;
import com.wxy.entity.SysDept;
import com.wxy.entity.SysUser;
import com.wxy.exception.ParamException;
import com.wxy.mapper.SysDeptMapper;
import com.wxy.mapper.SysUserMapper;
import com.wxy.param.SysDeptParam;
import com.wxy.service.SysDeptService;
import com.wxy.service.SysLogService;
import com.wxy.tree.DeptTreeUtil;
import com.wxy.util.DeptLevelUtil;
import com.wxy.util.IpUtil;
import com.wxy.util.RequestHold;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class SysDeptServiceImpl implements SysDeptService {

    @Resource
    SysDeptMapper mapper;

    @Resource
    SysUserMapper userMapper;

    @Resource
    SysLogService logService;

    // 添加一个部门
    @Override
    public int insert(SysDeptParam param) {
        // 检查更新的部门是否存在
        SysDept deptByName = mapper.findDeptByName(param.getName());
        if(deptByName==null){
            // 封装dept
            SysDept dept = SysDept.builder().
                    name(param.getName()).
                    remark(param.getRemark()).
                    seq(param.getSeq()).
                    parentId(param.getParentId()).build();
            dept.setOperateIp(IpUtil.getUserIP(RequestHold.getRequest()));
            dept.setOperator(RequestHold.getUser().getUsername());
            dept.setOperateTime(new Date());
            String parentLevel = getMyselfLevel(dept.getParentId());
            dept.setLevel(DeptLevelUtil.getDeptLevel(parentLevel, dept.getParentId()));
            // 添加部门
            int result = mapper.insertSelective(dept);
            // 保存操作日志
            logService.saveDeptLog(null, dept);
            return result;
        }else{
            throw new ParamException("此部门已存在");
        }
    }

    // 根据id获取level
    @Override
    public String getMyselfLevel(Integer id) {
        return mapper.getMyselfLevel(id);
    }

    // 查找全部部门
    @Override
    public List<SysDept> findAllDept() {
        return mapper.findAllDept();
    }

    // 根据id查找部门
    @Override
    public SysDept findDeptById(Integer id) {
        return mapper.selectByPrimaryKey(id);
    }

    // 创建部门树
    @Override
    public List<SysDeptDto> createDeptTree() {
        // 1、查询出全部的部门
        List<SysDept> allDept = mapper.findAllDept();
        // 2、将全部部门封装到SysDeptDto中
        List<SysDeptDto> dtoList = DeptTreeUtil.getDtoList(allDept);
        // 3、获取部门树
        List<SysDeptDto> deptTree = DeptTreeUtil.createDeptTree(dtoList);

        return deptTree;
    }

    /**
     * 更新部门
     * @param param
     * @return
     */
    @Override
    public int updateDeptById(SysDeptParam param) {
        // 查询这个部门存不存在
        SysDept sysDept = mapper.selectByPrimaryKey(param.getId());
        if(sysDept == null){
            // 需要修改的部门不存在
            return 0;
        }
        // 查询除开自己之外有没有名字相同的部门
        SysDept deptByName = mapper.checkUpdateDept(param.getName(), param.getId());
        if(deptByName==null){
            throw new ParamException("部门已存在");
        }
        SysDept dept = SysDept.builder().
                name(param.getName()).
                remark(param.getRemark()).
                seq(param.getSeq()).
                parentId(param.getParentId()).
                id(param.getId()).build();
        dept.setOperateIp(IpUtil.getUserIP(RequestHold.getRequest()));
        dept.setOperator(RequestHold.getUser().getUsername());
        dept.setOperateTime(new Date());
        // 计算部门的等级
        // 查找上级部门的level
        String parentLevel = getMyselfLevel(dept.getParentId());
        // 将当前部门的level封装到对象中
        dept.setLevel(DeptLevelUtil.getDeptLevel(parentLevel, dept.getParentId()));
        // 修改前部门的下级部门的level
        String deptLevel = DeptLevelUtil.getDeptLevel(sysDept.getLevel(), dept.getId());
        // 当前部门level
        String level = dept.getLevel();
        if(level.contains(deptLevel)){
            throw new ParamException("不能修改上级部门到它的子级部门中");
        }
        // 查寻下级部门
        List<SysDept> deptByParentId = mapper.findDeptByParentId(dept.getId());
        // 获取当前部门的level
        parentLevel = dept.getLevel();
        // 修改下级部门的level
        recUpdateDept(deptByParentId, parentLevel);
        // 修改原有部门
        int result = mapper.updateByPrimaryKey(dept);
        // 保存操作日志
        logService.saveDeptLog(sysDept, dept);
        return result;
    }

    /**
     * 递归更新部门level
     * @param deptList
     * @param parentLevel
     */
    @Override
    @Transactional
    public  void recUpdateDept(List<SysDept> deptList, String parentLevel){
        if(!deptList.isEmpty()){
            for (SysDept dept : deptList) {
                // 循环取出SysDept
                // 修改dept的数据
                dept.setOperateTime(new Date());
                dept.setOperateIp(IpUtil.getUserIP(RequestHold.getRequest()));
                dept.setOperator(RequestHold.getUser().getUsername());
                // 计算部门的等级
                // 将部门等级封装到对象中
                dept.setLevel(DeptLevelUtil.getDeptLevel(parentLevel, dept.getParentId()));
                // 获取当前部门的等级
                String parentIdNow = dept.getLevel();
                // 查寻下级部门
                List<SysDept> deptByParentId = mapper.findDeptByParentId(dept.getId());
                // 递归更新下级部门
                recUpdateDept(deptByParentId, parentIdNow);
                // 更新这个部门
                mapper.updateByPrimaryKey(dept);
            }
        }
    }

    /**
     * 根据部门id删除部门
     * 如果有下级部门，则抛出异常
     * @param id
     * @return
     */
    @Override
    public int deleteDeptById(Integer id) {
        SysDept sysDept = mapper.selectByPrimaryKey(id);
        if(sysDept==null){
            throw new ParamException("当前部门不存在");
        }
        // 查找下级部门
        List<SysDept> deptByParentId = mapper.findDeptByParentId(id);
        // 查找部门下的用户
        List<SysUser> deptUser = userMapper.findUserByDeptId(id);
        if((deptByParentId != null && !deptByParentId.isEmpty()) || (deptUser != null && !deptUser.isEmpty())){
            throw new ParamException("请先删除下级部门或部门员工");
        }
        // 如果没有下级部门，部门没有员工，则删除部门
        int result = mapper.deleteByPrimaryKey(id);
        // 保存操作日志
        logService.saveDeptLog(sysDept, null);
        return result;
    }


}
