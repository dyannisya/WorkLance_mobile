package com.example.kelolajasa.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.kelolajasa.model.Booking;
import com.example.kelolajasa.model.BookingDisplay;
import com.example.kelolajasa.model.RiwayatDisplay;

import java.util.ArrayList;
import java.util.List;

public class BookingDAO {

    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    // Konstanta status booking — gunakan ini di seluruh app, jangan hardcode string
    public static final String STATUS_MENUNGGU   = "Menunggu";
    public static final String STATUS_DIPROSES   = "Diproses";
    public static final String STATUS_SELESAI    = "Selesai";
    public static final String STATUS_DIBATALKAN = "Dibatalkan";

    public BookingDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    /**
     * Tambah booking baru. Status awal = "Menunggu".
     * @return id booking baru, atau -1 jika gagal
     */
    public long insert(int idPengguna, int idLayanan,
                       String tanggalBooking, String alamatBooking, String catatan) {
        ContentValues cv = new ContentValues();
        cv.put("id_pengguna", idPengguna);
        cv.put("id_layanan", idLayanan);
        cv.put("tanggal_booking", tanggalBooking);
        cv.put("alamat_booking", alamatBooking);
        cv.put("catatan", catatan);
        cv.put("status_booking", STATUS_MENUNGGU);
        return db.insert("booking", null, cv);
    }

    /**
     * Ambil semua booking milik satu pengguna (object Booking mentah).
     */
    public List<Booking> getByPengguna(int idPengguna) {
        List<Booking> list = new ArrayList<>();
        Cursor c = null;
        try {
            c = db.rawQuery(
                    "SELECT * FROM booking WHERE id_pengguna = ? ORDER BY id_booking DESC",
                    new String[]{ String.valueOf(idPengguna) }
            );
            if (c != null && c.moveToFirst()) {
                do { list.add(cursorToBooking(c)); } while (c.moveToNext());
            }
        } finally {
            if (c != null) c.close();
        }
        return list;
    }

    /**
     * Ambil semua booking (untuk admin — list lengkap).
     */
    public List<Booking> getAll() {
        List<Booking> list = new ArrayList<>();
        Cursor c = null;
        try {
            c = db.rawQuery("SELECT * FROM booking ORDER BY id_booking DESC", null);
            if (c != null && c.moveToFirst()) {
                do { list.add(cursorToBooking(c)); } while (c.moveToNext());
            }
        } finally {
            if (c != null) c.close();
        }
        return list;
    }

    /**
     * Ambil booking terbaru dengan JOIN (untuk tabel admin dashboard).
     */
    public List<BookingDisplay> getRecentWithDetails(int limit) {
        List<BookingDisplay> list = new ArrayList<>();
        Cursor c = null;
        try {
            String sql =
                    "SELECT b.id_booking, " +
                            "  COALESCE(p.nama_pengguna, 'Unknown') AS nama_pengguna, " +
                            "  COALESCE(l.namajasa, 'Layanan #' || b.id_layanan) AS namajasa, " +
                            "  b.status_booking, b.tanggal_booking " +
                            "FROM booking b " +
                            "LEFT JOIN pengguna p ON b.id_pengguna = p.id_pengguna " +
                            "LEFT JOIN layanan l ON b.id_layanan = l.id_layanan " +
                            "ORDER BY b.id_booking DESC " +
                            "LIMIT ?";
            c = db.rawQuery(sql, new String[]{ String.valueOf(limit) });
            if (c != null && c.moveToFirst()) {
                do {
                    list.add(new BookingDisplay(
                            c.getInt(0), c.getString(1), c.getString(2),
                            c.getString(3), c.getString(4)
                    ));
                } while (c.moveToNext());
            }
        } finally {
            if (c != null) c.close();
        }
        return list;
    }

    /**
     * Ambil riwayat booking milik satu pengguna (User view).
     * JOIN layanan + freelancer + satuan untuk tampilan RiwayatPesananActivity.
     *
     * PERBAIKAN BUG #4: l.harga diganti l.tarif
     */
    public List<RiwayatDisplay> getByPenggunaWithDetails(int idPengguna) {
        List<RiwayatDisplay> list = new ArrayList<>();
        String sql =
                "SELECT b.id_booking, " +
                        "  COALESCE(pf.nama_pengguna, 'Unknown') AS nama_freelancer, " +
                        "  COALESCE(l.namajasa, 'Layanan #' || b.id_layanan) AS nama_layanan, " +
                        "  COALESCE(l.tarif, 0) AS harga, " +           // ← FIX: tarif bukan harga
                        "  COALESCE(s.nama_satuan, 'Project') AS nama_satuan, " +
                        "  b.status_booking, b.tanggal_booking " +
                        "FROM booking b " +
                        "LEFT JOIN layanan l ON b.id_layanan = l.id_layanan " +
                        "LEFT JOIN pengguna pf ON l.id_pengguna = pf.id_pengguna " +
                        "LEFT JOIN satuan s ON l.id_satuan = s.id_satuan " +
                        "WHERE b.id_pengguna = ? " +
                        "ORDER BY b.id_booking DESC";
        Cursor c = null;
        try {
            c = db.rawQuery(sql, new String[]{ String.valueOf(idPengguna) });
            if (c != null && c.moveToFirst()) {
                do {
                    list.add(new RiwayatDisplay(
                            c.getInt(0), c.getString(1), c.getString(2),
                            c.getDouble(3), c.getString(4),
                            c.getString(5), c.getString(6)
                    ));
                } while (c.moveToNext());
            }
        } finally {
            if (c != null) c.close();
        }
        return list;
    }

