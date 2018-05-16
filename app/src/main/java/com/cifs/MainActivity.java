package com.cifs;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.File;
import java.net.UnknownHostException;
import java.util.List;

import jcifs.Config;
import jcifs.UniAddress;

public class MainActivity extends AppCompatActivity {


    private String url = "192.168.1.59";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.e("222", "1111111");

        new Thread() {
            @Override
            public void run() {
                super.run();
                Config.registerSmbURLHandler();

                UniAddress uniAddress = new UniAddress(url);


              /*  List<String> fileNames = Smb.getFileNamesFromSmb(url);

                Log.e("dd", fileNames.toString());

                for (String fileName : fileNames) {
                    File file = Smb.readFromSmb("smb://Administrator:zwt1314521zw@192.168.6.138/share/" + fileName, "E://138");
                }*/
            }
        }.start();


    }
}
