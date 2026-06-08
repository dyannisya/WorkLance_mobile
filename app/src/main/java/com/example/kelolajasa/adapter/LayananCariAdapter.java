package com.example.kelolajasa.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kelolajasa.R;
import com.example.kelolajasa.model.LayananDisplay;

import java.util.ArrayList;
import java.util.List;

public class LayananCariAdapter extends RecyclerView.Adapter<LayananCariAdapter.ViewHolder> {

    public interface OnItemClick {
        void onClick(LayananDisplay item);
    }

    private List<LayananDisplay> list = new ArrayList<>();
    private OnItemClick listener;

    public LayananCariAdapter(List<LayananDisplay> list, OnItemClick listener) {
        if (list != null) this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_layanan_cari, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
        LayananDisplay item = list.get(pos);
        h.tvKategori.setText(item.getKategori());
        h.tvNamaLayanan.setText(item.getNamaLayanan());
        h.tvNamaFreelancer.setText("oleh " + item.getNamaFreelancer());
        h.tvHarga.setText(item.getHargaFormatted());
        h.tvRating.setText("⭐ " + item.getRatingFormatted());
        h.itemView.setOnClickListener(v -> { if (listener != null) listener.onClick(item); });
    }

    @Override
    public int getItemCount() { return list.size(); }

    public void setData(List<LayananDisplay> newList) {
        this.list = newList != null ? newList : new ArrayList<>();
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvKategori, tvNamaLayanan, tvNamaFreelancer, tvHarga, tvRating;

        public ViewHolder(@NonNull View v) {
            super(v);
            tvKategori = v.findViewById(R.id.tvKategori);
            tvNamaLayanan = v.findViewById(R.id.tvNamaLayanan);
            tvNamaFreelancer = v.findViewById(R.id.tvNamaFreelancer);
            tvHarga = v.findViewById(R.id.tvHarga);
            tvRating = v.findViewById(R.id.tvRating);
        }
    }
}