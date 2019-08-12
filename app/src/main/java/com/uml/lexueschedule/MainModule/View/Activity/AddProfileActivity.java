package com.uml.lexueschedule.MainModule.View.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.uml.lexueschedule.ForumModule.Util.MyGlideEngine;
import com.uml.lexueschedule.MyApplication;
import com.uml.lexueschedule.R;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import org.jetbrains.annotations.NotNull;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.io.File;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddProfileActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private AppCompatEditText name_edt;
    private AppCompatEditText university_edt;
    private AppCompatEditText major_edt;
    private AppCompatEditText grade_edt;
    private Button commit_btn;
    private FloatingActionButton photo_fab;
    private ImageView profile_img;

    private String userId;
    private String profile_img_path;

    private static final int REQUEST_CODE_CHOOSE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_profile);

        userId = getIntent().getStringExtra("userId");
        initView();

        MyApplication.destroyActivity("MainActivity");
        MyApplication.destroyActivity("RegisterActivity");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_addprofile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_pass){
            Intent intent = new Intent(AddProfileActivity.this, HomeActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
            this.onDestroy();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 初始化控件
     */
    private void initView(){

        toolbar = (Toolbar) findViewById(R.id.profile_toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(null != actionBar)
            actionBar.setDisplayShowTitleEnabled(false);

        //状态栏颜色
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));

        name_edt = findViewById(R.id.profile_name);
        university_edt = findViewById(R.id.profile_university);
        major_edt = findViewById(R.id.profile_major);
        grade_edt = findViewById(R.id.profile_grade);
        commit_btn = findViewById(R.id.profile_commit_btn);
        photo_fab = findViewById(R.id.profile_photo_fab);
        profile_img = findViewById(R.id.profile_img);

        //提交
        commit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO
                if(name_edt.getText().toString().equals("")
                ||university_edt.getText().toString().equals("")
                ||major_edt.getText().toString().equals("")
                ||grade_edt.getText().toString().equals("")) {
                    Toast.makeText(AddProfileActivity.this, "请补全信息", Toast.LENGTH_LONG).show();
                    return;
                }
                commitProfile();
            }
        });

        //选择头像
        photo_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO
                chooseImages(1);
            }
        });

    }

    /**
     * 提交信息
     */
    private void commitProfile(){

        MediaType MEDIA_TYPE;
        MultipartBody.Builder multiBuilder = new MultipartBody.Builder();
        multiBuilder.setType(MultipartBody.FORM);

        File file = new File(profile_img_path);
        Log.e("test", "onResponse: 发送图片名称：" + file.getName());
        MEDIA_TYPE = file.getName().endsWith("png") ? MediaType.parse("image/png") : MediaType.parse("image/jpeg");
        RequestBody fileBody = MultipartBody.create(MEDIA_TYPE, file);
        multiBuilder.addFormDataPart("photo", file.getName(), fileBody);

        multiBuilder.addFormDataPart("email", userId);
        multiBuilder.addFormDataPart("name", name_edt.getText().toString());
        multiBuilder.addFormDataPart("university", university_edt.getText().toString());
        multiBuilder.addFormDataPart("major", major_edt.getText().toString());
        multiBuilder.addFormDataPart("grade", grade_edt.getText().toString());

        MultipartBody requestBody = multiBuilder.build();

        OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url("http://212.64.92.236:20000/api/v1/user/")
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e("test", "onFailure: 提交失败");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Log.e("test", "onResponse: 提交成功");
                Log.e("test", "onResponse: 返回内容：" + response.body().string());
                Intent intent = new Intent(AddProfileActivity.this, HomeActivity.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
            }
        });
    }

    /**
     * 选择头像
     * @param limitation
     */
    public void chooseImages(int limitation){

        Matisse.from(this)
                .choose(MimeType.of(MimeType.JPEG, MimeType.PNG))//照片视频全部显示MimeType.allOf()
                .countable(true)//true:选中后显示数字;false:选中后显示对号
                .maxSelectable(limitation)//最大选择数量为9
                //.addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
                .gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.grid_expected_size))//图片显示表格的大小
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)//图像选择和预览活动所需的方向
                .thumbnailScale(0.85f)//缩放比例
                .theme(R.style.Matisse_Zhihu)//主题  暗色主题 R.style.Matisse_Dracula
                .imageEngine(new MyGlideEngine())//图片加载方式，Glide4需要自定义实现
                .capture(true) //是否提供拍照功能，兼容7.0系统需要下面的配置
                //参数1 true表示拍照存储在共有目录，false表示存储在私有目录；参数2与 AndroidManifest中authorities值相同，用于适配7.0系统 必须设置
                .captureStrategy(new CaptureStrategy(true, "com.gordon.forum.fileprovider"))//存储到哪里
                .forResult(REQUEST_CODE_CHOOSE);//请求码
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE) {

            if(null != data) {
                List<Uri> mSelected = Matisse.obtainResult(data);
                profile_img_path = getPath(AddProfileActivity.this, mSelected.get(0));
                profile_img.setImageBitmap(getBitmapFromPath(profile_img_path));
            }else{
                Toast.makeText(this, "没有选择图片", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Uri转path
     * @param context
     * @param uri
     * @return
     */
    private String getPath(Context context, Uri uri) {
        String path = null;
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        if (cursor == null) {
            return null;
        }
        if (cursor.moveToFirst()) {
            try {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        cursor.close();
        return path;
    }

    /**
     * 根据路径得到位图
     * @param path
     * @return
     */
    public Bitmap getBitmapFromPath(String path) {

        if (!new File(path).exists()) {
            System.err.println("getBitmapFromPath: file not exists");
            return null;
        }
        byte[] buf = new byte[1024 * 1024 * 10];// 10M
        Bitmap bitmap = null;

        try {

            FileInputStream fis = new FileInputStream(path);
            int len = fis.read(buf, 0, buf.length);
            bitmap = BitmapFactory.decodeByteArray(buf, 0, len);
            if (bitmap == null) {
                System.out.println("len= " + len);
                System.err.println("path: " + path + "  could not be decode!!!");
            }
        } catch (Exception e) {
            e.printStackTrace();

        }

        return bitmap;
    }

}
