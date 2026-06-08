package com.example.kelolajasa.adapter;

import android.content.Context;
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

public class BookingAdminAdapter extends RecyclerView.Adapter<BookingAdminAdapter.ViewHolder> {

    private List<BookingDisplay> list = new ArrayList<>();
    private Context context;

    public BookingAdminAdapter(Context context, List<BookingDisplay> list) {
        this.context = context;
        if (list != null) this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_booking_admin, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BookingDisplay item = list.get(position);
        holder.tvNamaClient.setText(item.getNamaClient());
        holder.tvNamaJasa.setText(item.getNamaJasa());
        holder.tvStatus.setText(item.getStatusBooking());

        // Warna badge berdasarkan status
        switch (item.getStatusBooking()) {
            case "Menunggu":
                holder.tvStatus.setBackgroundColor(Color.parseColor("#FFF3E0"));
                holder.tvStatus.setTextColor(Color.parseColor("#E65100"));
                break;
            case "Diproses":
                holder.tvStatus.setBackgroundColor(Color.parseColor("#E3F2FD"));
                holder.tvStatus.setTextColor(Color.parseColor("#1565C0"));
                break;
            case "Selesai":
                holder.tvStatus.setBackgroundColor(Color.parseColor("#E8F5E9"));
                holder.tvStatus.setTextColor(Color.parseColor("#2E7D32"));
                break;
            case "Dibatalkan":
                holder.tvStatus.setBackgroundColor(Color.parseColor("#FFEBEE"));
                holder.tvStatus.setTextColor(Color.parseColor("#C62828"));
                break;
            default:
                holder.tvStatus.setBackgroundColor(Color.parseColor("#F5F5F5"));
                holder.tvStatus.setTextColor(Color.parseColor("#616161"));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setData(List<BookingDisplay> newList) {
        this.list = newList != null ? newList : new ArrayList<>();
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNamaClient, tvNamaJasa, tvStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNamaClient = itemView.findViewById(R.id.tvNamaClient);
            tvNamaJasa = itemView.findViewById(R.id.tvNamaJasa);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }
}