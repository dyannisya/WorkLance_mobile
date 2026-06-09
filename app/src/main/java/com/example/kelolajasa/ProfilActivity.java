package com.example.kelolajasa;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class ProfilActivity extends AppCompatActivity {

    // Deklarasi View
    TextView tvNama, tvEmail, tvLocation;
    CardView menuGroup1, menuGroup2, menuGroup3;
    ImageView btncari, btnbag, btnhome, btnriwayat, btnprofil;

    // Deklarasi SessionManager
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profil); // Pastikan sesuai nama file XML profil kamu

        // Inisialisasi SessionManager
        sessionManager = new SessionManager(this);

        // Jika user belum login, lempar kembali ke halaman Login (Keamanan tambahan)
        if (!sessionManager.isLoggedIn()) {
            goToLogin();
            return;
        }

        // Inisialisasi tampilan, data, dan navigasi
        initViews();
        loadProfileData();
        setupBottomNav();
    }

    private void initViews() {
        // Hubungkan variabel dengan ID di XML
        tvNama = findViewById(R.id.tvNama);
        tvEmail = findViewById(R.id.tvEmail);
        tvLocation = findViewById(R.id.tvLocation);

        menuGroup1 = findViewById(R.id.menuGroup1);
        menuGroup2 = findViewById(R.id.menuGroup2);
        menuGroup3 = findViewById(R.id.menuGroup3);

        btncari = findViewById(R.id.btncari);
        btnbag = findViewById(R.id.btnbag);
        btnhome = findViewById(R.id.btnhome);
        btnriwayat = findViewById(R.id.btnriwayat);
        btnprofil = findViewById(R.id.btnprofil);

        // Aksi klik untuk MenuGroup1 (Contoh: Edit Profil)
        if (menuGroup1 != null) {
            menuGroup1.setOnClickListener(v -> {
                Toast.makeText(this, "Menu Pengaturan Profil", Toast.LENGTH_SHORT).show();
            });
        }

        // Aksi klik untuk MenuGroup3 (Keluar / Logout)
        if (menuGroup3 != null) {
            menuGroup3.setOnClickListener(v -> {
                // Hapus data sesi
                sessionManager.logout();
                Toast.makeText(this, "Berhasil Keluar", Toast.LENGTH_SHORT).show();

                // Arahkan ke halaman login
                goToLogin();
            });
        }
    }

    private void loadProfileData() {
        // Mengambil data dinamis dari SessionManager
        String namaPengguna = sessionManager.getNamaPengguna();
        String emailPengguna = sessionManager.getEmail();

        // Tampilkan ke TextView
        if (tvNama != null) tvNama.setText(namaPengguna.isEmpty() ? "Nama Tidak Tersedia" : namaPengguna);
        if (tvEmail != null) tvEmail.setText(emailPengguna.isEmpty() ? "Email Tidak Tersedia" : emailPengguna);

        // Lokasi statis karena belum ada di SessionManager (Bisa diubah jika nanti ditambahkan ke DB)
        if (tvLocation != null) tvLocation.setText("Sidoarjo");
    }

    private void goToLogin() {
        // Ganti "LoginActivity.class" dengan nama activity login kamu yang sebenarnya
        Intent intent = new Intent(ProfilActivity.this, MasukAkunActivity.class);
        // Membersihkan riwayat halaman sebelumnya agar tidak bisa di-back setelah logout
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