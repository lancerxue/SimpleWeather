package com.example.dennis.jdtq.impl;



import com.example.dennis.jdtq.data.WeatherDataBean;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * 天气接口
 * Created by dennis on 2017/6/23.
 */

public interface WeatherImpl {
//    http://v.juhe.cn/  ----> baseurl
//    weather/index?  ---->get地址
//    format=2&cityname=深圳&key=1582fe6f942663b4d2e59faacca3a147 --->format;cityname;key

    @GET("weather/index?")
   Call<WeatherDataBean> getWeather(@Query("format") int format, @Query("cityname") String cityname, @Query("key") String key);

}
