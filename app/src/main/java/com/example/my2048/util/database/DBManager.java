package com.example.my2048.util.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.IOException;

public class DBManager {
    public static GameData getData(Context context,int flag){
        GameData data=null;
        DBHelper helper=new DBHelper(context,"data.db",null,1);
        SQLiteDatabase database=helper.getWritableDatabase();
        String []columns=new String[]{
                "data"
        };
        String []args=new String[]{
                "data"+flag
        };
        Cursor cursor=database.query("gamedata",columns,"type=?",args,null,null,null);
        if(cursor.moveToFirst()){
            byte[] da=cursor.getBlob(0);
            data= (GameData) Converter.bytesToObject(da);
        }
        cursor.close();
        database.close();
        return data;
    }

    public static void addData(Context context, GameData data){
        DBHelper helper=new DBHelper(context,"data.db",null,1);
        SQLiteDatabase database=helper.getWritableDatabase();
        byte[] dat= null;
        try {
            dat = Converter.objectToBytes(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ContentValues values=new ContentValues();
        values.put("type","data"+data.column);
        values.put("data",dat);
        database.insert("gamedata",null,values);
        database.close();
    }

    public static void updateData(Context context, GameData data){
        DBHelper helper=new DBHelper(context,"data.db",null,1);
        SQLiteDatabase database=helper.getWritableDatabase();
        ContentValues values=new ContentValues();
        String []args=new String[]{
                "data"+data.column
        };
        byte[] dat=null;
        try {
            dat = Converter.objectToBytes(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        values.put("data",dat);
        int res= database.update("gamedata",values,"type=?",args);
        if(res==0){
            addData(context,data);
        }
        database.close();
        Log.i("修改完成","----");
    }

    public static void updateMute(Context context, boolean mute){
        DBHelper helper=new DBHelper(context,"data.db",null,1);
        SQLiteDatabase database=helper.getWritableDatabase();
        ContentValues values=new ContentValues();
        String []args=new String[]{
                "setting"
        };
        values.put("mute",mute);
        database.update("gamesetting",values,"type=?",args);
        database.close();
    }

    public static boolean getMute(Context context){
        boolean data=false;
        DBHelper helper=new DBHelper(context,"data.db",null,1);
        SQLiteDatabase database=helper.getWritableDatabase();
        String []columns=new String[]{
                "mute"
        };
        String []args=new String[]{
                "setting"
        };
        Cursor cursor=database.query("gamesetting",columns,"type=?",args,null,null,null);
        if(cursor.moveToFirst()){
            data=cursor.getInt(0)>0;
        }
        cursor.close();
        database.close();
        return data;
    }
}
