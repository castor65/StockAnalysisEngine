package com.stockanalysis.service.impl;

import com.clearspring.analytics.util.Lists;
import com.stockanalysis.service.DataUpdate.DataUpdateService;
import com.stockanalysis.service.DataUpdate.ReadSingelFileThread;
import com.stockanalysis.service.algorithm.Algorithm;
import joinery.DataFrame;
import lombok.SneakyThrows;
import org.apache.arrow.flatbuf.DateUnit;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;

public class AnalysisSingleStockThread implements Runnable {
     String[] columns = {"代码",
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
    public String stockNo;

    public String getStockNo() {
        return stockNo;
    }

    public void setStockNo(String stockNo) {
        this.stockNo = stockNo;
    }

    public DataFrame stockList;
    public DataFrame singelStockGloble;

    public DataFrame analysisResult = new DataFrame(columns);

    public Algorithm algorithm=new Algorithm();

    public void getDataAfterPickUp() throws IOException {
        String path = "D:\\PyCharmWorkSpace\\StockAnalysisProject.git\\all\\" + stockNo + ".csv";
        singelStockGloble = DataFrame.readCsv(path);
        stockList = DataUpdateService.stockList;
         Date analysisStartDate = DataUpdateService.analysisStartDate;

        Date analysisEndDate = DataUpdateService.analysisEndDate;

        Date calculateDate = analysisStartDate;


//        int poolSize = Runtime.getRuntime().availableProcessors() * 2;
//        BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(512);
//        RejectedExecutionHandler policy = new ThreadPoolExecutor.DiscardPolicy();
//        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(poolSize, poolSize, 0, TimeUnit.SECONDS, queue, policy);
//
//        List<Future> futureList = Lists.newArrayList();

        while (calculateDate.before(analysisEndDate)) {

            getDataAfterPickUpForSingleDay(calculateDate);

            Calendar c = Calendar.getInstance();
            c.setTime(calculateDate);
            c.add(Calendar.DAY_OF_MONTH, 1);
            calculateDate = c.getTime();


        }
        synchronized (this)
        {
            DataUpdateService.pickUpResult=DataUpdateService.pickUpResult.concat(analysisResult);
            System.out.println(stockNo+"succeed");
        }
    }

    private void getDataAfterPickUpForSingleDay(Date calculateDate) {

        boolean isPickUp = algorithm.pickUp(singelStockGloble, calculateDate);

         DataFrame singleStockPickUpData;
        if (isPickUp){
             singleStockPickUpData = generateSingleStockPickUpData(calculateDate);

            analysisResult = analysisResult.concat(singleStockPickUpData);
         }



    }

    private DataFrame generateSingleStockPickUpData(Date calculateDate) {
        //get stockName from basics stockList
        String stockName = (String) stockList.select(new DataFrame.Predicate<Object>() {
            @Override
            public Boolean apply(List<Object> values) {
                String code=String.format("%06d", values.get(1));
                return code.equalsIgnoreCase(stockNo);
            }
        }).col("名称").get(0);

        //get 10d ays data after been picked up include the day been pickUp
        DataFrame singleStock = singelStockGloble.select(new DataFrame.Predicate<Object>() {
            @SneakyThrows
            @Override
            public Boolean apply(List<Object> row) {
                DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                Date date = (Date)row.get(0);
                return date.after(calculateDate) || date.equals(calculateDate);
            }
        });
        singleStock.sortBy("日期");

        singleStock = singleStock.slice(0, 11);
        DataFrame singleStockPickUpData = new DataFrame(columns);

        singleStockPickUpData = singleStockPickUpData.append(

                Arrays.asList(new Object[]{
                        stockNo,
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
        }catch (Exception e)
        {
            System.out.println(stockNo+ "Failed");
        }
    }
}