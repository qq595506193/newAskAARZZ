package com.dmsj.newask.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dmsj.newask.Activity.ChatActivity;
import com.dmsj.newask.Info.MessageBtn;
import com.dmsj.newask.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TriumphalSun
 * on 2019/10/8 0008
 * com.dmsj.newask.adapter name of the package in which the new file is created
 */
public class ButtonAdapter extends RecyclerView.Adapter<ButtonAdapter.ViewHolder> {
    private Context context;
    private ChatActivity mActivity;
    private List<MessageBtn> list;
    private ChatInfoAdapter.LeftViewHolder vh;

    public ButtonAdapter(Context context, ChatActivity mActivity, ChatInfoAdapter.LeftViewHolder vh) {
        list = new ArrayList<>();
        this.mActivity = mActivity;
        this.context = context;
        this.vh = vh;
    }

    public void setList(List<MessageBtn> list) {
        if (list != null) {
            this.list = list;
        }
        notifyDataSetChanged();
    }

    @Override
    public ButtonAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_button, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ButtonAdapter.ViewHolder holder, int position) {
        final MessageBtn messageBtn = list.get(position);
        holder.btn.setText(Html.fromHtml(messageBtn.getBtn_text()));
        holder.btn.setTag(messageBtn);
        if (!TextUtils.isEmpty(messageBtn.getColor())) {
            holder.btn.setTextColor(Color.parseColor("#" + messageBtn.getColor()));
        }
        if (holder.btn.getText().length() > 10) {
            holder.btn.setTextSize(14);
        }
        holder.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.phoneType = "";
                if (messageBtn.isAsk()) {
                    mActivity.showSureCancle(true);
                    mActivity.btn_sure.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            mActivity.onMessageSend(messageBtn.getBtn_text());


                        }
                    });
                    mActivity.btn_cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mActivity.showSureCancle(false);

                        }
                    });
                } else {
                    mActivity.onMessageSend(((MessageBtn) v.getTag()).getBtn_text());
                }
            }
        });
        if (position == list.size() - 1) {
            vh.mViewGroup.setPadding(0, 0, 0, 18);
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView btn;

        public ViewHolder(View itemView) {
            super(itemView);
            btn = itemView.findViewById(R.id.btn);
        }
    }
}
