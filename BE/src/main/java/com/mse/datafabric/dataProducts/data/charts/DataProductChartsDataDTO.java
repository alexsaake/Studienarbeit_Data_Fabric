package com.mse.datafabric.dataProducts.data.charts;

import java.util.ArrayList;
import java.util.List;

public class DataProductChartsDataDTO {
    public List<Float> dataF;
    public List<String> dataS;
    public String xValue;

    public float calcData;
    public DataProductChartsDataDTO(float data, String xValue){
        this.xValue = xValue;
        dataF  = new ArrayList<>();
        dataF.add(data);
        calcData = data;
    }
    public DataProductChartsDataDTO(String data, String xValue){
        this.xValue = xValue;
        dataS  = new ArrayList<>();
        dataS.add(data);
    }
    public void addData(float data){
        dataF.add(data);
        float sum = 0;
        for (float dataSum : dataF ){
            sum += dataSum;
        }
        calcData = sum / dataF.size();
    }
    public void addData(String data){
        dataS.add(data);
    }
    public String getXValueS(){
        return xValue;
    }
    public double getXValueD(){
        return Double.parseDouble(xValue);
    }
}
