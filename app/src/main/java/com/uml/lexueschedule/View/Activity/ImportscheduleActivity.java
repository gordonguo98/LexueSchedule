package com.uml.lexueschedule.View.Activity;

import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.uml.lexueschedule.R;
import com.uml.lexueschedule.Data.Model.Schedule;
import com.uml.lexueschedule.Util.Connect;
import com.uml.lexueschedule.Util.Getweb;
import com.uml.lexueschedule.Util.UploadData;

/**
 * by 孙瑞洲，郭晓凡
 */

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

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));

        String url = getIntent().getStringExtra("url");

        //设置webView属性
        webView=(WebView)findViewById(R.id.web_view);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new InJavaScriptLocalObj(), "java_obj");
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(final WebView view, String url) {
                // 在结束加载网页时会回调，获取页面内容
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            Thread.sleep(2000);
                            view.loadUrl("javascript:window.java_obj.showSource("
                                    + "document.getElementsByTagName('html')[0].innerHTML);");
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                }).start();
                super.onPageFinished(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return false;
            }
        });
        //webView.loadUrl("file:///android_asset/course.html");
        webView.loadUrl(url);
        //webView.loadUrl("https://sso.scut.edu.cn/cas/login?service=http%3A%2F%2Fxsjw2018.scuteo.com%2Fsso%2Fdriotlogin");
        //设置Button
        Button button=(Button)findViewById(R.id.v_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!Connect.isConnectIsNomarl(ImportscheduleActivity.this)){
                    Toast.makeText(ImportscheduleActivity.this, "无网络连接", Toast.LENGTH_LONG).show();
                    return;
                }
                webView.loadUrl("javascript:window.java_obj.showSource("
                        + "document.getElementsByTagName('html')[0].innerHTML);");
                SystemClock.sleep(1000);
                //loge(myhtml);
                Getweb.get(myhtml);
                Schedule mySchedule=Schedule.getInstance();
                UploadData.updateSchedule(ImportscheduleActivity.this, mySchedule.courses, true);
            }
        });
    }

    public void toBaseFunAC(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ImportscheduleActivity.this, "导入成功", Toast.LENGTH_LONG).show();
            }
        });
        finish();
    }

    @Override
    protected void onDestroy() {
        CookieSyncManager.createInstance(this);
        CookieManager.getInstance().removeAllCookie();
        super.onDestroy();
    }
}
