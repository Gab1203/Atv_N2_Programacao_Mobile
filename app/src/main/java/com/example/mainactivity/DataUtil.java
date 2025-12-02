package com.example.mainactivity;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DataUtil {

    public static String getDataAtual() { // Faz return com data atual no formato padrão de "dd/MM/yyyy"
        return new SimpleDateFormat("dd/MM/yyyy").format(new Date());
    }

    public static String getHoraAtual() { // Faz return com hora atual formatada no padrão "HH:mm:ss"
        return new SimpleDateFormat("HH:mm:ss").format(new Date());
    }

    public static String getDataHoraAtual() {
        /**
         * data e hora atuais formatadas no padrão dd/MM/yyyy HH:mm:ss para
         * regustrar data e hora do começo e fim das trilhas
         */
        return new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());
    }
}
