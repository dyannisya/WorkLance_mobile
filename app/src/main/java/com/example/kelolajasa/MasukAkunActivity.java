package com.example.kelolajasa;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MasukAkunActivity extends AppCompatActivity {
    TextView tvBack, tvDaftarSekarang;
    Button btnMasuk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.masuk_akun);

        tvBack = findViewById(R.id.tvBack);

        tvDaftarSekarang = findViewById(R.id.tvDaftarSekarang);

        btnMasuk = findViewById(R.id.btnMasuk);

        tvBack.setOnClickListener(v -> {
            finish();
        });

        btnMasuk.setOnClickListener(v -> {
            Intent intentDashboard = new Intent(MasukAkunActivity.this, DashboardActivity.class);
            startActivity(intentDashboard);
            finish();
        });

        tvDaftarSekarang.setOnClickListener(v -> {
            Intent intentDaftar = new Intent(MasukAkunActivity.this, DaftarAkunActivity.class);
            startActivity(intentDaftar);
            finish();
        });
    }
}