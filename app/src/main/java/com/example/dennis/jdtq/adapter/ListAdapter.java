package com.example.dennis.jdtq.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.dennis.jdtq.R;

import java.util.ArrayList;

/**
 * 列表适配器
 * Created by dennis on 2017/6/27.
 */

public class ListAdapter extends BaseAdapter{
    
    private Context mContext;
    private LayoutInflater inflater;
    private ArrayList<String> arrayList;

    public ListAdapter(Context mContext, ArrayList<String> arrayList) {
        this.mContext = mContext;
        this.arrayList = arrayList;
        inflater= (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder=null;
        if (convertView==null){
            viewHolder=new ViewHolder();
            convertView=inflater.inflate(R.layout.list_item,null);
            viewHolder.tv_item= (TextView) convertView.findViewById(R.id.tv_item);
            convertView.setTag(viewHolder);
        }else {
            viewHolder= (ViewHolder) convertView.getTag();
        }
        
        viewHolder.tv_item.setText(arrayList.get(position));
        
        return convertView;
        
    }
    
    class ViewHolder{
        private TextView tv_item;
    }
}
