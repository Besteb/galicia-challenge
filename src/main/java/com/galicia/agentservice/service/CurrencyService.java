package com.galicia.agentservice.service;

import com.galicia.agentservice.client.CurrencyInfoClient;
import com.galicia.agentservice.model.CurrencyResponse;
import com.galicia.agentservice.model.MenuOptions;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CurrencyService {

    private final CurrencyInfoClient currencyInfoClient;

    public List<CurrencyResponse> getCurrencyInfoForCurrency(MenuOptions menuOptions) {
        return switch (menuOptions) {
            case COTIZACIONES -> currencyInfoClient.getCotizaciones();
            case DOLARES -> currencyInfoClient.getDolares();
            default -> fetchSingleCurrency(menuOptions);
        };
    }

    private List<CurrencyResponse> fetchSingleCurrency(MenuOptions menuOptions) {
        String param = menuOptions.name().toLowerCase();

        if (menuOptions.isDolar()) {
            return List.of(currencyInfoClient.getDolares(param));
        }

        if (menuOptions.isCotizacion()) {
            return List.of(currencyInfoClient.getCotizaciones(param));
        }

        return Collections.emptyList();
    }
}
