package com.example.kelolajasa.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.kelolajasa.database.DatabaseHelper;

public class BookingDAO {

    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    public BookingDAO(Context context) {
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
