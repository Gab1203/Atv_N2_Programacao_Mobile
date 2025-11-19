package com.example.mainactivity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mainactivity.R;
import com.example.mainactivity.model.Trilha;

import java.util.List;

public class TrilhaAdapter extends RecyclerView.Adapter<TrilhaAdapter.TrilhaViewHolder> {

    public interface OnTrilhaActionListener {
        void onVisualizar(Trilha trilha);
        void onEditar(Trilha trilha);
        void onCompartilhar(Trilha trilha);
        void onExcluir(Trilha trilha);
    }

    private Context context;
    private List<Trilha> trilhas;
    private OnTrilhaActionListener listener;

    public TrilhaAdapter(Context context, List<Trilha> trilhas, OnTrilhaActionListener listener) {
        this.context = context;
        this.trilhas = trilhas;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TrilhaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_trilha, parent, false);
        return new TrilhaViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TrilhaViewHolder holder, int position) {
        Trilha t = trilhas.get(position);

        holder.nome.setText(t.getNome());

        // Se não existir data fim, mostra só a data de início
        String data = (t.getDataInicio() != null ? t.getDataInicio() : "") + " " +
                (t.getHoraInicio() != null ? t.getHoraInicio() : "");
        holder.data.setText(data);

        holder.btnVisualizar.setOnClickListener(v -> listener.onVisualizar(t));
        holder.btnEditar.setOnClickListener(v -> listener.onEditar(t));
        holder.btnCompartilhar.setOnClickListener(v -> listener.onCompartilhar(t));
        holder.btnExcluir.setOnClickListener(v -> listener.onExcluir(t));
    }

    @Override
    public int getItemCount() {
        return trilhas.size();
    }

    static class TrilhaViewHolder extends RecyclerView.ViewHolder {

        TextView nome, data;
        ImageButton btnEditar, btnExcluir, btnVisualizar, btnCompartilhar;

        public TrilhaViewHolder(@NonNull View itemView) {
            super(itemView);

            nome = itemView.findViewById(R.id.txtNome);
            data = itemView.findViewById(R.id.txtData);

            btnEditar = itemView.findViewById(R.id.btnEditar);
            btnExcluir = itemView.findViewById(R.id.btnExcluir);
            btnVisualizar = itemView.findViewById(R.id.btnVisualizar);
            btnCompartilhar = itemView.findViewById(R.id.btnCompartilhar);
        }
    }
}
