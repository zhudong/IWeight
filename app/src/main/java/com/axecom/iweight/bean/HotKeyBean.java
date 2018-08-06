package com.axecom.iweight.bean;

import com.axecom.iweight.base.AppDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by Administrator on 2018/8/6.
 */

@Table(database = AppDatabase.class)
public class HotKeyBean extends BaseModel {
    @PrimaryKey(autoincrement = true)//ID自增
    public long hotKeyid;

    @Column
    public int id;
    @Column
    public String name;
    @Column
    public int cid;
    @Column
    public int traceable_code;
    @Column
    public String price;
    @Column
    public int is_default;
    @Column
    public String weight;
    @Column
    public String grandTotal;
}
