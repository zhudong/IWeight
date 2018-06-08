package com.axecom.iweight.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.axecom.iweight.R;
import com.axecom.iweight.base.BaseActivity;
import com.axecom.iweight.bean.CommodityBean;
import com.axecom.iweight.impl.ItemDragHelperCallback;
import com.axecom.iweight.impl.OnDragVHListener;
import com.axecom.iweight.impl.OnItemMoveListener;

import java.util.ArrayList;
import java.util.List;

public class CommodityManagementActivity extends BaseActivity {

    private View rootView;
    RecyclerView commodityRV;
    private GridView classGv;
    private ClassAdapter classAdapter;
    private boolean isShowDelTv = false;

    @Override
    public View setInitView() {
        rootView = LayoutInflater.from(this).inflate(R.layout.commodity_management_activity, null);
        commodityRV = rootView.findViewById(R.id.commodity_management_rv);
        classGv = rootView.findViewById(R.id.commodity_management_class_gv);

        return rootView;
    }

    @Override
    public void initView() {
        final List<CommodityBean> list = new ArrayList<>();
        CommodityBean commodityBean;
        for (int i = 0; i < 30; i++) {
            commodityBean = new CommodityBean();
            commodityBean.setName("萝卜" + i);
            commodityBean.setShow(false);
            list.add(commodityBean);
        }
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

        final DragAdapter adapter = new DragAdapter(this, list);
        commodityRV.setAdapter(adapter);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClickListener(View v) {
                int position = (int) v.getTag();
                list.get(position);
                Intent intent = new Intent(CommodityManagementActivity.this, ModityCommodityActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("commodityBean", list.get(position));
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        List<CommodityBean> list2 = new ArrayList<>();
        CommodityBean commodityBean2;
        for (int i = 0; i < 30; i++) {
            commodityBean2 = new CommodityBean();
            commodityBean2.setName("白菜" + i);
            commodityBean2.setShow(false);
            list2.add(commodityBean2);
        }
        classAdapter = new ClassAdapter(this, list2);
        classGv.setAdapter(classAdapter);
        classGv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CommodityBean bean = (CommodityBean) classAdapter.getItem(position);
                bean.setShow(isShowDelTv);
                list.add(bean);
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onClick(View v) {

    }

    class ClassAdapter extends BaseAdapter{
        List<CommodityBean> list;
        private Context context;
        public ClassAdapter(Context context, List<CommodityBean> list){
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
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if(convertView == null){
                convertView = LayoutInflater.from(context).inflate(R.layout.commodity_class_item, null);
                holder = new ViewHolder();
                holder.nameBtn = convertView.findViewById(R.id.commodity_class_name_tv);
                convertView.setTag(holder);
            }else {
                holder = (ViewHolder) convertView.getTag();
            }
            CommodityBean item = list.get(position);
            holder.nameBtn.setText(item.getName());
            return convertView;
        }

        class ViewHolder{
            Button nameBtn;
        }
    }


    public interface OnItemClickListener{
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
            if(mItems.get(position).isShow()){
                holder.deleteTv.setVisibility(View.VISIBLE);
            }
            holder.nameTv.setText(mItems.get(position).getName());
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

        public void showDeleteTv(){
            for (int i = 0; i < mItems.size(); i++) {
                mItems.get(i).setShow(true);
            }
            isShowDelTv = true;
            notifyDataSetChanged();
        }

        public void removeItemByPosition(int position){
            mItems.remove(position);
            notifyDataSetChanged();
        }

        public void setOnItemClickListener(OnItemClickListener onItemClickListener){
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
