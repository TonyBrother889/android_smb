package com.cifs;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

import com.cifs.music.AACDecoderUtil;
import com.cifs.smbutils.smbstreamer.Streamer;

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

public class SmbFileMusicPlay2 {

    private final Streamer streamer;
    private final MediaPlayer mediaPlayer;

    public SmbFileMusicPlay2(Context context) {
        super();
        streamer = Streamer.getInstance();
        mediaPlayer = new MediaPlayer();
    }

    public void playFileStreamMusic(final String smbUrl) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //  SmbFile smbFile = new SmbFile(smbFileUrl);
                    //  SmbFileInputStream smbFileInputStream = new SmbFileInputStream(smbFile);
                    //  InputStream bis = new DataInputStream(smbFileInputStream);
                    SmbFile file = new SmbFile(smbUrl);
                    //the second argument can be a list of subtitle files
                    streamer.setStreamSrc(file, null);
                    mediaPlayer.setDataSource(Streamer.URL + Uri.fromFile(new File(Uri.parse(smbUrl).getPath())).getEncodedPath());
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }
}
