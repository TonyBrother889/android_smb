package com.cifs;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.cifs.cifs.SmbFileName;

import org.apache.commons.vfs2.FileType;

import java.net.UnknownHostException;

import jcifs.Config;
import jcifs.UniAddress;

import static jcifs.Config.registerSmbURLHandler;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        registerSmbURLHandler();


        new Thread() {

            @Override
            public void run() {
                super.run();
                try {
                    UniAddress uniAddress = UniAddress.getByName("192.168.1.59", true);
                    String hostName = uniAddress.getHostName();
                    Log.e("hostName",hostName);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
            }
        };


    }
}
