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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kelolajasa.adapter.LayananCariAdapter;
import com.example.kelolajasa.database.LayananDAO;
import com.example.kelolajasa.model.LayananDisplay;

import java.util.ArrayList;
import java.util.List;

public class CariActivity extends AppCompatActivity {

    EditText editTextText3;
    ImageView filter, btncari, btnbag, btnhome, btnriwayat, btnprofil;
    LinearLayout rekomendasi, riwayat;
    RecyclerView recyclerViewHasil;
    TextView tvEmptyHasil;

    LayananDAO layananDAO;
    LayananCariAdapter adapter;

    // Variabel untuk menyimpan status filter saat ini
    private String filterLokasi = "";
    private int filterMinHarga = 0;
    private int filterMaxHarga = Integer.MAX_VALUE; // Default tak terhingga

    // Launcher untuk menangkap data kembalian dari FilterCariActivity
    private final ActivityResultLauncher<Intent> filterLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Intent data = result.getData();

                    // Ambil data filter yang dikirim
                    filterLokasi = data.getStringExtra("lokasi");
                    String minStr = data.getStringExtra("min_harga");
                    String maxStr = data.getStringExtra("max_harga");

                    // Konversi string harga ke integer (jika kosong, kembalikan ke default)
                    filterMinHarga = (minStr != null && !minStr.isEmpty()) ? Integer.parseInt(minStr) : 0;
                    filterMaxHarga = (maxStr != null && !maxStr.isEmpty()) ? Integer.parseInt(maxStr) : Integer.MAX_VALUE;

                    // Jalankan ulang pencarian agar filter langsung diterapkan
                    String currentQuery = editTextText3.getText().toString().trim();
                    performSearch(currentQuery);
                }
            }
    );

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
//        riwayat = findViewById(R.id.riwayat);
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

        // Panggil FilterCariActivity menggunakan filterLauncher (bukan startActivity biasa)
        if (filter != null) filter.setOnClickListener(v -> {
            Intent intent = new Intent(this, FilterCariActivity.class);
            filterLauncher.launch(intent);
        });

        setupKategoriChips();
    }

    private void setupKategoriChips() {
        LinearLayout kat1 = findViewById(R.id.kategorirekom);
        LinearLayout kat2 = findViewById(R.id.kategorirekom2);

        View.OnClickListener chipClickListener = v -> {
            if (v instanceof TextView) {
                String label = ((TextView) v).getText().toString();
                editTextText3.setText(label);
                editTextText3.setSelection(label.length());
            }
        };

        if (kat1 != null) {
            for (int i = 0; i < kat1.getChildCount(); i++) {
                kat1.getChildAt(i).setOnClickListener(chipClickListener);
            }
        }

        if (kat2 != null) {
            for (int i = 0; i < kat2.getChildCount(); i++) {
                kat2.getChildAt(i).setOnClickListener(chipClickListener);
            }
        }
    }

    private void setupSearch() {
        editTextText3.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void onTextChanged(CharSequence s, int start, int b, int c) {
                String query = s.toString().trim();
                // Jika pencarian kosong DAN tidak ada filter, tampilkan rekomendasi awal
                if (query.isEmpty() && filterLokasi.isEmpty() && filterMinHarga == 0 && filterMaxHarga == Integer.MAX_VALUE) {
                    showDefaultState();
                } else {
                    performSearch(query);
                }
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void performSearch(String query) {
        // 1. Ambil data pencarian mentah dari database
        List<LayananDisplay> results = layananDAO.searchDisplay(query);

        // 2. Jika tidak ada filter yang aktif, langsung tampilkan
        if (filterLokasi.isEmpty() && filterMinHarga == 0 && filterMaxHarga == Integer.MAX_VALUE) {
            showSearchState(results);
            return;
        }

        // 3. Terapkan filter lokasi dan harga secara manual
        List<LayananDisplay> filteredResults = new ArrayList<>();
        if (results != null) {
            for (LayananDisplay item : results) {
                boolean matchLokasi = true;
                boolean matchHarga = true;

                // Cek Lokasi (Pastikan getLokasi() ada di model LayananDisplay kamu)
                if (filterLokasi != null && !filterLokasi.isEmpty()) {
                    if (item.getLokasiKabupaten() == null || !item.getLokasiKabupaten().toLowerCase().contains(filterLokasi.toLowerCase())) {
                        matchLokasi = false;
                    }
                }

                // Cek Harga (Pastikan getHarga() ada di model LayananDisplay kamu dan bertipe int/double)
                if (item.getHarga() < filterMinHarga || item.getHarga() > filterMaxHarga) {
                    matchHarga = false;
                }

                // Jika lolos kedua tes filter, masukkan ke hasil akhir
                if (matchLokasi && matchHarga) {
                    filteredResults.add(item);
                }
            }
        }

        // 4. Tampilkan hasil yang sudah di-filter
        showSearchState(filteredResults);
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
            // Reset filter juga jika pengguna kembali
            filterLokasi = "";
            filterMinHarga = 0;
            filterMaxHarga = Integer.MAX_VALUE;
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