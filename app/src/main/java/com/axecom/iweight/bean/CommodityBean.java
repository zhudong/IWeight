package com.axecom.iweight.bean;

import java.io.Serializable;

public class CommodityBean implements Serializable{

    private boolean isShow;
    private ScalesCategoryGoods.HotKeyGoods hotKeyGoods;
    private ScalesCategoryGoods.allGoods allGoods;
    private ScalesCategoryGoods.categoryGoods categoryGoods;
    private ScalesCategoryGoods.categoryGoods.child categoryChilds;

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }

    public ScalesCategoryGoods.HotKeyGoods getHotKeyGoods() {
        return hotKeyGoods;
    }

    public void setHotKeyGoods(ScalesCategoryGoods.HotKeyGoods hotKeyGoods) {
        this.hotKeyGoods = hotKeyGoods;
    }

    public ScalesCategoryGoods.categoryGoods getCategoryGoods() {
        return categoryGoods;
    }

    public void setCategoryGoods(ScalesCategoryGoods.categoryGoods categoryGoods) {
        this.categoryGoods = categoryGoods;
    }

    public ScalesCategoryGoods.allGoods getAllGoods() {
        return allGoods;
    }

    public void setAllGoods(ScalesCategoryGoods.allGoods allGoods) {
        this.allGoods = allGoods;
    }

    public ScalesCategoryGoods.categoryGoods.child getCategoryChilds() {
        return categoryChilds;
    }

    public void setCategoryChilds(ScalesCategoryGoods.categoryGoods.child categoryChilds) {
        this.categoryChilds = categoryChilds;
    }
}
