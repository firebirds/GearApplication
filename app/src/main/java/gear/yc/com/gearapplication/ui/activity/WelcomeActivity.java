package gear.yc.com.gearapplication.ui.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;

import javax.inject.Inject;

import gear.yc.com.gearapplication.R;
import gear.yc.com.gearapplication.base.BaseActivity;
import gear.yc.com.gearapplication.component.DaggerComponentManager;
import gear.yc.com.gearapplication.databinding.ActivityWelcomeBinding;
import gear.yc.com.gearapplication.pojo.Clock;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * GearApplication
 * 欢饮页面并在页面中显示一个时钟，时钟计时为Rx方式
 * Created by YichenZ on 2016/3/23 11:14.
 */
public class WelcomeActivity extends BaseActivity {
    //几秒之后跳转页面
    static final short TIME_COUNT=5000;
    ActivityWelcomeBinding binding;

    @Inject
    Clock mClock;

    long startTime;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置全屏效果
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        initUI();
        initData();

        gotoPage();
    }

    @Override
    public void initUI() {
        super.initUI();
        binding= DataBindingUtil.setContentView(this,R.layout.activity_welcome);

        AnimationSet animationSet=new AnimationSet(true);
        TranslateAnimation mHiddenAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                3.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        mHiddenAction.setDuration(3000);
        AlphaAnimation alphaAnimation =new AlphaAnimation(0.1f,1.0f);
        alphaAnimation.setDuration(2000);
        animationSet.addAnimation(mHiddenAction);
        animationSet.addAnimation(alphaAnimation);
        binding.tvTitle.setAnimation(animationSet);
    }

    @Override
    public void initData() {
        super.initData();
        DaggerComponentManager.builder().build().inject(this);
        Uri uri =Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.guide_1);
        binding.cvvVideoData.palyVideo(uri);
    }

    /**
     * 获取到开始时间戳，并在n秒后跳转页面
     */
    private void gotoPage() {
        startTime= System.currentTimeMillis();
        Observable.just(mClock)
                .map(f -> {
                    while (true){
                        binding.setClock(f.setTime());
                        if(System.currentTimeMillis()-startTime >=TIME_COUNT) {
                            break;
                        }
                    }
                    return true;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    startActivity(new Intent(this, gear.yc.com.gearapplication.ui.mvp.travelnotes.TravelNotesActivity.class));
                    finish();
                });
    }

    @Override
    protected void onDestroy() {
        binding.cvvVideoData.stopPlayback();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

    }
}
