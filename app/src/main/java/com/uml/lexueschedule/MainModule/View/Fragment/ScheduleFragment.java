package com.uml.lexueschedule.MainModule.View.Fragment;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.uml.lexueschedule.R;
import com.uml.lexueschedule.ScheduleModule.Util.Getdata;
import com.uml.lexueschedule.ScheduleModule.View.Activity.BaseFuncActivity;
import com.uml.lexueschedule.ScheduleModule.View.Activity.ImportscheduleActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class ScheduleFragment extends Fragment {

    private View view;

    public ScheduleFragment() {
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
            view = inflater.inflate(R.layout.fragment_schedule, container, false);
            initView(view);
        }
        return view;
    }

    private void initView(View view){
        //从服务器获取课程信息
        try {
            Getdata.getdata();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Button importscheduleformweb = (Button) view.findViewById(R.id.importscheduleformweb);
        importscheduleformweb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(), ImportscheduleActivity.class);
                startActivity(intent);
            }
        });
        Button checkschedule = (Button) view.findViewById(R.id.checkschedule);
        checkschedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(), BaseFuncActivity.class);
                startActivity(intent);
            }
        });
    }

}