    /**
     * Ambil semua booking masuk untuk satu freelancer (Freelancer view).
     * Dipakai RiwayatPesananActivity saat isFreelancerView = true.
     *
     * PERBAIKAN BUG #4: l.harga diganti l.tarif
     */
    public List<RiwayatDisplay> getIncomingByFreelancer(int idFreelancer) {
        List<RiwayatDisplay> list = new ArrayList<>();
        String sql =
                "SELECT b.id_booking, " +
                        "  COALESCE(pc.nama_pengguna, 'Unknown') AS nama_client, " +
                        "  COALESCE(l.namajasa, '-') AS nama_layanan, " +
                        "  COALESCE(l.tarif, 0) AS harga, " +           // ← FIX: tarif bukan harga
                        "  COALESCE(s.nama_satuan, 'Project') AS nama_satuan, " +
                        "  b.status_booking, b.tanggal_booking " +
                        "FROM booking b " +
                        "LEFT JOIN layanan l ON b.id_layanan = l.id_layanan " +
                        "LEFT JOIN pengguna pc ON b.id_pengguna = pc.id_pengguna " +
                        "LEFT JOIN satuan s ON l.id_satuan = s.id_satuan " +
                        "WHERE l.id_pengguna = ? " +
                        "ORDER BY b.id_booking DESC";
        Cursor c = null;
        try {
            c = db.rawQuery(sql, new String[]{ String.valueOf(idFreelancer) });
            if (c != null && c.moveToFirst()) {
                do {
                    list.add(new RiwayatDisplay(
                            c.getInt(0), c.getString(1), c.getString(2),
                            c.getDouble(3), c.getString(4),
                            c.getString(5), c.getString(6)
                    ));
                } while (c.moveToNext());
            }
        } finally {
            if (c != null) c.close();
        }
        return list;
    }

    /**
     * Update status booking (dipakai freelancer & admin).
     */
    public int updateStatus(int idBooking, String statusBaru) {
        ContentValues cv = new ContentValues();
        cv.put("status_booking", statusBaru);
        return db.update("booking", cv, "id_booking = ?",
                new String[]{ String.valueOf(idBooking) });
    }

    /**
     * Hapus booking (admin).
     */
    public int delete(int idBooking) {
        return db.delete("booking", "id_booking = ?",
                new String[]{ String.valueOf(idBooking) });
    }

    // ===== STATISTIK UNTUK ADMIN DASHBOARD =====

    public int countAll() {
        return countQuery("SELECT COUNT(*) FROM booking");
    }

    public int countByStatus(String status) {
        Cursor c = null;
        try {
            c = db.rawQuery(
                    "SELECT COUNT(*) FROM booking WHERE status_booking = ?",
                    new String[]{ status }
            );
            if (c != null && c.moveToFirst()) return c.getInt(0);
        } finally {
            if (c != null) c.close();
        }
        return 0;
    }

    public int countPenggunaByRole(int idRole) {
        Cursor c = null;
        try {
            c = db.rawQuery(
                    "SELECT COUNT(*) FROM pengguna WHERE id_role = ?",
                    new String[]{ String.valueOf(idRole) }
            );
            if (c != null && c.moveToFirst()) return c.getInt(0);
        } finally {
            if (c != null) c.close();
        }
        return 0;
    }

    // ========== PRIVATE HELPERS ==========

    private int countQuery(String sql) {
        Cursor c = null;
        try {
            c = db.rawQuery(sql, null);
            if (c != null && c.moveToFirst()) return c.getInt(0);
        } finally {
            if (c != null) c.close();
        }
        return 0;
    }

    private Booking cursorToBooking(Cursor c) {
        return new Booking(
                c.getInt(c.getColumnIndexOrThrow("id_booking")),
                c.getInt(c.getColumnIndexOrThrow("id_pengguna")),
                c.getInt(c.getColumnIndexOrThrow("id_layanan")),
                c.getString(c.getColumnIndexOrThrow("tanggal_booking")),
                c.getString(c.getColumnIndexOrThrow("alamat_booking")),
                c.getString(c.getColumnIndexOrThrow("catatan")),
                c.getString(c.getColumnIndexOrThrow("status_booking"))
        );
    }

    public void close() {
        if (db != null && db.isOpen()) db.close();
    }

    public SQLiteDatabase getDb() { return db; }
}