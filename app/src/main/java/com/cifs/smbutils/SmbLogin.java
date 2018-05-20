package com.cifs.smbutils;

import java.net.UnknownHostException;

import jcifs.UniAddress;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbSession;

public class SmbLogin {

    public static NtlmPasswordAuthentication getNtlmPasswordAuthentication(String ip, String userName, String password) {

        return new NtlmPasswordAuthentication(ip, userName, password);
    }

    /**
     * smb服务器登录
     *
     * @param ip smb 服务器ip
     */
    public static boolean smbLogin(String ip, NtlmPasswordAuthentication ntlmPasswordAuthentication) {
        try {
            UniAddress ua = UniAddress.getByName(ip);
            SmbSession.logon(ua, ntlmPasswordAuthentication);//验证是否能够成功登陆
            return true;
        } catch (UnknownHostException e) {
            e.printStackTrace();
            //找不到主机异常
            return false;
        } catch (SmbException e) {
            e.printStackTrace();
            //登录失败
            return false;
        }
    }

}
