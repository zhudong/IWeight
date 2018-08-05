package com.axecom.iweight.bean;

import com.axecom.iweight.base.AppDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.List;

/**
 * Created by Administrator on 2018/7/22.
 */

@Table(database = AppDatabase.class)
public class Order extends BaseModel {
    @PrimaryKey(autoincrement = true)//ID自增
    public long id;

    @Column
    public String order_no;
    @Column
    public String total_amount;
    @Column
    public String total_weight;
    @Column
    public String create_time;
    @Column
    public String payment_type;
    @Column
    public String goods_id;
    @Column
    public String goods_name;
    @Column
    public String goods_price;
    @Column
    public String goods_number;
    @Column
    public String goods_weight;
    @Column
    public String amount;
}
