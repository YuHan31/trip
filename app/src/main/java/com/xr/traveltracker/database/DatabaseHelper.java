package com.xr.traveltracker.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    // 数据库名称
    private static final String DATABASE_NAME = "sqlite.db";
    // 数据库版本
    private static final int DATABASE_VERSION = 1;

    // 表名
    private static final String TABLE_TRAVEL = "travel";
    // 列名
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_DESTINATION = "destination";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_DESCRIPTION = "description";

    // 创建表的 SQL 语句
    private static final String CREATE_TABLE_TRAVEL = "CREATE TABLE IF NOT EXISTS " + TABLE_TRAVEL + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_DESTINATION + " TEXT, " +
            COLUMN_DATE + " TEXT, " +
            COLUMN_DESCRIPTION + " TEXT)";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 创建表
        db.execSQL(CREATE_TABLE_TRAVEL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 如果数据库版本升级，删除旧表并重新创建
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRAVEL);
        onCreate(db);
    }

    // 插入旅行记录
    public long insertTravel(String destination, String date, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DESTINATION, destination);
        values.put(COLUMN_DATE, date);
        values.put(COLUMN_DESCRIPTION, description);

        long newRowId = db.insert(TABLE_TRAVEL, null, values);
        db.close();
        return newRowId;
    }

    // 查询所有旅行记录
    public Cursor getAllTravels() {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_ID, COLUMN_DESTINATION, COLUMN_DATE, COLUMN_DESCRIPTION};
        Cursor cursor = db.query(TABLE_TRAVEL, columns, null, null, null, null, null);
        return cursor;
    }

    // 根据 ID 查询单条旅行记录
    public Cursor getTravelById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_ID, COLUMN_DESTINATION, COLUMN_DATE, COLUMN_DESCRIPTION};
        String selection = COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};
        Cursor cursor = db.query(TABLE_TRAVEL, columns, selection, selectionArgs, null, null, null);
        return cursor;
    }

    // 更新旅行记录
    public int updateTravel(int id, String destination, String date, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DESTINATION, destination);
        values.put(COLUMN_DATE, date);
        values.put(COLUMN_DESCRIPTION, description);

        String whereClause = COLUMN_ID + " = ?";
        String[] whereArgs = {String.valueOf(id)};

        int rowsUpdated = db.update(TABLE_TRAVEL, values, whereClause, whereArgs);
        db.close();
        return rowsUpdated;
    }

    // 删除旅行记录
    public int deleteTravel(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = COLUMN_ID + " = ?";
        String[] whereArgs = {String.valueOf(id)};

        int rowsDeleted = db.delete(TABLE_TRAVEL, whereClause, whereArgs);
        db.close();
        return rowsDeleted;
    }
}