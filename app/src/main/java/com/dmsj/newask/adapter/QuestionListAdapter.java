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
    public QuestionListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_checkbox_index, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final QuestionListAdapter.ViewHolder holder, final int position) {
        final CheckMoreInfo checkMoreInfo = list.get(position);
        holder.ck_index.setText(checkMoreInfo.getMessage());
        holder.ck_index.setChecked(checkMoreInfo.isCheck());
        holder.ck_index.setSelected(true);
        holder.ck_index.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                checkMoreInfo.setIsCheck(isChecked);
                buttonView.post(new Runnable() {
                    @Override
                    public void run() {
                        hideSound.onCheckbox_hideSound();
                        // 这里写第一项与其他项互斥逻辑{



                        //}
                        boolean isOpen = false;
                        for (int i = 0; i < list.size(); i++) {
                            if (list.get(i).isCheck()) {
                                isOpen = true;
                                break;
                            }
                        }
                        onCheckboxItem.onCheckboxItem(isOpen, checkMoreInfo.getMessage());
                    }
                });
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
