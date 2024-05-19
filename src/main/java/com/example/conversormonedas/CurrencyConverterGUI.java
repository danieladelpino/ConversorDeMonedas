package com.example.conversormonedas;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.scene.control.ListView;
import javafx.scene.layout.ColumnConstraints;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

// Clase principal que gestiona la interfaz de usuario de la aplicación
public class CurrencyConverterGUI extends Application {
    private GridPane gridPane;
    private ComboBox<Currency> fromComboBox;
    private ComboBox<Currency> toComboBox;
    private TextField amountTextField;
    private Button convertButton;
    private Label resultLabel;
    private Button resetButton;
    private Button invertButton;
    private Button clearHistoryButton;
    private CurrencyConverter currencyConverter;
    private ConversionHistory conversionHistory;
    private ListView<String> historyListView;


    @Override
    public void start(Stage primaryStage) {
        // Inicializar la interfaz de usuario
        initializeUI(primaryStage);
    }

    private void initializeUI(Stage primaryStage) {
        // Crear diseño de la interfaz
        gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(10));

        // Inicializar componentes de la interfaz de usuario
        initUIComponents();

        primaryStage.setTitle("Conversor de Monedas"); // Establecer el título de la ventana
        Image icono = new Image(getClass().getResourceAsStream("/money-icon.png"));// Cargar el archivo de imagen del icono
        // Establecer el icono del escenario
        primaryStage.getIcons().add(icono);
        // Configurar escena y mostrar la ventana
        Scene scene = new Scene(gridPane, 400, 450);
        primaryStage.setScene(scene);


        // Evitar que la ventana se pueda redimensionar
        primaryStage.setResizable(false);

        primaryStage.show();

