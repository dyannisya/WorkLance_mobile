package com.example.kelolajasa;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class ProfilActivity extends AppCompatActivity {

    // Deklarasi View
    TextView tvNama, tvEmail, tvLocation;
    CardView menuGroup2, menuGroup3;

    // Tambahkan deklarasi untuk tombol menu profil
    LinearLayout btnEditProfil, btnKontakAlamat, btnKeamanan;

    ImageView btncari, btnbag, btnhome, btnriwayat, btnprofil;

    // Deklarasi SessionManager
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profil);

        sessionManager = new SessionManager(this);

        if (!sessionManager.isLoggedIn()) {
            goToLogin();
            return;
        }

        initViews();
        setupBottomNav();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Fungsi ini akan selalu dijalankan setiap kali user kembali ke halaman ini
        loadProfileData();
    }

    private void initViews() {
        tvNama = findViewById(R.id.tvNama);
        tvEmail = findViewById(R.id.tvEmail);
        tvLocation = findViewById(R.id.tvLocation);

        // Inisialisasi ID menu baru
        btnEditProfil = findViewById(R.id.btnEditProfil);
        btnKontakAlamat = findViewById(R.id.btnKontakAlamat);
        btnKeamanan = findViewById(R.id.btnKeamanan);

        menuGroup2 = findViewById(R.id.menuGroup2);
        menuGroup3 = findViewById(R.id.menuGroup3);

        btncari = findViewById(R.id.btncari);
        btnbag = findViewById(R.id.btnbag);
        btnhome = findViewById(R.id.btnhome);
        btnriwayat = findViewById(R.id.btnriwayat);
        btnprofil = findViewById(R.id.btnprofil);

        // Aksi klik untuk Edit Profil
        if (btnEditProfil != null) {
            btnEditProfil.setOnClickListener(v -> {
                startActivity(new Intent(this, ProfilInformasiAkunActivity.class));
            });
        }

        // Aksi klik untuk Kontak & Alamat
        if (btnKontakAlamat != null) {
            btnKontakAlamat.setOnClickListener(v -> {
                startActivity(new Intent(this, ProfilKontakAlamatActivity.class));
            });
        }

        // Aksi klik untuk Keamanan
        if (btnKeamanan != null) {
            btnKeamanan.setOnClickListener(v -> {
                startActivity(new Intent(this, ProfilKeamananActivity.class));
            });
        }

        // Aksi klik untuk MenuGroup3 (Keluar / Logout)
        if (menuGroup3 != null) {
            menuGroup3.setOnClickListener(v -> {
                sessionManager.logout();
                Toast.makeText(this, "Berhasil Keluar", Toast.LENGTH_SHORT).show();
                goToLogin();
            });
        }
    }

    private void loadProfileData() {
        // Ambil data langsung dari Database berdasarkan ID yang sedang login
        com.example.kelolajasa.database.PenggunaDAO penggunaDAO = new com.example.kelolajasa.database.PenggunaDAO(this);
        com.example.kelolajasa.model.Pengguna pengguna = penggunaDAO.getPenggunaById(sessionManager.getIdPengguna());

        if (pengguna != null) {
            // Tampilkan data asli dari database ke layar
            if (tvNama != null) tvNama.setText(pengguna.getNamaPengguna());
            if (tvEmail != null) tvEmail.setText(pengguna.getEmail());

            // Menampilkan alamat lengkap di text lokasi jika ada
            if (tvLocation != null) {
                String alamat = pengguna.getAlamatLengkap();
                tvLocation.setText(alamat == null || alamat.isEmpty() ? "Belum diatur" : alamat);
            }
        } else {
            // Jika gagal load DB, gunakan session sebagai backup
            if (tvNama != null) tvNama.setText(sessionManager.getNamaPengguna());
            if (tvEmail != null) tvEmail.setText(sessionManager.getEmail());
        }

        penggunaDAO.close(); // Selalu tutup DAO setelah digunakan
    }

    private void goToLogin() {
        Intent intent = new Intent(ProfilActivity.this, MasukAkunActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void setupBottomNav() {
        if (btncari != null) {
            btncari.setOnClickListener(v -> {
                startActivity(new Intent(this, CariActivity.class));
                finish();
            });
        }
        if (btnbag != null) {
            btnbag.setOnClickListener(v -> {
                startActivity(new Intent(this, KelolaJasaFreelancer1Activity.class));
                finish();
            });
        }
        if (btnhome != null) {
            btnhome.setOnClickListener(v -> {
                startActivity(new Intent(this, DashboardActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                finish();
            });
        }
        if (btnriwayat != null) {
            btnriwayat.setOnClickListener(v -> {
                startActivity(new Intent(this, RiwayatPesananActivity.class));
                finish();
            });
        }
        if (btnprofil != null) {
            btnprofil.setOnClickListener(v ->
                    Toast.makeText(this, "Anda sudah berada di halaman Profil", Toast.LENGTH_SHORT).show()
            );
        }
    }
}