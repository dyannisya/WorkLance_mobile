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

import com.example.kelolajasa.adapter.PenggunaAdapter;
import com.example.kelolajasa.database.PenggunaDAO;
import com.example.kelolajasa.model.Pengguna;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class KelolaPenggunaActivity extends AppCompatActivity {

    TextView title, txtEmpty;
    EditText editTextText3;
    MaterialButton btn1, btn2, btn3, btn4;
    RecyclerView recyclerView;
    ImageView btncari, btnbag, btnhome, btnriwayat, btnprofil;

    PenggunaDAO penggunaDAO;
    PenggunaAdapter adapter;
    int currentRoleFilter = -1; // -1 = Semua

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kelolapengguna);

        penggunaDAO = new PenggunaDAO(this);
        initViews();
        setupBottomNav();
        setupFilterTabs();
        setupSearch();
        loadData();
    }

    private void initViews() {
        title = findViewById(R.id.title);
        txtEmpty = findViewById(R.id.txtEmpty);
        editTextText3 = findViewById(R.id.editTextText3);
        btn1 = findViewById(R.id.btn1); // Semua
        btn2 = findViewById(R.id.btn2); // Admin
        btn3 = findViewById(R.id.btn3); // User
        btn4 = findViewById(R.id.btn4); // Freelancer
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
                Toast.makeText(this, "Kelola Pengguna", Toast.LENGTH_SHORT).show());
        if (btnbag != null) btnbag.setOnClickListener(v ->
                startActivity(new Intent(this, KelolaJasaActivity.class)));
        if (btnhome != null) btnhome.setOnClickListener(v ->
                startActivity(new Intent(this, AdminDashboardActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));
        if (btnriwayat != null) btnriwayat.setOnClickListener(v ->
                startActivity(new Intent(this, KelolaBookingActivity.class)));
        if (btnprofil != null) btnprofil.setOnClickListener(v ->
                startActivity(new Intent(this, KelolaPengajuanActivity.class)));
    }

    private void setupFilterTabs() {
        btn1.setOnClickListener(v -> applyRoleFilter(-1));   // Semua
        btn2.setOnClickListener(v -> applyRoleFilter(1));    // Admin
        btn3.setOnClickListener(v -> applyRoleFilter(2));    // User
        btn4.setOnClickListener(v -> applyRoleFilter(3));    // Freelancer
    }

    private void applyRoleFilter(int role) {
        currentRoleFilter = role;
        if (adapter != null) adapter.filterByRole(role);
    }

    private void setupSearch() {
        if (editTextText3 == null) return;
        editTextText3.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void onTextChanged(CharSequence s, int start, int b, int c) {
                if (adapter != null) adapter.filterByKeyword(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void loadData() {
        List<Pengguna> list = penggunaDAO.getAllPengguna();
        boolean isEmpty = list == null || list.isEmpty();
        if (txtEmpty != null) txtEmpty.setVisibility(isEmpty ? View.VISIBLE : View.GONE);

        adapter = new PenggunaAdapter(list, new PenggunaAdapter.ActionListener() {
            @Override
            public void onDelete(Pengguna pengguna) {
                new AlertDialog.Builder(KelolaPenggunaActivity.this)
                        .setTitle("Hapus Pengguna")
                        .setMessage("Hapus pengguna \"" + pengguna.getNamaPengguna() + "\"?\n" +
                                "Semua data terkait akan ikut terhapus.")
                        .setPositiveButton("Hapus", (d, w) -> {
                            int r = penggunaDAO.deletePengguna(pengguna.getIdPengguna());
                            if (r > 0) {
                                Toast.makeText(KelolaPenggunaActivity.this,
                                        "Pengguna dihapus", Toast.LENGTH_SHORT).show();
                                loadData();
                            } else {
                                Toast.makeText(KelolaPenggunaActivity.this,
                                        "Gagal menghapus", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Batal", null)
                        .show();
            }

            @Override
            public void onLihat(Pengguna pengguna) {
                // Tampilkan detail pengguna
                String info = "Username: " + pengguna.getUsername() +
                        "\nEmail: " + pengguna.getEmail() +
                        "\nNo. Telp: " + pengguna.getNoTelp() +
                        "\nRole ID: " + pengguna.getIdRole();
                new AlertDialog.Builder(KelolaPenggunaActivity.this)
                        .setTitle(pengguna.getNamaPengguna())
                        .setMessage(info)
                        .setPositiveButton("Tutup", null)
                        .show();
            }
        });

        recyclerView.setAdapter(adapter);
        if (currentRoleFilter != -1) adapter.filterByRole(currentRoleFilter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (penggunaDAO != null) penggunaDAO.close();
    }
}