package com.example.kelolajasa;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kelolajasa.adapter.LayananCariAdapter;
import com.example.kelolajasa.database.LayananDAO;
import com.example.kelolajasa.model.LayananDisplay;

import java.util.List;

public class CariActivity extends AppCompatActivity {

    EditText editTextText3;
    ImageView filter, btncari, btnbag, btnhome, btnriwayat, btnprofil;
    LinearLayout rekomendasi, riwayat;
    RecyclerView recyclerViewHasil;
    TextView tvEmptyHasil;

    LayananDAO layananDAO;
    LayananCariAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cari);

        layananDAO = new LayananDAO(this);
        initViews();
        setupSearch();
        setupBottomNav();

        // Cek apakah ada query dari DashboardActivity
        String queryFromDashboard = getIntent().getStringExtra("query");
        if (queryFromDashboard != null && !queryFromDashboard.isEmpty()) {
            editTextText3.setText(queryFromDashboard);
            performSearch(queryFromDashboard);
        }
    }

    private void initViews() {
        editTextText3 = findViewById(R.id.editTextText3);
        filter = findViewById(R.id.filter);
        rekomendasi = findViewById(R.id.rekomendasi);
        riwayat = findViewById(R.id.riwayat);
        recyclerViewHasil = findViewById(R.id.recyclerViewHasil);
        tvEmptyHasil = findViewById(R.id.tvEmptyHasil);
        btncari = findViewById(R.id.btncari);
        btnbag = findViewById(R.id.btnbag);
        btnhome = findViewById(R.id.btnhome);
        btnriwayat = findViewById(R.id.btnriwayat);
        btnprofil = findViewById(R.id.btnprofil);

        if (recyclerViewHasil != null) {
            recyclerViewHasil.setLayoutManager(new LinearLayoutManager(this));
            adapter = new LayananCariAdapter(null, item -> {
                Intent intent = new Intent(CariActivity.this, PemesananActivity.class);
                intent.putExtra("id_layanan", item.getIdLayanan());
                startActivity(intent);
            });
            recyclerViewHasil.setAdapter(adapter);
        }

        if (filter != null) filter.setOnClickListener(v ->
                Toast.makeText(this, "Filter — Coming Soon", Toast.LENGTH_SHORT).show());

        // Klik pada chip rekomendasi kategori
        setupKategoriChips();
    }

    private void setupKategoriChips() {
        // IDs chip tidak ada, kita pakai kategori dari DB untuk filter
        // Biarkan chip statis berfungsi sebagai search shortcut
        // Chip di kategorirekom tidak punya ID, kita pakai onTouch pada parent
        LinearLayout kat1 = findViewById(R.id.kategorirekom);
        LinearLayout kat2 = findViewById(R.id.kategorirekom2);

        if (kat1 != null) {
            for (int i = 0; i < kat1.getChildCount(); i++) {
                View child = kat1.getChildAt(i);
                if (child instanceof TextView) {
                    String label = ((TextView) child).getText().toString();
                    child.setOnClickListener(v -> {
                        editTextText3.setText(label);
                        performSearch(label);
                    });
                }
            }
        }
        if (kat2 != null) {
            for (int i = 0; i < kat2.getChildCount(); i++) {
                View child = kat2.getChildAt(i);
                if (child instanceof TextView) {
                    String label = ((TextView) child).getText().toString();
                    child.setOnClickListener(v -> {
                        editTextText3.setText(label);
                        performSearch(label);
                    });
                }
            }
        }
    }

    private void setupSearch() {
        editTextText3.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void onTextChanged(CharSequence s, int start, int b, int c) {
                String query = s.toString().trim();
                if (query.isEmpty()) {
                    showDefaultState();
                } else {
                    performSearch(query);
                }
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void performSearch(String query) {
        List<LayananDisplay> results = layananDAO.searchDisplay(query);
        showSearchState(results);
    }

    private void showDefaultState() {
        if (rekomendasi != null) rekomendasi.setVisibility(View.VISIBLE);
        if (riwayat != null) riwayat.setVisibility(View.VISIBLE);
        if (recyclerViewHasil != null) recyclerViewHasil.setVisibility(View.GONE);
        if (tvEmptyHasil != null) tvEmptyHasil.setVisibility(View.GONE);
    }

    private void showSearchState(List<LayananDisplay> results) {
        if (rekomendasi != null) rekomendasi.setVisibility(View.GONE);
        if (riwayat != null) riwayat.setVisibility(View.GONE);

        boolean isEmpty = results == null || results.isEmpty();
        if (tvEmptyHasil != null) tvEmptyHasil.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        if (recyclerViewHasil != null)
            recyclerViewHasil.setVisibility(isEmpty ? View.GONE : View.VISIBLE);

        if (!isEmpty && adapter != null) {
            adapter.setData(results);
        }
    }

    private void setupBottomNav() {
        if (btncari != null) btncari.setOnClickListener(v ->
                Toast.makeText(this, "Cari", Toast.LENGTH_SHORT).show());
        if (btnbag != null) btnbag.setOnClickListener(v ->
                startActivity(new Intent(this, KelolaJasaFreelancer1Activity.class)));
        if (btnhome != null) btnhome.setOnClickListener(v -> {
            startActivity(new Intent(this, DashboardActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        });
        if (btnriwayat != null) btnriwayat.setOnClickListener(v ->
                startActivity(new Intent(this, RiwayatPesananActivity.class)));
        if (btnprofil != null) btnprofil.setOnClickListener(v ->
                startActivity(new Intent(this, ProfilActivity.class)));
    }

    @Override
    public void onBackPressed() {
        if (editTextText3 != null && !editTextText3.getText().toString().isEmpty()) {
            editTextText3.setText("");
            showDefaultState();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (layananDAO != null) layananDAO.close();
    }
}