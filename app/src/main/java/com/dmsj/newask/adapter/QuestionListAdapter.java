package com.dmsj.newask.adapter;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.dmsj.newask.Info.CheckMoreInfo;
import com.dmsj.newask.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TriumphalSun
 * on 2019/9/26 0026
 * com.dmsj.newask.adapter name of the package in which the new file is created
 */
public class QuestionListAdapter extends RecyclerView.Adapter<QuestionListAdapter.ViewHolder> {
    private Context context;
    public List<CheckMoreInfo> list;
    private AlertDialog.Builder builder;

    public QuestionListAdapter(Context context) {
        list = new ArrayList<>();
        this.context = context;
    }

    public void setList(List<CheckMoreInfo> list) {
        if (list != null) {
            this.list = list;
        }
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_checkbox_index, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.ck_index.setText(list.get(position).getMessage());
        holder.ck_index.setSelected(true);
        holder.ck_index.setChecked(list.get(position).isCheck());
        holder.ck_index.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                hideSound.onCheckbox_hideSound();
                // 这里写第一项与其他项互斥逻辑{

                if (list.get(position).isCheck()) {
                    list.get(position).setIsCheck(false);

                } else{
                    if (position == 0) {
                        for (int i = 0; i < list.size(); i++) {
                            list.get(i).setIsCheck(false);
                        }
                        list.get(position).setIsCheck(true);
                    } else {
                        list.get(position).setIsCheck(true);
                        list.get(0).setIsCheck(false);
                    }
                }


                //}
                boolean isOpen = false;
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).isCheck()) {
                        isOpen = true;
                        break;
                    }
                }
                onCheckboxItem.onCheckboxItem(isOpen, list.get(position).getMessage());
                notifyDataSetChanged();
            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CheckBox ck_index;

        public ViewHolder(View itemView) {
            super(itemView);
            ck_index = (CheckBox) itemView.findViewById(R.id.text);

        }
    }

    // 回调是否有选中
    private OnCheckboxItem onCheckboxItem;

    public interface OnCheckboxItem {
        void onCheckboxItem(boolean isOpen, String message);
    }

    public void setOnCheckboxItem(OnCheckboxItem onCheckboxItem) {
        this.onCheckboxItem = onCheckboxItem;
    }


    private OnCheckbox_hideSound hideSound;

    public interface OnCheckbox_hideSound {
        void onCheckbox_hideSound();
    }

    public void setHideSound(OnCheckbox_hideSound hideSound) {
        this.hideSound = hideSound;
    }
}
