package com.example.kelolajasa.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.kelolajasa.model.Layanan;
import com.example.kelolajasa.model.LayananDisplay;

import java.util.ArrayList;
import java.util.List;

public class LayananDAO {

    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    public LayananDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    // ============================================================
    // QUERY HELPER — SQL dasar dengan JOIN lengkap
    // PERBAIKAN BUG #1: l.tarif di-alias sebagai "harga" supaya
    //   cursorToDisplay() bisa membacanya dengan nama kolom "harga"
    // PERBAIKAN BUG #3: JOIN ulasan sekarang melalui tabel booking
    //   (ulasan tidak punya kolom id_layanan di skema DB)
    // ============================================================
    private static final String BASE_DISPLAY_SQL =
            "SELECT l.id_layanan, l.id_pengguna, l.namajasa, " +
                    "  COALESCE(p.nama_pengguna, 'Unknown') AS nama_freelancer, " +
                    "  COALESCE(j.nama_jasa, '-') AS nama_kategori, " +
                    "  l.deskripsi, " +
                    "  COALESCE(l.tarif, 0) AS harga, " +   // ← FIX: kolom tarif, alias harga
                    "  COALESCE(s.nama_satuan, 'Project') AS nama_satuan, " +
                    "  COALESCE(kab.nama_kabupaten, '-') AS lokasi, " +
                    "  COALESCE(AVG(u.rating), 0.0) AS avg_rating, " +
                    "  COUNT(u.id_ulasan) AS total_ulasan " +
                    "FROM layanan l " +
                    "LEFT JOIN pengguna p ON l.id_pengguna = p.id_pengguna " +
                    "LEFT JOIN jasa j ON l.id_jasa = j.id_jasa " +
                    "LEFT JOIN satuan s ON l.id_satuan = s.id_satuan " +
                    "LEFT JOIN kabupaten kab ON p.id_kabupaten = kab.id_kabupaten " +
                    // FIX #3: ulasan diakses via booking, bukan langsung ke layanan
                    "LEFT JOIN booking bk ON bk.id_layanan = l.id_layanan " +
                    "LEFT JOIN ulasan u ON u.id_booking = bk.id_booking ";

    /**
     * Ambil LayananDisplay lengkap untuk satu layanan.
     * Dipakai oleh PemesananActivity.
     */
    public LayananDisplay getDisplayById(int idLayanan) {
        String sql = BASE_DISPLAY_SQL +
                "WHERE l.id_layanan = ? " +
                "GROUP BY l.id_layanan";
        Cursor c = null;
        try {
            c = db.rawQuery(sql, new String[]{ String.valueOf(idLayanan) });
            if (c != null && c.moveToFirst()) {
                return cursorToDisplay(c);
            }
        } finally {
            if (c != null) c.close();
        }
        return null;
    }

    /**
     * Ambil semua layanan aktif, diurutkan berdasarkan rating.
     * Dipakai halaman browse/dashboard.
     */
    public List<LayananDisplay> getAllDisplay() {
        List<LayananDisplay> list = new ArrayList<>();
        String sql = BASE_DISPLAY_SQL +
                "GROUP BY l.id_layanan " +
                "ORDER BY avg_rating DESC";
        Cursor c = null;
        try {
            c = db.rawQuery(sql, null);
            if (c != null && c.moveToFirst()) {
                do { list.add(cursorToDisplay(c)); } while (c.moveToNext());
            }
        } finally {
            if (c != null) c.close();
        }
        return list;
    }

    /**
     * Cari layanan berdasarkan kata kunci (nama layanan, jasa, atau freelancer).
     * Dipakai CariActivity.
     */
    public List<LayananDisplay> searchDisplay(String keyword) {
        List<LayananDisplay> list = new ArrayList<>();
        String like = "%" + keyword + "%";
        String sql = BASE_DISPLAY_SQL +
                "WHERE l.namajasa LIKE ? " +
                "   OR j.nama_jasa LIKE ? " +
                "   OR p.nama_pengguna LIKE ? " +
                "   OR kab.nama_kabupaten LIKE ? " +
                "GROUP BY l.id_layanan " +
                "ORDER BY avg_rating DESC";
        Cursor c = null;
        try {
            c = db.rawQuery(sql, new String[]{ like, like, like, like });
            if (c != null && c.moveToFirst()) {
                do { list.add(cursorToDisplay(c)); } while (c.moveToNext());
            }
        } finally {
            if (c != null) c.close();
        }
        return list;
    }

