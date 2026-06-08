package com.example.kelolajasa.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kelolajasa.R;
import com.example.kelolajasa.model.BookingDisplay;

import java.util.ArrayList;
import java.util.List;

public class BookingFullAdapter extends RecyclerView.Adapter<BookingFullAdapter.ViewHolder> {

    public interface OnUbahClick {
        void onUbah(BookingDisplay item);
    }

    private List<BookingDisplay> list = new ArrayList<>();
    private List<BookingDisplay> listFull = new ArrayList<>();
    private OnUbahClick listener;

    public BookingFullAdapter(List<BookingDisplay> list, OnUbahClick listener) {
        if (list != null) {
            this.list = new ArrayList<>(list);
            this.listFull = new ArrayList<>(list);
        }
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_booking_full, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
        BookingDisplay item = list.get(pos);
        h.tvNamaClient.setText(item.getNamaClient());
        h.tvNamaJasa.setText(item.getNamaJasa());
        h.tvStatus.setText(item.getStatusBooking());
        applyStatusColor(h.tvStatus, item.getStatusBooking());
        h.btnAksi.setOnClickListener(v -> { if (listener != null) listener.onUbah(item); });
    }

    @Override
    public int getItemCount() { return list.size(); }

    public void filterByStatus(String status) {
        list.clear();
        if (status == null || status.equals("Semua")) {
            list.addAll(listFull);
        } else {
            for (BookingDisplay b : listFull) {
                if (b.getStatusBooking().equals(status)) list.add(b);
            }
        }
        notifyDataSetChanged();
    }

    public void filterByKeyword(String keyword) {
        list.clear();
        if (keyword == null || keyword.isEmpty()) {
            list.addAll(listFull);
        } else {
            String kw = keyword.toLowerCase();
            for (BookingDisplay b : listFull) {
                if (b.getNamaClient().toLowerCase().contains(kw) ||
                        b.getNamaJasa().toLowerCase().contains(kw)) {
                    list.add(b);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void setData(List<BookingDisplay> newList) {
        this.listFull = newList != null ? new ArrayList<>(newList) : new ArrayList<>();
        this.list = new ArrayList<>(this.listFull);
        notifyDataSetChanged();
    }

    private void applyStatusColor(TextView tv, String status) {
        switch (status) {
            case "Menunggu":
                tv.setBackgroundColor(Color.parseColor("#FFF3E0"));
                tv.setTextColor(Color.parseColor("#E65100"));
                break;
            case "Diproses":
                tv.setBackgroundColor(Color.parseColor("#E3F2FD"));
                tv.setTextColor(Color.parseColor("#1565C0"));
                break;
            case "Selesai":
                tv.setBackgroundColor(Color.parseColor("#E8F5E9"));
                tv.setTextColor(Color.parseColor("#2E7D32"));
                break;
            case "Dibatalkan":
                tv.setBackgroundColor(Color.parseColor("#FFEBEE"));
                tv.setTextColor(Color.parseColor("#C62828"));
                break;
            default:
                tv.setBackgroundColor(Color.parseColor("#F5F5F5"));
                tv.setTextColor(Color.parseColor("#616161"));
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNamaClient, tvNamaJasa, tvStatus, btnAksi;

        public ViewHolder(@NonNull View v) {
            super(v);
            tvNamaClient = v.findViewById(R.id.tvNamaClient);
            tvNamaJasa = v.findViewById(R.id.tvNamaJasa);
            tvStatus = v.findViewById(R.id.tvStatus);
            btnAksi = v.findViewById(R.id.btnAksi);
        }
    }
}