package com.uml.lexueschedule.MainModule.View.Fragment;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.uml.lexueschedule.ForumModule.Util.BitmapUtil;
import com.uml.lexueschedule.ForumModule.Util.Constant;
import com.uml.lexueschedule.ForumModule.Util.MyGlideEngine;
import com.uml.lexueschedule.ForumModule.Util.UrlHelper;
import com.uml.lexueschedule.MainModule.Data.Model.UserInfo;
import com.uml.lexueschedule.MainModule.Util.BitmapConvertUtil;
import com.uml.lexueschedule.MainModule.View.Activity.AddProfileActivity;
import com.uml.lexueschedule.MainModule.View.Activity.MainActivity;
import com.uml.lexueschedule.MyApplication;
import com.uml.lexueschedule.R;
import com.uml.lexueschedule.ScheduleModule.Util.Connect;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private View view;

    private ConstraintLayout photo_bg;
    private TextView userId_tv;
    private TextView name_tv;
    private TextView university_tv;
    private TextView major_tv;
    private TextView grade_tv;
    private FloatingActionButton photo_fab;
    private CircleImageView photo_img;
    private ProgressDialog waitingDialog;

    private LinearLayout change_psw;
    private LinearLayout change_info;
    private LinearLayout exit_login;
    private LinearLayout feedback;
    private LinearLayout policy;

    private String userId;
    private UserInfo userInfo = null;

    private static final int REQUEST_CODE_CHOOSE = 3;
    private static final int UPLOAD_PHOTO = 100;
    private static final int UPLOAD_PASSWORD = 101;
    private static final int UPLOAD_INFO = 102;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (null != view) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (null != parent)
                parent.removeView(view);
        }
        else {
            view = inflater.inflate(R.layout.fragment_home, container, false);

            userId = getActivity().getSharedPreferences("loginlog", Context.MODE_PRIVATE).getString("userId", "");

            initView(view);
        }
        return view;
    }

    /**
     * 初始化控件
     * @param view
     */
    private void initView(View view){

        photo_bg = (ConstraintLayout) view.findViewById(R.id.homefragment_photocl);
        userId_tv = (TextView) view.findViewById(R.id.homefragment_useridtv);
        name_tv = (TextView) view.findViewById(R.id.homefragment_nametv);
        university_tv = (TextView) view.findViewById(R.id.homefragment_universitytv);
        major_tv = (TextView) view.findViewById(R.id.homefragment_majortv);
        grade_tv = (TextView) view.findViewById(R.id.homefragment_gradetv);
        photo_fab = (FloatingActionButton) view.findViewById(R.id.homefragment_photofab);
        photo_img = (CircleImageView) view.findViewById(R.id.homefragment_prodilephoto);

        change_psw = (LinearLayout) view.findViewById(R.id.homefragment_changepswll);
        change_info = (LinearLayout) view.findViewById(R.id.homefragment_changeinfoll);
        exit_login = (LinearLayout) view.findViewById(R.id.homefragment_exitloginll);
        feedback = (LinearLayout) view.findViewById(R.id.homefragment_feedbackll);
        policy = (LinearLayout) view.findViewById(R.id.homefragment_privacypolicyll);

        userId_tv.setText(userId);

        getUserInfo(userId);

        photo_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImages(1);
            }
        });

        //修改密码
        change_psw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //判断网络是否可用
                if(!Connect.isConnectIsNomarl(getContext())) {
                    Toast.makeText(getContext(),"网络无连接",Toast.LENGTH_SHORT).show();
                    return;
                }
                showInputDialog();
            }
        });

        //修改信息
        change_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //判断网络是否可用
                if(!Connect.isConnectIsNomarl(getContext())) {
                    Toast.makeText(getContext(),"网络无连接",Toast.LENGTH_SHORT).show();
                    return;
                }
                showMultiInputDialog();
            }
        });

        //退出登录
        exit_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //判断网络是否可用
                if(!Connect.isConnectIsNomarl(getContext())) {
                    Toast.makeText(getContext(),"网络无连接",Toast.LENGTH_SHORT).show();
                    return;
                }
                exitLogin();
            }
        });

        //反馈
        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //判断网络是否可用
                if(!Connect.isConnectIsNomarl(getContext())) {
                    Toast.makeText(getContext(),"网络无连接",Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

        //隐私政策
        policy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    /**
     * 检查用户信息是否已经上传
     * @param userId
     */
    private void getUserInfo(final String userId){

        //判断网络是否可用
        if(!Connect.isConnectIsNomarl(getContext())) {
            Toast.makeText(getContext(),"网络无连接",Toast.LENGTH_SHORT).show();
            return;
        }

        OkHttpClient mOkHttpClient=new OkHttpClient();

        Request request = new Request.Builder()
                .url(UrlHelper.URL_FOR_GETTING_INFO + "?email="+userId)
                .get()
                .build();

        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e("HomeActivity ", "onFailure:" + e.toString());
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                String responseStr = response.body().string();
                Log.e("HomeActivity ", "onResponse:" + responseStr);
                try {
                    JSONObject responseJson = new JSONObject(responseStr);
                    final int code = responseJson.getInt("code");
                    Log.e("HomeActivity ", "onResponse:" + code);
                    if(code == 1000){
                        userInfo = new UserInfo();
                        userInfo.setPassword(responseJson.getString("password"));
                        userInfo.setName(responseJson.getString("name"));
                        userInfo.setUniversity(responseJson.getString("university"));
                        userInfo.setMajor(responseJson.getString("major"));
                        userInfo.setGrade(responseJson.getString("grade"));
                        userInfo.setPhotoUrl(responseJson.getString("photo_url"));
                        Bitmap localPhoto = new BitmapUtil().getProfilePhoto(userInfo.getPhotoUrl());
                        try {
                            File file = new File(Constant.getFilePath()+"profile_photo.jpg");
                            FileOutputStream out = new FileOutputStream(file);
                            localPhoto.compress(Bitmap.CompressFormat.JPEG, 100, out);
                            out.flush();
                            out.close();
                            userInfo.setPhotoPath(file.getPath());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        //因头像url有问题，暂时使用测试url
                        //userInfo.setPhotoUrl(
                        //        "http://img.redocn.com/sheying/20150213/mulanweichangcaoyuanfengjing_3951976.jpg");

                        finishView();
                    }
                } catch(JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 当正确获取用户信息时调用
     */
    private void finishView(){
        Log.e("test", "initView: "+userInfo.getName());

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(null != userInfo) {
                    name_tv.setText(userInfo.getName());
                    university_tv.setText(userInfo.getUniversity());
                    major_tv.setText(userInfo.getMajor());
                    grade_tv.setText(userInfo.getGrade());
                    Bitmap normal = new BitmapUtil().getProfilePhoto(userInfo.getPhotoUrl());
                    Bitmap convert = new BitmapUtil().getProfilePhoto(userInfo.getPhotoUrl());
                    if (null != convert) {
                        photo_bg.setBackground(new BitmapDrawable(getContext().getResources(),
                                BitmapConvertUtil.rsBlur(getContext(), convert, 10)));
                        photo_img.setImageBitmap(normal);
                    }
                }
            }
        });
    }

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
                .captureStrategy(new CaptureStrategy(true, "com.uml.lexueschedule.fileprovider"))//存储到哪里
                .forResult(REQUEST_CODE_CHOOSE);//请求码
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE) {

            if(null != data) {
                List<Uri> mSelected = Matisse.obtainResult(data);
                userInfo.setPhotoPath(getPath(getContext(), mSelected.get(0)));
                showWaitingDialog();
                uploadUserInfo(UPLOAD_PHOTO);
            }else{
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), "没有选择图片", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    /**
     * 更新用户信息
     */
    private void uploadUserInfo(final int what){

        MediaType MEDIA_TYPE;
        MultipartBody.Builder multiBuilder = new MultipartBody.Builder();
        multiBuilder.setType(MultipartBody.FORM);

        File file = new File(userInfo.getPhotoPath());
        Log.e("test", "onResponse: 发送图片名称：" + file.getName());
        MEDIA_TYPE = file.getName().endsWith("png") ? MediaType.parse("image/png") : MediaType.parse("image/jpeg");
        RequestBody fileBody = MultipartBody.create(MEDIA_TYPE, file);
        multiBuilder.addFormDataPart("photo", file.getName(), fileBody);

        multiBuilder.addFormDataPart("email", userId);
        multiBuilder.addFormDataPart("password", userInfo.getPassword());
        multiBuilder.addFormDataPart("name", userInfo.getName());
        multiBuilder.addFormDataPart("university", userInfo.getUniversity());
        multiBuilder.addFormDataPart("major", userInfo.getMajor());
        multiBuilder.addFormDataPart("grade", userInfo.getGrade());

        MultipartBody requestBody = multiBuilder.build();

        OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(UrlHelper.URL_FOR_POSTING_INFO)
                .post(requestBody)
                .build();

        Log.e("test", "调用");

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e("test", "onFailure: 提交失败");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Log.e("test", "onResponse: 提交成功");
                ResponseBody responseBody = response.body();
                if(responseBody == null)
                    return;
                String responseStr = responseBody.string();
                Log.e("test", "onResponse: 返回内容：" + responseStr);
                if(responseStr.equals(""))
                    return;
                try {
                    JSONObject responseJson = new JSONObject(responseStr);
                    int code = responseJson.getInt("code");
                    if(code == 1000){
                        userInfo.setPassword(responseJson.getString("password"));
                        userInfo.setName(responseJson.getString("name"));
                        userInfo.setUniversity(responseJson.getString("university"));
                        userInfo.setMajor(responseJson.getString("major"));
                        userInfo.setGrade(responseJson.getString("grade"));
                        userInfo.setPhotoUrl(responseJson.getString("photo_url"));
                        //因头像url有问题，暂时使用测试url
                        //userInfo.setPhotoUrl(
                        //        "http://img.redocn.com/sheying/20150213/mulanweichangcaoyuanfengjing_3951976.jpg");
                        finishView();
                        if(what == UPLOAD_PHOTO) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (waitingDialog.isShowing())
                                        waitingDialog.dismiss();
                                }
                            });
                        }else if(what == UPLOAD_PASSWORD || what == UPLOAD_INFO){
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getContext(), "修改成功", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                }catch(JSONException e){
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 退出登录（后端未提供接口，所以只能清除本地登录日志）
     */
    private void exitLogin(){
        SharedPreferences sp = getActivity().getSharedPreferences("loginlog", Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.remove("userId");
        ed.remove("logtime");
        ed.commit();

        MyApplication.addDestroyActivity(getActivity(), "HomeActivity");
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);

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
     * 等待对话框
     */
    private void showWaitingDialog() {
        /*
         * 等待上传用户信息
         */
        waitingDialog=
                new ProgressDialog(getActivity());
        waitingDialog.setTitle("上传用户信息");
        waitingDialog.setMessage("等待中...");
        waitingDialog.setIndeterminate(true);
        waitingDialog.setCancelable(false);
        waitingDialog.show();
    }

    /**
     * 输入新密码
     */
    private void showInputDialog() {

        final EditText editText = new EditText(getActivity());
        AlertDialog.Builder inputDialog =
                new AlertDialog.Builder(getActivity());
        inputDialog.setTitle("请输入新密码").setView(editText);
        inputDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        while(userInfo == null);
                        Toast.makeText(getContext(), "password: "+editText.getText().toString(), Toast.LENGTH_SHORT).show();
                        userInfo.setPassword(editText.getText().toString());
                        uploadUserInfo(UPLOAD_PASSWORD);
                    }
                }).show();
    }

    /**
     * 修改信息对话框
     */
    private void showMultiInputDialog() {

        AlertDialog.Builder customizeDialog =
                new AlertDialog.Builder(getActivity());
        final View dialogView = LayoutInflater.from(getActivity())
                .inflate(R.layout.change_info,null);
        customizeDialog.setTitle("修改个人信息");
        customizeDialog.setView(dialogView);
        customizeDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 获取EditView中的输入内容
                        EditText name_edt =
                                (EditText) dialogView.findViewById(R.id.change_name);
                        EditText university_edt =
                                (EditText) dialogView.findViewById(R.id.change_university);
                        EditText major_edt =
                                (EditText) dialogView.findViewById(R.id.change_major);
                        EditText grade_edt =
                                (EditText) dialogView.findViewById(R.id.change_grade);
                        if(name_edt.getText().toString().equals("")
                        ||university_edt.getText().toString().equals("")
                        ||major_edt.getText().toString().equals("")
                        ||grade_edt.getText().toString().equals(""))
                            Toast.makeText(getContext(), "请补全信息", Toast.LENGTH_LONG).show();
                        else {
                            userInfo.setName(name_edt.getText().toString());
                            userInfo.setUniversity(university_edt.getText().toString());
                            userInfo.setMajor(major_edt.getText().toString());
                            userInfo.setGrade(grade_edt.getText().toString());
                            uploadUserInfo(UPLOAD_INFO);
                        }
                    }
                });
        customizeDialog.show();
    }

}
