package com.example.mainactivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.SystemClock;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.mainactivity.dao.PontoTrilhaDAO;
import com.example.mainactivity.dao.TrilhaDAO;
import com.example.mainactivity.dao.UsuarioDAO;
import com.example.mainactivity.model.PontoTrilha;
import com.example.mainactivity.model.Trilha;
import com.example.mainactivity.model.Usuario;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.CameraPosition;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RegistrarTrilhaActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int REQ_LOCATION = 100;

    private GoogleMap mMap;
    private FusedLocationProviderClient locationClient;
    private LocationCallback locationCallback;

    private TextView txtVelocidade, txtDistancia, txtTempo, txtVelocidadeMax, txtKcal;
    private Button btnIniciar, btnFinalizar;

    private boolean trilhaIniciada = false;

    private long trilhaId;
    private Trilha trilha;
    private TrilhaDAO trilhaDAO;
    private PontoTrilhaDAO pontoDAO;
    private Usuario usuario;

    private Location ultimaLocation;
    private double distanciaTotal = 0;
    private double velocidadeMax = 0;

    private long tempoInicio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_trilha);

        trilhaDAO = new TrilhaDAO(this);
        pontoDAO = new PontoTrilhaDAO(this);

        UsuarioDAO usuarioDAO = new UsuarioDAO(this);
        usuario = usuarioDAO.buscarPrimeiroUsuario();

        txtVelocidade = findViewById(R.id.txtVelocidade);
        txtDistancia = findViewById(R.id.txtDistancia);
        txtTempo = findViewById(R.id.txtTempo);
        txtVelocidadeMax = findViewById(R.id.txtVelocidadeMax);
        txtKcal = findViewById(R.id.txtKcal);

        btnIniciar = findViewById(R.id.Startbutton);
        btnFinalizar = findViewById(R.id.Stopbutton);

        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        btnIniciar.setOnClickListener(v -> iniciarTrilha());
        btnFinalizar.setOnClickListener(v -> finalizarTrilha());

        locationClient = LocationServices.getFusedLocationProviderClient(this);
    }

       private void iniciarTrilha() {
        if (usuario == null) {
            Toast.makeText(this, "Configure o usuário primeiro.", Toast.LENGTH_LONG).show();
            return;
        }

        trilha = new Trilha();
        trilha.setNome("Trilha " + new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date()));
        trilha.setDataInicio(dataAtual());
        trilha.setHoraInicio(horaAtual());

        trilhaId = trilhaDAO.inserirTrilha(trilha);

        trilha.setId(trilhaId);

        trilhaIniciada = true;
        tempoInicio = SystemClock.elapsedRealtime();

        iniciarLocalizacao();

        Toast.makeText(this, "Trilha iniciada!", Toast.LENGTH_SHORT).show();
    }

    private void finalizarTrilha() {
        if (!trilhaIniciada) return;

        trilhaIniciada = false;

        trilha.setDataFim(dataAtual());
        trilha.setHoraFim(horaAtual());
        trilha.setDistanciaPercorrida(distanciaTotal);
        trilha.setVelocidadeMaxima(velocidadeMax);

        long timeElapsed = SystemClock.elapsedRealtime() - tempoInicio;
        double horas = timeElapsed / 3600000.0;
        double vm = distanciaTotal / horas;
        trilha.setVelocidadeMedia(vm);

        double kcal = calcularGastoCalorico(distanciaTotal, usuario.getPeso());
        trilha.setGastoKcal(kcal);

        trilhaDAO.atualizarTrilha(trilha);

        Toast.makeText(this, "Trilha finalizada e salva!", Toast.LENGTH_LONG).show();
        finish();
    }


    private void iniciarLocalizacao() {
        if (!temPermissao()) {
            pedirPermissao();
            return;
        }

        LocationRequest req = LocationRequest.create();
        req.setInterval(1000);
        req.setFastestInterval(500);
        req.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult result) {
                for (Location loc : result.getLocations()) {
                    atualizarLocalizacao(loc);
                }
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationClient.requestLocationUpdates(req, locationCallback, getMainLooper());
    }

    private void atualizarLocalizacao(Location loc) {
        if (loc == null || !trilhaIniciada) return;

    LatLng pos = new LatLng(loc.getLatitude(), loc.getLongitude());

     SharedPreferences prefs = getSharedPreferences("app_config", MODE_PRIVATE);
    String navMode = prefs.getString("navMode", "north_up");
    float zoom = 18f;
    if ("course_up".equals(navMode) && ultimaLocation != null) {
        float bearing = ultimaLocation.bearingTo(loc);
        CameraPosition camPos = new CameraPosition.Builder()
            .target(pos)
            .zoom(zoom)
            .bearing(bearing)
            .tilt(0)
            .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(camPos));
    } else {
        CameraPosition camPos = new CameraPosition.Builder()
            .target(pos)
            .zoom(zoom)
            .bearing(0)
            .tilt(0)
            .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(camPos));
    }

        if (ultimaLocation != null) {
            float d = ultimaLocation.distanceTo(loc); // metros
            distanciaTotal += d;

            double v = loc.getSpeed() * 3.6; // km/h
            velocidadeMax = Math.max(velocidadeMax, v);

            txtVelocidade.setText(String.format(Locale.getDefault(), "%.1f km/h", v));
            txtDistancia.setText(String.format(Locale.getDefault(), "%.2f m", distanciaTotal));
            txtVelocidadeMax.setText(String.format(Locale.getDefault(), "%.1f km/h", velocidadeMax));

            long t = SystemClock.elapsedRealtime() - tempoInicio;
            txtTempo.setText(formatarTempo(t));

            double kcal = calcularGastoCalorico(distanciaTotal, usuario.getPeso());
            txtKcal.setText(String.format(Locale.getDefault(), "%.1f kcal", kcal));
        }

        mMap.addPolyline(
                new PolylineOptions().add(
                        ultimaLocation == null ?
                                pos :
                                new LatLng(ultimaLocation.getLatitude(), ultimaLocation.getLongitude()),
                        pos
                ).width(8)
        );

        mMap.addCircle(
                new CircleOptions()
                        .center(pos)
                        .radius(loc.getAccuracy())
                        .strokeWidth(2f)
        );

        pontoDAO.inserirPonto(trilhaId, new PontoTrilha(
                loc.getLatitude(),
                loc.getLongitude(),
                loc.getSpeed(),
                loc.getAccuracy(),
                horaAtual()
        ));

        ultimaLocation = loc;
    }

    private String dataAtual() {
        return new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
    }

    private String horaAtual() {
        return new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
    }

    private String formatarTempo(long ms) {
        int s = (int) (ms / 1000) % 60;
        int m = (int) (ms / 60000) % 60;
        int h = (int) (ms / 3600000);

        return String.format(Locale.getDefault(), "%02d:%02d:%02d", h, m, s);
    }

    private double calcularGastoCalorico(double distanciaMetros, double pesoKg) {
        double distanciaKm = distanciaMetros / 1000.0;
        return distanciaKm * pesoKg * 0.7;
    }

       private boolean temPermissao() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void pedirPermissao() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                REQ_LOCATION
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] p, @NonNull int[] g) {
        super.onRequestPermissionsResult(requestCode, p, g);
        if (requestCode == REQ_LOCATION && temPermissao()) {
            iniciarLocalizacao();
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        SharedPreferences prefs = getSharedPreferences("app_config", MODE_PRIVATE);
        String mapType = prefs.getString("mapType", "vetorial");
        if ("satellite".equals(mapType)) {
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        } else {
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }
    }
}
