package com.example.kelolajasa.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.kelolajasa.model.Pengguna;

import java.util.ArrayList;
import java.util.List;

public class PenggunaDAO {

    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    public PenggunaDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    /**
     * Daftarkan pengguna baru. Role default = 2 (User).
     * @return id baris yang diinsert, atau -1 jika gagal
     */
    public long register(String email, String username, String noTelp, String password) {
        // Cek apakah email sudah terdaftar
        if (isEmailTerdaftar(email)) return -2; // -2 = email duplikat
        // Cek apakah username sudah dipakai
        if (isUsernameTerdaftar(username)) return -3; // -3 = username duplikat

        ContentValues cv = new ContentValues();
        cv.put("id_role", 2); // default: User
        cv.put("username", username.trim());
        cv.put("nama_pengguna", username.trim()); // nama awal = username
        cv.put("email", email.trim().toLowerCase());
        cv.put("no_telp", noTelp.trim());
        cv.put("password", password);
        cv.put("id_provinsi", 0);
        cv.put("id_kabupaten", 0);
        cv.put("id_kecamatan", 0);
        cv.put("id_desa", 0);
        cv.put("alamat_lengkap", "");
        cv.put("foto_profil", "");
        cv.put("tanggal_lahir", "");

        return db.insert("pengguna", null, cv);
    }

    /**
     * Login: cari pengguna berdasarkan email + password.
     * @return objek Pengguna jika cocok, null jika tidak ditemukan
     */
    public Pengguna login(String email, String password) {
        Cursor c = null;
        try {
            c = db.rawQuery(
                    "SELECT * FROM pengguna WHERE LOWER(email) = ? AND password = ? LIMIT 1",
                    new String[]{ email.trim().toLowerCase(), password }
            );
            if (c != null && c.moveToFirst()) {
                return cursorToPengguna(c);
            }
        } finally {
            if (c != null) c.close();
        }
        return null;
    }

    /**
     * Ambil pengguna berdasarkan ID.
     */
    public Pengguna getPenggunaById(int idPengguna) {
        Cursor c = null;
        try {
            c = db.rawQuery(
                    "SELECT * FROM pengguna WHERE id_pengguna = ? LIMIT 1",
                    new String[]{ String.valueOf(idPengguna) }
            );
            if (c != null && c.moveToFirst()) {
                return cursorToPengguna(c);
            }
        } finally {
            if (c != null) c.close();
        }
        return null;
    }

    /**
     * Ambil semua pengguna (untuk panel admin).
     */
    public List<Pengguna> getAllPengguna() {
        List<Pengguna> list = new ArrayList<>();
        Cursor c = null;
        try {
            c = db.rawQuery("SELECT * FROM pengguna ORDER BY id_pengguna DESC", null);
            if (c != null && c.moveToFirst()) {
                do {
                    list.add(cursorToPengguna(c));
                } while (c.moveToNext());
            }
        } finally {
            if (c != null) c.close();
        }
        return list;
    }

    /**
     * Update profil pengguna.
     * @return jumlah baris yang diupdate
     */
    public int updateProfil(int idPengguna, String namaPengguna,
                            String noTelp, String tanggalLahir,
                            int idProvinsi, int idKabupaten, int idKecamatan,
                            int idDesa, String alamatLengkap) {
        ContentValues cv = new ContentValues();
        cv.put("nama_pengguna", namaPengguna);
        cv.put("no_telp", noTelp);
        cv.put("tanggal_lahir", tanggalLahir);
        cv.put("id_provinsi", idProvinsi);
        cv.put("id_kabupaten", idKabupaten);
        cv.put("id_kecamatan", idKecamatan);
        cv.put("id_desa", idDesa);
        cv.put("alamat_lengkap", alamatLengkap);
        return db.update("pengguna", cv, "id_pengguna = ?",
                new String[]{ String.valueOf(idPengguna) });
    }

    /**
     * Update role pengguna (dipakai admin untuk approve freelancer).
     */
    public int updateRole(int idPengguna, int idRoleBaru) {
        ContentValues cv = new ContentValues();
        cv.put("id_role", idRoleBaru);
        return db.update("pengguna", cv, "id_pengguna = ?",
                new String[]{ String.valueOf(idPengguna) });
    }

    /**
     * Hapus pengguna (admin).
     */
    public int deletePengguna(int idPengguna) {
        return db.delete("pengguna", "id_pengguna = ?",
                new String[]{ String.valueOf(idPengguna) });
    }

    // ========== HELPER ==========

    private boolean isEmailTerdaftar(String email) {
        Cursor c = null;
        try {
            c = db.rawQuery(
                    "SELECT id_pengguna FROM pengguna WHERE LOWER(email) = ? LIMIT 1",
                    new String[]{ email.trim().toLowerCase() }
            );
            return c != null && c.getCount() > 0;
        } finally {
            if (c != null) c.close();
        }
    }

    private boolean isUsernameTerdaftar(String username) {
        Cursor c = null;
        try {
            c = db.rawQuery(
                    "SELECT id_pengguna FROM pengguna WHERE LOWER(username) = ? LIMIT 1",
                    new String[]{ username.trim().toLowerCase() }
            );
            return c != null && c.getCount() > 0;
        } finally {
            if (c != null) c.close();
        }
    }

    private Pengguna cursorToPengguna(Cursor c) {
        return new Pengguna(
                c.getInt(c.getColumnIndexOrThrow("id_pengguna")),
                c.getInt(c.getColumnIndexOrThrow("id_role")),
                c.getString(c.getColumnIndexOrThrow("username")),
                c.getString(c.getColumnIndexOrThrow("nama_pengguna")),
                c.getString(c.getColumnIndexOrThrow("tanggal_lahir")),
                c.getString(c.getColumnIndexOrThrow("no_telp")),
                c.getString(c.getColumnIndexOrThrow("email")),
                c.getString(c.getColumnIndexOrThrow("password")),
                c.getInt(c.getColumnIndexOrThrow("id_provinsi")),
                c.getInt(c.getColumnIndexOrThrow("id_kabupaten")),
                c.getInt(c.getColumnIndexOrThrow("id_kecamatan")),
                c.getInt(c.getColumnIndexOrThrow("id_desa")),
                c.getString(c.getColumnIndexOrThrow("alamat_lengkap")),
                c.getString(c.getColumnIndexOrThrow("foto_profil"))
        );
    }

    public void close() {
        if (db != null && db.isOpen()) db.close();
    }

    public SQLiteDatabase getDb() {
        return db;
    }
}