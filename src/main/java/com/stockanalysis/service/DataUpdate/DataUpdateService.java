package com.stockanalysis.service.DataUpdate;

import joinery.DataFrame;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

public class DataUpdateService {

    public static ConcurrentHashMap fileMap=new ConcurrentHashMap();

    public static Date analysisStartDate ;

    public static Date analysisEndDate;
    static String[] columns = {"代码",
            "名称",
            "选中日期",
            "选中当日涨跌幅",
            "选中1日涨跌幅",
            "选中2日涨跌幅",
            "选中3日涨跌幅",
            "选中4日涨跌幅",
            "选中5日涨跌幅",
            "选中6日涨跌幅",
            "选中7日涨跌幅",
            "选中8日涨跌幅",
            "选中9日涨跌幅",
            "选中10日涨跌幅"};

    public static DataFrame pickUpResult =new DataFrame(columns);

    public static DataFrame stockList;
}
