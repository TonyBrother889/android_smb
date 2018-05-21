package com.cifs;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.util.Log;

import com.cifs.music.AACDecoderUtil;

import org.cybergarage.http.HTTPRequest;
import org.cybergarage.http.HTTPRequestListener;
import org.cybergarage.http.HTTPResponse;
import org.cybergarage.http.HTTPServerList;
import org.cybergarage.http.HTTPStatus;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;

public class SmbFileMusicPlay {


    private final int bufSize;
    private final AudioTrack audioTrack;
    private long contentLen;
    private FileInputStream contentIn;

    //根据帧率获取的解码每帧需要休眠的时间,根据实际帧率进行操作
    private int PRE_FRAME_TIME = 1000 / 50;

    //文件读取完成标识
    boolean isFinish = false;
    //这个值用于找到第一个帧头后，继续寻找第二个帧头，如果解码失败可以尝试缩小这个值
    int FRAME_MIN_LEN = 50;
    //一般AAC帧大小不超过200k,如果解码失败可以尝试增大这个值
    int FRAME_MAX_LEN = 100 * 1024;
    //记录获取的帧数
    int count = 0;
    //音频解码器
    private AACDecoderUtil audioUtil;
    private MediaCodec mediaEncode;
    private MediaCodec mediaDecode;

    public SmbFileMusicPlay(Context context) {
        super();
        // 获取最小缓冲区
        bufSize = AudioTrack.getMinBufferSize(44000, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
        // 实例化AudioTrack(设置缓冲区为最小缓冲区的2倍，至少要等于最小缓冲区)
        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 44000, AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT, bufSize * 2, AudioTrack.MODE_STREAM);

        // 设置音量
        //audioTrack.setVolume(50f);
        // 设置播放频率
        //audioTrack.setPlaybackRate(50);
        audioTrack.play();
        // 获取音乐文件输入流

        this.audioUtil = new AACDecoderUtil();
    }

    public void playFileStreamMusic(final File file) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    //  SmbFile smbFile = new SmbFile(smbFileUrl);
                    //  SmbFileInputStream smbFileInputStream = new SmbFileInputStream(smbFile);
                    //  InputStream bis = new DataInputStream(smbFileInputStream);

                    // 获取文件的大小
                    //   contentLen = smbFile.length();

                    // 获取文文件流
                    //   contentIn = smbFile.getInputStream();
                    Log.e("开始解析", "开始解析");
                    contentIn = new FileInputStream(file);
                  /*  //fileEX(contentIn);
                    contentIn.close();*/

                    byte[] buffer = new byte[bufSize * 2];

                    // 获取音乐文件输入流
                    while (contentIn.read(buffer) > 0) {
                        System.out.println("读取数据中...");
                        // 将读取的数据，写入Audiotrack
                        audioTrack.write(buffer, 0, buffer.length);

                    }

                    audioTrack.stop();
                    audioTrack.release();
                    contentIn.close();
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

   /* *//**
     * 初始化解码器
     *//*
    private void initMediaDecode() {
        try {
            MediaExtractor mediaExtractor = new MediaExtractor();//此类可分离视频文件的音轨和视频轨道
             mediaExtractor.setDataSource(srcPath);//媒体文件的位置
            for (int i = 0; i < mediaExtractor.getTrackCount(); i++) {//遍历媒体轨道 此处我们传入的是音频文件，所以也就只有一条轨道
                MediaFormat format = mediaExtractor.getTrackFormat(i);
                String mime = format.getString(MediaFormat.KEY_MIME);
                if (mime.startsWith("audio")) {//获取音频轨道
//                    format.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, 200 * 1024);
                    mediaExtractor.selectTrack(i);//选择此音频轨道
                    //创建Decode解码器
                    mediaDecode = MediaCodec.createDecoderByType(mime);
                    mediaDecode.configure(format, null, null, 0);
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (mediaDecode == null) {
            Log.e("music", "create mediaDecode failed");
            return;
        }
        mediaDecode.start();//启动MediaCodec ，等待传入数据
        ByteBuffer[] decodeInputBuffers = mediaDecode.getInputBuffers();//MediaCodec在此ByteBuffer[]中获取输入数据
        ByteBuffer[] decodeOutputBuffers = mediaDecode.getOutputBuffers();//MediaCodec将解码后的数据放到此ByteBuffer[]中 我们可以直接在这里面得到PCM数据
        MediaCodec.BufferInfo decodeBufferInfo = new MediaCodec.BufferInfo();//用于描述解码得到的byte[]数据的相关信息
        Log.e("buffers:", decodeInputBuffers.length + " ");
    }*/

