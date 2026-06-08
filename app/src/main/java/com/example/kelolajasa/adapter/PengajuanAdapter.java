package com.example.kelolajasa.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kelolajasa.R;
import com.example.kelolajasa.model.PengajuanFreelancer;

import java.util.ArrayList;
import java.util.List;

public class PengajuanAdapter extends RecyclerView.Adapter<PengajuanAdapter.ViewHolder> {

    public interface OnDetailClick {
        void onDetail(PengajuanFreelancer item);
    }

    private List<PengajuanFreelancer> list = new ArrayList<>();
    private List<PengajuanFreelancer> listFull = new ArrayList<>();
    // Parallel list for nama pengguna (from JOIN)
    private List<String> namaList = new ArrayList<>();
    private OnDetailClick listener;

    public PengajuanAdapter(List<PengajuanFreelancer> list,
                            List<String> namaList,
                            OnDetailClick listener) {
        if (list != null) {
            this.list = new ArrayList<>(list);
            this.listFull = new ArrayList<>(list);
        }
        if (namaList != null) this.namaList = namaList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pengajuan, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
        PengajuanFreelancer item = list.get(pos);
        String nama = (pos < namaList.size()) ? namaList.get(pos) : "-";

        // Format tanggal ringkas: 2026-04-15 → 15/04
        h.tvTanggal.setText(formatTanggalRingkas(item.getTanggalPengajuan()));
        h.tvNama.setText(nama);
        h.tvStatus.setText(item.getStatus());
        applyStatusColor(h.tvStatus, item.getStatus());

        h.btnAksi.setOnClickListener(v -> { if (listener != null) listener.onDetail(item); });
    }

    @Override
    public int getItemCount() { return list.size(); }

    public void filterByStatus(String status) {
        list.clear();
        if (status == null || status.equals("Semua")) {
            list.addAll(listFull);
        } else {
            for (PengajuanFreelancer p : listFull) {
                if (p.getStatus().equals(status)) list.add(p);
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
            for (int i = 0; i < listFull.size(); i++) {
                String nama = (i < namaList.size()) ? namaList.get(i).toLowerCase() : "";
                if (nama.contains(kw)) list.add(listFull.get(i));
            }
        }
        notifyDataSetChanged();
    }

    private String formatTanggalRingkas(String tanggal) {
        if (tanggal == null || tanggal.length() < 10) return "-";
        String[] parts = tanggal.split("-");
        if (parts.length < 3) return tanggal;
        return parts[2] + "/" + parts[1];
    }

    private void applyStatusColor(TextView tv, String status) {
        switch (status) {
            case "Menunggu":
                tv.setBackgroundColor(Color.parseColor("#FFF3E0"));
                tv.setTextColor(Color.parseColor("#E65100"));
                break;
            case "Diterima":
                tv.setBackgroundColor(Color.parseColor("#E8F5E9"));
                tv.setTextColor(Color.parseColor("#2E7D32"));
                break;
            case "Ditolak":
                tv.setBackgroundColor(Color.parseColor("#FFEBEE"));
                tv.setTextColor(Color.parseColor("#C62828"));
                break;
            default:
                tv.setBackgroundColor(Color.parseColor("#F5F5F5"));
                tv.setTextColor(Color.parseColor("#616161"));
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTanggal, tvNama, tvStatus, btnAksi;

        public ViewHolder(@NonNull View v) {
            super(v);
            tvTanggal = v.findViewById(R.id.tvTanggal);
            tvNama = v.findViewById(R.id.tvNama);
            tvStatus = v.findViewById(R.id.tvStatus);
            btnAksi = v.findViewById(R.id.btnAksi);
        }
    }
}