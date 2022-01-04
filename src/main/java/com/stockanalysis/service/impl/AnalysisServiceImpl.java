package com.stockanalysis.service.impl;

import com.stockanalysis.Thread.ThreadPool;
import com.stockanalysis.service.DataUpdate.DataUpdateService;
import com.stockanalysis.service.context.AnalysisContext;
import com.stockanalysis.service.context.CommonConstants;
import joinery.DataFrame;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

public class AnalysisServiceImpl {

    private DataUpdateService dataUpdateService = new DataUpdateService();

    public void getDataAfterPickUp(String analysisStartDate, String analysisEndDate) throws ParseException, IOException, ExecutionException, InterruptedException {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        AnalysisContext.analysisContext.put("analysisStartDate", format.parse(analysisStartDate));
        AnalysisContext.analysisContext.put("analysisEndDate", format.parse(analysisEndDate));
        DataFrame pickUpResult = new DataFrame(CommonConstants.ANALYSIS_RESULT_COLUMNS);
        AnalysisContext.analysisContext.put("pickUpResult", pickUpResult);

        dataUpdateService.setStockListInContext();

        File folder = new File(AnalysisContext.dataFolderPath + "\\all");
        File[] files = folder.listFiles();

        ThreadPoolExecutor threadPoolExecutor = ThreadPool.newThreadPool();
        int start = 0;
        int end = 0;
        int steplength = 200;
        int step = 1;
        while (end < files.length - 1) {
            List<Future> futureList = new ArrayList<>();
            end = steplength * step >= (files.length - 1) ? (files.length - 1) : steplength * step;
            for (int i = start; i <= end; i++) {
                String stockCode = files[i].getName().substring(0, files[i].getName().lastIndexOf("."));
                AnalysisSingleStockThread analysisSingleStockThread = new AnalysisSingleStockThread(stockCode);
                Thread thread = new Thread(analysisSingleStockThread);
                Future future = threadPoolExecutor.submit(thread);
                futureList.add(future);
            }
            for (Future future : futureList) {
                future.get();
            }
            System.gc();
            step++;
            start = end + 1;
        }
        pickUpResult = (DataFrame) AnalysisContext.analysisContext.get("pickUpResult");
        pickUpResult = pickUpResult.sortBy("选中日期");
        pickUpResult.writeCsv(AnalysisContext.dataFolderPath + "\\basics\\pickUpStockData.csv");
    }
}




