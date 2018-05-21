package com.cifs.smbutils;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.List;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;

public class SmbFileSysAsync extends AsyncTask<String, String, SmbFile> {

    private GetSMBFileAsyncInterface mGetSMBFileAsyncInterface;
    private String url;
    private List<SmbFile> smbFileList;
    private boolean isFile;
    private String parentFileRoute;
    private String fileRoute;
    private boolean smbIsFile;
    private String superlist;

    public SmbFileSysAsync(boolean isFile, String parentFileRoute, String fileRoute, GetSMBFileAsyncInterface getSMBFileAsyncInterface) {
        super();
        this.isFile = isFile;
        this.fileRoute = fileRoute;
        this.parentFileRoute = parentFileRoute;
        this.mGetSMBFileAsyncInterface = getSMBFileAsyncInterface;

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected SmbFile doInBackground(String... strings) {
        url = parentFileRoute == null ? "smb://" + strings[0] + "/" : parentFileRoute + fileRoute;
        superlist = parentFileRoute == null ? "smb://" + strings[0] + "/" : parentFileRoute;
        if (SmbLogin.smbLogin(strings[0], SmbLogin.getNtlmPasswordAuthentication(strings[0], null, null))) {
            onProgressUpdate("SMB登录成功");
            try {
                SmbFile smbFile = new SmbFile(url);
                smbFileList = Arrays.asList(smbFile.listFiles());
                smbIsFile = smbFile.isFile();
                return smbFile;
            } catch (MalformedURLException e) {
                e.printStackTrace();
                onProgressUpdate("SMB文件读取失败1");
            } catch (SmbException e) {
                e.printStackTrace();
                onProgressUpdate("SMB文件读取失败2");
            }
        } else {
            onProgressUpdate("SMB登录失败");
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        if (TextUtils.isEmpty(values[0])) {
            return;
        }
        Log.e("SMB异常信息：", values[0]);
    }

    @Override
    protected void onPostExecute(SmbFile smbFile) {
        super.onPostExecute(smbFile);
        if (smbFile == null) {
            return;
        }
        if (smbFileList == null) {
            return;
        }
        mGetSMBFileAsyncInterface.getReturnSMBFileInfo(smbIsFile, parentFileRoute, superlist, smbFile, smbFileList);
    }

    public interface GetSMBFileAsyncInterface {

        void getReturnSMBFileInfo(boolean isFile, String url, String superlist, SmbFile smbFile, List<SmbFile> smbFileList);
    }
}
