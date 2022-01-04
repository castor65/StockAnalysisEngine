package com.stockanalysis.service.impl;

import com.stockanalysis.service.DataUpdate.DataUpdateService;
import com.stockanalysis.service.algorithm.Algorithm;
import com.stockanalysis.service.context.AnalysisContext;
import com.stockanalysis.service.context.CommonConstants;
import joinery.DataFrame;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class Maintest {

    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException, ParseException, InvocationTargetException, IllegalAccessException, ClassNotFoundException, InstantiationException, NoSuchMethodException {
//
//        AnalysisServiceImpl analysisService=new AnalysisServiceImpl();
//
//        SparkConf conf = new SparkConf().setMaster("local").setAppName("TestSpark");
//        SparkContext sparkContext = new SparkContext(conf);
//        SparkSession sparkSession = new SparkSession(sparkContext);
//
//        SQLContext sqlContext = new SQLContext(sparkSession);
//        HashMap<String, String> options = new HashMap<String, String>();
//        options.put("header", "true");//no load header
//        options.put("inferSchema", "true");//no load header

//        Dataset<Row> dataset = sqlContext.read().options(options).csv("D:\\PyCharmWorkSpace\\StockAnalysisProject.git\\basics\\pickUpStockData.csv");


//SingleOne Stock for debug
//        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
//        AnalysisContext.analysisContext.put("analysisStartDate", format.parse("2021-01-01"));
//        AnalysisContext.analysisContext.put("analysisEndDate", format.parse("2021-12-01"));
//        DataUpdateService dataUpdateService = new DataUpdateService();
//        dataUpdateService.setStockListInContext();
//        DataFrame pickUpResult =new DataFrame(CommonConstants.ANALYSIS_RESULT_COLUMNS);
//        AnalysisContext.analysisContext.put("pickUpResult",pickUpResult);
//
//        AnalysisSingleStockThread analysisSingleStockThread=new AnalysisSingleStockThread("688778");
//         analysisSingleStockThread.getDataAfterPickUp();
//
//        pickUpResult = (DataFrame) AnalysisContext.analysisContext.get("pickUpResult");
//        pickUpResult=pickUpResult.sortBy("选中日期");
//        pickUpResult.writeCsv("./pickUpStockData.csv");


// test
        AnalysisServiceImpl analysisService=new AnalysisServiceImpl();
        analysisService.getDataAfterPickUp("2021-01-01","2021-12-20");


    }


}
