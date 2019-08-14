package com.uml.lexueschedule.ForumModule.View.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.lzy.ninegrid.NineGridView;
import com.squareup.picasso.Picasso;
import com.uml.lexueschedule.ForumModule.Data.Adapter.PostAdapter;
import com.uml.lexueschedule.ForumModule.Data.Model.Post;
import com.uml.lexueschedule.ForumModule.Util.BitmapUtil;
import com.uml.lexueschedule.ForumModule.Util.GlideImageLoader;
import com.uml.lexueschedule.ForumModule.Util.UrlHelper;
import com.uml.lexueschedule.MyApplication;
import com.uml.lexueschedule.R;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class ForumActivity extends AppCompatActivity {

    private int courseId;
    private String userId;

    List<Post> postList = new ArrayList<>();

    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private PostAdapter postAdapter;

    private MyHandler myHandler;

    private static final int UPDATE_POST_LIST = 100;
    private static final int STOP_REFRESHING = 101;
    private static final int DELETE_POST = 102;
    private static final int REQUEST_FOR_NEWPOSTACTIVITY = 103;
    private static final int MAKE_TOAST = 104;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum);
        MyApplication.destroyActivity("AddProfileActivity");

        //设置状态栏，工具栏

        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_forum_toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(null != actionBar)
            actionBar.setDisplayShowTitleEnabled(false);
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));

        Intent intent = getIntent();
        if(null != intent){
            courseId = intent.getIntExtra("courseId", -1);
            //添加中间标题
            addMiddleTitle(this, "课程ID: "+courseId, toolbar);
        }

        userId = getSharedPreferences("loginlog", MODE_PRIVATE).getString("userId", "");

        verifyStoragePermissions(this);

        initView();
        initImgLoadingOptions();
        getPost();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_forum_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        if (item.getItemId() == R.id.action_new_post) {
            Intent intent = new Intent(ForumActivity.this, NewPostActivity.class);
            intent.putExtra("email", userId);
            intent.putExtra("course_id", courseId);
            startActivityForResult(intent, REQUEST_FOR_NEWPOSTACTIVITY);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void addMiddleTitle(Context context, CharSequence title, Toolbar toolbar) {

        TextView textView = new TextView(context);
        textView.setText(title);
        textView.setTextColor(getResources().getColor(R.color.textColor));
        textView.setTextSize(20);

        Toolbar.LayoutParams params = new Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_HORIZONTAL;
        toolbar.addView(textView, params);

    }

    private void getData(final String url){

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e("getData: ", "onFailure: " + e.toString());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                ResponseBody responseBody = response.body();
                String body = null;
                if(null != responseBody)
                    body = responseBody.string();
                if(null == body)
                    Log.e("getData: ", "onResponse: responsebody is null!");
                else {
                    Log.e("getData: ", "onResponse: responsebody is " + body);
                    if(!body.equals("500")) {
                        try {
                            Log.e("getData", "执行");
                            JSONObject jsonObject = JSON.parseObject(body);
                            int code = (int) jsonObject.get("code");
                            final String msg = (String) jsonObject.get("msg");
                            if(code == 1000) {
                                int record_num = (int) jsonObject.get("record_num");
                                postList.clear();
                                for (int i = 1; i <= record_num; i++) {
                                    JSONObject post_json = (JSONObject) jsonObject.get("post_" + i);
                                    Post newPost = JSON.toJavaObject(post_json, Post.class);
                                    BitmapUtil bitmapUtil = new BitmapUtil();
                                    newPost.getCreator().setProfile_photo_bitmap(bitmapUtil.getProfilePhoto(newPost.getCreator().getProfile_photo()));
                                    postList.add(newPost);
                                    Message message = new Message();
                                    message.what = UPDATE_POST_LIST;
                                    myHandler.sendMessage(message);
                                }
                                Message message = new Message();
                                message.what = STOP_REFRESHING;
                                myHandler.sendMessage(message);
                            }else{
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(ForumActivity.this, msg, Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("test", e.toString());
                        }
                    }else{
                        Log.e("getData", "else执行");
                        Message message = new Message();
                        message.what = MAKE_TOAST;
                        message.arg1 = 1000;
                        myHandler.sendMessage(message);
                    }
                }
            }
        });

    }

    public void deletePost(final String postId, final int position) {

        OkHttpClient client = new OkHttpClient();
        FormBody.Builder formBody = new FormBody.Builder();
        formBody.add("method", "delete");
        formBody.add("post_id", postId);
        formBody.add("email", userId);
        final Request request = new Request.Builder()
                .url(UrlHelper.URL_FOR_DELETING_POST)
                .post(formBody.build())
                .build();

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e("test", "onFailure: 删除帖子失败");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Log.e("test", "onResponse: 删除帖子成功");
                Message message = new Message();
                message.what = DELETE_POST;
                message.arg1 = position;
                myHandler.sendMessage(message);
            }
        });

    }

    private void initView() {

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_post_list);
        refreshLayout.setColorSchemeResources(R.color.colorPrimary);
        refreshLayout.setProgressBackgroundColorSchemeResource(android.R.color.white);

        recyclerView = (RecyclerView) findViewById(R.id.post_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        postAdapter = new PostAdapter(this, postList);
        postAdapter.setOnItemClickListener(new PostAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String post_json = JSON.toJSONString(postList.get(position));
                Intent intent = new Intent(ForumActivity.this, PostActivity.class);
                intent.putExtra("post", post_json);
                intent.putExtra("user_id", userId);
                startActivity(intent);
            }
        });
        postAdapter.setOnItemLongClickListener(new PostAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, final int position) {

                if(postList.get(position).getCreator().getPhone_num().equals(userId)){
                    //如果帖子是自己发布的，则可以删除
                    final AlertDialog.Builder normalDialog = new AlertDialog.Builder(ForumActivity.this);
                    normalDialog.setIcon(R.drawable.ic_delete_forever_black_24dp);
                    normalDialog.setTitle("删除帖子");
                    normalDialog.setMessage("确认删除?");
                    normalDialog.setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //删除帖子
                                    deletePost(postList.get(position).getPostId()+"", position);
                                }
                            });
                    normalDialog.setNegativeButton("取消",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //取消删除
                                }
                            });
                    // 显示
                    normalDialog.show();
                }

            }
        });

        recyclerView.addItemDecoration(new SpacesItemDecoration(14));//设定间隔
        recyclerView.setAdapter(postAdapter);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //下拉刷新帖子列表
                getPost();
                postAdapter.notifyDataSetChanged();
            }
        });

        myHandler = new MyHandler(ForumActivity.this, postAdapter, refreshLayout, postList);
    }

    private void initImgLoadingOptions(){

        /** Picasso 加载 */
        class PicassoImageLoader implements NineGridView.ImageLoader {

            @Override
            public void onDisplayImage(Context context, ImageView imageView, String url) {
                Picasso.with(context).load(url)//
                        .placeholder(R.drawable.ic_default_image)//
                        .error(R.drawable.ic_default_image)//
                        .into(imageView);
            }

            @Override
            public Bitmap getCacheImage(String url) {
                return null;
            }
        }

        NineGridView.setImageLoader(new GlideImageLoader());
    }

    private void getPost(){
        getData(UrlHelper.URL_FOR_GETTING_POST + "?" + "course_id=" + courseId);
    }

    static class MyHandler extends Handler{

        private Context context;
        private PostAdapter postAdapter;
        private SwipeRefreshLayout refreshLayout;
        private List<Post> postList;

        public MyHandler(Context context, PostAdapter postAdapter, SwipeRefreshLayout refreshLayout, List<Post> postList){
            this.context = context;
            this.postAdapter = postAdapter;
            this.refreshLayout = refreshLayout;
            this.postList = postList;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            if(msg.what == UPDATE_POST_LIST)
                postAdapter.notifyDataSetChanged();
            switch(msg.what){
                case UPDATE_POST_LIST:
                    postAdapter.notifyDataSetChanged();
                    break;
                case STOP_REFRESHING:
                    refreshLayout.setRefreshing(false);
                    break;
                case DELETE_POST:
                    postList.remove(msg.arg1);
                    postAdapter.notifyDataSetChanged();
                    break;
                case MAKE_TOAST:
                    if(msg.arg1 == 1000)
                        Toast.makeText(context, "该课程下尚未有帖子", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }

    public static void verifyStoragePermissions(Activity activity) {

        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == REQUEST_FOR_NEWPOSTACTIVITY) {
            int isSuccess = 0;
            if (null != data)
                isSuccess = data.getIntExtra("IsSuccess", 0);
            if (isSuccess == 1)
                getPost();
        }
    }
}

class SpacesItemDecoration extends RecyclerView.ItemDecoration {

    private int space;

    public SpacesItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                               @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        outRect.left = space;
        outRect.right = space;
        outRect.bottom = space;
        if (parent.getChildLayoutPosition(view) == 0)
            outRect.top = space;
    }
}
