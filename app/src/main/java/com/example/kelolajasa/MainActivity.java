package com.example.kelolajasa;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ── CEK SESSION SEBELUM APAPUN ──
        // Kalau sudah login, langsung redirect tanpa tampilkan start_screen
        SessionManager session = new SessionManager(this);
        if (session.isLoggedIn()) {
            int role = session.getIdRole();
            Intent intent;
            if (role == SessionManager.ROLE_ADMIN) {
                intent = new Intent(this, AdminDashboardActivity.class);
            } else {
                // ROLE_USER (2) atau ROLE_FREELANCER (3) → DashboardActivity
                intent = new Intent(this, DashboardActivity.class);
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;   // ← WAJIB: hentikan onCreate agar tidak lanjut setup UI
        }

        // ── BELUM LOGIN → tampilkan start_screen ──
        EdgeToEdge.enable(this);
        setContentView(R.layout.start_screen);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button btnMasuk  = findViewById(R.id.btnMasuk);
        Button btnDaftar = findViewById(R.id.btnDaftar);

        if (btnMasuk != null) btnMasuk.setOnClickListener(v ->
                startActivity(new Intent(this, MasukAkunActivity.class)));

        if (btnDaftar != null) btnDaftar.setOnClickListener(v ->
                startActivity(new Intent(this, DaftarAkunActivity.class)));
    }
}