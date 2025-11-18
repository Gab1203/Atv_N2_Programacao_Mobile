package com.example.mainactivity.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.mainactivity.db.DbHelper;
import com.example.mainactivity.model.PontoTrilha;

import java.util.ArrayList;
import java.util.List;

public class PontoTrilhaDAO {

    private DbHelper dbHelper;

    public PontoTrilhaDAO(Context context) {
        dbHelper = new DbHelper(context);
    }

    public void inserirPonto(long trilhaId, PontoTrilha p) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("trilha_id", trilhaId);
        cv.put("latitude", p.getLatitude());
        cv.put("longitude", p.getLongitude());
        cv.put("velocidade", p.getVelocidade());
        cv.put("accuracy", p.getAcuracia());
        cv.put("data_hora", p.getDataHora());

        db.insert(DbHelper.TABLE_PONTOS, null, cv);
        db.close();
    }

    public List<PontoTrilha> listarPontos(long trilhaId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<PontoTrilha> pontos = new ArrayList<>();

        Cursor c = db.rawQuery(
                "SELECT * FROM trilha_pontos WHERE trilha_id = ? ORDER BY id ASC",
                new String[]{String.valueOf(trilhaId)}
        );

        while (c.moveToNext()) {
            PontoTrilha p = new PontoTrilha(
                    c.getDouble(c.getColumnIndexOrThrow("latitude")),
                    c.getDouble(c.getColumnIndexOrThrow("longitude")),
                    c.getDouble(c.getColumnIndexOrThrow("velocidade")),
                    c.getDouble(c.getColumnIndexOrThrow("accuracy")),
                    c.getString(c.getColumnIndexOrThrow("data_hora"))
            );
            pontos.add(p);
        }

        c.close();
        db.close();
        return pontos;
    }
}
