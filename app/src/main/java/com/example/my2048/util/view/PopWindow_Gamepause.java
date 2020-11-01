package com.example.my2048.util.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.example.my2048.R;


public class PopWindow_Gamepause extends PopupWindow implements View.OnClickListener {
    //自定义弹窗类（游戏暂停）
    private Activity activity;
    popLisetener lisetener;
    public interface popLisetener{
        void onrestart();
        void oncontinue();
        void onexit();
        void ondismiss();
        void onback();
    }
    @Override
    public void onClick(View v) {
        if(lisetener!=null){
            switch (v.getId()){
                case R.id.btn_pop_continue:
                    lisetener.oncontinue();
                    break;
                case R.id.btn_pop_restart:
                    lisetener.onrestart();
                    break;
                case R.id.btn_pop_backtomenu:
                    lisetener.onback();
                    break;
                case R.id.btn_pop_exit:
                    lisetener.onexit();
                    break;
            }
        }
        dismiss();
    }
    public PopWindow_Gamepause(Activity activity,popLisetener lisetener){
        this.activity=activity;
        this.lisetener=lisetener;
        initView();
    }
    public void show() {
        this.showAtLocation(activity.getWindow().getDecorView(), Gravity.CENTER, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        WindowManager.LayoutParams lp=activity.getWindow().getAttributes();
        lp.alpha=0.5f;
        activity.getWindow().setAttributes(lp);
    }

    @SuppressLint("InflateParams")
    private void initView() {
        setContentView(LayoutInflater.from(activity).inflate(R.layout.popup_gamepause,null));
        View view=getContentView();
        view.findViewById(R.id.btn_pop_continue).setOnClickListener(this);
        view.findViewById(R.id.btn_pop_restart).setOnClickListener(this);
        view.findViewById(R.id.btn_pop_exit).setOnClickListener(this);
        view.findViewById(R.id.btn_pop_backtomenu).setOnClickListener(this);
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
        if(lisetener!=null)
            lisetener.ondismiss();
        WindowManager.LayoutParams lp=activity.getWindow().getAttributes();
        lp.alpha=1.0f;
        activity.getWindow().setAttributes(lp);
    }
}
