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

public class DaftarAkunActivity extends AppCompatActivity {

    TextView tvKembali, tvMasukDisini;
    Button btnBuatAkun;
    EditText etEmail, etUsername, etPhone, etPassword;

    PenggunaDAO penggunaDAO;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.daftar_akun);

        penggunaDAO = new PenggunaDAO(this);
        sessionManager = new SessionManager(this);

        tvKembali = findViewById(R.id.tvKembali);
        tvMasukDisini = findViewById(R.id.tvMasukDisini);
        btnBuatAkun = findViewById(R.id.btnBuatAkun);
        etEmail = findViewById(R.id.etEmail);
        etUsername = findViewById(R.id.etUsername);
        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);

        tvKembali.setOnClickListener(v -> finish());

        tvMasukDisini.setOnClickListener(v -> {
            startActivity(new Intent(DaftarAkunActivity.this, MasukAkunActivity.class));
            finish();
        });

        btnBuatAkun.setOnClickListener(v -> prosesDaftar());
    }

    private void prosesDaftar() {
        String email = etEmail.getText().toString().trim();
        String username = etUsername.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validasi input
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email tidak boleh kosong");
            etEmail.requestFocus();
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Format email tidak valid");
            etEmail.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(username)) {
            etUsername.setError("Username tidak boleh kosong");
            etUsername.requestFocus();
            return;
        }
        if (username.length() < 3) {
            etUsername.setError("Username minimal 3 karakter");
            etUsername.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(phone)) {
            etPhone.setError("No. Telp tidak boleh kosong");
            etPhone.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password tidak boleh kosong");
            etPassword.requestFocus();
            return;
        }
        if (password.length() < 6) {
            etPassword.setError("Password minimal 6 karakter");
            etPassword.requestFocus();
            return;
        }

        // Insert ke database
        long result = penggunaDAO.register(email, username, phone, password);

        if (result == -2) {
            etEmail.setError("Email sudah terdaftar");
            etEmail.requestFocus();
            return;
        }
        if (result == -3) {
            etUsername.setError("Username sudah dipakai");
            etUsername.requestFocus();
            return;
        }
        if (result < 0) {
            Toast.makeText(this, "Gagal mendaftar, coba lagi", Toast.LENGTH_SHORT).show();
            return;
        }

        // Auto-login setelah daftar berhasil
        Pengguna pengguna = penggunaDAO.login(email, password);
        if (pengguna != null) {
            sessionManager.createLoginSession(
                    pengguna.getIdPengguna(),
                    pengguna.getIdRole(),
                    pengguna.getUsername(),
                    pengguna.getNamaPengguna(),
                    pengguna.getEmail()
            );
        }

        Toast.makeText(this, "Akun berhasil dibuat! Selamat datang, " + username + "!", Toast.LENGTH_LONG).show();

        Intent intent = new Intent(this, DashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (penggunaDAO != null) penggunaDAO.close();
    }
}