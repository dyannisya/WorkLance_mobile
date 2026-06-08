package com.example.kelolajasa;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.kelolajasa.database.JasaDAO;
import com.example.kelolajasa.database.KategoriDAO;
import com.example.kelolajasa.database.LayananDAO;
import com.example.kelolajasa.database.PengajuanFreelancerDAO;
import com.example.kelolajasa.database.SatuanDAO;
import com.example.kelolajasa.model.Jasa;
import com.example.kelolajasa.model.Kategori;
import com.example.kelolajasa.model.Satuan;

import java.util.ArrayList;
import java.util.List;

public class DaftarFreelancerActivity extends AppCompatActivity {

    // Views (dari daftar_freelancer.xml)
    private TextView tvBack;
    private Spinner spinnerKategori, spinnerJasa, spinnerTipeHarga;
    private EditText etHarga, etNamaJasa, etDeskripsi;
    private Button btnDaftarSimpan;

    // Data
    private SessionManager sessionManager;
    private KategoriDAO kategoriDAO;
    private JasaDAO jasaDAO;
    private SatuanDAO satuanDAO;
    private LayananDAO layananDAO;
    private PengajuanFreelancerDAO pengajuanDAO;

    private List<Kategori> listKategori = new ArrayList<>();
    private List<Jasa> listJasa = new ArrayList<>();
    private List<Satuan> listSatuan = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.daftar_freelancer);

        sessionManager = new SessionManager(this);

        // Cek: hanya user biasa yang boleh mendaftar jadi freelancer
        if (sessionManager.getIdRole() == SessionManager.ROLE_FREELANCER) {
            Toast.makeText(this, "Kamu sudah terdaftar sebagai freelancer", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initDAO();
        initViews();
        loadSpinners();
    }

    private void initDAO() {
        kategoriDAO  = new KategoriDAO(this);
        jasaDAO      = new JasaDAO(this);
        satuanDAO    = new SatuanDAO(this);
        layananDAO   = new LayananDAO(this);
        pengajuanDAO = new PengajuanFreelancerDAO(this);
    }

    private void initViews() {
        tvBack          = findViewById(R.id.tvBack);
        spinnerKategori = findViewById(R.id.spinnerKategori);
        spinnerJasa     = findViewById(R.id.spinnerJasa);
        spinnerTipeHarga= findViewById(R.id.spinnerTipeHarga);
        etHarga         = findViewById(R.id.etHarga);
        etNamaJasa      = findViewById(R.id.etNamaJasa);
        etDeskripsi     = findViewById(R.id.etDeskripsi);
        btnDaftarSimpan = findViewById(R.id.btnDaftarSimpan);

        if (tvBack != null) tvBack.setOnClickListener(v -> onBackPressed());

        if (spinnerKategori != null) {
            spinnerKategori.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(android.widget.AdapterView<?> parent, View view, int pos, long id) {
                    if (pos >= 0 && pos < listKategori.size()) {
                        loadJasaByKategori(listKategori.get(pos).getIdKategori());
                    }
                }
                @Override public void onNothingSelected(android.widget.AdapterView<?> parent) {}
            });
        }

        if (btnDaftarSimpan != null) btnDaftarSimpan.setOnClickListener(v -> tampilDialogNIK());
    }

    private void loadSpinners() {
        // Kategori
        listKategori = kategoriDAO.getAll();
        List<String> namaKategori = new ArrayList<>();
        for (Kategori k : listKategori) namaKategori.add(k.getNamaKategori());
        setSpinner(spinnerKategori, namaKategori);

        // Satuan / tipe harga
        listSatuan = satuanDAO.getAll();
        List<String> namaSatuan = new ArrayList<>();
        for (Satuan s : listSatuan) namaSatuan.add(s.getNamaSatuan());
        setSpinner(spinnerTipeHarga, namaSatuan);

        // Load jasa awal
        if (!listKategori.isEmpty()) {
            loadJasaByKategori(listKategori.get(0).getIdKategori());
        }
    }

    private void loadJasaByKategori(int idKategori) {
        listJasa = jasaDAO.getByKategori(idKategori);
        List<String> namaJasa = new ArrayList<>();
        for (Jasa j : listJasa) namaJasa.add(j.getNamaJasa());
        setSpinner(spinnerJasa, namaJasa);
    }

    private void setSpinner(Spinner spinner, List<String> data) {
        if (spinner == null) return;
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    /**
     * Tampilkan dialog untuk input NIK sebelum submit.
     * NIK wajib ada untuk pengajuan_freelancer.
     */
    private void tampilDialogNIK() {
        if (!validasiForm()) return;

        final EditText etNIK = new EditText(this);
        etNIK.setHint("Masukkan NIK (16 digit)");
        etNIK.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        etNIK.setPadding(48, 24, 48, 24);

        new AlertDialog.Builder(this)
                .setTitle("Verifikasi Identitas")
                .setMessage("Masukkan NIK (Nomor Induk Kependudukan) Anda untuk melengkapi pengajuan.")
                .setView(etNIK)
                .setPositiveButton("Daftar & Simpan", (dialog, which) -> {
                    String nik = etNIK.getText().toString().trim();
                    if (nik.length() < 16) {
                        Toast.makeText(this, "NIK harus 16 digit", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    prosesDaftar(nik);
                })
                .setNegativeButton("Batal", null)
                .show();
    }

    private boolean validasiForm() {
        String harga = etHarga != null ? etHarga.getText().toString().trim() : "";
        String nama  = etNamaJasa != null ? etNamaJasa.getText().toString().trim() : "";
        String desk  = etDeskripsi != null ? etDeskripsi.getText().toString().trim() : "";

        if (TextUtils.isEmpty(harga)) {
            Toast.makeText(this, "Harga belum diisi", Toast.LENGTH_SHORT).show(); return false;
        }
        if (TextUtils.isEmpty(nama)) {
            Toast.makeText(this, "Nama jasa belum diisi", Toast.LENGTH_SHORT).show(); return false;
        }
        if (TextUtils.isEmpty(desk)) {
            Toast.makeText(this, "Deskripsi belum diisi", Toast.LENGTH_SHORT).show(); return false;
        }
        return true;
    }

    private void prosesDaftar(String nik) {
        int idPengguna = sessionManager.getIdPengguna();

        // 1. Buat pengajuan freelancer
        long idPengajuan = pengajuanDAO.insert(idPengguna, nik,
                etDeskripsi.getText().toString().trim());

        if (idPengajuan == -2) {
            Toast.makeText(this, "Kamu sudah memiliki pengajuan yang sedang diproses", Toast.LENGTH_LONG).show();
            return;
        }
        if (idPengajuan < 0) {
            Toast.makeText(this, "Gagal membuat pengajuan, coba lagi", Toast.LENGTH_SHORT).show();
            return;
        }

        // 2. Simpan layanan pertama
        int posJasa    = spinnerJasa != null ? spinnerJasa.getSelectedItemPosition() : 0;
        int posSatuan  = spinnerTipeHarga != null ? spinnerTipeHarga.getSelectedItemPosition() : 0;
        int idJasa     = (posJasa >= 0 && posJasa < listJasa.size())
                ? listJasa.get(posJasa).getIdJasa() : 1;
        int idSatuan   = (posSatuan >= 0 && posSatuan < listSatuan.size())
                ? listSatuan.get(posSatuan).getIdSatuan() : 1;

        double tarif;
        try {
            tarif = Double.parseDouble(etHarga.getText().toString().trim().replace(".", ""));
        } catch (NumberFormatException e) {
            tarif = 0;
        }

        String namaJasa = etNamaJasa.getText().toString().trim();
        String deskripsi= etDeskripsi.getText().toString().trim();

        long idLayanan = layananDAO.insert(idPengguna, idJasa, namaJasa, deskripsi, tarif, idSatuan);

        if (idLayanan > 0) {
            new AlertDialog.Builder(this)
                    .setTitle("Pengajuan Berhasil! 🎉")
                    .setMessage("Pengajuan kamu sebagai freelancer telah dikirim.\n\n" +
                            "Admin akan mereview dalam 1-3 hari kerja. " +
                            "Kamu akan mendapat notifikasi setelah disetujui.")
                    .setPositiveButton("OK", (d, w) -> {
                        Intent intent = new Intent(this, DashboardActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    })
                    .setCancelable(false)
                    .show();
        } else {
            Toast.makeText(this, "Pengajuan tersimpan, tapi gagal menyimpan layanan", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (kategoriDAO  != null) kategoriDAO.close();
        if (jasaDAO      != null) jasaDAO.close();
        if (satuanDAO    != null) satuanDAO.close();
        if (layananDAO   != null) layananDAO.close();
        if (pengajuanDAO != null) pengajuanDAO.close();
    }
}