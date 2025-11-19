package com.example.mainactivity.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Trilha {
    private long id;
    private String nome;
    private String dataInicio;
    private String dataFim;
    private String horaInicio;
    private String horaFim;
    private double gastoKcal;
    private double distanciaPercorrida;
    private double velocidadeMedia;
    private double velocidadeMaxima;
    private List<PontoTrilha> pontos = new ArrayList<>();

    public String toJson() {
        try {
            JSONObject obj = new JSONObject();

            obj.put("id", id);
            obj.put("nome", nome);

            obj.put("dataInicio", dataInicio);
            obj.put("horaInicio", horaInicio);
            obj.put("dataFim", dataFim);
            obj.put("horaFim", horaFim);

            obj.put("distanciaPercorrida", distanciaPercorrida);
            obj.put("velocidadeMedia", velocidadeMedia);
            obj.put("velocidadeMaxima", velocidadeMaxima);
            obj.put("gastoKcal", gastoKcal);

            //todoa os pontos percorridos
            JSONArray pontosArray = new JSONArray();
            for (PontoTrilha p : pontos) {
                JSONObject pObj = new JSONObject();
                pObj.put("latitude", p.getLatitude());
                pObj.put("longitude", p.getLongitude());
                pObj.put("velocidade", p.getVelocidade());
                pObj.put("acuracia", p.getAcuracia());
                pObj.put("dataHora", p.getDataHora());

                pontosArray.put(pObj);
            }

            obj.put("pontos", pontosArray);

            return obj.toString(4);
        } catch (Exception e) {
            return "{}";
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(String dataInicio) {
        this.dataInicio = dataInicio;
    }

    public String getDataFim() {
        return dataFim;
    }

    public void setDataFim(String dataFim) {
        this.dataFim = dataFim;
    }

    public String getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(String horaInicio) {
        this.horaInicio = horaInicio;
    }

    public String getHoraFim() {
        return horaFim;
    }

    public void setHoraFim(String horaFim) {
        this.horaFim = horaFim;
    }

    public double getGastoKcal() {
        return gastoKcal;
    }

    public void setGastoKcal(double gastoKcal) {
        this.gastoKcal = gastoKcal;
    }

    public double getDistanciaPercorrida() {
        return distanciaPercorrida;
    }

    public void setDistanciaPercorrida(double distanciaPercorrida) {
        this.distanciaPercorrida = distanciaPercorrida;
    }

    public double getVelocidadeMedia() {
        return velocidadeMedia;
    }

    public void setVelocidadeMedia(double velocidadeMedia) {
        this.velocidadeMedia = velocidadeMedia;
    }

    public double getVelocidadeMaxima() {
        return velocidadeMaxima;
    }

    public void setVelocidadeMaxima(double velocidadeMaxima) {
        this.velocidadeMaxima = velocidadeMaxima;
    }

    public List<PontoTrilha> getPontos() {
        return pontos;
    }

    public void setPontos(List<PontoTrilha> pontos) {
        this.pontos = pontos;
    }
}

