package com.stockanalysis.service.DataUpdate;

import joinery.DataFrame;

import java.io.IOException;

public class ReadSingelFileThread implements Runnable {

    private String path;
    private String name;

    public ReadSingelFileThread(String name,String path)
    {
        this.path=path;
        this.name=name;
    }

    @Override
    public void run() {
        System.out.println(path);
        DataFrame<Object> df= null;
        try {
            df = DataFrame.readCsv(path);
            DataUpdateService.fileMap.put(name,df);
            df=null;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
