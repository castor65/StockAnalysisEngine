package com.stockanalysis.service.impl;

import com.stockanalysis.Util.CommonUtil;
import com.stockanalysis.service.algorithm.Algorithm;
import com.stockanalysis.service.context.AnalysisContext;
import com.stockanalysis.service.context.CommonConstants;
import joinery.DataFrame;
import lombok.SneakyThrows;

import java.util.*;

public class AnalysisSingleStockThread implements Runnable {

    private String stockCode;
    private String stockName;
    private DataFrame stockList;
    private DataFrame singelStockGloble;
    private DataFrame analysisResultForSingleStock = new DataFrame(CommonConstants.ANALYSIS_RESULT_COLUMNS);
    private Algorithm algorithm;

    public AnalysisSingleStockThread(String stockCode) {
        this.stockCode = stockCode;
    }

    public void getDataAfterPickUp() {

        try {
            String path = AnalysisContext.dataFolderPath + "\\all\\" + stockCode + ".csv";
            singelStockGloble = DataFrame.readCsv(path);
            //sort by date desc
            singelStockGloble = singelStockGloble.sortBy(new Comparator<List>() {
                @Override
                public int compare(List o1, List o2) {
                    Date date1 = (Date) o1.get(0);
                    Date date2 = (Date) o2.get(0);
                    if (date2.after(date1)) {
                        return 0;
                    }
                    return -1;
                }
            });
            stockList = (DataFrame) AnalysisContext.analysisContext.get("StockList");
            Date analysisStartDate = (Date) AnalysisContext.analysisContext.get("analysisStartDate");
            Date analysisEndDate = (Date) AnalysisContext.analysisContext.get("analysisEndDate");
            Date calculateDate = analysisStartDate;

            stockName = (String) stockList.select(new DataFrame.Predicate<Object>() {
                @Override
                public Boolean apply(List<Object> values) {
                    String code = String.format("%06d", values.get(1));
                    return code.equalsIgnoreCase(stockCode);
                }
            }).col("名称").get(0);

            if (stockName.indexOf("ST") == 0 || stockName.indexOf("*ST") == 0) {
                throw new Exception("no ST stock");
            }

            Class c = Class.forName(AnalysisContext.currentAlgorithm);
            algorithm = (Algorithm) c.getDeclaredConstructor().newInstance();

            while (calculateDate.before(analysisEndDate)) {
                getDataAfterPickUpForSingleDay(calculateDate);
                calculateDate = CommonUtil.addDay(calculateDate, 1);
            }
            synchronized (this) {
                DataFrame pickUpResult = (DataFrame) AnalysisContext.analysisContext.get("pickUpResult");
                pickUpResult = pickUpResult.concat(analysisResultForSingleStock);
                AnalysisContext.analysisContext.put("pickUpResult", pickUpResult);
                System.out.println(stockCode + "succeed");
            }
        } catch (Exception e) {
            System.out.println(stockCode + "Failed");
        } finally {
            singelStockGloble = null;
            analysisResultForSingleStock = null;
        }

    }

    private void getDataAfterPickUpForSingleDay(Date calculateDate) {
        DataFrame singleStock = singelStockGloble.select(new DataFrame.Predicate<Object>() {
            @SneakyThrows
            @Override
            public Boolean apply(List<Object> row) {
                Date date = (Date) row.get(0);
                return date.before(calculateDate) || date.equals(calculateDate);
            }
        });
        if (singleStock.col("日期").size() == 0) {
            return;
        }
        //if the first one is not equals calculateDate means today don't have data, then pass this one .
        Date currentDay = (Date) singleStock.col("日期").get(0);
        if (!currentDay.equals(calculateDate)) {
            return;
        }
        boolean isPickUp = algorithm.pickUp(singleStock, calculateDate);
        if (isPickUp) {
            DataFrame singleStockPickUpData = generateSingleStockPickUpData(calculateDate);
            analysisResultForSingleStock = analysisResultForSingleStock.concat(singleStockPickUpData);
        }
    }

    private DataFrame generateSingleStockPickUpData(Date calculateDate) {
        //get 10d ays data after been picked up include the day been pickUp
        DataFrame singleStock = singelStockGloble.select(new DataFrame.Predicate<Object>() {
            @SneakyThrows
            @Override
            public Boolean apply(List<Object> row) {
                Date date = (Date) row.get(0);
                return date.after(calculateDate) || date.equals(calculateDate);
            }
        });
        singleStock = singleStock.sortBy("日期");

        singleStock = singleStock.slice(0, 31);
        DataFrame singleStockPickUpData = new DataFrame(CommonConstants.ANALYSIS_RESULT_COLUMNS);

        ArrayList<Object> pickUpdataList= new ArrayList(Arrays.asList(new Object[]{
                stockCode,
                stockName,
                singleStock.slice(0, 1).col("日期").get(0),//"选中日期"

        }));
        for(int i=0;i<=30;i++)
        {
            pickUpdataList.add( singleStock.slice(i, i+1).col("涨跌幅").get(0));
        }
        singleStockPickUpData = singleStockPickUpData.append(pickUpdataList);




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