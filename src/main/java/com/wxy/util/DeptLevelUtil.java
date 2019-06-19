package com.wxy.util;

public class DeptLevelUtil {

    // 一级部门权限等级
    public final static String ROOT = "0";
    // 分隔符
    public final static String separator = ".";

    public static String getDeptLevel(String parentLevel, Integer parentId){
        if(parentLevel == null){
            return ROOT;
        }
        return parentLevel+separator+parentId;
    }
}
