package com.example.kelolajasa;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kelolajasa.adapter.RiwayatAdapter;
import com.example.kelolajasa.database.BookingDAO;
import com.example.kelolajasa.model.RiwayatDisplay;

import java.util.ArrayList;
import java.util.List;

public class RiwayatPesananActivity extends AppCompatActivity {

    TextView tabSemua, tabMenunggu, tabDiproses, tabSelesai, tabDibatalkan;
    ImageView icChat, btncari, btnbag, btnhome, btnriwayat, btnprofil;
    LinearLayout containerPesanan;
    TextView tvEmptyRiwayat;
    ScrollView scrollPesanan;

    BookingDAO bookingDAO;
    SessionManager sessionManager;
    RiwayatAdapter adapter;
    RecyclerView recyclerView;

    boolean isFreelancerView = false;
    String activeTabText = "Semua";

    // Warna tab
    private static final int COLOR_ACTIVE   = 0xFFE65100; // orange
    private static final int COLOR_INACTIVE = 0xFF666666; // gray

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.riwayat_pesanan);

        sessionManager = new SessionManager(this);
        bookingDAO     = new BookingDAO(this);
        isFreelancerView = (sessionManager.getIdRole() == SessionManager.ROLE_FREELANCER);

        initViews();
        setupTabs();
        setupBottomNav();
        setupRecyclerView();
        // Set "Semua" aktif by default
        setTabActive(tabSemua, true);
        loadData("Semua");
    }

    private void initViews() {
        tabSemua      = findViewById(R.id.tabSemua);
        tabMenunggu   = findViewById(R.id.tabMenunggu);
        tabDiproses   = findViewById(R.id.tabDiproses);
        tabSelesai    = findViewById(R.id.tabSelesai);
        tabDibatalkan = findViewById(R.id.tabDibatalkan);
        icChat        = findViewById(R.id.icChat);
        containerPesanan = findViewById(R.id.containerPesanan);
        tvEmptyRiwayat   = findViewById(R.id.tvEmptyRiwayat);
        scrollPesanan    = findViewById(R.id.scrollPesanan);
        btncari    = findViewById(R.id.btncari);
        btnbag     = findViewById(R.id.btnbag);
        btnhome    = findViewById(R.id.btnhome);
        btnriwayat = findViewById(R.id.btnriwayat);
        btnprofil  = findViewById(R.id.btnprofil);

        if (icChat != null) icChat.setOnClickListener(v ->
                startActivity(new Intent(this, DaftarChatActivity.class)));
    }

    private void setTabActive(TextView tab, boolean active) {
        if (tab == null) return;
        tab.setTextColor(active ? COLOR_ACTIVE : COLOR_INACTIVE);
        tab.setTypeface(null, active ? Typeface.BOLD : Typeface.NORMAL);
    }

    private void resetAllTabs() {
        setTabActive(tabSemua,      false);
        setTabActive(tabMenunggu,   false);
        setTabActive(tabDiproses,   false);
        setTabActive(tabSelesai,    false);
        setTabActive(tabDibatalkan, false);
    }

    private void setupTabs() {
        View.OnClickListener tabClick = v -> {
            resetAllTabs();
            TextView clicked = (TextView) v;
            setTabActive(clicked, true);
            activeTabText = clicked.getText().toString();
            loadData(activeTabText);
        };

        if (tabSemua != null)      tabSemua.setOnClickListener(tabClick);
        if (tabMenunggu != null)   tabMenunggu.setOnClickListener(tabClick);
        if (tabDiproses != null)   tabDiproses.setOnClickListener(tabClick);
        if (tabSelesai != null)    tabSelesai.setOnClickListener(tabClick);
        if (tabDibatalkan != null) tabDibatalkan.setOnClickListener(tabClick);
    }

    private void setupRecyclerView() {
        if (containerPesanan == null) return;
        containerPesanan.removeAllViews();

        // Empty state
        if (tvEmptyRiwayat != null) {
            containerPesanan.addView(tvEmptyRiwayat);
        }

        // Inject RecyclerView
        recyclerView = new RecyclerView(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setNestedScrollingEnabled(false);
        containerPesanan.addView(recyclerView);
    }

    private void loadData(String statusFilter) {
        int idPengguna = sessionManager.getIdPengguna();
        List<RiwayatDisplay> allData;

        if (isFreelancerView) {
            allData = bookingDAO.getIncomingByFreelancer(idPengguna);
        } else {
            allData = bookingDAO.getByPenggunaWithDetails(idPengguna);
        }

        // Filter berdasarkan status tab
        List<RiwayatDisplay> filtered = new ArrayList<>();
        for (RiwayatDisplay r : allData) {
            if (statusFilter.equals("Semua") || r.getStatusBooking().equals(statusFilter)) {
                filtered.add(r);
            }
        }

        boolean isEmpty = filtered.isEmpty();
        if (tvEmptyRiwayat != null)
            tvEmptyRiwayat.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        if (recyclerView != null)
            recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);

        if (!isEmpty && recyclerView != null) {
            adapter = new RiwayatAdapter(this, filtered, isFreelancerView,
                    item -> showAksiDialog(item));
            recyclerView.setAdapter(adapter);
        }
    }

    private void showAksiDialog(RiwayatDisplay item) {
        if (isFreelancerView) {
            String[] options = item.getStatusBooking().equals(BookingDAO.STATUS_MENUNGGU)
                    ? new String[]{ "Terima (Diproses)", "Tolak (Batalkan)" }
                    : new String[]{ "Tandai Selesai", "Batalkan" };

            new AlertDialog.Builder(this)
                    .setTitle("Ubah Status Pesanan #" + item.getIdBooking())
                    .setMessage("Layanan: " + item.getNamaLayanan()
                            + "\nClient: " + item.getNamaFreelancer())
                    .setItems(options, (d, which) -> {
                        String newStatus;
                        if (item.getStatusBooking().equals(BookingDAO.STATUS_MENUNGGU)) {
                            newStatus = which == 0 ? BookingDAO.STATUS_DIPROSES : "Dibatalkan";
                        } else {
                            newStatus = which == 0 ? BookingDAO.STATUS_SELESAI : "Dibatalkan";
                        }
                        bookingDAO.updateStatus(item.getIdBooking(), newStatus);
                        Toast.makeText(this, "Status → " + newStatus, Toast.LENGTH_SHORT).show();
                        resetAllTabs();
                        setTabActive(tabSemua, true);
                        activeTabText = "Semua";
                        loadData("Semua");
                    })
                    .setNegativeButton("Batal", null).show();
        } else {
            if (item.getStatusBooking().equals(BookingDAO.STATUS_MENUNGGU)) {
                new AlertDialog.Builder(this)
                        .setTitle("Batalkan Pesanan?")
                        .setMessage("Layanan: " + item.getNamaLayanan()
                                + "\nFreelancer: " + item.getNamaFreelancer()
                                + "\n\nYakin ingin membatalkan?")
                        .setPositiveButton("Ya, Batalkan", (d, w) -> {
                            bookingDAO.updateStatus(item.getIdBooking(), "Dibatalkan");
                            Toast.makeText(this, "Pesanan dibatalkan", Toast.LENGTH_SHORT).show();
                            loadData(activeTabText);
                        })
                        .setNegativeButton("Tidak", null).show();
            } else {
                new AlertDialog.Builder(this)
                        .setTitle("Detail Pesanan #" + item.getIdBooking())
                        .setMessage("Layanan: " + item.getNamaLayanan()
                                + "\nFreelancer: " + item.getNamaFreelancer()
                                + "\nHarga: " + item.getHargaFormatted()
                                + "\nTanggal: " + item.getTanggalFormatted()
                                + "\nStatus: " + item.getStatusBooking())
                        .setPositiveButton("Tutup", null).show();
            }
        }
    }

    private void setupBottomNav() {
        if (btncari != null) btncari.setOnClickListener(v ->
                startActivity(new Intent(this, CariActivity.class)));
        if (btnbag != null) btnbag.setOnClickListener(v -> {
            if (isFreelancerView)
                startActivity(new Intent(this, KelolaJasaFreelancer1Activity.class));
            else
                Toast.makeText(this, "Riwayat Pesanan", Toast.LENGTH_SHORT).show();
        });
        if (btnhome != null) btnhome.setOnClickListener(v ->
                startActivity(new Intent(this, DashboardActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));
        if (btnriwayat != null) btnriwayat.setOnClickListener(v ->
                Toast.makeText(this, "Riwayat Pesanan", Toast.LENGTH_SHORT).show());
        if (btnprofil != null) btnprofil.setOnClickListener(v ->
                Toast.makeText(this, "Profil — Coming Soon", Toast.LENGTH_SHORT).show());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData(activeTabText);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bookingDAO != null) bookingDAO.close();
    }
}