package com.stockanalysis.service.algorithm;

import joinery.DataFrame;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

/**
 * A1:=前一个交易日 成交额大于12亿
 * A2:=最近8个交易日涨跌幅大于30%
 * A3:=今天最低价小于前一个交易日收盘价
 * A4:=最近10个交易日日均成交额大于6亿
 * A5:=前一个交易日量比小于2
 * A6:=收盘价小于90
 * A7:=最近8个交易日的涨跌幅大于0出现次数>5
 * A8:=非ST IF(NAMELIKE('ST') OR NAMELIKE('*ST'),0,1);
 * A9:=前一个交易日涨幅大于2%小于8%
 * A10:=本日跌幅0%~9%
 * A11:=本日收盘价小于最高价
 */
public class Algorithm2 extends Algorithm {
    /**
     * A1:=前一个交易日 成交额大于12亿
     */
    public boolean condition1(DataFrame singelStock, Date calculateDate) {
        if (singelStock.index().size() < 2) {
            return false;
        }
        Boolean flag = false;
        DataFrame singleStockInCondition = singelStock.slice(1, 2);
        BigDecimal value = new BigDecimal(singelStock.col("成交额").get(0).toString());
        BigDecimal conditionValue = new BigDecimal(1200000000);
        if (value.compareTo(conditionValue) > -1)// a.compareTo(b) > -1 a >=b
        {
            flag = true;
        }
        return flag;
    }

    /**
     * A2:=最近8个交易日涨跌幅大于30%
     */
    public boolean condition2(DataFrame singelStock, Date calculateDate) {
        if (singelStock.index().size() < 8) {
            return false;
        }
        Boolean flag = false;
        DataFrame singleStockInCondition = singelStock.slice(0, 8);

        BigDecimal sumChangeRate = new BigDecimal(singleStockInCondition.sum().col("涨跌幅").get(0).toString());
        BigDecimal conditionValue = new BigDecimal(30);
        if (sumChangeRate.compareTo(conditionValue) > -1)// a.compareTo(b) > -1 a >=b
        {
            flag = true;
        }
        return flag;
    }

    /**
     * A3:=今天最低价小于前一个交易日收盘价
     */
    public boolean condition3(DataFrame singelStock, Date calculateDate) {
        if (singelStock.index().size() < 2) {
            return false;
        }
        Boolean flag = false;

        DataFrame singleStockInCondition = singelStock.slice(0, 2);
        BigDecimal todayLow = new BigDecimal(singleStockInCondition.slice(0, 1).col("最低").get(0).toString());
        BigDecimal yesterdayClose = new BigDecimal(singleStockInCondition.slice(1, 2).col("收盘").get(0).toString());

        if (todayLow.compareTo(yesterdayClose) == -1) { //a.compareTo(b) == -1 a小于b
            flag = true;
        }
        return flag;
    }

    /**
     * A4:=最近10个交易日日均成交额大于6亿
     */
    public boolean condition4(DataFrame singelStock, Date calculateDate) {
        if (singelStock.index().size() < 10) {
            return false;
        }
        Boolean flag = false;

        DataFrame singleStockInCondition = singelStock.slice(0, 10);
        BigDecimal avgAmount = new BigDecimal(singleStockInCondition.sum().col("成交额").get(0).toString()).divide(new BigDecimal(10));
        BigDecimal conditionValue = new BigDecimal(600000000);

        if (avgAmount.compareTo(conditionValue) > -1)// a.compareTo(b) > -1 a >=b
        {
            flag = true;
        }
        return flag;
    }

    /**
     * A5:=前一个交易日量比小于2
     * 量比=（现成交总手数 / 现累计开市时间(分) ）/ 过去5日平均每分钟成交量
     * 化简可得 5*本日成交量/前5日成交量总和
     */
    public boolean condition5(DataFrame singelStock, Date calculateDate) {
        if (singelStock.index().size() < 7) {
            return false;
        }
        Boolean flag = false;

        DataFrame singleStockInCondition = singelStock.slice(1, 7);

        BigDecimal yesterdayVolume = new BigDecimal(singleStockInCondition.col("成交量").get(0).toString());
        BigDecimal fiveDayBeforeVolum = new BigDecimal(singleStockInCondition.slice(1, 6).sum().col("成交量").get(0).toString());
        BigDecimal QRR = yesterdayVolume.multiply(new BigDecimal(5)).divide(fiveDayBeforeVolum, 4, RoundingMode.HALF_UP);
        BigDecimal conditionValue = new BigDecimal(2);
        if (QRR.compareTo(conditionValue) == -1) {//a.compareTo(b) == -1 a小于b
            flag = true;
        }
        return flag;
    }

