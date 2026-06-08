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
import com.example.kelolajasa.database.BookingDAO;
import com.example.kelolajasa.model.RiwayatDisplay;

import java.util.ArrayList;
import java.util.List;

public class RiwayatAdapter extends RecyclerView.Adapter<RiwayatAdapter.ViewHolder> {

    public interface OnAksiClick {
        void onAksi(RiwayatDisplay item);
    }

    private Context context;
    private List<RiwayatDisplay> list = new ArrayList<>();
    private List<RiwayatDisplay> listFull = new ArrayList<>();
    private OnAksiClick listener;
    // true = tampilkan sebagai freelancer (lihat pesanan masuk), false = user
    private boolean isFreelancerView;

    public RiwayatAdapter(Context context, List<RiwayatDisplay> list,
                          boolean isFreelancerView, OnAksiClick listener) {
        this.context = context;
        if (list != null) {
            this.list = new ArrayList<>(list);
            this.listFull = new ArrayList<>(list);
        }
        this.isFreelancerView = isFreelancerView;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_riwayat, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
        RiwayatDisplay item = list.get(pos);

        // Untuk freelancer: nama client; untuk user: nama freelancer
        h.tvNamaFreelancer.setText(item.getNamaFreelancer());
        h.tvNamaLayanan.setText(item.getNamaLayanan());
        h.tvHarga.setText(item.getHargaFormatted());
        h.tvTanggal.setText("📅 " + item.getTanggalFormatted());
        h.tvStatus.setText(item.getStatusBooking());

        // Status badge color
        applyStatusColor(h.tvStatus, item.getStatusBooking());

        // Tombol aksi berdasarkan status dan role
        String labelAksi;
        if (isFreelancerView) {
            // Freelancer bisa ubah status ke Diproses/Selesai
            labelAksi = item.getStatusBooking().equals(BookingDAO.STATUS_MENUNGGU)
                    ? "Terima" : "Ubah Status";
        } else {
            // User bisa batalkan yang masih Menunggu, atau lihat detail
            labelAksi = item.getStatusBooking().equals(BookingDAO.STATUS_MENUNGGU)
                    ? "Batalkan" : "Detail";
        }
        h.btnAksi.setText(labelAksi);
        h.btnAksi.setOnClickListener(v -> { if (listener != null) listener.onAksi(item); });
    }

    @Override
    public int getItemCount() { return list.size(); }

    public void filterByStatus(String status) {
        list.clear();
        if (status == null || status.equals("Semua")) {
            list.addAll(listFull);
        } else {
            for (RiwayatDisplay r : listFull) {
                if (r.getStatusBooking().equals(status)) list.add(r);
            }
        }
        notifyDataSetChanged();
    }

    public void setData(List<RiwayatDisplay> newList) {
        this.listFull = newList != null ? new ArrayList<>(newList) : new ArrayList<>();
        this.list = new ArrayList<>(listFull);
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
            default: // Dibatalkan
                tv.setBackgroundColor(Color.parseColor("#FFEBEE"));
                tv.setTextColor(Color.parseColor("#C62828"));
                break;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNamaFreelancer, tvStatus, tvNamaLayanan, tvHarga, tvTanggal, btnAksi;

        public ViewHolder(@NonNull View v) {
            super(v);
            tvNamaFreelancer = v.findViewById(R.id.tvNamaFreelancer);
            tvStatus = v.findViewById(R.id.tvStatus);
            tvNamaLayanan = v.findViewById(R.id.tvNamaLayanan);
            tvHarga = v.findViewById(R.id.tvHarga);
            tvTanggal = v.findViewById(R.id.tvTanggal);
            btnAksi = v.findViewById(R.id.btnAksi);
        }
    }
}