package com.supermercado.application.dto;

public class EstadisticasDTO {

    private final int totalClientesAtendidos;
    private final int totalArticulosVendidos;
    private final int totalMinutosAtencion;
    private final int clientesEnCola;
    private final String cajeroEstrella;
    private final double tiempoPromedioAtencion;
    private final double articulosPromedio;
    private final int clientesGenerados;
    private final String horaSimulada;

    public EstadisticasDTO() {
        this(0, 0, 0, 0, "-", 0.0, 0.0, 0, "08:00");
    }

    private EstadisticasDTO(int totalClientesAtendidos, int totalArticulosVendidos,
                           int totalMinutosAtencion, int clientesEnCola,
                           String cajeroEstrella, double tiempoPromedioAtencion,
                           double articulosPromedio, int clientesGenerados,
                           String horaSimulada) {
        this.totalClientesAtendidos = totalClientesAtendidos;
        this.totalArticulosVendidos = totalArticulosVendidos;
        this.totalMinutosAtencion = totalMinutosAtencion;
        this.clientesEnCola = clientesEnCola;
        this.cajeroEstrella = cajeroEstrella != null ? cajeroEstrella : "-";
        this.tiempoPromedioAtencion = tiempoPromedioAtencion;
        this.articulosPromedio = articulosPromedio;
        this.clientesGenerados = clientesGenerados;
        this.horaSimulada = horaSimulada != null ? horaSimulada : "08:00";
    }

    public int getTotalClientesAtendidos() { return totalClientesAtendidos; }
    public int getTotalArticulosVendidos() { return totalArticulosVendidos; }
    public int getTotalMinutosAtencion() { return totalMinutosAtencion; }
    public int getClientesEnCola() { return clientesEnCola; }
    public String getCajeroEstrella() { return cajeroEstrella; }
    public double getTiempoPromedioAtencion() { return tiempoPromedioAtencion; }
    public double getArticulosPromedio() { return articulosPromedio; }
    public int getClientesGenerados() { return clientesGenerados; }
    public String getHoraSimulada() { return horaSimulada; }

    public static class Builder {
        private int totalClientesAtendidos;
        private int totalArticulosVendidos;
        private int totalMinutosAtencion;
        private int clientesEnCola;
        private String cajeroEstrella;
        private double tiempoPromedioAtencion;
        private double articulosPromedio;
        private int clientesGenerados;
        private String horaSimulada;

        public Builder totalClientesAtendidos(int value) { this.totalClientesAtendidos = value; return this; }
        public Builder totalArticulosVendidos(int value) { this.totalArticulosVendidos = value; return this; }
        public Builder totalMinutosAtencion(int value) { this.totalMinutosAtencion = value; return this; }
        public Builder clientesEnCola(int value) { this.clientesEnCola = value; return this; }
        public Builder cajeroEstrella(String value) { this.cajeroEstrella = value; return this; }
        public Builder tiempoPromedioAtencion(double value) { this.tiempoPromedioAtencion = value; return this; }
        public Builder articulosPromedio(double value) { this.articulosPromedio = value; return this; }
        public Builder clientesGenerados(int value) { this.clientesGenerados = value; return this; }
        public Builder horaSimulada(String value) { this.horaSimulada = value; return this; }

        public EstadisticasDTO build() {
            return new EstadisticasDTO(totalClientesAtendidos, totalArticulosVendidos,
                                      totalMinutosAtencion, clientesEnCola,
                                      cajeroEstrella, tiempoPromedioAtencion,
                                      articulosPromedio, clientesGenerados,
                                      horaSimulada);
        }
    }
}