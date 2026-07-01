package com.supermercado.presentation.supermercado.view;

import java.awt.*;

public class Theme {

    // Azul corporativo principal
    public static final Color PRIMARY_BLUE     = new Color(41,  98,  155);
    public static final Color PRIMARY_DARK     = new Color(25,  60,  110);
    public static final Color PRIMARY_LIGHT    = new Color(180, 210, 240);
    public static final Color PRIMARY_VERY_LIGHT = new Color(230, 242, 255);

    // Naranja acento (solo para badges, no botones)
    public static final Color SECONDARY_ORANGE = new Color(210, 120, 30);
    public static final Color SECONDARY_DARK   = new Color(170,  90, 15);
    public static final Color SECONDARY_LIGHT  = new Color(255, 220, 170);

    // Estados - tonos suaves, no saturados
    public static final Color SUCCESS_GREEN  = new Color(39,  150,  80);
    public static final Color WARNING_YELLOW = new Color(200, 150,  20);
    public static final Color ALERT_RED      = new Color(180,  50,  40);

    // Aliases para compatibilidad con todos los archivos
    public static final Color PRIMARY  = PRIMARY_BLUE;
    public static final Color SUCCESS  = SUCCESS_GREEN;
    public static final Color WARNING  = WARNING_YELLOW;
    public static final Color DANGER   = ALERT_RED;
    public static final Color INFO     = new Color(30, 130, 190);
    public static final Color PAUSE    = new Color(120, 60, 160);
    public static final Color SECONDARY = SECONDARY_ORANGE;

    // Barras de cola progresivas
    public static final Color COLA_VERDE    = new Color(39,  150,  80);
    public static final Color COLA_AMARILLO = new Color(200, 160,  20);
    public static final Color COLA_NARANJA  = new Color(210, 100,  20);
    public static final Color COLA_ROJO     = new Color(180,  50,  40);

    // Neutros
    public static final Color BACKGROUND    = new Color(242, 244, 247);
    public static final Color SURFACE       = Color.WHITE;
    public static final Color SURFACE_LIGHT = new Color(250, 251, 253);
    public static final Color TEXT_PRIMARY   = new Color(25,  25,  25);
    public static final Color TEXT_SECONDARY = new Color(90,  90,  90);
    public static final Color TEXT_LIGHT     = Color.WHITE;
    public static final Color BORDER_GRAY    = new Color(200, 205, 210);
    public static final Color BORDER_LIGHT   = new Color(215, 220, 225);
    public static final Color BORDER_DARK    = new Color(150, 158, 168);

    // Fuentes
    public static final Font FONT_TITLE  = new Font("Segoe UI", Font.BOLD,  18);
    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD,  13);
    public static final Font FONT_NORMAL = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_SMALL  = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_MONO   = new Font("Monospaced", Font.PLAIN, 12);

    public static final int PADDING_SMALL  = 5;
    public static final int PADDING_MEDIUM = 10;
    public static final int PADDING_LARGE  = 15;
    public static final int BORDER_RADIUS  = 6;
}