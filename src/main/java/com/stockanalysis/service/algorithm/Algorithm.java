package com.stockanalysis.service.algorithm;

import joinery.DataFrame;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;

public abstract class Algorithm {

    public boolean pickUp(DataFrame singelStock, Date calculateDate) {
        boolean flag = true;
        try {
            Method[] methods = this.getClass().getDeclaredMethods();
            for (Method method : methods) {
                if (method.getName().indexOf("condition") == 0) {
                    Boolean ifPassConditon = null;
                    ifPassConditon = (Boolean) method.invoke(this, singelStock, calculateDate);
                    if (!ifPassConditon) {
                        flag = false;
                        break;
                    }
                }
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            flag = false;
            e.printStackTrace();
            System.out.println(calculateDate);
        }
        return flag;
    }
}
