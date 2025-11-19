package com.example.mainactivity.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.mainactivity.db.DbHelper;
import com.example.mainactivity.model.Trilha;

import java.util.ArrayList;
import java.util.List;

public class TrilhaDAO {

    private DbHelper dbHelper;

    public TrilhaDAO(Context context) {
        dbHelper = new DbHelper(context);
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

        Cursor c = db.rawQuery("SELECT * FROM trilha WHERE id = ?", new String[]{String.valueOf(id)});
        if (c.moveToFirst()) {
            Trilha t = cursorParaTrilha(c);
            c.close();
            db.close();
            return t;
        }

        c.close();
        db.close();
        return null;
    }

    public List<Trilha> listar() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Trilha> trilhas = new ArrayList<>();

        Cursor c = db.rawQuery("SELECT * FROM trilha ORDER BY id DESC", null);
        while (c.moveToNext()) {
            trilhas.add(cursorParaTrilha(c));
        }

        c.close();
        db.close();
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
