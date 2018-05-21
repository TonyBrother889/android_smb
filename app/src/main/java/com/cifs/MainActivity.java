package com.cifs;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.MimeTypeMap;
import android.widget.ListView;

import com.cifs.adapter.SmbFileListAdapter;
import com.cifs.smbutils.Config;
import com.cifs.smbutils.SmbFileModel;
import com.cifs.smbutils.SmbFileSysAsync;
import com.cifs.smbutils.smbstreamer.Streamer;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;

public class MainActivity extends AppCompatActivity implements SmbFileSysAsync.GetSMBFileAsyncInterface, SmbFileListAdapter.OnItemClickListener {


    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.listview)
    ListView listview;
    private SmbFileModel mSmbFileModel;
    private ArrayList<SmbFileModel> smbFileModelArrayList;
    private SmbFileListAdapter smbFileListAdapter;

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        setSupportActionBar(toolbar);
        ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.setDisplayHomeAsUpEnabled(true);
        supportActionBar.setDisplayShowTitleEnabled(true);
        new SmbFileSysAsync(false, null, null, this).execute(Config.SMB_IP);

 /*       if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CODE_ASK_PERMISSON);
        } else {

        }
*/
    /*    new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    Log.e("111", "+++++");
                    MediaPlayer mediaPlayer = new MediaPlayer();
                    mediaPlayer.setDataSource("/sdcard/许嵩 - 不语.mp3");
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }.start();*/

    }


/*    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    SmbFileMusicPlay fileMusicPlay = new SmbFileMusicPlay(this);
                    Log.e("111", ">>>");
                    fileMusicPlay.playFileStreamMusic(new File(Environment.getExternalStorageDirectory(), "许嵩 - 不语.mp3"));
                } else {
                    // Permission Denied
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions,
                        grantResults);
        }
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (mSmbFileModel != null) {
                    toolbar.setTitle(mSmbFileModel.getSuperlist());
                    new SmbFileSysAsync(mSmbFileModel.isFile(), mSmbFileModel.getSuperlist(), "", this).execute(Config.SMB_IP);
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void getReturnSMBFileInfo(boolean isFile, String url, String superlist, SmbFile smbFile, List<SmbFile> smbFileList) {

        if (smbFileModelArrayList == null) {
            smbFileModelArrayList = new ArrayList<>();
        } else {
            smbFileModelArrayList.clear();
        }
        Log.e(">>>>", smbFileList.size() + "");
        for (int i = 0; i < smbFileList.size(); i++) {
            try {
                SmbFile file = smbFileList.get(i);
                SmbFileModel smbFileModel = new SmbFileModel();
                smbFileModel.setFile(file.isFile());
                smbFileModel.setFileName(file.getName());
                smbFileModel.setUpperStrataRoute(file.getParent());
                smbFileModel.setSmbFile(smbFile);
                smbFileModel.setSuperlist(superlist);
                smbFileModelArrayList.add(smbFileModel);
            } catch (SmbException e) {
                e.printStackTrace();
            }
        }
        if (smbFileListAdapter == null) {
            smbFileListAdapter = new SmbFileListAdapter(this, smbFileModelArrayList, this);
            listview.setAdapter(smbFileListAdapter);
        } else {
            smbFileListAdapter.notifyDataSetChanged();
        }
    }


    @Override
    public void onFileItemClick(int position, SmbFileModel smbFileModel) {
        mSmbFileModel = smbFileModel;
        if (mSmbFileModel.isFile()) {
            Log.e("sss", "是文件");
            Log.e("sss", mSmbFileModel.getFileName());
            Log.e("sss", mSmbFileModel.getSmbFile().getParent());
            if (mSmbFileModel.getFileName().endsWith(".mp3")) {
                String musicUrl = mSmbFileModel.getUpperStrataRoute() + mSmbFileModel.getFileName();
                Log.e("mm", musicUrl);
                SmbFileMusicPlay2 fileMusicPlay = new SmbFileMusicPlay2(this);
                fileMusicPlay.playFileStreamMusic(musicUrl);
            }else if(mSmbFileModel.getFileName().endsWith(".mp4")||mSmbFileModel.getFileName().endsWith(".vdat")){
                try {
                    String videoUrl = mSmbFileModel.getUpperStrataRoute() + mSmbFileModel.getFileName();
                    Streamer streamer = Streamer.getInstance();

                    streamer.setStreamSrc(new SmbFile(videoUrl), null);

                    String url = Streamer.URL + Uri.fromFile(new File(Uri.parse(videoUrl).getPath())).getEncodedPath();

                    Intent intent=new Intent(this, VideoPlayerActivity.class);
                    intent.putExtra(Constants.INTENT_MOVE_URL, url);
                    startActivity(intent);

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }


            }
        } else {
            new SmbFileSysAsync(mSmbFileModel.isFile(), mSmbFileModel.getUpperStrataRoute(), mSmbFileModel.getFileName(), this).execute(Config.SMB_IP);
        }
        toolbar.setTitle(smbFileModel.getSuperlist());
    }
}
