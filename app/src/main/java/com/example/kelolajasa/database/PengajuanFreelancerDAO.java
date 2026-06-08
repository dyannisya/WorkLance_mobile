package com.example.kelolajasa.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.kelolajasa.model.PengajuanFreelancer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PengajuanFreelancerDAO {

    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    public static final String STATUS_MENUNGGU = "Menunggu";
    public static final String STATUS_DITERIMA = "Diterima";
    public static final String STATUS_DITOLAK = "Ditolak";

    public PengajuanFreelancerDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    /**
     * Ajukan menjadi freelancer.
     * Cek apakah pengguna sudah pernah mengajukan (status Menunggu/Diterima).
     * @return id baris baru, -2 jika sudah ada pengajuan aktif
     */
    public long insert(int idPengguna, String nik, String deskripsi) {
        // Cek apakah sudah ada pengajuan yang belum selesai
        if (hasPengajuanAktif(idPengguna)) return -2;

        String tanggal = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(new Date());

        ContentValues cv = new ContentValues();
        cv.put("id_pengguna", idPengguna);
        cv.put("nik", nik);
        cv.put("deskripsi", deskripsi);
        cv.put("status", STATUS_MENUNGGU);
        cv.put("catatan_admin", "");
        cv.put("tanggal_pengajuan", tanggal);
        return db.insert("pengajuan_freelancer", null, cv);
    }

    /**
     * Ambil semua pengajuan (untuk admin).
     */
    public List<PengajuanFreelancer> getAll() {
        List<PengajuanFreelancer> list = new ArrayList<>();
        Cursor c = null;
        try {
            c = db.rawQuery(
                    "SELECT * FROM pengajuan_freelancer ORDER BY id_pengajuan DESC", null);
            if (c != null && c.moveToFirst()) {
                do { list.add(cursorToPengajuan(c)); } while (c.moveToNext());
            }
        } finally {
            if (c != null) c.close();
        }
        return list;
    }

    /**
     * Ambil pengajuan terbaru dengan nama pengguna (untuk preview admin dashboard).
     */
    public List<String[]> getRecentWithNama(int limit) {
        List<String[]> list = new ArrayList<>();
        Cursor c = null;
        try {
            c = db.rawQuery(
                    "SELECT pf.id_pengajuan, p.nama_pengguna, pf.status " +
                            "FROM pengajuan_freelancer pf " +
                            "LEFT JOIN pengguna p ON pf.id_pengguna = p.id_pengguna " +
                            "ORDER BY pf.id_pengajuan DESC LIMIT ?",
                    new String[]{ String.valueOf(limit) }
            );
            if (c != null && c.moveToFirst()) {
                do {
                    list.add(new String[]{
                            String.valueOf(c.getInt(0)),
                            c.getString(1) != null ? c.getString(1) : "Unknown",
                            c.getString(2)
                    });
                } while (c.moveToNext());
            }
        } finally {
            if (c != null) c.close();
        }
        return list;
    }

    /**
     * Admin: approve atau tolak pengajuan. Jika diterima, update role pengguna → Freelancer.
     */
    public boolean prosesApproval(Context context, int idPengajuan,
                                  String statusBaru, String catatanAdmin) {
        db.beginTransaction();
        try {
            // Update status pengajuan
            ContentValues cv = new ContentValues();
            cv.put("status", statusBaru);
            cv.put("catatan_admin", catatanAdmin);
            db.update("pengajuan_freelancer", cv, "id_pengajuan = ?",
                    new String[]{ String.valueOf(idPengajuan) });

            // Jika diterima, naikkan role pengguna menjadi Freelancer (id_role = 3)
            if (STATUS_DITERIMA.equals(statusBaru)) {
                Cursor c = db.rawQuery(
                        "SELECT id_pengguna FROM pengajuan_freelancer WHERE id_pengajuan = ?",
                        new String[]{ String.valueOf(idPengajuan) }
                );
                if (c != null && c.moveToFirst()) {
                    int idPengguna = c.getInt(0);
                    c.close();
                    ContentValues cvRole = new ContentValues();
                    cvRole.put("id_role", 3); // Freelancer
                    db.update("pengguna", cvRole, "id_pengguna = ?",
                            new String[]{ String.valueOf(idPengguna) });
                } else {
                    if (c != null) c.close();
                }
            }
            db.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            db.endTransaction();
        }
    }

    /**
     * Ambil pengajuan milik satu pengguna.
     */
    public PengajuanFreelancer getByPengguna(int idPengguna) {
        Cursor c = null;
        try {
            c = db.rawQuery(
                    "SELECT * FROM pengajuan_freelancer WHERE id_pengguna = ? " +
                            "ORDER BY id_pengajuan DESC LIMIT 1",
                    new String[]{ String.valueOf(idPengguna) }
            );
            if (c != null && c.moveToFirst()) return cursorToPengajuan(c);
        } finally {
            if (c != null) c.close();
        }
        return null;
    }

    public int countByStatus(String status) {
        Cursor c = null;
        try {
            c = db.rawQuery(
                    "SELECT COUNT(*) FROM pengajuan_freelancer WHERE status = ?",
                    new String[]{ status }
            );
            if (c != null && c.moveToFirst()) return c.getInt(0);
        } finally {
            if (c != null) c.close();
        }
        return 0;
    }

    // ========== HELPER ==========

    private boolean hasPengajuanAktif(int idPengguna) {
        Cursor c = null;
        try {
            c = db.rawQuery(
                    "SELECT id_pengajuan FROM pengajuan_freelancer " +
                            "WHERE id_pengguna = ? AND status IN ('Menunggu','Diterima') LIMIT 1",
                    new String[]{ String.valueOf(idPengguna) }
            );
            return c != null && c.getCount() > 0;
        } finally {
            if (c != null) c.close();
        }
    }

    private PengajuanFreelancer cursorToPengajuan(Cursor c) {
        return new PengajuanFreelancer(
                c.getInt(c.getColumnIndexOrThrow("id_pengajuan")),
                c.getInt(c.getColumnIndexOrThrow("id_pengguna")),
                c.getString(c.getColumnIndexOrThrow("nik")),
                c.getString(c.getColumnIndexOrThrow("deskripsi")),
                c.getString(c.getColumnIndexOrThrow("status")),
                c.getString(c.getColumnIndexOrThrow("catatan_admin")),
                c.getString(c.getColumnIndexOrThrow("tanggal_pengajuan"))
        );
    }

    public void close() {
        if (db != null && db.isOpen()) db.close();
    }

    public SQLiteDatabase getDb() { return db; }
}