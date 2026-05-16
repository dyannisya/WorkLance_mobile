package com.example.kelolajasa;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DaftarAkunActivity extends AppCompatActivity {
    TextView tvKembali;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.daftar_akun);

        tvKembali = findViewById(R.id.tvKembali);

        tvKembali.setOnClickListener(v -> {
            finish();
        });
    }
}