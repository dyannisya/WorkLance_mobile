package com.example.kelolajasa;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.kelolajasa.adapter.DashboardLayananAdapter;
import com.example.kelolajasa.database.LayananDAO;
import com.example.kelolajasa.model.LayananDisplay;

import java.util.List;

public class LayananKategoriActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView tvJudul;

    private TextView tvJumlahLayanan;
    private ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layanan_kategori);

        recyclerView = findViewById(R.id.recyclerKategori);
        tvJudul = findViewById(R.id.tvJudulKategori);

        int idKategori =
                getIntent().getIntExtra(
                        "id_kategori",
                        0);

        String namaKategori =
                getIntent().getStringExtra(
                        "nama_kategori");

        tvJudul.setText(namaKategori);

        LayananDAO dao = new LayananDAO(this);

        List<LayananDisplay> data =
                dao.getByKategori(idKategori);

        recyclerView.setLayoutManager(
                new GridLayoutManager(this, 2)
        );

        recyclerView.addItemDecoration(
                new GridSpacingItemDecoration(12)
        );

        recyclerView.setAdapter(
                new DashboardLayananAdapter(
                        this,
                        data));

        tvJumlahLayanan =
                findViewById(R.id.tvJumlahLayanan);

        btnBack =
                findViewById(R.id.btnBack);

        tvJumlahLayanan.setText(
                data.size() + " layanan ditemukan"
        );

        btnBack.setOnClickListener(v -> finish());

        dao.close();
    }
}