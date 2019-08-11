package com.uml.lexueschedule.MainModule.View.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.uml.lexueschedule.R;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private Button login_btn;
    private Button register_btn;
    private EditText user_edt;
    private EditText password_edt;

    private static final int REQUEST_CODE_FOR_INTERNET = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermission();
        initView();
    }

    /**
     * android 6.0以上动态申请权限
     */
    private void requestPermission(){
        //申请网络权限
        try {
            //检测是否有网络权限
            int permission = ActivityCompat.checkSelfPermission(this,
                    "android.permission.INTERNET");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有网络权限
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, REQUEST_CODE_FOR_INTERNET);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化控件
     */
    private void initView(){

        register_btn = (Button) findViewById(R.id.register_btn);
        login_btn = (Button) findViewById(R.id.login_btn);
        user_edt = (EditText)findViewById(R.id.user_edt);
        password_edt = (EditText)findViewById(R.id.password_edt);

        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        login_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                login();
            }
        });
    }

    /**
     * 处理登录事件
     */
    private void login(){

        //检查权限
        requestPermission();

        String user_edt_str = user_edt.getText().toString();
        String password_edt_str = password_edt.getText().toString();

        OkHttpClient mOkHttpClient=new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("email" , user_edt_str)
                .add("user_pwd" , password_edt_str)
                .build();

        Request request = new Request.Builder()
                .url("http://212.64.92.236:20000/api/v1/auth/")
                .post(formBody)
                .build();

        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e("MainActivity ", "onFailure:" + e.toString());
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                String responseStr = response.body().string();
                Log.e("MainActivity ", "onResponse:" + responseStr);
                try {
                    JSONObject responseJson = new JSONObject(responseStr);
                    final int code = responseJson.getInt("code");
                    Log.e("MainActivity ", "onResponse:" + code);
                    runOnUiThread(new Runnable() {
                        @Override//此时已在主线程中，更新UI
                        public void run() {
                            if(code == 1000) {
                                //登录成功，进入系统
                                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                                startActivity(intent);
                            }
                            if(code == 1001){
                                Toast.makeText(MainActivity.this,"用户名密码错误！", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } catch(JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_FOR_INTERNET: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //网络权限申请成功
                }
                else {
                    //网络权限申请失败
                    Toast.makeText(this, "授权失败", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }
}
