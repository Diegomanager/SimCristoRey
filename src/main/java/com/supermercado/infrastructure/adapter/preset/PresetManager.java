package com.supermercado.infrastructure.adapter.preset;

import java.io.*;
import java.util.Properties;

public class PresetManager {

    public static void guardar(File archivo, Properties props) throws IOException {
        File dir = archivo.getParentFile();
        if (dir != null && !dir.exists()) dir.mkdirs();
        try (OutputStream os = new FileOutputStream(archivo)) {
            props.store(os, "SimCristoRey - Preset de configuracion");
        }
    }

    public static Properties cargar(File archivo) throws IOException {
        Properties props = new Properties();
        try (InputStream is = new FileInputStream(archivo)) {
            props.load(is);
        }
        return props;
    }
}