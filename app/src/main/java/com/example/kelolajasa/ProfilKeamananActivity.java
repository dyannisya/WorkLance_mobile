package com.example.kelolajasa;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kelolajasa.database.PenggunaDAO;
import com.example.kelolajasa.model.Pengguna;
import com.google.android.material.button.MaterialButton;

public class ProfilKeamananActivity extends AppCompatActivity {

    private ImageButton imgBack;
    private EditText inputPasswordLama, inputPasswordBaru, inputKonfirmasiPassword;
    private MaterialButton btnSimpan;

    private SessionManager sessionManager;
    private PenggunaDAO penggunaDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profil_keamanan);

        sessionManager = new SessionManager(this);
        penggunaDAO = new PenggunaDAO(this);

        initViews();

        // Tombol simpan perubahan
        if (btnSimpan != null) {
            btnSimpan.setOnClickListener(v -> prosesSimpan());
        }

        // Tombol kembali
        if (imgBack != null) {
            imgBack.setOnClickListener(v -> onBackPressed());
        }
    }

    private void initViews() {
        imgBack = findViewById(R.id.imgBack);
        inputPasswordLama = findViewById(R.id.input1);
        inputPasswordBaru = findViewById(R.id.input2);
        inputKonfirmasiPassword = findViewById(R.id.input3);
        btnSimpan = findViewById(R.id.btnLanjutkan);
    }

    private void prosesSimpan() {
        String passLama = inputPasswordLama.getText().toString();
        String passBaru = inputPasswordBaru.getText().toString();
        String passKonfirmasi = inputKonfirmasiPassword.getText().toString();

        // 1. Validasi tidak boleh ada yang kosong
        if (TextUtils.isEmpty(passLama)) {
            inputPasswordLama.setError("Password lama wajib diisi");
            inputPasswordLama.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(passBaru)) {
            inputPasswordBaru.setError("Password baru wajib diisi");
            inputPasswordBaru.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(passKonfirmasi)) {
            inputKonfirmasiPassword.setError("Konfirmasi password wajib diisi");
            inputKonfirmasiPassword.requestFocus();
            return;
        }

        // 2. Validasi apakah password baru dan konfirmasi sama
        if (!passBaru.equals(passKonfirmasi)) {
            inputKonfirmasiPassword.setError("Password baru dan konfirmasi tidak cocok!");
            inputKonfirmasiPassword.requestFocus();
            return;
        }

        // 3. Tarik data akun dari database untuk mengecek password lamanya
        Pengguna pengguna = penggunaDAO.getPenggunaById(sessionManager.getIdPengguna());
        if (pengguna == null) {
            Toast.makeText(this, "Gagal memuat data keamanan", Toast.LENGTH_SHORT).show();
            return;
        }

        // 4. Validasi apakah password lama yang diketik sesuai dengan di database
        if (!pengguna.getPassword().equals(passLama)) {
            inputPasswordLama.setError("Password lama yang Anda masukkan salah");
            inputPasswordLama.requestFocus();
            return;
        }

        // 5. Eksekusi simpan password baru ke database
        boolean isUpdated = penggunaDAO.updatePassword(sessionManager.getIdPengguna(), passBaru);

        if (isUpdated) {
            Toast.makeText(this, "Password berhasil diperbarui!", Toast.LENGTH_SHORT).show();
            finish(); // Kembali ke halaman profil
        } else {
            Toast.makeText(this, "Gagal memperbarui password", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (penggunaDAO != null) {
            penggunaDAO.close();
        }
    }
}