package com.example.kelolajasa;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.kelolajasa.database.JasaDAO;
import com.example.kelolajasa.database.KategoriDAO;
import com.example.kelolajasa.database.LayananDAO;
import com.example.kelolajasa.database.SatuanDAO;
import com.example.kelolajasa.model.Jasa;
import com.example.kelolajasa.model.Kategori;
import com.example.kelolajasa.model.Layanan;
import com.example.kelolajasa.model.Satuan;

import java.util.ArrayList;
import java.util.List;

public class EditJasaFreelancerActivity extends AppCompatActivity {

    // Views
    private Spinner spinnerKategori, spinnerJasa, spinnerTipeHarga;
    private EditText etHarga, etNamaJasa, etDeskripsi;
    private com.google.android.material.button.MaterialButton btnSimpanEdit;

    // Data
    private LayananDAO layananDAO;
    private KategoriDAO kategoriDAO;
    private JasaDAO jasaDAO;
    private SatuanDAO satuanDAO;

    private List<Kategori> listKategori = new ArrayList<>();
    private List<Jasa> listJasa = new ArrayList<>();
    private List<Satuan> listSatuan = new ArrayList<>();

    private int idLayanan = -1;
    private Layanan layananLama;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editjasafreelancer);

        idLayanan = getIntent().getIntExtra("id_layanan", -1);
        if (idLayanan == -1) {
            Toast.makeText(this, "Data layanan tidak ditemukan", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        layananDAO  = new LayananDAO(this);
        kategoriDAO = new KategoriDAO(this);
        jasaDAO     = new JasaDAO(this);
        satuanDAO   = new SatuanDAO(this);

        initViews();
        loadSpinners();
        loadLayananLama();
    }

    private void initViews() {
        spinnerKategori  = findViewById(R.id.spinnerKategori);
        spinnerJasa      = findViewById(R.id.spinnerJasa);
        spinnerTipeHarga = findViewById(R.id.spinnerTipeHarga);
        etHarga          = findViewById(R.id.etHarga);
        etNamaJasa       = findViewById(R.id.etNamaJasa);
        etDeskripsi      = findViewById(R.id.etDeskripsi);
        btnSimpanEdit    = findViewById(R.id.btnSimpanEdit);

        // Tombol back — LinearLayout header (tidak ada ID, cari via parent)
        View header = findViewById(R.id.linearLayout);
        if (header instanceof android.view.ViewGroup) {
            // Coba cari ImageView atau TextView dengan icon back di level pertama
        }

        // Tambahkan listener kategori → update spinner jasa
        if (spinnerKategori != null) {
            spinnerKategori.setOnItemSelectedListener(
                    new android.widget.AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(android.widget.AdapterView<?> parent,
                                                   android.view.View view, int pos, long id) {
                            if (pos >= 0 && pos < listKategori.size() && layananLama != null) {
                                // Hanya update jasa spinner saat user mengubah kategori
                                // (bukan saat load pertama kali — ditangani loadLayananLama)
                                loadJasaByKategori(listKategori.get(pos).getIdKategori(), -1);
                            }
                        }
                        @Override public void onNothingSelected(android.widget.AdapterView<?> parent) {}
                    });
        }

        if (btnSimpanEdit != null) btnSimpanEdit.setOnClickListener(v -> simpanPerubahan());
    }

    private void loadSpinners() {
        // Kategori
        listKategori = kategoriDAO.getAll();
        List<String> namaKategori = new ArrayList<>();
        for (Kategori k : listKategori) namaKategori.add(k.getNamaKategori());
        setSpinner(spinnerKategori, namaKategori);

        // Satuan
        listSatuan = satuanDAO.getAll();
        List<String> namaSatuan = new ArrayList<>();
        for (Satuan s : listSatuan) namaSatuan.add(s.getNamaSatuan());
        setSpinner(spinnerTipeHarga, namaSatuan);
    }

    /**
     * Load data layanan lama lalu isi semua field.
     * PENTING: dipanggil setelah loadSpinners() agar adapter sudah siap.
     */
    private void loadLayananLama() {
        // Ambil layanan via getByFreelancer lalu filter, atau buat method getById
        // Untuk sederhananya kita gunakan rawQuery langsung
        android.database.Cursor c = null;
        try {
            c = layananDAO.getDb().rawQuery(
                    "SELECT * FROM layanan WHERE id_layanan = ?",
                    new String[]{ String.valueOf(idLayanan) }
            );
            if (c != null && c.moveToFirst()) {
                layananLama = new Layanan(
                        c.getInt(c.getColumnIndexOrThrow("id_layanan")),
                        c.getInt(c.getColumnIndexOrThrow("id_pengguna")),
                        c.getInt(c.getColumnIndexOrThrow("id_jasa")),
                        c.getInt(c.getColumnIndexOrThrow("id_satuan")),
                        c.getInt(c.getColumnIndexOrThrow("tarif")),
                        c.getString(c.getColumnIndexOrThrow("deskripsi")),
                        c.getString(c.getColumnIndexOrThrow("namajasa"))
                );
            }
        } finally {
            if (c != null) c.close();
        }

        if (layananLama == null) {
            Toast.makeText(this, "Layanan tidak ditemukan", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Isi field teks
        if (etHarga    != null) etHarga.setText(String.valueOf(layananLama.getTarif()));
        if (etNamaJasa != null) etNamaJasa.setText(layananLama.getNamaJasa());
        if (etDeskripsi!= null) etDeskripsi.setText(layananLama.getDeskripsi());

        // Set spinner satuan
        setSpinnerSelection(spinnerTipeHarga, listSatuan.size(), idxSatuan(layananLama.getIdSatuan()));

        // Cari kategori berdasarkan id_jasa → load jasa → select yang sesuai
        int idJasaLama = layananLama.getIdJasa();
        // Dapatkan id_kategori dari jasa
        android.database.Cursor cj = null;
        int idKategoriLama = -1;
        try {
            cj = layananDAO.getDb().rawQuery(
                    "SELECT id_kategori FROM jasa WHERE id_jasa = ?",
                    new String[]{ String.valueOf(idJasaLama) }
            );
            if (cj != null && cj.moveToFirst()) idKategoriLama = cj.getInt(0);
        } finally {
            if (cj != null) cj.close();
        }

        // Select spinner kategori
        int idxKat = idxKategori(idKategoriLama);
        setSpinnerSelection(spinnerKategori, listKategori.size(), idxKat);

        // Load jasa berdasarkan kategori, select yang sesuai
        loadJasaByKategori(idKategoriLama, idJasaLama);
    }

    private void loadJasaByKategori(int idKategori, int idJasaToSelect) {
        listJasa = jasaDAO.getByKategori(idKategori);
        List<String> namaJasa = new ArrayList<>();
        for (Jasa j : listJasa) namaJasa.add(j.getNamaJasa());
        setSpinner(spinnerJasa, namaJasa);

        if (idJasaToSelect > 0) {
            int idx = idxJasa(idJasaToSelect);
            setSpinnerSelection(spinnerJasa, listJasa.size(), idx);
        }
    }

    private void simpanPerubahan() {
        String hargaStr  = etHarga    != null ? etHarga.getText().toString().trim()    : "";
        String namaJasa  = etNamaJasa != null ? etNamaJasa.getText().toString().trim() : "";
        String deskripsi = etDeskripsi!= null ? etDeskripsi.getText().toString().trim(): "";

        if (TextUtils.isEmpty(hargaStr)) {
            Toast.makeText(this, "Harga belum diisi", Toast.LENGTH_SHORT).show(); return;
        }
        if (TextUtils.isEmpty(namaJasa)) {
            Toast.makeText(this, "Nama jasa belum diisi", Toast.LENGTH_SHORT).show(); return;
        }
        if (TextUtils.isEmpty(deskripsi)) {
            Toast.makeText(this, "Deskripsi belum diisi", Toast.LENGTH_SHORT).show(); return;
        }

        double tarif;
        try {
            tarif = Double.parseDouble(hargaStr.replace(".", ""));
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Format harga tidak valid", Toast.LENGTH_SHORT).show(); return;
        }

        int posJasa   = spinnerJasa != null ? spinnerJasa.getSelectedItemPosition() : 0;
        int posSatuan = spinnerTipeHarga != null ? spinnerTipeHarga.getSelectedItemPosition() : 0;
        int idJasa    = (posJasa >= 0 && posJasa < listJasa.size())
                ? listJasa.get(posJasa).getIdJasa() : 1;
        int idSatuan  = (posSatuan >= 0 && posSatuan < listSatuan.size())
                ? listSatuan.get(posSatuan).getIdSatuan() : 1;

        new AlertDialog.Builder(this)
                .setTitle("Simpan Perubahan?")
                .setMessage("Yakin ingin menyimpan perubahan pada layanan ini?")
                .setPositiveButton("Ya, Simpan", (d, w) -> {
                    int rows = layananDAO.update(idLayanan, idJasa, namaJasa, deskripsi, tarif, idSatuan);
                    if (rows > 0) {
                        Toast.makeText(this, "Layanan berhasil diperbarui ✓", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        Toast.makeText(this, "Gagal menyimpan perubahan", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Batal", null)
                .show();
    }

    // ── helpers ──

    private void setSpinner(Spinner spinner, List<String> data) {
        if (spinner == null) return;
        ArrayAdapter<String> ad = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, data);
        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(ad);
    }

    private void setSpinnerSelection(Spinner spinner, int size, int idx) {
        if (spinner == null || size == 0) return;
        spinner.setSelection(Math.max(0, Math.min(idx, size - 1)));
    }

    private int idxKategori(int idKategori) {
        for (int i = 0; i < listKategori.size(); i++)
            if (listKategori.get(i).getIdKategori() == idKategori) return i;
        return 0;
    }

    private int idxJasa(int idJasa) {
        for (int i = 0; i < listJasa.size(); i++)
            if (listJasa.get(i).getIdJasa() == idJasa) return i;
        return 0;
    }

    private int idxSatuan(int idSatuan) {
        for (int i = 0; i < listSatuan.size(); i++)
            if (listSatuan.get(i).getIdSatuan() == idSatuan) return i;
        return 0;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (layananDAO  != null) layananDAO.close();
        if (kategoriDAO != null) kategoriDAO.close();
        if (jasaDAO     != null) jasaDAO.close();
        if (satuanDAO   != null) satuanDAO.close();
    }
}