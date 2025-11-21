package com.example.mainactivity.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.mainactivity.db.DbHelper;
import com.example.mainactivity.model.Trilha;
import com.example.mainactivity.model.Usuario;

public class UsuarioDAO {
    private DbHelper dbHelper;

    public UsuarioDAO(Context context) {
        dbHelper = new DbHelper(context);
    }

    public long inserirUsuario(Usuario usuario) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("data_nascimento", usuario.getDataNascimento());
        cv.put("altura", usuario.getAltura());
        cv.put("peso", usuario.getPeso());
        cv.put("sexo", usuario.getSexo());
        long id = db.insert(DbHelper.TABLE_USUARIO, null, cv);
        db.close();
        return id;
    }
}
