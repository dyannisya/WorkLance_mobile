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

public class ProfilKontakAlamatActivity extends AppCompatActivity {

    private ImageButton imgBack;
    private EditText inputEmail, inputKontak, inputProvinsi, inputKabupaten, inputKecamatan, inputDesa, inputAlamat;
    private MaterialButton btnSimpan;

    private SessionManager sessionManager;
    private PenggunaDAO penggunaDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profil_kontak_alamat);

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
        inputEmail = findViewById(R.id.input1);
        inputKontak = findViewById(R.id.input2);
        inputProvinsi = findViewById(R.id.input3);
        inputKabupaten = findViewById(R.id.input4);
        inputKecamatan = findViewById(R.id.input5);
        inputDesa = findViewById(R.id.input6);
        inputAlamat = findViewById(R.id.input7); // Memanggil ID baru yang sudah diperbaiki
        btnSimpan = findViewById(R.id.btnLanjutkan);
    }

    private void loadDataUser() {
        Pengguna pengguna = penggunaDAO.getPenggunaById(sessionManager.getIdPengguna());

        if (pengguna != null) {
            inputEmail.setText(pengguna.getEmail());
            inputKontak.setText(pengguna.getNoTelp());

            // Tampilkan data lokasi dari database
            inputProvinsi.setText(pengguna.getIdProvinsi()); // Akan berisi teks provinsi
            inputKabupaten.setText(pengguna.getIdKabupaten()); // Akan berisi teks kabupaten
            inputKecamatan.setText(pengguna.getIdKecamatan());
            inputDesa.setText(pengguna.getIdDesa());

            inputAlamat.setText(pengguna.getAlamatLengkap());
        } else {
            Toast.makeText(this, "Gagal memuat data pengguna", Toast.LENGTH_SHORT).show();
        }
    }

    private void prosesSimpan() {
        String emailBaru = inputEmail.getText().toString().trim();
        String kontakBaru = inputKontak.getText().toString().trim();
        String provBaru = inputProvinsi.getText().toString().trim();
        String kabBaru = inputKabupaten.getText().toString().trim();
        String kecBaru = inputKecamatan.getText().toString().trim();
        String desaBaru = inputDesa.getText().toString().trim();
        String alamatBaru = inputAlamat.getText().toString().trim();

        if (TextUtils.isEmpty(emailBaru)) {
            inputEmail.setError("Email tidak boleh kosong");
            inputEmail.requestFocus();
            return;
        }

        // Lempar semua data ke database (termasuk 4 wilayah yang baru)
        boolean isUpdated = penggunaDAO.updateKontakAlamat(
                sessionManager.getIdPengguna(),
                emailBaru,
                kontakBaru,
                provBaru,
                kabBaru,
                kecBaru,
                desaBaru,
                alamatBaru
        );

        if (isUpdated) {
            Toast.makeText(this, "Kontak dan Alamat berhasil diperbarui", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Gagal memperbarui data", Toast.LENGTH_SHORT).show();
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