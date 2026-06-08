package com.example.kelolajasa;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kelolajasa.database.PenggunaDAO;
import com.example.kelolajasa.model.Pengguna;

public class MasukAkunActivity extends AppCompatActivity {

    TextView tvBack, tvDaftarSekarang;
    Button btnMasuk;
    EditText etEmail, etPassword;

    PenggunaDAO penggunaDAO;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.masuk_akun);

        penggunaDAO = new PenggunaDAO(this);
        sessionManager = new SessionManager(this);

        // Jika sudah login, langsung redirect
        if (sessionManager.isLoggedIn()) {
            redirectByRole(sessionManager.getIdRole());
            finish();
            return;
        }

        tvBack = findViewById(R.id.tvBack);
        tvDaftarSekarang = findViewById(R.id.tvDaftarSekarang);
        btnMasuk = findViewById(R.id.btnMasuk);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        tvBack.setOnClickListener(v -> finish());

        tvDaftarSekarang.setOnClickListener(v -> {
            startActivity(new Intent(MasukAkunActivity.this, DaftarAkunActivity.class));
        });

        btnMasuk.setOnClickListener(v -> prosesLogin());
    }

    private void prosesLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validasi input kosong
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email tidak boleh kosong");
            etEmail.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password tidak boleh kosong");
            etPassword.requestFocus();
            return;
        }

        // Query ke database
        Pengguna pengguna = penggunaDAO.login(email, password);

        if (pengguna == null) {
            Toast.makeText(this, "Email atau password salah", Toast.LENGTH_SHORT).show();
            return;
        }

        // Simpan sesi
        sessionManager.createLoginSession(
                pengguna.getIdPengguna(),
                pengguna.getIdRole(),
                pengguna.getUsername(),
                pengguna.getNamaPengguna(),
                pengguna.getEmail()
        );

        Toast.makeText(this, "Selamat datang, " + pengguna.getNamaPengguna() + "!", Toast.LENGTH_SHORT).show();

        // Routing berdasarkan role
        redirectByRole(pengguna.getIdRole());
        finish();
    }

    private void redirectByRole(int idRole) {
        Intent intent;
        if (idRole == SessionManager.ROLE_ADMIN) {
            intent = new Intent(this, AdminDashboardActivity.class);
        } else {
            // ROLE_USER dan ROLE_FREELANCER sama-sama ke DashboardActivity
            intent = new Intent(this, DashboardActivity.class);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (penggunaDAO != null) penggunaDAO.close();
    }
}