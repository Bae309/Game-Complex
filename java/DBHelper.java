// DBHelper.java
package com.example.gamecomplex;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "members.db";
    private static final int DATABASE_VERSION = 2;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS my_table (" +
                "ID VARCHAR(60) PRIMARY KEY, " +
                "PW VARCHAR(60), " +
                "Name VARCHAR(60), " +
                "NickName VARCHAR(60), " +
                "Score_Yacht INTEGER, " +
                "Score_2048 INTEGER)";
        db.execSQL(CREATE_TABLE);
        Log.d("DBHelper", "Table created: " + CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS my_table");
        onCreate(db);
    }
}
