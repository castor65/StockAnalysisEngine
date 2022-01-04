package com.stockanalysis.service.DataUpdate;

import com.stockanalysis.service.context.AnalysisContext;
import joinery.DataFrame;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

public class DataUpdateService {

    public void setStockListInContext() throws IOException {
        DataFrame stockList = DataFrame.readCsv(AnalysisContext.dataFolderPath+"\\basics\\stock_basics.csv");
        AnalysisContext.analysisContext.put("StockList",stockList);

    }

}
