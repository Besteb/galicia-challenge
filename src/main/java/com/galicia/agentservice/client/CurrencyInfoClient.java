package com.galicia.agentservice.client;

import com.galicia.agentservice.model.CurrencyResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "currency-info", url = "https://dolarapi.com/v1")
public interface CurrencyInfoClient {

    @GetMapping("/dolares")
    List<CurrencyResponse> getDolares();

    @GetMapping("/dolares/{moneda}")
    CurrencyResponse getDolares(@PathVariable String moneda);

    @GetMapping("/cotizaciones/{moneda}")
    CurrencyResponse getCotizaciones(@PathVariable String moneda);

    @GetMapping("/cotizaciones")
    List<CurrencyResponse> getCotizaciones();
}
