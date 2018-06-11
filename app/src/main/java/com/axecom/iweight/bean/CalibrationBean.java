package com.axecom.iweight.bean;

public class CalibrationBean {

    private String token;
    private String maxAnge;
    private String calibrationValue;
    private String dividingValue;
    private String standardWeighing;

    public String getStandardWeighing() {
        return standardWeighing;
    }

    public void setStandardWeighing(String standardWeighing) {
        this.standardWeighing = standardWeighing;
    }

    public String getDividingValue() {
        return dividingValue;
    }

    public void setDividingValue(String dividingValue) {
        this.dividingValue = dividingValue;
    }

    public String getCalibrationValue() {
        return calibrationValue;
    }

    public void setCalibrationValue(String calibrationValue) {
        this.calibrationValue = calibrationValue;
    }

    public String getMaxAnge() {
        return maxAnge;
    }

    public void setMaxAnge(String maxAnge) {
        this.maxAnge = maxAnge;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
