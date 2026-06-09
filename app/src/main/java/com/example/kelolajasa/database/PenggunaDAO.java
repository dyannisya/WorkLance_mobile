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
        cv.put("id_role", 2);
        cv.put("username", username.trim());
        cv.put("nama_pengguna", username.trim());
        cv.put("email", email.trim().toLowerCase());
        cv.put("no_telp", noTelp.trim());
        cv.put("password", password);
        cv.put("id_provinsi", "");
        cv.put("id_kabupaten", "");
        cv.put("id_kecamatan", "");
        cv.put("id_desa", "");

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
     * Mengambil daftar pengguna yang terdaftar sebagai Freelancer (id_role = 3)
     */
    public List<Pengguna> getFreelancerUnggulan() {
        List<Pengguna> list = new ArrayList<>();
        Cursor c = null;
        try {
            // Mengambil data user yang role-nya 3 (Freelancer), dibatasi 5 orang agar dashboard tidak terlalu berat
            c = db.rawQuery("SELECT * FROM pengguna WHERE id_role = 3 ORDER BY id_pengguna DESC LIMIT 5", null);
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
    /**
     * Update profil pengguna.
     * @return jumlah baris yang diupdate
     */
    public int updateProfil(int idPengguna, String namaPengguna,
                            String noTelp, String tanggalLahir,
                            String idProvinsi, String idKabupaten, String idKecamatan, // UBAH DARI int KE String
                            String idDesa, String alamatLengkap) {                     // UBAH DARI int KE String
        ContentValues cv = new ContentValues();
        cv.put("nama_pengguna", namaPengguna);
        cv.put("no_telp", noTelp);
        cv.put("tanggal_lahir", tanggalLahir);
        cv.put("id_provinsi", idProvinsi); // Simpan teks
        cv.put("id_kabupaten", idKabupaten); // Simpan teks
        cv.put("id_kecamatan", idKecamatan); // Simpan teks
        cv.put("id_desa", idDesa); // Simpan teks
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
                c.getString(c.getColumnIndexOrThrow("id_provinsi")),
                c.getString(c.getColumnIndexOrThrow("id_kabupaten")),
                c.getString(c.getColumnIndexOrThrow("id_kecamatan")),
                c.getString(c.getColumnIndexOrThrow("id_desa")),
                c.getString(c.getColumnIndexOrThrow("alamat_lengkap")),
                c.getString(c.getColumnIndexOrThrow("foto_profil"))
        );
    }

    /**
     * Update Informasi Akun (Username, Nama Lengkap, Tanggal Lahir)
     * @return true jika berhasil, false jika gagal
     */
    public boolean updateInformasiAkun(int idPengguna, String username, String namaLengkap, String tanggalLahir) {
        android.content.ContentValues cv = new android.content.ContentValues();
        cv.put("username", username.trim());
        cv.put("nama_pengguna", namaLengkap.trim());
        cv.put("tanggal_lahir", tanggalLahir.trim());

        int result = db.update("pengguna", cv, "id_pengguna = ?",
                new String[]{String.valueOf(idPengguna)});
        return result > 0;
    }

    /**
     * Update Kontak dan Alamat (Email, No Telepon, Alamat Lengkap)
     */
    /**
     * Update Kontak dan Alamat Full (Termasuk Provinsi, Kabupaten, dll)
     */
    public boolean updateKontakAlamat(int idPengguna, String email, String noTelp,
                                      String provinsi, String kabupaten,
                                      String kecamatan, String desa, String alamatLengkap) {
        android.content.ContentValues cv = new android.content.ContentValues();
        cv.put("email", email.trim().toLowerCase());
        cv.put("no_telp", noTelp.trim());
        cv.put("id_provinsi", provinsi.trim());     // Menyimpan teks ke dalam database
        cv.put("id_kabupaten", kabupaten.trim());   // Menyimpan teks ke dalam database
        cv.put("id_kecamatan", kecamatan.trim());   // Menyimpan teks ke dalam database
        cv.put("id_desa", desa.trim());             // Menyimpan teks ke dalam database
        cv.put("alamat_lengkap", alamatLengkap.trim());

        int result = db.update("pengguna", cv, "id_pengguna = ?",
                new String[]{String.valueOf(idPengguna)});
        return result > 0;
    }

    /**
     * Update Password Pengguna
     * @return true jika berhasil, false jika gagal
     */
    public boolean updatePassword(int idPengguna, String passwordBaru) {
        android.content.ContentValues cv = new android.content.ContentValues();
        cv.put("password", passwordBaru);

        int result = db.update("pengguna", cv, "id_pengguna = ?",
                new String[]{String.valueOf(idPengguna)});
        return result > 0;
    }

    // Ubah fungsi ini di PenggunaDAO.java
    public String getInfoFreelancer(int idPengguna) {
        String info = "";
        Cursor cursor = null;

        try {
            // Gunakan SELECT * agar tidak crash jika ada nama kolom yang berbeda
            String query = "SELECT * FROM pengajuan_freelancer WHERE id_pengguna = ?";
            cursor = db.rawQuery(query, new String[]{String.valueOf(idPengguna)});

            if (cursor != null && cursor.moveToFirst()) {
                // Ambil data menggunakan nama kolom persis seperti di database kamu
                String nik = cursor.getString(cursor.getColumnIndexOrThrow("nik"));
                String deskripsi = cursor.getString(cursor.getColumnIndexOrThrow("deskripsi"));

                // Jika nama kolom statusmu bukan status_pengajuan (misal cuma 'status'), ini tetap aman
                int idxStatus = cursor.getColumnIndex("status_pengajuan");
                if (idxStatus == -1) idxStatus = cursor.getColumnIndex("status");
                String status = (idxStatus != -1) ? cursor.getString(idxStatus) : "-";

                // Pengecekan kolom tanggal yang aman
                int idxTanggal = cursor.getColumnIndex("tanggal_pengajuan");
                if (idxTanggal == -1) idxTanggal = cursor.getColumnIndex("tanggal");
                String tanggal = (idxTanggal != -1) ? cursor.getString(idxTanggal) : "-";

                info = "\n\n-- Info Freelancer --" +
                        "\nNIK: " + nik +
                        "\nStatus: " + status + " (" + tanggal + ")" +
                        "\nDeskripsi: " + deskripsi;
            }
        } catch (Exception e) {
            e.printStackTrace();
            info = "\n\n(Gagal memuat detail pengajuan freelancer)";
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return info;
    }

    public void close() {
        if (db != null && db.isOpen()) db.close();
    }

    public SQLiteDatabase getDb() {
        return db;
    }
}