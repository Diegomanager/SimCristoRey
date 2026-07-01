package com.supermercado.infrastructure.repository;

import com.supermercado.application.supermercado.dto.ConfiguracionDTO;
import com.supermercado.application.supermercado.port.IConfiguracionRepositorio;
import com.supermercado.application.supermercado.port.ILogService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Implementacion del repositorio de configuracion usando archivo .properties.
 *
 * Responsabilidad unica: persistir y recuperar la configuracion
 * del simulador desde el sistema de archivos.
 *
 * No contiene logica de negocio ni validaciones de dominio.
 */
public class ConfiguracionRepositorioImpl implements IConfiguracionRepositorio {

    // ============================================================
    // Constantes - claves del archivo de propiedades
    // ============================================================
    private static final String ARCHIVO_CONFIG = 
        System.getProperty("user.dir") + File.separator + "config.properties";

    private static final String KEY_HORAS_SIMULADAS          = "tiempo.simulado.horas";
    private static final String KEY_DURACION_REAL            = "duracion.real.segundos";
    private static final String KEY_CAJAS_NORMALES           = "num.cajas.normales";
    private static final String KEY_CAJAS_RAPIDAS            = "num.cajas.rapidas";
    private static final String KEY_LIMITE_CLIENTE_RAPIDO    = "limite.cliente.rapido";
    private static final String KEY_PROB_LLEGADA             = "probabilidad.llegada.cliente";
    private static final String KEY_LIMITE_CLIENTES          = "limite.clientes";
    private static final String KEY_TIEMPO_NORMAL_MIN        = "tiempo.caja.normal.min";
    private static final String KEY_TIEMPO_NORMAL_MAX        = "tiempo.caja.normal.max";
    private static final String KEY_TIEMPO_RAPIDA_MIN        = "tiempo.caja.rapida.min";
    private static final String KEY_TIEMPO_RAPIDA_MAX        = "tiempo.caja.rapida.max";
    private static final String KEY_ARTICULOS_MIN            = "articulos.cliente.min";
    private static final String KEY_ARTICULOS_MAX            = "articulos.cliente.max";
    private static final String KEY_MOSTRAR_DETALLE          = "mostrar.detalle.clientes";
    private static final String KEY_MOSTRAR_ESTADISTICAS     = "mostrar.estadisticas.avanzadas";

    private final ILogService logService;

    public ConfiguracionRepositorioImpl(ILogService logService) {
        this.logService = logService;
    }

    // ============================================================
    // Cargar configuracion
    // ============================================================

    @Override
    public ConfiguracionDTO cargar() {
        Properties props = leerArchivo();
        return construirDTO(props);
    }

