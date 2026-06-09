package com.example.kelolajasa;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.kelolajasa.database.JasaDAO;
import com.example.kelolajasa.database.LayananDAO;
import com.example.kelolajasa.database.SatuanDAO;
import com.example.kelolajasa.model.Jasa;
import com.example.kelolajasa.model.Layanan;
import com.example.kelolajasa.model.Satuan;

import java.util.ArrayList;
import java.util.List;

public class KelolaJasaFreelancer2Activity extends AppCompatActivity {

    TextView textView2, textView3, textView4;
    ImageView keranjang, btncari, btnbag, btnhome, btnriwayat, btnprofil;
    LinearLayout containerJasa;

    LayananDAO layananDAO;
    JasaDAO jasaDAO;
    SatuanDAO satuanDAO;
    SessionManager sessionManager;

    List<Jasa> jasaList = new ArrayList<>();
    List<Satuan> satuanList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kelolajasafreelancer2);

        sessionManager = new SessionManager(this);
        layananDAO = new LayananDAO(this);
        jasaDAO    = new JasaDAO(this);
        satuanDAO  = new SatuanDAO(this);

        initViews();
        loadProfile();
        loadLayananList();
        setupBottomNav();
    }

    private void initViews() {
        textView2    = findViewById(R.id.textView2);
        textView3    = findViewById(R.id.textView3);
        textView4    = findViewById(R.id.textView4);
        keranjang    = findViewById(R.id.keranjang);
        containerJasa = findViewById(R.id.containerJasa);
        btncari      = findViewById(R.id.btncari);
        btnbag       = findViewById(R.id.btnbag);
        btnhome      = findViewById(R.id.btnhome);
        btnriwayat   = findViewById(R.id.btnriwayat);
        btnprofil    = findViewById(R.id.btnprofil);

        if (keranjang != null) keranjang.setOnClickListener(v -> showTambahLayananDialog());

        jasaList   = jasaDAO.getAllWithKategori();
        satuanList = satuanDAO.getAll();
    }

    private void loadProfile() {
        if (textView2 != null) textView2.setText(sessionManager.getNamaPengguna());
        if (textView3 != null) textView3.setText("Freelancer");
        if (textView4 != null) textView4.setText(sessionManager.getEmail());
    }

    private void loadLayananList() {
        if (containerJasa == null) return;
        containerJasa.removeAllViews();

        int idFreelancer = sessionManager.getIdPengguna();
        List<Layanan> list = layananDAO.getByFreelancer(idFreelancer);

        if (list.isEmpty()) {
            TextView tvKosong = new TextView(this);
            tvKosong.setText("Belum ada layanan. Tap ikon + untuk menambah.");
            tvKosong.setPadding(32, 48, 32, 48);
            tvKosong.setTextColor(0xFF666666);
            tvKosong.setGravity(Gravity.CENTER);
            containerJasa.addView(tvKosong);
            return;
        }

        for (Layanan layanan : list) addLayananCard(layanan);
    }

    private void addLayananCard(Layanan layanan) {
        LinearLayout cardLayout = new LinearLayout(this);
        cardLayout.setOrientation(LinearLayout.HORIZONTAL);
        cardLayout.setPadding(24, 24, 24, 24);
        cardLayout.setBackgroundResource(R.drawable.bg_card);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 24);
        cardLayout.setLayoutParams(params);

        // Teks konten
        LinearLayout textLayout = new LinearLayout(this);
        textLayout.setOrientation(LinearLayout.VERTICAL);
        textLayout.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        TextView tvNama = new TextView(this);
        tvNama.setText(layanan.getNamaJasa());
        tvNama.setTextSize(15f);
        tvNama.setTextColor(0xFF1A1A2E);
        tvNama.setTypeface(null, Typeface.BOLD); // FIX: bukan setTextStyle
        textLayout.addView(tvNama);

        TextView tvHarga = new TextView(this);
        long h = (long) layanan.getTarif();
        tvHarga.setText("Rp" + String.format("%,d", h).replace(",", "."));
        tvHarga.setTextSize(13f);
        tvHarga.setTextColor(0xFF333333);
        textLayout.addView(tvHarga);

        // Tombol Edit + Hapus
        LinearLayout btnLayout = new LinearLayout(this);
        btnLayout.setOrientation(LinearLayout.VERTICAL);
        btnLayout.setGravity(Gravity.CENTER_VERTICAL);

        Button btnEdit = new Button(this);
        btnEdit.setText("Edit Jasa");
        btnEdit.setBackgroundTintList(ColorStateList.valueOf(0xFF161E54));
        btnEdit.setTextColor(0xFFFFFFFF);
        btnEdit.setTextSize(11f);
        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(this, EditJasaFreelancerActivity.class);
            intent.putExtra("id_layanan", layanan.getIdLayanan());
            startActivity(intent);
        });

        Button btnHapus = new Button(this);
        btnHapus.setText("Hapus");
        btnHapus.setBackgroundTintList(ColorStateList.valueOf(0xFFD32F2F));
        btnHapus.setTextColor(0xFFFFFFFF);
        btnHapus.setTextSize(11f);
        btnHapus.setOnClickListener(v ->
                new AlertDialog.Builder(this)
                        .setTitle("Hapus Layanan")
                        .setMessage("Hapus \"" + layanan.getNamaJasa() + "\"?")
                        .setPositiveButton("Hapus", (d, w) -> {
                            layananDAO.delete(layanan.getIdLayanan());
                            Toast.makeText(this, "Layanan dihapus", Toast.LENGTH_SHORT).show();
                            loadLayananList();
                        })
                        .setNegativeButton("Batal", null).show());

        btnLayout.addView(btnEdit);
        btnLayout.addView(btnHapus);

        cardLayout.addView(textLayout);
        cardLayout.addView(btnLayout);
        containerJasa.addView(cardLayout);
    }

    private void showTambahLayananDialog() {
        if (jasaList.isEmpty()) {
            Toast.makeText(this, "Data jasa belum tersedia. Hubungi admin.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(48, 32, 48, 16);

        // Label + Spinner Jasa
        TextView lblJasa = new TextView(this);
        lblJasa.setText("Pilih Jenis Jasa:");
        lblJasa.setTypeface(null, Typeface.BOLD); // FIX: bukan setTextStyle
        layout.addView(lblJasa);

        Spinner spinnerJasa = new Spinner(this);
        List<String> namaJasaList = new ArrayList<>();
        for (Jasa j : jasaList) namaJasaList.add(j.getNamaJasa() + " (" + j.getNamaKategori() + ")");
        ArrayAdapter<String> jasaAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, namaJasaList);
        jasaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerJasa.setAdapter(jasaAdapter);
        layout.addView(spinnerJasa);

        // Nama Layanan
        TextView lblNama = new TextView(this);
        lblNama.setText("Nama Layanan:");
        lblNama.setPadding(0, 16, 0, 4);
        layout.addView(lblNama);

        EditText etNama = new EditText(this);
        etNama.setHint("contoh: Desain Logo Profesional");
        layout.addView(etNama);

        // Deskripsi
        TextView lblDesk = new TextView(this);
        lblDesk.setText("Deskripsi:");
        lblDesk.setPadding(0, 12, 0, 4);
        layout.addView(lblDesk);

        EditText etDesk = new EditText(this);
        etDesk.setHint("Jelaskan layanan Anda...");
        etDesk.setMinLines(2);
        layout.addView(etDesk);

        // Harga
        TextView lblHarga = new TextView(this);
        lblHarga.setText("Harga (Rp):");
        lblHarga.setPadding(0, 12, 0, 4);
        layout.addView(lblHarga);

        EditText etHarga = new EditText(this);
        etHarga.setHint("contoh: 500000");
        etHarga.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        layout.addView(etHarga);

        // Spinner Satuan
        TextView lblSatuan = new TextView(this);
        lblSatuan.setText("Satuan:");
        lblSatuan.setPadding(0, 12, 0, 4);
        layout.addView(lblSatuan);

        Spinner spinnerSatuan = new Spinner(this);
        List<String> namaSatuanList = new ArrayList<>();
        for (Satuan s : satuanList) namaSatuanList.add(s.getNamaSatuan());
        ArrayAdapter<String> satuanAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, namaSatuanList);
        satuanAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSatuan.setAdapter(satuanAdapter);
        layout.addView(spinnerSatuan);

        new AlertDialog.Builder(this)
                .setTitle("Tambah Layanan Baru")
                .setView(layout)
                .setPositiveButton("Simpan", (d, w) -> {
                    String nama     = etNama.getText().toString().trim();
                    String desk     = etDesk.getText().toString().trim();
                    String hargaStr = etHarga.getText().toString().trim();

                    if (TextUtils.isEmpty(nama) || TextUtils.isEmpty(hargaStr)) {
                        Toast.makeText(this, "Nama dan harga wajib diisi", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    int selJasaPos   = spinnerJasa.getSelectedItemPosition();
                    int selSatuanPos = spinnerSatuan.getSelectedItemPosition();

                    // FIX: ternary precedence bug diperbaiki dengan kurung
                    if (selJasaPos < 0 || selJasaPos >= jasaList.size()) return;
                    if (selSatuanPos < 0 || selSatuanPos >= (satuanList.isEmpty() ? 0 : satuanList.size())) return;

                    int idJasa   = jasaList.get(selJasaPos).getIdJasa();
                    int idSatuan = satuanList.isEmpty() ? 1 : satuanList.get(selSatuanPos).getIdSatuan();
                    double harga = Double.parseDouble(hargaStr);

                    long result = layananDAO.insert(
                            sessionManager.getIdPengguna(), idJasa, nama, desk, harga, idSatuan);

                    if (result > 0) {
                        Toast.makeText(this, "Layanan berhasil ditambahkan!", Toast.LENGTH_SHORT).show();
                        loadLayananList();
                    } else {
                        Toast.makeText(this, "Gagal menambahkan layanan", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Batal", null).show();
    }

    private void setupBottomNav() {
        if (btncari != null) btncari.setOnClickListener(v ->
                startActivity(new Intent(this, CariActivity.class)));
        if (btnbag != null) btnbag.setOnClickListener(v ->
                Toast.makeText(this, "Kelola Jasa", Toast.LENGTH_SHORT).show());
        if (btnhome != null) btnhome.setOnClickListener(v ->
                startActivity(new Intent(this, DashboardActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));
        if (btnriwayat != null) btnriwayat.setOnClickListener(v ->
                startActivity(new Intent(this, RiwayatPesananActivity.class)));
        if (btnprofil != null) btnprofil.setOnClickListener(v ->
                Toast.makeText(this, "Profil — Coming Soon", Toast.LENGTH_SHORT).show());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadLayananList();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (layananDAO != null) layananDAO.close();
        if (jasaDAO != null) jasaDAO.close();
        if (satuanDAO != null) satuanDAO.close();
    }
}