package com.example.dennis.jdtq.ui;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.dennis.jdtq.R;
import com.example.dennis.jdtq.adapter.ListAdapter;

import java.util.ArrayList;


/**
 * 设置
 * Created by dennis on 2017/6/26.
 */

public class SettingActivity extends BaseActivity{
    
    private ListView mListView;
    private ArrayList<String> arrayList;
    private ListAdapter mAdapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        
        initView();
    }

    private void initView() {
        mListView= (ListView) findViewById(R.id.mListView);
        Intent intent=getIntent();
        arrayList=intent.getStringArrayListExtra("info");
        
        arrayList.add("版本号: "+getVersion());
        arrayList.add("意见反馈");
        arrayList.add("关于");
        
        mAdapter=new ListAdapter(this,arrayList);
        mListView.setAdapter(mAdapter);
        
        //设置监听
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 7:
                        startActivity(new Intent(SettingActivity.this,BackActivity.class));
                        break;
                    case 8:
                        startActivity(new Intent(SettingActivity.this,AboutActivity.class));
                        break;
                }
            }
        });
    }
    
    //获取版本号
    private String getVersion(){
       
        try {
            PackageManager pm=getPackageManager();
            PackageInfo packageInfo=pm.getPackageInfo(getPackageName(),0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "无法获取到版本号";
        }
    }
}
