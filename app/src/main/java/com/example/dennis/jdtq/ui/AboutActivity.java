package com.example.dennis.jdtq.ui;

import android.os.Bundle;
import android.widget.ListView;

import com.example.dennis.jdtq.R;
import com.example.dennis.jdtq.adapter.ListAdapter;

import java.util.ArrayList;

/**
 * Created by dennis on 2017/6/27.
 */

public class AboutActivity extends BaseActivity{
    
    private ListView mListView;
    private ArrayList<String> arrayList=new ArrayList<>();
    private ListAdapter mAdapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        
        initView();
    }

    private void initView() {
        mListView= (ListView) findViewById(R.id.mListView);
        
        arrayList.add("用户名：周周");
        arrayList.add("手机：18144350822");
        arrayList.add("QQ：1026965170");
        arrayList.add("加我微信： ↓");
        
        mAdapter=new ListAdapter(this,arrayList);
        mListView.setAdapter(mAdapter);
        
    }
}
