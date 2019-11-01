package com.dmsj.newask.adapter;

import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dmsj.newask.Activity.ChatActivity;
import com.dmsj.newask.Activity.CountActivity;
import com.dmsj.newask.Activity.DoctorMessageActivity;
import com.dmsj.newask.HApplication;
import com.dmsj.newask.Info.CheckMoreInfo;
import com.dmsj.newask.Info.MessageBtn;
import com.dmsj.newask.Info.MessageDoctorInfo;
import com.dmsj.newask.Info.MessageInfo;
import com.dmsj.newask.Info.TalkEvent;
import com.dmsj.newask.R;
import com.dmsj.newask.Views.CircleImageView;
import com.dmsj.newask.http.HttpRestClient;
import com.dmsj.newask.http.JsonHttpResponseHandler;
import com.dmsj.newask.http.LodingFragmentDialog;
import com.dmsj.newask.http.LoginHttp;
import com.dmsj.newask.utils.AnimaUtils;
import com.dmsj.newask.utils.MKLog;
import com.dmsj.newask.utils.SharePreUtils;
import com.dmsj.newask.utils.SpeechUtils;
import com.dmsj.newask.utils.SystemUtils;
import com.dmsj.newask.utils.ThemeUtils;
import com.dmsj.newask.utils.TimeUtil;
import com.dmsj.newask.utils.UnitUtil;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.greenrobot.event.EventBus;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;


/**
 * 聊天信息适配器
 * Created by Mickey.Li on 2016-3-23.
 */
public class ChatInfoAdapter extends BaseAdapter {
    private static final String TAG = "ChatInfoAdapter";
    private final LayoutInflater mInflater;
    private final int mChatTCommonColor;
    private final int mChatTWhiteColor;
    private ChatActivity mActivity;
    private boolean isOpenSound = false;
    private final SpeechUtils mSpeechUtils;
    boolean isReceive = false;
    private ListView mListView;
    public static final int ITEM_LEFT_LAYOUT = 0;//左边对话框
    public static final int ITEM_RIGHTT_LAYOUT = 1;//右边对话框
    private LodingFragmentDialog mLodingFragmentDialog;
    AnimationDrawable animation;
    List<MessageInfo> list = new ArrayList<MessageInfo>();

    public ChatInfoAdapter(ChatActivity activity) {
        mActivity = activity;
        mInflater = LayoutInflater.from(activity);
        mSpeechUtils = new SpeechUtils(mActivity);
        mWhriteDrawable = activity.getResources().getDrawable(ThemeUtils.getIdFromAttr(this.mActivity, R.attr.item_chat_l_light_bg));
        mChatTCommonColor = mActivity.getResources().getColor(ThemeUtils.getIdFromAttr(activity, R.attr.inputColor));

        mChatTWhiteColor = mActivity.getResources().getColor(R.color.white);
    }

    /**
     * 添加单条消息
     *
     * @param info
     * @return
     */
    int n = 0;

    public void onDataChange(MessageInfo info, boolean isAdd, boolean isUP, boolean isFinish, boolean isHis) {
        isReceive = true;
        info.setIsAdd(isAdd);
        info.setHis(isHis);
        if (isUP) {
            list.add(0, info);
        } else {
            list.add(info);
        }
        isOpenSound = SharePreUtils.getSoundsBoolean(mActivity);
        n++;
        if (isFinish) {
            final int index = mListView.getFirstVisiblePosition();
            View v = mListView.getChildAt(0);
            final int top = (v == null) ? 0 : v.getTop();
            notifyDataSetChanged();
            if (isUP) {
                mListView.post(new Runnable() {
                    @Override
                    public void run() {
                        mListView.setSelectionFromTop(n + index + 1, top);
                        n = 0;
                    }
                });
            }
        }


    }


