package com.dmsj.newask.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.dmsj.newask.HApplication;
import com.dmsj.newask.Info.CheckMoreInfo;
import com.dmsj.newask.Info.DoctorOutInfo;
import com.dmsj.newask.Info.MessageBtn;
import com.dmsj.newask.Info.MessageDoctorInfo;
import com.dmsj.newask.Info.MessageInfo;
import com.dmsj.newask.Info.TalkEvent;
import com.dmsj.newask.R;
import com.dmsj.newask.Views.OnWheelChangedListener;
import com.dmsj.newask.Views.RippleLayout;
import com.dmsj.newask.Views.WheelView;
import com.dmsj.newask.adapter.ChatInfoAdapter;
import com.dmsj.newask.adapter.NumericWheelAdapter;
import com.dmsj.newask.adapter.QuestionListAdapter;
import com.dmsj.newask.http.HttpRestClient;
import com.dmsj.newask.http.JsonHttpResponseHandler;
import com.dmsj.newask.http.LodingFragmentDialog;
import com.dmsj.newask.http.LoginHttp;
import com.dmsj.newask.http.RequestParams;
import com.dmsj.newask.page_grid.PageIndicatorView;
import com.dmsj.newask.page_scroll.HorizontalPageLayoutManager;
import com.dmsj.newask.page_scroll.PagingItemDecoration;
import com.dmsj.newask.page_scroll.PagingScrollHelper;
import com.dmsj.newask.utils.JsonParser;
import com.dmsj.newask.utils.MKLog;
import com.dmsj.newask.utils.RingPlayer;
import com.dmsj.newask.utils.SharePreUtils;
import com.dmsj.newask.utils.SystemUtils;
import com.dmsj.newask.utils.ThemeUtils;
import com.dmsj.newask.utils.TimeShow;
import com.dmsj.newask.utils.TimeUtil;
import com.dmsj.newask.utils.ToastUtil;
import com.dmsj.newask.utils.WeakHandler;
import com.dmsj.newask.utils.WheelUtils;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;

import org.handmark.pulltorefresh.library.PullToRefreshBase;
import org.handmark.pulltorefresh.library.PullToRefreshListView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import de.greenrobot.event.EventBus;


/**
 * 聊天界面
 * Created by Mickey.Li on 2016-3-17.
 */
public class ChatActivity extends FragmentActivity implements View.OnClickListener, PagingScrollHelper.onPageChangeListener {

    private static final String TAG = "ChatActivity";
    public static ChatActivity chatActivity;
    private ImageView chat_send_voice, chat_send_text, title_change, imageViewThemeChange, im_src;
    public EditText chat_input_text;
    private PullToRefreshListView chat_info_lv;
    private ListView mListView;
    public ChatInfoAdapter mChatAapter;
    private TextView chat_speech_tv, tv_title, tv_help;
    public final int AskMessage = 1;
    public final int AskMessageTime = 10 * 1000;
    TextView rip_text, tv_language;
    public String phoneType = "1";
    LinearLayout ll_more;
    public static Class<?> DoctorClass;
    public static String userid = "";
    public static String deviceid = "";
    private QuestionListAdapter questionListAdapter;
    private RecyclerView rv_question_list;
    private PageIndicatorView page_indicator;


    class MJsonHttpResponseHandler extends JsonHttpResponseHandler {


        public void onSuccess(int statusCode, JSONObject response) {//成功
            MKLog.e("fxc", response.toString());
            phoneType = "";
            messJson(response, false);
            mListView.setSelection(mChatAapter.getCount());

        }

        @Override
        public void onStart() {
            super.onStart();
            mListView.setSelection(mChatAapter.getCount());


        }
    }


