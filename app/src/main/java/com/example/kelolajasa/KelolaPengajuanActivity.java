package com.example.kelolajasa;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kelolajasa.adapter.PengajuanAdapter;
import com.example.kelolajasa.database.PengajuanFreelancerDAO;
import com.example.kelolajasa.model.PengajuanFreelancer;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class KelolaPengajuanActivity extends AppCompatActivity {

    TextView title, txtEmpty;
    EditText editText1;
    MaterialButton btn1, btn2, btn3, btn4;
    RecyclerView recyclerView;
    ImageView btncari, btnbag, btnhome, btnriwayat, btnprofil;

    PengajuanFreelancerDAO pengajuanDAO;
    PengajuanAdapter adapter;
    String currentStatusFilter = "Semua";

    private static final int COLOR_ACTIVE_BG    = 0xFF161E54;
    private static final int COLOR_ACTIVE_TEXT   = 0xFFFFFFFF;
    private static final int COLOR_INACTIVE_BG   = 0xFFEEEEEE;
    private static final int COLOR_INACTIVE_TEXT = 0xFF161E54;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kelolapengajuan);

        pengajuanDAO = new PengajuanFreelancerDAO(this);
        initViews();
        setupBottomNav();
        setupFilterTabs();
        setupSearch();
        loadData();
    }

    private void initViews() {
        title        = findViewById(R.id.title);
        txtEmpty     = findViewById(R.id.txtEmpty);
        editText1    = findViewById(R.id.editText1);
        btn1 = findViewById(R.id.btn1); // Semua
        btn2 = findViewById(R.id.btn2); // Menunggu
        btn3 = findViewById(R.id.btn3); // Diterima
        btn4 = findViewById(R.id.btn4); // Ditolak
        recyclerView = findViewById(R.id.recyclerView);
        btncari    = findViewById(R.id.btncari);
        btnbag     = findViewById(R.id.btnbag);
        btnhome    = findViewById(R.id.btnhome);
        btnriwayat = findViewById(R.id.btnriwayat);
        btnprofil  = findViewById(R.id.btnprofil);

        if (title != null) title.setOnClickListener(v -> finish());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setTabActive(MaterialButton btn, boolean active) {
        if (btn == null) return;
        btn.setBackgroundTintList(ColorStateList.valueOf(
                active ? COLOR_ACTIVE_BG : COLOR_INACTIVE_BG));
        btn.setTextColor(active ? COLOR_ACTIVE_TEXT : COLOR_INACTIVE_TEXT);
    }

    private void applyStatusFilter(String status) {
        currentStatusFilter = status;
        setTabActive(btn1, "Semua".equals(status));
        setTabActive(btn2, "Menunggu".equals(status));
        setTabActive(btn3, "Diterima".equals(status));
        setTabActive(btn4, "Ditolak".equals(status));
        if (adapter != null) adapter.filterByStatus(status);
    }

    private void setupFilterTabs() {
        if (btn1 != null) btn1.setOnClickListener(v -> applyStatusFilter("Semua"));
        if (btn2 != null) btn2.setOnClickListener(v -> applyStatusFilter("Menunggu"));
        if (btn3 != null) btn3.setOnClickListener(v -> applyStatusFilter("Diterima"));
        if (btn4 != null) btn4.setOnClickListener(v -> applyStatusFilter("Ditolak"));
        applyStatusFilter("Semua"); // default aktif
    }

    private void setupSearch() {
        if (editText1 == null) return;
        editText1.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) {
                if (adapter != null) adapter.filterByKeyword(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void loadData() {
        List<PengajuanFreelancer> list = pengajuanDAO.getAll();
        List<String[]> withNama = pengajuanDAO.getRecentWithNama(200);
        List<String> namaList = new ArrayList<>();
        for (String[] row : withNama) namaList.add(row[1]);

        boolean isEmpty = list == null || list.isEmpty();
        if (txtEmpty != null) txtEmpty.setVisibility(isEmpty ? View.VISIBLE : View.GONE);

        adapter = new PengajuanAdapter(list, namaList,
                item -> showDetailDialog(item, namaList));
        recyclerView.setAdapter(adapter);
        applyStatusFilter(currentStatusFilter);
    }

    private void showDetailDialog(PengajuanFreelancer item, List<String> namaList) {
        List<PengajuanFreelancer> allList = pengajuanDAO.getAll();
        String nama = "-";
        for (int i = 0; i < allList.size(); i++) {
            if (allList.get(i).getIdPengajuan() == item.getIdPengajuan() && i < namaList.size()) {
                nama = namaList.get(i);
                break;
            }
        }

        String info = "Nama: " + nama
                + "\nNIK: " + item.getNik()
                + "\nTanggal: " + item.getTanggalPengajuan()
                + "\nStatus: " + item.getStatus()
                + "\n\nDeskripsi:\n" + item.getDeskripsi();

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Detail Pengajuan")
                .setMessage(info)
                .setNegativeButton("Tutup", null);

        final String finalNama = nama;
        if ("Menunggu".equals(item.getStatus())) {
            builder.setPositiveButton("✅ Terima", (d, w) -> {
                boolean ok = pengajuanDAO.prosesApproval(
                        this, item.getIdPengajuan(), "Diterima", "Pengajuan disetujui");
                if (ok) {
                    Toast.makeText(this, finalNama + " resmi jadi Freelancer!",
                            Toast.LENGTH_SHORT).show();
                    loadData();
                }
            });
            builder.setNeutralButton("❌ Tolak", (d, w) -> showTolakDialog(item));
        }
        builder.show();
    }

    private void showTolakDialog(PengajuanFreelancer item) {
        EditText etCatatan = new EditText(this);
        etCatatan.setHint("Alasan penolakan (opsional)");
        etCatatan.setPadding(40, 20, 40, 20);

        new AlertDialog.Builder(this)
                .setTitle("Tolak Pengajuan")
                .setView(etCatatan)
                .setPositiveButton("Tolak", (d, w) -> {
                    String catatan = etCatatan.getText().toString().trim();
                    boolean ok = pengajuanDAO.prosesApproval(this, item.getIdPengajuan(),
                            "Ditolak", TextUtils.isEmpty(catatan) ? "Tidak memenuhi syarat" : catatan);
                    if (ok) {
                        Toast.makeText(this, "Pengajuan ditolak", Toast.LENGTH_SHORT).show();
                        loadData();
                    }
                })
                .setNegativeButton("Batal", null).show();
    }

    private void setupBottomNav() {
        if (btncari != null) btncari.setOnClickListener(v ->
                startActivity(new Intent(this, KelolaPenggunaActivity.class)));
        if (btnbag != null) btnbag.setOnClickListener(v ->
                startActivity(new Intent(this, KelolaJasaActivity.class)));
        if (btnhome != null) btnhome.setOnClickListener(v ->
                startActivity(new Intent(this, AdminDashboardActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));
        if (btnriwayat != null) btnriwayat.setOnClickListener(v ->
                startActivity(new Intent(this, KelolaBookingActivity.class)));
        if (btnprofil != null) btnprofil.setOnClickListener(v ->
                Toast.makeText(this, "Kelola Pengajuan", Toast.LENGTH_SHORT).show());
    }

    @Override protected void onResume() { super.onResume(); loadData(); }
    @Override protected void onDestroy() {
        super.onDestroy();
        if (pengajuanDAO != null) pengajuanDAO.close();
    }
}