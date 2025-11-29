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

    private EditText edtNome, edtData, edtHora;
    private Button btnSalvar;

    private TrilhaDAO trilhaDAO;
    private Trilha trilha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_trilha);

        trilhaDAO = new TrilhaDAO(this);

        edtNome = findViewById(R.id.edtNomeTrilha);
        edtData = findViewById(R.id.edtDataTrilha);
        edtHora = findViewById(R.id.edtHoraTrilha);

        btnSalvar = findViewById(R.id.btnSalvarEdicao);

        long trilhaId = getIntent().getLongExtra("trilha_id", -1);
        trilha = trilhaDAO.buscarPorId(trilhaId);

        if (trilha == null) {
            Toast.makeText(this, getString(R.string.erro_carregar_trilha), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        preencherCampos();

        edtData.setOnClickListener(v -> escolherData());
        edtHora.setOnClickListener(v -> escolherHora());

        btnSalvar.setOnClickListener(v -> salvarAlteracoes());
    }

    private void preencherCampos() {
        edtNome.setText(trilha.getNome());
        edtData.setText(trilha.getDataInicio());
        edtHora.setText(trilha.getHoraInicio());
    }

    private void escolherData() {
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

    private void escolherHora() {
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

    private void salvarAlteracoes() {
        String nome = edtNome.getText().toString().trim();
        String data = edtData.getText().toString().trim();
        String hora = edtHora.getText().toString().trim();

        if (nome.isEmpty()) {
            edtNome.setError(getString(R.string.informe_nome));
            return;
        }

        trilha.setNome(nome);
        trilha.setDataInicio(data);
        trilha.setHoraInicio(hora);

        trilhaDAO.atualizarCamposIniciais(trilha);

        Toast.makeText(this, getString(R.string.trilha_atualizada), Toast.LENGTH_SHORT).show();
        finish();
    }
}
