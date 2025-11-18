package com.example.mainactivity;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DataUtil {

    public static String getDataAtual() {
        return new SimpleDateFormat("dd/MM/yyyy").format(new Date());
    }

    public static String getHoraAtual() {
        return new SimpleDateFormat("HH:mm:ss").format(new Date());
    }

    public static String getDataHoraAtual() {
        return new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());
    }
}
