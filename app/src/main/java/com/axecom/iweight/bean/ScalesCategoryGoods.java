package com.axecom.iweight.bean;

import com.axecom.iweight.base.BaseEntity;

import java.io.Serializable;
import java.util.List;

public class ScalesCategoryGoods<T> extends BaseEntity {
    public List<HotKeyGoods> hotKeyGoods;
    public List<allGoods> allGoods;
    public List<categoryGoods> categoryGoods;
    public List<Goods> goodsList;

    public static class Goods{
        public int id;
        public String name;
        public int cid;
        public int traceable_code;
        public String price;
        public int id_default;
    }

    public static class HotKeyGoods implements Serializable{
        public int id;
        public String name;
        public int cid;
        public int traceable_code;
        public String price;
        public int id_default;
    }

    public class allGoods{
        public int id;
        public String name;
        public int cid;
        public int traceable_code;
        public String price;
        public int id_default;
    }

    public class categoryGoods{
        public int id;
        public String name;
        public List<child> child;

        public class child  {
            public int id;
            public String name;
            public int cid;
            public int traceable_code;
            public String price;
            public int id_default;
        }
    }
}