    private Properties leerArchivo() {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(ARCHIVO_CONFIG)) {
            props.load(fis);
            logService.info("Configuracion cargada desde " + ARCHIVO_CONFIG);
        } catch (IOException e) {
            logService.warn("No se encontro " + ARCHIVO_CONFIG + ", usando valores por defecto");
        }
        return props;
    }

    private ConfiguracionDTO construirDTO(Properties props) {
        return new ConfiguracionDTO.Builder()
            .horasSimuladas(          getInt(props, KEY_HORAS_SIMULADAS,       12))
            .duracionRealSegundos(    getInt(props, KEY_DURACION_REAL,          20))
            .numCajasNormales(        getInt(props, KEY_CAJAS_NORMALES,          4))
            .numCajasRapidas(         getInt(props, KEY_CAJAS_RAPIDAS,           2))
            .limiteClienteRapido(     getInt(props, KEY_LIMITE_CLIENTE_RAPIDO,  10))
            .probabilidadLlegadaCliente(getInt(props, KEY_PROB_LLEGADA,         45))
            .limiteClientes(          getInt(props, KEY_LIMITE_CLIENTES,          0))
            .tiempoCajaNormalMin(     getInt(props, KEY_TIEMPO_NORMAL_MIN,        4))
            .tiempoCajaNormalMax(     getInt(props, KEY_TIEMPO_NORMAL_MAX,        9))
            .tiempoCajaRapidaMin(     getInt(props, KEY_TIEMPO_RAPIDA_MIN,        2))
            .tiempoCajaRapidaMax(     getInt(props, KEY_TIEMPO_RAPIDA_MAX,        5))
            .articulosClienteMin(     getInt(props, KEY_ARTICULOS_MIN,            1))
            .articulosClienteMax(     getInt(props, KEY_ARTICULOS_MAX,           50))
            .mostrarDetalleClientes(  getBoolean(props, KEY_MOSTRAR_DETALLE,   true))
            .mostrarEstadisticasAvanzadas(getBoolean(props, KEY_MOSTRAR_ESTADISTICAS, true))
            .build();
    }

    // ============================================================
    // Guardar configuracion
    // ============================================================

    @Override
    public void guardar(ConfiguracionDTO config) {
        Properties props = construirProperties(config);
        escribirArchivo(props);
    }

    private Properties construirProperties(ConfiguracionDTO config) {
        Properties props = new Properties();
        props.setProperty(KEY_HORAS_SIMULADAS,       String.valueOf(config.getHorasSimuladas()));
        props.setProperty(KEY_DURACION_REAL,         String.valueOf(config.getDuracionRealSegundos()));
        props.setProperty(KEY_CAJAS_NORMALES,        String.valueOf(config.getNumCajasNormales()));
        props.setProperty(KEY_CAJAS_RAPIDAS,         String.valueOf(config.getNumCajasRapidas()));
        props.setProperty(KEY_LIMITE_CLIENTE_RAPIDO, String.valueOf(config.getLimiteClienteRapido()));
        props.setProperty(KEY_PROB_LLEGADA,          String.valueOf(config.getProbabilidadLlegadaCliente()));
        props.setProperty(KEY_LIMITE_CLIENTES,       String.valueOf(config.getLimiteClientes()));
        props.setProperty(KEY_TIEMPO_NORMAL_MIN,     String.valueOf(config.getTiempoCajaNormalMin()));
        props.setProperty(KEY_TIEMPO_NORMAL_MAX,     String.valueOf(config.getTiempoCajaNormalMax()));
        props.setProperty(KEY_TIEMPO_RAPIDA_MIN,     String.valueOf(config.getTiempoCajaRapidaMin()));
        props.setProperty(KEY_TIEMPO_RAPIDA_MAX,     String.valueOf(config.getTiempoCajaRapidaMax()));
        props.setProperty(KEY_ARTICULOS_MIN,         String.valueOf(config.getArticulosClienteMin()));
        props.setProperty(KEY_ARTICULOS_MAX,         String.valueOf(config.getArticulosClienteMax()));
        props.setProperty(KEY_MOSTRAR_DETALLE,       String.valueOf(config.isMostrarDetalleClientes()));
        props.setProperty(KEY_MOSTRAR_ESTADISTICAS,  String.valueOf(config.isMostrarEstadisticasAvanzadas()));
        return props;
    }

    private void escribirArchivo(Properties props) {
        File file = new File(ARCHIVO_CONFIG);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            props.store(fos, "Configuracion del Simulador de Supermercado");
            logService.info("Configuracion guardada en " + file.getAbsolutePath());
        } catch (IOException e) {
            logService.error("Error al guardar configuracion en " + ARCHIVO_CONFIG, e);
        }
    }

    // ============================================================
    // Helpers de lectura de propiedades
    // ============================================================

    private int getInt(Properties props, String key, int defaultValue) {
        String value = props.getProperty(key);
        if (value == null) return defaultValue;
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            logService.warn("Valor invalido para '" + key + "': " + value + ". Usando default: " + defaultValue);
            return defaultValue;
        }
    }

    private boolean getBoolean(Properties props, String key, boolean defaultValue) {
        String value = props.getProperty(key);
        if (value == null) return defaultValue;
        return Boolean.parseBoolean(value.trim());
    }
}