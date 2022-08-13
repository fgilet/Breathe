package com.example.breathe;

import android.content.Context;
import android.database.ContentObservable;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataAccessObject {

    private SQLiteOpenHelper openHelper;
    private SQLiteDatabase db;
    private static DataAccessObject instance;
    public Cursor c = null;

    private DataAccessObject(Context context) {
        this.openHelper = new DataBaseOpenHelper(context);
    }

    public static DataAccessObject getInstance(Context context) {
        if (instance == null) {
            instance = new DataAccessObject(context);
        }
        return instance;
    }

    public void open() {
        this.db = openHelper.getWritableDatabase();
    }

    public void close() {
        if(db != null) {
            this.db.close();
        }
    }

    public String[] getFavorites() {
        c = db.rawQuery("select name from Cities", null);
        String[] cities = new String[c.getCount()];
        if(c.moveToFirst()) {
            for (int i = 0; i < c.getCount(); i++) {
                cities[i] = c.getString(0);
                c.moveToNext();
            }
        }
        return cities;
    }

    public void addFavorite(String city) {
        if(!isFavorite(city)) {
            db.execSQL("insert into Cities values (?)", new String[]{city});
        }
    }

    public void deleteFavorite(String city) {
        db.execSQL("delete from Cities where name=?", new String[]{city});
    }

    public boolean isFavorite(String city) {
        c = db.rawQuery("select exists(select 1 from Cities where name=?)", new String[]{city});
        if(c.moveToFirst()) {
            return c.getInt(0) == 1;
        }
        return false;
    }
}
