package com.example.kelolajasa;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kelolajasa.adapter.DaftarChatAdapter;
import com.example.kelolajasa.database.PesanDAO;
import com.example.kelolajasa.model.ChatListItem;

import java.util.List;

public class DaftarChatActivity extends AppCompatActivity {

    // Views dari layout daftar_chat.xml
    private RecyclerView recyclerViewChat;
    private TextView tvEmptyChat;
    private ImageView btnBackChat;

    // Data
    private PesanDAO pesanDAO;
    private SessionManager sessionManager;
    private DaftarChatAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.daftar_chat);

        sessionManager = new SessionManager(this);
        pesanDAO       = new PesanDAO(this);

        setupViews();
        loadData();
    }

    private void setupViews() {
        // ── Tombol back ──
        // Layout daftar_chat.xml memiliki toolbar dengan ImageButton tanpa ID.
        // Kita cari LinearLayout di dalam toolbar sebagai container back button.
        // Untuk keamanan, kita pakai onBackPressed jika tidak ketemu.
        View toolbar = findViewById(R.id.toolbar);
        if (toolbar instanceof android.view.ViewGroup) {
            android.view.ViewGroup vg = (android.view.ViewGroup) toolbar;
            for (int i = 0; i < vg.getChildCount(); i++) {
                if (vg.getChildAt(i) instanceof android.widget.ImageButton) {
                    vg.getChildAt(i).setOnClickListener(v -> onBackPressed());
                    break;
                }
            }
        }

        // ── Inject RecyclerView ke dalam ScrollView ──
        // scrollChat (ScrollView) → LinearLayout induk → kita tambah RV
        View scrollChat = findViewById(R.id.scrollChat);
        if (scrollChat instanceof android.widget.ScrollView) {
            android.widget.ScrollView sv = (android.widget.ScrollView) scrollChat;
            // Ambil LinearLayout di dalam ScrollView
            if (sv.getChildCount() > 0 && sv.getChildAt(0) instanceof LinearLayout) {
                LinearLayout container = (LinearLayout) sv.getChildAt(0);
                container.removeAllViews();

                // Empty state
                tvEmptyChat = new TextView(this);
                tvEmptyChat.setText("Belum ada percakapan.\nMulai dengan memesan layanan!");
                tvEmptyChat.setGravity(android.view.Gravity.CENTER);
                tvEmptyChat.setPadding(32, 64, 32, 64);
                tvEmptyChat.setTextColor(0xFF9E9E9E);
                tvEmptyChat.setTextSize(14f);
                tvEmptyChat.setVisibility(View.GONE);
                container.addView(tvEmptyChat);

                // RecyclerView
                recyclerViewChat = new RecyclerView(this);
                recyclerViewChat.setLayoutManager(new LinearLayoutManager(this));
                recyclerViewChat.setNestedScrollingEnabled(false);
                container.addView(recyclerViewChat);
            }
        }

        // ── Adapter ──
        adapter = new DaftarChatAdapter(this::onChatItemClicked);
        if (recyclerViewChat != null) recyclerViewChat.setAdapter(adapter);
    }

    private void loadData() {
        int idPengguna = sessionManager.getIdPengguna();
        int idRole     = sessionManager.getIdRole();

        List<ChatListItem> list;
        if (idRole == SessionManager.ROLE_FREELANCER) {
            list = pesanDAO.getChatListAsFreelancer(idPengguna);
        } else {
            list = pesanDAO.getChatListAsUser(idPengguna);
        }

        boolean empty = list == null || list.isEmpty();
        if (tvEmptyChat != null)
            tvEmptyChat.setVisibility(empty ? View.VISIBLE : View.GONE);
        if (recyclerViewChat != null)
            recyclerViewChat.setVisibility(empty ? View.GONE : View.VISIBLE);

        if (!empty && adapter != null) {
            adapter.setData(list);
        }
    }

    private void onChatItemClicked(ChatListItem item) {
        Intent intent = new Intent(this, CustomerChatActivity.class);
        intent.putExtra("id_booking",    item.getIdBooking());
        intent.putExtra("id_lawan",      item.getIdLawan());
        intent.putExtra("nama_lawan",    item.getNamaLawan());
        intent.putExtra("nama_layanan",  item.getNamaLayanan());
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();   // refresh saat kembali dari chat room
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (pesanDAO != null) pesanDAO.close();
    }
}