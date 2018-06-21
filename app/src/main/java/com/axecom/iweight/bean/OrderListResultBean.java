package com.axecom.iweight.bean;

import com.axecom.iweight.base.BaseEntity;

import java.util.List;

public class OrderListResultBean<T> extends BaseEntity {
    public int total;
    public String total_amount;

    public List<list> list;

    public class list{
        public String order_no;
        public String total_amount;
        public String total_weight;
        public String times;
        public String payment_type;
    }
}
