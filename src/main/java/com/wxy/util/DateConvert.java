package com.wxy.util;

import com.wxy.exception.ParamException;

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class DateConvert {

    /**
     * 将字符串转换为日期格式
     * @param source
     * @return
     */
    public static Date toDate(String source){
        if(source==null){
            return null;
        }
        Date date = null;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            date = format.parse(source);
        } catch (ParseException e) {
            e.printStackTrace();
            throw new ParamException("日期格式有问题，应该为 yyyy-MM-dd HH:mm:ss");
        }

        return date;
    }
}
