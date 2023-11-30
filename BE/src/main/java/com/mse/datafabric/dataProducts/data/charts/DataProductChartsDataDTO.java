package com.mse.datafabric.dataProducts.data.charts;

import java.util.ArrayList;
import java.util.List;

public class DataProductChartsDataDTO {
    public List<Float> dataF;
    public List<String> dataS;
    public String xValue;

    public Float calcData;
    public DataProductChartsDataDTO(String xValue){
        this.xValue = xValue;
        dataS = new ArrayList<>();
        dataF = new ArrayList<>();
    }
    public void addDataS(String data){
        dataS.add(data);
    }
    public void addDataF(Float data){
        dataF.add(data);
        calcData = getCalcData(dataF);
    }

    public float getCalcData(List<Float> data){
        float sum = 0;
        for (Float dataSum : data ){
            sum += dataSum;
        }
        return sum / data.size();
    }
    public String getXValueS(){
        return xValue;
    }
    public double getXValueD(){
        return Double.parseDouble(xValue);
    }
}
