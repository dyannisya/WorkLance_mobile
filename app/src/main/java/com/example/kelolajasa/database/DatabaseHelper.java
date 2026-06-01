package com.example.kelolajasa.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.content.Context;
import android.content.ContentValues;
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "worklance.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // =========================
    // ROLE
    // =========================

    private static final String CREATE_ROLE =
            "CREATE TABLE role (" +
                    "id_role INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "nama_role TEXT" +
                    ");";

    // =========================
    // PROVINSI
    // =========================

    private static final String CREATE_PROVINSI =
            "CREATE TABLE provinsi (" +
                    "id_provinsi INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "nama_provinsi TEXT" +
                    ");";

    // =========================
    // KABUPATEN
    // =========================

    private static final String CREATE_KABUPATEN =
            "CREATE TABLE kabupaten (" +
                    "id_kabupaten INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "id_provinsi INTEGER," +
                    "nama_kabupaten TEXT" +
                    ");";

    // =========================
    // KECAMATAN
    // =========================

    private static final String CREATE_KECAMATAN =
            "CREATE TABLE kecamatan (" +
                    "id_kecamatan INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "id_kabupaten INTEGER," +
                    "nama_kecamatan TEXT" +
                    ");";

    // =========================
    // DESA
    // =========================

    private static final String CREATE_DESA =
            "CREATE TABLE desa (" +
                    "id_desa INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "id_kecamatan INTEGER," +
                    "nama_desa TEXT" +
                    ");";

    // =========================
    // PENGGUNA
    // =========================

    private static final String CREATE_PENGGUNA =
            "CREATE TABLE pengguna (" +
                    "id_pengguna INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "id_role INTEGER," +
                    "username TEXT," +
                    "nama_pengguna TEXT," +
                    "tanggal_lahir TEXT," +
                    "no_telp TEXT," +
                    "email TEXT," +
                    "password TEXT," +
                    "id_provinsi INTEGER," +
                    "id_kabupaten INTEGER," +
                    "id_kecamatan INTEGER," +
                    "id_desa INTEGER," +
                    "alamat_lengkap TEXT," +
                    "foto_profil TEXT" +
                    ");";

    // =========================
    // PENGAJUAN FREELANCER
    // =========================

    private static final String CREATE_PENGAJUAN =
            "CREATE TABLE pengajuan_freelancer (" +
                    "id_pengajuan INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "id_pengguna INTEGER," +
                    "nik TEXT," +
                    "deskripsi TEXT," +
                    "status TEXT," +
                    "catatan_admin TEXT," +
                    "tanggal_pengajuan TEXT" +
                    ");";

    // =========================
    // KATEGORI
    // =========================

    private static final String CREATE_KATEGORI =
            "CREATE TABLE kategori (" +
                    "id_kategori INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "nama_kategori TEXT" +
                    ");";

    // =========================
    // JASA
    // =========================

    private static final String CREATE_JASA =
            "CREATE TABLE jasa (" +
                    "id_jasa INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "id_kategori INTEGER," +
                    "nama_jasa TEXT" +
                    ");";

    // =========================
    // SATUAN
    // =========================

    private static final String CREATE_SATUAN =
            "CREATE TABLE satuan (" +
                    "id_satuan INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "nama_satuan TEXT" +
                    ");";

    // =========================
    // LAYANAN
    // =========================

    private static final String CREATE_LAYANAN =
            "CREATE TABLE layanan (" +
                    "id_layanan INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "id_pengguna INTEGER," +
                    "id_jasa INTEGER," +
                    "id_satuan INTEGER," +
                    "tarif INTEGER," +
                    "deskripsi TEXT," +
                    "namajasa TEXT" +
                    ");";

    // =========================
    // GAMBAR LAYANAN
    // =========================

    private static final String CREATE_GAMBAR_LAYANAN =
            "CREATE TABLE gambar_layanan (" +
                    "id_gambar INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "id_layanan INTEGER," +
                    "file_gambar TEXT" +
                    ");";

    // =========================
    // BOOKING
    // =========================

    private static final String CREATE_BOOKING =
            "CREATE TABLE booking (" +
                    "id_booking INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "id_pengguna INTEGER," +
                    "id_layanan INTEGER," +
                    "tanggal_booking TEXT," +
                    "alamat_booking TEXT," +
                    "catatan TEXT," +
                    "status_booking TEXT" +
                    ");";

    // =========================
    // ULASAN
    // =========================

    private static final String CREATE_ULASAN =
            "CREATE TABLE ulasan (" +
                    "id_ulasan INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "id_booking INTEGER," +
                    "id_pengguna INTEGER," +
                    "rating INTEGER," +
                    "komentar TEXT," +
                    "tanggal_ulasan TEXT" +
                    ");";

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_ROLE);
        db.execSQL(CREATE_PROVINSI);
        db.execSQL(CREATE_KABUPATEN);
        db.execSQL(CREATE_KECAMATAN);
        db.execSQL(CREATE_DESA);

        db.execSQL(CREATE_PENGGUNA);
        db.execSQL(CREATE_PENGAJUAN);

        db.execSQL(CREATE_KATEGORI);
        db.execSQL(CREATE_JASA);
        db.execSQL(CREATE_SATUAN);

        db.execSQL(CREATE_LAYANAN);
        db.execSQL(CREATE_GAMBAR_LAYANAN);

        db.execSQL(CREATE_BOOKING);
        db.execSQL(CREATE_ULASAN);

        insertDefaultData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db,
                          int oldVersion,
                          int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS ulasan");
        db.execSQL("DROP TABLE IF EXISTS booking");
        db.execSQL("DROP TABLE IF EXISTS gambar_layanan");
        db.execSQL("DROP TABLE IF EXISTS layanan");
        db.execSQL("DROP TABLE IF EXISTS satuan");
        db.execSQL("DROP TABLE IF EXISTS jasa");
        db.execSQL("DROP TABLE IF EXISTS kategori");
        db.execSQL("DROP TABLE IF EXISTS pengajuan_freelancer");
        db.execSQL("DROP TABLE IF EXISTS pengguna");
        db.execSQL("DROP TABLE IF EXISTS desa");
        db.execSQL("DROP TABLE IF EXISTS kecamatan");
        db.execSQL("DROP TABLE IF EXISTS kabupaten");
        db.execSQL("DROP TABLE IF EXISTS provinsi");
        db.execSQL("DROP TABLE IF EXISTS role");

        onCreate(db);
    }

    private void insertDefaultData(SQLiteDatabase db) {

        // ROLE
        db.execSQL("INSERT INTO role VALUES (1,'Admin')");
        db.execSQL("INSERT INTO role VALUES (2,'User')");
        db.execSQL("INSERT INTO role VALUES (3,'Freelancer')");

        // PROVINSI
        db.execSQL("INSERT INTO provinsi VALUES (1,'Jawa Timur')");

        // KABUPATEN
        db.execSQL("INSERT INTO kabupaten VALUES (1,1,'Surabaya')");
        db.execSQL("INSERT INTO kabupaten VALUES (2,1,'Malang')");
        db.execSQL("INSERT INTO kabupaten VALUES (3,1,'Sidoarjo')");
        db.execSQL("INSERT INTO kabupaten VALUES (4,1,'Gresik')");
        db.execSQL("INSERT INTO kabupaten VALUES (5,1,'Mojokerto')");
        db.execSQL("INSERT INTO kabupaten VALUES (6,1,'Jember')");
        db.execSQL("INSERT INTO kabupaten VALUES (7,1,'Kediri')");
        db.execSQL("INSERT INTO kabupaten VALUES (8,1,'Blitar')");

        // KECAMATAN
        db.execSQL("INSERT INTO kecamatan VALUES (1,1,'Sukolilo')");
        db.execSQL("INSERT INTO kecamatan VALUES (2,1,'Rungkut')");
        db.execSQL("INSERT INTO kecamatan VALUES (3,1,'Tegalsari')");

        db.execSQL("INSERT INTO kecamatan VALUES (4,2,'Lowokwaru')");
        db.execSQL("INSERT INTO kecamatan VALUES (5,2,'Blimbing')");
        db.execSQL("INSERT INTO kecamatan VALUES (6,2,'Klojen')");

        db.execSQL("INSERT INTO kecamatan VALUES (7,3,'Waru')");
        db.execSQL("INSERT INTO kecamatan VALUES (8,3,'Candi')");
        db.execSQL("INSERT INTO kecamatan VALUES (9,3,'Taman')");

        db.execSQL("INSERT INTO kecamatan VALUES (10,4,'Manyar')");
        db.execSQL("INSERT INTO kecamatan VALUES (11,4,'Driyorejo')");

        db.execSQL("INSERT INTO kecamatan VALUES (12,5,'Mojosari')");
        db.execSQL("INSERT INTO kecamatan VALUES (13,5,'Ngoro')");

        db.execSQL("INSERT INTO kecamatan VALUES (14,6,'Kaliwates')");
        db.execSQL("INSERT INTO kecamatan VALUES (15,6,'Patrang')");

        db.execSQL("INSERT INTO kecamatan VALUES (16,7,'Mojoroto')");
        db.execSQL("INSERT INTO kecamatan VALUES (17,7,'Pesantren')");

        db.execSQL("INSERT INTO kecamatan VALUES (18,8,'Sananwetan')");
        db.execSQL("INSERT INTO kecamatan VALUES (19,8,'Kepanjenkidul')");

        //DESA
        db.execSQL("INSERT INTO desa VALUES (1,1,'Keputih')");
        db.execSQL("INSERT INTO desa VALUES (2,1,'Gebang Putih')");

        db.execSQL("INSERT INTO desa VALUES (3,2,'Rungkut Tengah')");
        db.execSQL("INSERT INTO desa VALUES (4,2,'Kedung Baruk')");

        db.execSQL("INSERT INTO desa VALUES (5,3,'Dr. Soetomo')");
        db.execSQL("INSERT INTO desa VALUES (6,3,'Wonorejo')");

        db.execSQL("INSERT INTO desa VALUES (7,4,'Dinoyo')");
        db.execSQL("INSERT INTO desa VALUES (8,4,'Tlogomas')");

        db.execSQL("INSERT INTO desa VALUES (9,5,'Purwodadi')");
        db.execSQL("INSERT INTO desa VALUES (10,5,'Polowijen')");

        db.execSQL("INSERT INTO desa VALUES (11,6,'Kauman')");
        db.execSQL("INSERT INTO desa VALUES (12,6,'Sukoharjo')");

        db.execSQL("INSERT INTO desa VALUES (13,7,'Wedoro')");
        db.execSQL("INSERT INTO desa VALUES (14,7,'Tambak Oso')");

        db.execSQL("INSERT INTO desa VALUES (15,8,'Gelam')");
        db.execSQL("INSERT INTO desa VALUES (16,8,'Bligo')");

        db.execSQL("INSERT INTO desa VALUES (17,9,'Sepanjang')");
        db.execSQL("INSERT INTO desa VALUES (18,9,'Kedungturi')");

        db.execSQL("INSERT INTO desa VALUES (19,10,'Manyarejo')");
        db.execSQL("INSERT INTO desa VALUES (20,10,'Suci')");

        db.execSQL("INSERT INTO desa VALUES (21,11,'Petiken')");
        db.execSQL("INSERT INTO desa VALUES (22,11,'Bambe')");

        db.execSQL("INSERT INTO desa VALUES (23,12,'Sawahan')");
        db.execSQL("INSERT INTO desa VALUES (24,12,'Mojosari')");

        db.execSQL("INSERT INTO desa VALUES (25,13,'Watesnegoro')");
        db.execSQL("INSERT INTO desa VALUES (26,13,'Lolawang')");

        db.execSQL("INSERT INTO desa VALUES (27,14,'Kebonsari')");
        db.execSQL("INSERT INTO desa VALUES (28,14,'Sempusari')");

        db.execSQL("INSERT INTO desa VALUES (29,15,'Jember Lor')");
        db.execSQL("INSERT INTO desa VALUES (30,15,'Patrang')");

        db.execSQL("INSERT INTO desa VALUES (31,16,'Campurejo')");
        db.execSQL("INSERT INTO desa VALUES (32,16,'Bandar Kidul')");

        db.execSQL("INSERT INTO desa VALUES (33,17,'Burengan')");
        db.execSQL("INSERT INTO desa VALUES (34,17,'Banjaran')");

        db.execSQL("INSERT INTO desa VALUES (35,18,'Karangtengah')");
        db.execSQL("INSERT INTO desa VALUES (36,18,'Kepanjen Lor')");

        db.execSQL("INSERT INTO desa VALUES (37,19,'Sananwetan')");
        db.execSQL("INSERT INTO desa VALUES (38,19,'Tanggung')");

        // KATEGORI
        db.execSQL("INSERT INTO kategori VALUES (1,'Desain & Kreatif')");
        db.execSQL("INSERT INTO kategori VALUES (2,'Teknisi & Perbaikan')");
        db.execSQL("INSERT INTO kategori VALUES (3,'Fotografi & Videografi')");
        db.execSQL("INSERT INTO kategori VALUES (4,'Pendidikan & Les Privat')");
        db.execSQL("INSERT INTO kategori VALUES (5,'IT & Digital')");
        db.execSQL("INSERT INTO kategori VALUES (6,'Rumah Tangga')");
        db.execSQL("INSERT INTO kategori VALUES (7,'Tukang & Konstruksi')");
        db.execSQL("INSERT INTO kategori VALUES (8,'Event & Hiburan')");

        // SATUAN
        db.execSQL("INSERT INTO satuan VALUES (1,'Unit')");
        db.execSQL("INSERT INTO satuan VALUES (2,'Jam')");
        db.execSQL("INSERT INTO satuan VALUES (3,'Paket')");
        db.execSQL("INSERT INTO satuan VALUES (4,'Hari')");
        db.execSQL("INSERT INTO satuan VALUES (5,'Project')");

        // JASA
        db.execSQL("INSERT INTO jasa VALUES (1,1,'Desain Logo')");
        db.execSQL("INSERT INTO jasa VALUES (2,1,'Desain Poster / Banner')");
        db.execSQL("INSERT INTO jasa VALUES (3,1,'Desain Konten Sosial Media')");
        db.execSQL("INSERT INTO jasa VALUES (4,1,'Editing Foto')");
        db.execSQL("INSERT INTO jasa VALUES (5,1,'Editing Video Sederhana')");

        db.execSQL("INSERT INTO jasa VALUES (6,2,'Service Alat Elektronik')");
        db.execSQL("INSERT INTO jasa VALUES (7,2,'Service AC')");
        db.execSQL("INSERT INTO jasa VALUES (8,2,'Kelistrikan Rumah')");

        db.execSQL("INSERT INTO jasa VALUES (9,3,'Foto Prewedding')");
        db.execSQL("INSERT INTO jasa VALUES (10,3,'Dokumentasi Acara')");
        db.execSQL("INSERT INTO jasa VALUES (11,3,'Foto Produk UMKM')");
        db.execSQL("INSERT INTO jasa VALUES (12,3,'Video Shooting Event')");

        db.execSQL("INSERT INTO jasa VALUES (13,4,'Les Matematika')");
        db.execSQL("INSERT INTO jasa VALUES (14,4,'Les Bahasa Inggris')");
        db.execSQL("INSERT INTO jasa VALUES (15,4,'Les SD/SMP/SMA')");
        db.execSQL("INSERT INTO jasa VALUES (16,4,'Les Mengaji')");

        db.execSQL("INSERT INTO jasa VALUES (17,5,'Pembuatan Website')");
        db.execSQL("INSERT INTO jasa VALUES (18,5,'Pembuatan Aplikasi Desktop')");
        db.execSQL("INSERT INTO jasa VALUES (19,5,'Pembuatan Aplikasi Mobile')");
        db.execSQL("INSERT INTO jasa VALUES (20,5,'UI/UX Design')");

        db.execSQL("INSERT INTO jasa VALUES (21,6,'Bersih-bersih Rumah')");
        db.execSQL("INSERT INTO jasa VALUES (22,6,'Cuci Setrika')");

        db.execSQL("INSERT INTO jasa VALUES (23,7,'Tukang Bangunan')");
        db.execSQL("INSERT INTO jasa VALUES (24,7,'Tukang Cat Rumah')");
        db.execSQL("INSERT INTO jasa VALUES (25,7,'Tukang Kayu')");
        db.execSQL("INSERT INTO jasa VALUES (26,7,'Renovasi Kecil')");

        db.execSQL("INSERT INTO jasa VALUES (27,8,'MC Acara')");
        db.execSQL("INSERT INTO jasa VALUES (28,8,'Penyanyi / Band')");
        db.execSQL("INSERT INTO jasa VALUES (29,8,'Dekorasi Acara')");
        db.execSQL("INSERT INTO jasa VALUES (30,8,'Wedding Organizer')");
    }
}