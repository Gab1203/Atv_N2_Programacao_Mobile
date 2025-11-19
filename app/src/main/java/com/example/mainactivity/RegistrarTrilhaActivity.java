package com.example.mainactivity;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;

import com.example.mainactivity.dao.PontoTrilhaDAO;
import com.example.mainactivity.dao.TrilhaDAO;
import com.example.mainactivity.databinding.ActivityRegistrarTrilhaBinding;
import com.example.mainactivity.model.PontoTrilha;

import com.example.mainactivity.model.Trilha;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;

import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

public class RegistrarTrilhaActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private FusedLocationProviderClient locationClient;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;

    private Trilha trilha;
    private TrilhaDAO trilhaDAO;
    private PontoTrilhaDAO pontoDAO;

    private Polyline polyline;

    private static final int PERMISSION_REQUEST = 10;

    // UI
    private TextView txtVelocidade;
    private TextView txtVelocidadeMax;
    private TextView txtDistancia;
    private TextView txtTempo;
    private TextView txtKcal;
    private Circle accuracyCircle;
    private long startTime;
    private float distanciaTotal = 0f;
    private float velocidadeMax = 0f;
    private Location ultimaLocation = null;

    Button btnIniciar;
    Button btnFinalizar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityRegistrarTrilhaBinding binding =
                ActivityRegistrarTrilhaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        txtVelocidade = binding.txtVelocidade;
        txtVelocidadeMax = binding.txtVelocidadeMax;
        txtDistancia = binding.txtDistancia;
        txtTempo = binding.txtTempo;
        txtKcal = binding.txtKcal;

         btnIniciar = findViewById(R.id.Startbutton);
         btnFinalizar = findViewById(R.id.Stopbutton);

        trilhaDAO = new TrilhaDAO(this);
        pontoDAO = new PontoTrilhaDAO(this);

        locationClient = LocationServices.getFusedLocationProviderClient(this);

        //inicializa o mapa
        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        btnIniciar.setOnClickListener(v -> iniciarTrilha());
        btnFinalizar.setOnClickListener(v -> finalizarTrilha());
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        polyline = mMap.addPolyline(new PolylineOptions()
                .color(Color.BLUE)
                .width(10f));
    }

    private void iniciarTrilha() {
        btnIniciar.setEnabled(false);
        btnFinalizar.setEnabled(true);

        trilha = new Trilha();
        trilha.setNome("Trilha " + System.currentTimeMillis());
        trilha.setDataInicio(DataUtil.getDataAtual());
        trilha.setHoraInicio(DataUtil.getHoraAtual());

        long id = trilhaDAO.inserirTrilha(trilha);
        trilha.setId(id);

        Toast.makeText(this, "Trilha iniciada!", Toast.LENGTH_SHORT).show();

        startTime = System.currentTimeMillis();

        iniciarLocalizacao();
    }

    //aq ele solicita a localização
    private void iniciarLocalizacao() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST
            );
            return;
        }

        locationRequest = new LocationRequest.Builder(1000)
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .build();

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult result) {
                Location loc = result.getLastLocation();
                if (loc != null) processarLocalizacao(loc);
            }
        };

        locationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    private void processarLocalizacao(Location loc) {

        LatLng ponto = new LatLng(loc.getLatitude(), loc.getLongitude());

        // desenhar no mapa
        List<LatLng> pts = polyline.getPoints();
        pts.add(ponto);
        polyline.setPoints(pts);

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ponto, 17));

        // círculo de acurácia
        if (accuracyCircle == null) {
            accuracyCircle = mMap.addCircle(new CircleOptions()
                    .strokeColor(Color.RED)
                    .radius(loc.getAccuracy())
                    .center(ponto));
        } else {
            accuracyCircle.setCenter(ponto);
            accuracyCircle.setRadius(loc.getAccuracy());
        }


        // calculo distancia
        if (ultimaLocation != null) {
            distanciaTotal += ultimaLocation.distanceTo(loc);
        }
        ultimaLocation = loc;

        //aq é m/s
        float velocidade = loc.getSpeed();
        float velocidadeKmH = velocidade * 3.6f;

        if (velocidadeKmH > velocidadeMax)
            velocidadeMax = velocidadeKmH;

        double kcal = distanciaTotal * 0.06;

        pontoDAO.inserirPonto(trilha.getId(),
                new PontoTrilha(
                        loc.getLatitude(),
                        loc.getLongitude(),
                        velocidadeKmH,
                        loc.getAccuracy(),
                        DataUtil.getDataHoraAtual()
                )
        );

        txtVelocidade.setText(String.format("%.1f km/h", velocidadeKmH));
        txtVelocidadeMax.setText(String.format("%.1f km/h", velocidadeMax));
        txtDistancia.setText(String.format("%.1f m", distanciaTotal));
        txtKcal.setText(String.format("%.1f kcal", kcal));

        long tempoSegundos = (System.currentTimeMillis() - startTime) / 1000;
        txtTempo.setText(formatarTempo(tempoSegundos));
    }

    private void finalizarTrilha() {

        trilha.setDataFim(DataUtil.getDataAtual());
        trilha.setHoraFim(DataUtil.getHoraAtual());
        trilha.setDistanciaPercorrida(distanciaTotal);
        trilha.setVelocidadeMaxima(velocidadeMax);
        trilha.setVelocidadeMedia(distanciaTotal / ((System.currentTimeMillis() - startTime) / 1000f));
        trilha.setGastoKcal(distanciaTotal * 0.06);

        trilhaDAO.atualizarTrilha(trilha);

        locationClient.removeLocationUpdates(locationCallback);

        Toast.makeText(this, "Trilha finalizada e salva!", Toast.LENGTH_LONG).show();

        finish();
    }

    private String formatarTempo(long s) {
        long h = s / 3600;
        long m = (s % 3600) / 60;
        long sec = s % 60;
        return String.format("%02d:%02d:%02d", h, m, sec);
    }
}
