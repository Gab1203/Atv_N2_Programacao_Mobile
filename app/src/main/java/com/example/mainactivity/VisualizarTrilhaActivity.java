package com.example.mainactivity;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mainactivity.dao.PontoTrilhaDAO;
import com.example.mainactivity.dao.TrilhaDAO;
import com.example.mainactivity.model.PontoTrilha;
import com.example.mainactivity.model.Trilha;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

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

        tvDistancia.setText("Distância: " + trilhaSelecionada.getDistanciaPercorrida() + " m");

        String duracao = trilhaSelecionada.getHoraInicio() + " → " + trilhaSelecionada.getHoraFim();
        tvDuracao.setText("Duração: " + duracao);

        tvVelocidade.setText("Velocidade média: " + trilhaSelecionada.getVelocidadeMedia() + " km/h");

        tvVelocidadeMax.setText("Velocidade máxima: " + trilhaSelecionada.getVelocidadeMaxima() + " km/h");
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.map = googleMap;

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