    public void messJson(JSONObject response, boolean isUP) {
        try {
            String id = response.getJSONObject("server_params").optString("customer_id");
            if (!TextUtils.isEmpty(id)) {
                LoginHttp.getLoginHttp().setLoginId(id);
//                tv_title.setText(id);
            }
            JSONArray jsonArray = new JSONArray(response.getJSONObject("server_params").optString("content"));
//            JSONArray jsonArray = new JSONArray(response.getString("content"));
            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject jsb = jsonArray.getJSONObject(i);
                messJson(jsb, response, true, isUP, true);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void messJson(JSONObject jsb, JSONObject response, Boolean isAdd, boolean isUP, boolean isFinish) throws JSONException {

        long message_time = TimeUtil.getMesgTime(response.optString("message_time"));
        String chongxin_shuru = jsb.optString("chongxin_shuru");
        String send_code = response.optString("message_id");
        boolean isSystemMessage = "1".equals(jsb.optString("system_message").replace("null", ""));
        send_code = TextUtils.isEmpty(send_code) ? "999999" : send_code;

        if (response.optString("metadata").contains("customer_info")) {
            mChatAapter.onDataChange(MessageInfo.addReceiveMessage(SystemUtils.getHtmlChange(jsb.optString("cont")), message_time, chongxin_shuru, send_code, isSystemMessage), isAdd, isUP, isFinish, false);
            JSONObject jsonObject = new JSONObject(response.optString("metadata"));
            String info = jsonObject.optString("customer_info");
            Intent intent = new Intent(chatActivity, DoctorChatMyinformation.class);
            intent.putExtra("modify_Content", info);
            startActivityForResult(intent, 500);
        } else if (jsb.toString().contains("sub_titles")) {
            JSONArray jsonMBtns = jsb.getJSONArray("btns");
            List<MessageBtn> moreList = new ArrayList<MessageBtn>();
            for (int j = 0; j < jsonMBtns.length(); j++) {
                JSONObject jsbbtn = jsonMBtns.getJSONObject(j);
                moreList.add(MessageBtn.addbtn(jsbbtn.optString("id"), SystemUtils.getHtmlChange(jsbbtn.optString("cont")), jsbbtn.optString("Option_Color"), jsbbtn.optString("Option_Confirm")));
            }
            JSONArray jsonBtns = jsb.getJSONArray("sub_titles");
            List<MessageBtn> list = new ArrayList<MessageBtn>();
            for (int j = 0; j < jsonBtns.length(); j++) {
                JSONObject jsbbtn = jsonBtns.getJSONObject(j);
                list.add(MessageBtn.addbtn(jsbbtn.optString("id"), SystemUtils.getHtmlChange(jsbbtn.optString("cont")), jsbbtn.optString("Option_Color"), jsbbtn.optString("Option_Confirm")));
            }
            mChatAapter.onDataChange(MessageInfo.addReceiveMoreList(SystemUtils.getHtmlChange(jsb.optString("cont")), list, moreList, message_time, chongxin_shuru, send_code, isSystemMessage), isAdd, isUP, isFinish, false);

        } else if (jsb.toString().contains("btnduoxuan") && jsb.getString("btnduoxuan").equals("1")) {
//                        JSONArray jsonBtns = new JSONArray(jsb.getString("btns"));
            JSONArray jsonBtns = jsb.getJSONArray("btns");
            List<CheckMoreInfo> list = new ArrayList<CheckMoreInfo>();
            for (int j = 0; j < jsonBtns.length(); j++) {
                JSONObject jsbbtn = jsonBtns.getJSONObject(j);
                list.add(CheckMoreInfo.addInfo(SystemUtils.getHtmlChange(jsbbtn.optString("cont")), jsbbtn.optString("option_type"), jsbbtn.optString("Option_Color")));

            }

            mChatAapter.onDataChange(MessageInfo.addTSCheckMoreList(SystemUtils.getHtmlChange(jsb.optString("cont")), list, message_time, chongxin_shuru, send_code, isSystemMessage), isAdd, isUP, isFinish, false);
        } else if (jsb.toString().contains("btns")) {
//                        JSONArray jsonBtns = new JSONArray(jsb.getString("btns"));
            JSONArray jsonBtns = jsb.getJSONArray("btns");
            List<MessageBtn> list = new ArrayList<MessageBtn>();
            for (int j = 0; j < jsonBtns.length(); j++) {
                JSONObject jsbbtn = jsonBtns.getJSONObject(j);
                list.add(MessageBtn.addbtn(jsbbtn.optString("id"), SystemUtils.getHtmlChange(jsbbtn.optString("cont")), jsbbtn.optString("Option_Color"), jsbbtn.optString("Option_Confirm")));
            }

            mChatAapter.onDataChange(MessageInfo.addReceiveList(SystemUtils.getHtmlChange(jsb.optString("cont")), jsb.optInt("btn_align"), list, message_time, chongxin_shuru, send_code, isSystemMessage), isAdd, isUP, isFinish, false);
        }
        //////4.19
        else if (jsb.toString().contains("doctors")) {
//                        JSONArray jsonBtns = new JSONArray(jsb.getString("btns"));
            JSONArray jsonBtns = new JSONArray(jsb.getString("doctors"));
            List<MessageDoctorInfo> list = new ArrayList<MessageDoctorInfo>();

            for (int k = 0; k < jsonBtns.length(); k++) {
                JSONObject jsbDoctors = jsonBtns.getJSONObject(k);
                String office = SystemUtils.getHtmlChange(jsbDoctors.optString("office"));
                JSONArray jsonBtnss = new JSONArray(jsbDoctors.getString("doctors"));
                for (int j = 0; j < jsonBtnss.length(); j++) {
                    JSONObject jsbDoctor = jsonBtnss.getJSONObject(j);
                    List<DoctorOutInfo> listOut = new ArrayList<DoctorOutInfo>();
                    if (jsbDoctor.toString().contains("list")) {
                        JSONArray jsonArrayOut = jsbDoctor.getJSONArray("list");
                        for (int m = 0; m < jsonArrayOut.length(); m++) {
                            JSONObject jsonOut = jsonArrayOut.getJSONObject(m);
                            listOut.add(DoctorOutInfo.addInfo(jsonOut.optString("NOON_NAME"), jsonOut.optString("REGISTER_DATE"), jsonOut.optString("SESSION_TYPE")));
                        }
                    }
                    list.add(MessageDoctorInfo.addInfo(SystemUtils.getHtmlChange(jsbDoctor.optString("DOCTOR_CODE")), SystemUtils.getHtmlChange(jsbDoctor.optString("DOCTOR_NAME")), SystemUtils.getHtmlChange(jsbDoctor.optString("DOCTOR_TITLE")), SystemUtils.getHtmlChange(jsbDoctor.optString("DOCTOR_DESC")), SystemUtils.getHtmlChange(jsbDoctor.optString("DOCTOR_ICON")), SystemUtils.getHtmlChange(jsbDoctor.optString("CUREENT_WORK")), SystemUtils.getHtmlChange(jsbDoctor.optString("EXPERIENCE")), SystemUtils.getHtmlChange(jsbDoctor.optString("DOCTOR_DUTIES")), office, SystemUtils.getHtmlChange(jsbDoctor.optString("WORK_NUMBER")), SystemUtils.getHtmlChange(jsbDoctor.optString("DEPT_CODE")), listOut));
                }
            }


            mChatAapter.onDataChange(MessageInfo.addDoctorMessageList(SystemUtils.getHtmlChange(jsb.optString("cont")), list, message_time, chongxin_shuru, send_code, isSystemMessage), isAdd, isUP, isFinish, false);
        } else if (jsb.toString().contains("situations")) {
//                        JSONArray jsonBtns = new JSONArray(jsb.getString("btns"));
            JSONArray jsonBtns = jsb.getJSONArray("situations");
            List<CheckMoreInfo> list = new ArrayList<CheckMoreInfo>();
            for (int j = 0; j < jsonBtns.length(); j++) {
                list.add(CheckMoreInfo.addInfo(SystemUtils.getHtmlChange(jsonBtns.getString(j)), "2", ""));
            }

            mChatAapter.onDataChange(MessageInfo.addCheckMoreList(SystemUtils.getHtmlChange(jsb.optString("cont")), list, message_time, chongxin_shuru, send_code, isSystemMessage), isAdd, isUP, isFinish, false);
//            if (isAdd) {
//
//                setMore(list);
//            }
            SystemUtils.hideSoftBord(chatActivity, chat_input_text);


        } else if (jsb.toString().contains("time_valid") && jsb.optString("time_valid").equals("1")) {

            mChatAapter.onDataChange(MessageInfo.addTIME(SystemUtils.getHtmlChange(jsb.optString("cont")), message_time, chongxin_shuru, send_code, isSystemMessage), isAdd, isUP, isFinish, false);

        }

        //////4.19
        else {
            mChatAapter.onDataChange(MessageInfo.addReceiveMessage(SystemUtils.getHtmlChange(jsb.optString("cont")), message_time, chongxin_shuru, send_code, isSystemMessage), isAdd, isUP, isFinish, false);

        }
    }

    public Button btn_sure, btn_cancel;
    public LinearLayout ll_btn;


    public void showSureCancle(Boolean isShow) {
        if (ll_btn == null)
            return;
        if (isShow) {

            if (ll_btn.getVisibility() != View.VISIBLE) {
                ll_btn.setVisibility(View.VISIBLE);
            }
        } else {
            if (ll_btn.getVisibility() != View.GONE) {
                ll_btn.setVisibility(View.GONE);
            }
        }

    }

    PagingScrollHelper scrollHelper = new PagingScrollHelper();
    RecyclerView.ItemDecoration itemDecoration = null;
    RecyclerView.LayoutManager layoutManager = null;
    private HorizontalPageLayoutManager horizontalPageLayoutManager = null;
    private PagingItemDecoration pagingItemDecoration = null;

    // 横向滚动
    public void setMore(final List<CheckMoreInfo> list) {
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                list.get(i).setIsSelect(0);
            } else {
                list.get(i).setIsSelect(1);
            }
        }
        if (ll_more.getChildCount() != 0) {
            return;
        }
        showSureCancle(false);
        View chGroup;
        chGroup = View.inflate(this, R.layout.item_somecheck_ch_02, null);
        ll_more.addView(chGroup);
        // 获取列表控件
        rv_question_list = chGroup.findViewById(R.id.rv_question_list);
        // 获取底部指针控件
        page_indicator = chGroup.findViewById(R.id.page_indicator);
        // 设置列表布局
        horizontalPageLayoutManager = new HorizontalPageLayoutManager(3, 3);
        pagingItemDecoration = new PagingItemDecoration(this, horizontalPageLayoutManager);
        layoutManager = horizontalPageLayoutManager;
        itemDecoration = pagingItemDecoration;
        rv_question_list.setLayoutManager(layoutManager);
        // 初始化适配器
        questionListAdapter = new QuestionListAdapter(ChatActivity.this);
        rv_question_list.setAdapter(questionListAdapter);
        questionListAdapter.setList(list);
        scrollHelper.setUpRecycleView(rv_question_list);
        scrollHelper.setOnPageChangeListener(this);
        rv_question_list.setHorizontalScrollBarEnabled(true);
        //获取总页数,采用这种方法才能获得正确的页数。否则会因为RecyclerView.State 缓存问题，页数不正确。
        rv_question_list.post(new Runnable() {
            @Override
            public void run() {
                //tv_page_total.setText("共" + scrollHelper.getPageCount() + "页");
                int pageCount = scrollHelper.getPageCount();
                // 设置指针监听
                page_indicator.initIndicator(pageCount);
            }
        });
        questionListAdapter.setHideSound(new QuestionListAdapter.OnCheckbox_hideSound() {
            @Override
            public void onCheckbox_hideSound() {
                hideSound();
            }
        });
        questionListAdapter.setOnCheckboxItem(new QuestionListAdapter.OnCheckboxItem() {
            @Override
            public void onCheckboxItem(boolean isOpen, String message) {
                if (isOpen) {
                    showSureCancle(true);
                } else {
                    showSureCancle(false);
                }
            }
        });

        btn_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = "";
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).isCheck()) {
                        if (!TextUtils.isEmpty(str)) {
                            str += ",";
                        }
                        str += list.get(i).getMessage();
                    }
                }
                ll_more.removeAllViews();
                showSureCancle(false);
                chat_input_text.setText("");

                if (TextUtils.isEmpty(str)) {
                    onMessageSend("没有");
                } else {
                    onMessageSend(str);

                }


            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < list.size(); i++) {
                    list.get(i).setIsCheck(false);
                }
                questionListAdapter.setList(list);
                showSureCancle(false);
            }
        });

    }

    @Override
    public void onPageChange(int index) {
        //tv_title.setText("第" + (index + 1) + "页");
        page_indicator.setSelectedPage(index);
    }


