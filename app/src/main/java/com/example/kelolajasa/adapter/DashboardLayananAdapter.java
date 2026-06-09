package com.example.kelolajasa.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kelolajasa.PemesananActivity;
import com.example.kelolajasa.R;
import com.example.kelolajasa.model.LayananDisplay;

import java.util.List;

public class DashboardLayananAdapter
        extends RecyclerView.Adapter<DashboardLayananAdapter.ViewHolder> {

    private Context context;
    private List<LayananDisplay> list;

    public DashboardLayananAdapter(Context context,
                                   List<LayananDisplay> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(
                        R.layout.item_dashboard_layanan,
                        parent,
                        false
                );

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position) {

        LayananDisplay item = list.get(position);

        holder.tvNamaLayanan.setText(
                item.getNamaLayanan()
        );

        holder.tvFreelancer.setText(
                item.getNamaFreelancer()
        );

        holder.tvHarga.setText(
                item.getHargaFormatted()
        );

        holder.tvRating.setText(
                "⭐ " + item.getRataRating()
        );

        holder.itemView.setOnClickListener(v -> {

            Intent intent =
                    new Intent(context,
                            PemesananActivity.class);

            intent.putExtra(
                    "id_layanan",
                    item.getIdLayanan()
            );

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder
            extends RecyclerView.ViewHolder {

        TextView tvNamaLayanan,
                tvFreelancer,
                tvHarga,
                tvRating;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvNamaLayanan =
                    itemView.findViewById(
                            R.id.tvNamaLayanan);

            tvFreelancer =
                    itemView.findViewById(
                            R.id.tvFreelancer);

            tvHarga =
                    itemView.findViewById(
                            R.id.tvHarga);

            tvRating =
                    itemView.findViewById(
                            R.id.tvRating);
        }
    }
}