package com.example.my2048.util.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.util.Optional;

public class DBManager {
    @RequiresApi(api = Build.VERSION_CODES.N)
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
            Optional<GameData> opd=Converter.bytesToObject(da);
            data=opd.get();
        }
        cursor.close();
        database.close();
        return data;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void addData(Context context, GameData data){
        DBHelper helper=new DBHelper(context,"data.db",null,1);
        SQLiteDatabase database=helper.getWritableDatabase();
        Optional<byte[]> da=Converter.objectToBytes(data);
        byte[] dat=da.get();
        ContentValues values=new ContentValues();
        values.put("type","data"+data.column);
        values.put("data",dat);
        database.insert("gamedata",null,values);
        database.close();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void updateData(Context context, GameData data){
        DBHelper helper=new DBHelper(context,"data.db",null,1);
        SQLiteDatabase database=helper.getWritableDatabase();
        ContentValues values=new ContentValues();
        String []args=new String[]{
                "data"+data.column
        };
        Optional<byte[]> da=Converter.objectToBytes(data);
        byte[] dat=da.get();
        values.put("data",dat);
        int res= database.update("gamedata",values,"type=?",args);
        if(res==0){
            addData(context,data);
        }
        database.close();
        Log.i("修改完成","----");
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
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
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
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
