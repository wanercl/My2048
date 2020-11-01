package com.example.my2048.util.view;

import android.animation.Animator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.example.my2048.MyApplication;
import com.example.my2048.R;
import com.example.my2048.util.database.GameData;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameLayout extends RelativeLayout implements View.OnTouchListener {
    //游戏场景类
    private int width;
    private int height;
    //场景宽高
    private int column;
    private int line;
    //行列
    private int spacing;
    //间隙
    private boolean IsFirstDraw=true;
    //是否为第一次绘制
    private List<BlockView> mviews;
    //场上的view
    private float lx;
    private float ly;
    //触摸事件上一次的y;
    private boolean canleft=true;
    //是否可以向左
    private boolean canright=true;
    //是否可以向右
    private boolean canup=true;
    //是否可以向上
    private boolean candown=true;
    //是否可以向下
    private final int MoveUp=1000;
    //向上标识
    private final int MoveDown=1001;
    //向下标识
    private final int MoveLeft=1002;
    //向左标识
    private final int MoveRight=1003;
    //向右标识
    private BlockView[][]Viewmap=new BlockView[8][8];
    //场景地图
    private List<BlockView> preparetoremoveViews=new ArrayList<>();
    //即将删除的View
    private boolean isMoving=false;
    //是否正在移动构件
    private boolean isAddedView=false;
    private int combo=0;
    private int isComboed=0;
    private int score=0;
    private int lscore=0;
    private int maxscore=0;
    private int maxcombo=0;
    private int lcombo;
    private int maxblock=2;
    private GameListener mgameListener=null;

    public void setMgameListener(GameListener mgameListener) {
        this.mgameListener = mgameListener;
    }

    public interface GameListener{
        void onScoreChange(int score);
        void onComboChange(int combo);
        void onMaxComboChange(int combo);
        void onGameOver(PopWindow_GameOver.popdata data);
        void onDataChange();
        void onMaxScoreChange(int maxscore);
        void onRestartGame();
    }
    //重写构造方法
    public GameLayout(Context context) {
        super(context);
        create();
    }

    public GameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        create();
    }

    public GameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        create();
    }

    public void create() {
        //初始数据
        mviews=new ArrayList<>();
        setOnTouchListener(this);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        //第一次绘制的时候需要获取宽高数据
        super.onDraw(canvas);
        if(IsFirstDraw){
            IsFirstDraw=false;
            width=getWidth();
            height=width;
            //默认使宽高相同
            initial(column,line);
            RelativeLayout.LayoutParams layoutParams= (RelativeLayout.LayoutParams) getLayoutParams();
            layoutParams.height=width;
            setLayoutParams(layoutParams);
            for(BlockView view:mviews){
                //根据view信息确定View位置
                resetViewLayoutParam(view);
            }
        }
    }
    public void initial(int column, int line){
        //使用行列数初始化界面
        this.column=column;
        this.line=line;
        this.spacing=width/(10)/(column+1);
        //重置空隙宽度
        removeAllViews();
        mviews.clear();
        //清除View数据
        preparetoremoveViews.clear();
        for(int i=0;i<column;i++){
            for(int j=0;j<line;j++){
                //绘制背景方块
                addView(j*column+i,0,true);
            }
        }
        for (int i=0;i<8;i++){
            for(int j=0;j<8;j++){
                //清空已有数据
                Viewmap[i][j]=null;
            }
        }
        isMoving=false;
        //重置移动状态
    }
    public void add(){
        //随即添加2、4
        List<Integer> emptyloc=new ArrayList<>();
        //标记空白位置，使用随机下标获得空白位置
        for(int i=0;i<column;i++){
            for(int j=0;j<line;j++){
                if(Viewmap[j][i]==null)
                    emptyloc.add(j*column+i);
            }
        }
        int l=emptyloc.size();
        Random random=new Random();
        if(l<=0)return;
        int a=random.nextInt(l);
        int n=emptyloc.get(a);
        emptyloc.remove(a);
        int m=random.nextInt(100);
        if(m>=20)
        addView(n,2,false);
        else addView(n,4,false);
    }
    public void addView(int location,int value,boolean flag) {
        //在指定位置添加方块
        BlockView child;
        child=new BlockView(getContext(),value);
        if(!flag){
            mviews.add(child);
            Viewmap[location/column][location%column]=child;
        }
        addView(child);
        child.setIndex(location);
        //需要设定View位置
        if(!IsFirstDraw){
            resetViewLayoutParam(child);
        }
    }
    public Point getCoordinate(int location){
        //根据位置获取View的坐标
        int x=(location%column)*(((width-spacing*(column+1))/column))+(location%column+1)*(spacing);
        int y=(location/line)*(((height-spacing*(line+1))/line))+(location/line+1)*(spacing);
        return new Point(x,y);
    }
    private void resetViewLayoutParam(BlockView view){
        RelativeLayout.LayoutParams layoutParams= (LayoutParams) view.getLayoutParams();
        layoutParams.width=(width-spacing*(column+1))/column;
        layoutParams.height=(height-spacing*(line+1))/line;
        view.setLayoutParams(layoutParams);
        Point point=getCoordinate(view.getIndex());
        int l=point.x;
        int t=point.y;
        if(!(view.getLeft()==l&&view.getTop()==t)&&!view.IsNew){
            //显示移动动画（属性动画），时间为0.12秒
            showTranslatAni(view,l,t,120);
        }
        if(view.IsNew){
            //如果该View是刚创建的，只需要让它呆在该出现的位置即可
            view.IsNew=false;
            layoutParams.leftMargin=point.x;
            layoutParams.topMargin=point.y;
            view.setLayoutParams(layoutParams);
        }
    }
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        //触摸事件
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                lx=event.getX();
                ly=event.getY();
                break;
            case MotionEvent.ACTION_UP:
                float dx=event.getX()-lx;
                float dy=event.getY()-ly;
                //判断滑动操作方向并执行操作
                if(Math.abs(dx)>Math.abs(dy)){
                    if(dx<0)
                        moveViews(MoveLeft);
                    else moveViews(MoveRight);
                }
                else {
                    if (dy<0)
                        moveViews(MoveUp);
                    else
                        moveViews(MoveDown);
                }
                break;
        }
        return true;
    }
    public void moveViews(int flag){
        //移动View（如果此时没在移动）
        if(!isMoving){
            isMoving=true;
            isAddedView=false;
            switch (flag){
                //根据情况确定方向
                case MoveUp:
                    if(!canup){
                        isMoving=false;
                        return;
                    }
                    moveUp();
                    return;
                case MoveDown:
                    if(!candown){
                        isMoving=false;
                        return;
                    }
                    moveDown();
                    return;
                case MoveLeft:
                    if(!canleft){
                        isMoving=false;
                        return;
                    }
                    moveLeft();
                    return;
                case MoveRight:
                    if(!canright){
                        isMoving=false;
                        return;
                    }
                    moveRight();
            }
        }
    }
    private void moveUp(){
        //上移
        for (int i=0;i<column;i++){
            int n=0;
            for(int j=0;j<line;j++){
                //纵向检测每一个View的目标位置和是否合并
                if(Viewmap[j][i]!=null){
                    if (Viewmap[n][i] == null) {
                        translate(j,i,n,i);
                    }
                    else if(Viewmap[j][i].getValue()==Viewmap[n][i].getValue()&&n!=j){
                        //合并相同相邻
                        conbine(j,i,n,i);
                        n++;
                    }
                    else if(Viewmap[j][i].getValue()!=Viewmap[n][i].getValue()){
                        n++;
                        if(n!=j){
                            translate(j,i,n,i);
                        }
                    }
                }
            }
        }
        fresh();
    }
    private void moveLeft(){
        //左移
        for(int j=0;j<line;j++){
            int n=0;
            for (int i=0;i<column;i++){
                if(Viewmap[j][i]!=null){
                    if (Viewmap[j][n] == null) {
                        translate(j,i,j,n);
                    }
                    else if(Viewmap[j][i].getValue()==Viewmap[j][n].getValue()&&n!=i){
                        conbine(j,i,j,n);
                        n++;
                    }
                    else if(Viewmap[j][i].getValue()!=Viewmap[j][n].getValue()){
                        n++;
                        if(n!=i){
                            translate(j,i,j,n);
                        }
                    }
                }
            }
        }
        fresh();
    }
    private void moveDown(){
        //下移
        for (int i=0;i<column;i++){
            int n=line-1;
            for(int j=line-1;j>=0;j--){
                if(Viewmap[j][i]!=null){
                    if (Viewmap[n][i] == null) {
                        translate(j,i,n,i);
                    }
                    else if(Viewmap[j][i].getValue()==Viewmap[n][i].getValue()&&n!=j){
                        conbine(j,i,n,i);
                        n--;
                    }
                    else if(Viewmap[j][i].getValue()!=Viewmap[n][i].getValue()){
                        n--;
                        if(n!=j){
                            translate(j,i,n,i);
                        }
                    }
                }
            }
        }
        fresh();
    }
    private void moveRight(){
        //右移
        for(int j=0;j<line;j++){
            int n=column-1;
            for (int i=column-1;i>=0;i--){
                if(Viewmap[j][i]!=null){
                    if (Viewmap[j][n] == null) {
                        translate(j,i,j,n);
                    }
                    else if(Viewmap[j][i].getValue()==Viewmap[j][n].getValue()&&n!=i){
                        conbine(j,i,j,n);
                        n--;
                    }
                    else if(Viewmap[j][i].getValue()!=Viewmap[j][n].getValue()){
                        n--;
                        if(n!=i){
                            translate(j,i,j,n);
                        }
                    }
                }
            }
        }
        fresh();
    }
    private void translate(int sl,int sc,int tl,int tc){
        Viewmap[tl][tc]=Viewmap[sl][sc];
        Viewmap[tl][tc].setIndex(tl*column+tc);
        Viewmap[sl][sc]=null;
    }
    private void conbine(int al,int ac,int bl,int bc){
        isComboed++;
        preparetoremoveViews.add(Viewmap[bl][bc]);
        translate(al,ac,bl,bc);
        score+=Viewmap[bl][bc].getValue();
        Viewmap[bl][bc].setValue(Viewmap[bl][bc].getValue()*2);
        Viewmap[bl][bc].IsReadyToUpdate=true;
    }
    private void fresh(){
        if(isComboed==0){
            combo=0;
        }else {
            combo++;
        }
        if(combo>maxcombo){
            maxcombo=combo;
            if(mgameListener!=null){
                mgameListener.onMaxComboChange(combo);
            }
        }
        if(lcombo!=combo){
            if(mgameListener!=null){
                mgameListener.onComboChange(combo);
            }
            lcombo=combo;
        }
        if(lscore!=score){
            if(mgameListener!=null){
                mgameListener.onScoreChange(score);
            }
            lscore=score;
        }
        if(maxscore<score){
            maxscore=score;
            if(mgameListener!=null){
                mgameListener.onMaxScoreChange(maxscore);
            }
        }
        isComboed=0;
        playsound();
        for(BlockView view:mviews){
            resetViewLayoutParam(view);
        }
    }
    private void check(){
        //检测
        //重置可移动状态
        canleft=false;
        canright=false;
        canup=false;
        candown=false;
        //检测横向
        for(int i=0;i<line;i++){
            int s=-1,e=-1;
            for(int j=0;j<column;j++){
                if(Viewmap[i][j]!=null){
                    s=j;break;
                }
            }
            if(s==-1)
                continue;
            for(int j=column-1;j>=0;j--){
                if(Viewmap[i][j]!=null){
                    e=j;break;
                }
            }
            for(int j=s;j<e;j++){
                if(Viewmap[i][j]==null){
                    canleft=true;
                    canright=true;
                }
                else if(Viewmap[i][j+1]!=null&&Viewmap[i][j].getValue()==Viewmap[i][j+1].getValue()){
                    canleft=true;
                    canright=true;
                }
            }
            if(s!=0)
                if(Viewmap[i][s-1]==null){
                    canleft=true;
                }
            if(e!=column-1)
                if(Viewmap[i][e+1]==null){
                    canright=true;
                }
            if(canleft&&canright)
                break;
        }
        //检测纵向
        for(int j=0;j<column;j++){
            int s=-1,e=-1;
            for(int i=0;i<line;i++){
                if(Viewmap[i][j]!=null){
                    s=i;
                    break;
                }
            }
            if(s==-1)
                continue;
            for(int i=line-1;i>=0;i--){
                if(Viewmap[i][j]!=null){
                    e=i;break;
                }
            }
            for(int i=s;i<e;i++){
                if(Viewmap[i][j]==null){
                    canup=true;
                    candown=true;
                }
                else if(Viewmap[i+1][j]!=null&&Viewmap[i][j].getValue()==Viewmap[i+1][j].getValue()){
                    canup=true;
                    candown=true;
                }
            }
            if(s!=0)
                if(Viewmap[s-1][j]==null){
                    canup=true;
                }
            if(e!=line-1)
                if(Viewmap[e+1][j]==null){
                    candown=true;
                }
            if(canup&&candown)
                break;
        }
        if(mgameListener!=null){
            //游戏数据改变
            mgameListener.onDataChange();
        }
        if(!(canup||candown||canleft||canright)){
            if(mgameListener!=null)
                //游戏结束（不能移动）
                mgameListener.onGameOver(new PopWindow_GameOver.popdata(
                        column,
                        score,
                        maxscore,
                        maxcombo,
                        maxblock
                ));
        }
    }
    static class TranslateValue implements TypeEvaluator<Point>{
        //平移数据
        @Override
        public Point evaluate(float fraction, Point startValue, Point endValue) {
            int x=startValue.x+(int)(fraction*(endValue.x-startValue.x));
            int y=startValue.y+(int)(fraction*(endValue.y-startValue.y));
            return new Point(x,y);
        }
    }
    private void showTranslatAni(final BlockView view, final int endx, int endy, int time){
        //显示平移属性动画
        ValueAnimator animator=ValueAnimator.ofObject(new TranslateValue(),new Point(view.getLeft(),view.getTop()),new Point(endx,endy));
        animator.setDuration(time);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                //平移
                Point point=(Point)animation.getAnimatedValue();
                RelativeLayout.LayoutParams layoutParams= (LayoutParams) view.getLayoutParams();
                layoutParams.leftMargin=point.x;
                layoutParams.topMargin=point.y;
                view.setLayoutParams(layoutParams);
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                //动画结束
                //让View自行检测是否变更（合并操作）
                view.update();
                if(view.getValue()>maxblock){
                    maxblock=view.getValue();
                }
                if(!isAddedView){
                    //是否已经完成了新方块添加
                    isAddedView=true;
                    isMoving=false;
                    for(BlockView v:preparetoremoveViews){
                        removeView(v);
                        mviews.remove(v);
                    }
                    add();
                    preparetoremoveViews.clear();
                    check();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        animator.start();
    }
    public void RestoreWithGameData(GameData data){
        //使用数据恢复游戏
        initial(data.column,data.line);
        Log.i("data"+data.column,""+data.line);
        Log.i("mydt"+column,""+line);
        for(int i=0;i<8;i++){
            for(int j=0;j<8;j++){
                if(data.map[i][j]!=0){
                    Log.i("++++"+i,"----"+j);
                    addView(i*column+j,data.map[i][j],false);
                    Log.i(""+i,""+j);
                }
            }
        }
        lscore=data.score;
        score=data.score;
        maxscore=data.maxscore;
        maxcombo=data.maxcombo;
        maxblock=data.maxblock;
        combo=0;
        lcombo=0;
        if(mgameListener!=null){
            //游戏事件更新
            mgameListener.onMaxComboChange(combo);
            mgameListener.onMaxComboChange(maxcombo);
            mgameListener.onScoreChange(score);
            mgameListener.onMaxScoreChange(maxscore);
        }
        check();
    }
    public GameData getGameData(){
        //获取场景上的游戏数据
        GameData data = new GameData();
        data.column=column;
        data.line=line;
        data.score=score;
        data.maxscore=maxscore;
        data.maxcombo=maxcombo;
        data.maxblock=maxblock;
        for(int i=0;i<8;i++){
            for(int j=0;j<8;j++){
                data.map[i][j]=Viewmap[i][j]==null?0:Viewmap[i][j].getValue();
            }
        }
        return data;
    }
    private void playsound(){
        //音效系统
        if(!MyApplication.setting_mute)
        new Thread(new Runnable() {
            @Override
            public void run() {
                MediaPlayer mMediaPlayer;
                switch (combo){
                    //连击音效
                    case 0:
                        mMediaPlayer=MediaPlayer.create(getContext(), R.raw.move);
                        break;
                    case 1:
                        mMediaPlayer=MediaPlayer.create(getContext(), R.raw.combo_sound1);
                        break;
                    case 2:
                        mMediaPlayer=MediaPlayer.create(getContext(), R.raw.combo_sound2);
                        break;
                    case 3:
                        mMediaPlayer=MediaPlayer.create(getContext(), R.raw.combo_sound3);
                        break;
                    case 4:
                        mMediaPlayer=MediaPlayer.create(getContext(), R.raw.combo_sound4);
                        break;
                    case 5:
                        mMediaPlayer=MediaPlayer.create(getContext(), R.raw.combo_sound5);
                        break;
                    case 6:
                        mMediaPlayer=MediaPlayer.create(getContext(), R.raw.combo_sound6);
                        break;
                    case 7:
                        mMediaPlayer=MediaPlayer.create(getContext(), R.raw.combo_sound7);
                        break;
                    case 8:
                        mMediaPlayer=MediaPlayer.create(getContext(), R.raw.combo_sound8);
                        break;
                    case 9:
                        mMediaPlayer=MediaPlayer.create(getContext(), R.raw.combo_sound9);
                        break;
                    case 10:
                        mMediaPlayer=MediaPlayer.create(getContext(), R.raw.combo_sound10);
                        break;
                    case 11:
                        mMediaPlayer=MediaPlayer.create(getContext(), R.raw.combo_sound11);
                        break;
                    default:
                        mMediaPlayer=MediaPlayer.create(getContext(), R.raw.combo_sound12);
                        break;
                }
                mMediaPlayer.start();
                try {
                    Thread.sleep(300);
                    //音效播放完毕释放资源
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mMediaPlayer.stop();
                mMediaPlayer.release();
            }
        }).start();
    }
    public void startgame(int column, int line){
        //开始游戏(新游戏)
        initial(column,line);
        //初始随机个方块
        add();
        add();
        maxcombo=0;
        combo=0;
        lcombo=0;
        score=0;
        lscore=0;
        check();
        if(mgameListener!=null){
            mgameListener.onScoreChange(score);
            mgameListener.onMaxComboChange(maxcombo);
            mgameListener.onComboChange(combo);
        }
    }
    public void firstplay(int column,int line){
        //初次运行
        maxscore=0;
        startgame(column,line);
    }
    public void restart(){
        //重新开游戏
        startgame(column,line);
        if(mgameListener!=null){
            mgameListener.onRestartGame();
        }
    }
}
