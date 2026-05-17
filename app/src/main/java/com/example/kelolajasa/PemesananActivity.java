package com.example.kelolajasa;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button; // Asumsi btnPesan adalah sebuah Button

import androidx.appcompat.app.AppCompatActivity;

public class PemesananActivity extends AppCompatActivity {
    Button btnPesan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pemesanan);

        btnPesan = findViewById(R.id.btnPesan);

        btnPesan.setOnClickListener(v -> {
            Intent intentPesanJasa = new Intent(PemesananActivity.this, PesanJasaActivity.class);
            startActivity(intentPesanJasa);
        });
    }
}