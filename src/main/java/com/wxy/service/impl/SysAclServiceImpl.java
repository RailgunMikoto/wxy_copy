package com.wxy.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Lists;
import com.wxy.beans.CacheKeyPrefix;
import com.wxy.beans.PageBean;
import com.wxy.beans.PageQuery;
import com.wxy.entity.SysAcl;
import com.wxy.entity.SysAclModule;
import com.wxy.entity.SysUser;
import com.wxy.exception.ParamException;
import com.wxy.mapper.SysAclMapper;
import com.wxy.mapper.SysAclModuleMapper;
import com.wxy.mapper.SysRoleAclMapper;
import com.wxy.mapper.SysRoleUserMapper;
import com.wxy.param.PageQueryAclParam;
import com.wxy.param.SysAclParam;
import com.wxy.service.JedisService;
import com.wxy.service.SysAclService;
import com.wxy.service.SysLogService;
import com.wxy.util.IpUtil;
import com.wxy.util.JsonMapper;
import com.wxy.util.RequestHold;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class SysAclServiceImpl implements SysAclService {

    @Resource
    SysAclMapper mapper;

    @Resource
    SysAclModuleMapper moduleMapper;

    @Resource
    SysRoleUserMapper roleUserMapper;

    @Resource
    SysRoleAclMapper roleAclMapper;

    @Resource
    JedisService jedisService;

    @Resource
    SysLogService logService;

    /**
     * 查询分页显示的数据
     * @param param
     * @return
     */
    @Override
    public PageBean<SysAcl> findAclPage(PageQueryAclParam param) {
        // 构建分页查询对象
        PageQuery query = PageQuery.builder().
                pageNo(param.getPageNo()).
                pageSize(param.getPageSize()).
                aclModuleId(param.getAclModuleId()).build();
        // 计算begin
        Integer begin = query.getPageSize() * (query.getPageNo() - 1);
        // 分页数据
        List<SysAcl> aclPage = mapper.findAclPage(query.getAclModuleId(), begin, query.getPageSize());
        // 总条数
        int count = mapper.getAclCount(query.getAclModuleId());
        // 封装PageBean
        PageBean<SysAcl> page = new PageBean<>();
        page.setTotal(count);
        page.setData(aclPage);
        return page;
    }

    /**
     * 添加一个权限点
     * @param param
     * @return
     */
    @Override
    public int insertAcl(SysAclParam param) {
        // 查询在同一权限模块下是否存在名字相同的权限点
        SysAcl aclByName = mapper.checkAcl(param.getName(), param.getAclModuleId());
        if(aclByName==null){
            // 封装SysAcl
            SysAcl acl = SysAcl.builder().
                    aclModuleId(param.getAclModuleId()).
                    name(param.getName()).
                    remark(param.getRemark()).
                    seq(param.getSeq()).
                    status(param.getStatus()).
                    type(param.getType()).
                    url(param.getUrl()).build();
            acl.setOperateIp(IpUtil.getUserIP(RequestHold.getRequest()));
            acl.setOperator(RequestHold.getUser().getUsername());
            acl.setOperateTime(new Date());
            //TODO:权限点code属性没有添加
            int res = mapper.insertSelective(acl);
            // 保存修改日志
            logService.saveAclLog(null, acl);
            return res;
        }
        return 0;
    }

    /**
     * 修改权限点
     * @param param
     * @return
     */
    @Override
    public int updateAcl(SysAclParam param) {
        // 查询这个权限点存在与否
        SysAcl sysAcl = mapper.selectByPrimaryKey(param.getId());
        if(sysAcl == null){
            // 要修改的权限点不存在
            throw new ParamException("你要改的权限点不存在了");
        }
        // 要修改的权限点存在
        // 查询修改之后的权限模块是否存在
        SysAclModule aclModule = moduleMapper.selectByPrimaryKey(param.getAclModuleId());
        if(aclModule == null){
            // 修改之后所属的权限模块不存在
            throw new ParamException("你要改的权限模块不存在");
        }
        // 查询修改后的权限模块下面是否已存在这个权限点，除开自己
        SysAcl aclByName = mapper.checkAclId(param.getName(), param.getAclModuleId(), param.getId());
        if(aclByName != null){
            throw new ParamException("权限点已存在");
        }
        // 封装SysAcl
        SysAcl acl = SysAcl.builder().
                aclModuleId(param.getAclModuleId()).
                name(param.getName()).
                remark(param.getRemark()).
                seq(param.getSeq()).
                status(param.getStatus()).
                type(param.getType()).
                url(param.getUrl()).
                id(param.getId()).build();
        acl.setOperateIp(IpUtil.getUserIP(RequestHold.getRequest()));
        acl.setOperator(RequestHold.getUser().getUsername());
        acl.setOperateTime(new Date());
        // 修改权限点
        int result = mapper.updateByPrimaryKeySelective(acl);
        // 保存修改日志
        logService.saveAclLog(sysAcl, acl);
        return result;
    }

    /**
     * 删除权限点
     * @param param
     * @return
     */
    @Override
    public int deleteAcl(SysAclParam param) {
        // 查询待删除的权限点是否存在
        SysAcl sysAcl = mapper.selectByPrimaryKey(param.getId());
        if(sysAcl==null){
            throw new ParamException("待删除的权限点不存在");
        }
        int result = mapper.deleteByPrimaryKey(param.getId());
        // 保存修改日志
        logService.saveAclLog(sysAcl, null);
        return result;
    }

    /**
     * 查询用户是否有权限
     * @param url
     * @return
     */
    @Override
    public boolean hasAcl(String url) {
        // 判断是否为超级用户
        if(isSuperUser(RequestHold.getUser())){
            return true;
        }
        // 存储根据url查询出来的权限点
        SysAcl acl = null;
        // 根据url查询权限点
        String urlAcl = jedisService.getCache(url, CacheKeyPrefix.USER);
        if(urlAcl!=null && urlAcl.equals("nothing")){
            return true;
        }
        if(urlAcl==null){
            // 从数据库中读取，并存入缓存
            acl = mapper.findAclByUrl(url);
            // 不能将null存入redis缓存
            if (acl != null){
                // 将对象转换成Json字符串
                urlAcl= JsonMapper.obj2String(acl);
                // 存入redis缓存
                jedisService.saveCache(url, urlAcl, 60*60*24, CacheKeyPrefix.USER);
            }else{
                // 存入redis缓存
                // 如果是无权限路径，就存值为nothing
                jedisService.saveCache(url, "nothing", 60*60*24, CacheKeyPrefix.USER);
            }
        }else {
            acl = JsonMapper.string2Obj(urlAcl, new TypeReference<SysAcl>() {});
        }
        if(acl == null){
            // 没有查找到权限点，即无权限也可以访问
            return true;
        }
        List<SysAcl> userAcls = null;
        // 查询到了权限点
        // 查询当前用户所拥有的权限点
        String value = jedisService.getCache(RequestHold.getUser().getUsername(), CacheKeyPrefix.USER);
        if(value != null && value.equals("nothing")){
            return false;
        }
        if(value == null){
            // 从数据库中读取，并存入redis缓存
            userAcls = getUserAcl();
            // 不能将null存入redis缓存
            if (userAcls != null && !userAcls.isEmpty()){
                // 转换成字符串
                value = JsonMapper.obj2String(userAcls);
                // 存入redis缓存
                jedisService.saveCache(RequestHold.getUser().getUsername(), value, 60*60*24, CacheKeyPrefix.USER);
            }else{
                // 存入redis缓存
                // 如果当前用户没有权限，就存入nothing
                jedisService.saveCache(url, "nothing", 60*60*24, CacheKeyPrefix.USER);
            }
        }else {
            userAcls = JsonMapper.string2Obj(value, new TypeReference<List<SysAcl>>() {});
        }
        if(userAcls != null && !userAcls.isEmpty()){
            boolean flag = false;
            for (SysAcl userAcl: userAcls) {
                if(acl.getId()==userAcl.getId()){
                    flag = true;
                    break;
                }
            }
            return flag;
        }
        return false;
    }

    /**
     * 查询当前用户所拥有的权限点
     * @return
     */
    public List<SysAcl> getUserAcl(){

        List<SysAcl> userAcls = Lists.newArrayList();
        // 查询当前用户所拥有的权限点
        // 1、根据用户id查询角色id
        List<Integer> roleIds = roleUserMapper.getRoleIdByUserId(RequestHold.getUser().getId());
        if(roleIds!=null && !roleIds.isEmpty()){
            // 2、根据角色id查询权限点id
            List<Integer> aclIds = roleAclMapper.getAclIdByRoleId(roleIds);
            if(aclIds!=null && !aclIds.isEmpty()){
                // 3、根据权限点id查询拥有的权限
                userAcls = mapper.findAclByIds(aclIds);
            }
        }
        return userAcls;
    }

    /**
     * 判断用户是否为超级用户
     * @param user
     * @return
     */
    private boolean isSuperUser(SysUser user) {

        if(user.getUsername().equals("Admin") && user.getId()==1){
            return true;
        }
        return false;
    }
}
