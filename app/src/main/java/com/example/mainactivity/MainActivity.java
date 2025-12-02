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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button creditosBtn = findViewById(R.id.creditosBtn);
        Button registrarTrilhaBtn = findViewById(R.id.registrarTrilhaBtn);
        Button configuracaoBtn = findViewById(R.id.configuracaoBtn);
        Button visualizarTrilhaBtn = findViewById(R.id.visualizarTrilhaBtn);

        creditosBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), CreditosActivity.class));
            }
        });

        registrarTrilhaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), RegistrarTrilhaActivity.class));
            }
        });

        configuracaoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ConfiguracaoActivity.class));
            }
        });

        visualizarTrilhaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ConsultarTrilha.class));
            }
        });
    }


}