package com.example.kelolajasa;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class PesanJasaActivity extends AppCompatActivity {
    Button btnLanjutkan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pesanjasa);

        btnLanjutkan = findViewById(R.id.btnLanjutkan);

        btnLanjutkan.setOnClickListener(v -> {
            Intent intentRingkasan = new Intent(PesanJasaActivity.this, RingkasanPesananActivity.class);
            startActivity(intentRingkasan);
        });
    }
}