    public List<LayananDisplay> getByKategori(int idKategori) {
        List<LayananDisplay> list = new ArrayList<>();
        String sql =
                BASE_DISPLAY_SQL +
                        "WHERE j.id_kategori = ? " +
                        "GROUP BY l.id_layanan " +
                        "ORDER BY avg_rating DESC";

        Cursor c = null;
        try {
            c = db.rawQuery(sql, new String[]{String.valueOf(idKategori)});
            if (c != null && c.moveToFirst()) {
                do {
                    list.add(
                            cursorToDisplay(c)
                    );
                } while (c.moveToNext());
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return list;
    }

    /**
     * Cari layanan berdasarkan id_jasa (kategori).
     * Dipakai DashboardActivity saat user klik chip kategori.
     */
    public List<LayananDisplay> getByJasa(int idJasa) {
        List<LayananDisplay> list = new ArrayList<>();
        String sql = BASE_DISPLAY_SQL +
                "WHERE l.id_jasa = ? " +
                "GROUP BY l.id_layanan " +
                "ORDER BY avg_rating DESC";
        Cursor c = null;
        try {
            c = db.rawQuery(sql, new String[]{ String.valueOf(idJasa) });
            if (c != null && c.moveToFirst()) {
                do { list.add(cursorToDisplay(c)); } while (c.moveToNext());
            }
        } finally {
            if (c != null) c.close();
        }
        return list;
    }

    /**
     * Ambil layanan milik satu freelancer (raw Layanan model).
     * Dipakai KelolaJasaFreelancer2Activity.
     */
    public List<Layanan> getByFreelancer(int idPengguna) {
        List<Layanan> list = new ArrayList<>();
        Cursor c = null;
        try {
            c = db.rawQuery(
                    "SELECT * FROM layanan WHERE id_pengguna = ? ORDER BY id_layanan DESC",
                    new String[]{ String.valueOf(idPengguna) }
            );
            if (c != null && c.moveToFirst()) {
                do { list.add(cursorToLayanan(c)); } while (c.moveToNext());
            }
        } finally {
            if (c != null) c.close();
        }
        return list;
    }

    /**
     * Tambah layanan baru (oleh freelancer).
     * PERBAIKAN: cv.put("tarif", ...) — bukan "harga"
     * @return id layanan baru, atau -1 jika gagal
     */
    public long insert(int idPengguna, int idJasa, String namaJasa,
                       String deskripsi, double tarif, int idSatuan) {
        ContentValues cv = new ContentValues();
        cv.put("id_pengguna", idPengguna);
        cv.put("id_jasa", idJasa);
        cv.put("namajasa", namaJasa);
        cv.put("deskripsi", deskripsi);
        cv.put("tarif", (long) tarif);   // ← FIX: kolom "tarif", bukan "harga"
        cv.put("id_satuan", idSatuan);
        return db.insert("layanan", null, cv);
    }

    /**
     * Update layanan yang sudah ada.
     * PERBAIKAN: cv.put("tarif", ...) — bukan "harga"
     */
    public int update(int idLayanan, int idJasa, String namaJasa,
                      String deskripsi, double tarif, int idSatuan) {
        ContentValues cv = new ContentValues();
        cv.put("id_jasa", idJasa);
        cv.put("namajasa", namaJasa);
        cv.put("deskripsi", deskripsi);
        cv.put("tarif", (long) tarif);   // ← FIX: kolom "tarif", bukan "harga"
        cv.put("id_satuan", idSatuan);
        return db.update("layanan", cv, "id_layanan = ?",
                new String[]{ String.valueOf(idLayanan) });
    }

    /**
     * Hapus layanan (oleh freelancer atau admin).
     */
    public int delete(int idLayanan) {
        return db.delete("layanan", "id_layanan = ?",
                new String[]{ String.valueOf(idLayanan) });
    }

    // ==========================
    // COUNT untuk admin dashboard
    // ==========================

    public int countAll() {
        Cursor c = null;
        try {
            c = db.rawQuery("SELECT COUNT(*) FROM layanan", null);
            if (c != null && c.moveToFirst()) return c.getInt(0);
        } finally {
            if (c != null) c.close();
        }
        return 0;
    }

    // ========== PRIVATE HELPERS ==========

    /**
     * Mapping cursor (dari query JOIN) ke LayananDisplay.
     * Kolom "harga" tersedia karena query menggunakan alias "COALESCE(l.tarif,0) AS harga".
     */
    private LayananDisplay cursorToDisplay(Cursor c) {
        return new LayananDisplay(
                c.getInt(c.getColumnIndexOrThrow("id_layanan")),
                c.getInt(c.getColumnIndexOrThrow("id_pengguna")),
                c.getString(c.getColumnIndexOrThrow("namajasa")),
                c.getString(c.getColumnIndexOrThrow("nama_freelancer")),
                c.getString(c.getColumnIndexOrThrow("nama_kategori")),
                c.getString(c.getColumnIndexOrThrow("deskripsi")),
                c.getDouble(c.getColumnIndexOrThrow("harga")),
                c.getString(c.getColumnIndexOrThrow("nama_satuan")),
                c.getString(c.getColumnIndexOrThrow("lokasi")),
                c.getFloat(c.getColumnIndexOrThrow("avg_rating")),
                c.getInt(c.getColumnIndexOrThrow("total_ulasan"))
        );
    }

    /**
     * Mapping cursor (dari SELECT * FROM layanan) ke model Layanan.
     * PERBAIKAN BUG #2: urutan argumen diperbaiki sesuai konstruktor
     *   Layanan(idLayanan, idPengguna, idJasa, idSatuan, tarif, deskripsi, namaJasa)
     */
    private Layanan cursorToLayanan(Cursor c) {
        return new Layanan(
                c.getInt(c.getColumnIndexOrThrow("id_layanan")),
                c.getInt(c.getColumnIndexOrThrow("id_pengguna")),
                c.getInt(c.getColumnIndexOrThrow("id_jasa")),
                c.getInt(c.getColumnIndexOrThrow("id_satuan")),   // ← posisi ke-4: idSatuan
                c.getInt(c.getColumnIndexOrThrow("tarif")),       // ← posisi ke-5: tarif
                c.getString(c.getColumnIndexOrThrow("deskripsi")),// ← posisi ke-6: deskripsi
                c.getString(c.getColumnIndexOrThrow("namajasa"))  // ← posisi ke-7: namaJasa
        );
    }

    public void close() {
        if (db != null && db.isOpen()) db.close();
    }

    public SQLiteDatabase getDb() { return db; }

}