    /**
     * A6:=收盘价小于90
     */
    public boolean condition6(DataFrame singelStock, Date calculateDate) {
        if (singelStock.index().size() < 1) {
            return false;
        }
        Boolean flag = false;
        DataFrame singleStockInCondition = singelStock.slice(0, 1);
        BigDecimal close = new BigDecimal(singleStockInCondition.col("收盘").get(0).toString());
        BigDecimal conditionValue = new BigDecimal(90);
        if (close.compareTo(conditionValue) == -1) {//a.compareTo(b) == -1 a小于b
            flag = true;
        }
        return flag;
    }

    /**
     * A7:=最近8个交易日的涨跌幅大于0出现次数>5
     */
    public boolean condition7(DataFrame singelStock, Date calculateDate) {
        if (singelStock.index().size() < 8) {
            return false;
        }
        Boolean flag = false;
        DataFrame singleStockInCondition = singelStock.slice(0, 8);
        int count = 0;
        List<Double> ratelist = singleStockInCondition.col("涨跌幅");
        for (Double rate : ratelist) {
            if (rate > 0) {
                count++;
            }
        }
        if (count > 5) {
            flag = true;
        }
        return flag;
    }

    /**
     * A8:=非ST IF(NAMELIKE('ST') OR NAMELIKE('*ST'),0,1) always be true
     */

    /**
     * A9:=前一个交易日涨幅大于2%小于8%
     */
    public boolean condition9(DataFrame singelStock, Date calculateDate) {
        if (singelStock.index().size() < 2) {
            return false;
        }
        Boolean flag = false;

        DataFrame singleStockInCondition = singelStock.slice(1, 2);
        BigDecimal rate = new BigDecimal(singleStockInCondition.col("涨跌幅").get(0).toString());
        BigDecimal conditionValue1 = new BigDecimal(2);
        BigDecimal conditionValue2 = new BigDecimal(8);

        if (rate.compareTo(conditionValue1) == 1 && rate.compareTo(conditionValue2) == -1) {//a.compareTo(b) == 1 a大于b a.compareTo(b) == -1 a小于b
            flag = true;
        }
        return flag;
    }

    /**
     * A10:=本日跌幅0%~9%
     */
    public boolean condition10(DataFrame singelStock, Date calculateDate) {
        if (singelStock.index().size() < 1) {
            return false;
        }
        Boolean flag = false;
        DataFrame singleStockInCondition = singelStock.slice(0, 1);

        BigDecimal rate = new BigDecimal(singleStockInCondition.col("涨跌幅").get(0).toString());
        BigDecimal conditionValue1 = new BigDecimal(0);
        BigDecimal conditionValue2 = new BigDecimal(9);

        if (rate.compareTo(conditionValue1) == 1 && rate.compareTo(conditionValue2) == -1) {//a.compareTo(b) == 1 a大于b a.compareTo(b) == -1 a小于b
            flag = true;
        }
        return flag;
    }

    /**
     * A11:=本日收盘价小于开盘价
     */
    public boolean condition11(DataFrame singelStock, Date calculateDate) {
        if (singelStock.index().size() < 2) {
            return false;
        }
        Boolean flag = false;

        DataFrame singleStockInCondition = singelStock.slice(0, 1);
        BigDecimal todayopen = new BigDecimal(singleStockInCondition.col("开盘").get(0).toString());
        BigDecimal todayClose = new BigDecimal(singleStockInCondition.col("收盘").get(0).toString());

        if (todayClose.compareTo(todayopen) == -1) { //a.compareTo(b) == -1 a小于b
            flag = true;
        }
        return flag;
    }
}