    public void setListView(ListView mListView) {
        this.mListView = mListView;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public MessageInfo getItem(int position) {
        return list.get(position);
    }

    public String getLastSendCode() {
        long n = Long.MAX_VALUE;
        if (list == null || list.size() == 0) {
            return "" + n;
        } else {
            for (int i = list.size() - 1; i >= 0; i--) {
                if (n > Integer.parseInt(list.get(i).getSend_code())) {
                    n = Integer.parseInt(list.get(i).getSend_code());
                }
            }
            return "" + n;
        }

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    int postion = 0;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        postion = position;
        final MessageInfo info = getItem(position);
        int type = info.isSend() ? ITEM_RIGHTT_LAYOUT : ITEM_LEFT_LAYOUT;
        long lastTime = 0;
        LeftViewHolder leftViewHolder = null;
        RightViewHolder rightViewHolder;
        if (convertView == null || convertView.getTag() == null) {
            if (position > 0) {
                final MessageInfo info1 = list.get(position - 1);
                lastTime = info1.getTime();
            }
            switch (type) {
                case ITEM_LEFT_LAYOUT:
                    leftViewHolder = new LeftViewHolder();
                    convertView = mInflater.inflate(R.layout.item_chat_l, null);//左侧item
                    // 得到TextView控件对象
                    leftViewHolder.chat_head = (CircleImageView) convertView.findViewById(R.id.chat_head);//头像
                    leftViewHolder.mViewGroup = ((LinearLayout) convertView.findViewById(R.id.group));
                    leftViewHolder.timeTextV = (TextView) convertView.findViewById(R.id.chat_time);
                    leftViewHolder.imageViewDianDian = (ImageView) convertView.findViewById(R.id.chat_diandiandian);
                    leftViewHolder.contentTextV = (TextView) convertView.findViewById(R.id.chat_content);
                    leftViewHolder.contentTextV.setLineSpacing(5f, 1.1f);
                    leftViewHolder.mContent_layout = (LinearLayout) convertView.findViewById(R.id.content_layout);
                    leftViewHolder.timeView = convertView.findViewById(R.id.time_view);
                    onBoundData(leftViewHolder, info, lastTime, position);
                    convertView.setTag(leftViewHolder);
                    leftViewHolder.contentTextV.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showPopOfficeWindow(v);
                        }
                    });
                    break;
                case ITEM_RIGHTT_LAYOUT:
                    rightViewHolder = new RightViewHolder();
                    convertView = mInflater.inflate(R.layout.item_chat_r, null);
                    rightViewHolder.timeTextV = (TextView) convertView.findViewById(R.id.chat_time);
                    rightViewHolder.contentTextV = (TextView) convertView.findViewById(R.id.chat_content);
                    rightViewHolder.contentTextV.setLineSpacing(5f, 1.1f);
                    rightViewHolder.timeView = convertView.findViewById(R.id.time_view);
                    rightViewHolder.imageView = (ImageView) convertView.findViewById(R.id.chat_image);
                    onBoundData(rightViewHolder, info, lastTime, position);
                    convertView.setTag(rightViewHolder);

                    break;
            }
        } else {
            if (position > 0) {
                final MessageInfo entity2 = list.get(position - 1);
                lastTime = entity2.getTime();
            }
            switch (type) {
                case ITEM_LEFT_LAYOUT:
                    leftViewHolder = (LeftViewHolder) convertView.getTag();
                    onBoundData(leftViewHolder, info, lastTime, position);
                    break;
                case ITEM_RIGHTT_LAYOUT:
                    rightViewHolder = (RightViewHolder) convertView.getTag();
                    onBoundData(rightViewHolder, info, lastTime, position);
                    break;
            }
        }

        if (position == list.size() - 1 && isReceive && isOpenSound && !info.isSend()) {
            String content;
            try {
                content = info.getMessage();
            } catch (Exception e) {
                content = "";
            }


            String text;
            if ("".equals(content)) {
                text = leftViewHolder.contentTextV.getText().toString();
            } else {
                text = content;
            }
            MKLog.e(TAG, info.isHis() + "");
            if (!TextUtils.isEmpty(text) && !info.isHis()) {
                //  读出问题内容
                mSpeechUtils.startPeed(text, null);
            }
        }
        isReceive = false;
        return convertView;
    }

    /**
     * 绑定数据
     *
     * @param o
     * @param info
     * @param lastTime
     * @param position
     */
    private void onBoundData(final Object o, MessageInfo info, long lastTime, int position) {
        if (o instanceof LeftViewHolder) {
            setChatTime(((LeftViewHolder) o).timeTextV, lastTime, info.getTime(), ((LeftViewHolder) o).timeView);
            onParseContent(info, (LeftViewHolder) o, position);
            ((LeftViewHolder) o).contentTextV.setOnLongClickListener(copyOnClick);
        } else if (o instanceof RightViewHolder) {

            setChatTime(((RightViewHolder) o).timeTextV, lastTime, info.getTime(), ((RightViewHolder) o).timeView);

            ((RightViewHolder) o).contentTextV.setVisibility(View.VISIBLE);
            ((RightViewHolder) o).imageView.setVisibility(View.GONE);
            ((RightViewHolder) o).contentTextV.setText(info.getMessage());

            ((RightViewHolder) o).contentTextV.setOnLongClickListener(copyOnClick);
        }
    }

    /**
     * 设置聊天时间
     * 20151120191722
     * 00000000000000
     */
    private void setChatTime(TextView textView, long lastTime, long nextTime, View timeView) {
        long c = nextTime - lastTime;
        if (c <= 1000 * 60 * 2) {//两分钟以内 不显示
            timeView.setVisibility(View.GONE);
        } else {
            try {
                textView.setText(TimeUtil.getMesgTime(nextTime));
                timeView.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                timeView.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 获取item展示方式
     *
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        final MessageInfo info = list.get(position);
        if (info.isSend())
            return ITEM_RIGHTT_LAYOUT;
        else
            return ITEM_LEFT_LAYOUT;
    }

    /**
     * item类型数
     *
     * @return
     */
    @Override
    public int getViewTypeCount() {
        return 2;
    }


    /**
     * 解析数据
     *
     * @param entity
     * @param vh
     * @param position
     */
    private Drawable mWhriteDrawable, mGreenDrawable;//都属于左边背景

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void onParseContent(final MessageInfo info, final LeftViewHolder vh, final int position) {
        //确定 并设置背景
        if (info.isAdd()) {
            vh.mContent_layout.setVisibility(View.INVISIBLE);
            vh.imageViewDianDian.setVisibility(View.VISIBLE);
            AnimationDrawable animation = (AnimationDrawable) vh.imageViewDianDian.getDrawable();
            animation.start();
            Observable.timer(1, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                    .map(new Func1<Long, Object>() {
                        @Override
                        public Object call(Long aLong) {
                            vh.mContent_layout.setVisibility(View.VISIBLE);
                            vh.imageViewDianDian.setVisibility(View.GONE);
                            mListView.setSelection(getCount());
                            // mListView.invalidate();

                            info.setIsAdd(false);
                            return null;
                        }
                    }).subscribe();
        } else {
            vh.imageViewDianDian.setVisibility(View.GONE);
        }
        LinearLayout contentView = vh.mContent_layout;

        ((RelativeLayout) vh.contentTextV.getParent()).setVisibility(View.VISIBLE);
        vh.mViewGroup.removeAllViews();//添加控件的父类
        RelativeLayout.LayoutParams layoutParams;
        switch (info.getType()) {
            case MessageInfo.TEXT:
                mWhriteDrawable = mActivity.getResources().getDrawable(ThemeUtils.getIdFromAttr(this.mActivity, R.attr.item_chat_l_light_bg));
                contentView.setBackgroundDrawable(mWhriteDrawable);
                vh.contentTextV.setTextColor(mChatTCommonColor);
                vh.mViewGroup.setPadding(0, 0, 0, 5);
                vh.contentTextV.setVisibility(View.VISIBLE);
                vh.contentTextV.setText(Html.fromHtml(info.getMessage()));
                layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                contentView.setLayoutParams(layoutParams);
                break;
            case MessageInfo.BTN:
                mWhriteDrawable = mActivity.getResources().getDrawable(ThemeUtils.getIdFromAttr(this.mActivity, R.attr.item_chat_l_light_bg));
                contentView.setBackgroundDrawable(mWhriteDrawable);
                vh.contentTextV.setTextColor(mChatTCommonColor);
                vh.mViewGroup.setPadding(0, 0, 0, 0);
                /**
                 * {"cont":"您头疼发病时是什么情况？",
                 * "btns"[{"id":"1","cont":"急性起病伴发热","type":"3"},
                 * {"id":"2","cont":"急剧持续头痛伴意识障碍无发热","type":"3"},
                 * {"id":"3","cont":"反复发作性或搏动性头痛","type":"3"},
                 * {"id":"4","cont":"慢性头痛进行性加重","type":"3"},
                 * {"id":"5","cont":"青壮年患者，慢性头痛","type":"3"},
                 * {"id":"6","cont":"没有以上情况","type":"3"}],"btn_align":0,"btn_type":0}
                 */

                if (isEnable(position)) {

                    vh.contentTextV.setText(Html.fromHtml(info.getMessage()));
                    View btnGroup;
                    //Button button;
//                        btnGroup = mInflater.inflate(R.layout.layout_item_btn_single, null);
                        btnGroup = mInflater.inflate(R.layout.layout_item_btn_single_02, null);
                        vh.mViewGroup.addView(btnGroup);
                        //button = (Button) btnGroup.findViewById(R.id.btn);
                        RecyclerView rv_button = btnGroup.findViewById(R.id.rv_button);
                        ButtonAdapter buttonAdapter = new ButtonAdapter(HApplication.getApplication(),mActivity,vh);
                        if (info.getBtn_align() == 1) {
                            rv_button.setLayoutManager(new GridLayoutManager(HApplication.getApplication(), 2));
                        }else {
                            rv_button.setLayoutManager(new GridLayoutManager(HApplication.getApplication(), 1));
                        }
                        rv_button.setAdapter(buttonAdapter);
                        buttonAdapter.setList(info.getList());
//                        //final MessageBtn btn = info.getList().get(i);
//                        if (!TextUtils.isEmpty(btn.getColor())) {
//
//                            button.setTextColor(Color.parseColor("#" + btn.getColor()));
//                        }
//                        button.setText(Html.fromHtml(btn.getBtn_text()));
//                        button.setTag(btn);
//
//                        button.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                mActivity.phoneType = "";
//
//                                if (btn.isAsk()) {
//                                    mActivity.showSureCancle(true);
//                                    mActivity.btn_sure.setOnClickListener(new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View v) {
//
//                                            mActivity.onMessageSend(btn.getBtn_text());
//
//
//                                        }
//                                    });
//                                    mActivity.btn_cancel.setOnClickListener(new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View v) {
//                                            mActivity.showSureCancle(false);
//
//                                        }
//                                    });
//                                } else {
//
//                                    mActivity.onMessageSend(((MessageBtn) v.getTag()).getBtn_text());
//                                }
//
//
//                            }
//                        });
//                        if (i == info.getList().size() - 1) {
//                            vh.mViewGroup.setPadding(0, 0, 0, 18);
//                        }
                    layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    contentView.setLayoutParams(layoutParams);
                } else {
                    String str = info.getMessage();
                    for (int i = 0; i < info.getList().size(); i++) {

                        MessageBtn btn = info.getList().get(i);
                        if (info.getList().size() == 1) {

                            str += "<br>" + btn.getBtn_text();
                        } else {
                            str += "<br>" + (i + 1) + "、" + btn.getBtn_text();

                        }
                    }
                    vh.contentTextV.setText(Html.fromHtml(str));

                    layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    contentView.setLayoutParams(layoutParams);
                }
                break;
            case MessageInfo.SUANBING:
                mGreenDrawable = mActivity.getResources().getDrawable(ThemeUtils.getIdFromAttr(this.mActivity, R.attr.item_chat_l_dark_bg));
                contentView.setBackgroundDrawable(mGreenDrawable);
                vh.contentTextV.setTextColor(mChatTWhiteColor);
                layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                contentView.setLayoutParams(layoutParams);
                vh.contentTextV.setText(info.getMessage());
                if (info.viewlist != null) {
                    for (int i = 0; i < info.viewlist.size(); i++) {
                        vh.mViewGroup.addView(info.viewlist.get(i));
                    }
                    vh.mViewGroup.setPadding(0, 0, 0, 0);
                    return;
                }
                info.viewlist = new ArrayList<View>();
                Button button = null;
                /**suGroup 为其他或者更多中包含的数据,如果id为100时 将显示
                 */
                final LinearLayout suGroup = new LinearLayout(mActivity);
                suGroup.setOrientation(LinearLayout.VERTICAL);
                suGroup.setBackgroundColor(Color.WHITE);

                for (int i = 0; i < info.getList().size(); i++) {
                    final View btnGroup = mInflater.inflate(R.layout.layout_item_btn_single_green, null);
                    vh.mViewGroup.addView(btnGroup);
                    info.viewlist.add(btnGroup);
                    button = (Button) btnGroup.findViewById(R.id.btn);
                    button.setText(info.getList().get(i).getBtn_text());
                    button.setTag(info.getList().get(i));
                    final View view = btnGroup.findViewById(R.id.line2);
                    view.setVisibility(View.GONE);
                    if (i == info.getList().size() - 1) {
                        btnGroup.setBackgroundResource(ThemeUtils.getIdFromAttr(this.mActivity, R.attr.btn_down));
                    } else {
                        btnGroup.setBackgroundResource(ThemeUtils.getIdFromAttr(this.mActivity, R.attr.btn_middle));
                    }
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mActivity.phoneType = "";
                            if (!isEnable(position))
                                return;
                            if (((MessageBtn) v.getTag()).getId().equals("100")) {
                                if (suGroup.getVisibility() == View.GONE) {
                                    AnimaUtils.viewDownVisitly(suGroup);
                                    view.setVisibility(View.VISIBLE);
                                    btnGroup.setBackgroundResource(ThemeUtils.getIdFromAttr(ChatInfoAdapter.this.mActivity, R.attr.btn_middle));
                                    TalkEvent talkEvent = new TalkEvent();
                                    talkEvent.setWhat(TalkEvent.UPTALK);
                                    talkEvent.setArg1(position);
                                    talkEvent.setArg2(vh.mViewGroup.getHeight() - view.getHeight());
//                                talkEvent.setObject(suGroup);
                                    EventBus.getDefault().post(talkEvent);
                                } else {
                                    AnimaUtils.viewUpGone(suGroup);
                                    view.setVisibility(View.GONE);
                                    btnGroup.setBackgroundResource(ThemeUtils.getIdFromAttr(ChatInfoAdapter.this.mActivity, R.attr.btn_down));
                                }
                            } else {
                                LinearLayout linearLayouts = (LinearLayout) btnGroup.findViewById(R.id.desc_group);
                                if (linearLayouts.getVisibility() == View.VISIBLE) {
                                    linearLayouts.setVisibility(View.GONE);
                                    return;
                                } else if (linearLayouts.getChildCount() > 0 && linearLayouts.getVisibility() == View.GONE) {
                                    linearLayouts.setVisibility(View.VISIBLE);

                                } else {
                                    HttpRestClient.doHttpVirtualDoctor(((MessageBtn) v.getTag()).getBtn_text(), info.getSend_code(), new MJsonHttpResponseHandler(linearLayouts));
                                }
                            }
                        }
                    });
                }

                //解析更多中得数据


                LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                info.viewlist.add(suGroup);
                vh.mViewGroup.addView(suGroup, layoutParams2);
                suGroup.setVisibility(View.GONE);
                suGroup.setPadding(0, UnitUtil.dip2px(mActivity, 10), 0, 2);

                suGroup.setBackgroundDrawable(mActivity.getResources().getDrawable(R.drawable.btn_bg_gray));
                View su_desc = null;//点击更多显示布局  id为100
                for (int i = 0; i < info.getMorelist().size(); i++) {
                    if (i % 2 == 0) {
                        su_desc = mInflater.inflate(R.layout.layout_item_btn_double, null);
                        suGroup.addView(su_desc);
                        button = (Button) su_desc.findViewById(R.id.btn);
                    } else {
                        button = (Button) su_desc.findViewById(R.id.btn2);
                        button.setVisibility(View.VISIBLE);
                    }
                    button.setText(info.getMorelist().get(i).getBtn_text());
                    button.setTag(info.getMorelist().get(i));

                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mActivity.phoneType = "";
//                            oncliCkTouch(entity, (Button) v, 1, false);
                            if (!isEnable(position))
                                return;
                            Intent intent = new Intent(mActivity, CountActivity.class);
                            intent.putExtra("cont", ((MessageBtn) v.getTag()).getBtn_text());
                            mActivity.startActivity(intent);

                        }
                    });
                }
                vh.mViewGroup.setPadding(0, 0, 0, 0);
                break;
            case MessageInfo.DOCTORCARD:
                mWhriteDrawable = mActivity.getResources().getDrawable(ThemeUtils.getIdFromAttr(this.mActivity, R.attr.item_chat_l_light_bg));
                contentView.setBackgroundDrawable(mWhriteDrawable);
                vh.contentTextV.setTextColor(mChatTCommonColor);
                vh.mViewGroup.setPadding(0, 0, 0, 18);
                List<MessageDoctorInfo> messageDoctorInfoList = info.getDoctorMessageList();

                vh.contentTextV.setText(Html.fromHtml(info.getMessage()));
                View btnGroup;
                String office = "";
                for (int i = 0; i < messageDoctorInfoList.size(); i++) {
                    final MessageDoctorInfo mdInfo = messageDoctorInfoList.get(i);
                    btnGroup = mInflater.inflate(R.layout.item_doctor, null);
                    vh.mViewGroup.addView(btnGroup);
                    ImageView iv_head = (ImageView) btnGroup.findViewById(R.id.iv_head);
                    if (TextUtils.isEmpty(mdInfo.getHeadurl())) {
                        Picasso.with(mActivity).load(R.drawable.default_head_doctor).noFade().into(iv_head);
                    } else {
                        Picasso.with(mActivity).load(SystemUtils.toBrowserCode(mdInfo.getHeadurl())).noFade().error(R.drawable.default_head_doctor).into(iv_head);
//                        Picasso.with(mActivity).load("http://www.sysucc.org.cn/UploadFiles/expert/prouct_2014_1_17_DOBPP5YH.jpg").noFade().error(R.drawable.default_head_doctor).into(iv_head);

                    }
                    if (!office.equals(mdInfo.getOffice())) {
                        office = mdInfo.getOffice();
                        TextView tv_office = (TextView) btnGroup.findViewById(R.id.tv_office);
                        tv_office.setText(office + "：");
                        tv_office.setVisibility(View.VISIBLE);
                    }
                    TextView tv_name = (TextView) btnGroup.findViewById(R.id.tv_name);
                    tv_name.setText(Html.fromHtml(mdInfo.getName()));
                    TextView tv_job = (TextView) btnGroup.findViewById(R.id.tv_job);
                    tv_job.setText(Html.fromHtml(mdInfo.getJob()));
                    TextView tv_message = (TextView) btnGroup.findViewById(R.id.tv_message);
                    tv_message.setText(Html.fromHtml(mdInfo.getZc()));
                    TextView tv_out = (TextView) btnGroup.findViewById(R.id.tv_out);
                    String time = "";
                    String timei = "";
                    for (int j = 0; j < mdInfo.getList().size(); j++) {

                        time += (j == 0 ? "" : "，") + mdInfo.getList().get(j).getREGISTER_DATE();
                        timei += (j == 0 ? "" : "，") + mdInfo.getList().get(j).getREGISTER_DATE() + "，" + mdInfo.getList().get(j).getSESSION_TYPE();
                    }
                    tv_out.setText(Html.fromHtml(TextUtils.isEmpty(time) ? "暂无出诊信息" : time));

                    final String finalTimei = timei;
                    btnGroup.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mActivity, DoctorMessageActivity.class);
                            intent.putExtra("name", mdInfo.getName());
                            intent.putExtra("WORK_NUMBER", mdInfo.getWORK_NUMBER());
                            intent.putExtra("DEPT_CODE", mdInfo.getDEPT_CODE());
                            intent.putExtra("job", mdInfo.getJob());
                            intent.putExtra("hospital", mdInfo.getHospital());
                            intent.putExtra("zc", mdInfo.getZc());
                            intent.putExtra("jyjl", mdInfo.getJyjl());
                            intent.putExtra("headurl", mdInfo.getHeadurl());
                            intent.putExtra("zw", mdInfo.getZw());
                            intent.putExtra("time", finalTimei);
                            mActivity.startActivity(intent);


                        }
                    });

                }
                layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                contentView.setLayoutParams(layoutParams);
