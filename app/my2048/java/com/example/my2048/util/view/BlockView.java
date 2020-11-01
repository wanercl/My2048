package com.example.my2048.util.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import androidx.core.content.ContextCompat;

import com.example.my2048.R;

import java.io.Serializable;

public class BlockView extends androidx.appcompat.widget.AppCompatTextView implements Serializable {
    private static final long serialVersionUID = 654321L;
    private int value;
    private int index;
    public boolean IsNew;
    private boolean IsFristDraw=true;
    public boolean IsReadyToUpdate=false;
    public BlockView(Context context,int value) {
        super(context);
        setGravity(Gravity.CENTER);
        setLineSpacing(0,1);
        setValue(value);
        IsNew=true;
    }

    public int getValue() {
        return value;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(IsFristDraw){
            IsFristDraw=false;
            IsReadyToUpdate=true;
            setValue(value);
        }
    }

    @SuppressLint("NewApi")
    public void setValue(int value) {
        this.value = value;
        if(value!=0){
            setText(String.valueOf(value));
            switch (value){
                case 2:
                    setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_block_2));
                    break;
                case 4:
                    setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_block_4));
                    break;
                case 8:
                    setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_block_8));
                    break;
                case 16:
                    setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_block_16));
                    break;
                case 32:
                    setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_block_32));
                    break;
                case 64:
                    setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_block_64));
                    break;
                case 128:
                    setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_block_128));
                    break;
                default:
                    setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_block_default));
                    break;
            }
            if (value>4){
                setTextColor(Color.WHITE);
            }
            int l=getText().toString().length();
            float width=getWidth()*0.7f/l;
            float height=width*2;
            if(height>0.7f*getHeight()){
                height=0.7f*getHeight();
                Log.i("已重置","--");
            }
            float ll=(float)Math.sqrt((0.8*width/l)*(0.8*width/l)+(0.8*height/l)*(0.8*height/l));
            Log.i("l"+l,"size"+ll);
            setTextSize(TypedValue.COMPLEX_UNIT_PX,height);
            if(IsReadyToUpdate){
                IsReadyToUpdate=false;
                showScaleAni();
            }
        }
        else {
            IsReadyToUpdate=false;
            setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_block_none));
        }
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
    private void showScaleAni(){
        AnimationSet animation= (AnimationSet) AnimationUtils.loadAnimation(getContext(),R.anim.anim_addablock);
        this.startAnimation(animation);
    }
    public void update(){
        if(IsReadyToUpdate){
            showScaleAni();
            IsReadyToUpdate=false;
        }
    }
}
