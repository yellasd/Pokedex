package com.example.pokedex.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class FavouritesDatabase extends SQLiteOpenHelper {

    private final static int DB_VERSION = 1;
    private final static String DATABASE_NAME = "PokemonDatabase";
    private final static String TABLE_NAME = "pokemon_favourites";
    private final static String COL_1 = "NAME";
    private final static String COL_2 = "ID";
    private final static String COL_3 = "IMAGE";


    public FavouritesDatabase(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " (NAME TEXT,ID INTEGER,IMAGE TEXT) ");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(String name, int id, String imageUrl) {

        SQLiteDatabase db = this.getWritableDatabase();

        if (!checkIfDataExists(db, "ID", id)) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(COL_1, name);
            contentValues.put(COL_2, id);
            contentValues.put(COL_3, imageUrl);
            long result = db.insert(TABLE_NAME, null, contentValues);
            return result != -1;
        }else {
            return false;
        }
    }

    public Cursor getData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME, null);
        return res;
    }

    private boolean checkIfDataExists(SQLiteDatabase db, String fieldName, int fieldValue) {
        String Query = "Select * from " + TABLE_NAME + " where " + fieldName + " = " + fieldValue;
        Cursor cursor = db.rawQuery(Query, null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }
}
