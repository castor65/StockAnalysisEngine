package com.stockanalysis.service.algorithm;

import joinery.DataFrame;
import lombok.SneakyThrows;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Algorithm {

    public boolean pickUp(DataFrame singelStock, Date calculateDate) {
        boolean flag = true;
        try {
            Method[] methods = this.getClass().getDeclaredMethods();
            for (Method method : methods) {
                if (method.getName().indexOf("condition") == 0) {
                    Boolean ifPassConditon = null;
                    ifPassConditon = (Boolean) method.invoke(this, singelStock,calculateDate);
                    if (!ifPassConditon) {
                        flag = false;
                        break;
                    }
                }
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            flag = false;
            e.printStackTrace();
        }
        return flag;
    }
    private boolean condition1(DataFrame singelStock,Date calculateDate) {
        Boolean flag = false;
        DataFrame singleStockInCondition = singelStock.select(new DataFrame.Predicate<Object>() {
            @SneakyThrows
            @Override
            public Boolean apply(List<Object> row) {
                DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                Date date = (Date) row.get(0);
                return date.equals(calculateDate);
            }
        });

        if (singleStockInCondition.col("涨跌幅").size() > 0) {
            if (Float.parseFloat(String.valueOf(singleStockInCondition.col("涨跌幅").get(0))) > 9.9) {
                flag = true;
            }
        }
        return flag;
    }
}
