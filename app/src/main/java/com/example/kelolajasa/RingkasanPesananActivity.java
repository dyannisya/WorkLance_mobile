package com.example.kelolajasa;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kelolajasa.database.BookingDAO;
import com.google.android.material.button.MaterialButton;

public class RingkasanPesananActivity extends AppCompatActivity {

    ImageButton imgBack;
    // Detail Freelancer card
    TextView tvNamaFreelancer, tvTarif, tvLayanan, tvSatuan;
    // Detail Pesanan card
    TextView tvNamaFreelancer2, tvLayanan2, tvTanggal, tvCatatan;
    // Tombol konfirmasi
    MaterialButton btnKonfirmasi;

    // Data dari Intent
    int idLayanan, idFreelancer;
    String namaLayanan, namaFreelancer, namaSatuan;
    String tanggalBooking, alamatBooking, deskripsi;
    double harga;

    BookingDAO bookingDAO;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ringkasan_pesanan);

        // Terima semua data dari PesanJasaActivity
        idLayanan = getIntent().getIntExtra("id_layanan", -1);
        idFreelancer = getIntent().getIntExtra("id_freelancer", -1);
        namaLayanan = getIntent().getStringExtra("nama_layanan");
        namaFreelancer = getIntent().getStringExtra("nama_freelancer");
        harga = getIntent().getDoubleExtra("harga", 0);
        namaSatuan = getIntent().getStringExtra("nama_satuan");
        tanggalBooking = getIntent().getStringExtra("tanggal_booking");
        alamatBooking = getIntent().getStringExtra("alamat_booking");
        deskripsi = getIntent().getStringExtra("deskripsi");

        if (idLayanan == -1) { finish(); return; }

        bookingDAO = new BookingDAO(this);
        sessionManager = new SessionManager(this);

        initViews();
        tampilkanRingkasan();
    }

    private void initViews() {
        imgBack = findViewById(R.id.imgBack);
        tvNamaFreelancer = findViewById(R.id.tvNamaFreelancer);
        tvTarif = findViewById(R.id.tvTarif);
        tvLayanan = findViewById(R.id.tvLayanan);
        tvSatuan = findViewById(R.id.tvSatuan);
        tvNamaFreelancer2 = findViewById(R.id.tvNamaFreelancer2);
        tvLayanan2 = findViewById(R.id.tvLayanan2);
        tvTanggal = findViewById(R.id.tvTanggal);
        tvCatatan = findViewById(R.id.tvCatatan);
        btnKonfirmasi = findViewById(R.id.btnKonfirmasi);

        if (imgBack != null) imgBack.setOnClickListener(v -> finish());
    }

    private void tampilkanRingkasan() {
        // Format harga
        long h = (long) harga;
        String hargaStr = "Rp" + String.format("%,d", h).replace(",", ".");

        // Format tanggal untuk display (2026-04-15 → 15 April 2026)
        String tanggalDisplay = formatTanggal(tanggalBooking);

        // Isi card Detail Freelancer
        safeSetText(tvNamaFreelancer, namaFreelancer);
        safeSetText(tvTarif, hargaStr);
        safeSetText(tvLayanan, namaLayanan);
        safeSetText(tvSatuan, namaSatuan);

        // Isi card Detail Pesanan
        safeSetText(tvNamaFreelancer2, namaFreelancer);
        safeSetText(tvLayanan2, namaLayanan);
        safeSetText(tvTanggal, tanggalDisplay);
        safeSetText(tvCatatan, deskripsi);

        // Tombol Konfirmasi → simpan ke DB
        if (btnKonfirmasi != null) {
            btnKonfirmasi.setOnClickListener(v -> konfirmasiPesanan());
        }
    }

    private void konfirmasiPesanan() {
        int idPengguna = sessionManager.getIdPengguna();

        // Simpan booking ke database
        long result = bookingDAO.insert(
                idPengguna,
                idLayanan,
                tanggalBooking,
                alamatBooking,
                deskripsi
        );

        if (result < 0) {
            Toast.makeText(this,
                    "Gagal menyimpan pesanan. Silakan coba lagi.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Booking berhasil — tampilkan dialog sukses
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("✅ Pesanan Berhasil!")
                .setMessage("Pesanan Anda untuk layanan \"" + namaLayanan + "\" " +
                        "dari " + namaFreelancer + " telah dikirim.\n\n" +
                        "Tanggal: " + formatTanggal(tanggalBooking) + "\n" +
                        "Status: Menunggu konfirmasi freelancer.")
                .setCancelable(false)
                .setPositiveButton("Kembali ke Beranda", (d, w) -> {
                    Intent intent = new Intent(this, DashboardActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                            Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    finish();
                })
                .setNeutralButton("Lihat Riwayat", (d, w) -> {
                    Intent intent = new Intent(this, RiwayatPesananActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                })
                .show();
    }

    /** Format "2026-04-15" → "15 April 2026" */
    private String formatTanggal(String tanggal) {
        if (tanggal == null || tanggal.isEmpty()) return "-";
        try {
            String[] parts = tanggal.split("-");
            if (parts.length < 3) return tanggal;
            int bulan = Integer.parseInt(parts[1]);
            String[] namaBulan = {"", "Januari","Februari","Maret","April","Mei",
                    "Juni","Juli","Agustus","September","Oktober","November","Desember"};
            return parts[2] + " " + namaBulan[bulan] + " " + parts[0];
        } catch (Exception e) {
            return tanggal;
        }
    }

    private void safeSetText(TextView tv, String value) {
        if (tv != null) tv.setText(value != null ? value : "-");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bookingDAO != null) bookingDAO.close();
    }
}