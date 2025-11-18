package com.example.mainactivity.model;

public class PontoTrilha {
    private double latitude;
    private double longitude;
    private double velocidade;
    private double acuracia;
    private String dataHora;

    public PontoTrilha(double lat, double lng, double vel, double acuracia, String dataHora) {
        this.latitude = lat;
        this.longitude = lng;
        this.velocidade = vel;
        this.acuracia = acuracia;
        this.dataHora = dataHora;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getVelocidade() {
        return velocidade;
    }

    public void setVelocidade(double velocidade) {
        this.velocidade = velocidade;
    }

    public double getAcuracia() {
        return acuracia;
    }

    public void setAcuracia(double acuracia) {
        this.acuracia = acuracia;
    }

    public String getDataHora() {
        return dataHora;
    }

    public void setDataHora(String dataHora) {
        this.dataHora = dataHora;
    }
}

