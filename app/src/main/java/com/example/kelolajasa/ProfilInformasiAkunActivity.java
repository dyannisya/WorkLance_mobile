package com.example.kelolajasa;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kelolajasa.database.PenggunaDAO;
import com.example.kelolajasa.model.Pengguna;
import com.google.android.material.button.MaterialButton;

public class ProfilInformasiAkunActivity extends AppCompatActivity {

    private ImageButton imgBack;
    private EditText inputUsername, inputNamaLengkap, inputTanggalLahir;
    private MaterialButton btnSimpan;

    private SessionManager sessionManager;
    private PenggunaDAO penggunaDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profil_informasi_akun);

        sessionManager = new SessionManager(this);
        penggunaDAO = new PenggunaDAO(this);

        initViews();
        loadDataUser();

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
        inputUsername = findViewById(R.id.input1);
        inputNamaLengkap = findViewById(R.id.input2);
        inputTanggalLahir = findViewById(R.id.input3);
        btnSimpan = findViewById(R.id.btnLanjutkan);
    }

    private void loadDataUser() {
        // Ambil data pengguna dari database berdasarkan ID yang sedang login
        Pengguna pengguna = penggunaDAO.getPenggunaById(sessionManager.getIdPengguna());

        if (pengguna != null) {
            // Tampilkan data ke dalam form EditText
            inputUsername.setText(pengguna.getUsername());
            inputNamaLengkap.setText(pengguna.getNamaPengguna());
            inputTanggalLahir.setText(pengguna.getTanggalLahir());
        } else {
            Toast.makeText(this, "Gagal memuat data pengguna", Toast.LENGTH_SHORT).show();
        }
    }

    private void prosesSimpan() {
        String usernameBaru = inputUsername.getText().toString().trim();
        String namaBaru = inputNamaLengkap.getText().toString().trim();
        String tanggalLahirBaru = inputTanggalLahir.getText().toString().trim();

        // Validasi form agar tidak kosong
        if (TextUtils.isEmpty(usernameBaru)) {
            inputUsername.setError("Username tidak boleh kosong");
            inputUsername.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(namaBaru)) {
            inputNamaLengkap.setError("Nama Lengkap tidak boleh kosong");
            inputNamaLengkap.requestFocus();
            return;
        }

        // Jalankan proses update ke database
        boolean isUpdated = penggunaDAO.updateInformasiAkun(
                sessionManager.getIdPengguna(),
                usernameBaru,
                namaBaru,
                tanggalLahirBaru
        );

        if (isUpdated) {
            // Update juga nama di SessionManager agar langsung berubah di semua halaman
            sessionManager.setNamaPengguna(namaBaru);

            Toast.makeText(this, "Informasi akun berhasil diperbarui", Toast.LENGTH_SHORT).show();
            // Kembali ke halaman profil setelah berhasil menyimpan
            finish();
        } else {
            Toast.makeText(this, "Gagal memperbarui informasi akun", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Tutup koneksi database untuk mencegah memory leak
        if (penggunaDAO != null) {
            penggunaDAO.close();
        }
    }
}