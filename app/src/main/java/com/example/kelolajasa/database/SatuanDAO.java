package com.example.kelolajasa.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class SatuanDAO {

    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    public SatuanDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    public SQLiteDatabase getDb() {
        return db;
    }

    public void close() {
        if (db != null && db.isOpen()) {
            db.close();
        }
    }
}
