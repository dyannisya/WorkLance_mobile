package com.example.kelolajasa;

import android.content.Intent;
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
        title = findViewById(R.id.title);
        txtEmpty = findViewById(R.id.txtEmpty);
        tvCountMenunggu = findViewById(R.id.tvCountMenunggu);
        tvCountDiproses = findViewById(R.id.tvCountDiproses);
        tvCountSelesai = findViewById(R.id.tvCountSelesai);
        tvCountDibatalkan = findViewById(R.id.tvCountDibatalkan);
        editText1 = findViewById(R.id.editText1);
        btn1 = findViewById(R.id.btn1); // Semua
        btn2 = findViewById(R.id.btn2); // Menunggu
        btn3 = findViewById(R.id.btn3); // Diproses
        btn4 = findViewById(R.id.btn4); // Selesai
        btn5 = findViewById(R.id.btn5); // Dibatalkan
        recyclerView = findViewById(R.id.recyclerView);
        btncari = findViewById(R.id.btncari);
        btnbag = findViewById(R.id.btnbag);
        btnhome = findViewById(R.id.btnhome);
        btnriwayat = findViewById(R.id.btnriwayat);
        btnprofil = findViewById(R.id.btnprofil);

        if (title != null) title.setOnClickListener(v -> finish());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
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

    private void setupFilterTabs() {
        btn1.setOnClickListener(v -> { if (adapter != null) adapter.filterByStatus("Semua"); });
        btn2.setOnClickListener(v -> { if (adapter != null) adapter.filterByStatus("Menunggu"); });
        btn3.setOnClickListener(v -> { if (adapter != null) adapter.filterByStatus("Diproses"); });
        btn4.setOnClickListener(v -> { if (adapter != null) adapter.filterByStatus("Selesai"); });
        btn5.setOnClickListener(v -> { if (adapter != null) adapter.filterByStatus("Dibatalkan"); });
    }

    private void setupSearch() {
        if (editText1 == null) return;
        editText1.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void onTextChanged(CharSequence s, int start, int b, int c) {
                if (adapter != null) adapter.filterByKeyword(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void loadData() {
        // Update stat cards
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
    }

    private void showUbahStatusDialog(BookingDisplay item) {
        String[] statusOptions = {
                BookingDAO.STATUS_MENUNGGU,
                BookingDAO.STATUS_DIPROSES,
                BookingDAO.STATUS_SELESAI,
                "Dibatalkan"
        };

        // Tentukan posisi status saat ini
        int currentPos = 0;
        for (int i = 0; i < statusOptions.length; i++) {
            if (statusOptions[i].equals(item.getStatusBooking())) {
                currentPos = i;
                break;
            }
        }
        final int[] selected = { currentPos };

        new AlertDialog.Builder(this)
                .setTitle("Ubah Status Booking #" + item.getIdBooking())
                .setMessage("Client: " + item.getNamaClient() +
                        "\nLayanan: " + item.getNamaJasa() +
                        "\nStatus saat ini: " + item.getStatusBooking())
                .setSingleChoiceItems(statusOptions, currentPos,
                        (d, which) -> selected[0] = which)
                .setPositiveButton("Simpan", (d, w) -> {
                    String statusBaru = statusOptions[selected[0]];
                    int r = bookingDAO.updateStatus(item.getIdBooking(), statusBaru);
                    if (r > 0) {
                        Toast.makeText(this,
                                "Status diubah ke: " + statusBaru, Toast.LENGTH_SHORT).show();
                        loadData();
                    } else {
                        Toast.makeText(this, "Gagal mengubah status", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Batal", null)
                .show();
    }

    private void safeSetText(TextView tv, String val) {
        if (tv != null) tv.setText(val);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bookingDAO != null) bookingDAO.close();
    }
}