package com.example.kelolajasa;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import com.example.kelolajasa.adapter.DashboardLayananAdapter;
import com.example.kelolajasa.database.LayananDAO;
import com.example.kelolajasa.model.LayananDisplay;

import java.util.List;

public class DashboardActivity extends AppCompatActivity {

    EditText etSearch;
    ImageView imgAvatar, btncari, btnbag, btnhome, btnriwayat, btnprofil;
    LinearLayout card1;

    private RecyclerView rvLayananDashboard;

    SessionManager sessionManager;
    // ID layanan pertama dari DB (default -1 jika kosong)
    private int firstLayananId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);

        sessionManager = new SessionManager(this);

        if (!sessionManager.isLoggedIn()) {
            startActivity(new Intent(this, MainActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
            return;
        }

        // Load ID layanan pertama dari DB untuk card1
        loadFirstLayananId();

        initViews();
        setupSearch();
        setupBottomNav();
        setupContentClicks();
    }

    private void loadFirstLayananId() {
        LayananDAO layananDAO = new LayananDAO(this);
        try {
            List<LayananDisplay> all = layananDAO.getAllDisplay();
            if (all != null && !all.isEmpty()) {
                firstLayananId = all.get(0).getIdLayanan();
            }
        } finally {
            layananDAO.close();
        }
    }

    private void initViews() {
        etSearch   = findViewById(R.id.editTextText3);
        imgAvatar  = findViewById(R.id.imgAvatar);
        btncari    = findViewById(R.id.btncari);
        btnbag     = findViewById(R.id.btnbag);
        btnhome    = findViewById(R.id.btnhome);
        btnriwayat = findViewById(R.id.btnriwayat);
        btnprofil  = findViewById(R.id.btnprofil);
        card1      = findViewById(R.id.card1);
        rvLayananDashboard = findViewById(R.id.rvLayananDashboard);
        loadDashboardLayanan();
    }

    private void loadDashboardLayanan() {

        LayananDAO dao = new LayananDAO(this);

        List<LayananDisplay> list =
                dao.getAllDisplay();

        dao.close();

        rvLayananDashboard.setLayoutManager(
                new LinearLayoutManager(
                        this,
                        LinearLayoutManager.HORIZONTAL,
                        false
                )
        );

        rvLayananDashboard.setAdapter(
                new DashboardLayananAdapter(
                        this,
                        list
                )
        );
    }

    private void setupSearch() {
        if (etSearch == null) return;
        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            boolean isEnter = (event != null
                    && event.getKeyCode() == KeyEvent.KEYCODE_ENTER
                    && event.getAction() == KeyEvent.ACTION_DOWN);
            if (actionId == EditorInfo.IME_ACTION_SEARCH || isEnter) {
                String q = etSearch.getText().toString().trim();
                Intent intent = new Intent(this, CariActivity.class);
                if (!q.isEmpty()) intent.putExtra("query", q);
                startActivity(intent);
                return true;
            }
            return false;
        });
        etSearch.setOnClickListener(v ->
                startActivity(new Intent(this, CariActivity.class)));
    }

    private void setupBottomNav() {
        int idRole = sessionManager.getIdRole();

        if (btncari != null) btncari.setOnClickListener(v ->
                startActivity(new Intent(this, CariActivity.class)));

        if (btnbag != null) btnbag.setOnClickListener(v -> {
            if (idRole == SessionManager.ROLE_FREELANCER) {
                startActivity(new Intent(this, KelolaJasaFreelancer2Activity.class));
            } else {
                startActivity(new Intent(this, KelolaJasaFreelancer1Activity.class));
            }
        });

        if (btnhome != null) btnhome.setOnClickListener(v ->
                Toast.makeText(this, "Beranda", Toast.LENGTH_SHORT).show());

        if (btnriwayat != null) btnriwayat.setOnClickListener(v ->
                startActivity(new Intent(this, RiwayatPesananActivity.class)));

        if (btnprofil != null) btnprofil.setOnClickListener(v ->
                startActivity(new Intent(this, ProfilActivity.class)));

        if (imgAvatar != null) imgAvatar.setOnClickListener(v ->
                showProfilDialog(idRole));
    }

    private void showProfilDialog(int idRole) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Profil")
                .setMessage("Login sebagai: " + sessionManager.getNamaPengguna()
                        + "\nEmail: " + sessionManager.getEmail()
                        + "\nRole: " + getRoleLabel(idRole))
                .setPositiveButton("Logout", (d, w) -> {
                    sessionManager.logout();
                    startActivity(new Intent(this, MainActivity.class)
                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    finish();
                })
                .setNegativeButton("Tutup", null)
                .show();
    }

    private void setupContentClicks() {
        if (card1 == null) return;
        card1.setOnClickListener(v -> {
            if (firstLayananId == -1) {
                Toast.makeText(this, "Belum ada layanan tersedia", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(this, PemesananActivity.class);
            intent.putExtra("id_layanan", firstLayananId);
            startActivity(intent);
        });
    }

    private String getRoleLabel(int idRole) {
        switch (idRole) {
            case SessionManager.ROLE_ADMIN:      return "Admin";
            case SessionManager.ROLE_FREELANCER: return "Freelancer";
            default:                             return "User";
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Keluar Aplikasi")
                .setMessage("Apakah Anda yakin ingin keluar?")
                .setPositiveButton("Ya", (d, w) -> finish())
                .setNegativeButton("Tidak", null)
                .show();
    }
}