package com.cifs;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;

import com.cifs.adapter.SmbFileListAdapter;
import com.cifs.smbutils.Config;
import com.cifs.smbutils.SmbFileModel;
import com.cifs.smbutils.SmbFileSysAsync;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;

public class MainActivity extends AppCompatActivity implements SmbFileSysAsync.GetSMBFileAsyncInterface, SmbFileListAdapter.OnItemClickListener {


    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.listview)
    ListView listview;
    private SmbFileModel mSmbFileModel;
    private ArrayList<SmbFileModel> smbFileModelArrayList;
    private SmbFileListAdapter smbFileListAdapter;

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
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (mSmbFileModel != null) {
                    toolbar.setTitle(mSmbFileModel.getUpperStrataRoute() == null ? "smb://" + Config.SMB_IP + "/" : mSmbFileModel.getUpperStrataRoute());
                    new SmbFileSysAsync(mSmbFileModel.isFile(), mSmbFileModel.getUpperStrataRoute(), "", this).execute(Config.SMB_IP);
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void getReturnSMBFileInfo(boolean isFile, String url, SmbFile smbFile, List<SmbFile> smbFileList) {

        if (smbFileModelArrayList == null) {
            smbFileModelArrayList = new ArrayList<>();
        } else {
            smbFileModelArrayList.clear();
        }
        Log.e(">>>>", smbFileList.size()+"");
        for (int i = 0; i < smbFileList.size(); i++) {
            try {
                SmbFile file = smbFileList.get(i);
                SmbFileModel smbFileModel = new SmbFileModel();
                smbFileModel.setFile(file.isFile());
                smbFileModel.setFileName(file.getName());
                smbFileModel.setUpperStrataRoute(file.getParent());
                smbFileModel.setSmbFile(smbFile);
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
                SmbFileMusicPlay fileMusicPlay = new SmbFileMusicPlay(this);
                fileMusicPlay.playFileStreamMusic(musicUrl);
            }
        } else {
            new SmbFileSysAsync(mSmbFileModel.isFile(), mSmbFileModel.getUpperStrataRoute(), mSmbFileModel.getFileName(), this).execute(Config.SMB_IP);
        }
        toolbar.setTitle(mSmbFileModel.getUpperStrataRoute() == null ? "smb://" + Config.SMB_IP + "/" : mSmbFileModel.getUpperStrataRoute() + mSmbFileModel.getFileName());
    }
}
