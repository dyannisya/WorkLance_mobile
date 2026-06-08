package com.example.kelolajasa.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.kelolajasa.model.Kategori;

import java.util.ArrayList;
import java.util.List;

public class KategoriDAO {

    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    public KategoriDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    public List<Kategori> getAll() {
        List<Kategori> list = new ArrayList<>();
        Cursor c = null;
        try {
            c = db.rawQuery("SELECT * FROM kategori ORDER BY nama_kategori ASC", null);
            if (c != null && c.moveToFirst()) {
                do {
                    list.add(new Kategori(
                            c.getInt(c.getColumnIndexOrThrow("id_kategori")),
                            c.getString(c.getColumnIndexOrThrow("nama_kategori"))
                    ));
                } while (c.moveToNext());
            }
        } finally {
            if (c != null) c.close();
        }
        return list;
    }

    public Kategori getById(int idKategori) {
        Cursor c = null;
        try {
            c = db.rawQuery("SELECT * FROM kategori WHERE id_kategori = ? LIMIT 1",
                    new String[]{ String.valueOf(idKategori) });
            if (c != null && c.moveToFirst()) {
                return new Kategori(
                        c.getInt(c.getColumnIndexOrThrow("id_kategori")),
                        c.getString(c.getColumnIndexOrThrow("nama_kategori"))
                );
            }
        } finally {
            if (c != null) c.close();
        }
        return null;
    }

    /** @return -2 jika nama sudah ada, -1 jika gagal, id baru jika sukses */
    public long insert(String namaKategori) {
        if (isNamaExists(namaKategori)) return -2;
        ContentValues cv = new ContentValues();
        cv.put("nama_kategori", namaKategori.trim());
        return db.insert("kategori", null, cv);
    }

    public int update(int idKategori, String namaKategori) {
        ContentValues cv = new ContentValues();
        cv.put("nama_kategori", namaKategori.trim());
        return db.update("kategori", cv, "id_kategori = ?",
                new String[]{ String.valueOf(idKategori) });
    }

    public int delete(int idKategori) {
        // Cek apakah kategori masih dipakai oleh jasa
        Cursor c = null;
        try {
            c = db.rawQuery("SELECT COUNT(*) FROM jasa WHERE id_kategori = ?",
                    new String[]{ String.valueOf(idKategori) });
            if (c != null && c.moveToFirst() && c.getInt(0) > 0) {
                return -2; // Masih dipakai, tidak bisa dihapus
            }
        } finally {
            if (c != null) c.close();
        }
        return db.delete("kategori", "id_kategori = ?",
                new String[]{ String.valueOf(idKategori) });
    }

    private boolean isNamaExists(String nama) {
        Cursor c = null;
        try {
            c = db.rawQuery(
                    "SELECT id_kategori FROM kategori WHERE LOWER(nama_kategori) = ? LIMIT 1",
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