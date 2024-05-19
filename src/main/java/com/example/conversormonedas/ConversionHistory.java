package com.example.conversormonedas;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ConversionHistory {
    private final List<ConversionRecord> conversionHistory;
    private final String fileName;

    public ConversionHistory(String fileName) {
        this.fileName = fileName;
        this.conversionHistory = new ArrayList<>();
    }

    // Método para agregar un registro de conversión al historial
    public void add(ConversionRecord record) {
        conversionHistory.add(record);
    }

    public void saveHistoryToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (ConversionRecord record : conversionHistory) {
                String formattedTimestamp = record.getTimestamp().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
                String line = String.format("[%s] %.1f %s -> %s: %.2f", formattedTimestamp, record.getAmount(), record.getFromCurrency(), record.getToCurrency(), record.getResult());
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadHistoryFromFile() {
        File file = new File(fileName);
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.matches("\\[\\d{2}-\\d{2}-\\d{4} \\d{2}:\\d{2}:\\d{2}] \\d+\\.\\d+ [A-Z]+ -> [A-Z]+: \\d+\\.\\d+")) {
                        String[] parts = line.split(" ");
                        String formattedTimestamp = parts[0].replace("[", "") + " " + parts[1].replace("]", "");

                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("[dd-MM-yyyy HH:mm:ss]");

                        LocalDateTime timestamp = LocalDateTime.parse(formattedTimestamp, formatter);
                        double amount = Double.parseDouble(parts[2]);
                        String fromCurrency = parts[3];
                        String toCurrency = parts[5].replace(":", "");
                        double result = Double.parseDouble(parts[6]);
                        conversionHistory.add(new ConversionRecord(timestamp, fromCurrency, toCurrency, amount, result));
                    } else {
                        System.err.println("Error: Formato incorrecto en la línea: " + line);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void clearHistory() {
        conversionHistory.clear();
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write("");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<ConversionRecord> getConversionHistory() {
        return conversionHistory;
    }
}
