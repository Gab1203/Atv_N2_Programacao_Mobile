package com.example.mainactivity.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.example.mainactivity.db.DbHelper;
import com.example.mainactivity.model.Trilha;

import java.util.ArrayList;
import java.util.List;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TrilhaDAO {

    private DbHelper dbHelper;

    public TrilhaDAO(Context context) {
        dbHelper = new DbHelper(context);
    }

    public void apagarPorIntervalo(String inicio, String fim) {
        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try {
            Date dInicio = fmt.parse(inicio);
            Date dFim = fmt.parse(fim);

            if (dInicio == null || dFim == null) return;

            if (dInicio.after(dFim)) {
                Date tmp = dInicio;
                dInicio = dFim;
                dFim = tmp;
            }

            SQLiteDatabase db = dbHelper.getWritableDatabase();

            List<Trilha> todas = listar();
            for (Trilha t : todas) {
                try {
                    Date d = fmt.parse(t.getDataInicio());
                    if (d != null && !d.before(dInicio) && !d.after(dFim)) {
                        db.delete(DbHelper.TABLE_TRILHA, "id = ?", new String[]{String.valueOf(t.getId())});
                        db.delete("trilha_pontos", "trilha_id = ?", new String[]{String.valueOf(t.getId())});
                    }
                } catch (ParseException e) {

                }
            }

            db.close();

        } catch (ParseException e) {

        }
    }

    public long inserirTrilha(Trilha trilha) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("nome", trilha.getNome());
        cv.put("data_inicio", trilha.getDataInicio());
        cv.put("hora_inicio", trilha.getHoraInicio());

        long id = db.insert(DbHelper.TABLE_TRILHA, null, cv);
        db.close();
        return id;
    }

    public void atualizarTrilha(Trilha trilha) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("data_fim", trilha.getDataFim());
        cv.put("hora_fim", trilha.getHoraFim());
        cv.put("gasto_kcal", trilha.getGastoKcal());
        cv.put("distancia_percorrida", trilha.getDistanciaPercorrida());
        cv.put("velocidade_media", trilha.getVelocidadeMedia());
        cv.put("velocidade_maxima", trilha.getVelocidadeMaxima());

        db.update(DbHelper.TABLE_TRILHA, cv, "id = ?", new String[]{String.valueOf(trilha.getId())});
        db.close();
    }

    public Trilha buscarPorId(long id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Trilha t;

        try(Cursor c = db.rawQuery("SELECT * FROM trilha WHERE id = ?", new String[]{String.valueOf(id)})) {
            if (c.moveToFirst()) {
                t = cursorParaTrilha(c);
                Log.d("VisualizarTrilha",t.toString());
                c.close();
                db.close();
                return t;
            }
        }catch (Exception e){
            Log.d("VisualizarTrilha",e.getMessage());
        }
        return null;
    }

    public List<Trilha> listar() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Trilha> trilhas = new ArrayList<>();

        try(Cursor c = db.rawQuery("SELECT * FROM trilha ORDER BY id DESC", null)) {
            while (c.moveToNext()) {
                trilhas.add(cursorParaTrilha(c));
            }
            c.close();
            db.close();
        }
        return trilhas;
    }

    public void atualizarCamposIniciais(Trilha trilha) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("nome", trilha.getNome());
        cv.put("data_inicio", trilha.getDataInicio());
        cv.put("hora_inicio", trilha.getHoraInicio());

        db.update(DbHelper.TABLE_TRILHA, cv, "id = ?", new String[]{String.valueOf(trilha.getId())});
        db.close();
    }


    private Trilha cursorParaTrilha(Cursor c) {
        Trilha t = new Trilha();
        t.setId(c.getLong(c.getColumnIndexOrThrow("id")));
        t.setNome(c.getString(c.getColumnIndexOrThrow("nome")));
        t.setDataInicio(c.getString(c.getColumnIndexOrThrow("data_inicio")));
        t.setHoraInicio(c.getString(c.getColumnIndexOrThrow("hora_inicio")));
        t.setDataFim(c.getString(c.getColumnIndexOrThrow("data_fim")));
        t.setHoraFim(c.getString(c.getColumnIndexOrThrow("hora_fim")));
        t.setGastoKcal(c.getDouble(c.getColumnIndexOrThrow("gasto_kcal")));
        t.setDistanciaPercorrida(c.getDouble(c.getColumnIndexOrThrow("distancia_percorrida")));
        t.setVelocidadeMedia(c.getDouble(c.getColumnIndexOrThrow("velocidade_media")));
        t.setVelocidadeMaxima(c.getDouble(c.getColumnIndexOrThrow("velocidade_maxima")));

        return t;
    }
}
