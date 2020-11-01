package com.example.my2048;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.my2048.util.database.DBManager;
import com.example.my2048.util.view.HideStatuBar;

public class MainMenuActivity extends AppCompatActivity implements View.OnClickListener {
    //主菜单
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainmenu);
        HideStatuBar.hideStatuBar(this);
        //菜单项数据
        if(MyApplication.gameData[0]!=null){
            ((Button)findViewById(R.id.btn_login_4)).setText(R.string.contgame_4);
        }
        if(MyApplication.gameData[1]!=null){
            ((Button)findViewById(R.id.btn_login_5)).setText(R.string.contgame_5);
        }
        if(MyApplication.gameData[2]!=null){
            ((Button)findViewById(R.id.btn_login_6)).setText(R.string.contgame_6);
        }
        if(MyApplication.gameData[3]!=null){
            ((Button)findViewById(R.id.btn_login_7)).setText(R.string.contgame_7);
        }
        if(MyApplication.gameData[4]!=null){
            ((Button)findViewById(R.id.btn_login_8)).setText(R.string.contgame_8);
        }
        if(MyApplication.setting_mute){
            ((Button)findViewById(R.id.btn_login_gameset)).setText(R.string.muteon);
        }
        findViewById(R.id.btn_login_4).setOnClickListener(this);
        findViewById(R.id.btn_login_5).setOnClickListener(this);
        findViewById(R.id.btn_login_6).setOnClickListener(this);
        findViewById(R.id.btn_login_7).setOnClickListener(this);
        findViewById(R.id.btn_login_8).setOnClickListener(this);
        findViewById(R.id.btn_login_gameset).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_login_4:
                startGame(0);
                break;
            case R.id.btn_login_5:
                startGame(1);
                break;
            case R.id.btn_login_6:
                startGame(2);
                break;
            case R.id.btn_login_7:
                startGame(3);
                break;
            case R.id.btn_login_8:
                startGame(4);
                break;
            case R.id.btn_login_gameset:
                //音效设置
                if(MyApplication.setting_mute){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            DBManager.updateMute(MainMenuActivity.this,false);
                        }
                    }).start();
                    ((Button)v).setText(R.string.muteoff);
                }
                else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            DBManager.updateMute(MainMenuActivity.this,true);
                        }
                    }).start();
                    ((Button)v).setText(R.string.muteon);
                }
                MyApplication.setting_mute=!MyApplication.setting_mute;
                break;
        }
    }
    private void startGame(int flag){
        //跳转至游戏界面（flag标识棋盘大小）
        Intent intent=new Intent(this, GameActivity.class);
        intent.putExtra("flag",flag);
        startActivity(intent);
        finish();
    }
}
