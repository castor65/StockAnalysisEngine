package com.stockanalysis.service.context;

import java.util.concurrent.ConcurrentHashMap;

public class AnalysisContext {
    public static String dataFolderPath = "D:\\PyCharmWorkSpace\\StockAnalysisProject.git";
    public static ConcurrentHashMap<String, Object> analysisContext = new ConcurrentHashMap();
    public static String currentAlgorithm = "com.stockanalysis.service.algorithm.Algorithm1";
}
