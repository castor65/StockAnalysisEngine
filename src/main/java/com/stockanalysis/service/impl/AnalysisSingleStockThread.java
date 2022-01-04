package com.stockanalysis.service.impl;

import com.stockanalysis.Util.CommonUtil;
import com.stockanalysis.service.algorithm.Algorithm;
import com.stockanalysis.service.context.AnalysisContext;
import com.stockanalysis.service.context.CommonConstants;
import joinery.DataFrame;
import lombok.SneakyThrows;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class AnalysisSingleStockThread implements Runnable {

    private String stockCode;
    private DataFrame stockList;
    private DataFrame singelStockGloble;
    private DataFrame analysisResultForSingleStock = new DataFrame(CommonConstants.ANALYSIS_RESULT_COLUMNS);
    private Algorithm algorithm = new Algorithm();
    public  AnalysisSingleStockThread(String stockCode)
    {
        this.stockCode=stockCode;
    }

    public void getDataAfterPickUp() throws IOException {
        String path = AnalysisContext.dataFolderPath+"\\all\\" + stockCode + ".csv";
        singelStockGloble = DataFrame.readCsv(path);
        stockList = (DataFrame) AnalysisContext.analysisContext.get("StockList");
        Date analysisStartDate =(Date)AnalysisContext.analysisContext.get("analysisStartDate");
        Date analysisEndDate = (Date)AnalysisContext.analysisContext.get("analysisEndDate");
        Date calculateDate = analysisStartDate;

        while (calculateDate.before(analysisEndDate)) {
            getDataAfterPickUpForSingleDay(calculateDate);
            calculateDate = CommonUtil.addDay(calculateDate,1);
        }
        synchronized (this) {
            DataFrame pickUpResult = (DataFrame) AnalysisContext.analysisContext.get("pickUpResult");
            pickUpResult=pickUpResult.concat(analysisResultForSingleStock);
            AnalysisContext.analysisContext.put("pickUpResult",pickUpResult);
            System.out.println(stockCode + "succeed");
        }
        singelStockGloble=null;
        analysisResultForSingleStock=null;
    }

    private void getDataAfterPickUpForSingleDay(Date calculateDate) {
        boolean isPickUp = algorithm.pickUp(singelStockGloble, calculateDate);
        if (isPickUp) {
            DataFrame singleStockPickUpData = generateSingleStockPickUpData(calculateDate);
            analysisResultForSingleStock = analysisResultForSingleStock.concat(singleStockPickUpData);
        }
    }

    private DataFrame generateSingleStockPickUpData(Date calculateDate) {
        //get stockName from basics stockList
        String stockName = (String) stockList.select(new DataFrame.Predicate<Object>() {
            @Override
            public Boolean apply(List<Object> values) {
                String code = String.format("%06d", values.get(1));
                return code.equalsIgnoreCase(stockCode);
            }
        }).col("名称").get(0);

        //get 10d ays data after been picked up include the day been pickUp
        DataFrame singleStock = singelStockGloble.select(new DataFrame.Predicate<Object>() {
            @SneakyThrows
            @Override
            public Boolean apply(List<Object> row) {
                DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                Date date = (Date) row.get(0);
                return date.after(calculateDate) || date.equals(calculateDate);
            }
        });
        singleStock.sortBy("日期");

        singleStock = singleStock.slice(0, 11);
        DataFrame singleStockPickUpData = new DataFrame(CommonConstants.ANALYSIS_RESULT_COLUMNS);

        singleStockPickUpData = singleStockPickUpData.append(
                Arrays.asList(new Object[]{
                        stockCode,
                        stockName,
                        singleStock.slice(0, 1).col("日期").get(0),//"选中日期"
                        singleStock.slice(0, 1).col("涨跌幅").get(0), //"选中当日涨跌幅":
                        singleStock.slice(1, 2).col("涨跌幅").get(0),     // "选中1日涨跌幅":
                        singleStock.slice(2, 3).col("涨跌幅").get(0), //  "选中2日涨跌幅":
                        singleStock.slice(3, 4).col("涨跌幅").get(0),   //  "选中3日涨跌幅":
                        singleStock.slice(4, 5).col("涨跌幅").get(0), //  "选中4日涨跌幅":
                        singleStock.slice(5, 6).col("涨跌幅").get(0),//   "选中5日涨跌幅":
                        singleStock.slice(6, 7).col("涨跌幅").get(0),   //  "选中6日涨跌幅":
                        singleStock.slice(7, 8).col("涨跌幅").get(0), //  "选中7日涨跌幅":
                        singleStock.slice(8, 9).col("涨跌幅").get(0), //  "选中8日涨跌幅":
                        singleStock.slice(9, 10).col("涨跌幅").get(0),  //  "选中9日涨跌幅":
                        singleStock.slice(10, 11).col("涨跌幅").get(0)  //  "选中10日涨跌幅"
                }));

        return singleStockPickUpData;
    }


    @Override
    public void run() {
        try {
            this.getDataAfterPickUp();
        } catch (Exception e) {
            System.out.println(stockCode + "Failed");
        }
    }


    public String getStockCode() {
        return stockCode;
    }

    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
    }
}