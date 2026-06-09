package com.example.kelolajasa;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
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

import com.example.kelolajasa.adapter.BookingFullAdapter;
import com.example.kelolajasa.database.BookingDAO;
import com.example.kelolajasa.model.BookingDisplay;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class KelolaBookingActivity extends AppCompatActivity {

    TextView title, txtEmpty;
    TextView tvCountMenunggu, tvCountDiproses, tvCountSelesai, tvCountDibatalkan;
    EditText editText1;
    MaterialButton btn1, btn2, btn3, btn4, btn5;
    RecyclerView recyclerView;
    ImageView btncari, btnbag, btnhome, btnriwayat, btnprofil;

    BookingDAO bookingDAO;
    BookingFullAdapter adapter;
    String currentStatusFilter = "Semua";

    private static final int COLOR_ACTIVE_BG    = 0xFF161E54;
    private static final int COLOR_ACTIVE_TEXT   = 0xFFFFFFFF;
    private static final int COLOR_INACTIVE_BG   = 0xFFEEEEEE;
    private static final int COLOR_INACTIVE_TEXT = 0xFF161E54;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kelolabooking);

        bookingDAO = new BookingDAO(this);
        initViews();
        setupBottomNav();
        setupFilterTabs();
        setupSearch();
        loadData();
    }

    private void initViews() {
        title              = findViewById(R.id.title);
        txtEmpty           = findViewById(R.id.txtEmpty);
        tvCountMenunggu    = findViewById(R.id.tvCountMenunggu);
        tvCountDiproses    = findViewById(R.id.tvCountDiproses);
        tvCountSelesai     = findViewById(R.id.tvCountSelesai);
        tvCountDibatalkan  = findViewById(R.id.tvCountDibatalkan);
        editText1          = findViewById(R.id.editText1);
        btn1 = findViewById(R.id.btn1); // Semua
        btn2 = findViewById(R.id.btn2); // Menunggu
        btn3 = findViewById(R.id.btn3); // Diproses
        btn4 = findViewById(R.id.btn4); // Selesai
        btn5 = findViewById(R.id.btn5); // Dibatalkan
        recyclerView       = findViewById(R.id.recyclerView);
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
        setTabActive(btn3, "Diproses".equals(status));
        setTabActive(btn4, "Selesai".equals(status));
        setTabActive(btn5, "Dibatalkan".equals(status));
        if (adapter != null) adapter.filterByStatus(status);
    }

    private void setupFilterTabs() {
        if (btn1 != null) btn1.setOnClickListener(v -> applyStatusFilter("Semua"));
        if (btn2 != null) btn2.setOnClickListener(v -> applyStatusFilter("Menunggu"));
        if (btn3 != null) btn3.setOnClickListener(v -> applyStatusFilter("Diproses"));
        if (btn4 != null) btn4.setOnClickListener(v -> applyStatusFilter("Selesai"));
        if (btn5 != null) btn5.setOnClickListener(v -> applyStatusFilter("Dibatalkan"));
        // Default "Semua" aktif
        applyStatusFilter("Semua");
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
        safeSetText(tvCountMenunggu,
                String.valueOf(bookingDAO.countByStatus(BookingDAO.STATUS_MENUNGGU)));
        safeSetText(tvCountDiproses,
                String.valueOf(bookingDAO.countByStatus(BookingDAO.STATUS_DIPROSES)));
        safeSetText(tvCountSelesai,
                String.valueOf(bookingDAO.countByStatus(BookingDAO.STATUS_SELESAI)));
        safeSetText(tvCountDibatalkan,
                String.valueOf(bookingDAO.countByStatus("Dibatalkan")));

        List<BookingDisplay> list = bookingDAO.getRecentWithDetails(200);
        boolean isEmpty = list == null || list.isEmpty();
        if (txtEmpty != null) txtEmpty.setVisibility(isEmpty ? View.VISIBLE : View.GONE);

        adapter = new BookingFullAdapter(list, item -> showUbahStatusDialog(item));
        recyclerView.setAdapter(adapter);
        applyStatusFilter(currentStatusFilter);
    }

    private void showUbahStatusDialog(BookingDisplay item) {
        String[] options = { "Menunggu", "Diproses", "Selesai", "Dibatalkan" };
        int currentPos = 0;
        for (int i = 0; i < options.length; i++) {
            if (options[i].equals(item.getStatusBooking())) { currentPos = i; break; }
        }
        final int[] selected = { currentPos };

        new AlertDialog.Builder(this)
                .setTitle("Ubah Status #" + item.getIdBooking())
                .setMessage("Client: " + item.getNamaClient()
                        + "\nLayanan: " + item.getNamaJasa()
                        + "\nStatus saat ini: " + item.getStatusBooking())
                .setSingleChoiceItems(options, currentPos, (d, w) -> selected[0] = w)
                .setPositiveButton("Simpan", (d, w) -> {
                    String statusBaru = options[selected[0]];
                    if (bookingDAO.updateStatus(item.getIdBooking(), statusBaru) > 0) {
                        Toast.makeText(this, "Status → " + statusBaru, Toast.LENGTH_SHORT).show();
                        loadData();
                    }
                })
                .setNegativeButton("Batal", null).show();
    }

    private void safeSetText(TextView tv, String val) {
        if (tv != null) tv.setText(val);
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
                Toast.makeText(this, "Kelola Booking", Toast.LENGTH_SHORT).show());
        if (btnprofil != null) btnprofil.setOnClickListener(v ->
                startActivity(new Intent(this, KelolaPengajuanActivity.class)));
    }

    @Override protected void onResume() { super.onResume(); loadData(); }
    @Override protected void onDestroy() {
        super.onDestroy();
        if (bookingDAO != null) bookingDAO.close();
    }
}