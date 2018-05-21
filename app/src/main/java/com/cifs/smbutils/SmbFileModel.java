package com.cifs.smbutils;

import jcifs.smb.SmbFile;

public class SmbFileModel {

    private boolean isFile;
    private String fileName;
    private String upperStrataRoute;
    private SmbFile smbFile;
    private String superlist;
    @Override
    public String toString() {
        return "SmbFileModel{" +
                "isFile=" + isFile +
                ", fileName='" + fileName + '\'' +
                ", upperStrataRoute='" + upperStrataRoute + '\'' +
                ", smbFile=" + smbFile +
                ", superlist='" + superlist + '\'' +
                '}';
    }



    public String getSuperlist() {
        return superlist;
    }

    public void setSuperlist(String superlist) {
        this.superlist = superlist;
    }

    public SmbFile getSmbFile() {
        return smbFile;
    }

    public void setSmbFile(SmbFile smbFile) {
        this.smbFile = smbFile;
    }

    public String getUpperStrataRoute() {
        return upperStrataRoute;
    }

    public void setUpperStrataRoute(String upperStrataRoute) {
        this.upperStrataRoute = upperStrataRoute;
    }

    public boolean isFile() {
        return isFile;
    }

    public void setFile(boolean file) {
        isFile = file;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

}
