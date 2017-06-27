package com.example.dennis.jdtq.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import com.example.dennis.jdtq.MainActivity;
import com.example.dennis.jdtq.R;


/**
 *
 * Created by dennis on 2017/6/22.
 */


public class SplashActivity extends AppCompatActivity {
    
    private TextView tv_splash;

    //组合动画
    private AnimationSet set;
    //时间
    private int animTime = 2000;

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1001:
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        
        initView();
    }

    //初始化View
    private void initView() {
        tv_splash= (TextView) findViewById(R.id.tv_splash);

        //是否共用一个动画补间
        set=new AnimationSet(true);
        set.setDuration(animTime);
        //动画执行完之后保持状态
        set.setFillAfter(true);

        //缩放动画
        ScaleAnimation scale=new ScaleAnimation(0,1,0,1);
        scale.setDuration(animTime);

        set.addAnimation(scale);

        //平移动画
        TranslateAnimation translate=new TranslateAnimation(0,0,0,-200);
        translate.setDuration(animTime);
        set.addAnimation(translate);

        //动画执行
        tv_splash.startAnimation(set);

        //动画监听
        set.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                handler.sendEmptyMessageDelayed(1001,500);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }
}
