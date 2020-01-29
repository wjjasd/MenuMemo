package com.wjjasd.google.menumemo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class MemoDBHelper extends SQLiteOpenHelper {

    private static MemoDBHelper sInstance;
    private static final int DB_VERSION = 3;
    private static final String DB_NAME = "Memo.db";
    private static final String TABLE_NAME = "menu";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_CATEGORY = "category";

    public MemoDBHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + "(" + COLUMN_NAME + " text primary key, " + COLUMN_CATEGORY + " text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(" DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public static MemoDBHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new MemoDBHelper(context);
        }
        return sInstance;
    }
}
