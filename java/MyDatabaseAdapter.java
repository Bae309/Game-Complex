package com.example.gamecomplex;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class MyDatabaseAdapter {

    private SQLiteDatabase database;
    private DBHelper dbHelper;

    public MyDatabaseAdapter(Context context) {
        dbHelper = new DBHelper(context);
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public boolean authenticateUser(String userId, String pw) {
        Cursor cursor = null;
        boolean isAuthenticated = false;
        try {
            cursor = database.rawQuery("SELECT * FROM my_table WHERE ID = ? AND PW = ?", new String[]{userId, pw});
            isAuthenticated = cursor.getCount() > 0;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return isAuthenticated;
    }

    public Cursor getMemberInfo(String userId) {
        return database.rawQuery("SELECT * FROM my_table WHERE ID = ?", new String[]{userId});
    }

    public String getPasswordForMember(String userId) {
        Cursor cursor = null;
        String password = null;
        try {
            cursor = database.rawQuery("SELECT PW FROM my_table WHERE ID = ?", new String[]{userId});
            if (cursor.moveToFirst()) {
                password = cursor.getString(cursor.getColumnIndex("PW"));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return password;
    }

    public boolean deleteMember(String userId) {
        int result = database.delete("my_table", "ID = ?", new String[]{userId});
        return result > 0;
    }

    public boolean updateMemberInfoWithPassword(String userId, String name, String nickname, String password) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("NAME", name);
        contentValues.put("NickName", nickname);

        if (password != null) {
            contentValues.put("PW", password);
        }

        int result = database.update("my_table", contentValues, "ID = ?", new String[]{userId});
        return result > 0;
    }

    public boolean checkIdExists(String userId) {
        Cursor cursor = null;
        boolean idExists = false;
        try {
            cursor = database.rawQuery("SELECT * FROM my_table WHERE ID = ?", new String[]{userId});
            idExists = cursor.getCount() > 0;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return idExists;
    }

    public long insertData(String userId, String pw, String name, String nickname, int scoreYacht, int score2048) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("ID", userId);
        contentValues.put("PW", pw);
        contentValues.put("NAME", name);
        contentValues.put("NickName", nickname);
        contentValues.put("Score_Yacht", scoreYacht);
        contentValues.put("Score_2048", score2048);
        return database.insert("my_table", null, contentValues);
    }

    public int getHighScore2048(String userId) {
        int highScore = 0;
        Cursor cursor = null;
        try {
            cursor = database.query("my_table", new String[]{"Score_2048"}, "ID = ?", new String[]{userId}, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                highScore = cursor.getInt(cursor.getColumnIndex("Score_2048"));
            }
        } catch (Exception e) {
            Log.e("MyDatabaseAdapter", "Error getting high score for userId: " + userId, e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return highScore;
    }


    public int getUserRanking2048(String userId) {
        Cursor cursor = null;
        int ranking = -1;
        try {
            cursor = database.rawQuery(
                    "SELECT COUNT(*) + 1 AS rank FROM my_table WHERE Score_2048 > (SELECT Score_2048 FROM my_table WHERE ID = ?)",
                    new String[]{userId});
            if (cursor.moveToFirst()) {
                ranking = cursor.getInt(cursor.getColumnIndex("rank"));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return ranking;
    }

    public List<UserScore> getTop10HighScores2048() {
        List<UserScore> userScores = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(
                    "SELECT ID, Name, Score_2048 FROM my_table ORDER BY Score_2048 DESC LIMIT 10", null);
            if (cursor.moveToFirst()) {
                do {
                    String id = cursor.getString(cursor.getColumnIndex("ID"));
                    String name = cursor.getString(cursor.getColumnIndex("Name"));
                    int highScore = cursor.getInt(cursor.getColumnIndex("Score_2048"));
                    userScores.add(new UserScore(id, name, highScore));
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return userScores;
    }

    // 추가한 updateHighScore2048 메서드
    public void updateHighScore2048(String userId, int score) {
        ContentValues values = new ContentValues();
        values.put("Score_2048", score);
        int rowsAffected = database.update("my_table", values, "ID = ?", new String[]{userId});
        if (rowsAffected > 0) {
            Log.d("MyDatabaseAdapter", "High score updated successfully. Rows affected: " + rowsAffected);
        } else {
            Log.d("MyDatabaseAdapter", "High score update failed. Rows affected: " + rowsAffected);
        }
    }

    public int getUserRankingYacht(String userId) {
        Cursor cursor = null;
        int ranking = -1;
        try {
            cursor = database.rawQuery(
                    "SELECT COUNT(*) + 1 AS rank FROM my_table WHERE Score_Yacht > (SELECT Score_Yacht FROM my_table WHERE ID = ?)",
                    new String[]{userId});
            if (cursor.moveToFirst()) {
                ranking = cursor.getInt(cursor.getColumnIndex("rank"));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return ranking;
    }

    public List<UserScore> getTop10HighScoresYacht() {
        List<UserScore> userScores = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(
                    "SELECT ID, Name, Score_Yacht FROM my_table ORDER BY Score_Yacht DESC LIMIT 10", null);
            if (cursor.moveToFirst()) {
                do {
                    String userId = cursor.getString(cursor.getColumnIndex("ID"));
                    String name = cursor.getString(cursor.getColumnIndex("Name"));
                    int highScore = cursor.getInt(cursor.getColumnIndex("Score_Yacht"));
                    userScores.add(new UserScore(userId, name, highScore));
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return userScores;
    }

    public void updateHighScoreYacht(String userId, int newScore) {
        int currentHighScore = getHighScoreYacht(userId);
        if (newScore > currentHighScore) {
            ContentValues values = new ContentValues();
            values.put("Score_Yacht", newScore);
            int rowsAffected = database.update("my_table", values, "ID = ?", new String[]{userId});
            if (rowsAffected > 0) {
                Log.d("MyDatabaseAdapter", "High score updated successfully. Rows affected: " + rowsAffected);
            } else {
                Log.d("MyDatabaseAdapter", "High score update failed. Rows affected: " + rowsAffected);
            }
        }
    }

    public int getHighScoreYacht(String userId) {
        int highScore = 0;
        Cursor cursor = null;
        try {
            cursor = database.query("my_table", new String[]{"Score_Yacht"}, "ID = ?", new String[]{userId}, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                highScore = cursor.getInt(cursor.getColumnIndex("Score_Yacht"));
            }
        } catch (Exception e) {
            Log.e("MyDatabaseAdapter", "Error getting high score for userId: " + userId, e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return highScore;
    }


    private static class DBHelper extends SQLiteOpenHelper {

        private static final int DATABASE_VERSION = 2;
        private static final String DATABASE_NAME = "my_database";

        public DBHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE my_table (ID TEXT PRIMARY KEY, PW TEXT, Name TEXT, NickName TEXT, Score_Yacht INTEGER, Score_2048 INTEGER)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS my_table");
            onCreate(db);
        }
    }
}