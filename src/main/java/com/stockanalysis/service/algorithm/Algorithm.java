package com.stockanalysis.service.algorithm;

import joinery.DataFrame;
import lombok.SneakyThrows;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Algorithm {

    public boolean pickUp(DataFrame singelStockGloble, Date calculateDate) {
        boolean flag = false;

        DataFrame singleStock = singelStockGloble.select(new DataFrame.Predicate<Object>() {
            @SneakyThrows
            @Override
            public Boolean apply(List<Object> row) {
                DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                Date date = (Date) row.get(0);
                return date.equals(calculateDate);
            }
        });
        if (singleStock.col("涨跌幅").size() > 0) {
            if (Float.parseFloat(String.valueOf(singleStock.col("涨跌幅").get(0))) > 9.9) {
                flag = true;
            }
        }

        return flag;
    }
}
