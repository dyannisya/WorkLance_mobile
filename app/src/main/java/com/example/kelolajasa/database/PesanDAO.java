package com.example.kelolajasa.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.kelolajasa.model.ChatListItem;
import com.example.kelolajasa.model.Pesan;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PesanDAO {

    private final DatabaseHelper dbHelper;
    private final SQLiteDatabase db;

    public PesanDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    // =========================================================
    // INSERT
    // =========================================================

    /**
     * Simpan pesan baru. Waktu otomatis dari sistem.
     * @return id pesan baru, atau -1 jika gagal
     */
    public long insert(int idBooking, int idPengirim, int idPenerima, String isi) {
        String waktu = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                .format(new Date());
        ContentValues cv = new ContentValues();
        cv.put("id_booking",  idBooking);
        cv.put("id_pengirim", idPengirim);
        cv.put("id_penerima", idPenerima);
        cv.put("isi_pesan",   isi.trim());
        cv.put("waktu_kirim", waktu);
        cv.put("dibaca", 0);
        return db.insert("pesan", null, cv);
    }

    // =========================================================
    // READ — CHAT ROOM
    // =========================================================

    /**
     * Ambil semua pesan dalam satu booking, urut dari terlama.
     */
    public List<Pesan> getByBooking(int idBooking) {
        List<Pesan> list = new ArrayList<>();
        Cursor c = null;
        try {
            c = db.rawQuery(
                    "SELECT * FROM pesan WHERE id_booking = ? ORDER BY id_pesan ASC",
                    new String[]{ String.valueOf(idBooking) }
            );
            if (c != null && c.moveToFirst()) {
                do { list.add(cursorToPesan(c)); } while (c.moveToNext());
            }
        } finally {
            if (c != null) c.close();
        }
        return list;
    }

    // =========================================================
    // READ — DAFTAR CHAT (list percakapan)
    // =========================================================

    /**
     * Daftar chat untuk User (sebagai pembeli/booker).
     * Menampilkan semua booking yang dia miliki beserta pesan terakhirnya.
     */
    public List<ChatListItem> getChatListAsUser(int idPengguna) {
        List<ChatListItem> list = new ArrayList<>();
        // Query: join booking → layanan → freelancer (pf) → pesan terakhir
        String sql =
                "SELECT b.id_booking, " +
                        "  COALESCE(pf.id_pengguna, 0) AS id_lawan, " +
                        "  COALESCE(pf.nama_pengguna, 'Freelancer') AS nama_lawan, " +
                        "  COALESCE(l.namajasa, 'Layanan #' || b.id_layanan) AS nama_layanan, " +
                        "  COALESCE(pm_last.isi_pesan, 'Mulai percakapan...') AS pesan_terakhir, " +
                        "  COALESCE(pm_last.waktu_kirim, b.tanggal_booking || ' 00:00:00') AS waktu_terakhir, " +
                        "  COUNT(CASE WHEN pm_unread.dibaca = 0 AND pm_unread.id_penerima = ? THEN 1 END) AS unread " +
                        "FROM booking b " +
                        "LEFT JOIN layanan l    ON b.id_layanan  = l.id_layanan " +
                        "LEFT JOIN pengguna pf  ON l.id_pengguna = pf.id_pengguna " +
                        "LEFT JOIN pesan pm_last ON pm_last.id_pesan = (" +
                        "  SELECT MAX(p2.id_pesan) FROM pesan p2 WHERE p2.id_booking = b.id_booking) " +
                        "LEFT JOIN pesan pm_unread ON pm_unread.id_booking = b.id_booking " +
                        "WHERE b.id_pengguna = ? " +
                        "GROUP BY b.id_booking " +
                        "ORDER BY waktu_terakhir DESC";
        Cursor c = null;
        try {
            String uid = String.valueOf(idPengguna);
            c = db.rawQuery(sql, new String[]{ uid, uid });
            if (c != null && c.moveToFirst()) {
                do { list.add(cursorToChatListItem(c)); } while (c.moveToNext());
            }
        } finally {
            if (c != null) c.close();
        }
        return list;
    }

    /**
     * Daftar chat untuk Freelancer (sebagai penyedia jasa).
     * Menampilkan semua booking yang masuk untuk layanannya.
     */
    public List<ChatListItem> getChatListAsFreelancer(int idFreelancer) {
        List<ChatListItem> list = new ArrayList<>();
        String sql =
                "SELECT b.id_booking, " +
                        "  COALESCE(pc.id_pengguna, 0) AS id_lawan, " +
                        "  COALESCE(pc.nama_pengguna, 'Client') AS nama_lawan, " +
                        "  COALESCE(l.namajasa, 'Layanan #' || b.id_layanan) AS nama_layanan, " +
                        "  COALESCE(pm_last.isi_pesan, 'Mulai percakapan...') AS pesan_terakhir, " +
                        "  COALESCE(pm_last.waktu_kirim, b.tanggal_booking || ' 00:00:00') AS waktu_terakhir, " +
                        "  COUNT(CASE WHEN pm_unread.dibaca = 0 AND pm_unread.id_penerima = ? THEN 1 END) AS unread " +
                        "FROM booking b " +
                        "LEFT JOIN layanan l    ON b.id_layanan  = l.id_layanan " +
                        "LEFT JOIN pengguna pc  ON b.id_pengguna = pc.id_pengguna " +
                        "LEFT JOIN pesan pm_last ON pm_last.id_pesan = (" +
                        "  SELECT MAX(p2.id_pesan) FROM pesan p2 WHERE p2.id_booking = b.id_booking) " +
                        "LEFT JOIN pesan pm_unread ON pm_unread.id_booking = b.id_booking " +
                        "WHERE l.id_pengguna = ? " +
                        "GROUP BY b.id_booking " +
                        "ORDER BY waktu_terakhir DESC";
        Cursor c = null;
        try {
            String uid = String.valueOf(idFreelancer);
            c = db.rawQuery(sql, new String[]{ uid, uid });
            if (c != null && c.moveToFirst()) {
                do { list.add(cursorToChatListItem(c)); } while (c.moveToNext());
            }
        } finally {
            if (c != null) c.close();
        }
        return list;
    }

    // =========================================================
    // UPDATE
    // =========================================================

    /**
     * Tandai semua pesan dalam satu booking yang ditujukan ke idPenerima sebagai "dibaca".
     */
    public int tandaiBaca(int idBooking, int idPenerima) {
        ContentValues cv = new ContentValues();
        cv.put("dibaca", 1);
        return db.update("pesan", cv,
                "id_booking = ? AND id_penerima = ? AND dibaca = 0",
                new String[]{ String.valueOf(idBooking), String.valueOf(idPenerima) });
    }

    /**
     * Hitung total pesan belum dibaca untuk seorang pengguna.
     * Dipakai untuk badge notifikasi di bottom nav.
     */
    public int countUnread(int idPenerima) {
        Cursor c = null;
        try {
            c = db.rawQuery(
                    "SELECT COUNT(*) FROM pesan WHERE id_penerima = ? AND dibaca = 0",
                    new String[]{ String.valueOf(idPenerima) }
            );
            if (c != null && c.moveToFirst()) return c.getInt(0);
        } finally {
            if (c != null) c.close();
        }
        return 0;
    }

    // =========================================================
    // PRIVATE HELPERS
    // =========================================================

    private Pesan cursorToPesan(Cursor c) {
        return new Pesan(
                c.getInt(c.getColumnIndexOrThrow("id_pesan")),
                c.getInt(c.getColumnIndexOrThrow("id_booking")),
                c.getInt(c.getColumnIndexOrThrow("id_pengirim")),
                c.getInt(c.getColumnIndexOrThrow("id_penerima")),
                c.getString(c.getColumnIndexOrThrow("isi_pesan")),
                c.getString(c.getColumnIndexOrThrow("waktu_kirim")),
                c.getInt(c.getColumnIndexOrThrow("dibaca")) == 1
        );
    }

    private ChatListItem cursorToChatListItem(Cursor c) {
        return new ChatListItem(
                c.getInt(c.getColumnIndexOrThrow("id_booking")),
                c.getInt(c.getColumnIndexOrThrow("id_lawan")),
                c.getString(c.getColumnIndexOrThrow("nama_lawan")),
                c.getString(c.getColumnIndexOrThrow("nama_layanan")),
                c.getString(c.getColumnIndexOrThrow("pesan_terakhir")),
                c.getString(c.getColumnIndexOrThrow("waktu_terakhir")),
                c.getInt(c.getColumnIndexOrThrow("unread"))
        );
    }

    public void close() {
        if (db != null && db.isOpen()) db.close();
    }
}