package com.javaone.onepet;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteDbHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "database.db";
    public static final int DB_VERSION = 1;
    public static final String TABLE_PET = "pet";

    //创建 Pet 表的 sql 语句
    private static final String PET_CREATE_TABLE_SQL = "create table " + TABLE_PET + "("
            + "id integer primary key autoincrement,"
            + "belong varchar(20),"
            + "name varchar(20),"
            + "sex integer,"
            + "level integer,"
            + "character varchar(20),"
            + "partner varchar(20),"
            + "wechat varchar(20),"
            + "model integer"
            + ");";

    public SQLiteDbHelper(Context context) {
        // 传递数据库名与版本号给父类
        super(context, DB_NAME, null, DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        // 在这里通过 db.execSQL 函数执行 SQL 语句创建所需要的表
        // 创建 Pet 表
        db.execSQL(PET_CREATE_TABLE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 数据库版本号变更会调用 onUpgrade 函数，在这根据版本号进行升级数据库
        switch (oldVersion) {
            case 1:
                // do something
                break;

            default:
                break;
        }
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            // 启动外键
            db.execSQL("PRAGMA foreign_keys = 1;");

            //或者这样写
            String query = String.format("PRAGMA foreign_keys = %s", "ON");
            db.execSQL(query);
        }
    }
}