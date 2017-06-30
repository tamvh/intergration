/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gbc.mc.common;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

/**
 *
 * @author diepth
 */
public class CommonFunction {
    
    protected static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(CommonFunction.class);
    private static String _currentDayFormat = "";

    public static String getCurrentDateTimeString() {
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT+7"));
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        fmt.setCalendar(cal);
        String currDateTime = fmt.format(cal.getTimeInMillis());
        
        return currDateTime;
    }
    
    public static String getCurrentDateTimeStringFormat(String format) {
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT+7"));
        SimpleDateFormat fmt = new SimpleDateFormat(format);
        fmt.setCalendar(cal);
        String currDateTime = fmt.format(cal.getTimeInMillis());
        
        return currDateTime;
    }
    
    public static long getCurrentDateTimeNum() {
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT+7"));
        return cal.getTimeInMillis();
    }
    
    public static String getCurrentDayFormat() {
        //if (_currentDayFormat.isEmpty()) {
            long currentTime = getCurrentDateTimeNum();
            SimpleDateFormat fmt = new SimpleDateFormat("yyMMdd");
            _currentDayFormat = fmt.format(currentTime);
        //}
        
        return _currentDayFormat;
    }
    
    public static Map<String, String> convertQueryMap(Map<String, String[]> paramMap) {
        Map<String, String> tempMap = new HashMap<>();
        paramMap.forEach( (key, values) -> {
            if (values.length == 0) {
                tempMap.put(key, "");
            } else {
                tempMap.put(key, values[0]);
            }
        });
        return tempMap;
    }
}
