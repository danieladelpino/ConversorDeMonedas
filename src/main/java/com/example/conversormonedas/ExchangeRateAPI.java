package com.example.conversormonedas;
import com.google.gson.Gson;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

public class ExchangeRateAPI {
    private final String apiKey;
    private final HttpClient httpClient;
    private final Gson gson;

    public ExchangeRateAPI(String apiKey) {
        this.apiKey = apiKey;
        this.httpClient = HttpClient.newHttpClient();
        this.gson = new Gson();
    }

    public BigDecimal getConversionRate(String fromCurrency, String toCurrency) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("https://v6.exchangerate-api.com/v6/" + apiKey + "/latest/" + fromCurrency))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                String responseBody = response.body();
                ExchangeRateApiResponse apiResponse = gson.fromJson(responseBody, ExchangeRateApiResponse.class);
                Map<String, Double> conversionRates = apiResponse.getConversionRates();
                if (conversionRates.containsKey(toCurrency)) {
                    return BigDecimal.valueOf(conversionRates.get(toCurrency));
                } else {
                    System.out.println("La moneda de destino especificada no está disponible en la respuesta.");
                }
            } else {
                System.out.println("Error al obtener la tasa de conversión. Código de estado: " + response.statusCode());
            }
        } catch (IOException | InterruptedException | URISyntaxException e) {
            e.printStackTrace();
        }

        return BigDecimal.ONE; // Valor por defecto, es decir, 1:1
    }

    // Clase auxiliar para deserializar la respuesta JSON
    private static class ExchangeRateApiResponse {
        private Map<String, Double> conversion_rates;

        public Map<String, Double> getConversionRates() {
            return conversion_rates;
        }
    }
}
