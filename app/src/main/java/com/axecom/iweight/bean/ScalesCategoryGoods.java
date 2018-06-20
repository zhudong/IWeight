package com.axecom.iweight.bean;

import com.axecom.iweight.base.BaseEntity;

import java.util.List;

public class ScalesCategoryGoods<T> extends BaseEntity {
    private List<T> hotKeyGoods;
    private List<T> allGoods;
    private List<T> categoryGoods;

    class hotKeyGoods<T>{
        int id;
        String name;
        int cid;
        int traceable_code;
        String price;
        int id_default;
    }

    class allGoods<T>{
        int id;
        String name;
        int cid;
        int traceable_code;
        String price;
        int id_default;
    }

    class categoryGoods<T>{
        int id;
        String name;
        List<T> child;

        class child<T> extends categoryGoods{
            int id;
            String name;
            int cid;
            int traceable_code;
            String price;
            int id_default;
        }
    }
}