//    // 横向滚动
//    public void setMore(final List<CheckMoreInfo> list) {
//        if (ll_more.getChildCount() != 0) {
//            return;
//        }
//        showSureCancle(false);
//        View chGroup;
//        chGroup = View.inflate(this, R.layout.item_somecheck, null);
//        ll_more.addView(chGroup);
//        final LinearLayout layout = (LinearLayout) chGroup.findViewById(R.id.ll_h);
//        final HorizontalScrollView scroll_view = (HorizontalScrollView) chGroup.findViewById(R.id.scroll_view);
//        scroll_view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                scroll_view.arrowScroll(View.FOCUS_RIGHT);
//            }
//        });
//        for (int i = 0; i < list.size(); i++) {
//            LinearLayout ll_v;
//            if (layout.getChildCount() == 0) {
//                ll_v = new LinearLayout(this);
//                ll_v.setOrientation(LinearLayout.VERTICAL);
//                layout.addView(ll_v);
//            } else {
//                ll_v = (LinearLayout) layout.getChildAt(layout.getChildCount() - 1);
//                if (ll_v.getChildCount() == 3) {
//                    ll_v = new LinearLayout(this);
//                    ll_v.setOrientation(LinearLayout.VERTICAL);
//                    layout.addView(ll_v);
//                }
//            }
//            CheckMoreInfo mdInfo = list.get(i);
//            final CheckBox checkBox = (CheckBox) View.inflate(this, R.layout.item_check_box, null);
//            ll_v.addView(checkBox);
//            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) checkBox.getLayoutParams();
//            lp.setMargins(5, 5, 5, 5);
//            checkBox.setText(mdInfo.getMessage());
//            checkBox.setChecked(mdInfo.isCheck());
//            checkBox.setSelected(true);
//            final int finalI = i;
//            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                @Override
//                public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
//                    hideSound();
//                    list.get(finalI).setIsCheck(isChecked);
//                    buttonView.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            boolean isOpen = false;
//                            for (int i = 0; i < list.size(); i++) {
//                                if (list.get(i).isCheck()) {
//                                    isOpen = true;
//                                    break;
//                                }
//                            }
//                            if (isOpen) {
//                                showSureCancle(true);
//                            } else {
//                                showSureCancle(false);
//                            }
//                        }
//                    });
//                }
//            });
//
//        }
//
//
//        btn_sure.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String str = "";
//                for (int i = 0; i < list.size(); i++) {
//                    if (list.get(i).isCheck()) {
//                        if (!TextUtils.isEmpty(str)) {
//                            str += ",";
//                        }
//
//                        str += list.get(i).getMessage();
//                    }
//                }
//                ll_more.removeAllViews();
//                showSureCancle(false);
//                chat_input_text.setText("");
//
//                if (TextUtils.isEmpty(str)) {
//                    onMessageSend("没有");
//                } else {
//                    onMessageSend(str);
//
//                }
//
//
//            }
//        });
//        btn_cancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                for (int i = 0; i < layout.getChildCount(); i++) {
//                    for (int j = 0; j < ((LinearLayout) layout.getChildAt(i)).getChildCount(); j++) {
//                        CheckBox checkBox = (CheckBox) ((LinearLayout) layout.getChildAt(i)).getChildAt(j);
//                        checkBox.setChecked(false);
//                    }
//                }
//
//            }
//        });
//    }


    public void setTime() {
        if (ll_more.getChildCount() != 0) {
            return;
        }
        showSureCancle(false);
        final ScrollView wheelView = (ScrollView) View.inflate(chatActivity, R.layout.treable_wheel, null);
        final WheelView wheelOne = (WheelView) wheelView.findViewById(R.id.wheel_one);
        final WheelView wheelTwo = (WheelView) wheelView.findViewById(R.id.wheel_two);
        final WheelView wheelThree = (WheelView) wheelView.findViewById(R.id.wheel_three);
        Button wheelCancel = (Button) wheelView.findViewById(R.id.wheel_cancel);
        Button wheelSure = (Button) wheelView.findViewById(R.id.wheel_sure);


        wheelCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        wheelSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ll_more.removeAllViews();
                showSureCancle(false);
                chat_input_text.setText("");
                String str = wheelOne.getAdapter().getItem(wheelOne.getCurrentItem()) + "," + wheelTwo.getAdapter().getItem(wheelTwo.getCurrentItem()) + "," + wheelThree.getAdapter().getItem(wheelThree.getCurrentItem());
                onMessageSend(str);
            }
        });
        wheelOne.setAdapter(new NumericWheelAdapter(1980, Calendar.getInstance().get(Calendar.YEAR)));
        wheelOne.setCyclic(false);
        wheelOne.setInterpolator(new AnticipateOvershootInterpolator());
        wheelTwo.setAdapter(new NumericWheelAdapter(1, 12));
        wheelTwo.setCyclic(false);
        wheelTwo.setInterpolator(new AnticipateOvershootInterpolator());
        wheelThree.setAdapter(new NumericWheelAdapter(1, 30));
        wheelThree.setCyclic(false);
        wheelThree.setInterpolator(new AnticipateOvershootInterpolator());
        wheelOne.addChangingListener(new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                if (wheelOne.getCurrentItem() == 0) {
                    wheelTwo.setCurrentItem(0);
                    wheelThree.setCurrentItem(0);
                } else {

                    int oldNum = wheelTwo.getCurrentItem();
                    int n;
                    if (Integer.parseInt(wheelOne.getAdapter().getItem(wheelOne.getCurrentItem())) >= Calendar.getInstance().get(Calendar.YEAR)) {
                        n = Calendar.getInstance().get(Calendar.MONTH) + 1;
                    } else {
                        n = 12;
                    }

                    if (wheelTwo.getAdapter().getItemsCount() - 1 != n)
                        wheelTwo.setAdapter(new NumericWheelAdapter(1, n));
                    if (oldNum > n) {
                        wheelTwo.setCurrentItem(wheelTwo.getAdapter().getItemsCount() - 1);
                    }

                    if (wheelTwo.getCurrentItem() == 0) {
                        wheelThree.setCurrentItem(0);
                    } else {
                        oldNum = wheelThree.getCurrentItem();

                        if (Integer.parseInt(wheelOne.getAdapter().getItem(wheelOne.getCurrentItem())) >= Calendar.getInstance().get(Calendar.YEAR) && Integer.parseInt(wheelTwo.getAdapter().getItem(wheelTwo.getCurrentItem())) >= Calendar.getInstance().get(Calendar.MONTH) + 1) {
                            n = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
                        } else {

                            n = TimeShow.getDayOfMonth(wheelOne.getAdapter().getItem(wheelOne.getCurrentItem()), wheelTwo.getAdapter().getItem(wheelTwo.getCurrentItem()));
                        }
                        if (wheelThree.getAdapter().getItemsCount() - 1 != n)
                            wheelThree.setAdapter(new NumericWheelAdapter(1, n));
                        if (oldNum > n) {
                            wheelThree.setCurrentItem(wheelThree.getAdapter().getItemsCount() - 1);
                        }

                    }
                }
            }
        });
        wheelTwo.addChangingListener(new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                if (wheelOne.getCurrentItem() == 0) {
                    wheelTwo.setCurrentItem(0);
                } else if (wheelTwo.getCurrentItem() == 0) {
                    wheelThree.setCurrentItem(0);
                } else {
                    int oldNum = wheelThree.getCurrentItem();
                    int n;
                    if (Integer.parseInt(wheelOne.getAdapter().getItem(wheelOne.getCurrentItem())) >= Calendar.getInstance().get(Calendar.YEAR) && Integer.parseInt(wheelTwo.getAdapter().getItem(wheelTwo.getCurrentItem())) >= Calendar.getInstance().get(Calendar.MONTH) + 1) {
                        n = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
                    } else {

                        n = TimeShow.getDayOfMonth(wheelOne.getAdapter().getItem(wheelOne.getCurrentItem()), wheelTwo.getAdapter().getItem(wheelTwo.getCurrentItem()));
                    }
                    if (wheelThree.getAdapter().getItemsCount() - 1 != n)
                        wheelThree.setAdapter(new NumericWheelAdapter(1, n));
                    if (oldNum > n) {
                        wheelThree.setCurrentItem(wheelThree.getAdapter().getItemsCount() - 1);
                    }

                }
            }
        });
        wheelThree.addChangingListener(new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                if (wheelTwo.getCurrentItem() == 0) {
                    wheelThree.setCurrentItem(0);
                }
            }
        });
        wheelOne.setCurrentItem(wheelOne.getAdapter().getItemsCount() - 1);
        ll_more.addView(wheelView);

    }

    /**
     * 发送的数据有三种形式：文字、图片和语音
     * 目前只有文字方式
     */
    public void onMessageSend(Object obj) {

        if (mChatAapter.getCount() == 0) {
            return;
        }
        if (ll_more != null && ll_more.getChildCount() != 0)
            ll_more.removeAllViews();
        showSureCancle(false);
        MessageInfo info = MessageInfo.addSendMessage(obj.toString(), "999999", System.currentTimeMillis());
        mChatAapter.onDataChange(info, true, false, true, false);
        mListView.setSelection(mChatAapter.getCount());
        String str = SystemUtils.getStringChange(obj.toString());
        HttpRestClient.doHttpVirtualDoctor(str, "", new MJsonHttpResponseHandler());
//        HttpRestClient.doHttpVirtualDoctor(obj.toString(), asyncHttpResponseHandler);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        if (savedInstanceState != null && savedInstanceState.getBoolean("message")) {
//            startActivity(new Intent(this, WelcomeActivity.class));
//            finish();
//            return;
//        }
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setTheme(WelcomeActivity.themeId);

        SystemUtils.setWindowStatusBarColor(this, R.attr.titleColor);

        setContentView(R.layout.activity_chat);
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        HApplication hApplication = new HApplication();
        hApplication.init(this);
        LoginHttp.getLoginHttp().setLoginId("");
        phoneType = "1";
        chatActivity = this;
        Intent intent = getIntent();
        userid = intent.getStringExtra("appUserId");
        userid = TextUtils.isEmpty(userid) ? "" : userid;
        deviceid = intent.getStringExtra("appDeviceId");
        deviceid = TextUtils.isEmpty(deviceid) ? "" : deviceid;
        DoctorClass = (Class<?>) getIntent().getSerializableExtra("doctorClass");
        initView();
        initVoiceData();
//        HistroyMessage(true);
        firstGet();
    }


    private void initView() {
        im_src = (ImageView) findViewById(R.id.title_setting);
        im_src.setOnClickListener(this);
        ll_more = (LinearLayout) findViewById(R.id.ll_more);
//        im_src.setImageDrawable(getResources().getDrawable(WelcomeActivity.headdrawid));
        tv_title = (TextView) findViewById(R.id.title_text);
        tv_title.setText(WelcomeActivity.name);
        btn_sure = (Button) findViewById(R.id.btn_sure);
        btn_cancel = (Button) findViewById(R.id.btn_cancel);
        ll_btn = (LinearLayout) findViewById(R.id.ll_btn);
        chat_info_lv = (PullToRefreshListView) findViewById(R.id.chat_info_lv);

        mListView = chat_info_lv.getRefreshableView();
        mChatAapter = new ChatInfoAdapter(chatActivity);
        mListView.setAdapter(mChatAapter);
        mListView.setSelector(new BitmapDrawable());
        mChatAapter.setListView(mListView);

        chat_send_voice = (ImageView) findViewById(R.id.chat_send_voice);
        tv_language = (TextView) findViewById(R.id.tv_language);
        chat_send_voice.setOnClickListener(this);
        chat_send_text = (ImageView) findViewById(R.id.chat_send_text);
        chat_send_text.setOnClickListener(this);
        chat_input_text = (EditText) findViewById(R.id.chat_input_text);
        chat_input_text.setOnClickListener(this);
        chat_speech_tv = (TextView) findViewById(R.id.chat_speech_tv);
        chat_speech_tv.setClickable(true);
        ThemeUtils.setCursorDrawableColor(chat_input_text, getResources().getColor(ThemeUtils.getIdFromAttr(this, R.attr.inputColor)));
        //5月16号

        mListView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_NORMAL);

        // title_change.setVisibility(View.GONE);

        title_change = (ImageView) findViewById(R.id.title_change);
        title_change.setSelected(SharePreUtils.getSoundsBoolean(this));
        title_change.setOnClickListener(this);
        imageViewThemeChange = (ImageView) findViewById(R.id.theme_change);
