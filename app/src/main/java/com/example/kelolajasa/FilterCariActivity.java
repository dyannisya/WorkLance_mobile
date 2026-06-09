package com.example.kelolajasa;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class FilterCariActivity extends AppCompatActivity {

    private ImageButton btnClose;
    private TextView tvReset;
    private EditText inputMin, inputMax;
    private MaterialButton btnLanjutkan;

    // Lokasi
    private TextView locSidoarjo, locSurabaya, locMalang, locPasuruan, locJember;
    // Harga
    private TextView range1, range2, range3;

    private String selectedLocation = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filter_cari);

        initViews();
        setupListeners();
    }

    private void initViews() {
        btnClose = findViewById(R.id.btnClose);
        tvReset = findViewById(R.id.tvReset);
        inputMin = findViewById(R.id.inputMin);
        inputMax = findViewById(R.id.inputMax);
        btnLanjutkan = findViewById(R.id.btnLanjutkan);

        locSidoarjo = findViewById(R.id.locSidoarjo);
        locSurabaya = findViewById(R.id.locSurabaya);
        locMalang = findViewById(R.id.locMalang);
        locPasuruan = findViewById(R.id.locPasuruan);
        locJember = findViewById(R.id.locJember);

        range1 = findViewById(R.id.range1);
        range2 = findViewById(R.id.range2);
        range3 = findViewById(R.id.range3);
    }

    private void setupListeners() {
        // Tombol Close
        btnClose.setOnClickListener(v -> finish());

        // Tombol Reset
        tvReset.setOnClickListener(v -> {
            resetFilters();
            Toast.makeText(this, "Filter direset", Toast.LENGTH_SHORT).show();
        });

        // Logika Pilih Lokasi
        locSidoarjo.setOnClickListener(v -> selectLocation(locSidoarjo, "Sidoarjo"));
        locSurabaya.setOnClickListener(v -> selectLocation(locSurabaya, "Surabaya"));
        locMalang.setOnClickListener(v -> selectLocation(locMalang, "Malang"));
        locPasuruan.setOnClickListener(v -> selectLocation(locPasuruan, "Pasuruan"));
        locJember.setOnClickListener(v -> selectLocation(locJember, "Jember"));

        // Logika Pilih Range Harga (Otomatis mengisi EditText)
        range1.setOnClickListener(v -> selectPriceRange(range1, "0", "150000"));
        range2.setOnClickListener(v -> selectPriceRange(range2, "150000", "450000"));
        range3.setOnClickListener(v -> selectPriceRange(range3, "450000", "850000"));

        // Tombol Terapkan Filter
        btnLanjutkan.setOnClickListener(v -> {
            String minPrice = inputMin.getText().toString().trim();
            String maxPrice = inputMax.getText().toString().trim();

            Toast.makeText(this, "Menerapkan Filter...", Toast.LENGTH_SHORT).show();

            // Mengirim data kembali ke CariActivity
            Intent resultIntent = new Intent();
            resultIntent.putExtra("lokasi", selectedLocation);
            resultIntent.putExtra("min_harga", minPrice);
            resultIntent.putExtra("max_harga", maxPrice);

            setResult(RESULT_OK, resultIntent);
            finish(); // Tutup halaman filter dan kembali ke CariActivity
        });
    }

    private void selectLocation(TextView clickedView, String locationName) {
        // Reset warna semua lokasi ke default (Background biru muda transparan)
        TextView[] allLocations = {locSidoarjo, locSurabaya, locMalang, locPasuruan, locJember};
        for (TextView tv : allLocations) {
            tv.setBackgroundTintList(null); // Menghapus tint
            tv.setTextColor(Color.parseColor("#161E54"));
        }

        // Set warna untuk yang dipilih menjadi biru pekat
        clickedView.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#161E54")));
        clickedView.setTextColor(Color.WHITE);

        selectedLocation = locationName;
    }

    private void selectPriceRange(TextView clickedView, String min, String max) {
        // Reset warna semua range
        TextView[] allRanges = {range1, range2, range3};
        for (TextView tv : allRanges) {
            tv.setBackgroundTintList(null);
            tv.setTextColor(Color.parseColor("#161E54"));
        }

        // Set warna untuk yang dipilih
        clickedView.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#161E54")));
        clickedView.setTextColor(Color.WHITE);

        // Otomatis isikan nominal ke kotak input Min & Max
        inputMin.setText(min);
        inputMax.setText(max);
    }

    private void resetFilters() {
        // Bersihkan data lokasi
        selectedLocation = "";
        TextView[] allLocations = {locSidoarjo, locSurabaya, locMalang, locPasuruan, locJember};
        for (TextView tv : allLocations) {
            tv.setBackgroundTintList(null);
            tv.setTextColor(Color.parseColor("#161E54"));
        }

        // Bersihkan data harga
        inputMin.setText("");
        inputMax.setText("");
        TextView[] allRanges = {range1, range2, range3};
        for (TextView tv : allRanges) {
            tv.setBackgroundTintList(null);
            tv.setTextColor(Color.parseColor("#161E54"));
        }
    }
}