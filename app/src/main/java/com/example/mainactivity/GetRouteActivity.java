package com.example.mainactivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.mainactivity.db.TrilhasDB;
import com.example.mainactivity.model.Waypoint;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;

public class GetRouteActivity extends AppCompatActivity {
    // Request Code para ser utilizado no gerenciamento das permissões
    private static final int REQUEST_LOCATION_UPDATES = 1;

    // Objetos da API de localização
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;

    // Banco de dados
    TrilhasDB trilhadb;

    int waypoint_counter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_route);

        Button startbutton=(Button) findViewById(R.id.Startbutton);
        Button stopbutton=(Button) findViewById(R.id.Stopbutton);
        startbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // instancia/abre o banco de dados
                trilhadb=new TrilhasDB(GetRouteActivity.this);
                trilhadb.apagaTrilha();
                startLocationUpdates();
                waypoint_counter=0;
            }
        });
        stopbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopLocationUpdates();
                trilhadb.close();
            }
        });
    }
    public void startLocationUpdates() {
        // 2. Verifica a permissão para obtenção da localização
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            // A permissão foi dada– OK vá em frente
            // 3. Cria o cliente (FusedLocationProviderClient)
            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
            // 4. Configura a solicitação de localizações (LocationRequest)
            mLocationRequest = new LocationRequest.Builder(1*1000).build();
            // 5. Programa o escutador para consumir as novas localizações geradas (LocationCallback)
            mLocationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    Location location=locationResult.getLastLocation();
                    addWayPoint(location);
                } };
            // 6. Manda o cliente começar a gerar atualizações de localização.
            mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest,mLocationCallback,null);

        } else {
            // A permissão ainda não foi dada - Solicite a permissão
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_UPDATES);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_UPDATES) {
            if(grantResults.length == 1 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED) {
                // O usuário acabou de dar a permissão
                startLocationUpdates();
            }
            else {
                // O usuário não deu a permissão solicitada
                Toast.makeText(this,"Sem permissão para registrar sua localização",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
    public void addWayPoint(Location location) {
        Waypoint waypoint=new Waypoint(location);
        trilhadb.registrarWaypoint(waypoint);
        TextView logTextView=(TextView)findViewById(R.id.logTextView);
        waypoint_counter++;
        logTextView.setText("Adicionado("+waypoint_counter+"):"+waypoint.getLatitude()+","+waypoint.getLongitude());
    }
    public void stopLocationUpdates() {
        if (mFusedLocationProviderClient!=null)
            mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);

        TextView logTextView=(TextView)findViewById(R.id.logTextView);
        ArrayList<Waypoint> waypoints=trilhadb.recuperarWaypoints();
        String log="";
        for (int i=0;i<waypoints.size();i++) {
            log+="("+(i+1)+")"+waypoints.get(i).getLatitude()+","+waypoints.get(i).getLongitude()+"\n";
        }
        logTextView.setText(log);
    }
}