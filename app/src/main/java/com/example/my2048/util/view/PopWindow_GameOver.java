package com.example.my2048.util.view;

import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.my2048.GameActivity;
import com.example.my2048.R;


public class PopWindow_GameOver extends PopupWindow implements View.OnClickListener {
    //自定义弹窗类(游戏结束)
    private Activity activity;

    @Override
    public void onClick(View v) {
        dismiss();
    }
    popListener listener=null;
    public static interface popListener{
        void onrestart();
        void ondismiss();
    }
    public static class popdata{
        int flag;
        int score;
        int maxscore;
        int maxcombo;
        int maxblock;
        public popdata(int flag, int score,int maxscore, int maxcombo, int maxblock){
            this.flag=flag;
            this.score=score;
            this.maxscore=maxscore;
            this.maxcombo=maxcombo;
            this.maxblock=maxblock;
        }
    }
    public PopWindow_GameOver(Activity activity,popdata data,popListener listener){
        this.activity=activity;
        this.listener=listener;
        initView(data);
    }
    public void show() {
        this.showAtLocation(activity.getWindow().getDecorView(), Gravity.CENTER, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        WindowManager.LayoutParams lp=activity.getWindow().getAttributes();
        lp.alpha=0.5f;
        activity.getWindow().setAttributes(lp);
    }

    private void initView(popdata data) {
        //初始化数据
        setContentView(LayoutInflater.from(activity).inflate(R.layout.popup_gameover,null));
        View view=getContentView();
        ((TextView)view.findViewById(R.id.tv_pop_mapsize)).setText("场地大小："+data.flag+"×"+data.flag);
        ((TextView)view.findViewById(R.id.tv_pop_score)).setText("本局分数："+data.score);
        ((TextView)view.findViewById(R.id.tv_pop_maxscore)).setText("历史最高："+data.maxscore);
        ((TextView)view.findViewById(R.id.tv_pop_maxcombo)).setText("最大连击："+data.maxcombo);
        ((TextView)view.findViewById(R.id.tv_pop_maxblock)).setText("最大合成："+data.maxblock);
        view.findViewById(R.id.tv_pop_restart).setOnClickListener(this);
        //设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        //设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
        this.setOutsideTouchable(false);
        this.setTouchable(true);
        this.setFocusable(false);
        setAnimationStyle(R.style.popanim);
    }

    @Override
    public void dismiss() {
        //使弹窗消失
        super.dismiss();
        if(listener!=null){
            listener.ondismiss();
            listener.onrestart();
        }
        WindowManager.LayoutParams lp=activity.getWindow().getAttributes();
        lp.alpha=1.0f;
        activity.getWindow().setAttributes(lp);
    }
}
