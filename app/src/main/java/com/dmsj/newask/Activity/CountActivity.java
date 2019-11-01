package com.dmsj.newask.Activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.dmsj.newask.R;
import com.dmsj.newask.http.HttpRestClient;
import com.dmsj.newask.http.JsonHttpResponseHandler;
import com.dmsj.newask.http.LoginHttp;
import com.dmsj.newask.utils.MKLog;
import com.dmsj.newask.utils.SharePreUtils;
import com.dmsj.newask.utils.SpeechUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by x_wind on 17/3/24.
 */
public class CountActivity extends FragmentActivity implements View.OnClickListener {
    private ImageView mPlay;
    private SpeechUtils mSpeechUtils;
    private TextView mContent, title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setTheme(WelcomeActivity.themeId);
        setContentView(R.layout.layout_displty_content);
        findViewById(R.id.title_back).setOnClickListener(this);
        mPlay = (ImageView) findViewById(R.id.play);
        mPlay.setOnClickListener(this);
        mSpeechUtils = new SpeechUtils(this);
        mContent = (TextView) findViewById(R.id.textview_count);
        title = (TextView) findViewById(R.id.title_text);
        initData();
    }

    @Override
    public void onClick(View v) {


        if (v.getId() == R.id.title_back) {
            finish();
        } else if (v.getId() == R.id.play) {
            mPlay.setSelected(!mPlay.isSelected());
            mSpeechUtils.startPeed(mContent.getText().toString(), mPlay);

        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        mSpeechUtils.stopPeed();
        mPlay.setSelected(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSpeechUtils.destory();
    }

    private void initData() {
        HttpRestClient.doHttpVirtualDoctor(getIntent().getStringExtra("cont"), "", new MJsonHttpResponseHandler());
        title.setText(getIntent().getStringExtra("cont"));

    }

    private class MJsonHttpResponseHandler extends JsonHttpResponseHandler {


        public MJsonHttpResponseHandler() {
            super(CountActivity.this);
        }

        public void onSuccess(int statusCode, JSONObject response) {//成功
            MKLog.e("a", response.toString());
            try {
                String id = response.getJSONObject("server_params").optString("customer_id");
                if (!TextUtils.isEmpty(id)) {
                    LoginHttp.getLoginHttp().setLoginId(id);
                }
                JSONArray jsonArray = new JSONArray(response.getJSONObject("server_params").optString("content"));
                for (int i = 0; i < jsonArray.length(); i++) {

                    final JSONObject jsb = jsonArray.getJSONObject(i);
                    //文字
                    TextView t = mContent;
                    t.setText(Html.fromHtml(jsb.optString("cont").replace("</br>", "<br>")));

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }
}
