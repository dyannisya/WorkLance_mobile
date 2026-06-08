package com.example.kelolajasa;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;

import java.util.HashMap;
import java.util.Map;

public class PesanJasaActivity extends AppCompatActivity {

    ImageButton imgBack;
    TextView tvHargaDisplay, tvTanggalDipilih;
    EditText etDeskripsi, etAlamat;
    MaterialButton btnLanjutkan;

    // Data dari Intent
    int idLayanan, idFreelancer;
    String namaLayanan, namaFreelancer, namaSatuan;
    double harga;

    // State kalender
    String tanggalDipilih = "";
    TextView selectedDayView = null;

    // Map ID resource → tanggal string (April 2026)
    private Map<Integer, String> dayMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pesanjasa);

        // Terima data dari PemesananActivity
        idLayanan = getIntent().getIntExtra("id_layanan", -1);
        idFreelancer = getIntent().getIntExtra("id_freelancer", -1);
        namaLayanan = getIntent().getStringExtra("nama_layanan");
        namaFreelancer = getIntent().getStringExtra("nama_freelancer");
        harga = getIntent().getDoubleExtra("harga", 0);
        namaSatuan = getIntent().getStringExtra("nama_satuan");

        if (idLayanan == -1) { finish(); return; }

        initViews();
        setupCalendar();
    }

    private void initViews() {
        imgBack = findViewById(R.id.imgBack);
        tvHargaDisplay = findViewById(R.id.tvHargaDisplay);
        tvTanggalDipilih = findViewById(R.id.tvTanggalDipilih);
        etDeskripsi = findViewById(R.id.etDeskripsi);
        etAlamat = findViewById(R.id.etAlamat);
        btnLanjutkan = findViewById(R.id.btnLanjutkan);

        if (imgBack != null) imgBack.setOnClickListener(v -> finish());

        // Tampilkan harga dari data Intent
        if (tvHargaDisplay != null) {
            long h = (long) harga;
            String hargaStr = String.format("%,d", h).replace(",", ".");
            tvHargaDisplay.setText("Rp" + hargaStr + " / " + namaSatuan);
        }

        if (btnLanjutkan != null) btnLanjutkan.setOnClickListener(v -> prosesLanjutkan());
    }

    private void setupCalendar() {
        // Build map: resource ID → tanggal string April 2026
        int[] dayIds = {
                R.id.day1,  R.id.day2,  R.id.day3,  R.id.day4,  R.id.day5,
                R.id.day6,  R.id.day7,  R.id.day8,  R.id.day9,  R.id.day10,
                R.id.day11, R.id.day12, R.id.day13, R.id.day14, R.id.day15,
                R.id.day16, R.id.day17, R.id.day18, R.id.day19, R.id.day20,
                R.id.day21, R.id.day22, R.id.day23, R.id.day24, R.id.day25,
                R.id.day26, R.id.day27, R.id.day28, R.id.day29, R.id.day30
        };

        for (int i = 0; i < dayIds.length; i++) {
            final int dayNumber = i + 1;
            final int resId = dayIds[i];
            // Format: "2026-04-01", "2026-04-02", ...
            dayMap.put(resId, String.format("2026-04-%02d", dayNumber));

            TextView dayView = findViewById(resId);
            if (dayView != null) {
                dayView.setOnClickListener(v -> onDaySelected((TextView) v, resId));
            }
        }
    }

    private void onDaySelected(TextView dayView, int resId) {
        // Reset tampilan hari sebelumnya
        if (selectedDayView != null) {
            selectedDayView.setBackgroundColor(Color.TRANSPARENT);
            selectedDayView.setTextColor(Color.parseColor("#1A1A2E"));
        }

        // Highlight hari yang dipilih
        dayView.setBackgroundColor(Color.parseColor("#161E54"));
        dayView.setTextColor(Color.WHITE);
        selectedDayView = dayView;

        // Simpan tanggal
        tanggalDipilih = dayMap.get(resId);

        // Update label tanggal dipilih
        if (tvTanggalDipilih != null) {
            tvTanggalDipilih.setText("✓ Tanggal dipilih: " +
                    dayView.getText().toString() + " April 2026");
        }
    }

    private void prosesLanjutkan() {
        String deskripsi = etDeskripsi.getText().toString().trim();
        String alamat = etAlamat.getText().toString().trim();

        // Validasi input
        if (TextUtils.isEmpty(deskripsi)) {
            etDeskripsi.setError("Deskripsi pekerjaan tidak boleh kosong");
            etDeskripsi.requestFocus();
            return;
        }
        if (deskripsi.length() < 10) {
            etDeskripsi.setError("Deskripsi minimal 10 karakter");
            etDeskripsi.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(alamat)) {
            etAlamat.setError("Alamat tidak boleh kosong");
            etAlamat.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(tanggalDipilih)) {
            Toast.makeText(this, "Pilih tanggal pengerjaan terlebih dahulu",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Lanjut ke Ringkasan Pesanan
        Intent intent = new Intent(this, RingkasanPesananActivity.class);
        intent.putExtra("id_layanan", idLayanan);
        intent.putExtra("id_freelancer", idFreelancer);
        intent.putExtra("nama_layanan", namaLayanan);
        intent.putExtra("nama_freelancer", namaFreelancer);
        intent.putExtra("harga", harga);
        intent.putExtra("nama_satuan", namaSatuan);
        intent.putExtra("tanggal_booking", tanggalDipilih);
        intent.putExtra("alamat_booking", alamat);
        intent.putExtra("deskripsi", deskripsi);
        startActivity(intent);
    }
}