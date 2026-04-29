package com.example.kelolajasa.adapter;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kelolajasa.R;
import com.example.kelolajasa.model.Jasa;

import java.util.ArrayList;
import java.util.List;

public class JasaAdapter extends RecyclerView.Adapter<JasaAdapter.ViewHolder> {

    private List<Jasa> list;
    private OnDeleteClickListener listener;
    private OnEditClickListener editListener;
    private boolean isEmpty = false;

    public interface OnDeleteClickListener {
        void onDelete(Jasa jasa);
    }

    public interface OnEditClickListener {
        void onEdit(Jasa jasa);
    }

    public JasaAdapter(List<Jasa> list,
                       OnDeleteClickListener deleteListener,
                       OnEditClickListener editListener) {
        this.list = list;
        this.listener = deleteListener;
        this.editListener = editListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_jasa, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Jasa jasa = list.get(position);

        holder.valNama.setText(jasa.getNama());
        holder.valKategori.setText(jasa.getKategori());

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDelete(jasa);
            }
        });

        holder.btnEdit.setOnClickListener(v -> {
            if (editListener != null) {
                editListener.onEdit(jasa);
            }
        });
    }

    @Override
    public int getItemCount() {
        return isEmpty ? 1 : list.size();
    }

    public void setData(List<Jasa> newList) {
        if (newList == null || newList.isEmpty()) {
            this.list = new ArrayList<>();
            isEmpty = true;
        } else {
            this.list = newList;
            isEmpty = false;
        }
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView valNama, valKategori;
        ImageView btnDelete, btnEdit;

        public ViewHolder(View itemView) {
            super(itemView);

            valNama = itemView.findViewById(R.id.valNama);
            valKategori = itemView.findViewById(R.id.valKategori);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnEdit = itemView.findViewById(R.id.btnEdit);
        }
    }
}