package com.example.kelolajasa;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kelolajasa.adapter.ChatBubbleAdapter;
import com.example.kelolajasa.database.PesanDAO;
import com.example.kelolajasa.model.Pesan;

import java.util.List;

public class CustomerChatActivity extends AppCompatActivity {

    // Views
    private ImageView btnBack;
    private TextView tvNamaLawan, tvNamaLayananChat;
    private RecyclerView recyclerViewChat;
    private EditText etPesan;
    private ImageView btnKirim;

    // Data
    private PesanDAO pesanDAO;
    private SessionManager sessionManager;
    private ChatBubbleAdapter adapter;

    private int idBooking;
    private int idLawan;        // id lawan bicara
    private String namaLawan;
    private String namaLayanan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_customer);

        // Ambil data dari Intent
        idBooking  = getIntent().getIntExtra("id_booking",   -1);
        idLawan    = getIntent().getIntExtra("id_lawan",      -1);
        namaLawan  = getIntent().getStringExtra("nama_lawan");
        namaLayanan= getIntent().getStringExtra("nama_layanan");

        if (idBooking == -1) {
            Toast.makeText(this, "Data chat tidak valid", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        sessionManager = new SessionManager(this);
        pesanDAO       = new PesanDAO(this);

        initViews();
        setupAdapter();
        loadMessages();
        setupSend();

        // Tandai semua pesan masuk sebagai dibaca
        pesanDAO.tandaiBaca(idBooking, sessionManager.getIdPengguna());
    }

    private void initViews() {
        btnBack            = findViewById(R.id.btnBack);
        tvNamaLawan        = findViewById(R.id.tvNamaLawan);
        tvNamaLayananChat  = findViewById(R.id.tvNamaLayananChat);
        recyclerViewChat   = findViewById(R.id.recyclerViewChat);
        etPesan            = findViewById(R.id.etPesan);
        btnKirim           = findViewById(R.id.btnKirim);

        // Isi header
        if (tvNamaLawan   != null && namaLawan   != null) tvNamaLawan.setText(namaLawan);
        if (tvNamaLayananChat != null && namaLayanan != null)
            tvNamaLayananChat.setText("📌 " + namaLayanan);

        // Tombol back
        if (btnBack != null) btnBack.setOnClickListener(v -> onBackPressed());
    }

    private void setupAdapter() {
        adapter = new ChatBubbleAdapter(sessionManager.getIdPengguna());
        if (recyclerViewChat != null) {
            LinearLayoutManager lm = new LinearLayoutManager(this);
            lm.setStackFromEnd(true);   // auto-scroll ke bawah
            recyclerViewChat.setLayoutManager(lm);
            recyclerViewChat.setAdapter(adapter);
        }
    }

    private void loadMessages() {
        List<Pesan> messages = pesanDAO.getByBooking(idBooking);
        adapter.setData(messages);
        scrollToBottom();
    }

    private void setupSend() {
        if (btnKirim == null) return;
        btnKirim.setOnClickListener(v -> kirimPesan());

        // Kirim dengan tombol "Send" di keyboard
        if (etPesan != null) {
            etPesan.setOnEditorActionListener((tv, actionId, event) -> {
                kirimPesan();
                return true;
            });
        }
    }

    private void kirimPesan() {
        if (etPesan == null) return;
        String isi = etPesan.getText().toString().trim();
        if (TextUtils.isEmpty(isi)) return;

        int idSaya = sessionManager.getIdPengguna();

        // Tentukan penerima: kalau idLawan valid pakai itu, else fallback 0
        int penerima = (idLawan > 0) ? idLawan : 0;

        long idBaru = pesanDAO.insert(idBooking, idSaya, penerima, isi);
        if (idBaru > 0) {
            etPesan.setText("");
            // Buat objek Pesan lokal untuk langsung tampil tanpa reload DB
            Pesan pesanBaru = new Pesan(
                    (int) idBaru, idBooking, idSaya, penerima,
                    isi,
                    getCurrentTime(),
                    false
            );
            adapter.addPesan(pesanBaru);
            scrollToBottom();
        } else {
            Toast.makeText(this, "Gagal mengirim pesan", Toast.LENGTH_SHORT).show();
        }
    }

    private void scrollToBottom() {
        if (recyclerViewChat != null && adapter.getItemCount() > 0) {
            recyclerViewChat.smoothScrollToPosition(adapter.getItemCount() - 1);
        }
    }

    private String getCurrentTime() {
        // Format cepat tanpa import tambahan
        java.util.Calendar cal = java.util.Calendar.getInstance();
        return String.format(java.util.Locale.getDefault(),
                "%d-%02d-%02d %02d:%02d:%02d",
                cal.get(java.util.Calendar.YEAR),
                cal.get(java.util.Calendar.MONTH) + 1,
                cal.get(java.util.Calendar.DAY_OF_MONTH),
                cal.get(java.util.Calendar.HOUR_OF_DAY),
                cal.get(java.util.Calendar.MINUTE),
                cal.get(java.util.Calendar.SECOND)
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (pesanDAO != null) pesanDAO.close();
    }
}