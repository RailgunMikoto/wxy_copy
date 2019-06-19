package com.wxy.mapper;

import com.wxy.beans.PageQuery;
import com.wxy.entity.SysLog;
import com.wxy.entity.SysLogWithBLOBs;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysLogMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SysLogWithBLOBs record);

    int insertSelective(SysLogWithBLOBs record);

    SysLogWithBLOBs selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysLogWithBLOBs record);

    int updateByPrimaryKeyWithBLOBs(SysLogWithBLOBs record);

    int updateByPrimaryKey(SysLog record);

    // 获取分页数据
    List<SysLogWithBLOBs> findPage(PageQuery query);

    // 获取总条数
    int getCount(PageQuery query);
}