package com.example.kelolajasa.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.kelolajasa.model.Jasa;

import java.util.ArrayList;
import java.util.List;

public class JasaDAO {

    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    public JasaDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    /** Ambil semua jasa dengan nama kategori (JOIN). Dipakai oleh JasaAdapter. */
    public List<Jasa> getAllWithKategori() {
        List<Jasa> list = new ArrayList<>();
        String sql = "SELECT j.id_jasa, j.id_kategori, j.nama_jasa, " +
                "COALESCE(k.nama_kategori, '-') AS nama_kategori " +
                "FROM jasa j " +
                "LEFT JOIN kategori k ON j.id_kategori = k.id_kategori " +
                "ORDER BY j.nama_jasa ASC";
        Cursor c = null;
        try {
            c = db.rawQuery(sql, null);
            if (c != null && c.moveToFirst()) {
                do { list.add(cursorToJasa(c)); } while (c.moveToNext());
            }
        } finally {
            if (c != null) c.close();
        }
        return list;
    }

    /** Cari jasa berdasarkan keyword. */
    public List<Jasa> searchWithKategori(String keyword) {
        List<Jasa> list = new ArrayList<>();
        String like = "%" + keyword + "%";
        String sql = "SELECT j.id_jasa, j.id_kategori, j.nama_jasa, " +
                "COALESCE(k.nama_kategori, '-') AS nama_kategori " +
                "FROM jasa j LEFT JOIN kategori k ON j.id_kategori = k.id_kategori " +
                "WHERE j.nama_jasa LIKE ? OR k.nama_kategori LIKE ? " +
                "ORDER BY j.nama_jasa ASC";
        Cursor c = null;
        try {
            c = db.rawQuery(sql, new String[]{ like, like });
            if (c != null && c.moveToFirst()) {
                do { list.add(cursorToJasa(c)); } while (c.moveToNext());
            }
        } finally {
            if (c != null) c.close();
        }
        return list;
    }

    public List<Jasa> getByKategori(int idKategori) {
        List<Jasa> list = new ArrayList<>();
        Cursor c = db.rawQuery(
                "SELECT * FROM jasa WHERE id_kategori = ? ORDER BY nama_jasa ASC",
                new String[]{ String.valueOf(idKategori) }
        );
        if (c != null && c.moveToFirst()) {
            do {
                list.add(new Jasa(
                        c.getInt(c.getColumnIndexOrThrow("id_jasa")),
                        c.getInt(c.getColumnIndexOrThrow("id_kategori")),
                        c.getString(c.getColumnIndexOrThrow("nama_jasa"))
                ));
            } while (c.moveToNext());
            c.close();
        }
        return list;
    }

    /** @return -2 nama+kategori duplikat, -1 gagal, id baru jika sukses */
    public long insert(int idKategori, String namaJasa) {
        if (isExists(idKategori, namaJasa)) return -2;
        ContentValues cv = new ContentValues();
        cv.put("id_kategori", idKategori);
        cv.put("nama_jasa", namaJasa.trim());
        return db.insert("jasa", null, cv);
    }

    public int update(int idJasa, int idKategori, String namaJasa) {
        ContentValues cv = new ContentValues();
        cv.put("id_kategori", idKategori);
        cv.put("nama_jasa", namaJasa.trim());
        return db.update("jasa", cv, "id_jasa = ?",
                new String[]{ String.valueOf(idJasa) });
    }

    /** @return -2 jika masih dipakai layanan, otherwise jumlah baris terhapus */
    public int delete(int idJasa) {
        Cursor c = null;
        try {
            c = db.rawQuery("SELECT COUNT(*) FROM layanan WHERE id_jasa = ?",
                    new String[]{ String.valueOf(idJasa) });
            if (c != null && c.moveToFirst() && c.getInt(0) > 0) return -2;
        } finally {
            if (c != null) c.close();
        }
        return db.delete("jasa", "id_jasa = ?", new String[]{ String.valueOf(idJasa) });
    }

    private boolean isExists(int idKategori, String namaJasa) {
        Cursor c = null;
        try {
            c = db.rawQuery(
                    "SELECT id_jasa FROM jasa WHERE id_kategori = ? AND LOWER(nama_jasa) = ? LIMIT 1",
                    new String[]{ String.valueOf(idKategori), namaJasa.trim().toLowerCase() });
            return c != null && c.getCount() > 0;
        } finally {
            if (c != null) c.close();
        }
    }

    private Jasa cursorToJasa(Cursor c) {
        Jasa j = new Jasa(
                c.getInt(c.getColumnIndexOrThrow("id_jasa")),
                c.getInt(c.getColumnIndexOrThrow("id_kategori")),
                c.getString(c.getColumnIndexOrThrow("nama_jasa"))
        );
        int kolNamaKat = c.getColumnIndex("nama_kategori");
        if (kolNamaKat != -1) j.setNamaKategori(c.getString(kolNamaKat));
        return j;
    }

    public void close() {
        if (db != null && db.isOpen()) db.close();
    }

    public SQLiteDatabase getDb() { return db; }
}