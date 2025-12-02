package com.example.mainactivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mainactivity.dao.TrilhaDAO;
import com.example.mainactivity.model.Trilha;

import java.util.Calendar;

public class EditarTrilhaActivity extends AppCompatActivity {

    private EditText edtNome, edtData, edtHora; // Váraveis dos campos para edição
    private Button btnSalvar;

    // DAO para operações no banco de dados e objeto da trilha sendo editada
    private TrilhaDAO trilhaDAO;
    private Trilha trilha;

    @Override
    protected void onCreate(Bundle savedInstanceState) { // Método OnCreate para carregar layout e inicializar compontentes
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_trilha);


        trilhaDAO = new TrilhaDAO(this); // Inicializa o DAO para modificações com o bando de dados

        // Referencia os campos carregados do layout
        edtNome = findViewById(R.id.edtNomeTrilha);
        edtData = findViewById(R.id.edtDataTrilha);
        edtHora = findViewById(R.id.edtHoraTrilha);

        btnSalvar = findViewById(R.id.btnSalvarEdicao);

        long trilhaId = getIntent().getLongExtra("trilha_id", -1);
        // Busca a trilha no banco de dados pelo ID
        trilha = trilhaDAO.buscarPorId(trilhaId);

        // Verifica se a trilha foi encontrada
        if (trilha == null) {
            Toast.makeText(this, getString(R.string.erro_carregar_trilha), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Preenche os campos com os dados atuais da trilha
        preencherCampos();

        // Configura listeners para os seletores de data e hora
        edtData.setOnClickListener(v -> escolherData());
        edtHora.setOnClickListener(v -> escolherHora());

        // Configura listener do botão salvar
        btnSalvar.setOnClickListener(v -> salvarAlteracoes());
    }

    private void preencherCampos() { // Preenche os campos do layout com os dados atuais da trilha
        edtNome.setText(trilha.getNome());
        edtData.setText(trilha.getDataInicio());
        edtHora.setText(trilha.getHoraInicio());
    }

    private void escolherData() { // Dialog para data de ínicio da trilha
        Calendar c = Calendar.getInstance();

        DatePickerDialog dp = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    String data = year + "-" + (month + 1) + "-" + dayOfMonth;
                    edtData.setText(data);
                },
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH)
        );
        dp.show();
    }

    private void escolherHora() { // DatePickerDialog para seleção da data de início da trilha
        Calendar c = Calendar.getInstance();

        TimePickerDialog tp = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    String hora = String.format("%02d:%02d", hourOfDay, minute);
                    edtHora.setText(hora);
                },
                c.get(Calendar.HOUR_OF_DAY),
                c.get(Calendar.MINUTE),
                true
        );
        tp.show();
    }

    private void salvarAlteracoes() { // Salva as alterações
        String nome = edtNome.getText().toString().trim();
        String data = edtData.getText().toString().trim();
        String hora = edtHora.getText().toString().trim();

        if (nome.isEmpty()) { // Verifica se o campo "nome" está preenchido, caso não, retorna erro
            edtNome.setError(getString(R.string.informe_nome));
            return;
        }
        // Atualiza o objeto trilha com os novos valores
        trilha.setNome(nome);
        trilha.setDataInicio(data);
        trilha.setHoraInicio(hora);

        trilhaDAO.atualizarCamposIniciais(trilha);

        // Mensagem de feedback para o usuário e encerramento da atividade
        Toast.makeText(this, getString(R.string.trilha_atualizada), Toast.LENGTH_SHORT).show();
        finish();
    }
}
