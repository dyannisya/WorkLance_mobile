package com.example.kelolajasa;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class RingkasanPesananActivity extends AppCompatActivity {
    Button btnKonfirmasi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ringkasan_pesanan);

        btnKonfirmasi = findViewById(R.id.btnKonfirmasi);

        btnKonfirmasi.setOnClickListener(v -> {
            Intent intentChat = new Intent(RingkasanPesananActivity.this, CustomerChatActivity.class);
            startActivity(intentChat);
            finish();
        });
    }
}