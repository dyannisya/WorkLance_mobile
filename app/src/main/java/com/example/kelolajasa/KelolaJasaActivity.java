package com.example.kelolajasa;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kelolajasa.adapter.JasaAdapter;
import com.example.kelolajasa.database.JasaDAO;
import com.example.kelolajasa.database.KategoriDAO;
import com.example.kelolajasa.database.SatuanDAO;
import com.example.kelolajasa.model.Jasa;
import com.example.kelolajasa.model.Kategori;
import com.example.kelolajasa.model.Satuan;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class KelolaJasaActivity extends AppCompatActivity {

    TextView title, tambahjasa, labelKategori, labelNama, txtEmpty;
    EditText editTextText3, inputNama;
    Spinner spinnerKategori;
    MaterialButton btn1, btn2, btn3, btnSimpan;
    RecyclerView recyclerView;
    ImageView btncari, btnbag, btnhome, btnriwayat, btnprofil;

    JasaDAO jasaDAO;
    KategoriDAO kategoriDAO;
    SatuanDAO satuanDAO;
    JasaAdapter jasaAdapter;

    List<Kategori> kategoriList = new ArrayList<>();
    int selectedKategoriId = -1;
    int currentTab = 2; // default: Jasa

    // Warna tab
    private static final int COLOR_ACTIVE_BG   = 0xFF161E54;
    private static final int COLOR_ACTIVE_TEXT  = 0xFFFFFFFF;
    private static final int COLOR_INACTIVE_BG  = 0xFFEEEEEE;
    private static final int COLOR_INACTIVE_TEXT = 0xFF161E54;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kelolajasa);

        jasaDAO     = new JasaDAO(this);
        kategoriDAO = new KategoriDAO(this);
        satuanDAO   = new SatuanDAO(this);

        initViews();
        setupBottomNav();
        setupTabs();
        loadKategoriSpinner();
        switchToTab(2);
        setupSearch();
    }

    private void initViews() {
        title           = findViewById(R.id.title);
        tambahjasa      = findViewById(R.id.tambahjasa);
        labelKategori   = findViewById(R.id.labelKategori);
        labelNama       = findViewById(R.id.labelNama);
        editTextText3   = findViewById(R.id.editTextText3);
        inputNama       = findViewById(R.id.inputNama);
        spinnerKategori = findViewById(R.id.spinnerKategori);
        btnSimpan       = findViewById(R.id.btnSimpan);
        txtEmpty        = findViewById(R.id.txtEmpty);
        recyclerView    = findViewById(R.id.recyclerView);
        btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);
        btn3 = findViewById(R.id.btn3);

        btncari    = findViewById(R.id.btncari);
        btnbag     = findViewById(R.id.btnbag);
        btnhome    = findViewById(R.id.btnhome);
        btnriwayat = findViewById(R.id.btnriwayat);
        btnprofil  = findViewById(R.id.btnprofil);

        if (title != null) title.setOnClickListener(v -> finish());

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setNestedScrollingEnabled(false);

        if (spinnerKategori != null) {
            spinnerKategori.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> p, View v, int pos, long id) {
                    if (pos < kategoriList.size())
                        selectedKategoriId = kategoriList.get(pos).getIdKategori();
                }
                @Override public void onNothingSelected(AdapterView<?> p) {}
            });
        }

        if (btnSimpan != null) btnSimpan.setOnClickListener(v -> prosesSimp());
    }

    // ── TAB HIGHLIGHT ──────────────────────────────────────────
    private void setTabActive(MaterialButton btn, boolean active) {
        if (btn == null) return;
        btn.setBackgroundTintList(ColorStateList.valueOf(
                active ? COLOR_ACTIVE_BG : COLOR_INACTIVE_BG));
        btn.setTextColor(active ? COLOR_ACTIVE_TEXT : COLOR_INACTIVE_TEXT);
    }

    private void setupTabs() {
        if (btn1 != null) btn1.setOnClickListener(v -> switchToTab(1));
        if (btn2 != null) btn2.setOnClickListener(v -> switchToTab(2));
        if (btn3 != null) btn3.setOnClickListener(v -> switchToTab(3));
    }

    private void switchToTab(int tab) {
        currentTab = tab;
        if (inputNama != null) { inputNama.setText(""); inputNama.setError(null); }

        setTabActive(btn1, tab == 1);
        setTabActive(btn2, tab == 2);
        setTabActive(btn3, tab == 3);

        switch (tab) {
            case 1: // Kategori
                if (tambahjasa != null) tambahjasa.setText("Tambah Kategori");
                if (labelNama != null) labelNama.setText("Nama Kategori");
                if (labelKategori != null) labelKategori.setVisibility(View.GONE);
                if (spinnerKategori != null) spinnerKategori.setVisibility(View.GONE);
                if (inputNama != null) inputNama.setHint("Masukkan nama kategori...");
                loadKategoriList();
                break;
            case 2: // Jasa
                if (tambahjasa != null) tambahjasa.setText("Tambah Jasa");
                if (labelNama != null) labelNama.setText("Nama Jasa");
                if (labelKategori != null) labelKategori.setVisibility(View.VISIBLE);
                if (spinnerKategori != null) spinnerKategori.setVisibility(View.VISIBLE);
                if (inputNama != null) inputNama.setHint("Masukkan Nama Jasa di sini..");
                loadJasaList();
                break;
            case 3: // Satuan
                if (tambahjasa != null) tambahjasa.setText("Tambah Satuan");
                if (labelNama != null) labelNama.setText("Nama Satuan");
                if (labelKategori != null) labelKategori.setVisibility(View.GONE);
                if (spinnerKategori != null) spinnerKategori.setVisibility(View.GONE);
                if (inputNama != null) inputNama.setHint("Masukkan nama satuan (misal: Jam, Project)...");
                loadSatuanList();
                break;
        }
    }

    private void loadKategoriSpinner() {
        kategoriList = kategoriDAO.getAll();
        List<String> namaList = new ArrayList<>();
        for (Kategori k : kategoriList) namaList.add(k.getNamaKategori());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, namaList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        if (spinnerKategori != null) {
            spinnerKategori.setAdapter(adapter);
            if (!kategoriList.isEmpty()) selectedKategoriId = kategoriList.get(0).getIdKategori();
        }
    }

    private void loadJasaList() {
        List<Jasa> list = jasaDAO.getAllWithKategori();
        showEmptyOrList(list.isEmpty());
        jasaAdapter = new JasaAdapter(list,
                jasa -> showHapusDialog("Hapus jasa \"" + jasa.getNamaJasa() + "\"?", () -> {
                    int r = jasaDAO.delete(jasa.getIdJasa());
                    if (r == -2) Toast.makeText(this, "Jasa masih dipakai layanan!", Toast.LENGTH_SHORT).show();
                    else loadJasaList();
                }),
                jasa -> {
                    if (inputNama != null) inputNama.setText(jasa.getNamaJasa());
                });
        recyclerView.setAdapter(jasaAdapter);
    }

    private void loadKategoriList() {
        List<Jasa> fakeList = new ArrayList<>();
        for (Kategori k : kategoriDAO.getAll()) {
            Jasa fake = new Jasa(k.getIdKategori(), 0, k.getNamaKategori());
            fake.setNamaKategori("Kategori");
            fakeList.add(fake);
        }
        showEmptyOrList(fakeList.isEmpty());
        JasaAdapter adapter = new JasaAdapter(fakeList,
                jasa -> showHapusDialog("Hapus kategori \"" + jasa.getNamaJasa() + "\"?", () -> {
                    int r = kategoriDAO.delete(jasa.getIdJasa());
                    if (r == -2) Toast.makeText(this, "Kategori masih dipakai jasa!", Toast.LENGTH_SHORT).show();
                    else { loadKategoriSpinner(); loadKategoriList(); }
                }),
                jasa -> { if (inputNama != null) inputNama.setText(jasa.getNamaJasa()); });
        recyclerView.setAdapter(adapter);
    }

    private void loadSatuanList() {
        List<Jasa> fakeList = new ArrayList<>();
        for (Satuan s : satuanDAO.getAll()) {
            Jasa fake = new Jasa(s.getIdSatuan(), 0, s.getNamaSatuan());
            fake.setNamaKategori("Satuan");
            fakeList.add(fake);
        }
        showEmptyOrList(fakeList.isEmpty());
        JasaAdapter adapter = new JasaAdapter(fakeList,
                jasa -> showHapusDialog("Hapus satuan \"" + jasa.getNamaJasa() + "\"?", () -> {
                    int r = satuanDAO.delete(jasa.getIdJasa());
                    if (r == -2) Toast.makeText(this, "Satuan masih dipakai layanan!", Toast.LENGTH_SHORT).show();
                    else loadSatuanList();
                }),
                jasa -> { if (inputNama != null) inputNama.setText(jasa.getNamaJasa()); });
        recyclerView.setAdapter(adapter);
    }

    private void prosesSimp() {
        if (inputNama == null) return;
        String nama = inputNama.getText().toString().trim();
        if (TextUtils.isEmpty(nama)) {
            inputNama.setError("Nama tidak boleh kosong");
            inputNama.requestFocus();
            return;
        }
        long result;
        switch (currentTab) {
            case 1:
                result = kategoriDAO.insert(nama);
                if (result == -2) { inputNama.setError("Kategori sudah ada"); return; }
                if (result > 0) {
                    Toast.makeText(this, "Kategori ditambahkan", Toast.LENGTH_SHORT).show();
                    inputNama.setText("");
                    loadKategoriSpinner();
                    loadKategoriList();
                }
                break;
            case 2:
                if (selectedKategoriId == -1 || kategoriList.isEmpty()) {
                    Toast.makeText(this, "Pilih kategori terlebih dahulu", Toast.LENGTH_SHORT).show();
                    return;
                }
                result = jasaDAO.insert(selectedKategoriId, nama);
                if (result == -2) { inputNama.setError("Jasa sudah ada di kategori ini"); return; }
                if (result > 0) {
                    Toast.makeText(this, "Jasa ditambahkan", Toast.LENGTH_SHORT).show();
                    inputNama.setText("");
                    loadJasaList();
                }
                break;
            case 3:
                result = satuanDAO.insert(nama);
                if (result == -2) { inputNama.setError("Satuan sudah ada"); return; }
                if (result > 0) {
                    Toast.makeText(this, "Satuan ditambahkan", Toast.LENGTH_SHORT).show();
                    inputNama.setText("");
                    loadSatuanList();
                }
                break;
        }
    }

    private void setupSearch() {
        if (editTextText3 == null) return;
        editTextText3.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) {
                if (currentTab == 2 && jasaAdapter != null) {
                    jasaAdapter.setData(s.toString().isEmpty()
                            ? jasaDAO.getAllWithKategori()
                            : jasaDAO.searchWithKategori(s.toString()));
                }
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void showEmptyOrList(boolean empty) {
        if (txtEmpty != null) txtEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
    }

    private void showHapusDialog(String pesan, Runnable onConfirm) {
        new AlertDialog.Builder(this)
                .setTitle("Konfirmasi Hapus").setMessage(pesan)
                .setPositiveButton("Hapus", (d, w) -> onConfirm.run())
                .setNegativeButton("Batal", null).show();
    }

    private void setupBottomNav() {
        if (btncari != null) btncari.setOnClickListener(v ->
                startActivity(new Intent(this, KelolaPenggunaActivity.class)));
        if (btnbag != null) btnbag.setOnClickListener(v ->
                Toast.makeText(this, "Kelola Jasa", Toast.LENGTH_SHORT).show());
        if (btnhome != null) btnhome.setOnClickListener(v ->
                startActivity(new Intent(this, AdminDashboardActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));
        if (btnriwayat != null) btnriwayat.setOnClickListener(v ->
                startActivity(new Intent(this, KelolaBookingActivity.class)));
        if (btnprofil != null) btnprofil.setOnClickListener(v ->
                startActivity(new Intent(this, KelolaPengajuanActivity.class)));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (jasaDAO != null) jasaDAO.close();
        if (kategoriDAO != null) kategoriDAO.close();
        if (satuanDAO != null) satuanDAO.close();
    }
}