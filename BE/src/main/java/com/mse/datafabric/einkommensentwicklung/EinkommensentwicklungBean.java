package com.mse.datafabric.einkommensentwicklung;

public class EinkommensentwicklungBean {

    //Jahr;Insgesamt;Maenner;Frauen;

    private String jahr;


    private String insgesamt;


    private String maenner;

    public EinkommensentwicklungBean(String jahr, String insgesamt, String maenner, String frauen) {
        this.jahr = jahr;
        this.insgesamt = insgesamt;
        this.maenner = maenner;
        this.frauen = frauen;
    }

    public EinkommensentwicklungBean() {
    }

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
