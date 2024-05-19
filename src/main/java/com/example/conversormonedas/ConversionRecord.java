package com.example.conversormonedas;
import java.time.LocalDateTime;

public class ConversionRecord {
    private final String fromCurrency;
    private final String toCurrency;
    private final double amount;
    private final double result;
    private final LocalDateTime timestamp;


    public ConversionRecord(LocalDateTime timestamp, String fromCurrency, String toCurrency, double amount, double result) {
        this.timestamp = timestamp;
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
        this.amount = amount;
        this.result = result;
    }

    // Getters para los atributos de ConversionRecord
    public String getFromCurrency() {
        return fromCurrency;
    }

    public String getToCurrency() {
        return toCurrency;
    }

    public double getAmount() {
        return amount;
    }

    public double getResult() {
        return result;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

}
