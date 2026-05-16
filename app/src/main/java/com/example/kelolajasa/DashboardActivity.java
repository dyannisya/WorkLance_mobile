package com.example.kelolajasa;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout; // Wajib import LinearLayout

import androidx.appcompat.app.AppCompatActivity;

public class DashboardActivity extends AppCompatActivity {
    LinearLayout card1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);

        card1 = findViewById(R.id.card1);

        card1.setOnClickListener(v -> {
            Intent intentPemesanan = new Intent(DashboardActivity.this, PemesananActivity.class);
            startActivity(intentPemesanan);
        });
    }
}