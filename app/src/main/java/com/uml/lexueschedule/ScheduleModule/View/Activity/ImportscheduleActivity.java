package com.uml.lexueschedule.ScheduleModule.View.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.uml.lexueschedule.MainModule.Util.StatusBarUtil;
import com.uml.lexueschedule.R;
import com.uml.lexueschedule.ScheduleModule.Data.Model.Schedule;
import com.uml.lexueschedule.ScheduleModule.Util.Getweb;
import com.uml.lexueschedule.ScheduleModule.Util.UploadData;

public class ImportscheduleActivity extends AppCompatActivity {

    //打印长日志
    public static String TAG="-----LongLog-----";
    public  static void  loge(String str){
        int max_str_length=2001-TAG.length();
        //大于4000时
        while (str.length()>max_str_length){
            Log.e("tag", str.substring(0,max_str_length) );
            str=str.substring(max_str_length);
        }
        //剩余部分
        Log.e("tag", str );
    }

    private String myhtml;
    public final class InJavaScriptLocalObj
    {
        @JavascriptInterface
        public void showSource(String html) {
            myhtml=html;
        }
    }
    private WebView webView;
    private String myurl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_importschedule);

        StatusBarUtil.transparencyBar(this);
        StatusBarUtil.StatusBarLightMode(this);

        //设置webView属性
        webView=(WebView)findViewById(R.id.web_view);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new InJavaScriptLocalObj(), "java_obj");
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                // 在结束加载网页时会回调，获取页面内容
                view.loadUrl("javascript:window.java_obj.showSource("
                        + "document.getElementsByTagName('html')[0].innerHTML);");
                super.onPageFinished(view, url);
            }
        });

        webView.loadUrl("file:///android_asset/course.html");
        //webView.loadUrl("https://sso.scut.edu.cn/cas/login?service=http%3A%2F%2Fxsjw2018.scuteo.com%2Fsso%2Fdriotlogin");
        //设置Button
        Button button=(Button)findViewById(R.id.v_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Getweb.get(myhtml);
                Schedule mySchedule=Schedule.getInstance();
                UploadData.upload(ImportscheduleActivity.this, mySchedule.courses, true);
            }
        });
    }

    public void toBaseFunAC(){
        finish();
    }
}
