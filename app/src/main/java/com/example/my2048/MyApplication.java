package com.example.my2048;

import android.app.Application;
import android.os.Build;

import com.example.my2048.util.database.DBManager;
import com.example.my2048.util.database.GameData;

public class MyApplication extends Application {
    public static MyApplication application=null;
    public static GameData []gameData=new GameData[5];
    public static boolean setting_mute=false;
    @Override
    public void onCreate() {
        super.onCreate();
        application=this;
        for(int i=0;i<5;i++)
            load(i);
        new Thread(new Runnable() {
            @Override
            public void run() {
                setting_mute=DBManager.getMute(MyApplication.this);
            }
        }).start();
    }
    private void load(final int flag){
        new Thread(new Runnable() {
            @Override
            public void run() {
                gameData[flag]=DBManager.getData(MyApplication.this,flag+4);
            }
        }).start();
    }
}
