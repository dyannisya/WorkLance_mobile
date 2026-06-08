package com.example.kelolajasa.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.kelolajasa.model.Satuan;

import java.util.ArrayList;
import java.util.List;

public class SatuanDAO {

    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    public SatuanDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    public List<Satuan> getAll() {
        List<Satuan> list = new ArrayList<>();
        Cursor c = null;
        try {
            c = db.rawQuery("SELECT * FROM satuan ORDER BY nama_satuan ASC", null);
            if (c != null && c.moveToFirst()) {
                do {
                    list.add(new Satuan(
                            c.getInt(c.getColumnIndexOrThrow("id_satuan")),
                            c.getString(c.getColumnIndexOrThrow("nama_satuan"))
                    ));
                } while (c.moveToNext());
            }
        } finally {
            if (c != null) c.close();
        }
        return list;
    }

    /** @return -2 nama duplikat, -1 gagal, id baru jika sukses */
    public long insert(String namaSatuan) {
        if (isNamaExists(namaSatuan)) return -2;
        ContentValues cv = new ContentValues();
        cv.put("nama_satuan", namaSatuan.trim());
        return db.insert("satuan", null, cv);
    }

    public int update(int idSatuan, String namaSatuan) {
        ContentValues cv = new ContentValues();
        cv.put("nama_satuan", namaSatuan.trim());
        return db.update("satuan", cv, "id_satuan = ?",
                new String[]{ String.valueOf(idSatuan) });
    }

    public int delete(int idSatuan) {
        // Cek apakah masih dipakai oleh layanan
        Cursor c = null;
        try {
            c = db.rawQuery("SELECT COUNT(*) FROM layanan WHERE id_satuan = ?",
                    new String[]{ String.valueOf(idSatuan) });
            if (c != null && c.moveToFirst() && c.getInt(0) > 0) return -2;
        } finally {
            if (c != null) c.close();
        }
        return db.delete("satuan", "id_satuan = ?",
                new String[]{ String.valueOf(idSatuan) });
    }

    private boolean isNamaExists(String nama) {
        Cursor c = null;
        try {
            c = db.rawQuery(
                    "SELECT id_satuan FROM satuan WHERE LOWER(nama_satuan) = ? LIMIT 1",
                    new String[]{ nama.trim().toLowerCase() });
            return c != null && c.getCount() > 0;
        } finally {
            if (c != null) c.close();
        }
    }

    public void close() {
        if (db != null && db.isOpen()) db.close();
    }

    public SQLiteDatabase getDb() { return db; }
}