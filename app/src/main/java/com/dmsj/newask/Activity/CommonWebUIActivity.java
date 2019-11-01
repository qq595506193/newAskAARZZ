package com.dmsj.newask.Activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.GeolocationPermissions.Callback;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.dmsj.newask.R;
import com.dmsj.newask.http.HttpRestClient;

/**
 * web页面
 * getIntent().getStringExtra("url")   地址 http://www.baidu.com
 * getIntent().getStringExtra("title")  title标题
 *
 * @author root
 */
public class CommonWebUIActivity extends Activity implements OnClickListener {

    private WebView mWebView;
    private WebSettings settings;
    private ImageView iv_back;


    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setTheme(WelcomeActivity.themeId);
        setContentView(R.layout.setting_web_ui);
        mWebView = (WebView) findViewById(R.id.webview);
        settings = mWebView.getSettings();
        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_back.setOnClickListener(this);
        initView();
    }

    private void initView() {

        settings.setLoadWithOverviewMode(true);
        settings.setJavaScriptEnabled(true); // 支持js
        settings.setUseWideViewPort(false); // 将图片调整到适合webview的大小
        settings.setSupportZoom(true); // 支持缩放
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL); // 支持内容重新布局
        settings.supportMultipleWindows(); // 多窗口
        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); // 关闭webview中缓存
        settings.setAllowFileAccess(true); // 设置可以访问文件
        settings.setBlockNetworkImage(false);
        settings.setBlockNetworkLoads(false);
        settings.setNeedInitialFocus(true); // 当webview调用requestFocus时为webview设置节点
        settings.setBuiltInZoomControls(true); // 设置支持缩放
        settings.setJavaScriptCanOpenWindowsAutomatically(true); // 支持通过JS打开新窗口
        settings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
        settings.setLoadsImagesAutomatically(true); // 支持自动加载图片
        mWebView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                CommonWebUIActivity.this.setProgress(progress * 1000);
            }

            @Override
            public void onGeolocationPermissionsShowPrompt(String origin,
                                                           Callback callback) {
                callback.invoke(origin, true, false);
                super.onGeolocationPermissionsShowPrompt(origin, callback);

            }
        });
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                // TODO Auto-generated method stub
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView arg0, String arg1) {
                super.onPageFinished(arg0, arg1);
                try {
                    // removeDialog(1);
                } catch (Exception e) {
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.contains("jd.com")) {
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                        Uri uri = Uri.parse(url);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        CommonWebUIActivity.this.startActivity(intent);
                        finish();
                        return true;
                    }
                }
                return false;
            }
        });
        String url = "http://sfai.sysucc.org.cn/XiaoYiRobotSer/app_help.html";
        if (!url.startsWith("http")) url = "http://" + url;
        final String finalUrl = url;
        mWebView.loadUrl(finalUrl, HttpRestClient.getDefaultHeaders());

        mWebView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
                        mWebView.goBack();
                        return true;
                    }
                }
                return false;
            }
        });


    }

//    @Override
//    protected void onDestroy() {
//        if (mWebView != null) {
//            mWebView.setVisibility(View.GONE);
//        }
//        super.onDestroy();
//        mWebView.removeAllViews();
//        mWebView.destroy();
//    }


    @Override
    protected void onDestroy() {
        if (mWebView != null) {

            // 如果先调用destroy()方法，则会命中if (isDestroyed()) return;这一行代码，需要先onDetachedFromWindow()，再
            // destory()
            ViewParent parent = mWebView.getParent();
            if (parent != null) {
                ((ViewGroup) parent).removeView(mWebView);
            }
            mWebView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            mWebView.stopLoading();
            // 退出时调用此方法，移除绑定的服务，否则某些特定系统会报错
            mWebView.getSettings().setJavaScriptEnabled(false);
            mWebView.clearHistory();
            mWebView.clearView();
            mWebView.removeAllViews();
            mWebView.destroy();
            mWebView = null;
        }
        super.onDestroy();
    }

    // 重写onKeyDown
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack())) {
            mWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_back) {
            finish();
        }
    }
}