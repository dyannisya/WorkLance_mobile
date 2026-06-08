package com.example.kelolajasa;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kelolajasa.adapter.BookingAdminAdapter;
import com.example.kelolajasa.database.BookingDAO;
import com.example.kelolajasa.database.PengajuanFreelancerDAO;
import com.example.kelolajasa.model.BookingDisplay;

import java.util.List;

public class AdminDashboardActivity extends AppCompatActivity {

    // Stats
    TextView tvTotalPengguna, tvTotalFreelancer, tvTotalBooking, tvTotalSelesai;

    // Pengajuan preview
    TextView namafreelancer1, namafreelancer2;
    ImageView btnstatus1, btnstatus2, btndetail1, btndetail2;

    // Booking table
    RecyclerView recyclerView;
    TextView txtEmpty;
    TextView tvHeaderBooking, tvHeaderPengajuan;

    // Search
    EditText editText1;

    // Bottom nav (admin)
    ImageView btncari, btnbag, btnhome, btnriwayat, btnprofil;
    ImageView imgAvatar;

    // DAO & Adapter
    BookingDAO bookingDAO;
    PengajuanFreelancerDAO pengajuanDAO;
    BookingAdminAdapter adapter;

    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_admin);

        sessionManager = new SessionManager(this);

        // Guard: pastikan hanya Admin yang bisa akses
        if (!sessionManager.isLoggedIn() ||
                sessionManager.getIdRole() != SessionManager.ROLE_ADMIN) {
            startActivity(new Intent(this, MainActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
            return;
        }

        bookingDAO = new BookingDAO(this);
        pengajuanDAO = new PengajuanFreelancerDAO(this);

        initViews();
        loadStats();
        loadBookingTable();
        loadPengajuanPreview();
        setupSearch();
        setupBottomNav();
        setupHeaderClicks();
    }

    private void initViews() {
        // Stats
        tvTotalPengguna = findViewById(R.id.tvTotalPengguna);
        tvTotalFreelancer = findViewById(R.id.tvTotalFreelancer);
        tvTotalBooking = findViewById(R.id.tvTotalBooking);
        tvTotalSelesai = findViewById(R.id.tvTotalSelesai);

        // Pengajuan preview
        namafreelancer1 = findViewById(R.id.namafreelancer1);
        namafreelancer2 = findViewById(R.id.namafreelancer2);
        btnstatus1 = findViewById(R.id.btnstatus1);
        btnstatus2 = findViewById(R.id.btnstatus2);
        btndetail1 = findViewById(R.id.btndetail1);
        btndetail2 = findViewById(R.id.btndetail2);

        // Booking table
        recyclerView = findViewById(R.id.recyclerView);
        txtEmpty = findViewById(R.id.txtEmpty);
        tvHeaderBooking = findViewById(R.id.textView7);
        tvHeaderPengajuan = findViewById(R.id.textView9);

        // Search & Nav
        editText1 = findViewById(R.id.editText1);
        btncari = findViewById(R.id.btncari);
        btnbag = findViewById(R.id.btnbag);
        btnhome = findViewById(R.id.btnhome);
        btnriwayat = findViewById(R.id.btnriwayat);
        btnprofil = findViewById(R.id.btnprofil);
        imgAvatar = findViewById(R.id.imgAvatar);

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setNestedScrollingEnabled(false);
        adapter = new BookingAdminAdapter(this, null);
        recyclerView.setAdapter(adapter);
    }

    private void loadStats() {
        // Total user (role 2)
        int totalPengguna = bookingDAO.countPenggunaByRole(SessionManager.ROLE_USER);
        // Total freelancer (role 3)
        int totalFreelancer = bookingDAO.countPenggunaByRole(SessionManager.ROLE_FREELANCER);
        // Total booking
        int totalBooking = bookingDAO.countAll();
        // Total selesai
        int totalSelesai = bookingDAO.countByStatus(BookingDAO.STATUS_SELESAI);

        if (tvTotalPengguna != null)
            tvTotalPengguna.setText(String.valueOf(totalPengguna));
        if (tvTotalFreelancer != null)
            tvTotalFreelancer.setText(String.valueOf(totalFreelancer));
        if (tvTotalBooking != null)
            tvTotalBooking.setText(String.valueOf(totalBooking));
        if (tvTotalSelesai != null)
            tvTotalSelesai.setText(String.valueOf(totalSelesai));
    }

    private void loadBookingTable() {
        List<BookingDisplay> recent = bookingDAO.getRecentWithDetails(10);
        if (recent == null || recent.isEmpty()) {
            if (txtEmpty != null) {
                txtEmpty.setVisibility(android.view.View.VISIBLE);
            }
        } else {
            if (txtEmpty != null) {
                txtEmpty.setVisibility(android.view.View.GONE);
            }
            adapter.setData(recent);
        }
    }

    private void loadPengajuanPreview() {
        List<String[]> recent = pengajuanDAO.getRecentWithNama(2);

        if (recent.size() >= 1) {
            if (namafreelancer1 != null)
                namafreelancer1.setText(recent.get(0)[1]);
            // Status badge (drawable yang ada: status_diterima)
            if (btnstatus1 != null) {
                String status = recent.get(0)[2];
                btnstatus1.setImageResource("Diterima".equals(status)
                        ? R.drawable.status_diterima : R.drawable.status_detail);
            }
        }
        if (recent.size() >= 2) {
            if (namafreelancer2 != null)
                namafreelancer2.setText(recent.get(1)[1]);
            if (btnstatus2 != null) {
                String status = recent.get(1)[2];
                btnstatus2.setImageResource("Diterima".equals(status)
                        ? R.drawable.status_diterima : R.drawable.status_detail);
            }
        }

        // Klik detail → ke halaman Kelola Pengajuan
        if (btndetail1 != null) btndetail1.setOnClickListener(v ->
                startActivity(new Intent(this, KelolaPengajuanActivity.class)));
        if (btndetail2 != null) btndetail2.setOnClickListener(v ->
                startActivity(new Intent(this, KelolaPengajuanActivity.class)));
    }

    private void setupSearch() {
        if (editText1 == null) return;
        editText1.setOnEditorActionListener((v, actionId, event) -> {
            String q = editText1.getText().toString().trim();
            // Di Phase 4 → filter tabel booking berdasarkan q
            Toast.makeText(this, "Cari: " + q, Toast.LENGTH_SHORT).show();
            return false;
        });
    }

    private void setupBottomNav() {
        // Admin bottom nav:
        // btncari(ic_klpengguna) → KelolaPenggunaActivity
        // btnbag(ic_bag)         → KelolaJasaActivity
        // btnhome(ic_dashboard)  → AdminDashboardActivity (sudah di sini)
        // btnriwayat(ic_booking) → KelolaBookingActivity
        // btnprofil(ic_pengajuan)→ KelolaPengajuanActivity

        if (btncari != null) btncari.setOnClickListener(v ->
                startActivity(new Intent(this, KelolaPenggunaActivity.class)));

        if (btnbag != null) btnbag.setOnClickListener(v ->
                startActivity(new Intent(this, KelolaJasaActivity.class)));

        if (btnhome != null) btnhome.setOnClickListener(v ->
                Toast.makeText(this, "Dashboard Admin", Toast.LENGTH_SHORT).show());

        if (btnriwayat != null) btnriwayat.setOnClickListener(v ->
                startActivity(new Intent(this, KelolaBookingActivity.class)));

        if (btnprofil != null) btnprofil.setOnClickListener(v ->
                startActivity(new Intent(this, KelolaPengajuanActivity.class)));

        if (imgAvatar != null) imgAvatar.setOnClickListener(v -> {
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Admin")
                    .setMessage("Login sebagai: " + sessionManager.getNamaPengguna())
                    .setPositiveButton("Logout", (d, w) -> {
                        sessionManager.logout();
                        Intent intent = new Intent(this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                                Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .setNegativeButton("Tutup", null)
                    .show();
        });
    }

    private void setupHeaderClicks() {
        // "Lihat Semua Booking"
        if (tvHeaderBooking != null) tvHeaderBooking.setOnClickListener(v ->
                startActivity(new Intent(this, KelolaBookingActivity.class)));

        // "Lihat Semua Pengajuan"
        if (tvHeaderPengajuan != null) tvHeaderPengajuan.setOnClickListener(v ->
                startActivity(new Intent(this, KelolaPengajuanActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh stats & data saat kembali ke dashboard
        loadStats();
        loadBookingTable();
        loadPengajuanPreview();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bookingDAO != null) bookingDAO.close();
        if (pengajuanDAO != null) pengajuanDAO.close();
    }

    @Override
    public void onBackPressed() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Keluar Aplikasi")
                .setMessage("Apakah Anda yakin ingin keluar?")
                .setPositiveButton("Ya", (d, w) -> finish())
                .setNegativeButton("Tidak", null)
                .show();
    }
}