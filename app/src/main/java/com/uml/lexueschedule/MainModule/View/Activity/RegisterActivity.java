package com.uml.lexueschedule.MainModule.View.Activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.uml.lexueschedule.ForumModule.Util.UrlHelper;
import com.uml.lexueschedule.MyApplication;
import com.uml.lexueschedule.R;
import com.uml.lexueschedule.ScheduleModule.Util.Connect;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Button checkcode_btn;
    private Button commit_btn;
    private EditText user_edt;
    private EditText password_edt;
    private EditText checkcode_edt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initView();
    }

    /**
     * 初始化控件
     */
    private void initView(){

        //状态栏颜色
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        checkcode_btn = (Button) findViewById(R.id.getcheckcode_btn);
        commit_btn = (Button) findViewById(R.id.commit_regist_btn);
        user_edt = (EditText)findViewById(R.id.regist_user_edt);
        password_edt = (EditText)findViewById(R.id.regist_password_edt);
        checkcode_edt = (EditText)findViewById(R.id.checkcode_edt);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(null != actionBar)
            getSupportActionBar().setDisplayShowTitleEnabled(false);

        checkcode_btn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCheckCode();
            }
        });

        commit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commit();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 获取验证码
     */
    private void getCheckCode(){

        //判断网络是否可用
        if(!Connect.isConnectIsNomarl(RegisterActivity.this)) {
            Toast.makeText(RegisterActivity.this,"网络无连接",Toast.LENGTH_SHORT).show();
            return;
        }

        final String userStr = user_edt.getText().toString();//邮箱

        new Thread(new Runnable() {//创建子线程
            @Override
            public void run() {
                final int code = getWebInfo(userStr);
                runOnUiThread(new Runnable() {
                    @Override//此时已在主线程中，更新UI
                    public void run() {
                        if(code == 1000) {
                            Toast.makeText(RegisterActivity.this,"成功发送验证码",Toast.LENGTH_LONG).show();
                        }
                        if(code == 1001) {
                            Toast.makeText(RegisterActivity.this,"邮箱已经被注册",Toast.LENGTH_LONG).show();
                        }
                        if(code == 1002) {
                            Toast.makeText(RegisterActivity.this,"邮箱无效",Toast.LENGTH_LONG).show();
                        }
                        if(code == 1) {
                            Toast.makeText(RegisterActivity.this,"出错了",Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }).start();//启动子线程
    }

    /**
     * 提交注册
     */
    private void commit(){

        //判断网络是否可用
        if(!Connect.isConnectIsNomarl(RegisterActivity.this)) {
            Toast.makeText(RegisterActivity.this,"网络无连接",Toast.LENGTH_SHORT).show();
            return;
        }

        final String userStr = user_edt.getText().toString();//邮箱
        final String passwordStr = password_edt.getText().toString();//密码
        final String checkcodeStr = checkcode_edt.getText().toString();//确认验证码栏

        OkHttpClient mOkHttpClient=new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("email" , userStr)
                .add("user_pwd" , passwordStr)
                .add("checkcode" , checkcodeStr)
                .build();

        Request request = new Request.Builder()
                .url(UrlHelper.URL_FOR_REGISTER)
                .post(formBody)
                .build();

        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e("RegisterActivity", "onFailure: " + e.toString());
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseStr = response.body().string();
                try {
                    JSONObject responseJson = new JSONObject(responseStr);
                    final int code = responseJson.getInt("code");
                    Log.e("RegisterActivity", "onResponse: " + code);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(code == 1000) {
                                MyApplication.addDestroyActivity(RegisterActivity.this, "RegisterActivity");
                                Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(RegisterActivity.this, AddProfileActivity.class);
                                intent.putExtra("userId", userStr);
                                intent.putExtra("password", passwordStr);
                                startActivity(intent);
                            }
                            if(code == 1001) {
                                Toast.makeText(RegisterActivity.this, "用户已存在", Toast.LENGTH_SHORT).show();
                            }
                            if(code == 1002) {
                                Toast.makeText(RegisterActivity.this, "验证码错误", Toast.LENGTH_SHORT).show();
                            }
                            if(code == 1003){
                                Toast.makeText(RegisterActivity.this, "验证码失效", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } catch(JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 获取响应code字段
     * @param a
     * @return
     */
    private int getWebInfo(String a) {
        try {
            String registerUrl = UrlHelper.URL_FOR_CHECK_CODE + "?email=";
            registerUrl = registerUrl.concat(a);
            URL url = new URL(registerUrl);

            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");

            InputStream inputStream = httpURLConnection.getInputStream();
            InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            BufferedReader bufferedReader = new BufferedReader(reader);

            StringBuffer buffer = new StringBuffer();
            String temp;

            while ((temp = bufferedReader.readLine()) != null) {
                buffer.append(temp);
            }
            bufferedReader.close();
            reader.close();
            inputStream.close();
            String returnString = buffer.toString();
            Log.e("RegisterActivity", "getWebInfo: returnString is " + returnString);
            try {
                JSONObject returnJson = new JSONObject(returnString);
                int code = returnJson.getInt("code");
                Log.e("RegisterActivity", "getWebInfo: code is " + code);
                return code;
            } catch(JSONException e) {
                e.printStackTrace();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 1;
    }
}
