package com.example.conversormonedas;

import java.math.BigDecimal;
public class CurrencyConverter {
    private final ExchangeRateAPI exchangeRateAPI;

    public CurrencyConverter(String apiKey) {
        this.exchangeRateAPI = new ExchangeRateAPI(apiKey);
    }

    public double convertCurrency(Currency fromCurrency, Currency toCurrency, double amount) {
        BigDecimal conversionRate = exchangeRateAPI.getConversionRate(fromCurrency.getCode(), toCurrency.getCode());
        return amount * conversionRate.doubleValue();
    }
}
