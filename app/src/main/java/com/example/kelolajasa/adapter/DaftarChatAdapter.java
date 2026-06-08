package com.example.kelolajasa.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kelolajasa.R;
import com.example.kelolajasa.model.ChatListItem;

import java.util.ArrayList;
import java.util.List;

public class DaftarChatAdapter extends RecyclerView.Adapter<DaftarChatAdapter.ViewHolder> {

    public interface OnItemClick {
        void onClick(ChatListItem item);
    }

    private List<ChatListItem> list = new ArrayList<>();
    private final OnItemClick listener;

    public DaftarChatAdapter(OnItemClick listener) {
        this.listener = listener;
    }

    public void setData(List<ChatListItem> newList) {
        this.list = newList != null ? new ArrayList<>(newList) : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_daftar_chat, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
        ChatListItem item = list.get(pos);

        h.tvNamaLawan.setText(item.getNamaLawan());
        h.tvNamaLayanan.setText("📌 " + item.getNamaLayanan());
        h.tvPesanTerakhir.setText(item.getPesanTerakhir());
        h.tvWaktu.setText(item.getWaktuFormatted());

        // Badge unread
        if (item.getJumlahUnread() > 0) {
            h.tvUnread.setVisibility(View.VISIBLE);
            h.tvUnread.setText(String.valueOf(
                    Math.min(item.getJumlahUnread(), 99)));   // max "99"
        } else {
            h.tvUnread.setVisibility(View.GONE);
        }

        h.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onClick(item);
        });
    }

    @Override
    public int getItemCount() { return list.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNamaLawan, tvNamaLayanan, tvPesanTerakhir, tvWaktu, tvUnread;

        ViewHolder(@NonNull View v) {
            super(v);
            tvNamaLawan      = v.findViewById(R.id.tvNamaLawan);
            tvNamaLayanan    = v.findViewById(R.id.tvNamaLayanan);
            tvPesanTerakhir  = v.findViewById(R.id.tvPesanTerakhir);
            tvWaktu          = v.findViewById(R.id.tvWaktu);
            tvUnread         = v.findViewById(R.id.tvUnread);
        }
    }
}