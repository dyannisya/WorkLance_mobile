package com.example.kelolajasa;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kelolajasa.adapter.JasaAdapter;
import com.example.kelolajasa.database.DatabaseHelper;
import com.example.kelolajasa.model.Jasa;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    JasaAdapter adapter;
    DatabaseHelper db;

    EditText inputNama;
    Spinner spinnerKategori;
    Button btnSimpan;
    int selectedId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        inputNama = findViewById(R.id.inputNama);
        spinnerKategori = findViewById(R.id.spinnerKategori);
        btnSimpan = findViewById(R.id.btnSimpan);

        db = new DatabaseHelper(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new JasaAdapter(getData(),

                // delete
                jasa -> {
                    db.deleteJasa(jasa.getId());
                    loadData();
                },

                // edit
                jasa -> {
                    selectedId = jasa.getId();

                    inputNama.setText(jasa.getNama());
                    spinnerKategori.setSelection(getSpinnerIndex(jasa.getKategori()));
                }
        );
        recyclerView.setAdapter(adapter);

        loadData();

        btnSimpan.setOnClickListener(v -> {
            String nama = inputNama.getText().toString();
            String kategori = spinnerKategori.getSelectedItem().toString();

            if (nama.isEmpty()) {
                Toast.makeText(this, "Nama tidak boleh kosong", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedId == -1) {
                // INSERT
                db.insertJasa(nama, kategori);
                Toast.makeText(this, "Data ditambahkan", Toast.LENGTH_SHORT).show();
            } else {
                // UPDATE
                db.updateJasa(selectedId, nama, kategori);
                Toast.makeText(this, "Data diupdate", Toast.LENGTH_SHORT).show();
                selectedId = -1;
            }

            inputNama.setText("");
            loadData();
        });
    }

    private int getSpinnerIndex(String value) {
        for (int i = 0; i < spinnerKategori.getCount(); i++) {
            if (spinnerKategori.getItemAtPosition(i).toString().equals(value)) {
                return i;
            }
        }
        return 0;
    }

    private List<Jasa> getData() {
        List<Jasa> list = new ArrayList<>();

        Cursor cursor = db.getAllJasa();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String nama = cursor.getString(1);
                String kategori = cursor.getString(2);

                list.add(new Jasa(id, nama, kategori));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return list;
    }

    private void loadData() {
        List<Jasa> list = getData();

        if (list.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            findViewById(R.id.txtEmpty).setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            findViewById(R.id.txtEmpty).setVisibility(View.GONE);

            adapter.setData(list);
        }
    }

    private void simpanData() {
        String nama = inputNama.getText().toString();
        String kategori = spinnerKategori.getSelectedItem().toString();

        if (nama.isEmpty()) {
            Toast.makeText(this, "Nama tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean inserted = db.insertJasa(nama, kategori);

        if (inserted) {
            Toast.makeText(this, "Data berhasil disimpan", Toast.LENGTH_SHORT).show();

            // 🔥 refresh data
            loadData();

        } else {
            Toast.makeText(this, "Gagal menyimpan", Toast.LENGTH_SHORT).show();
        }
    }
}