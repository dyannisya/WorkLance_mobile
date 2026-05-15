package com.example.kelolajasa.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.content.Context;
import android.content.ContentValues;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "kelola.db";
    private static final int DB_VERSION = 1;

    public static final String TABLE_JASA = "jasa";
    public static final String COL_ID = "id";
    public static final String COL_NAMA = "nama";
    public static final String COL_KATEGORI = "kategori";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_JASA + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NAMA + " TEXT, " +
                COL_KATEGORI + " TEXT)";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_JASA);
        onCreate(db);
    }

    public Cursor getAllJasa() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM jasa", null);
    }

    // create
    public boolean insertJasa(String nama, String kategori) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COL_NAMA, nama);
        values.put(COL_KATEGORI, kategori);

        long result = db.insert(TABLE_JASA, null, values);
        return result != -1;
    }

    // delete
    public boolean deleteJasa(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete("jasa", "id=?", new String[]{String.valueOf(id)});
        return result > 0;
    }

    // update
    public boolean updateJasa(int id, String nama, String kategori) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("nama", nama);
        values.put("kategori", kategori);

        int result = db.update("jasa", values, "id=?", new String[]{String.valueOf(id)});
        return result > 0;
    }
}