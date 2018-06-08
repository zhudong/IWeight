package com.axecom.iweight.bean;

import java.io.Serializable;

public class CommodityBean implements Serializable{

    private boolean isShow;
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }
}
