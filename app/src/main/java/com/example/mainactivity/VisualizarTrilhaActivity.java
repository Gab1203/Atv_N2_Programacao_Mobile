package com.example.mainactivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mainactivity.dao.PontoTrilhaDAO;
import com.example.mainactivity.dao.TrilhaDAO;
import com.example.mainactivity.model.PontoTrilha;
import com.example.mainactivity.model.Trilha;
import com.google.android.gms.maps.CameraUpdateFactory;
import android.content.SharedPreferences;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;
import java.util.Locale;

public class VisualizarTrilhaActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap map;
    private TrilhaDAO trilhaDAO;
    private PontoTrilhaDAO pontoDAO;

    private Trilha trilhaSelecionada;
    private List<PontoTrilha> pontos;

    private TextView tvNome, tvData, tvDistancia, tvDuracao, tvVelocidade, tvVelocidadeMax;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizar_trilha);

        trilhaDAO = new TrilhaDAO(this);
        pontoDAO = new PontoTrilhaDAO(this);

        tvNome = findViewById(R.id.tvNomeTrilha);
        tvData = findViewById(R.id.tvDataTrilha);
        tvDistancia = findViewById(R.id.tvDistancia);
        tvDuracao = findViewById(R.id.tvDuracao);
        tvVelocidade = findViewById(R.id.tvVelocidadeMedia);
        tvVelocidadeMax = findViewById(R.id.tvVelocidadeMaxima);

        long trilhaId = getIntent().getLongExtra("trilha_id", -1);

        trilhaSelecionada = trilhaDAO.buscarPorId(trilhaId);
        pontos = pontoDAO.listarPontos(trilhaId);

        preencherInfoTrilha();

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapTrilha);

        if (mapFragment != null)
            mapFragment.getMapAsync(this);
    }

    private void preencherInfoTrilha() {
        if (trilhaSelecionada == null) return;

        tvNome.setText(trilhaSelecionada.getNome());

        tvData.setText(
                "Início: " + trilhaSelecionada.getDataInicio() +
                        " " + trilhaSelecionada.getHoraInicio()
        );

        tvDistancia.setText("Distância: " +  String.format(Locale.getDefault(),
                "%.2f m", trilhaSelecionada.getDistanciaPercorrida()));

        String duracao = trilhaSelecionada.getHoraInicio() + " → " + trilhaSelecionada.getHoraFim();
        tvDuracao.setText("Duração: " + duracao);

        tvVelocidade.setText("Velocidade média: " + String.format(Locale.getDefault(),
                "%.2f km/h", trilhaSelecionada.getVelocidadeMedia()));

        tvVelocidadeMax.setText("Velocidade máxima: " + String.format(Locale.getDefault(),
                "%.2f km/h", trilhaSelecionada.getVelocidadeMaxima()));
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.map = googleMap;

        SharedPreferences prefs = getSharedPreferences("app_config", MODE_PRIVATE);
        String mapType = prefs.getString("mapType", "vetorial");
        if ("satellite".equals(mapType)) {
            map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        } else {
            map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }

        if (pontos == null || pontos.isEmpty()) return;

        PolylineOptions poly = new PolylineOptions();

        for (PontoTrilha p : pontos) {
            LatLng pos = new LatLng(p.getLatitude(), p.getLongitude());
            poly.add(pos);
        }

        map.addPolyline(poly);

        LatLng inicio = new LatLng(pontos.get(0).getLatitude(), pontos.get(0).getLongitude());
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(inicio, 17f));
    }
}
