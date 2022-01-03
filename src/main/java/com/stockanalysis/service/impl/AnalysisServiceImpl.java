package com.stockanalysis.service.impl;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import scala.Function1;
import scala.runtime.Static;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

public class AnalysisServiceImpl implements Serializable {


    private static final long serialVersionUID = 5144518456495043542L;

    public void testGetCsv(Dataset<Row> dataset  ) {

        dataset.rdd().foreach(new functionTest());

    }

    private class functionTest implements Function1,Serializable
    {

        @Override
        public Object apply(Object v1) {
            Row row=(Row)v1;
            System.out.println((String) row.getAs("名称"));
            return null;
        }
    }

}