    /**
     * 初始化AAC编码器
     */
    private void initAACMediaEncode() {
        try {
            //参数对应-> mime type、采样率、声道数
            MediaFormat encodeFormat = MediaFormat.createAudioFormat(MediaFormat.MIMETYPE_AUDIO_MPEG, 44100, 2);
            //比特率
            encodeFormat.setInteger(MediaFormat.KEY_BIT_RATE, 96000);
            encodeFormat.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC);
            //作用于inputBuffer的大小
            encodeFormat.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, 100 * 1024);
            mediaEncode = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_AUDIO_MPEG);
            mediaEncode.configure(encodeFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (mediaEncode == null) {
            Log.e("music", "create mediaEncode failed");
            return;
        }
        mediaEncode.start();
        ByteBuffer[] encodeInputBuffers = mediaEncode.getInputBuffers();
        ByteBuffer[] encodeOutputBuffers = mediaEncode.getOutputBuffers();
        MediaCodec.BufferInfo encodeBufferInfo = new MediaCodec.BufferInfo();
    }

    /**
     * 解码{@link #}音频文件 得到PCM数据块
     *
     * @return 是否解码完所有数据
     */
    private void srcAudioFormatToPCM(InputStream inputStream) {
     /*   for (int i = 0; i < decodeInputBuffers.length - 1; i++) {
            int inputIndex = mediaDecode.dequeueInputBuffer(-1);//获取可用的inputBuffer -1代表一直等待，0表示不等待 建议-1,避免丢帧
            if (inputIndex < 0) {
                codeOver = true;
                return;
            }

            ByteBuffer inputBuffer = decodeInputBuffers[inputIndex];//拿到inputBuffer
            inputBuffer.clear();//清空之前传入inputBuffer内的数据
            int sampleSize = mediaExtractor.readSampleData(inputBuffer, 0);//MediaExtractor读取数据到inputBuffer中
            if (sampleSize < 0) {//小于0 代表所有数据已读取完成
                codeOver = true;
            } else {
                mediaDecode.queueInputBuffer(inputIndex, 0, sampleSize, 0, 0);//通知MediaDecode解码刚刚传入的数据
                mediaExtractor.advance();//MediaExtractor移动到下一取样处
                decodeSize += sampleSize;
            }
        }

        //获取解码得到的byte[]数据 参数BufferInfo上面已介绍 10000同样为等待时间 同上-1代表一直等待，0代表不等待。此处单位为微秒
        //此处建议不要填-1 有些时候并没有数据输出，那么他就会一直卡在这 等待
        int outputIndex = mediaDecode.dequeueOutputBuffer(decodeBufferInfo, 10000);

//        showLog("decodeOutIndex:" + outputIndex);
        ByteBuffer outputBuffer;
        byte[] chunkPCM;
        while (outputIndex >= 0) {//每次解码完成的数据不一定能一次吐出 所以用while循环，保证解码器吐出所有数据
            outputBuffer = decodeOutputBuffers[outputIndex];//拿到用于存放PCM数据的Buffer
            chunkPCM = new byte[decodeBufferInfo.size];//BufferInfo内定义了此数据块的大小
            outputBuffer.get(chunkPCM);//将Buffer内的数据取出到字节数组中
            outputBuffer.clear();//数据取出后一定记得清空此Buffer MediaCodec是循环使用这些Buffer的，不清空下次会得到同样的数据
            putPCMData(chunkPCM);//自己定义的方法，供编码器所在的线程获取数据,下面会贴出代码
            mediaDecode.releaseOutputBuffer(outputIndex, false);//此操作一定要做，不然MediaCodec用完所有的Buffer后 将不能向外输出数据
            outputIndex = mediaDecode.dequeueOutputBuffer(decodeBufferInfo, 10000);//再次获取数据，如果没有数据输出则outputIndex=-1 循环结束
        }*/

    }


    public void fileEX(FileInputStream inputStream) {
        try {
            //保存完整数据帧
            byte[] frame = new byte[FRAME_MAX_LEN];
            //当前帧长度
            int frameLen = 0;
            //每次从文件读取的数据
            byte[] readData = new byte[10 * 1024];
            //开始时间
            long startTime = System.currentTimeMillis();
            //循环读取数据
            while (!isFinish) {
                // Log.e("开始解析", inputStream.available()+"");
                if (inputStream.available() > 0) {
                    int readLen = inputStream.read(readData);
                    //当前长度小于最大值
                    if (frameLen + readLen < FRAME_MAX_LEN) {
                        //将readData拷贝到frame
                        System.arraycopy(readData, 0, frame, frameLen, readLen);
                        //修改frameLen
                        frameLen += readLen;
                        //寻找第一个帧头
                        int headFirstIndex = findHead(frame, 0, frameLen);
                        //Log.e("开始解析+寻找第一个帧头", headFirstIndex+"");
                        // Log.e("开始解析+是帧头", isHead(frame, headFirstIndex)+"");
                        while (headFirstIndex >= 0 && isHead(frame, headFirstIndex)) {
                            //寻找第二个帧头
                            int headSecondIndex = findHead(frame, headFirstIndex + FRAME_MIN_LEN, frameLen);
                            //如果第二个帧头存在，则两个帧头之间的就是一帧完整的数据
                            if (headSecondIndex > 0 && isHead(frame, headSecondIndex)) {
                                //视频解码
                                count++;
                                //  Log.e("ReadAACFileThread", "Length : " + (headSecondIndex - headFirstIndex));
                                audioUtil.decode(frame, headFirstIndex, headSecondIndex - headFirstIndex);
                                //截取headSecondIndex之后到frame的有效数据,并放到frame最前面
                                byte[] temp = Arrays.copyOfRange(frame, headSecondIndex, frameLen);
                                System.arraycopy(temp, 0, frame, 0, temp.length);
                                //修改frameLen的值
                                frameLen = temp.length;
                                //线程休眠
                                sleepThread(startTime, System.currentTimeMillis());
                                //重置开始时间
                                startTime = System.currentTimeMillis();
                                //继续寻找数据帧
                                headFirstIndex = findHead(frame, 0, frameLen);
                            } else {
                                //找不到第二个帧头
                                headFirstIndex = -1;
                            }
                        }
                    } else {
                        //如果长度超过最大值，frameLen置0
                        frameLen = 0;
                    }
                } else {
                    //文件读取结束
                    isFinish = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 寻找指定buffer中AAC帧头的开始位置
     *
     * @param startIndex 开始的位置
     * @param data       数据
     * @param max        需要检测的最大值
     * @return
     */
    private int findHead(byte[] data, int startIndex, int max) {
        int i;
        for (i = startIndex; i <= max; i++) {
            //发现帧头
            if (isHead(data, i)) {
                break;
            }
        }
        //检测到最大值，未发现帧头
        if (i == max) {
            i = -1;
        }
        return i;
    }

    /**
     * 判断aac帧头
     */
    private boolean isHead(byte[] data, int offset) {
        boolean result = false;
        if (data[offset] == (byte) 0xFF && data[offset + 1] == (byte) 0xF1
                && data[offset + 3] == (byte) 0x80) {
            result = true;
        }
        return result;
    }

    private void sleepThread(long startTime, long endTime) {
        //根据读文件和解码耗时，计算需要休眠的时间
        long time = PRE_FRAME_TIME - (endTime - startTime);
        if (time > 0) {
            try {
                Thread.sleep(time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
