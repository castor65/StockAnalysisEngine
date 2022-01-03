package com.stockanalysis.service.impl;

import com.clearspring.analytics.util.Lists;
import com.stockanalysis.service.DataUpdate.DataUpdateService;
import com.stockanalysis.service.DataUpdate.ReadSingelFileThread;
import joinery.DataFrame;
import org.apache.commons.net.ntp.TimeStamp;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.rdd.RDD;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.execution.datasources.FilePartition;
import scala.Function1;
import scala.Tuple2;
import scala.runtime.BoxedUnit;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.*;

public class Maintest   {

    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException, ParseException {
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





        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        DataUpdateService.analysisStartDate = format.parse("2021-01-01");
        DataUpdateService.analysisEndDate =format.parse("2021-12-01");
        DataFrame stockList = DataFrame.readCsv("D:\\PyCharmWorkSpace\\StockAnalysisProject.git\\basics\\stock_basics.csv");

        DataUpdateService.stockList=stockList;
        File folder = new File("D:\\PyCharmWorkSpace\\StockAnalysisProject.git\\all");
        List<String> fileNameList=new ArrayList<>();
        File[] files = folder.listFiles();
        HashMap<String,DataFrame<Object>> datasetHashMap=new HashMap<>();
        System.out.println(new Date());
         int poolSize= Runtime.getRuntime().availableProcessors()*2;
        BlockingQueue<Runnable> queue=new ArrayBlockingQueue<>(512);
         RejectedExecutionHandler policy=new ThreadPoolExecutor.DiscardPolicy();
        ThreadPoolExecutor threadPoolExecutor=new ThreadPoolExecutor(poolSize,poolSize,0,TimeUnit.SECONDS,queue,policy);
        int start=0;
        int end=0;
        int steplength=50;
        int step=1;
        while(end<files.length-1){
            List<Future> futureList= Lists.newArrayList();

            end=steplength*step>=(files.length-1)?(files.length-1):steplength*step;
            for (int i= start;i<=end; i++) {

                String stockCode=files[i].getName().substring(0,files[i].getName().lastIndexOf("."));
                 AnalysisSingleStockThread analysisSingleStockThread=new AnalysisSingleStockThread();
                analysisSingleStockThread.setStockNo(stockCode);

                Thread thread = new Thread(analysisSingleStockThread);

                Future future = threadPoolExecutor.submit(thread);
                futureList.add(future);
           }
           for(Future future :futureList)
           {
               future.get();
           }
           System.gc();
            step++;
            start=end+1;
        }


        System.out.println(new Date());


//        System.out.println("Start: "+new Date());
//        AnalysisSingleStockThread analysisSingleStockThread=new AnalysisSingleStockThread();
//        analysisSingleStockThread.setStockNo("000544");
//        analysisSingleStockThread.getDataAfterPickUp();






        DataUpdateService.pickUpResult.writeCsv("./pickUpStockData.csv");


}


}
