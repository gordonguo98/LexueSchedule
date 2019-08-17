package com.uml.lexueschedule.View.Fragment;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.uml.lexueschedule.Util.Connect;
import com.uml.lexueschedule.Util.UrlHelper;
import com.uml.lexueschedule.R;


/**
 * by 郭晓凡
 *
 * A simple {@link Fragment} subclass.
 */
public class NotificationFragment extends Fragment {

    private View view;
    private LinearLayout gitLl;
    private LinearLayout updateLl;
    private LinearLayout helpLl;

    public NotificationFragment() {
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
            view = inflater.inflate(R.layout.fragment_notification, container, false);

            initView(view);
        }
        return view;
    }

    /**
     * 初始化控件
     */
    private void initView(View view){

        gitLl = (LinearLayout) view.findViewById(R.id.git_ll);
        updateLl = (LinearLayout) view.findViewById(R.id.update_ll);
        helpLl= (LinearLayout) view.findViewById(R.id.help_ll);

        gitLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UrlHelper.openBrowser(getActivity(), "https://github.com/guoxiaofan980520/LexueSchedule");
            }
        });

        updateLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder normalDialog =
                        new AlertDialog.Builder(getContext());
                normalDialog.setIcon(R.drawable.ic_update);
                normalDialog.setTitle("更新日志");
                normalDialog.setMessage("版本1.0:" +
                        "\n\t·课表支持导入、上传、保存" +
                        "\n\t·课程论坛支持发帖、浏览、评论、点赞");
                // 显示
                normalDialog.show();
            }
        });

        helpLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!Connect.isConnectIsNomarl(getContext())){
                    Toast.makeText(getContext(), "网络无连接", Toast.LENGTH_LONG).show();
                    return;
                }else{
                    UrlHelper.openBrowser(getActivity(), "https://github.com/guoxiaofan980520/LexueSchedule/blob/master/README.md");
                }
            }
        });
    }

}
