package com.example.kelolajasa.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.kelolajasa.R;
import com.example.kelolajasa.model.Pengguna;
import java.util.List;

public class FreelancerUnggulanAdapter extends RecyclerView.Adapter<FreelancerUnggulanAdapter.ViewHolder> {

    private List<Pengguna> listFreelancer;

    public FreelancerUnggulanAdapter(List<Pengguna> listFreelancer) {
        this.listFreelancer = listFreelancer;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_freelancer_unggulan, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Pengguna freelancer = listFreelancer.get(position);

        // Set nama freelancer dari database
        holder.tvNamaFreelancer.setText(freelancer.getNamaPengguna());

        // Set lokasi (sementara default atau ambil dari database jika field tersedia)
        holder.tvLokasi.setText("Indonesia");

        // Catatan: Rating dan Foto bisa kamu sesuaikan logicnya nanti
    }

    @Override
    public int getItemCount() {
        return listFreelancer != null ? listFreelancer.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNamaFreelancer, tvKategoriUtama, tvLokasi, tvRating;
        ImageView ivFotoFreelancer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNamaFreelancer = itemView.findViewById(R.id.tvNamaFreelancer);
            tvKategoriUtama = itemView.findViewById(R.id.tvKategoriUtama);
            tvLokasi = itemView.findViewById(R.id.tvLokasi);
            tvRating = itemView.findViewById(R.id.tvRating);
            ivFotoFreelancer = itemView.findViewById(R.id.ivFotoFreelancer);
        }
    }
}