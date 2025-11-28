package com.example.mainactivity;

import android.app.DatePickerDialog;
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

import com.example.mainactivity.TrilhaAdapter;
import com.example.mainactivity.dao.TrilhaDAO;
import com.example.mainactivity.model.Trilha;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
        menu.getMenu().add(getString(R.string.menu_excluir_tudo));
        menu.getMenu().add(getString(R.string.menu_excluir_intervalo));

        menu.setOnMenuItemClickListener(item -> {
            String title = item.getTitle().toString();
            if (title.equals(getString(R.string.menu_excluir_tudo))) {
                confirmarExclusaoTotal();
            } else if (title.equals(getString(R.string.menu_excluir_intervalo))) {
                escolherIntervaloExclusao();
            }
            return true;
        });

        menu.show();
    }

    private void confirmarExclusaoTotal() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.excluir_tudo_titulo))
                .setMessage(getString(R.string.excluir_tudo_msg))
                .setPositiveButton(getString(R.string.sim), (dialog, which) -> {
                    getDatabasePath("trilhas.db").delete();
                    Toast.makeText(this, getString(R.string.todas_trilhas_apagadas), Toast.LENGTH_SHORT).show();
                    carregarTrilhas();
                })
                .setNegativeButton(getString(R.string.cancelar), null)
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

            Intent shareIntent = Intent.createChooser(sendIntent, getString(R.string.compartilhar_trilha));
            startActivity(shareIntent);

        } catch (Exception e) {
            Toast.makeText(this, String.format(getString(R.string.erro_compartilhar), e.getMessage()), Toast.LENGTH_SHORT).show();
        }
    }


    private void excluirTrilha(Trilha trilha) {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.excluir_trilha_titulo))
                .setMessage(String.format(getString(R.string.excluir_trilha_msg), trilha.getNome()))
                .setPositiveButton(getString(R.string.sim), (dialog, which) -> {
                    apagarUmaTrilha(trilha.getId());
                })
                .setNegativeButton(getString(R.string.cancelar), null)
                .show();
    }

    private void apagarUmaTrilha(long id) {
        getWritableDatabase().execSQL("DELETE FROM trilha WHERE id = " + id);
        getWritableDatabase().execSQL("DELETE FROM trilha_pontos WHERE trilha_id = " + id);

        Toast.makeText(this, getString(R.string.trilha_apagada), Toast.LENGTH_SHORT).show();
        carregarTrilhas();
    }

    private SQLiteDatabase getWritableDatabase() {
        return new com.example.mainactivity.db.DbHelper(this).getWritableDatabase();
    }

    private void escolherIntervaloExclusao() {
        final Calendar c = Calendar.getInstance();
        DatePickerDialog dpStart = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    String inicio = String.format(Locale.getDefault(), "%02d/%02d/%04d", dayOfMonth, month + 1, year);

                    // depois do start, selecionar end
                    final Calendar c2 = Calendar.getInstance();
                    DatePickerDialog dpEnd = new DatePickerDialog(
                            this,
                            (v2, y2, m2, d2) -> {
                                String fim = String.format(Locale.getDefault(), "%02d/%02d/%04d", d2, m2 + 1, y2);
                                confirmarExclusaoIntervalo(inicio, fim);
                            },
                            c2.get(Calendar.YEAR),
                            c2.get(Calendar.MONTH),
                            c2.get(Calendar.DAY_OF_MONTH)
                    );
                    dpEnd.show();
                },
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH)
        );
        dpStart.show();
    }

    private void confirmarExclusaoIntervalo(String inicio, String fim) {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.excluir_intervalo_titulo))
                .setMessage(String.format(getString(R.string.excluir_intervalo_msg), inicio, fim))
                .setPositiveButton(getString(R.string.sim), (dialog, which) -> {
                    // delegar para DAO
                    trilhaDAO.apagarPorIntervalo(inicio, fim);
                    Toast.makeText(this, getString(R.string.trilhas_intervalo_apagadas), Toast.LENGTH_SHORT).show();
                    carregarTrilhas();
                })
                .setNegativeButton(getString(R.string.cancelar), null)
                .show();
    }
}
