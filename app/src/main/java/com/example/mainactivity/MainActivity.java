package com.example.mainactivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) { // Método Oncreate para inicialização de componentes e carregamento do layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializa os campos/componentes da interface
        Button creditosBtn = findViewById(R.id.creditosBtn);
        Button registrarTrilhaBtn = findViewById(R.id.registrarTrilhaBtn);
        Button configuracaoBtn = findViewById(R.id.configuracaoBtn);
        Button visualizarTrilhaBtn = findViewById(R.id.visualizarTrilhaBtn);

        creditosBtn.setOnClickListener(new View.OnClickListener() { // Define o Listener para acessar a tela de Créditos
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), CreditosActivity.class));
            }
        });

        registrarTrilhaBtn.setOnClickListener(new View.OnClickListener() {// Define o Listener para acessar a tela de RegistrarTrilha
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), RegistrarTrilhaActivity.class));
            }
        });

        configuracaoBtn.setOnClickListener(new View.OnClickListener() { // Define o Listener para acessar a tela de Configuração
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ConfiguracaoActivity.class));
            }
        });

        visualizarTrilhaBtn.setOnClickListener(new View.OnClickListener() { // Define o Listener para acessar a tela de VisualizarTrilha
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ConsultarTrilha.class));
            }
        });
    }


}