package com.cifs;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.rtp.AudioCodec;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.util.Log;

import org.apache.commons.io.IOUtils;
import org.cybergarage.http.HTTPRequest;
import org.cybergarage.http.HTTPRequestListener;
import org.cybergarage.http.HTTPResponse;
import org.cybergarage.http.HTTPServerList;
import org.cybergarage.http.HTTPStatus;

import java.io.BufferedInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.zip.InflaterInputStream;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;

public class SmbFileMusicPlay {


    private final int bufSize;
   // private final AudioTrack audioTrack;
   // private MediaCodec mediaDecode;
   // private MediaCodec mediaEncode;
    private long contentLen;
    private InputStream contentIn;
    private HTTPServerList httpServerList = new HTTPServerList();
    private int HTTPPort = 2222;
    // 绑定的ip
    private String bindIP = null;
    public SmbFileMusicPlay(Context context) {
        super();
        // 获取最小缓冲区
        bufSize = AudioTrack.getMinBufferSize(32000, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
        // 实例化AudioTrack(设置缓冲区为最小缓冲区的2倍，至少要等于最小缓冲区)
      //  audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 32000, AudioFormat.CHANNEL_OUT_MONO,
        //        AudioFormat.ENCODING_PCM_16BIT, bufSize * 2, AudioTrack.MODE_STREAM);

        // 设置音量
        //   audioTrack.setVolume(50f);
        // 设置播放频率
        //audioTrack.setPlaybackRate(50);
       // audioTrack.play();
        // 获取音乐文件输入流
    }

    public HTTPServerList getHttpServerList() {
        return httpServerList;
    }

    public int getHTTPPort() {
        return HTTPPort;
    }

    public void setHTTPPort(int hTTPPort) {
        HTTPPort = hTTPPort;
    }

    public String getBindIP() {
        return bindIP;
    }

    public void setBindIP(String bindIP) {
        this.bindIP = bindIP;
    }

    public void playFileStreamMusic(final String smbFileUrl) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                int retryCnt = 0;
                try {

                    SmbFile smbFile = new SmbFile(smbFileUrl);
                    //  SmbFileInputStream smbFileInputStream = new SmbFileInputStream(smbFile);
                    //  InputStream bis = new DataInputStream(smbFileInputStream);

                    // 获取文件的大小
                    contentLen = smbFile.length();

                    // 获取文文件流
                    contentIn = smbFile.getInputStream();

                    contentIn.close();

                    int bindPort = getHTTPPort();

                    HTTPServerList hsl = getHttpServerList();
                    while (hsl.open(2222) == false) {
                        retryCnt++;
                        // 重试次数大于服务器重试次数时返回
                        if (100 < retryCnt) {
                            return;
                        }
                        setHTTPPort(bindPort + 1);
                        bindPort = getHTTPPort();
                    }
                    // 给集合中的每个HTTPServer对象添加HTTPRequestListener对象
                    hsl.addRequestListener(new HTTPRequestListener() {
                        @Override
                        public void httpRequestRecieved(HTTPRequest httpRequest) {
                            if (contentLen <= 0 || contentIn == null) {
                                httpRequest.returnBadRequest();
                                return;
                            }

                            HTTPResponse httpRes = new HTTPResponse();
                            // httpRes.setContentType(contentType);
                            httpRes.setStatusCode(HTTPStatus.OK);
                            httpRes.setContentLength(contentLen);
                            httpRes.setContentInputStream(contentIn);

                            httpRequest.post(httpRes);
                        }
                    });

                    // 调用集合中所有HTTPServer的start方法
                    hsl.start();



                /*    byte[] buffer = new byte[bufSize * 2];

                    // 获取音乐文件输入流
                    while (bis.read(buffer) > 0) {
                        System.out.println("读取数据中...");
                        // 将读取的数据，写入Audiotrack
                        audioTrack.write(buffer, 0, buffer.length);

                    }*/

                //    audioTrack.stop();
                 //   audioTrack.release();
                  //  bis.close();
                } catch (SmbException e) {
                    e.printStackTrace();
                    System.out.println("异常1...");
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    System.out.println("异常2...");
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                    System.out.println("异常3...");
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("异常3...");
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("异常4...");
                }
            }
        }).start();

    }

}
