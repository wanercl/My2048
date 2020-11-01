package com.example.my2048;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.my2048.util.database.DBManager;
import com.example.my2048.util.database.GameData;
import com.example.my2048.util.view.GameLayout;
import com.example.my2048.util.view.HideStatuBar;
import com.example.my2048.util.view.PopWindow_GameOver;
import com.example.my2048.util.view.PopWindow_Gamepause;

import java.util.ArrayList;
import java.util.List;

import static com.example.my2048.MyApplication.*;

public class GameActivity extends AppCompatActivity implements View.OnClickListener,GameLayout.GameListener{
    public static float scaledDensity;
    public static float density;
    private GameLayout layout;
    private int index=0;
    private TextView tvScore;
    private TextView tvcombo;
    private TextView tvmaxcombo;
    private TextView tvmaxscore;
    private PopWindow_Gamepause pop_pause;
    private boolean IsShowPop=false;
    @SuppressLint("HandlerLeak")
    private Handler handler=new Handler(){
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if(gameData[index]!=null){
                    //数据恢复（如果有）
                    layout.RestoreWithGameData(gameData[index]);
                }
                else {
                    switch (index){
                        case 0:
                            layout.firstplay(4,4);
                            break;
                        case 1:
                            layout.firstplay(5,5);
                            break;
                        case 2:
                            layout.firstplay(6,6);
                            break;
                        case 3:
                            layout.firstplay(7,7);
                            break;
                        case 4:
                            layout.firstplay(8,8);
                            break;
                    }
                }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //初始化界面
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        HideStatuBar.hideStatuBar(this);
        index=getIntent().getIntExtra("flag",0);
        DisplayMetrics metrics=new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        scaledDensity=metrics.scaledDensity;
        density=metrics.density;
        layout=findViewById(R.id.mg);
        layout.setMgameListener(this);
        tvScore =findViewById(R.id.tv_main_score);
        tvcombo =findViewById(R.id.tv_main_combo);
        tvmaxcombo=findViewById(R.id.tv_main_maxcombo);
        tvmaxscore=findViewById(R.id.tv_main_maxscore);
        if(MyApplication.setting_mute){
            ((TextView)findViewById(R.id.tv_game_mute)).setText(R.string.gamebar_muteon);
        }
        findViewById(R.id.tv_game_mute).setOnClickListener(this);
        findViewById(R.id.tv_game_pause).setOnClickListener(this);
        pop_pause=new PopWindow_Gamepause(this, new PopWindow_Gamepause.popLisetener() {
            @Override
            public void onrestart() {
                //重新开始
                layout.restart();
            }

            @Override
            public void oncontinue() {
                //继续游戏
            }

            @Override
            public void onexit() {
                //退出游戏
                finish();
            }

            @Override
            public void ondismiss() {
                //弹窗消失
                IsShowPop=false;
            }

            @Override
            public void onback() {
                //回主菜单
                Intent intent=new Intent(GameActivity.this,MainMenuActivity.class);
                startActivity(intent);
                finish();
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //等待0.1S以确保游戏布局加载完成再初始化场景
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                handler.sendEmptyMessage(0);
            }
        }).start();
        Spinner spinnertext = findViewById(R.id.sp_game);
        List<String >list=new ArrayList<>();
        //下拉菜单
        list.add("4×4");
        list.add("5×5");
        list.add("6×6");
        list.add("7×7");
        list.add("8×8");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.sp_mapselector, list);
        adapter.setDropDownViewResource(R.layout.spi_mapselector);
        spinnertext.setAdapter(adapter);
        spinnertext.setSelection(index);
        spinnertext.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> argO, View argl, int arg2, long arg3) {
                // TODO Auto-generated method stub
                argO.setVisibility(View.VISIBLE);
                if(index!=arg2){
                    Log.i("index",""+index);
                    index=arg2;
                    handler.sendEmptyMessage(0);
                }
            }
            public void onNothingSelected(AdapterView<?> argO) {
                // TODO Auto-generated method stub
                argO.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onBackPressed() {
        //返回键事件
        if(pop_pause!=null&&!IsShowPop)
            pop_pause.show();
    }

    @Override
    protected void onPause() {
        //游戏暂停事件
        super.onPause();
        //new PopWindow_Gamepause(this).show();
        if(pop_pause!=null&&!IsShowPop)
            pop_pause.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View v) {
        //构件点击事件
        switch (v.getId()){
            case R.id.tv_game_mute:
                if(MyApplication.setting_mute){
                    new Thread(new Runnable() {
                        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                        @Override
                        public void run() {
                            DBManager.updateMute(GameActivity.this,false);
                        }
                    }).start();
                    ((TextView)v).setText(R.string.gamebar_muteoff);
                }
                else {
                    new Thread(new Runnable() {
                        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                        @Override
                        public void run() {
                            DBManager.updateMute(GameActivity.this,true);
                        }
                    }).start();
                    ((TextView)v).setText(R.string.gamebar_muteon);
                }
                MyApplication.setting_mute=!MyApplication.setting_mute;
                break;
            case R.id.tv_game_pause:
                if(pop_pause!=null&&!IsShowPop)
                pop_pause.show();
                break;
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onScoreChange(int score) {
        //分数改变事件
        tvScore.setText(""+score);
        AnimationSet animation= (AnimationSet) AnimationUtils.loadAnimation(this,R.anim.anim_scorechange);
        tvScore.startAnimation(animation);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onComboChange(int combo) {
        //连击改变事件
        tvcombo.setText(""+combo);
        AnimationSet animation= (AnimationSet) AnimationUtils.loadAnimation(this,R.anim.anim_scorechange);
        tvcombo.startAnimation(animation);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onMaxComboChange(int combo) {
        //最大连击改变事件
        tvmaxcombo.setText(""+combo);
        AnimationSet animation= (AnimationSet) AnimationUtils.loadAnimation(this,R.anim.anim_scorechange);
        tvmaxcombo.startAnimation(animation);
    }

    @Override
    public void onGameOver(PopWindow_GameOver.popdata data) {
        //游戏结束事件
        new PopWindow_GameOver(this, data, new PopWindow_GameOver.popListener() {
            @Override
            public void onrestart() {
                layout.restart();
            }

            @Override
            public void ondismiss() {
                IsShowPop=false;
            }
        }).show();
        IsShowPop=true;
    }

    @Override
    public void onDataChange() {
        //游戏数据变更
        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void run() {
                DBManager.updateData(GameActivity.this,layout.getGameData());
            }
        }).start();
        GameData data=layout.getGameData();
        gameData[data.column-4]=data;
    }
    @SuppressLint("SetTextI18n")
    @Override
    public void onMaxScoreChange(int maxscore) {
        //历史记录刷新
        tvmaxscore.setText("历史最高："+maxscore);
        AnimationSet animation= (AnimationSet) AnimationUtils.loadAnimation(this,R.anim.anim_scorechange);
        tvmaxscore.startAnimation(animation);
    }

    @Override
    public void onRestartGame() {
        //重新开始游戏
        IsShowPop=false;
    }
}