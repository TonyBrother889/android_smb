package com.cifs.activity;

import android.annotation.SuppressLint;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.SeekBar;

import com.cifs.base.BaseActivity;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by zhangxiang on 2018/4/26.
 */

public class MusicPlayActivity extends BaseActivity implements MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener {


    public MediaPlayer mediaPlayer; // 媒体播放器
 //   private SeekBar seekBar; // 拖动条
    private Timer mTimer = new Timer(); // 计时器

    // 初始化播放器
    public MusicPlayActivity(SeekBar seekBar) {
        super();
      //  this.seekBar = seekBar;
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);// 设置媒体流类型
            mediaPlayer.setOnBufferingUpdateListener(this);
            mediaPlayer.setOnPreparedListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 每一秒触发一次
        mTimer.schedule(timerTask, 0, 1000);
    }

    // 计时器
    TimerTask timerTask = new TimerTask() {

        @Override
        public void run() {
            if (mediaPlayer == null)
                return;
            if (mediaPlayer.isPlaying()/* && seekBar.isPressed() == false*/) {
                handler.sendEmptyMessage(0); // 发送消息
            }
        }
    };
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            int position = mediaPlayer.getCurrentPosition();
            int duration = mediaPlayer.getDuration();
            if (duration > 0) {
                // 计算进度（获取进度条最大刻度*当前音乐播放位置 / 当前音乐时长）
               // long pos = seekBar.getMax() * position / duration;
              //  seekBar.setProgress((int) pos);
            }
        }
    };


    public void play() {
        mediaPlayer.start();
    }

    /**
     * @param url url地址
     */
    public void playUrl(final String url) {

                try {
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(url); // 设置数据源
                    mediaPlayer.prepare(); // prepare自动播放
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (SecurityException e) {
                    e.printStackTrace();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

    }




    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int percent) {
        //seekBar.setSecondaryProgress(percent);
       // int currentProgress = seekBar.getMax() * mediaPlayer.getCurrentPosition() / mediaPlayer.getDuration();
    //    Log.e(currentProgress + "% play", percent + " buffer");

    }



    // 播放准备
    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
        Log.e("mediaPlayer", "onPrepared");
    }


    // 播放完成
    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {

    }

}
