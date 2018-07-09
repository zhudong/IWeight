package com.axecom.iweight.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.axecom.iweight.R;
import com.axecom.iweight.base.BaseActivity;
import com.axecom.iweight.base.BaseEntity;
import com.axecom.iweight.bean.CommodityBean;
import com.axecom.iweight.bean.SaveGoodsReqBean;
import com.axecom.iweight.bean.ScalesCategoryGoods;
import com.axecom.iweight.conf.Constants;
import com.axecom.iweight.impl.ItemDragHelperCallback;
import com.axecom.iweight.impl.OnDragVHListener;
import com.axecom.iweight.impl.OnItemMoveListener;
import com.axecom.iweight.manager.AccountManager;
import com.axecom.iweight.manager.MacManager;
import com.axecom.iweight.net.RetrofitFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class CommodityManagementActivity extends BaseActivity {

    private View rootView;
    RecyclerView commodityRV;
    private GridView classGv;
    private ClassAdapter classAdapter;
    private DragAdapter adapter;
    private List<CommodityBean> hotKeyList;
    private List<CommodityBean> allGoodsList;
    private List<CommodityBean> categoryList;
    private List<CommodityBean> categoryChildList;
    private LinearLayout classTitleLayout;
    private TextView allTitleTv;
    private Button saveBtn, backBtn;
    private EditText searchEt;

    private boolean isShowDelTv = false;

    @Override
    public View setInitView() {
        rootView = LayoutInflater.from(this).inflate(R.layout.commodity_management_activity, null);
        commodityRV = rootView.findViewById(R.id.commodity_management_rv);
        classGv = rootView.findViewById(R.id.commodity_management_class_gv);
        classTitleLayout = rootView.findViewById(R.id.commodity_management_class_title_layout);
        allTitleTv = rootView.findViewById(R.id.commodity_management_class_titlte_all_tv);
        saveBtn = rootView.findViewById(R.id.commodity_management_save_btn);
        backBtn = rootView.findViewById(R.id.commodity_management_back_btn);
        searchEt = rootView.findViewById(R.id.commodity_management_search_et);
        getGoodsData();
        searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() == 0){
                    return;
                }
                Pattern pattern = Pattern.compile(s.toString());
                List<CommodityBean> result = new ArrayList<>();

                for (int i = 0; i < allGoodsList.size(); i++) {
                    CommodityBean bean = allGoodsList.get(i);
                    Matcher matcher = pattern.matcher(allGoodsList.get(i).getAllGoods().name);
                    if(matcher.find()){
                        result.add(allGoodsList.get(i));
                    }
                }
                classAdapter = new ClassAdapter(CommodityManagementActivity.this, result);
                classGv.setAdapter(classAdapter);
                setClassTitleTxtColor();
            }
        });
        allTitleTv.setOnClickListener(this);
        saveBtn.setOnClickListener(this);
        backBtn.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void initView() {
        hotKeyList = new ArrayList<>();
        allGoodsList = new ArrayList<>();
        categoryList = new ArrayList<>();
        categoryChildList = new ArrayList<>();
        GridLayoutManager manager = new GridLayoutManager(this, 5);
        commodityRV.setLayoutManager(manager);
        ItemDragHelperCallback callback = new ItemDragHelperCallback() {
            @Override
            public boolean isLongPressDragEnabled() {
                // 长按拖拽打开
                return true;
            }
        };
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(commodityRV);

        adapter = new DragAdapter(this, hotKeyList);
        commodityRV.setAdapter(adapter);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClickListener(View v) {
                int position = (int) v.getTag();
                hotKeyList.get(position);
                Intent intent = new Intent(CommodityManagementActivity.this, ModityCommodityActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("commodityBean", hotKeyList.get(position));
                intent.putExtra("position", position);
                intent.putExtras(bundle);
                startActivityForResult(intent, 1001);
            }
        });

        classAdapter = new ClassAdapter(this, allGoodsList);
        classGv.setAdapter(classAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.commodity_management_class_titlte_all_tv:
                classAdapter = new ClassAdapter(this, allGoodsList);
                classGv.setAdapter(classAdapter);
                setClassTitleTxtColor();
                break;
            case R.id.commodity_management_save_btn:
                SaveGoodsReqBean goodsReqBean = new SaveGoodsReqBean();
                List<SaveGoodsReqBean.Goods> goodsList = new ArrayList<>();
                SaveGoodsReqBean.Goods good;
                for (int i = 0; i < hotKeyList.size(); i++) {
                    good = new SaveGoodsReqBean.Goods();
                    good.id = hotKeyList.get(i).getHotKeyGoods().id;
                    good.cid = hotKeyList.get(i).getHotKeyGoods().cid;
                    good.is_default = hotKeyList.get(i).getHotKeyGoods().is_default;
                    good.name = hotKeyList.get(i).getHotKeyGoods().name;
                    good.price = hotKeyList.get(i).getHotKeyGoods().price;
                    good.traceable_code = hotKeyList.get(i).getHotKeyGoods().traceable_code;
                    goodsList.add(good);
                }
                goodsReqBean.setToken(AccountManager.getInstance().getAdminToken());
                goodsReqBean.setMac(Constants.MAC_TEST);
                goodsReqBean.setGoods(goodsList);
                storeGoodsData(goodsReqBean);
                break;
            case R.id.commodity_management_back_btn:
                finish();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1001) {
            if (data != null) {
                int position = data.getIntExtra("position", -1);
                ScalesCategoryGoods.HotKeyGoods goods = (ScalesCategoryGoods.HotKeyGoods) data.getExtras().getSerializable("hotKeyGoods");
                CommodityBean bean = new CommodityBean();
                bean.setHotKeyGoods(goods);
                hotKeyList.set(position, bean);
            }

        }
    }

    public void setClassTitleTxtColor(){
        for (int i = 0; i < classTitleLayout.getChildCount(); i++) {
            ((TextView) classTitleLayout.getChildAt(i)).setTextColor(ContextCompat.getColor(CommodityManagementActivity.this, R.color.black));
        }
        allTitleTv.setTextColor(ContextCompat.getColor(CommodityManagementActivity.this, R.color.green_3CB371));
    }

    public void storeGoodsData(SaveGoodsReqBean goodsReqBean) {
        RetrofitFactory.getInstance().API()
                .storeGoodsData(goodsReqBean)
                .compose(this.<BaseEntity>setThread())
                .subscribe(new Observer<BaseEntity>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        showLoading();
                    }

                    @Override
                    public void onNext(BaseEntity baseEntity) {
                        if (baseEntity.isSuccess()) {
                            Toast.makeText(CommodityManagementActivity.this, baseEntity.getMsg(), Toast.LENGTH_SHORT).show();
                        } else {
                            showLoading(baseEntity.getMsg());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        closeLoading();
                    }
                });
    }

    public void getGoodsData() {
        RetrofitFactory.getInstance().API()
                .getGoodsData(AccountManager.getInstance().getAdminToken(), Constants.MAC_TEST)
                .compose(this.<BaseEntity<ScalesCategoryGoods>>setThread())
                .subscribe(new Observer<BaseEntity<ScalesCategoryGoods>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        showLoading();
                    }

                    @Override
                    public void onNext(final BaseEntity<ScalesCategoryGoods> scalesCategoryGoodsBaseEntity) {
                        if (scalesCategoryGoodsBaseEntity.isSuccess()) {
                            CommodityBean commodityBean = null;
                            for (int i = 0; i < scalesCategoryGoodsBaseEntity.getData().hotKeyGoods.size(); i++) {
                                commodityBean = new CommodityBean();
                                commodityBean.setHotKeyGoods((ScalesCategoryGoods.HotKeyGoods) scalesCategoryGoodsBaseEntity.getData().hotKeyGoods.get(i));
                                hotKeyList.add(commodityBean);
                            }
                            CommodityBean allGoodsBean;
                            for (int i = 0; i < scalesCategoryGoodsBaseEntity.getData().allGoods.size(); i++) {
                                allGoodsBean = new CommodityBean();
                                allGoodsBean.setAllGoods((ScalesCategoryGoods.allGoods) scalesCategoryGoodsBaseEntity.getData().allGoods.get(i));
                                allGoodsList.add(allGoodsBean);
                            }
                            CommodityBean categoryBean;
                            for (int i = 0; i < scalesCategoryGoodsBaseEntity.getData().categoryGoods.size(); i++) {
                                categoryBean = new CommodityBean();
                                categoryBean.setCategoryGoods((ScalesCategoryGoods.categoryGoods) scalesCategoryGoodsBaseEntity.getData().categoryGoods.get(i));
//                                categoryBean.setCategoryChilds((ScalesCategoryGoods.categoryGoods.child) ((ScalesCategoryGoods.categoryGoods) scalesCategoryGoodsBaseEntity.getData().categoryGoods.get(i)).child);
                                categoryList.add(categoryBean);
                                final TextView titleTv = new TextView(CommodityManagementActivity.this);
                                titleTv.setText(categoryBean.getCategoryGoods().name);
                                titleTv.setTextSize(25);
                                titleTv.setTextColor(ContextCompat.getColor(CommodityManagementActivity.this, R.color.black));
                                titleTv.setLayoutParams(new LinearLayout.LayoutParams(100, LinearLayout.LayoutParams.WRAP_CONTENT));
                                titleTv.setGravity(Gravity.CENTER);
                                titleTv.setTag(((ScalesCategoryGoods.categoryGoods) scalesCategoryGoodsBaseEntity.getData().categoryGoods.get(i)).id);
                                classTitleLayout.addView(titleTv);
                                final int finalI = i;
                                final int finalI1 = i;
                                titleTv.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        categoryChildList.clear();
                                        CommodityBean clildBean;
                                        for (int j = 0; j < ((ScalesCategoryGoods.categoryGoods) scalesCategoryGoodsBaseEntity.getData().categoryGoods.get(finalI)).child.size(); j++) {
                                            if (((ScalesCategoryGoods.categoryGoods.child) ((ScalesCategoryGoods.categoryGoods) scalesCategoryGoodsBaseEntity.getData().categoryGoods.get(finalI)).child.get(j)).cid ==
                                                    ((ScalesCategoryGoods.categoryGoods) scalesCategoryGoodsBaseEntity.getData().categoryGoods.get(finalI1)).id) {
                                                clildBean = new CommodityBean();
                                                clildBean.setCategoryChilds((ScalesCategoryGoods.categoryGoods.child) ((ScalesCategoryGoods.categoryGoods) scalesCategoryGoodsBaseEntity.getData().categoryGoods.get(finalI)).child.get(j));
                                                categoryChildList.add(clildBean);
                                                ClassAdapter adapter = new ClassAdapter(CommodityManagementActivity.this, categoryChildList);
                                                classGv.setAdapter(adapter);
                                                titleTv.setTextColor(ContextCompat.getColor(CommodityManagementActivity.this, R.color.green_3CB371));
                                                for (int i = 0; i < classTitleLayout.getChildCount(); i++) {
                                                    TextView tv = (TextView) classTitleLayout.getChildAt(i);
                                                    if(tv.getTag() == titleTv.getTag()){
                                                        tv.setTextColor(ContextCompat.getColor(CommodityManagementActivity.this, R.color.green_3CB371));
                                                    }else {
                                                        tv.setTextColor(ContextCompat.getColor(CommodityManagementActivity.this, R.color.black));
                                                    }
                                                }
                                                allTitleTv.setTextColor(ContextCompat.getColor(CommodityManagementActivity.this, R.color.black));
                                            }
                                        }
                                    }
                                });
                            }
