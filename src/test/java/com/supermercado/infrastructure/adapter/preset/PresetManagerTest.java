package com.supermercado.infrastructure.adapter.preset;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class PresetManagerTest {

    @TempDir
    Path tempDir;

    @Test
    void guardarYCargar_devuelveLosMismosValores() throws IOException {
        File archivo = tempDir.resolve("test.properties").toFile();
        Properties original = new Properties();
        original.setProperty("modo", "CALIBRADO");
        original.setProperty("maxSociosDia", "519");

        PresetManager.guardar(archivo, original);

        Properties cargado = PresetManager.cargar(archivo);
        assertEquals("CALIBRADO", cargado.getProperty("modo"));
        assertEquals("519", cargado.getProperty("maxSociosDia"));
    }

    @Test
    void guardar_creaLaCarpetaSiNoExiste() throws IOException {
        File archivo = tempDir.resolve("subcarpeta/nested.properties").toFile();
        Properties p = new Properties();
        p.setProperty("x", "1");
        PresetManager.guardar(archivo, p);
        assertTrue(archivo.exists());
    }

    @Test
    void cargarArchivoInexistente_lanzaIOException() {
        File inexistente = tempDir.resolve("noexiste.properties").toFile();
        assertThrows(IOException.class, () -> PresetManager.cargar(inexistente));
    }
}