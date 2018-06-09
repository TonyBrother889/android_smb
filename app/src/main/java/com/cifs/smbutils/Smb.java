package com.cifs.smbutils;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;

/**
 * @author 作者：张祥 on 2018/5/16 0016.
 *         邮箱：847874028@qq.com
 *         版本：v1.0
 *         功能：
 */

public class Smb {

    public static List<String> getFileNamesFromSmb(String smbMachine){
        SmbFile file;
        SmbFile[] files = null;
        try {
            file = new SmbFile(smbMachine);
            files = file.listFiles();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (SmbException e) {
            e.printStackTrace();
        }
        List<String> fileNames = new ArrayList<String>();

        for (SmbFile smbFile : files) {
            if(smbFile.getName().indexOf(".EXP")!=-1){
                fileNames.add(smbFile.getName());
            }
        }
        return fileNames;
    }
    /**
     * 从smbMachine读取文件并存储到localpath指定的路径
     *
     * @param smbMachine
     *            共享机器的文件,如smb://xxx:xxx@10.108.23.112/myDocument/测试文本.txt,xxx:xxx是共享机器的用户名密码
     * @param localpath
     *            本地路径
     * @return
     */
    public static File readFromSmb(String smbMachine, String localpath){
        File localfile=null;
        InputStream bis=null;
        OutputStream bos=null;
        List<File> files = new ArrayList<>();
        try {
            SmbFile rmifile = new SmbFile(smbMachine);
            String filename=rmifile.getName();
            bis=new BufferedInputStream(new SmbFileInputStream(rmifile));
            localfile=new File(localpath+File.separator+filename);
            bos=new BufferedOutputStream(new FileOutputStream(localfile));
            int length=rmifile.getContentLength();
            byte[] buffer=new byte[length];
            bis.read(buffer);
            bos.write(buffer);
            try {
                bos.close();
                bis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            files.add(localfile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return localfile;
    }
    public static boolean removeFile(File file) {
        return file.delete();
    }

}
