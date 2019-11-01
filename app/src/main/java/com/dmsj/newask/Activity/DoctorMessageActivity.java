package com.dmsj.newask.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dmsj.newask.R;
import com.dmsj.newask.Views.CircleImageView;
import com.dmsj.newask.utils.SystemUtils;
import com.squareup.picasso.Picasso;

/**
 * Created by x_wind on 18/4/24.
 */
public class DoctorMessageActivity extends Activity implements View.OnClickListener {

    CircleImageView iv_head;
    ImageView iv_back;
    TextView tv_name;
    TextView tv_job;
    TextView tv_hospital;
    TextView tv_zc;
    TextView tv_jyjl;
    TextView tv_order;
    TextView tv_time;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setTheme(WelcomeActivity.themeId);
        setContentView(R.layout.activity_doctormessage);
        initView();
        initDate();
        setListener();
    }

    public void initView() {
        iv_head = (CircleImageView) findViewById(R.id.iv_head);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_job = (TextView) findViewById(R.id.tv_job);
        tv_hospital = (TextView) findViewById(R.id.tv_hospital);
        tv_zc = (TextView) findViewById(R.id.tv_zc);
        tv_jyjl = (TextView) findViewById(R.id.tv_jyjl);
        tv_order = (TextView) findViewById(R.id.tv_order);
        tv_time = (TextView) findViewById(R.id.tv_time);


    }

    String WORK_NUMBER = "";
    String DEPT_CODE = "";

    public void initDate() {
        Intent intent = getIntent();
        WORK_NUMBER = intent.getStringExtra("WORK_NUMBER");
        DEPT_CODE = intent.getStringExtra("DEPT_CODE");
        String name = intent.getStringExtra("name");
        tv_name.setText(Html.fromHtml(name));
        String job = intent.getStringExtra("job");
        tv_job.setText(Html.fromHtml(job));
        String hospital = intent.getStringExtra("hospital");
        tv_hospital.setText(Html.fromHtml(hospital));
        String zc = intent.getStringExtra("zc");
        if (TextUtils.isEmpty(zc)) {
            tv_zc.setText("暂无信息");
        } else {
            tv_zc.setText(Html.fromHtml(zc));
        }
        String jyjl = intent.getStringExtra("jyjl");
        tv_jyjl.setText(Html.fromHtml(jyjl));
        String headurl = intent.getStringExtra("headurl");
        if (TextUtils.isEmpty(headurl)) {
            Picasso.with(this).load(R.drawable.default_head_doctor).noFade().into(iv_head);
        } else {
            Picasso.with(this).load(SystemUtils.toBrowserCode(headurl)).noFade().error(R.drawable.default_head_doctor).into(iv_head);

        }
        String time = intent.getStringExtra("time");
        if (TextUtils.isEmpty(time)) {
            tv_order.setBackgroundColor(Color.GRAY);
            tv_time.setText(Html.fromHtml("暂无出诊信息"));
        } else {
            String[] times = time.split("，");
            String str = "";
            for (int i = 0; i < times.length; i++) {

                if (i % 2 == 0) {

                    str += times[i] + "        ";
                } else {
                    str += times[i] + "<br>";

                }

            }
            tv_time.setText(Html.fromHtml(str));

        }
        if (ChatActivity.DoctorClass == null || TextUtils.isEmpty(WORK_NUMBER) || TextUtils.isEmpty(DEPT_CODE)) {
            tv_order.setBackgroundColor(Color.GRAY);
        }

    }

    public void setListener() {
        tv_order.setOnClickListener(this);
        iv_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_order) {
            if (ChatActivity.DoctorClass == null || TextUtils.isEmpty(WORK_NUMBER) || TextUtils.isEmpty(DEPT_CODE)) {

                Toast.makeText(this, "暂未开通!", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(this, ChatActivity.DoctorClass);
                intent.putExtra("docId", WORK_NUMBER);
                intent.putExtra("deptId", DEPT_CODE);
                startActivity(intent);
                finish();
            }
        } else if (v.getId() == R.id.iv_back) {
            finish();
        }
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
