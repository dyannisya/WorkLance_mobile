package com.example.kelolajasa.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kelolajasa.R;
import com.example.kelolajasa.model.Pengguna;

import java.util.ArrayList;
import java.util.List;

public class PenggunaAdapter extends RecyclerView.Adapter<PenggunaAdapter.ViewHolder> {

    public interface ActionListener {
        void onDelete(Pengguna pengguna);
        void onLihat(Pengguna pengguna);
    }

    private List<Pengguna> list = new ArrayList<>();
    private List<Pengguna> listFull = new ArrayList<>();
    private ActionListener listener;

    public PenggunaAdapter(List<Pengguna> list, ActionListener listener) {
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
                .inflate(R.layout.item_pengguna, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
        Pengguna p = list.get(pos);
        h.valNama.setText(p.getNamaPengguna());

        // Role badge
        switch (p.getIdRole()) {
            case 1:
                h.valRole.setText("ADMIN");
                h.valRole.setTextColor(Color.parseColor("#C62828"));
                h.valRole.setBackgroundColor(Color.parseColor("#FFEBEE"));
                break;
            case 3:
                h.valRole.setText("FREELANCER");
                h.valRole.setTextColor(Color.parseColor("#E65100"));
                h.valRole.setBackgroundColor(Color.parseColor("#FFF3E0"));
                break;
            default:
                h.valRole.setText("USER");
                h.valRole.setTextColor(Color.parseColor("#1565C0"));
                h.valRole.setBackgroundColor(Color.parseColor("#E3F2FD"));
                break;
        }

        h.btnLihat.setOnClickListener(v -> { if (listener != null) listener.onLihat(p); });
        h.btnDelete.setOnClickListener(v -> { if (listener != null) listener.onDelete(p); });
    }

    @Override
    public int getItemCount() { return list.size(); }

    /** Filter berdasarkan role. role=-1 untuk semua. */
    public void filterByRole(int idRole) {
        list.clear();
        if (idRole == -1) {
            list.addAll(listFull);
        } else {
            for (Pengguna p : listFull) {
                if (p.getIdRole() == idRole) list.add(p);
            }
        }
        notifyDataSetChanged();
    }

    /** Filter berdasarkan keyword nama. */
    public void filterByKeyword(String keyword) {
        list.clear();
        if (keyword == null || keyword.isEmpty()) {
            list.addAll(listFull);
        } else {
            String kw = keyword.toLowerCase();
            for (Pengguna p : listFull) {
                if (p.getNamaPengguna().toLowerCase().contains(kw) ||
                        p.getUsername().toLowerCase().contains(kw)) {
                    list.add(p);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void setData(List<Pengguna> newList) {
        this.listFull = newList != null ? new ArrayList<>(newList) : new ArrayList<>();
        this.list = new ArrayList<>(this.listFull);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView valNama, valRole;
        ImageView btnLihat, btnDelete;

        public ViewHolder(@NonNull View v) {
            super(v);
            valNama = v.findViewById(R.id.valNama);
            valRole = v.findViewById(R.id.valRole);
            btnLihat = v.findViewById(R.id.btnLihat);
            btnDelete = v.findViewById(R.id.btnDelete);
        }
    }
}