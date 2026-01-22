package com.galicia.agentservice.service;

import com.galicia.agentservice.client.CurrencyInfoClient;
import com.galicia.agentservice.model.CurrencyResponse;
import com.galicia.agentservice.model.MenuOptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CurrencyServiceTest {
    @Mock
    CurrencyInfoClient currencyInfoClient;

    @InjectMocks
    CurrencyService currencyService;

    @Test
    void getCurrencyInfoForCurrency_shouldGetDolaresInfo() {
        CurrencyResponse dolarOficial = new CurrencyResponse();
        dolarOficial.setNombre("Oficial");
        CurrencyResponse dolarTarjeta = new CurrencyResponse();
        dolarTarjeta.setNombre("Tarjeta");

        List<CurrencyResponse> dolaresList = List.of(dolarOficial, dolarTarjeta);

        when(currencyInfoClient.getDolares()).thenReturn(dolaresList);

        List<CurrencyResponse> currencyResponseList = currencyService.getCurrencyInfoForCurrency(MenuOptions.DOLARES);

        assertThat(currencyResponseList).containsExactlyInAnyOrder(dolarTarjeta, dolarOficial);
        verify(currencyInfoClient).getDolares();
    }

    @Test
    void getCurrencyInfoForCurrency_shouldGetCotizacionesInfo() {
        CurrencyResponse real = new CurrencyResponse();
        real.setNombre("Real");
        CurrencyResponse euro = new CurrencyResponse();
        euro.setNombre("Euro");

        List<CurrencyResponse> cotizacionesList = List.of(real, euro);

        when(currencyInfoClient.getCotizaciones()).thenReturn(cotizacionesList);

        List<CurrencyResponse> currencyResponseList = currencyService.getCurrencyInfoForCurrency(MenuOptions.COTIZACIONES);

        assertThat(currencyResponseList).containsExactlyInAnyOrder(euro, real);
        verify(currencyInfoClient).getCotizaciones();
    }

    @Test
    void getCurrencyInfoForCurrency_shouldGetOnlyDolarCriptoInfo() {
        CurrencyResponse dolarCripto = new CurrencyResponse();
        dolarCripto.setNombre("Dolar cripto");

        when(currencyInfoClient.getDolares(MenuOptions.CRIPTO.name().toLowerCase())).thenReturn(dolarCripto);

        List<CurrencyResponse> currencyResponseList = currencyService.getCurrencyInfoForCurrency(MenuOptions.CRIPTO);

        assertThat(currencyResponseList).hasSize(1);
        assertThat(currencyResponseList).containsExactly(dolarCripto);
    }

    @Test
    void getCurrencyInfoForCurrency_shouldGetOnlyPesoUruguayoInfo() {
        CurrencyResponse pesoUyu = new CurrencyResponse();
        pesoUyu.setNombre("Peso uruguayo");

        when(currencyInfoClient.getCotizaciones(MenuOptions.UYU.name().toLowerCase())).thenReturn(pesoUyu);

        List<CurrencyResponse> currencyResponseList = currencyService.getCurrencyInfoForCurrency(MenuOptions.UYU);

        assertThat(currencyResponseList).hasSize(1);
        assertThat(currencyResponseList).containsExactly(pesoUyu);
    }

    @Test
    void getCurrencyInfo_whenOptionIsUnknown_shouldReturnEmpty() {
        // En realidad, por las opciones disponibles actualmente este flujo es imposible
        // Pero ayuda a ganar coverage
        MenuOptions option = MenuOptions.WELCOME;

        List<CurrencyResponse> result = currencyService.getCurrencyInfoForCurrency(option);

        assertThat(result).isEmpty();
    }
}