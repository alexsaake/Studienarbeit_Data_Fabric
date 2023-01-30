package com.mse.datafabric;

import com.opencsv.bean.CsvBindByName;

public class EinkommenBean {

    //Jahr;Insgesamt;Maenner;Frauen;
    @CsvBindByName(column = "Jahr", required = false)
    private String jahr;

    @CsvBindByName(column = "Insgesamt", required = false)
    private String insgesamt;

    @CsvBindByName(column = "Maenner", required = false)
    private String maenner;

    @CsvBindByName(column = "Frauen", required = false)
    private String frauen;

    public String getJahr() {
        return jahr;
    }

    public void setJahr(String jahr) {
        this.jahr = jahr;
    }

    public String getInsgesamt() {
        return insgesamt;
    }

    public void setInsgesamt(String insgesamt) {
        this.insgesamt = insgesamt;
    }

    public String getMaenner() {
        return maenner;
    }

    public void setMaenner(String maenner) {
        this.maenner = maenner;
    }

    public String getFrauen() {
        return frauen;
    }

    public void setFrauen(String frauen) {
        this.frauen = frauen;
    }
}
