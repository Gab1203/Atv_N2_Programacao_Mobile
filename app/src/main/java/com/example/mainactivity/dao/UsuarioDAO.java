package com.example.mainactivity.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.mainactivity.db.DbHelper;
import com.example.mainactivity.model.Usuario;

import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {
    private DbHelper dbHelper;

    public UsuarioDAO(Context context) {
        dbHelper = new DbHelper(context);
    }

    public long inserirUsuario(Usuario usuario) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("nome", usuario.getNome());
        cv.put("data_nascimento", usuario.getDataNascimento());
        cv.put("sexo", usuario.getSexo());
        cv.put("altura", usuario.getAltura());
        cv.put("peso", usuario.getPeso());

        long id = db.insert(DbHelper.TABLE_USUARIO, null, cv);
        db.close();
        return id;
    }

    public void atualizarUsuario(Usuario usuario) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("nome", usuario.getNome());
        cv.put("data_nascimento", usuario.getDataNascimento());
        cv.put("sexo", usuario.getSexo());
        cv.put("altura", usuario.getAltura());
        cv.put("peso", usuario.getPeso());

        db.update(DbHelper.TABLE_USUARIO, cv, "id = ?", new String[]{String.valueOf(usuario.getId())});
        db.close();
    }

    public Usuario buscarPrimeiroUsuario() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor c = db.rawQuery("SELECT * FROM usuario LIMIT 1", null);
        if (c.moveToFirst()) {
            Usuario u = cursorParaUsuario(c);
            c.close();
            db.close();
            return u;
        }

        c.close();
        db.close();
        return null;
    }

    private Usuario cursorParaUsuario(Cursor c) {
        Usuario u = new Usuario();
        u.setId(c.getLong(c.getColumnIndexOrThrow("id")));
        u.setNome(c.getString(c.getColumnIndexOrThrow("nome")));
        u.setDataNascimento(c.getString(c.getColumnIndexOrThrow("data_nascimento")));
        u.setSexo(c.getString(c.getColumnIndexOrThrow("sexo")));

        try {
            u.setAltura(Double.parseDouble(c.getString(c.getColumnIndexOrThrow("altura"))));
        } catch (Exception e) {
            u.setAltura(0);
        }
        try {
            u.setPeso(Double.parseDouble(c.getString(c.getColumnIndexOrThrow("peso"))));
        } catch (Exception e) {
            u.setPeso(0);
        }

        return u;
    }

    public void salvarUsuario(Usuario usuario) {
        Usuario existente = buscarPrimeiroUsuario();
        if (existente == null) {
            inserirUsuario(usuario);
        } else {
            usuario.setId(existente.getId());
            atualizarUsuario(usuario);
        }
    }
}
