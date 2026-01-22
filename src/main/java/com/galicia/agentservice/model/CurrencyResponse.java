package com.galicia.agentservice.model;

import lombok.Data;
import lombok.Getter;
import lombok.ToString;

import java.util.Date;

@Data
@Getter
@ToString
public class CurrencyResponse {
    Long compra;
    Long venta;
    String casa;
    String nombre;
    String moneda;
    Date fechaActualizacion;
}
