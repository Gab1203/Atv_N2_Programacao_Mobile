package com.example.mainactivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mainactivity.dao.UsuarioDAO;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mainactivity.dao.UsuarioDAO;
import com.example.mainactivity.model.Usuario;

public class ConfiguracaoActivity extends AppCompatActivity {
    private EditText nomeUsuario;
    private EditText dataNascimento;
    private RadioGroup sexo;
    private EditText altura;
    private EditText peso;
    private Button btnSalvar;
    private Button btnVoltar;
    private UsuarioDAO usuarioDAO;

    private RadioGroup mapGroup;
    private RadioGroup navGroup;

    private static final String PREFS = "app_config";

    @Override
    protected void onCreate(Bundle savedInstanceState) {     // Método "onCreate" para carregar o layout de Configuração
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracao);

        //Inicializa os componenetes do layout
        nomeUsuario = findViewById(R.id.nomeUsuario);
        dataNascimento = findViewById(R.id.data_nascimento);
        sexo = findViewById(R.id.sexo);
        altura = findViewById(R.id.altura);
        peso = findViewById(R.id.pesoUsuario);
        btnSalvar = findViewById(R.id.btnSalvar);
        btnVoltar = findViewById(R.id.btnVoltar);

        mapGroup = findViewById(R.id.map_group);
        navGroup = findViewById(R.id.nav_group);

        usuarioDAO = new UsuarioDAO(this); //Inicializa o DAO do usuário pra uso do banco de Dados


        carregarUsuarioSalvo(); // Carrega os dados do usuário
        carregarPreferencias(); // Carrega os dados salvos no SharedPreferences

        btnSalvar.setOnClickListener(v -> salvar());
        btnVoltar.setOnClickListener(v -> finish());
    }

    private void carregarPreferencias() { // Método que define o SharedPreferences para persistência de dados das opções de Satélite


        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        String mapType = prefs.getString("mapType", "vetorial");
        String navMode = prefs.getString("navMode", "north_up");

        if ("satellite".equals(mapType)) {
            mapGroup.check(R.id.map_satelite);
        } else {
            mapGroup.check(R.id.map_default);
        }

        if ("course_up".equals(navMode)) {
            navGroup.check(R.id.nav_course_up);
        } else {
            navGroup.check(R.id.nav_north_up);
        }
    }

    private void carregarUsuarioSalvo() {// Busca os dados do usuário no "UsuárioDAO", caso existam, preenche os campos do formulário
        Usuario u = usuarioDAO.buscarPrimeiroUsuario();

        if (u == null) return;

        nomeUsuario.setText(u.getNome());
        dataNascimento.setText(u.getDataNascimento());


        if (u.getSexo().equalsIgnoreCase("Masculino")) {
            sexo.check(R.id.sexo_masculino);
        } else {
            sexo.check(R.id.sexo_feminino);
        }

        altura.setText(String.valueOf(u.getAltura()));
        peso.setText(String.valueOf(u.getPeso()));
    }

    private void salvar() { // Salva os dados do usuário e retorna uma mensagem de erro caso algum input obrigatório fique vazio
        int idSexo = sexo.getCheckedRadioButtonId();

        if (nomeUsuario.getText().toString().trim().isEmpty()) {
            nomeUsuario.setError(getString(R.string.informe_nome));
            return;
        }

        if (dataNascimento.getText().toString().trim().isEmpty()) {
            dataNascimento.setError(getString(R.string.informe_data_nascimento));
            return;
        }

        if (idSexo == -1) {
            Toast.makeText(this, getString(R.string.selecionar_sexo), Toast.LENGTH_SHORT).show();
            return;
        }

        if (altura.getText().toString().trim().isEmpty() ||
                peso.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, getString(R.string.informe_altura_peso), Toast.LENGTH_SHORT).show();
            return;
        }
        // Obtém o dado do sexo do usuário
        RadioButton rb = findViewById(idSexo);
        String sexoStr = rb.getText().toString();

        // Converse o valor da altura e peso em double
        double alturaVal = Double.parseDouble(altura.getText().toString());
        double pesoVal = Double.parseDouble(peso.getText().toString());

        Usuario usuario = new Usuario(); // Cria o novo usuário com seus dados pessoais associando-os ao ID
        usuario.setNome(nomeUsuario.getText().toString());
        usuario.setDataNascimento(dataNascimento.getText().toString());
        usuario.setSexo(sexoStr);
        usuario.setAltura(alturaVal);
        usuario.setPeso(pesoVal);

        usuarioDAO.salvarUsuario(usuario);


        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        SharedPreferences.Editor ed = prefs.edit();

        // Salva o tipo de mapa
        int mapId = mapGroup.getCheckedRadioButtonId();
        if (mapId == R.id.map_satelite) ed.putString("mapType", "satellite");
        else ed.putString("mapType", "vetorial");

        // Salva o tipo de navegação
        int navId = navGroup.getCheckedRadioButtonId();
        if (navId == R.id.nav_course_up) ed.putString("navMode", "course_up");
        else ed.putString("navMode", "north_up");

        ed.putFloat("peso", (float) pesoVal);
        ed.putFloat("altura", (float) alturaVal);
        ed.putString("sexo", sexoStr);
        ed.putString("dataNascimento", dataNascimento.getText().toString());

        ed.apply();

        Toast.makeText(this, getString(R.string.configuracoes_salvas), Toast.LENGTH_SHORT).show();
        finish();
    }
}
