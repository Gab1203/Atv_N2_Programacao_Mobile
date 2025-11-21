package com.example.mainactivity.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "trilhas.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_TRILHA = "trilha";
    public static final String TABLE_PONTOS = "trilha_pontos";
    public static final String TABLE_USUARIO = "usuario";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + TABLE_TRILHA + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nome TEXT NOT NULL," +
                "data_inicio TEXT," +
                "hora_inicio TEXT," +
                "data_fim TEXT," +
                "hora_fim TEXT," +
                "gasto_kcal REAL," +
                "distancia_percorrida REAL," +
                "velocidade_media REAL," +
                "velocidade_maxima REAL" +
                ");"
        );

        db.execSQL("CREATE TABLE " + TABLE_PONTOS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "trilha_id INTEGER NOT NULL," +
                "latitude REAL," +
                "longitude REAL," +
                "velocidade REAL," +
                "accuracy REAL," +
                "data_hora TEXT," +
                "FOREIGN KEY(trilha_id) REFERENCES trilha(id)" +
                ");"
        );

        db.execSQL("CREATE TABLE " + TABLE_USUARIO + " ("+
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nome TEXT,"+
                "data_nascimento TEXT," +
                "sexo TEXT," +
                "altura TEXT,"+
                "peso TEXT" +
                ");"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}

