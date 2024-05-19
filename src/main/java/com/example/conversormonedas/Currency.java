package com.example.conversormonedas;

public class Currency {
    private final String code; // Código de la moneda (ejemplo: USD)
    private final String description; // Descripción de la moneda (ejemplo: Dólar estadounidense)

    public Currency(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    @Override
    public String toString() {
        return description + " (" + code + ")";
    }
}

