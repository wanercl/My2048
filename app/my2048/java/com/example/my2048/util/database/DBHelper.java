package com.example.my2048.util.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

public class DBHelper extends SQLiteOpenHelper {
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public DBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        setWriteAheadLoggingEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists gamedata(type varchar,data blob)");
        db.execSQL("create table if not exists gamesetting(type varchar,lastflag integer,mute boolean)");
        ContentValues values=new ContentValues();
        values.put("type","setting");
        values.put("mute",false);
        db.insert("gamesetting",null,values);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
