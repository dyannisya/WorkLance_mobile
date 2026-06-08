package com.example.kelolajasa;

import android.content.Intent;
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

    // RecyclerView injected programmatically
    RecyclerView recyclerView;

    boolean isFreelancerView = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.riwayat_pesanan);

        sessionManager = new SessionManager(this);
        bookingDAO = new BookingDAO(this);
        isFreelancerView = (sessionManager.getIdRole() == SessionManager.ROLE_FREELANCER);

        initViews();
        setupTabs();
        setupBottomNav();
        setupRecyclerView();
        loadData("Semua");
    }

    private void initViews() {
        tabSemua = findViewById(R.id.tabSemua);
        tabMenunggu = findViewById(R.id.tabMenunggu);
        tabDiproses = findViewById(R.id.tabDiproses);
        tabSelesai = findViewById(R.id.tabSelesai);
        tabDibatalkan = findViewById(R.id.tabDibatalkan);
        icChat = findViewById(R.id.icChat);
        containerPesanan = findViewById(R.id.containerPesanan);
        tvEmptyRiwayat = findViewById(R.id.tvEmptyRiwayat);
        scrollPesanan = findViewById(R.id.scrollPesanan);
        btncari = findViewById(R.id.btncari);
        btnbag = findViewById(R.id.btnbag);
        btnhome = findViewById(R.id.btnhome);
        btnriwayat = findViewById(R.id.btnriwayat);
        btnprofil = findViewById(R.id.btnprofil);

        if (icChat != null) icChat.setOnClickListener(v ->
                startActivity(new Intent(this, DaftarChatActivity.class)));
    }

    private void setupRecyclerView() {
        // Hapus hardcoded cards dari container, inject RecyclerView
        if (containerPesanan != null) {
            containerPesanan.removeAllViews();

            // Tambah empty state
            if (tvEmptyRiwayat != null) containerPesanan.addView(tvEmptyRiwayat);

            // Inject RecyclerView
            recyclerView = new RecyclerView(this);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setNestedScrollingEnabled(false);
            containerPesanan.addView(recyclerView);
        }
    }

    private void setupTabs() {
        View.OnClickListener tabClick = v -> {
            // Reset semua tab ke abu-abu dan tanpa garis bawah
            resetTabStyles();

            // Ubah tab yang diklik menjadi oranye dan pasang garis bawah
            TextView clicked = (TextView) v;
            clicked.setTextColor(getResources().getColor(android.R.color.holo_orange_dark, null));
            clicked.setBackgroundResource(R.drawable.bg_tab_active); // Memindahkan garis bawah

            loadData(clicked.getText().toString());
        };

        if (tabSemua != null) tabSemua.setOnClickListener(tabClick);
        if (tabMenunggu != null) tabMenunggu.setOnClickListener(tabClick);
        if (tabDiproses != null) tabDiproses.setOnClickListener(tabClick);
        if (tabSelesai != null) tabSelesai.setOnClickListener(tabClick);
        if (tabDibatalkan != null) tabDibatalkan.setOnClickListener(tabClick);
    }

    private void resetTabStyles() {
        int grayColor = getResources().getColor(android.R.color.darker_gray, null);

        // Kumpulkan semua tab untuk di-reset secara massal
        TextView[] tabs = {tabSemua, tabMenunggu, tabDiproses, tabSelesai, tabDibatalkan};

        for (TextView tab : tabs) {
            if (tab != null) {
                tab.setTextColor(grayColor);
                tab.setBackgroundResource(R.drawable.bg_tab_transparan); // Menghapus garis bawah
            }
        }
    }

    private void loadData(String statusFilter) {
        int idPengguna = sessionManager.getIdPengguna();
        List<RiwayatDisplay> allData;

        if (isFreelancerView) {
            allData = bookingDAO.getIncomingByFreelancer(idPengguna);
        } else {
            allData = bookingDAO.getByPenggunaWithDetails(idPengguna);
        }

        // Filter by status
        List<RiwayatDisplay> filtered = new java.util.ArrayList<>();
        for (RiwayatDisplay r : allData) {
            if (statusFilter.equals("Semua") || r.getStatusBooking().equals(statusFilter)) {
                filtered.add(r);
            }
        }

        boolean isEmpty = filtered.isEmpty();
        if (tvEmptyRiwayat != null) tvEmptyRiwayat.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        if (recyclerView != null) recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);

        if (!isEmpty) {
            adapter = new RiwayatAdapter(this, filtered, isFreelancerView, item -> {
                showAksiDialog(item);
            });
            if (recyclerView != null) recyclerView.setAdapter(adapter);
        }
    }

    private void showAksiDialog(RiwayatDisplay item) {
        if (isFreelancerView) {
            // Freelancer: ubah status pesanan
            String[] options;
            if (item.getStatusBooking().equals(BookingDAO.STATUS_MENUNGGU)) {
                options = new String[]{ "Terima (Diproses)", "Tolak (Batalkan)" };
            } else {
                options = new String[]{ "Tandai Selesai", "Batalkan" };
            }
            new AlertDialog.Builder(this)
                    .setTitle("Ubah Status Pesanan #" + item.getIdBooking())
                    .setMessage("Layanan: " + item.getNamaLayanan() +
                            "\nClient: " + item.getNamaFreelancer())
                    .setItems(options, (d, which) -> {
                        String newStatus;
                        if (item.getStatusBooking().equals(BookingDAO.STATUS_MENUNGGU)) {
                            newStatus = which == 0 ? BookingDAO.STATUS_DIPROSES : "Dibatalkan";
                        } else {
                            newStatus = which == 0 ? BookingDAO.STATUS_SELESAI : "Dibatalkan";
                        }
                        bookingDAO.updateStatus(item.getIdBooking(), newStatus);
                        Toast.makeText(this, "Status diubah ke: " + newStatus, Toast.LENGTH_SHORT).show();
                        loadData("Semua");
                        resetTabStyles();
                        if (tabSemua != null) {
                            tabSemua.setTextColor(getResources().getColor(android.R.color.holo_orange_dark, null));
                            tabSemua.setBackgroundResource(R.drawable.bg_tab_active); // Kembalikan garis bawah ke Semua
                        }
                    })
                    .setNegativeButton("Batal", null)
                    .show();
        } else {
            // User: detail atau batalkan
            if (item.getStatusBooking().equals(BookingDAO.STATUS_MENUNGGU)) {
                new AlertDialog.Builder(this)
                        .setTitle("Batalkan Pesanan?")
                        .setMessage("Layanan: " + item.getNamaLayanan() +
                                "\nFreelancer: " + item.getNamaFreelancer() +
                                "\n\nYakin ingin membatalkan pesanan ini?")
                        .setPositiveButton("Ya, Batalkan", (d, w) -> {
                            bookingDAO.updateStatus(item.getIdBooking(), "Dibatalkan");
                            Toast.makeText(this, "Pesanan dibatalkan", Toast.LENGTH_SHORT).show();
                            loadData("Semua");
                        })
                        .setNegativeButton("Tidak", null)
                        .show();
            } else {
                new AlertDialog.Builder(this)
                        .setTitle("Detail Pesanan #" + item.getIdBooking())
                        .setMessage("Layanan: " + item.getNamaLayanan() +
                                "\nFreelancer: " + item.getNamaFreelancer() +
                                "\nHarga: " + item.getHargaFormatted() +
                                "\nTanggal: " + item.getTanggalFormatted() +
                                "\nStatus: " + item.getStatusBooking())
                        .setPositiveButton("Tutup", null)
                        .show();
            }
        }
    }

    private void setupBottomNav() {
        if (btncari != null) btncari.setOnClickListener(v ->
                startActivity(new Intent(this, CariActivity.class)));
        if (btnbag != null) btnbag.setOnClickListener(v -> {
            if (isFreelancerView) startActivity(new Intent(this, KelolaJasaFreelancer2Activity.class));
            else startActivity(new Intent(this, KelolaJasaFreelancer1Activity.class));
        });
        if (btnhome != null) btnhome.setOnClickListener(v -> {
            Intent intent = new Intent(this, DashboardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });
        if (btnriwayat != null) btnriwayat.setOnClickListener(v ->
                Toast.makeText(this, "Riwayat Pesanan", Toast.LENGTH_SHORT).show());
        if (btnprofil != null) btnprofil.setOnClickListener(v ->
                startActivity(new Intent(this, ProfilActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData("Semua");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bookingDAO != null) bookingDAO.close();
    }
}