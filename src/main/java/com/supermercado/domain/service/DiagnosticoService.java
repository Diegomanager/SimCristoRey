package com.supermercado.domain.service;

import com.supermercado.domain.model.Caja;
import com.supermercado.domain.model.Recomendacion;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio de dominio: Diagnostico del estado de las cajas.
 *
 * Responsabilidad unica: analizar el estado actual de las cajas
 * y generar diagnosticos con niveles de alerta y sugerencias.
 *
 * No depende de infraestructura ni de la capa de presentacion.
 */
public class DiagnosticoService {

    // ============================================================
    // Enumeracion de niveles de alerta
    // ============================================================

    public enum NivelAlerta {
        NORMAL  ("", "Funcionando correctamente"),
        ATENCION("", "Monitorear"),
        ALERTA  ("", "Requiere atencion"),
        CRITICO ("", "URGENTE!");

        public final String icono;
        public final String descripcion;

        NivelAlerta(String icono, String descripcion) {
            this.icono       = icono;
            this.descripcion = descripcion;
        }
    }

    // ============================================================
    // Clase Diagnostico - campos privados con getters
    // ============================================================

    public static class Diagnostico {

        private final String      cajaId;
        private final NivelAlerta nivel;
        private final String      mensaje;
        private final String      sugerencia;
        private final int         colaActual;
        private final int         colaMaxima;
        private final int         clientesAtendidos;
        private final boolean     esRapida;

        public Diagnostico(
                String cajaId, NivelAlerta nivel,
                String mensaje, String sugerencia,
                int colaActual, int colaMaxima,
                int clientesAtendidos, boolean esRapida) {
            this.cajaId            = cajaId;
            this.nivel             = nivel;
            this.mensaje           = mensaje;
            this.sugerencia        = sugerencia;
            this.colaActual        = colaActual;
            this.colaMaxima        = colaMaxima;
            this.clientesAtendidos = clientesAtendidos;
            this.esRapida          = esRapida;
        }

        public String      getCajaId()            { return cajaId; }
        public NivelAlerta getNivel()              { return nivel; }
        public String      getMensaje()            { return mensaje; }
        public String      getSugerencia()         { return sugerencia; }
        public int         getColaActual()         { return colaActual; }
        public int         getColaMaxima()         { return colaMaxima; }
        public int         getClientesAtendidos()  { return clientesAtendidos; }
        public boolean     isEsRapida()            { return esRapida; }

        @Override
        public String toString() {
            return String.format("%s %s: %s (Cola: %d/%d)",
                nivel.icono, cajaId, mensaje, colaActual, colaMaxima);
        }
    }

    // ============================================================
    // Umbrales de alerta (constantes con nombre descriptivo)
    // ============================================================

    private static final int UMBRAL_CRITICO         = 40;
    private static final int UMBRAL_ALERTA           = 25;
    private static final int UMBRAL_SUBUTILIZACION   = 3;
    private static final int MIN_ATENDIDOS_PARA_EVAL = 50;

    // ============================================================
    // Diagnosticar
    // ============================================================

    public List<Diagnostico> diagnosticar(List<Caja> cajas) {
        List<Diagnostico> diagnosticos = new ArrayList<>();
        for (Caja caja : cajas) {
            diagnosticos.add(diagnosticarCaja(caja));
        }
        return diagnosticos;
    }

    private Diagnostico diagnosticarCaja(Caja caja) {
        int     colaActual  = caja.getClientesEnCola();
        int     colaMaxima  = caja.getColaMaxima();
        int     atendidos   = caja.getTotalAtendidos();
        boolean esRapida    = caja.esRapida();

        NivelAlerta nivel;
        String      mensaje;
        String      sugerencia;

        if (colaActual > UMBRAL_CRITICO) {
            nivel      = NivelAlerta.CRITICO;
            mensaje    = "COLA EXCESIVA: " + colaActual + " clientes esperando";
            sugerencia = esRapida
                ? "Convertir a NORMAL o abrir otra caja RAPIDA"
                : "Aumentar numero de cajas NORMALES o reducir tiempo de atencion";

        } else if (colaActual > UMBRAL_ALERTA) {
            nivel      = NivelAlerta.ALERTA;
            mensaje    = "Cola alta: " + colaActual + " clientes";
            sugerencia = "Considerar abrir otra caja o revisar tiempos";

        } else if (colaActual < UMBRAL_SUBUTILIZACION && atendidos > MIN_ATENDIDOS_PARA_EVAL) {
            nivel      = NivelAlerta.ATENCION;
            mensaje    = "Caja subutilizada: solo " + colaActual + " clientes en cola";
            sugerencia = esRapida
                ? "Redirigir mas clientes a esta caja RAPIDA"
                : "Considerar convertir a RAPIDA";

        } else {
            nivel      = NivelAlerta.NORMAL;
            mensaje    = "Funcionando correctamente";
            sugerencia = "Mantener configuracion actual";
        }

        return new Diagnostico(
            caja.getId(), nivel, mensaje, sugerencia,
            colaActual, colaMaxima, atendidos, esRapida
        );
    }

    // ============================================================
    // Recomendacion general
    // ============================================================

    public Recomendacion generarRecomendacionGeneral(List<Diagnostico> diagnosticos) {
        long criticos = contarPorNivel(diagnosticos, NivelAlerta.CRITICO);
        long alertas  = contarPorNivel(diagnosticos, NivelAlerta.ALERTA);
        long atencion = contarPorNivel(diagnosticos, NivelAlerta.ATENCION);

        if (criticos > 0) return recomendacionCritica(criticos);
        if (alertas  > 0) return recomendacionAlerta(alertas);
        if (atencion > 0) return recomendacionAtencion(atencion);
        return recomendacionNormal();
    }

    private long contarPorNivel(List<Diagnostico> diagnosticos, NivelAlerta nivel) {
        return diagnosticos.stream()
            .filter(d -> d.getNivel() == nivel)
            .count();
    }

    private Recomendacion recomendacionCritica(long criticos) {
        return new Recomendacion(
            "URGENTE: " + criticos + " cajas con cola critica.\n" +
            "  Aumentar el numero de cajas\n" +
            "  Reducir tiempos de atencion\n" +
            "  Revisar distribucion de clientes",
            "CRITICO",
            "Aumentar cajas en " + criticos + " unidades",
            25.0, false
        );
    }

    private Recomendacion recomendacionAlerta(long alertas) {
        return new Recomendacion(
            "ATENCION: " + alertas + " cajas con cola alta.\n" +
            "  Considerar abrir mas cajas en horas pico\n" +
            "  Optimizar tiempos de atencion",
            "ALTO",
            "Ajustar tiempos o agregar cajas temporales",
            15.0, false
        );
    }

    private Recomendacion recomendacionAtencion(long atencion) {
        return new Recomendacion(
            "INFO: " + atencion + " cajas subutilizadas.\n" +
            "  Redistribuir clientes a estas cajas\n" +
            "  Considerar convertir cajas normales a rapidas",
            "MEDIO",
            "Redistribuir carga entre cajas",
            10.0, false
        );
    }

    private Recomendacion recomendacionNormal() {
        return new Recomendacion(
            "CONFIGURACION OPTIMA!\n" +
            "  Todas las cajas funcionan correctamente\n" +
            "  Mantener la configuracion actual",
            "NORMAL",
            "Mantener configuracion actual",
            0.0, false
        );
    }
}