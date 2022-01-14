package com.stockanalysis.service.algorithm;

import joinery.DataFrame;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

/**
  5日线 上 越过20 日线 且本日收盘价在此之上
 */
public class Algorithm5 extends Algorithm {
    /**
     *   5日线 上 越过20 日线 且本日收盘价在此之上
     */
    public boolean condition1(DataFrame singelStock, Date calculateDate) {
        if (singelStock.index().size() < 70) {
            return false;
        }
        Boolean flag = false;
        //本日5日线>60日线
        //本日收盘价大于5日线
        //连续10日5日线小于60日线
        BigDecimal today_AvgMt5=avgMt5(0,singelStock);
        BigDecimal today_AvgMt60=avgMt60(0,singelStock);

        BigDecimal today_Close = new BigDecimal(singelStock.col("收盘").get(0).toString());
        Boolean condition1Flag=false;
        Boolean condition2Flag=false;
        Boolean condition3Flag=true;

        if (today_AvgMt5.compareTo(today_AvgMt60)==1)
        {
            condition1Flag=true;
        }
        if (today_Close.compareTo(today_AvgMt5)==1)
        {
            condition2Flag=true;

        }

        for(int i=1;i<=10;i++)
        {
            BigDecimal day_AvgMt5=avgMt5(i,singelStock);
            BigDecimal day_AvgMt60=avgMt60(i,singelStock);

            if(day_AvgMt5.compareTo(day_AvgMt60)==1)
            {
                condition3Flag=false;
                break;
            }
        }

        flag=condition1Flag&&condition2Flag&&condition3Flag;

        return flag;
    }
    private  BigDecimal avgMt5(int i,DataFrame singleStock)
    {
        DataFrame singleStockLocal=singleStock.slice(i,i+5);

        BigDecimal sumAmt = new BigDecimal(singleStockLocal.sum().col("收盘").get(0).toString());
        BigDecimal avgMt5=sumAmt.divide(new BigDecimal(5),4,RoundingMode.HALF_UP);
        return  avgMt5;
    }

    private  BigDecimal avgMt60(int i,DataFrame singleStock)
    {
        DataFrame singleStockLocal=singleStock.slice(i,i+60);

        BigDecimal sumAmt = new BigDecimal(singleStockLocal.sum().col("收盘").get(0).toString());
        BigDecimal avgMt5=sumAmt.divide(new BigDecimal(60),4,RoundingMode.HALF_UP);
        return  avgMt5;
    }

}


