package com.dmsj.newask.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.dmsj.newask.R;
import com.dmsj.newask.utils.SharePreUtils;


/**
 * Created by x_wind on 16/7/22.
 */
public class ThemeChangeActivity extends Activity implements View.OnClickListener{
    public static int themeIds[]={R.style.MOREN,R.style.AQUA};
    public String themeName[]={"默认主题","浅绿色主题"};

    ListView listView;
    ImageView imageViewBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setTheme(WelcomeActivity.themeId);
        setContentView(R.layout.activity_change_them);
        initView();
    }


    private void initView() {

        listView = (ListView) findViewById(R.id.listview_changetheme);
        listView.setAdapter(new madapter());
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (ChatActivity.chatActivity != null) {
                    ChatActivity.chatActivity.finish();
                    Intent intent = new Intent(ThemeChangeActivity.this, ChatActivity.class);
                    // Intent intent = new Intent(LoginRegisterActivity.this, ChatActivity.class);

                    intent.putExtra("isThemeChange", true);
                    WelcomeActivity.themeId = themeIds[position];
                    SharePreUtils.saveThemei(ThemeChangeActivity.this, position);
                    startActivity(intent);
                    //  LoginServiceManeger.instance().login("", "");
                    //  intent.putExtra("ICON_ADDRESS", R.drawable.icon_back);
                    // startActivity(intent);
                    finish();
                }
            }
        });
        imageViewBack= (ImageView) findViewById(R.id.title_back);
        imageViewBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if (v.getId()==R.id.title_back){
            onBackPressed();
        }

    }
class madapter extends BaseAdapter {
    @Override
    public int getCount() {
        return themeIds.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {
            vh = new ViewHolder();
            convertView = LayoutInflater.from(ThemeChangeActivity.this).inflate(R.layout.item_bottom_send, null);
            vh.tv = (TextView) convertView.findViewById(R.id.bottom_send_tv);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        vh.tv.setText(themeName[position]);
        return convertView;
    }

}
    class ViewHolder{
        TextView tv;
    }
}
