package com.example.kelolajasa.database;

import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.content.Context;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "worklance.db";
    // Bump ke 4 → paksa onUpgrade rebuild + seed ulang data dummy
    private static final int DATABASE_VERSION = 5;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private static final String CREATE_ROLE =
            "CREATE TABLE role (id_role INTEGER PRIMARY KEY AUTOINCREMENT, nama_role TEXT);";

    private static final String CREATE_PROVINSI =
            "CREATE TABLE provinsi (id_provinsi INTEGER PRIMARY KEY AUTOINCREMENT, nama_provinsi TEXT);";

    private static final String CREATE_KABUPATEN =
            "CREATE TABLE kabupaten (id_kabupaten INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "id_provinsi INTEGER, nama_kabupaten TEXT);";

    private static final String CREATE_KECAMATAN =
            "CREATE TABLE kecamatan (id_kecamatan INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "id_kabupaten INTEGER, nama_kecamatan TEXT);";

    private static final String CREATE_DESA =
            "CREATE TABLE desa (id_desa INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "id_kecamatan INTEGER, nama_desa TEXT);";

    private static final String CREATE_PENGGUNA =
            "CREATE TABLE pengguna (id_pengguna INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "id_role INTEGER, username TEXT, nama_pengguna TEXT, tanggal_lahir TEXT, " +
                    "no_telp TEXT, email TEXT, password TEXT, id_provinsi TEXT, " +
                    "id_kabupaten TEXT, id_kecamatan TEXT, id_desa TEXT, " +
                    "alamat_lengkap TEXT, foto_profil TEXT);";

    private static final String CREATE_PENGAJUAN =
            "CREATE TABLE pengajuan_freelancer (id_pengajuan INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "id_pengguna INTEGER, nik TEXT, deskripsi TEXT, status TEXT, " +
                    "catatan_admin TEXT, tanggal_pengajuan TEXT);";

    private static final String CREATE_KATEGORI =
            "CREATE TABLE kategori (id_kategori INTEGER PRIMARY KEY AUTOINCREMENT, nama_kategori TEXT);";

    private static final String CREATE_JASA =
            "CREATE TABLE jasa (id_jasa INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "id_kategori INTEGER, nama_jasa TEXT);";

    private static final String CREATE_SATUAN =
            "CREATE TABLE satuan (id_satuan INTEGER PRIMARY KEY AUTOINCREMENT, nama_satuan TEXT);";

    private static final String CREATE_LAYANAN =
            "CREATE TABLE layanan (id_layanan INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "id_pengguna INTEGER, id_jasa INTEGER, id_satuan INTEGER, " +
                    "tarif INTEGER, deskripsi TEXT, namajasa TEXT);";

    private static final String CREATE_GAMBAR_LAYANAN =
            "CREATE TABLE gambar_layanan (id_gambar INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "id_layanan INTEGER, file_gambar TEXT);";

    private static final String CREATE_BOOKING =
            "CREATE TABLE booking (id_booking INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "id_pengguna INTEGER, id_layanan INTEGER, tanggal_booking TEXT, " +
                    "alamat_booking TEXT, catatan TEXT, status_booking TEXT);";

    private static final String CREATE_ULASAN =
            "CREATE TABLE ulasan (id_ulasan INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "id_booking INTEGER, id_pengguna INTEGER, rating INTEGER, " +
                    "komentar TEXT, tanggal_ulasan TEXT);";

    private static final String CREATE_PESAN =
            "CREATE TABLE pesan (id_pesan INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "id_booking INTEGER NOT NULL, id_pengirim INTEGER NOT NULL, " +
                    "id_penerima INTEGER NOT NULL, isi_pesan TEXT NOT NULL, " +
                    "waktu_kirim TEXT NOT NULL, dibaca INTEGER DEFAULT 0);";

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
        db.execSQL(CREATE_PESAN);
        insertDefaultData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Rebuild total saat versi naik (aman untuk development)
        dropAll(db);
        onCreate(db);
    }

    private void dropAll(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS pesan");
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
    }

    private void insertDefaultData(SQLiteDatabase db) {

        // ===== ROLE =====
        db.execSQL("INSERT INTO role VALUES (1,'Admin')");
        db.execSQL("INSERT INTO role VALUES (2,'User')");
        db.execSQL("INSERT INTO role VALUES (3,'Freelancer')");

        // ===== PROVINSI =====
        db.execSQL("INSERT INTO provinsi VALUES (1,'Jawa Timur')");

        // ===== KABUPATEN =====
        db.execSQL("INSERT INTO kabupaten VALUES (1,1,'Surabaya')");
        db.execSQL("INSERT INTO kabupaten VALUES (2,1,'Malang')");
        db.execSQL("INSERT INTO kabupaten VALUES (3,1,'Sidoarjo')");
        db.execSQL("INSERT INTO kabupaten VALUES (4,1,'Gresik')");
        db.execSQL("INSERT INTO kabupaten VALUES (5,1,'Mojokerto')");
        db.execSQL("INSERT INTO kabupaten VALUES (6,1,'Jember')");
        db.execSQL("INSERT INTO kabupaten VALUES (7,1,'Kediri')");
        db.execSQL("INSERT INTO kabupaten VALUES (8,1,'Blitar')");

        // ===== KECAMATAN =====
        db.execSQL("INSERT INTO kecamatan VALUES (1,1,'Sukolilo')");
        db.execSQL("INSERT INTO kecamatan VALUES (2,1,'Rungkut')");
        db.execSQL("INSERT INTO kecamatan VALUES (3,1,'Tegalsari')");
        db.execSQL("INSERT INTO kecamatan VALUES (4,2,'Lowokwaru')");
        db.execSQL("INSERT INTO kecamatan VALUES (5,2,'Blimbing')");
        db.execSQL("INSERT INTO kecamatan VALUES (6,3,'Waru')");
        db.execSQL("INSERT INTO kecamatan VALUES (7,3,'Candi')");
        db.execSQL("INSERT INTO kecamatan VALUES (8,4,'Manyar')");
        db.execSQL("INSERT INTO kecamatan VALUES (9,5,'Mojosari')");
        db.execSQL("INSERT INTO kecamatan VALUES (10,6,'Kaliwates')");

        // ===== DESA =====
        db.execSQL("INSERT INTO desa VALUES (1,1,'Keputih')");
        db.execSQL("INSERT INTO desa VALUES (2,1,'Gebang Putih')");
        db.execSQL("INSERT INTO desa VALUES (3,2,'Rungkut Tengah')");
        db.execSQL("INSERT INTO desa VALUES (4,2,'Kedung Baruk')");
        db.execSQL("INSERT INTO desa VALUES (5,3,'Dr. Soetomo')");
        db.execSQL("INSERT INTO desa VALUES (6,4,'Dinoyo')");
        db.execSQL("INSERT INTO desa VALUES (7,4,'Tlogomas')");
        db.execSQL("INSERT INTO desa VALUES (8,6,'Wedoro')");
        db.execSQL("INSERT INTO desa VALUES (9,6,'Tambak Oso')");
        db.execSQL("INSERT INTO desa VALUES (10,7,'Gelam')");

        // ===== PENGGUNA =====
        // Admin — login: admin@worklance.com / admin123
        db.execSQL("INSERT INTO pengguna VALUES (1,1,'admin','Administrator',''," +
                "'081234567890','admin@worklance.com','admin123','','','',''," +
                "'','')");

        // User/Klien — login: user@worklance.com / user123
        db.execSQL("INSERT INTO pengguna VALUES (2,2,'budi_user','Budi Santoso'," +
                "'1998-05-12','081299887766','user@worklance.com','user123'," +
                "'Jawa Timur','Surabaya','Sukolilo','Keputih'," +
                "'Jl. Keputih No 10','')");

        // Freelancer — login: freelancer@worklance.com / freelance123
        db.execSQL("INSERT INTO pengguna VALUES (3,3,'joko_free','Joko Handoko'," +
                "'1995-08-20','081555444333','freelancer@worklance.com','freelance123'," +
                "'Jawa Timur','Surabaya','Rungkut','Rungkut Tengah'," +
                "'Jl. Rungkut Tengah No 5','')");

        // Freelancer ke-2 — login: sari@worklance.com / sari123
        db.execSQL("INSERT INTO pengguna VALUES (4,3,'sari_design','Sari Dewi'," +
                "'1997-03-15','081677889900','sari@worklance.com','sari123'," +
                "'Jawa Timur','Surabaya','Sukolilo','Gebang Putih'," +
                "'Jl. Gebang Putih No 7','')");

        // ===== KATEGORI =====
        db.execSQL("INSERT INTO kategori VALUES (1,'Desain & Kreatif')");
        db.execSQL("INSERT INTO kategori VALUES (2,'Teknisi & Perbaikan')");
        db.execSQL("INSERT INTO kategori VALUES (3,'Fotografi & Videografi')");
        db.execSQL("INSERT INTO kategori VALUES (4,'Pendidikan & Les Privat')");
        db.execSQL("INSERT INTO kategori VALUES (5,'IT & Digital')");
        db.execSQL("INSERT INTO kategori VALUES (6,'Rumah Tangga')");
        db.execSQL("INSERT INTO kategori VALUES (7,'Tukang & Konstruksi')");
        db.execSQL("INSERT INTO kategori VALUES (8,'Event & Hiburan')");

        // ===== SATUAN =====
        db.execSQL("INSERT INTO satuan VALUES (1,'Unit')");
        db.execSQL("INSERT INTO satuan VALUES (2,'Jam')");
        db.execSQL("INSERT INTO satuan VALUES (3,'Paket')");
        db.execSQL("INSERT INTO satuan VALUES (4,'Hari')");
        db.execSQL("INSERT INTO satuan VALUES (5,'Project')");

        // ===== JASA =====
        db.execSQL("INSERT INTO jasa VALUES (1,1,'Desain Logo')");
        db.execSQL("INSERT INTO jasa VALUES (2,1,'Desain Poster / Banner')");
        db.execSQL("INSERT INTO jasa VALUES (3,1,'Desain Konten Sosial Media')");
        db.execSQL("INSERT INTO jasa VALUES (4,1,'Editing Foto')");
        db.execSQL("INSERT INTO jasa VALUES (5,1,'Editing Video')");
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

        // ===== LAYANAN (dari Joko, id_pengguna=3) =====
        // Kolom: id_layanan, id_pengguna, id_jasa, id_satuan, tarif, deskripsi, namajasa
        db.execSQL("INSERT INTO layanan VALUES (1,3,3,5,150000," +
                "'Desain konten Instagram profesional untuk brand Anda. Meliputi feed post, " +
                "story, dan reels template. Termasuk revisi 2x dan file siap upload.'," +
                "'Desain Konten Instagram')");
        db.execSQL("INSERT INTO layanan VALUES (2,3,1,5,500000," +
                "'Pembuatan logo profesional untuk bisnis dan brand Anda. File diberikan dalam " +
                "format PNG, JPG, dan SVG. Tersedia revisi 3x hingga menemukan desain yang sempurna.'," +
                "'Desain Logo Profesional')");
        db.execSQL("INSERT INTO layanan VALUES (3,3,17,3,2000000," +
                "'Pembuatan website company profile atau landing page yang responsif dan modern. " +
                "Menggunakan HTML, CSS, JavaScript. Domain dan hosting tidak termasuk.'," +
                "'Pembuatan Website Company Profile')");
        db.execSQL("INSERT INTO layanan VALUES (4,3,20,5,1500000," +
                "'Desain UI/UX untuk aplikasi mobile Android atau iOS. Meliputi wireframe, mockup, " +
                "dan prototype interaktif menggunakan Figma. Revisi tidak terbatas.'," +
                "'UI/UX Design Aplikasi Mobile')");
        db.execSQL("INSERT INTO layanan VALUES (5,3,2,5,200000," +
                "'Desain poster dan banner digital untuk kebutuhan promosi bisnis Anda. Tersedia " +
                "untuk berbagai ukuran media sosial dan cetak. Revisi 2x.'," +
                "'Desain Poster & Banner Digital')");

        // ===== LAYANAN (dari Sari, id_pengguna=4) =====
        db.execSQL("INSERT INTO layanan VALUES (6,4,9,5,3000000," +
                "'Sesi foto prewedding di lokasi pilihan Anda. Paket termasuk 2 outfit, editing " +
                "profesional, dan 50 foto siap cetak. Lokasi indoor/outdoor.'," +
                "'Foto Prewedding Profesional')");
        db.execSQL("INSERT INTO layanan VALUES (7,4,11,3,500000," +
                "'Foto produk berkualitas tinggi untuk kebutuhan UMKM dan marketplace. Termasuk " +
                "editing dan background removal. Cocok untuk Tokopedia, Shopee, Instagram.'," +
                "'Foto Produk UMKM')");

        // ===== PENGAJUAN FREELANCER =====
        // Joko sudah diterima
        db.execSQL("INSERT INTO pengajuan_freelancer VALUES (1,3,'3578012008950001'," +
                "'Desainer grafis dengan pengalaman 3 tahun. Terlatih dalam Adobe Photoshop, " +
                "Illustrator, Figma, dan berbagai tools desain digital lainnya.'," +
                "'Diterima','Pengajuan disetujui','2026-03-01')");

        // Sari sudah diterima
        db.execSQL("INSERT INTO pengajuan_freelancer VALUES (2,4,'3578015503970002'," +
                "'Fotografer profesional dengan portofolio 5 tahun di bidang wedding dan produk. " +
                "Berpengalaman dengan berbagai kamera DSLR dan mirrorless.'," +
                "'Diterima','Pengajuan disetujui','2026-03-05')");

        // ===== BOOKING DUMMY (user Budi memesan layanan Joko) =====
        db.execSQL("INSERT INTO booking (id_booking,id_pengguna,id_layanan," +
                "tanggal_booking,alamat_booking,catatan,status_booking) VALUES " +
                "(1,2,1,'2026-04-15','Jl. Keputih No 10, Surabaya'," +
                "'Tolong buat konten bertema Ramadan untuk promosi toko saya.','Menunggu')");

        db.execSQL("INSERT INTO booking (id_booking,id_pengguna,id_layanan," +
                "tanggal_booking,alamat_booking,catatan,status_booking) VALUES " +
                "(2,2,2,'2026-04-18','Jl. Keputih No 10, Surabaya'," +
                "'Butuh logo untuk bisnis kuliner baru saya. Warna dominan merah dan putih.','Diproses')");
    }
}