//                }

                break;
            case MessageInfo.CHECK_MORE:
                mWhriteDrawable = mActivity.getResources().getDrawable(ThemeUtils.getIdFromAttr(this.mActivity, R.attr.item_chat_l_light_bg));
                contentView.setBackgroundDrawable(mWhriteDrawable);
                vh.contentTextV.setTextColor(mChatTCommonColor);
                vh.mViewGroup.setPadding(0, 0, 0, 5);
                vh.contentTextV.setVisibility(View.VISIBLE);
                vh.contentTextV.setText(Html.fromHtml(info.getMessage()));
                vh.contentTextV.post(new Runnable() {
                    @Override
                    public void run() {
                        if (isEnable(position)) {
                            mActivity.setMore(info.getCheckMoreInfoList());
                        }

                    }
                });
                layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                contentView.setLayoutParams(layoutParams);
                break;
            case MessageInfo.TSCHECKBOX:
                mWhriteDrawable = mActivity.getResources().getDrawable(ThemeUtils.getIdFromAttr(this.mActivity, R.attr.item_chat_l_light_bg));
                contentView.setBackgroundDrawable(mWhriteDrawable);
                vh.contentTextV.setTextColor(mChatTCommonColor);
                vh.mViewGroup.setPadding(0, 0, 0, 0);
                final List<CheckBox> listCB = new ArrayList<>();
                if (isEnable(position)) {
                    vh.contentTextV.setText(Html.fromHtml(info.getMessage()));
                    View TSbtnGroup;
                    CheckBox TSbutton;
                    for (int i = 0; i < info.getTScheckMoreInfoList().size(); i++) {
                        TSbtnGroup = mInflater.inflate(R.layout.layout_item_check_double, null);
                        vh.mViewGroup.addView(TSbtnGroup);
                        TSbutton = (CheckBox) TSbtnGroup.findViewById(R.id.cb_btn);

                        final CheckMoreInfo checkMoreInfo = info.getTScheckMoreInfoList().get(i);
                        if (!TextUtils.isEmpty(checkMoreInfo.getColor())) {

                            TSbutton.setTextColor(Color.parseColor("#" + checkMoreInfo.getColor()));
                        }
                        TSbutton.setText(Html.fromHtml(checkMoreInfo.getMessage()));
                        listCB.add(TSbutton);
                        TSbutton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {


                                if (isChecked) {
                                    if (checkMoreInfo.getSingleDouble().equals(CheckMoreInfo.singlebtn)) {

                                        for (int i = 0; i < info.getTScheckMoreInfoList().size(); i++) {
                                            info.getTScheckMoreInfoList().get(i).setIsCheck(false);
                                            if (listCB != null && listCB.size() > i) {

                                                listCB.get(i).setChecked(false);
                                            }
                                            buttonView.setChecked(true);
                                        }
                                    } else if (checkMoreInfo.getSingleDouble().equals(CheckMoreInfo.doublebtn)) {
                                        for (int i = 0; i < info.getTScheckMoreInfoList().size(); i++) {
                                            if (info.getTScheckMoreInfoList().get(i).getSingleDouble().equals(CheckMoreInfo.singlebtn)) {
                                                info.getTScheckMoreInfoList().get(i).setIsCheck(false);
                                                if (listCB != null && listCB.size() > i) {

                                                    listCB.get(i).setChecked(false);
                                                }
                                            }
                                        }
                                    }
                                }


                                checkMoreInfo.setIsCheck(isChecked);
//                                notifyDataSetChanged();
                                mActivity.hideSound();

                                boolean isOpen = false;
                                for (int i = 0; i < info.getTScheckMoreInfoList().size(); i++) {
                                    if (info.getTScheckMoreInfoList().get(i).isCheck()) {
                                        isOpen = true;
                                        break;
                                    }
                                }
                                if (isOpen) {

                                    mActivity.showSureCancle(true);
                                } else {

                                    mActivity.showSureCancle(false);
                                }


                            }
                        });
                        TSbutton.setChecked(checkMoreInfo.isCheck());
                        if (i == info.getTScheckMoreInfoList().size() - 1) {
                            vh.mViewGroup.setPadding(0, 0, 0, 18);
                        }
                    }
                    mActivity.btn_sure.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String str = "";
                            for (int i = 0; i < info.getTScheckMoreInfoList().size(); i++) {
                                if (info.getTScheckMoreInfoList().get(i).isCheck()) {
                                    if (!TextUtils.isEmpty(str)) {
                                        str += ",";
                                    }

                                    str += info.getTScheckMoreInfoList().get(i).getMessage();
                                }
                            }
                            mActivity.showSureCancle(false);
                            mActivity.chat_input_text.setText("");

                            if (TextUtils.isEmpty(str)) {
                                mActivity.onMessageSend("没有");
                            } else {
                                mActivity.onMessageSend(str);

                            }


                        }
                    });
                    mActivity.btn_cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            for (int i = 0; i < vh.mViewGroup.getChildCount(); i++) {
                                CheckBox checkBox = (CheckBox) ((LinearLayout) vh.mViewGroup.getChildAt(i)).getChildAt(0);
                                checkBox.setChecked(false);
                            }

                        }
                    });
                    layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    contentView.setLayoutParams(layoutParams);
                } else {
                    mActivity.showSureCancle(false);
                    String str = info.getMessage();
                    for (int i = 0; i < info.getTScheckMoreInfoList().size(); i++) {

                        CheckMoreInfo btn = info.getTScheckMoreInfoList().get(i);
                        if (info.getTScheckMoreInfoList().size() == 1) {

                            str += "<br>" + btn.getMessage();
                        } else {
                            str += "<br>" + (i + 1) + "、" + btn.getMessage();

                        }
                    }
                    vh.contentTextV.setText(Html.fromHtml(str));

                    layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    contentView.setLayoutParams(layoutParams);
                }
                break;
            case MessageInfo.TIMESHOW:
                mWhriteDrawable = mActivity.getResources().getDrawable(ThemeUtils.getIdFromAttr(this.mActivity, R.attr.item_chat_l_light_bg));
                contentView.setBackgroundDrawable(mWhriteDrawable);
                vh.contentTextV.setTextColor(mChatTCommonColor);
                vh.mViewGroup.setPadding(0, 0, 0, 5);
                vh.contentTextV.setVisibility(View.VISIBLE);
                vh.contentTextV.setText(Html.fromHtml(info.getMessage()));
                vh.contentTextV.post(new Runnable() {
                    @Override
                    public void run() {
                        if (isEnable(position)) {
                            mActivity.setTime();
                        }
                    }
                });
                layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                contentView.setLayoutParams(layoutParams);
                break;
        }
        if (info.getChongxin_shuru().equals("1") && !isEnable(position)) {
            addCXSR(vh.mViewGroup, info);
        }
    }

    public void addCXSR(final LinearLayout mViewGroup, final MessageInfo info) {
        View btnGroup;
        Button button;
        btnGroup = mInflater.inflate(R.layout.layout_item_btn_single, null);
        mViewGroup.addView(btnGroup);
        button = (Button) btnGroup.findViewById(R.id.btn);

        button.setText("重新回答");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.phoneType = "";
                showPop(v, "是否重新回答?", info);

            }
        });
    }

    static class LeftViewHolder {
        public TextView timeTextV;
        public ImageView imageViewDianDian;
        public CircleImageView chat_head;
        public View timeView;
        public TextView contentTextV;
        public LinearLayout mViewGroup;//添加空间的父布局
        public LinearLayout mContent_layout;//添加空间的父布局
    }

    static class RightViewHolder {
        public TextView timeTextV;
        public TextView contentTextV;
        public ImageView imageView;
        public View timeView;
    }


    /**
     * 停止语音
     */
    public void stopRecoard() {
        mSpeechUtils.stopPeed();
    }

    /**
     * 释放语音
     */
    public void destorySpeed() {
        mSpeechUtils.destory();
    }


    PopupWindow popOfficeWindow;
    LinearLayout popOfficeView;
    View.OnLongClickListener copyOnClick = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            showPopOfficeWindow(v);
            return true;
        }


    };

    private void showPopOfficeWindow(final View view) {
        if (popOfficeWindow != null && popOfficeWindow.isShowing()) {
            popOfficeWindow.dismiss();
            // titleRightBtn2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.selete_room, 0, 0, 0);
            return;
        }
        // titleRightBtn2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.im_more_press, 0, 0, 0);

        if (popOfficeWindow == null) {
            popOfficeView = new LinearLayout(mActivity);
            popOfficeView.setPadding(0, 10, 0, 10);
            popOfficeView.setBackgroundColor(0xff000000);
            popOfficeView.setGravity(Gravity.CENTER);
            popOfficeView.setOrientation(LinearLayout.HORIZONTAL);
            TextView textView = new TextView(mActivity);
            textView.setTextColor(Color.WHITE);
            textView.setText("拷贝");
            textView.setGravity(Gravity.CENTER);
            popOfficeView.setBackgroundDrawable(mActivity.getResources().getDrawable(R.drawable.longenter_pop));
            popOfficeView.addView(textView);
            popOfficeWindow = new PopupWindow(popOfficeView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
            popOfficeWindow.setTouchable(true);
            // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
            // 我觉得这里是API的一个bug
            popOfficeWindow.setBackgroundDrawable(new BitmapDrawable());
//            popOfficeWindow.setAnimationStyle(R.style.poPPreview);
//            return;
        } else {
            // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
            // 我觉得这里是API的一个bug
            popOfficeWindow.setBackgroundDrawable(new BitmapDrawable());
//            popOfficeWindow.setAnimationStyle(R.style.poPPreview);

        }

        popOfficeView.getChildAt(0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipData myClip = ClipData.newPlainText("text", ((TextView) (view)).getText().toString());
                ((ClipboardManager) mActivity.getSystemService(mActivity.CLIPBOARD_SERVICE)).setPrimaryClip(myClip);
                popOfficeWindow.dismiss();
            }
        });

        popOfficeWindow.showAsDropDown(view, 0, (int) (-view.getHeight() - 2 * ((TextView) (view)).getTextSize()));
    }


    private class MJsonHttpResponseHandler extends JsonHttpResponseHandler {

        LinearLayout linearLayouts;

        public MJsonHttpResponseHandler(LinearLayout linearLayouts) {
            super(mActivity);
            this.linearLayouts = linearLayouts;
        }

        public void onSuccess(int statusCode, JSONObject response) {//成功
            MKLog.e("a", response.toString());
            try {
                String id = response.getJSONObject("server_params").optString("customer_id");
                if (!TextUtils.isEmpty(id)) {
                    LoginHttp.getLoginHttp().setLoginId(id);
                }
                JSONArray jsonArray = new JSONArray(response.getJSONObject("server_params").optString("content"));
                linearLayouts.removeAllViews();
                for (int i = 0; i < jsonArray.length(); i++) {

                    final JSONObject jsb = jsonArray.getJSONObject(i);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    //文字
                    TextView t = new TextView(mActivity);
                    t.setText(Html.fromHtml(SystemUtils.getHtmlChange(jsb.optString("cont"))));
                    t.setTextSize(16);
                    t.setMovementMethod(LinkMovementMethod.getInstance());
                    layoutParams.setMargins(12, 10, 10, 10);
                    t.setLayoutParams(layoutParams);
                    linearLayouts.addView(t);
                    linearLayouts.setVisibility(View.VISIBLE);

//                    动态添加五个按钮
                    final View view = mInflater.inflate(R.layout.good_bad_share_layout, null);


                    final ImageView talk_icon = (ImageView) view.findViewById(R.id.talk_icon);
                    talk_icon.setSelected(false);
                    talk_icon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mActivity.phoneType = "";
                            talk_icon.setSelected(!talk_icon.isSelected());
                            mSpeechUtils.startPeed(jsb.optString("cont"), talk_icon);
                        }
                    });
                    linearLayouts.addView(view);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    PopupWindow pw;

    public void showPop(View view, final String content, final MessageInfo info) {
        LinearLayout layout;
        if (pw == null) {
            layout = (LinearLayout) View.inflate(mActivity, R.layout.popask, null);

            pw = new PopupWindow(layout, mActivity.getResources().getDisplayMetrics().widthPixels * 80 / 100, ViewGroup.LayoutParams.WRAP_CONTENT, true);
            pw.setOutsideTouchable(true);
            pw.setBackgroundDrawable(new ColorDrawable(0x00000000));
            pw.setTouchable(true);
            pw.setFocusable(true);

        } else {
            layout = (LinearLayout) pw.getContentView();
        }
        TextView tv_ask = (TextView) layout.findViewById(R.id.tv_ask);
        tv_ask.setText(content);
        TextView tv_cancel = (TextView) layout.findViewById(R.id.tv_cancle);
        TextView tv_sure = (TextView) layout.findViewById(R.id.tv_sure);
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pwDismiss();
            }
        });
        tv_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.phoneType = "";
                list.add(info);
                notifyDataSetChanged();
                mListView.setSelection(list.size());
                pwDismiss();
            }
        });
        pw.showAtLocation(view, Gravity.CENTER, 0, 0);

    }

    void pwDismiss() {
        if (pw != null && pw.isShowing()) {

            pw.dismiss();
        }
    }


    public boolean isEnable(int position) {
        boolean isEnable = true;
        for (int i = position + 1; i < list.size(); i++) {
            if (!list.get(i).isSystemMessage()) {
                isEnable = false;
                break;
            }
        }
        return isEnable;
    }

}
