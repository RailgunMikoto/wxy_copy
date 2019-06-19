package com.wxy.service.impl;

import com.wxy.beans.PageBean;
import com.wxy.beans.PageQuery;
import com.wxy.entity.SysDept;
import com.wxy.entity.SysUser;
import com.wxy.exception.ParamException;
import com.wxy.mapper.SysDeptMapper;
import com.wxy.mapper.SysUserMapper;
import com.wxy.param.PageQueryParam;
import com.wxy.param.SysUserParam;
import com.wxy.service.SysLogService;
import com.wxy.service.SysUserService;
import com.wxy.util.IpUtil;
import com.wxy.util.MD5Util;
import com.wxy.util.PasswordUtil;
import com.wxy.util.RequestHold;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class SysUserServiceImpl implements SysUserService {

    @Resource
    SysUserMapper mapper;

    @Resource
    SysDeptMapper deptMapper;

    @Resource
    SysLogService logService;

    // 查询所有用户
    @Override
    public List<SysUser> findAllUser() {
        return mapper.findAllUser();
    }


    // 添加一个用户
    @Override
    public int insertUser(SysUserParam param) {
        if(mapper.findUserByName(param.getUsername())==null){
            // 封装User
            SysUser user = SysUser.builder().
                    username(param.getUsername()).
                    telephone(param.getTelephone()).
                    mail(param.getMail()).
                    deptId(param.getDeptId()).
                    status(param.getStatus()).
                    remark(param.getRemark()).build();
            user.setOperateTime(new Date());
            user.setOperateIp(IpUtil.getUserIP(RequestHold.getRequest()));
            user.setOperator(RequestHold.getUser().getUsername());
            // 使用工具类获取密码
            String password = PasswordUtil.randomPassword();
            // MD5加密
            password = MD5Util.encrypt(password);
            user.setPassword(password);
            // 添加用户
            int result = mapper.insertSelective(user);
            // 保存操作日志
            logService.saveUserLog(null, user);
            return result;
        }
        return 0;
    }

    /**
     * 根据部门获取User，分页显示
     * @param param
     * @return
     */
    @Override
    public PageBean<SysUser> getPageBean(PageQueryParam param, PageBean<SysUser> page) {
        // 查询此部门的人数
        int count = mapper.findUserCountByDeptId(param.getDeptId());
        if(count>0){
            // 构建查询对象
            PageQuery query = PageQuery.builder().
                    deptId(param.getDeptId()).
                    pageNo(param.getPageNo()).
                    pageSize(param.getPageSize()).build();
            // 部门有人
            // 偏移量
            Integer begin = query.getPageSize() * (query.getPageNo() - 1);
            // 获取分页查询的数据
            List<SysUser> userPageBean = mapper.findUserPageBean(query.getDeptId(), begin, query.getPageSize());
            // 将数据封装到PageBean中
            page.setData(userPageBean);
            page.setTotal(count);
        }
        return page;
    }

    /**
     * 根据用户名和密码查询用户
     * 用户登录
     * @param username
     * @param password
     * @return
     */
    @Override
    public SysUser userLogin(String username, String password) {
        // 查询用户
        SysUser user = mapper.userLogin(username, password);
        if(user != null && user.getStatus()==1){
            return user;
        }
        return null;
    }

    /**
     * 修改用户
     * @param param
     * @return
     */
    @Override
    public int updateUser(SysUserParam param) {
        // 查询此用户是否存在
        SysUser sysUser = mapper.selectByPrimaryKey(param.getId());
        if(sysUser == null){
            throw new ParamException("此用户不存在");
        }
        // 需要修改的用户存在
        // 查询修改后的部门
        SysDept sysDept = deptMapper.selectByPrimaryKey(param.getDeptId());
        if(sysDept==null){
            // 这个部门不存在
            throw new ParamException("此部门不存在");
        }
        // 部门存在
        // 查询是否已有此用户
        SysUser userByName = mapper.checkUpdateUser(param.getUsername(), param.getId());
        if(userByName != null){
            throw new ParamException("用户名已存在");
        }
        // 封装User
        SysUser user = SysUser.builder().
                username(param.getUsername()).
                telephone(param.getTelephone()).
                mail(param.getMail()).
                deptId(param.getDeptId()).
                status(param.getStatus()).
                remark(param.getRemark()).
                id(param.getId()).build();
        user.setOperateTime(new Date());
        user.setOperateIp(IpUtil.getUserIP(RequestHold.getRequest()));
        user.setOperator(RequestHold.getUser().getUsername());
        // 如果用户不存在或者就是它自己，就修改用户
        int result = mapper.updateByPrimaryKeySelective(user);
        // 保存操作日志
        logService.saveUserLog(sysUser, user);
        return result;
    }

    /**
     * 删除用户
     * @param id
     */
    @Override
    public int deleteUser(Integer id) {
        SysUser user = mapper.selectByPrimaryKey(id);
        if(user!=null){
            // 删除用户
            int result = mapper.deleteByPrimaryKey(id);
            // 保存操作日志
            logService.saveUserLog(user, null);
            return result;
        }
        return 0;
    }
}
