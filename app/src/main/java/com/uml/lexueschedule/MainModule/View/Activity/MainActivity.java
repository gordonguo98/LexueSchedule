package com.uml.lexueschedule.MainModule.View.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.uml.lexueschedule.MyApplication;
import com.uml.lexueschedule.R;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private Button login_btn;
    private TextView register_btn;
    private TextInputEditText user_edt;
    private TextInputEditText password_edt;
    private CheckBox checkBox;

    private static final int REQUEST_CODE_FOR_INTERNET = 100;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //处理退出登录
        MyApplication.destroyActivity("HomeActivity");

        //已经登录过，不再进入登录界面
        String log_usr = getSharedPreferences("loginlog", MODE_PRIVATE).getString("userId", "");
        if(!log_usr.equals("")){
            MyApplication.addDestroyActivity(MainActivity.this, "MainActivity");
            Intent intent =  new Intent(this, HomeActivity.class);
            intent.putExtra("userId", log_usr);
            startActivity(intent);
            return;
        }
        requestPermission();
        initView();
        readAccount();
    }

    //读取保存在本地的用户名和密码
    public void readAccount() {

        //创建SharedPreferences对象
        SharedPreferences sp = getSharedPreferences("userinfo", MODE_PRIVATE);
        //获得保存在SharedPredPreferences中的用户名和密码
        String userId = sp.getString("userId", "");
        String password = sp.getString("password", "");

        //没有账户密码，不请求登录
        if(userId.equals("") || password.equals(""))
            return;

        //在用户名和密码的输入框中显示用户名和密码
        user_edt.setText(userId);
        password_edt.setText(password);
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
            //检测是否有写的权限
            permission = ActivityCompat.checkSelfPermission(this,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE,REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化控件
     */
    private void initView(){

        //状态栏颜色
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));

        register_btn = (TextView) findViewById(R.id.register_btn);
        login_btn = (Button) findViewById(R.id.login_btn);
        user_edt = (TextInputEditText) findViewById(R.id.user_edt);
        password_edt = (TextInputEditText) findViewById(R.id.password_edt);
        checkBox = (CheckBox) findViewById(R.id.login_checkBox);

        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
                MyApplication.addDestroyActivity(MainActivity.this, "MainActivity");
            }
        });

        login_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                final String user_edt_str = user_edt.getText().toString();
                String password_edt_str = password_edt.getText().toString();

                if(user_edt_str.equals("") || password_edt_str.equals("")){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "账号密码不能为空", Toast.LENGTH_LONG).show();
                        }
                    });
                    return;
                }
                checkInfo(user_edt_str, password_edt_str);
            }
        });
    }

    /**
     * 检查用户信息是否已经上传
     * @param userId
     * @param password
     */
    private void checkInfo(final String userId, final String password){
        //检查权限
        requestPermission();

        OkHttpClient mOkHttpClient=new OkHttpClient();

        Request request = new Request.Builder()
                .url("http://212.64.92.236:20000/api/v1/user/?email="+userId)
                .get()
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
                    final String name = responseJson.getString("name");
                    Log.e("MainActivity ", "onResponse:" + code);
                    runOnUiThread(new Runnable() {
                        @Override//此时已在主线程中，更新UI
                        public void run() {
                            if(code == 1000 && name != null && !name.equals("null")) {
                                login(userId, password);
                            }
                            else{
                                Toast.makeText(MainActivity.this,"未填写用户信息，无法登录", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(MainActivity.this, AddProfileActivity.class);
                                intent.putExtra("userId", userId);
                                intent.putExtra("password", password);
                                startActivity(intent);
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
     * 处理登录事件
     */
    private void login(final String user_edt_str, final String password_edt_str){

        //检查权限
        requestPermission();

        //如果勾选了复选框
        if(checkBox.isChecked()) {

            SharedPreferences sp = getSharedPreferences("userinfo", MODE_PRIVATE);
            SharedPreferences.Editor ed = sp.edit();
            ed.putString("userId", user_edt_str);
            ed.putString("password", password_edt_str);
            ed.commit();
        }

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
                                MyApplication.addDestroyActivity(MainActivity.this, "MainActivity");
                                SharedPreferences sp = getSharedPreferences("loginlog", MODE_PRIVATE);
                                SharedPreferences.Editor ed = sp.edit();
                                ed.putString("userId", user_edt_str);
                                ed.putString("logtime", new Date().toString());
                                ed.commit();
                                //登录成功，进入系统
                                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                                intent.putExtra("userId", user_edt_str);
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
