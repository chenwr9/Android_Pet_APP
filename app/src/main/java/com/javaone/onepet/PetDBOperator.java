package com.javaone.onepet;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class PetDBOperator {
    private SQLiteDbHelper dbHelper;
    private SQLiteDatabase db;
    private Pet mPet = new Pet();
    private Pet yPet = new Pet();

    public PetDBOperator(Context context) {
        dbHelper = new SQLiteDbHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    public void add(Pet mPet) {
        ContentValues cValue = new ContentValues();
        cValue.put("belong", mPet.belong);
        cValue.put("name", mPet.name);
        cValue.put("sex", mPet.sex);
        cValue.put("level", mPet.level);
        cValue.put("character", mPet.character);
        cValue.put("partner", mPet.partner);
        cValue.put("wechat", mPet.wechat);
        cValue.put("model", mPet.model);
        db.insert("pet", null, cValue);
    }

    public void delete(String name) {

    }

    public void update(String belong, String column, String value) {
        ContentValues cValue = new ContentValues();
        cValue.put(column, value);
        db.update("pet", cValue, "belong = ?", new String[]{belong});
    }

    public int select(String belong) {
        int count = 0;
        Cursor cursor = db.query("pet", null, "belong = ? ", new String[]{belong}, null, null, null, null);
        while(cursor.moveToNext()) {
            mPet.id = cursor.getInt(0);
            mPet.belong = cursor.getString(1);
            mPet.name = cursor.getString(2);
            mPet.sex = cursor.getInt(3);
            mPet.level = cursor.getInt(4);
            mPet.character = cursor.getString(5);
            mPet.partner = cursor.getString(6);
            mPet.wechat = cursor.getString(7);
            mPet.model = cursor.getInt(8);
            count += 1;
        }
        return count;
    }

    public boolean isPetExisted() {
        return this.select("self") > 0;
    }

    public Pet getPet() {
        return mPet;
    }
}