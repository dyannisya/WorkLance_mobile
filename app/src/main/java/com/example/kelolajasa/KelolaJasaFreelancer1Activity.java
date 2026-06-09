package com.example.kelolajasa;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class KelolaJasaFreelancer1Activity extends AppCompatActivity {

    Button dftrfreelancer;
    ImageView btncari, btnbag, btnhome, btnriwayat, btnprofil;

    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(this);

        // Jika sudah freelancer, langsung ke halaman kelola layanan
        if (sessionManager.getIdRole() == SessionManager.ROLE_FREELANCER) {
            startActivity(new Intent(this, KelolaJasaFreelancer2Activity.class));
            finish();
            return;
        }

        setContentView(R.layout.kelolajasafreelancer1);

        dftrfreelancer = findViewById(R.id.dftrfreelancer);
        btncari = findViewById(R.id.btncari);
        btnbag = findViewById(R.id.btnbag);
        btnhome = findViewById(R.id.btnhome);
        btnriwayat = findViewById(R.id.btnriwayat);
        btnprofil = findViewById(R.id.btnprofil);

        if (dftrfreelancer != null) {
            dftrfreelancer.setOnClickListener(v -> {
                // Cek apakah sudah ada pengajuan aktif
                com.example.kelolajasa.database.PengajuanFreelancerDAO dao =
                        new com.example.kelolajasa.database.PengajuanFreelancerDAO(this);
                com.example.kelolajasa.model.PengajuanFreelancer existing =
                        dao.getByPengguna(sessionManager.getIdPengguna());
                dao.close();

                // Jika data pengajuan ditemukan di database
                if (existing != null && existing.getStatus() != null) {
                    String status = existing.getStatus();

                    // Gunakan equalsIgnoreCase agar aman dari perbedaan huruf besar/kecil
                    if (status.equalsIgnoreCase("Menunggu") || status.equalsIgnoreCase("Pending")) {
                        Toast.makeText(this,
                                "Pengajuan Anda sedang ditinjau admin. Harap tunggu.",
                                Toast.LENGTH_LONG).show();

                    } else if (status.equalsIgnoreCase("Diterima")) {
                        Toast.makeText(this,
                                "Pengajuan Anda telah diterima. Silakan login ulang.",
                                Toast.LENGTH_LONG).show();

                    } else {
                        // Jika statusnya "Ditolak" atau lainnya, izinkan daftar lagi
                        startActivity(new Intent(this, DaftarFreelancerActivity.class));
                    }
                } else {
                    // Jika belum ada data pengajuan sama sekali (existing == null)
                    startActivity(new Intent(this, DaftarFreelancerActivity.class));
                }
            });
        }

        setupBottomNav();
    }

    private void setupBottomNav() {
        if (btncari != null) btncari.setOnClickListener(v ->
                startActivity(new Intent(this, CariActivity.class)));
        if (btnbag != null) btnbag.setOnClickListener(v ->
                Toast.makeText(this, "Kelola Jasa", Toast.LENGTH_SHORT).show());
        if (btnhome != null) btnhome.setOnClickListener(v -> {
            startActivity(new Intent(this, DashboardActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        });
        if (btnriwayat != null) btnriwayat.setOnClickListener(v ->
                startActivity(new Intent(this, RiwayatPesananActivity.class)));
        if (btnprofil != null) btnprofil.setOnClickListener(v ->
                startActivity(new Intent(this, ProfilActivity.class)));

    }
}