//                            CommodityBean clildBean;
//                            for (int i = 0; i < scalesCategoryGoodsBaseEntity.getData().categoryGoods.size(); i++) {
//                                for (int j = 0; j < ((ScalesCategoryGoods.categoryGoods)scalesCategoryGoodsBaseEntity.getData().categoryGoods.get(i)).child.size(); j++) {
//                                    clildBean = new CommodityBean();
//                                    clildBean.setCategoryChilds((ScalesCategoryGoods.categoryGoods.child) ((ScalesCategoryGoods.categoryGoods)scalesCategoryGoodsBaseEntity.getData().categoryGoods.get(i)).child.get(j));
//                                    categoryChildList.add(clildBean);
//                                }
//                            }
                            adapter.notifyDataSetChanged();
                            classAdapter.notifyDataSetChanged();
                        } else {
                            showLoading(scalesCategoryGoodsBaseEntity.getMsg());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        closeLoading();
                    }

                    @Override
                    public void onComplete() {
                        closeLoading();
                    }
                });
    }

    class ClassAdapter extends BaseAdapter {
        List<CommodityBean> list;
        private Context context;

        public ClassAdapter(Context context, List<CommodityBean> list) {
            this.list = list;
            this.context = context;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.commodity_class_item, null);
                holder = new ViewHolder();
                holder.nameBtn = convertView.findViewById(R.id.commodity_class_name_tv);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final CommodityBean item = list.get(position);
            holder.nameBtn.setText(item.getAllGoods() != null ? item.getAllGoods().name : item.getCategoryChilds().name);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    CommodityBean bean = (CommodityBean) classAdapter.getItem(position);
                    item.setShow(isShowDelTv);
                    ScalesCategoryGoods.HotKeyGoods hotKeyGoods = new ScalesCategoryGoods.HotKeyGoods();
                    if (item.getAllGoods() != null) {
                        hotKeyGoods.id = item.getAllGoods().id;
                        hotKeyGoods.cid = item.getAllGoods().cid;
                        hotKeyGoods.name = item.getAllGoods().name;
                        hotKeyGoods.price = item.getAllGoods().price;
                        hotKeyGoods.traceable_code = item.getAllGoods().traceable_code;
                        hotKeyGoods.is_default = item.getAllGoods().is_default;
                    }
                    if (item.getCategoryChilds() != null) {
                        hotKeyGoods.id = item.getCategoryChilds().id;
                        hotKeyGoods.cid = item.getCategoryChilds().cid;
                        hotKeyGoods.name = item.getCategoryChilds().name;
                        hotKeyGoods.price = item.getCategoryChilds().price;
                        hotKeyGoods.traceable_code = item.getCategoryChilds().traceable_code;
                        hotKeyGoods.is_default = item.getCategoryChilds().is_default;
                    }
                    CommodityBean hotKeyBean = new CommodityBean();
                    hotKeyBean.setHotKeyGoods(hotKeyGoods);
                    hotKeyList.add(hotKeyBean);
                    adapter.notifyDataSetChanged();
                }
            });
            return convertView;
        }

        class ViewHolder {
            Button nameBtn;
        }
    }


    public interface OnItemClickListener {
        void onItemClickListener(View v);
    }

    class DragAdapter extends RecyclerView.Adapter<DragAdapter.DragViewHolder> implements OnItemMoveListener {
        private List<CommodityBean> mItems;
        private LayoutInflater mInflater;
        private OnItemClickListener onItemClickListener;

        public DragAdapter(Context context, List<CommodityBean> items) {
            mInflater = LayoutInflater.from(context);
            this.mItems = items;
        }

        @Override
        public DragViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.commodity_item_2, parent, false);
            DragViewHolder holder = new DragViewHolder(view);
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    showDeleteTv();
                    return true;
                }
            });
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClickListener(v);
                }
            });
            return holder;
        }

        @Override
        public void onBindViewHolder(DragViewHolder holder, final int position) {
            if (mItems.get(position).isShow()) {
                holder.deleteTv.setVisibility(View.VISIBLE);
            }
            holder.nameTv.setText(mItems.get(position).getHotKeyGoods().name);
            holder.deleteTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeItemByPosition(position);
                }
            });
            holder.itemView.setTag(position);
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

        @Override
        public void onItemMove(int fromPosition, int toPosition) {
            CommodityBean item = mItems.get(fromPosition);
            mItems.remove(fromPosition);
            mItems.add(toPosition, item);
            notifyItemMoved(fromPosition, toPosition);
        }

        public void showDeleteTv() {
            for (int i = 0; i < mItems.size(); i++) {
                mItems.get(i).setShow(true);
            }
            isShowDelTv = true;
            notifyDataSetChanged();
        }

        public void removeItemByPosition(int position) {
            mItems.remove(position);
            notifyDataSetChanged();
        }

        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            this.onItemClickListener = onItemClickListener;
        }

        private void startEditMode(RecyclerView parent) {

            int visibleChildCount = parent.getChildCount();
            for (int i = 0; i < visibleChildCount; i++) {
                View view = parent.getChildAt(i);
                TextView deleteTv = (TextView) view.findViewById(R.id.commodity_item_2_delete_tv);
                if (deleteTv != null) {
                    deleteTv.setVisibility(View.VISIBLE);
                }
            }
        }

        class DragViewHolder extends RecyclerView.ViewHolder implements OnDragVHListener {
            private TextView nameTv;
            private TextView deleteTv;
            private ImageView selectedTv;

            public DragViewHolder(View itemView) {
                super(itemView);
                nameTv = (TextView) itemView.findViewById(R.id.commodity_item_2_name_tv);
                deleteTv = (TextView) itemView.findViewById(R.id.commodity_item_2_delete_tv);
                selectedTv = (ImageView) itemView.findViewById(R.id.commodity_item_2_selected_iv);
            }

            @Override
            public void onItemSelected() {
//                deleteTv.setVisibility(View.VISIBLE);
            }

            @Override
            public void onItemFinish() {
//                itemView.setBackgroundColor(0);
            }
        }
    }


}
