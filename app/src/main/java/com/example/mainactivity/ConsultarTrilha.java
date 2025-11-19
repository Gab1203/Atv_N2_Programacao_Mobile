package com.example.mainactivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mainactivity.adapter.TrilhaAdapter;
import com.example.mainactivity.dao.TrilhaDAO;
import com.example.mainactivity.model.Trilha;

import java.util.List;

public class ConsultarTrilha extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TrilhaDAO trilhaDAO;
    private List<Trilha> listaTrilhas;
    private TrilhaAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultar_trilha);

        trilhaDAO = new TrilhaDAO(this);

        recyclerView = findViewById(R.id.recyclerTrilhas);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ImageButton btnMenu = findViewById(R.id.btnMenu);
        btnMenu.setOnClickListener(this::abrirMenuOpcoes);

        carregarTrilhas();
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarTrilhas(); // pra atualizar quando voltar
    }

    private void carregarTrilhas() {
        listaTrilhas = trilhaDAO.listar();

        adapter = new TrilhaAdapter(
                this,
                listaTrilhas,
                new TrilhaAdapter.OnTrilhaActionListener() {
                    @Override
                    public void onVisualizar(Trilha t) {
                        visualizarTrilha(t);
                    }

                    @Override
                    public void onEditar(Trilha t) {
                        editarTrilha(t);
                    }

                    @Override
                    public void onCompartilhar(Trilha t) {
                        compartilharTrilha(t);
                    }

                    @Override
                    public void onExcluir(Trilha t) {
                        excluirTrilha(t);
                    }
                }
        );


        recyclerView.setAdapter(adapter);
    }

    private void abrirMenuOpcoes(View v) {
        PopupMenu menu = new PopupMenu(this, v);
        menu.getMenu().add("Excluir todas as trilhas");
        menu.getMenu().add("Excluir trilhas por intervalo (em breve)");

        menu.setOnMenuItemClickListener(item -> {
            if (item.getTitle().equals("Excluir todas as trilhas")) {
                confirmarExclusaoTotal();
            }
            return true;
        });

        menu.show();
    }

    private void confirmarExclusaoTotal() {
        new AlertDialog.Builder(this)
                .setTitle("Excluir tudo?")
                .setMessage("Isso removerá todas as trilhas registradas. Deseja continuar?")
                .setPositiveButton("Sim", (dialog, which) -> {
                    getDatabasePath("trilhas.db").delete();
                    Toast.makeText(this, "Todas as trilhas foram apagadas.", Toast.LENGTH_SHORT).show();
                    carregarTrilhas();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void visualizarTrilha(Trilha trilha) {
        Intent i = new Intent(this, VisualizarTrilhaActivity.class);
        i.putExtra("trilha_id", trilha.getId());
        startActivity(i);
    }

    private void editarTrilha(Trilha trilha) {
        Intent i = new Intent(this, EditarTrilhaActivity.class);
        i.putExtra("trilha_id", trilha.getId());
        startActivity(i);
    }

    private void compartilharTrilha(Trilha trilha) {
        try {
            String json = trilha.toJson();

            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, json);
            sendIntent.setType("application/json");

            Intent shareIntent = Intent.createChooser(sendIntent, "Compartilhar trilha");
            startActivity(shareIntent);

        } catch (Exception e) {
            Toast.makeText(this, "Erro ao compartilhar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    private void excluirTrilha(Trilha trilha) {
        new AlertDialog.Builder(this)
                .setTitle("Excluir trilha")
                .setMessage("Deseja apagar a trilha \"" + trilha.getNome() + "\"?")
                .setPositiveButton("Sim", (dialog, which) -> {
                    apagarUmaTrilha(trilha.getId());
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void apagarUmaTrilha(long id) {
        getWritableDatabase().execSQL("DELETE FROM trilha WHERE id = " + id);
        getWritableDatabase().execSQL("DELETE FROM trilha_pontos WHERE trilha_id = " + id);

        Toast.makeText(this, "Trilha apagada.", Toast.LENGTH_SHORT).show();
        carregarTrilhas();
    }

    private SQLiteDatabase getWritableDatabase() {
        return new com.example.mainactivity.db.DbHelper(this).getWritableDatabase();
    }
}
