package com.example.kelolajasa;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class DashboardActivity extends AppCompatActivity {

    EditText etSearch;
    ImageView imgAvatar, btncari, btnbag, btnhome, btnriwayat, btnprofil;
    LinearLayout card1;

    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);

        sessionManager = new SessionManager(this);

        // Jika belum login, kembalikan ke start screen
        if (!sessionManager.isLoggedIn()) {
            startActivity(new Intent(this, MainActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
            return;
        }

        initViews();
        setupSearch();
        setupBottomNav();
        setupContentClicks();
    }

    private void initViews() {
        etSearch = findViewById(R.id.editTextText3);
        imgAvatar = findViewById(R.id.imgAvatar);

        // Bottom Nav
        btncari = findViewById(R.id.btncari);
        btnbag = findViewById(R.id.btnbag);
        btnhome = findViewById(R.id.btnhome);
        btnriwayat = findViewById(R.id.btnriwayat);
        btnprofil = findViewById(R.id.btnprofil);

        // Content
        card1 = findViewById(R.id.card1);
    }

    private void setupSearch() {
        // Tekan Enter pada search → ke CariActivity
        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            boolean isEnter = (event != null &&
                    event.getKeyCode() == KeyEvent.KEYCODE_ENTER &&
                    event.getAction() == KeyEvent.ACTION_DOWN);
            if (actionId == EditorInfo.IME_ACTION_SEARCH || isEnter) {
                String query = etSearch.getText().toString().trim();
                Intent intent = new Intent(DashboardActivity.this, CariActivity.class);
                if (!query.isEmpty()) {
                    intent.putExtra("query", query);
                }
                startActivity(intent);
                return true;
            }
            return false;
        });

        // Klik icon search di editText juga redirect
        etSearch.setOnClickListener(v ->
                startActivity(new Intent(DashboardActivity.this, CariActivity.class)));
    }

    private void setupBottomNav() {
        int idRole = sessionManager.getIdRole();

        btncari.setOnClickListener(v ->
                startActivity(new Intent(DashboardActivity.this, CariActivity.class)));

        btnbag.setOnClickListener(v -> {
            // User → Riwayat Pesanan | Freelancer → Kelola Layananku
            if (idRole == SessionManager.ROLE_FREELANCER) {
                startActivity(new Intent(DashboardActivity.this,
                        KelolaJasaFreelancer1Activity.class));
            } else {
                startActivity(new Intent(DashboardActivity.this,
                        RiwayatPesananActivity.class));
            }
        });

        btnhome.setOnClickListener(v -> {
            // Sudah di home, scroll ke atas
            Toast.makeText(this, "Beranda", Toast.LENGTH_SHORT).show();
        });

        btnriwayat.setOnClickListener(v ->
                startActivity(new Intent(DashboardActivity.this,
                        RiwayatPesananActivity.class)));

        btnprofil.setOnClickListener(v -> {
            // Placeholder logout sampai ProfilActivity dibuat di Phase berikutnya
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Profil")
                    .setMessage("Login sebagai: " + sessionManager.getNamaPengguna() +
                            "\nRole: " + getRoleLabel(idRole))
                    .setPositiveButton("Logout", (dialog, which) -> {
                        sessionManager.logout();
                        Intent intent = new Intent(DashboardActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                                Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .setNegativeButton("Tutup", null)
                    .show();
        });

        imgAvatar.setOnClickListener(v -> btnprofil.performClick());
    }

    private void setupContentClicks() {
        // Card pertama di "Pencarian Teratas" → halaman detail pemesanan
        if (card1 != null) {
            card1.setOnClickListener(v -> {
                // Sementara kirim ke PemesananActivity dengan layanan pertama
                Intent intent = new Intent(DashboardActivity.this, PemesananActivity.class);
                intent.putExtra("id_layanan", 1); // akan diganti dynamic di Phase 3
                startActivity(intent);
            });
        }
    }

    private String getRoleLabel(int idRole) {
        switch (idRole) {
            case SessionManager.ROLE_ADMIN: return "Admin";
            case SessionManager.ROLE_FREELANCER: return "Freelancer";
            default: return "User";
        }
    }

    @Override
    public void onBackPressed() {
        // Konfirmasi sebelum keluar dari dashboard
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Keluar Aplikasi")
                .setMessage("Apakah Anda yakin ingin keluar?")
                .setPositiveButton("Ya", (d, w) -> finish())
                .setNegativeButton("Tidak", null)
                .show();
    }
}