        // Inicializar el historial de conversiones ListView
        historyListView = new ListView<>();
        historyListView.setPrefHeight(200); // Altura
        historyListView.setPrefWidth(300); // Ancho
        gridPane.add(historyListView, 0, 7, 5, 1); // Agrega el ListView en una nueva fila bajo los otros elementos
        // Instanciar CurrencyConverter
        currencyConverter = new CurrencyConverter("676db68db54088ac11f74167");
        // Inicializar historial de conversiones
        conversionHistory = new ConversionHistory("conversion_history.txt");
        // Cargar historial desde el archivo después de inicializar historyListView
        conversionHistory.loadHistoryFromFile();
        // Actualizar el historial de conversiones en el ListView
        updateHistoryListView(historyListView);
    }

    private void initUIComponents() {
        // Crear controles
        Label fromLabel = new Label("De:");
        Label toLabel = new Label("A:");
        Label amountLabel = new Label("Cantidad:");
        fromComboBox = new ComboBox<>();
        toComboBox = new ComboBox<>();
        amountTextField = new TextField();
        convertButton = new Button("Convertir");
        resultLabel = new Label();
        resetButton = new Button("\uD83D\uDD03");
        invertButton = new Button("\uD83D\uDD00");
        Label texHist = new Label("Historial de conversiones:");
        clearHistoryButton = new Button("Borrar historial");

        // Agregar columnas al GridPane
        ColumnConstraints column1 = new ColumnConstraints();
        column1.setPercentWidth(27);
        ColumnConstraints column2 = new ColumnConstraints();
        column2.setPercentWidth(8);
        ColumnConstraints column3 = new ColumnConstraints();
        column3.setPercentWidth(25);
        ColumnConstraints column4 = new ColumnConstraints();
        column4.setPercentWidth(40);

        gridPane.getColumnConstraints().addAll(column1, column2, column3, column4);

        // Agregar componentes al GridPane
        gridPane.add(fromLabel, 1, 0);
        gridPane.add(fromComboBox, 2, 0, 4, 1);
        gridPane.add(invertButton, 0, 0, 1, 1);
        gridPane.add(toLabel, 1, 1);
        gridPane.add(toComboBox, 2, 1, 4, 1);
        gridPane.add(resetButton, 0, 1, 1, 1);
        gridPane.add(amountLabel, 0, 2);
        gridPane.add(amountTextField, 1, 2, 4, 1);
        gridPane.add(convertButton, 2, 3, 3, 1);
        gridPane.add(resultLabel, 1, 4, 5, 1);
        gridPane.add(texHist, 0, 6, 5, 1);
        gridPane.add(clearHistoryButton, 2, 8, 4, 1);

        setupComboBoxes();
        setupButtons();
        setupListeners();
    }
    private void setupComboBoxes(){
        // Llenar ComboBox con las opciones de monedas soportadas
        CurrencyCoins.fillComboBox(fromComboBox.getItems());
        CurrencyCoins.fillComboBox(toComboBox.getItems());
    }
    private void setupButtons(){
        // Establecer la acción del botón de convertir
        convertButton.setOnAction(e -> convertCurrency());

        // Establecer la acción del botón de reinicio
        resetButton.setOnAction(e -> resetUI());

        // Establecer la acción del botón de invertir
        invertButton.setOnAction(e -> updateComboBoxOptions());

        // Establecer la acción del botón de borrar historial
        clearHistoryButton.setOnAction(e -> clearHistory());
    }
    private void setupListeners(){
        // Agregar oyentes de selección a los ComboBoxes
        toComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                // Si se selecciona una moneda en toComboBox, eliminarla de fromComboBox
                fromComboBox.getItems().removeIf(currency -> currency.getCode().equals(newValue.getCode()));
                toComboBox.setValue(newValue);
                // Si había una moneda seleccionada anteriormente en toComboBox, agrégala de nuevo a fromComboBox
                if (oldValue != null && !fromComboBox.getItems().contains(oldValue)) {
                    fromComboBox.getItems().add(oldValue);
                }
            } else {
                // Si no hay ninguna selección en toComboBox, restaurar las opciones originales en fromComboBox
                CurrencyCoins.fillComboBox(fromComboBox.getItems());
            }
        });

        fromComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                // Si se selecciona una moneda en fromComboBox, eliminarla de toComboBox
                toComboBox.getItems().removeIf(currency -> currency.getCode().equals(newValue.getCode()));
                fromComboBox.setValue(newValue);
                // Si había una moneda seleccionada anteriormente en fromComboBox, agrégala de nuevo a toComboBox
                if (oldValue != null && !toComboBox.getItems().contains(oldValue)) {
                    toComboBox.getItems().add(oldValue);
                }
            } else {
                // Si no hay ninguna selección en fromComboBox, restaurar las opciones originales en toComboBox
                CurrencyCoins.fillComboBox(toComboBox.getItems());
            }
        });
    }
    private void convertCurrency() {
        Currency fromCurrency = fromComboBox.getValue();
        Currency toCurrency = toComboBox.getValue();
        String amountText = amountTextField.getText();

        // Verificar si se han seleccionado las monedas en ambos ComboBox
        if (fromCurrency == null || toCurrency == null) {
            resultLabel.setText("Seleccione las monedas.");
            return; // Salir del método si las monedas no están seleccionadas
        }

        // Verificar si el texto ingresado es un número válido
        try {
            double amount = Double.parseDouble(amountText); // Obtener la cantidad ingresada
            double result = currencyConverter.convertCurrency(fromCurrency, toCurrency, amount);
            resultLabel.setText(String.format("Resultado de la conversión: %.2f", result)); // Mostrar el resultado en la etiqueta resultLabel

            // Agregar el registro de conversión al historial
            conversionHistory.add(new ConversionRecord(LocalDateTime.now(), fromCurrency.getCode(), toCurrency.getCode(), amount, result));
            conversionHistory.saveHistoryToFile();

            // Actualiza el historial de conversiones en el ListView
            updateHistoryListView(historyListView);
        } catch (NumberFormatException e) {
            // Mostrar un mensaje de error si no se ingresa un número válido
            resultLabel.setText("Ingrese un valor válido.");
        }
    }

    // Método para actualizar el ListView del historial de conversiones
    private void updateHistoryListView(ListView<String> historyListView) {
        // Limpia el ListView
        historyListView.getItems().clear();

        // Formatea y agrega cada registro al ListView
        for (ConversionRecord record : conversionHistory.getConversionHistory()) {
            String formattedTimestamp = record.getTimestamp().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
            String historyEntry = String.format("[%s] %s %s -> %s: %.2f", formattedTimestamp, record.getAmount(), record.getFromCurrency(), record.getToCurrency(), record.getResult());
            historyListView.getItems().add(historyEntry);
        }
    }

    private void updateComboBoxOptions() {
        // Obtener las monedas seleccionadas en los ComboBoxes
        Currency selectedFrom = fromComboBox.getValue();
        Currency selectedTo = toComboBox.getValue();

        // Verificar si hay una selección en ambos ComboBoxes
        if (selectedFrom != null && selectedTo != null) {
            // Intercambiar las selecciones
            fromComboBox.setValue(selectedTo);
            toComboBox.setValue(selectedFrom);
        }
    }

    private void resetUI() {
        // Limpiar las selecciones de ComboBox
        fromComboBox.getSelectionModel().clearSelection();
        toComboBox.getSelectionModel().clearSelection();

        // Limpiar el texto en resultLabel
        resultLabel.setText("");
        amountTextField.setText("");

        // Restaurar las opciones originales en ambos ComboBox
        fromComboBox.getItems().clear();
        toComboBox.getItems().clear();

        // Llenar los ComboBox con las monedas disponibles
        CurrencyCoins.fillComboBox(fromComboBox.getItems());
        CurrencyCoins.fillComboBox(toComboBox.getItems());
    }


    private void clearHistory() {
        conversionHistory.clearHistory();
        // Actualizar el ListView del historial después de borrar los datos
        updateHistoryListView(historyListView);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
