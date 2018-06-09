package com.cifs.activity;

import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cifs.R;
import com.cifs.base.BaseActivity;
import com.cifs.constant.Constants;
import com.cifs.utils.Utils;
import com.cifs.widget.MediaController;
import com.jia.jsplayer.bean.VideoInfo;
import com.jia.jsplayer.listener.OnVideoControlListener;
import com.jia.jsplayer.utils.DisplayUtils;
import com.jia.jsplayer.video.JsPlayer;
import com.pili.pldroid.player.AVOptions;
import com.pili.pldroid.player.PLOnAudioFrameListener;
import com.pili.pldroid.player.PLOnBufferingUpdateListener;
import com.pili.pldroid.player.PLOnCompletionListener;
import com.pili.pldroid.player.PLOnErrorListener;
import com.pili.pldroid.player.PLOnInfoListener;
import com.pili.pldroid.player.PLOnVideoFrameListener;
import com.pili.pldroid.player.PLOnVideoSizeChangedListener;
import com.pili.pldroid.player.widget.PLVideoView;

import java.util.Arrays;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class VideoPlayerActivity2 extends BaseActivity {


    /* @InjectView(R.id.toolbar)
     Toolbar toolbar;*/
    @InjectView(R.id.jsPlayer)
    JsPlayer player;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videoplayer2);
        ButterKnife.inject(this);
        /*  setSupportActionBar(toolbar);*/
        //  ActionBar supportActionBar = getSupportActionBar();
        ///supportActionBar.setDisplayHomeAsUpEnabled(true);
        //supportActionBar.setDisplayShowTitleEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        String moveUrl = getIntent().getStringExtra(Constants.INTENT_MOVE_URL);

        DisplayUtils.toggleScreenOrientation(VideoPlayerActivity2.this);

//        player.setOnVideoControlListener(new OnVideoControlListener() {
//            @Override
//            public void onStartPlay() {
//                player.startPlay();
//            }
//
//            @Override
//            public void onBack() {
//
//            }
//
//            @Override
//            public void onFullScreen() {
//                DisplayUtils.toggleScreenOrientation(VideoPlayerActivity2.this);
//            }
//
//            @Override
//            public void onRetry(int errorStatus) {
//
//            }
//        });

        player.setPath(new VideoInfo("艺术人生", moveUrl));

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    @Override
    public void onBackPressed() {
        if (!DisplayUtils.isPortrait(this)) {
            if (!player.isLock()) {
                DisplayUtils.toggleScreenOrientation(this);
            }
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        player.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        player.onDestroy();
    }

}
