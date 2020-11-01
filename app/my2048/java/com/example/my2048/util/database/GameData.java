package com.example.my2048.util.database;

import java.io.Serializable;

public class GameData implements Serializable {
    private static final long serialVersionUID=1111L;
    //游戏数据类
    public int column=4;
    public int line=4;
    public int [][]map=new int[8][8];
    public int score;
    public int maxscore;
    public int maxcombo;
    public int maxblock;
}
