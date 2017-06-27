package com.example.dennis.jdtq;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.DashPathEffect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.example.dennis.jdtq.data.WeatherDataBean;
import com.example.dennis.jdtq.entity.Constans;
import com.example.dennis.jdtq.impl.WeatherImpl;
import com.example.dennis.jdtq.ui.BaseActivity;
import com.example.dennis.jdtq.ui.SettingActivity;
import com.example.dennis.jdtq.utils.SharePreUtils;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends BaseActivity implements View.OnClickListener, OnChartGestureListener, OnChartValueSelectedListener{
    // Content View Elements

    private LinearLayout mLlRefresh;
    private ImageView mIvRefresh;
    private TextView mTvNowTime;
    private TextView mTvCity;
    private TextView mTvUpdateTime;
    private TextView mTvWeek;
    private ImageView mIvWeatherIcon;
    private TextView mTvCode;
    private TextView mTvWeather;
    private TextView mTvWind;
    private TextView mTvWindCode;

    //定位服务
    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListener();

    //提示框
    private ProgressDialog dialog;
    //城市
    private String city = "深圳";

    private WeatherImpl impl;

    private int format = 2;

    //动画
    private Animation anim;

    //折线图
    private LineChart mLineChart;

    private int mColor = 0xFF2AA0E7;
    
    //指标建议
    private ArrayList<String> mList=new ArrayList<>();

    //数据集
    private ArrayList<Entry> values=new ArrayList<>();

    //星期数组
    private String[] mWeek = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};

    //天气图标
    private int[] mIcon = {R.mipmap.weather_icon_white_01, R.mipmap.weather_icon_white_02, R.mipmap.weather_icon_white_03,
            R.mipmap.weather_icon_white_04, R.mipmap.weather_icon_white_05, R.mipmap.weather_icon_white_06,
            R.mipmap.weather_icon_white_07, R.mipmap.weather_icon_white_08, R.mipmap.weather_icon_white_09,
            R.mipmap.weather_icon_white_10, R.mipmap.weather_icon_white_11, R.mipmap.weather_icon_white_12,
            R.mipmap.weather_icon_white_13, R.mipmap.weather_icon_white_14, R.mipmap.weather_icon_white_15,
            R.mipmap.weather_icon_white_16, R.mipmap.weather_icon_white_17, R.mipmap.weather_icon_white_18,
            R.mipmap.weather_icon_white_19, R.mipmap.weather_icon_white_20, R.mipmap.weather_icon_white_21,
            R.mipmap.weather_icon_white_22, R.mipmap.weather_icon_white_23, R.mipmap.weather_icon_white_24,
            R.mipmap.weather_icon_white_25};

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                //动画停止
                case 1000:
                    if (anim != null) {
                        mIvRefresh.clearAnimation();
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //去掉ActionBar阴影
        getSupportActionBar().setElevation(0);

        initView();
    }

    //初始化View
    private void initView() {

        //声明LocationClient类
        mLocationClient = new LocationClient(getApplicationContext());
        //注册监听函数
        mLocationClient.registerLocationListener(myListener);

        initLocation();
        initDialog();
        dialog.show();
        initRetrofit();
        initLineChart();

        //开启定位
        mLocationClient.start();
        Log.i(Constans.TAG, "开始定位");

        mLlRefresh = (LinearLayout) findViewById(R.id.llRefresh);
        mLlRefresh.setOnClickListener(this);
        mIvRefresh = (ImageView) findViewById(R.id.ivRefresh);
        mTvNowTime = (TextView) findViewById(R.id.tvNowTime);
        mTvCity = (TextView) findViewById(R.id.tvCity);
        mTvUpdateTime = (TextView) findViewById(R.id.tvUpdateTime);
        mTvWeek = (TextView) findViewById(R.id.tvWeek);
        mIvWeatherIcon = (ImageView) findViewById(R.id.ivWeatherIcon);
        mTvCode = (TextView) findViewById(R.id.tvCode);
        mTvWeather = (TextView) findViewById(R.id.tvWeather);
        mTvWind = (TextView) findViewById(R.id.tvWind);
        mTvWindCode = (TextView) findViewById(R.id.tvWindCode);

        mTvNowTime.setText(SharePreUtils.getString(this, "now_time", "刷新"));
    }

    //初始化折线图
    private void initLineChart() {
        mLineChart = (LineChart) findViewById(R.id.mLineChart);
        //设置手势滑动事件
        mLineChart.setOnChartGestureListener(this);
        //设置数值选择监听
        mLineChart.setOnChartValueSelectedListener(this);
        //设置后台绘制
        mLineChart.setDrawGridBackground(false);

        // 不需要描述文本
        mLineChart.getDescription().setEnabled(false);

        // 支持触摸手势
        mLineChart.setTouchEnabled(true);

        // 设置缩放以及滑动
        mLineChart.setDragEnabled(true);
        mLineChart.setScaleEnabled(true);
        // if disabled, scaling can be done on x- and y-axis separately
        mLineChart.setPinchZoom(true);

        XAxis xAxis = mLineChart.getXAxis();
        xAxis.enableGridDashedLine(10f, 10f, 0f);
//        //设置最大值
//        xAxis.setAxisMaximum(7);
//        //设置最小值
//        xAxis.setAxisMaximum(1);

//        xAxis.isAvoidFirstLastClippingEnabled();
//        //设置间隔1天
//        xAxis.setGranularity(1);

        LimitLine ll1 = new LimitLine(30, "高温");
        ll1.setLineWidth(4f);
        ll1.enableDashedLine(10f, 10f, 0f);
        ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        ll1.setTextSize(10f);
        ll1.setLineColor(mColor);

        LimitLine ll2 = new LimitLine(0, "低温");
        ll2.setLineWidth(4f);
        ll2.enableDashedLine(10f, 10f, 0f);
        ll2.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        ll2.setTextSize(10f);
        ll2.setLineColor(mColor);

        YAxis leftAxis = mLineChart.getAxisLeft();
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        leftAxis.addLimitLine(ll1);
        leftAxis.addLimitLine(ll2);
        leftAxis.setAxisMaximum(40);
        leftAxis.setAxisMinimum(-10);
        leftAxis.setGranularity(10);
        //leftAxis.setYOffset(20f);
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawZeroLine(false);

        // limit lines are drawn behind data (and not on top)
        leftAxis.setDrawLimitLinesBehindData(true);

        mLineChart.getAxisRight().setEnabled(false);

        mLineChart.animateY(2500);
    }

    //设置数据
    private void setData(ArrayList<Entry> values) {
        Log.i(Constans.TAG,values.toString());
        LineDataSet set1;
        if (mLineChart.getData() != null &&
                mLineChart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) mLineChart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            mLineChart.getData().notifyDataChanged();
            mLineChart.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            set1 = new LineDataSet(values, "一周天气近况");

            // set the line to be drawn like this "- - - - - -"
            set1.enableDashedLine(10f, 5f, 0f);
            set1.enableDashedHighlightLine(10f, 5f, 0f);
            set1.setColor(mColor);
            set1.setCircleColor(mColor);
            set1.setLineWidth(1f);
            set1.setCircleRadius(3f);
            set1.setDrawCircleHole(false);
            set1.setValueTextSize(9f);
            set1.setDrawFilled(true);
            set1.setFormLineWidth(1f);
            set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            set1.setFormSize(15.f);
            set1.setFillColor(mColor);

            ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
            dataSets.add(set1); // add the datasets

            // create a data object with the datasets
            LineData data = new LineData(dataSets);
            // set data
            mLineChart.setData(data);
        }
    }


    //初始化dialog
    private void initDialog() {
        dialog = new ProgressDialog(this);
        dialog.setMessage("正在努力定位中...");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        //屏幕外点击无效
        dialog.setCancelable(false);
    }

    //初始化Retrofit
    private void initRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://v.juhe.cn/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        impl = retrofit.create(WeatherImpl.class);
    }

    //请求天气
    private void requestWeather(String city) {
        //异步操作
        impl.getWeather(format, city, Constans.WEATHER_KEY).enqueue(new Callback<WeatherDataBean>() {
            @Override
            public void onResponse(Call<WeatherDataBean> call, Response<WeatherDataBean> response) {
//                Log.i(Constans.TAG,response.body().getResult().getToday().getTemperature());
//                Log.i(Constans.TAG,response.body().getResult().getFuture().get(1).getWeek());
                WeatherDataBean bean = response.body();
                //城市
                mTvCity.setText(bean.getResult().getToday().getCity() + "市");
                //更新时间
                mTvUpdateTime.setText(bean.getResult().getToday().getDate_y());
                //农历/星期
                mTvWeek.setText(bean.getResult().getToday().getWeek());
                //度数
                mTvCode.setText(bean.getResult().getToday().getTemperature() + "°");
                //天气
                mTvWeather.setText(bean.getResult().getToday().getWeather());
                // 风向
                mTvWind.setText("风向" + " | " + bean.getResult().getSk().getWind_direction());
                //风力
                mTvWindCode.setText("风力" + " | " + bean.getResult().getSk().getWind_strength());
                //图标
                String img = bean.getResult().getToday().getWeather_id().getFa();

                int imgCode = Integer.parseInt(img);
                if (imgCode <= 25) {
                    mIvWeatherIcon.setBackgroundResource(mIcon[imgCode - 1]);
                } else {
                    mIvWeatherIcon.setBackgroundResource(mIcon[0]);
                }

                //折线图数据
                for (int i = 0; i <bean.getResult().getFuture().size() ; i++) {
                    Log.i(Constans.TAG,bean.getResult().getFuture().size()+"");
                    String temperature = bean.getResult().getFuture().get(i).getTemperature().substring(0,2);
                    int weatherCode= Integer.parseInt(temperature);
                    Log.i(Constans.TAG,i+"");
                    values.add(new Entry(i+1,weatherCode));
                }
                //设置数据
                setData(values);
                mList.clear();
                //添加指标数据
                mList.add("穿衣指数："+bean.getResult().getToday().getDressing_index());
                mList.add("穿衣建议："+bean.getResult().getToday().getDressing_advice());
                mList.add("紫外线强度："+bean.getResult().getToday().getUv_index());
                mList.add("游泳指数："+bean.getResult().getToday().getWash_index());
                mList.add("旅行指数："+bean.getResult().getToday().getTravel_index());
                mList.add("锻炼指数："+bean.getResult().getToday().getExercise_index());
               
            }

            @Override
            public void onFailure(Call<WeatherDataBean> call, Throwable t) {

            }
        });
    }


    //初始化定位
    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);

        //可选，默认gcj02，设置返回的定位结果坐标系
        option.setCoorType("bd09ll");

        //可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setScanSpan(0);

        //可选，设置是否需要地址信息，默认不需要
        option.setIsNeedAddress(true);

        //可选，默认false,设置是否使用gps
        option.setOpenGps(true);

        //可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
        option.setLocationNotify(true);


        //可选，默认false，设置是否需要位置语义化结果，
        // 可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationDescribe(true);

        //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIsNeedLocationPoiList(true);

        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，
        // 设置是否在stop的时候杀死这个进程，默认不杀死
        option.setIgnoreKillProcess(false);

        //可选，默认false，设置是否收集CRASH信息，默认收集
        option.SetIgnoreCacheException(false);


        //可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
        option.setEnableSimulateGps(false);

        mLocationClient.setLocOption(option);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llRefresh:
                startAnimation();
                handler.sendEmptyMessageDelayed(1000, 3000);
                //设置当前时间
                mTvNowTime.setText(getNomeTime());
                requestWeather(city);
                break;
        }
    }

    //获取当前时间
    private String getNomeTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        return format.format(new Date());
    }

    //开始动画
    private void startAnimation() {
        anim = AnimationUtils.loadAnimation(this, R.anim.iv_rotating);
        LinearInterpolator ll = new LinearInterpolator();
        anim.setInterpolator(ll);
        if (anim != null) {
            mIvRefresh.startAnimation(anim);
        }
    }

    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }

    @Override
    public void onChartLongPressed(MotionEvent me) {

    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {

    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {

    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {

    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {

    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {

    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }

    //定位回调
    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {

            dialog.dismiss();
            switch (location.getLocType()) {
                case BDLocation.TypeGpsLocation:
                case BDLocation.TypeNetWorkLocation:
                case BDLocation.TypeOffLineLocation:
                    city = location.getCity();
                    Log.i(Constans.TAG, "定位成功: " + city);
                    break;
                case BDLocation.TypeServerError:
                case BDLocation.TypeNetWorkException:
                case BDLocation.TypeCriteriaException:
                    Log.i(Constans.TAG, "定位失败");
                    break;
            }
            //请求
            requestWeather(city);

        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        SharePreUtils.putString(this, "now_time", mTvNowTime.getText().toString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.item_setting:
                Intent intent=new Intent(this, SettingActivity.class);
                intent.putStringArrayListExtra("info",mList);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
        
    }
}

















