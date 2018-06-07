package com.axecom.iweight.ui.activity;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.axecom.iweight.R;
import com.axecom.iweight.base.BaseActivity;
import com.axecom.iweight.impl.ItemDragHelperCallback;
import com.axecom.iweight.impl.OnDragVHListener;
import com.axecom.iweight.impl.OnItemMoveListener;

import java.util.ArrayList;
import java.util.List;

public class CommodityManagementActivity extends BaseActivity {

    private View rootView;
    RecyclerView commodityRV;

    @Override
    public View setInitView() {
        rootView = LayoutInflater.from(this).inflate(R.layout.commodity_management_activity, null);
        commodityRV = rootView.findViewById(R.id.commodity_management_rv);

        return rootView;
    }

    @Override
    public void initView() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            list.add("萝卜" + i);
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

        DragAdapter adapter = new DragAdapter(this, list);
        commodityRV.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {

    }


    class DragAdapter extends RecyclerView.Adapter<DragAdapter.DragViewHolder> implements OnItemMoveListener {
        private List<String> mItems;
        private LayoutInflater mInflater;

        public DragAdapter(Context context, List<String> items) {
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
                    RecyclerView recyclerView = (RecyclerView) parent;
                    startEditMode(recyclerView);
                    return true;
                }
            });
            return holder;
        }

        @Override
        public void onBindViewHolder(DragViewHolder holder, int position) {
            holder.nameTv.setText(mItems.get(position));
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

        @Override
        public void onItemMove(int fromPosition, int toPosition) {
            String item = mItems.get(fromPosition);
            mItems.remove(fromPosition);
            mItems.add(toPosition, item);
            notifyItemMoved(fromPosition, toPosition);
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
