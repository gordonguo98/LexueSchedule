package com.uml.lexueschedule.View.Activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.teprinciple.mailsender.Mail;
import com.teprinciple.mailsender.MailSender;
import com.uml.lexueschedule.R;
import com.uml.lexueschedule.Util.Connect;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * 用于发送反馈
 *
 * 2019年 08月 17日
 *
 * by 郭晓凡
 */

public class FeedbackActivity extends AppCompatActivity {

    private AppCompatEditText phoneType;
    private AppCompatEditText feedback;
    private AppCompatEditText mail;
    private Button commitFeedback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(null != actionBar)
            actionBar.setDisplayShowTitleEnabled(false);

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));

        initView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 初始化控件
     */
    private void initView(){

        phoneType = (AppCompatEditText) findViewById(R.id.phone_type);
        feedback = (AppCompatEditText) findViewById(R.id.feedback);
        mail = (AppCompatEditText) findViewById(R.id.mail);
        commitFeedback = (Button) findViewById(R.id.feedback_commit_btn);

        commitFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!Connect.isConnectIsNomarl(FeedbackActivity.this)){
                    Toast.makeText(FeedbackActivity.this, "网络无连接", Toast.LENGTH_LONG).show();
                    return;
                }

                if(mail.getText().toString().equals("")
                ||phoneType.getText().toString().equals("")
                ||feedback.getText().toString().equals("")){
                    Toast.makeText(FeedbackActivity.this, "请补全信息", Toast.LENGTH_LONG).show();
                    return;
                }else{
                    // 创建邮箱
                    Mail send = new Mail();
                    send.setMailServerHost("smtp.qq.com");
                    send.setMailServerPort("587");
                    send.setFromAddress("1923984897@qq.com");
                    send.setPassword("bchkmvbupsvveejj");
                    ArrayList<String> receiver = new ArrayList<>();
                    receiver.add("gordonguo98@gmail.com");
                    send.setToAddress(receiver);
                    send.setSubject("Feedback");
                    send.setContent("机型：" + phoneType.getText().toString()
                            + "\n反馈：" + feedback.getText().toString()
                            + "\n邮箱：" + mail.getText().toString());
                    // 发送邮箱
                    MailSender.getInstance().sendMail(send, new MailSender.OnMailSendListener() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(FeedbackActivity.this, "发送成功", Toast.LENGTH_LONG).show();
                            finish();
                        }

                        @Override
                        public void onError(@NotNull Throwable throwable) {
                            throwable.printStackTrace();
                            Toast.makeText(FeedbackActivity.this, "发送失败", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    });
                }
            }
        });

    }
}
