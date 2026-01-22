package com.galicia.agentservice.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;

@RequiredArgsConstructor
@Getter
@ToString
public enum MenuOptions {
    WELCOME ("Mensaje de bienvenida"),
    DOLARES("Ver todas las cotizaciones de dólares"),
    OFICIAL("Ver cotización de dólar oficial"),
    BLUE("Ver cotización de dólar blue"),
    BOLSA("Ver cotización de dólar bolsa"),
    CONTADOCONLIQUI("Ver cotización de dólar CCL"),
    TARJETA("Ver cotización de dólar tarjeta"),
    MAYORISTA("Ver cotización de dólar mayorista"),
    CRIPTO("Ver cotización de dólar cripto"),
    COTIZACIONES("Ver todas las cotizaciones oficiales"),
    EUR("Ver cotización del euro"),
    BRL("Ver cotización del real brasileño"),
    CLP("Ver cotización del peso chileno"),
    UYU("Ver cotización del peso uruguayo");

    private final String description;

    public boolean isDolar() {
        return List.of(DOLARES, OFICIAL, BLUE, BOLSA, CONTADOCONLIQUI, TARJETA, MAYORISTA, CRIPTO).contains(this);
    }

    public boolean isCotizacion() {
        return List.of(COTIZACIONES, EUR, BRL, CLP, UYU).contains(this);
    }
}
