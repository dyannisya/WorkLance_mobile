package com.example.kelolajasa;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kelolajasa.database.LayananDAO;
import com.example.kelolajasa.model.LayananDisplay;
import com.google.android.material.button.MaterialButton;

public class PemesananActivity extends AppCompatActivity {

    ImageView imgBack, imgProfil;
    TextView tvKategori, tvNamaLayanan, tvHarga;
    TextView tvNamaFreelancer, tvRating, tvLokasi, tvDeskripsi;
    MaterialButton btnPesan;

    LayananDAO layananDAO;
    LayananDisplay layananDisplay;
    int idLayanan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pemesanan);

        idLayanan = getIntent().getIntExtra("id_layanan", -1);
        if (idLayanan == -1) {
            Toast.makeText(this, "Data layanan tidak ditemukan", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        layananDAO = new LayananDAO(this);

        initViews();
        loadData();
    }

    private void initViews() {
        imgBack = findViewById(R.id.imgBack);
        imgProfil = findViewById(R.id.imgProfil);
        tvKategori = findViewById(R.id.tvKategori);
        tvNamaLayanan = findViewById(R.id.tvNamaLayanan);
        tvHarga = findViewById(R.id.tvHarga);
        tvNamaFreelancer = findViewById(R.id.tvNamaFreelancer);
        tvRating = findViewById(R.id.tvRating);
        tvLokasi = findViewById(R.id.tvLokasi);
        tvDeskripsi = findViewById(R.id.tvDeskripsi);
        btnPesan = findViewById(R.id.btnPesan);

        if (imgBack != null) imgBack.setOnClickListener(v -> finish());
    }

    private void loadData() {
        layananDisplay = layananDAO.getDisplayById(idLayanan);

        if (layananDisplay == null) {
            Toast.makeText(this, "Layanan tidak ditemukan di database", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Isi semua UI dari data DB
        safeSetText(tvKategori, layananDisplay.getKategori());
        safeSetText(tvNamaLayanan, layananDisplay.getNamaLayanan());
        safeSetText(tvHarga, layananDisplay.getHargaFormatted());
        safeSetText(tvNamaFreelancer, layananDisplay.getNamaFreelancer());
        safeSetText(tvRating, " " + layananDisplay.getRatingFormatted());
        safeSetText(tvLokasi, " " + layananDisplay.getLokasiKabupaten());
        safeSetText(tvDeskripsi, layananDisplay.getDeskripsi());

        // Tombol Pesan → ke PesanJasaActivity
        if (btnPesan != null) {
            btnPesan.setOnClickListener(v -> lanjutPesan());
        }
    }

    private void lanjutPesan() {
        Intent intent = new Intent(this, PesanJasaActivity.class);
        intent.putExtra("id_layanan", layananDisplay.getIdLayanan());
        intent.putExtra("id_freelancer", layananDisplay.getIdFreelancer());
        intent.putExtra("nama_layanan", layananDisplay.getNamaLayanan());
        intent.putExtra("nama_freelancer", layananDisplay.getNamaFreelancer());
        intent.putExtra("harga", layananDisplay.getHarga());
        intent.putExtra("nama_satuan", layananDisplay.getNamaSatuan());
        startActivity(intent);
    }

    private void safeSetText(TextView tv, String value) {
        if (tv != null) tv.setText(value != null ? value : "-");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (layananDAO != null) layananDAO.close();
    }
}