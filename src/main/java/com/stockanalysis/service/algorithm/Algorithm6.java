package com.stockanalysis.service.algorithm;

import joinery.DataFrame;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

/**
 * A1:=前三个交易日 涨幅大于6
 * A2:=前日涨幅大于0
 * A3:=前日收盘低于开盘
 * A4:=本日-5~3
 */
public class Algorithm6 extends Algorithm {
    /**
     A1:=前二个交易日 涨幅大于6
     * A2:=本日涨幅大于0
     */
    public boolean condition1(DataFrame singelStock, Date calculateDate) {
        if (singelStock.index().size() < 4) {
            return false;
        }
        Boolean flag = false;
        DataFrame singleStockInCondition = singelStock.slice(0, 4);

        BigDecimal rate_today = new BigDecimal(singleStockInCondition.col("涨跌幅").get(0).toString());
        BigDecimal rate_yesterday = new BigDecimal(singleStockInCondition.col("涨跌幅").get(1).toString());
        BigDecimal rate1 = new BigDecimal(singleStockInCondition.col("涨跌幅").get(2).toString());
        BigDecimal rate2 = new BigDecimal(singleStockInCondition.col("涨跌幅").get(3).toString());

        BigDecimal conditionValue = new BigDecimal(0);
        BigDecimal conditionValue1 = new BigDecimal(6);
        boolean flag1=false;
        if(rate_today.compareTo(new BigDecimal(-5))==1&&rate_today.compareTo(new BigDecimal(3))==-1)
        {
            flag1=true;
        }
        boolean flag2=false;

        if (rate_yesterday.compareTo(conditionValue)==1&&rate1.compareTo(conditionValue1) == 1 && rate2.compareTo(conditionValue1) == 1) {//a.compareTo(b) == 1 a大于b a.compareTo(b) == -1 a小于b
            flag2 = true;
        }
        flag=flag1&&flag2;
        return flag;
    }

    /**
     * A11:=本日收盘价小于开盘价
     */
    public boolean condition2(DataFrame singelStock, Date calculateDate) {
        if (singelStock.index().size() < 2) {
            return false;
        }
        Boolean flag = false;

        DataFrame singleStockInCondition = singelStock.slice(1, 2);
        BigDecimal todayopen = new BigDecimal(singleStockInCondition.col("开盘").get(0).toString());
        BigDecimal todayClose = new BigDecimal(singleStockInCondition.col("收盘").get(0).toString());

        if (todayClose.compareTo(todayopen) == -1) { //a.compareTo(b) == -1 a小于b
            flag = true;
        }
        return flag;
    }
}


