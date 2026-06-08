package com.example.kelolajasa.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kelolajasa.R;
import com.example.kelolajasa.model.Pesan;

import java.util.ArrayList;
import java.util.List;

public class ChatBubbleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_SENT     = 1;   // pesan dari saya (kanan)
    private static final int TYPE_RECEIVED = 2;   // pesan dari lawan (kiri)

    private List<Pesan> list = new ArrayList<>();
    private final int idSaya;   // id pengguna yang sedang login

    public ChatBubbleAdapter(int idSaya) {
        this.idSaya = idSaya;
    }

    public void setData(List<Pesan> newList) {
        this.list = newList != null ? new ArrayList<>(newList) : new ArrayList<>();
        notifyDataSetChanged();
    }

    /** Tambah satu pesan baru di ujung list tanpa reload semua */
    public void addPesan(Pesan pesan) {
        list.add(pesan);
        notifyItemInserted(list.size() - 1);
    }

    @Override
    public int getItemViewType(int position) {
        return list.get(position).getIdPengirim() == idSaya ? TYPE_SENT : TYPE_RECEIVED;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inf = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_SENT) {
            View v = inf.inflate(R.layout.item_chat_right, parent, false);
            return new BubbleViewHolder(v);
        } else {
            View v = inf.inflate(R.layout.item_chat_left, parent, false);
            return new BubbleViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Pesan item = list.get(position);
        BubbleViewHolder h = (BubbleViewHolder) holder;
        h.tvIsiPesan.setText(item.getIsiPesan());
        h.tvWaktu.setText(item.getWaktuFormatted());
    }

    @Override
    public int getItemCount() { return list.size(); }

    static class BubbleViewHolder extends RecyclerView.ViewHolder {
        TextView tvIsiPesan, tvWaktu;

        BubbleViewHolder(@NonNull View v) {
            super(v);
            tvIsiPesan = v.findViewById(R.id.tvIsiPesan);
            tvWaktu    = v.findViewById(R.id.tvWaktu);
        }
    }
}