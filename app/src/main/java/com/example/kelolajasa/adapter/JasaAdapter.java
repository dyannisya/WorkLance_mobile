package com.example.kelolajasa.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kelolajasa.R;
import com.example.kelolajasa.model.Jasa;

import java.util.ArrayList;
import java.util.List;

public class JasaAdapter extends RecyclerView.Adapter<JasaAdapter.ViewHolder> {

    private List<Jasa> list = new ArrayList<>();
    private OnDeleteClickListener deleteListener;
    private OnEditClickListener editListener;

    public interface OnDeleteClickListener {
        void onDelete(Jasa jasa);
    }

    public interface OnEditClickListener {
        void onEdit(Jasa jasa);
    }

    public JasaAdapter(List<Jasa> list,
                       OnDeleteClickListener deleteListener,
                       OnEditClickListener editListener) {
        if (list != null) this.list = list;
        this.deleteListener = deleteListener;
        this.editListener = editListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_jasa, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Jasa jasa = list.get(position);

        // FIX: gunakan getNamaJasa() bukan getNama() yang tidak ada
        holder.valNama.setText(jasa.getNamaJasa());
        // Kategori name akan diisi saat Phase 4 (KelolaJasaActivity)
        // Sementara tampilkan ID kategori sebagai placeholder
        holder.valKategori.setText(
                jasa.getNamaKategori().isEmpty() ? "-" : jasa.getNamaKategori()
        );

        holder.btnDelete.setOnClickListener(v -> {
            if (deleteListener != null) deleteListener.onDelete(jasa);
        });

        holder.btnEdit.setOnClickListener(v -> {
            if (editListener != null) editListener.onEdit(jasa);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setData(List<Jasa> newList) {
        this.list = newList != null ? newList : new ArrayList<>();
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView valNama, valKategori;
        ImageView btnDelete, btnEdit;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            valNama = itemView.findViewById(R.id.valNama);
            valKategori = itemView.findViewById(R.id.valKategori);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnEdit = itemView.findViewById(R.id.btnEdit);
        }
    }
}