//        imageViewThemeChange.setVisibility(View.VISIBLE);
        imageViewThemeChange.setOnClickListener(this);
        mTalkRipView = findViewById(R.id.sound_vv);
        tv_help = (TextView) findViewById(R.id.tv_help);
        tv_help.setOnClickListener(this);
        mTalkBtn = (RippleLayout) mTalkRipView.findViewById(R.id.ripple_layout);
        chat_info_lv.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        chat_info_lv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                HistroyMessage(false);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

            }
        });
        initData();

    }

    @SuppressLint("ClickableViewAccessibility")
    private void initData() {

        tv_language.setOnClickListener(this);

        mListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        SystemUtils.hideSoftBord(ChatActivity.this, chat_input_text);
                        hideSound();
                }
                return false;
            }
        });

        // TODO: 2016-3-30 发送语音功能/

        //5月16号
        chat_speech_tv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (!checkSelfPermission()) {
                                chat_speech_tv.setTag(false);
                                return false;
                            }
                        }
                        mChatAapter.stopRecoard();
                        mIatResults.clear();
                        if (mIat.isListening())
                            mIat.cancel();
                        int n = mIat.startListening(mRecognizerListener);
                        if (n != ErrorCode.SUCCESS) {
                            //未安装则跳转到提示安装页面
                            chat_speech_tv.setTag(false);
                        } else {
                            mTalkBtn.startRippleAnimation();
                            if (rip_text == null) {
                                rip_text = (TextView) mTalkRipView.findViewById(R.id.rip_text);
                            }
                            rip_text.setTag(false);
                            rip_text.setBackgroundColor(Color.TRANSPARENT);
                            mTalkRipView.setVisibility(View.VISIBLE);

                            chat_speech_tv.setText("松开结束");
                            chat_speech_tv.setBackgroundDrawable(getResources().getDrawable(ThemeUtils.getIdFromAttr(ChatActivity.this, R.attr.chat_speech_select)));
                            chat_speech_tv.setTag(true);
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (!(boolean) chat_speech_tv.getTag()) {
                            break;
                        }
                        if (rip_text == null) {
                            rip_text = (TextView) mTalkRipView.findViewById(R.id.rip_text);
                            rip_text.setTag(false);
                            rip_text.setBackgroundColor(Color.TRANSPARENT);
                        }

                        if (event.getY() < -200) {

                            chat_speech_tv.setText("取消发送");
                            mTalkRipView.setVisibility(View.VISIBLE);
                            if (!(boolean) rip_text.getTag()) {
                                rip_text.setBackgroundDrawable(getResources().getDrawable(R.drawable.chat_sound_red));
                                rip_text.setTag(true);
                            }
                        } else {
//                            mediaRecord.changeCancelState(false);
                            chat_speech_tv.setText("松开结束");
                            if ((boolean) rip_text.getTag()) {
                                rip_text.setBackgroundColor(Color.TRANSPARENT);
                                rip_text.setTag(false);
                            }
                        }
                        if (event.getY() < -200) {
                            //LogUtil.d("----------------------", event.getY() + "------true");
                        } else {
                            //LogUtil.d("----------------------", event.getY() + "------false");
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        mChatAapter.stopRecoard();
                        if (!(boolean) chat_speech_tv.getTag()) {
                            break;
                        }
                        chat_speech_tv.setBackgroundDrawable(getResources().getDrawable(ThemeUtils.getIdFromAttr(ChatActivity.this, R.attr.chat_speech_select)));
                        if (event.getY() < -200) {
                            mIatResults.clear();
                            mIat.cancel();
                            chat_speech_tv.setText("按住说话");
                            ToastUtil.showShort(ChatActivity.this, "已取消语音发送");
                            //LogUtil.d("--------------------up--", "------cancle_fasong");
                        } else {
                            new android.os.Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    //LogUtil.d("--------------------up--", "------fasong");
                                    RingPlayer.playPressSound(ChatActivity.this);
                                    mIat.stopListening();
                                    chat_speech_tv.setText("按住说话");
                                }
                            }, 500);
                            mLodingFragmentDialog = LodingFragmentDialog.showLodingDialog(getSupportFragmentManager(), "");

                        }
                        mTalkRipView.setVisibility(View.GONE);
                        break;
                }
                return false;
            }
        });
    }

    //点击事件
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.title_change) {
            boolean flag = SharePreUtils.getSoundsBoolean(ChatActivity.this);
            SharePreUtils.saveSoundsBoolean(ChatActivity.this, !flag);
            title_change.setSelected(!flag);
            if (flag && mChatAapter != null) {
                mChatAapter.stopRecoard();
            }
        } else if (v.getId() == R.id.chat_send_voice) {
            changeSound();
        } else if (v.getId() == R.id.chat_input_text) {

            hideSound();
            mListView.setSelection(mListView.getAdapter().getCount() - 1);

        } else if (v.getId() == R.id.chat_send_text) {
            String str = chat_input_text.getEditableText().toString().trim();
            chat_input_text.setText(null);
            if (TextUtils.isEmpty(str)) return;
            onMessageSend(str);
            hideInput();// 发送后隐藏软键盘
        } else if (v.getId() == R.id.theme_change) {
            startActivity(new Intent(ChatActivity.this, ThemeChangeActivity.class));

        } else if (v.getId() == R.id.title_setting) {
            finish();
        } else if (v.getId() == R.id.tv_language) {
            int n = getYY().equals("普通话") ? 0 : getYY().equals("粤语") ? 1 : getYY().equals("四川话") ? 2 : getYY().equals("河南话") ? 3 : 0;
            WheelUtils.showSingleWheel(this, new String[]{"普通话", "粤语", "四川话", "河南话"}, v, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String str = v.getTag(R.id.wheel_one).toString();
                    if (str.equals("0")) {
                        setLanguage(PTH);
                        tv_language.setText("普通话(点击切换)");
                    } else if (str.equals("1")) {
                        setLanguage(YY);
                        tv_language.setText("粤语(点击切换)");
                    } else if (str.equals("2")) {
                        setLanguage(SCH);
                        tv_language.setText("四川话(点击切换)");
                    } else if (str.equals("3")) {
                        setLanguage(HNH);
                        tv_language.setText("河南话(点击切换)");
                    }
                }
            }, n);
        } else if (v.getId() == R.id.tv_help) {
            startActivity(new Intent(this, CommonWebUIActivity.class));

        }


    }

    /**
     * 隐藏键盘
     */
    protected void hideInput() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        View v = getWindow().peekDecorView();
        if (null != v) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }


    /**
     * 有未读消息就进行更新
     */
    @Override
    protected void onRestart() {
        super.onRestart();
        mListView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

    }

    @Override
    protected void onStart() {
        super.onStart();
        HisMessage();
        if (!EventBus.getDefault().isRegistered(this)) {

            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        mChatAapter.stopRecoard();
        cancelHisMessage();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mChatAapter.destorySpeed();
        if (!EventBus.getDefault().isRegistered(this)) {

            EventBus.getDefault().register(this);
        }
        if (mIat != null) {
            mIat.cancel();
            mIat.destroy();
            mIat = null;
        }
    }

    public void onEventMainThread(final TalkEvent log) {
        switch (log.getWhat()) {

            case TalkEvent.UPTALK:
                mListView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mListView.smoothScrollToPositionFromTop(log.getArg1() + 1, -log.getArg2());
                    }
                }, 200);

                break;

        }
    }

    //5月16号
    ConstraintLayout relativeLayoutSound;

    /**
     * 隐藏语音//5月16号
     */
    public void hideSound() {
        if (relativeLayoutSound == null) {
            relativeLayoutSound = (ConstraintLayout) findViewById(R.id.chat_sound_layout);
        }
        if (relativeLayoutSound.getVisibility() != View.GONE)
            relativeLayoutSound.setVisibility(View.GONE);
        chat_send_voice.setImageResource(R.drawable.chat_bottom_voice_aqua);
        //  SystemUtils.openSoftBord(this, chat_input_text);
    }

    /**
     * 切换语音//5月16号
     */
    public void changeSound() {
        if (relativeLayoutSound == null) {
            relativeLayoutSound = (ConstraintLayout) findViewById(R.id.chat_sound_layout);
        }
        if (relativeLayoutSound.getVisibility() == View.GONE) {
            relativeLayoutSound.setVisibility(View.VISIBLE);
            chat_send_voice.setImageResource(R.drawable.chat_bottom_jianpan_aqua);
            SystemUtils.hideSoftBord(this, chat_input_text);
        } else {
            relativeLayoutSound.setVisibility(View.GONE);
            chat_send_voice.setImageResource(R.drawable.chat_bottom_voice_aqua);
            SystemUtils.openSoftBord(this, chat_input_text);

        }
    }


    // 语音听写对象
    private SpeechRecognizer mIat;
    private SharedPreferences mSharedPreferences;
    /**
     * 初始化监听器。
     */
    private InitListener mInitListener = new InitListener() {

        @Override
        public void onInit(int code) {
            if (code != ErrorCode.SUCCESS) {
//                ToastUtil.showToastPanl("初始化失败，错误码：" + code);
//                mTalkRipView.setVisibility(View.GONE);
//                ToastUtil.showShort(ChatActivity.this, "初始化失败，错误码：" + code);
//				showTip("初始化失败，错误码：" + code);

            }
        }
    };
    public static final String PTH = "mandarin";
    public static final String YY = "cantonese";
    public static final String SCH = "lmz";
    public static final String HNH = "henanese";

    public String language = PTH;

    public void setLanguage(String yy) {
        mIat.setParameter(SpeechConstant.ACCENT, yy);
        language = yy;
    }

    public String getYY() {
        if (language.equals(PTH)) {
            return "普通话";
        } else if (language.equals(YY)) {
            return "粤语";
        } else if (language.equals(SCH)) {
            return "四川话";

        } else if (language.equals(HNH)) {
            return "河南话";
        } else {
            setLanguage(PTH);
            return "普通话";
        }

    }

    /**
     * 参数设置
     *
     * @param
     * @return
     */
    public void setParam() {
        // 清空参数
        mIat.setParameter(SpeechConstant.PARAMS, null);

        // 设置听写引擎
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
        // 设置返回结果格式
        mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");

        String lag = mSharedPreferences.getString("iat_language_preference",
                "mandarin");
        if (lag.equals("en_us")) {
            // 设置语言
            mIat.setParameter(SpeechConstant.LANGUAGE, "en_us");
        } else {
            // 设置语言
            mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
            // 设置语言区域
            mIat.setParameter(SpeechConstant.ACCENT, lag);
        }

        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        mIat.setParameter(SpeechConstant.VAD_BOS, mSharedPreferences.getString("iat_vadbos_preference", "40000"));

        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mIat.setParameter(SpeechConstant.VAD_EOS, mSharedPreferences.getString("iat_vadeos_preference", "30000"));

        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        mIat.setParameter(SpeechConstant.ASR_PTT, mSharedPreferences.getString("iat_punc_preference", "1"));
        // 设置音频保存路径，保存音频格式仅为pcm，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
//        mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory() + "/iflytek/wavaudio.pcm");

        // 设置听写结果是否结果动态修正，为“1”则在听写过程中动态递增地返回结果，否则只在听写结束之后返回最终结果
        // 注：该参数暂时只对在线听写有效
        mIat.setParameter(SpeechConstant.ASR_DWA, mSharedPreferences.getString("iat_dwa_preference", "0"));
    }

    RecognizerListener mRecognizerListener = new RecognizerListener() {
        @Override
        public void onVolumeChanged(int i, byte[] bytes) {

//            mChatVm.setMediaRecord(i);
        }

        @Override
        public void onBeginOfSpeech() {

        }

        @Override
        public void onEndOfSpeech() {
        }

        //5月16号
        @Override
        public void onResult(RecognizerResult recognizerResult, boolean b) {
            printResult(recognizerResult);
            if (b) {
                SystemUtils.hideSoftBord(ChatActivity.this, chat_input_text);
//                hideSound();
//                voiceAction();
                StringBuffer resultBuffer = new StringBuffer();
                for (String key : mIatResults.keySet()) {
                    resultBuffer.append(mIatResults.get(key));
                }
                String str = resultBuffer.toString();
                chat_input_text.setText(null);
                if (TextUtils.isEmpty(str)) return;
                onMessageSend(str);
                //5月16号
                if (mLodingFragmentDialog != null) {

                    mLodingFragmentDialog.dismissAllowingStateLoss();
                }
            } else {

            }

        }

        @Override
        public void onError(SpeechError speechError) {
//            mTalkRipView.setVisibility(View.GONE);
            if (mLodingFragmentDialog != null) {

                mLodingFragmentDialog.dismissAllowingStateLoss();
            }
        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {

        }
    };


    public interface DoctorInputControlListerner {
        void onMessageSend(String content);

        void onSoftWord(boolean isSoft);
    }

    private void initVoiceData() {
        //SpeechUtility.createUtility(this, SpeechConstant.APPID + "=" + "5c0f47a2");
        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=" + "5a0007f1");
        // 初始化识别无UI识别对象
        // 使用SpeechRecognizer对象，可根据回调消息自定义界面；
        mIat = SpeechRecognizer.createRecognizer(this, mInitListener);
        mSharedPreferences = this.getSharedPreferences(PREFER_NAME, Activity.MODE_PRIVATE);
        // 设置参数
        setParam();
        tv_language.setText("普通话" + "(点击切换)");

    }

    private final String PREFER_NAME = "com.iflytek.setting";
    // 引擎类型
    private String mEngineType = SpeechConstant.TYPE_CLOUD;
    // 用HashMap存储听写结果
    private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();

    //5月16号
    private void printResult(RecognizerResult results) {
//        RingPlayer.playPressSound(mActivity);
        String text = JsonParser.parseIatResult(results.getResultString());

        String sn = null;
        // 读取json结果中的sn字段
        try {
            JSONObject resultJson = new JSONObject(results.getResultString());
            sn = resultJson.optString("sn");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mIatResults.put(sn, text);


    }

    private LodingFragmentDialog mLodingFragmentDialog;
    private View mTalkRipView;
    private RippleLayout mTalkBtn;

    //欢迎语
    private void onLoadHistoryMesgs() {
        RequestParams params = new RequestParams();
        params.put("Type", "XiaoYi_chat_his_pb");
        params.put("MERCHANT_ID", WelcomeActivity.MERCHANT_ID);//商户ID
        HttpRestClient.doHttpMESGHISTORY(params, new JsonHttpResponseHandler() {
            @Override
            public void onFinish() {
                super.onFinish();

                chat_info_lv.onRefreshComplete();
            }

            @Override
            public void onFailure(Throwable error, String content) {
                if (mChatAapter != null && mChatAapter.getCount() == 0) {
                    chat_info_lv.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (mChatAapter.getCount() == 0) {
                                onLoadHistoryMesgs();
                            }
                        }
                    }, 2000);
                }
            }

            @Override
            public void onSuccess(JSONObject response) {
                super.onSuccess(response);
                MKLog.e(response.toString());
                if (response.optInt("code") != 0) return;
                String message = response.optString("server_params");
                mChatAapter.onDataChange(MessageInfo.addReceiveMessage(SystemUtils.getHtmlChange(message), System.currentTimeMillis(), "", "", false), true, false, true, false);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case RESULT_OK:
//                HttpRestClient.doHttpMyMessage(data.getStringExtra("json"), new MJsonHttpResponseHandler());
                break;

        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("message", true);
        super.onSaveInstanceState(outState);
    }


    WeakHandler mHandler = new WeakHandler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case AskMessage:
                    HisMessage();
                    break;
            }
            return false;
        }
    });

    public void firstGet() {
//        http://157.122.68.20/XiaoYiRobotSer/history?APPKEY=EUmTO4OXAoftcylL&message_id=999999&customer_id=318854&limit=10
//        if (TextUtils.isEmpty(LoginHttp.getLoginHttp().getLoginId()))
//            return;
        RequestParams params = new RequestParams();
        params.put("APPKEY", WelcomeActivity.KEY);
        params.put("message_id", mChatAapter.getLastSendCode());
        params.put("PLATFORM_USER_ID", ChatActivity.userid);
        params.put("TERMINAL_ID", ChatActivity.deviceid);
        params.put("limit", "10");
        params.put("customer_id", LoginHttp.getLoginHttp().getLoginId());
        HttpRestClient.doHttpHISTROY(params, new JsonHttpResponseHandler() {
            @Override
            public void onFinish() {
                super.onFinish();
                chat_info_lv.onRefreshComplete();


            }

            @Override
            public void onFailure(Throwable error, String content) {
                if (mChatAapter != null && mChatAapter.getCount() == 0) {
                    chat_info_lv.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            firstGet();
                        }
                    }, 2000);
                }
            }

            @Override
            public void onSuccess(JSONObject response) {
                super.onSuccess(response);
                MKLog.e(response.toString());
                HistroyMessage(true);
            }
        });
    }

    //历史
    public void HistroyMessage(final boolean isFirst) {
//        http://157.122.68.20/XiaoYiRobotSer/history?APPKEY=EUmTO4OXAoftcylL&message_id=999999&customer_id=318854&limit=10
//        if (TextUtils.isEmpty(LoginHttp.getLoginHttp().getLoginId()))
//            return;
        RequestParams params = new RequestParams();
        params.put("APPKEY", WelcomeActivity.KEY);
        params.put("message_id", mChatAapter.getLastSendCode());
        params.put("PLATFORM_USER_ID", ChatActivity.userid);
        params.put("TERMINAL_ID", ChatActivity.deviceid);
        params.put("limit", "10");
        params.put("customer_id", LoginHttp.getLoginHttp().getLoginId());
        HttpRestClient.doHttpHISTROY(params, new JsonHttpResponseHandler() {
            @Override
            public void onFinish() {
                super.onFinish();
                chat_info_lv.onRefreshComplete();
            }

            @Override
            public void onFailure(Throwable error, String content) {
                if (mChatAapter != null && mChatAapter.getCount() == 0) {
                    chat_info_lv.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (isFirst)
                                HistroyMessage(true);
                        }
                    }, 2000);
                }
            }

            @Override
            public void onSuccess(JSONObject response) {
                super.onSuccess(response);
                MKLog.e(response.toString());
                if (response.optInt("code") != 0 && isFirst) {
                    isHis = true;
                    HisMessage();
                    return;
                }
                JSONArray jsonArray = null;
                try {
                    jsonArray = new JSONArray(response.optString("server_params"));
//            JSONArray jsonArray = new JSONArray(response.getString("content"));
                    for (int i = jsonArray.length() - 1; i >= 0; i--) {
                        JSONObject jsb = jsonArray.getJSONObject(i);
                        if (jsb.optString("sender_Flag").equals("1")) {
                            MessageInfo info = MessageInfo.addSendMessage(jsb.optString("content"), jsb.optString("message_id"), TimeUtil.getMesgTime(jsb.optString("message_time")));
                            mChatAapter.onDataChange(info, true, true, i == 0 ? true : false, true);
                            continue;
                        }
                        JSONArray jsonArrayC = new JSONArray(jsb.getString("content"));
                        for (int j = jsonArrayC.length() - 1; j >= 0; j--) {
                            JSONObject jsbC = jsonArrayC.getJSONObject(j);
                            if (i == 0 && j == 0) {
                                messJson(jsbC, jsb, false, true, true);
                            } else {
                                messJson(jsbC, jsb, false, true, false);

                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (isFirst) {

                    isHis = true;
                    HisMessage();
                }
            }
        });
    }

    boolean isHis = false;

    //离线
    public void HisMessage() {
        if (!isHis)
            return;
        mHandler.removeMessages(AskMessage);
        mHandler.sendEmptyMessageDelayed(AskMessage, AskMessageTime);
        RequestParams params = new RequestParams();
        params.put("APPKEY", WelcomeActivity.KEY);
        params.put("Type", "Offlinemsg");
        params.put("customer_id", LoginHttp.getLoginHttp().getLoginId());
        params.put("PLATFORM_USER_ID", ChatActivity.userid);
        params.put("TERMINAL_ID", ChatActivity.deviceid);
        HttpRestClient.doHttpHISTROY(params, new JsonHttpResponseHandler() {
            @Override
            public void onFinish() {
                super.onFinish();
//                chat_info_lv.onRefreshComplete();
            }

            @Override
            public void onFailure(Throwable error, String content) {
                chat_info_lv.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mHandler.removeMessages(AskMessage);
                        mHandler.sendEmptyMessage(AskMessage);
                    }
                }, 2000);
            }

            @Override
            public void onSuccess(JSONObject response) {
                super.onSuccess(response);

                MKLog.e(response.toString());
                if (response.optInt("code") != 0) {
                    if (mChatAapter.getCount() == 0) {
                        onLoadHistoryMesgs();
                    }
                    return;
                }
                JSONArray jsonArray = null;
                try {
                    jsonArray = new JSONArray(response.optString("server_params"));

//            JSONArray jsonArray = new JSONArray(response.getString("content"));
                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject jsb = jsonArray.getJSONObject(i);
                        if (jsb.optString("sender_Flag").equals("1")) {
                            MessageInfo info = MessageInfo.addSendMessage(jsb.optString("content"), jsb.optString("message_id"), TimeUtil.getMesgTime(jsb.optString("message_time")));
                            mChatAapter.onDataChange(info, true, false, i == jsonArray.length() - 1 ? true : false, false);
                            continue;
                        }
                        JSONArray jsonArrayC = new JSONArray(jsb.getString("content"));
                        for (int j = 0; j < jsonArrayC.length(); j++) {
                            JSONObject jsbC = jsonArrayC.getJSONObject(j);
                            if (i == jsonArray.length() - 1 && j == jsonArrayC.length() - 1) {

                                messJson(jsbC, jsb, false, false, true);
                            } else {
                                messJson(jsbC, jsb, false, false, false);

                            }
                        }


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (mChatAapter.getCount() == 0) {
                    onLoadHistoryMesgs();
                }
            }
        });
    }

    public void cancelHisMessage() {
        mHandler.removeMessages(AskMessage);
    }


    public boolean checkSelfPermission() {
        boolean flag = ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
        if (!flag) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.RECORD_AUDIO
                    },
                    1
            );
        }
        return flag;
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (newConfig.fontScale != 1)//非默认值
            getResources();
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        if (res.getConfiguration().fontScale != 1) {//非默认值
            Configuration newConfig = new Configuration();
            newConfig.setToDefaults();//设置默认
            res.updateConfiguration(newConfig, res.getDisplayMetrics());
        }
        return res;
    }


}
