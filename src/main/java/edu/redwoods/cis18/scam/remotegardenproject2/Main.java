package edu.redwoods.cis18.scam.remotegardenproject2;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main extends Application {
    private ArduinoConnector arduinoConnector;
    private List<VBox> plantDetailsList = new ArrayList<>();

    @Override
    public void start(Stage primaryStage) {
        // Instantiate ArduinoConnector with the appropriate file path
        arduinoConnector = new ArduinoConnector("src/SerialPortDataSimulator/NormalConditions");
        arduinoConnector.start();

        // Creating a VBox for the plant list
        VBox plantList = new VBox();
        plantList.setSpacing(5);
        plantList.setPadding(new Insets(10));

        // Creating a ScrollPane to hold the plant list
        ScrollPane scrollPane = new ScrollPane(plantList);
        scrollPane.setFitToWidth(true);

        // Creating buttons for adding and removing plants
        Button addPlantButton = new Button("Add Plant");
        Button removePlantButton = new Button("Remove Plant");

        addPlantButton.setOnAction(e -> addPlant(plantList));
        removePlantButton.setOnAction(e -> removePlant(plantList));

        // Creating a VBox for the buttons
        VBox buttonBox = new VBox();
        buttonBox.getChildren().addAll(addPlantButton, removePlantButton);
        buttonBox.setSpacing(10);
        buttonBox.setPadding(new Insets(10));

        // Creating a BorderPane to hold all components
        BorderPane root = new BorderPane();
        root.setCenter(scrollPane);
        root.setRight(buttonBox);

        // Creating the scene
        Scene scene = new Scene(root, 800, 600);

        // Setting the scene to the stage
        primaryStage.setScene(scene);
        primaryStage.setTitle("Plant Monitoring App");
        primaryStage.show();
    }

    // Method to add a new plant to the list
    private void addPlant(VBox plantList) {
        // Creating a VBox for plant details
        VBox plantDetails = new VBox();
        plantDetails.setSpacing(10);
        plantDetails.setPadding(new Insets(10));

        // Creating input fields for plant information
        TextField nameField = new TextField();
        TextField soilField = new TextField();
        TextField typeField = new TextField();

        // Label for the input fields
        Label nameLabel = new Label("Name:");
        Label soilLabel = new Label("Soil:");
        Label typeLabel = new Label("Type:");

        // Adding input fields and labels to plant details VBox
        plantDetails.getChildren().addAll(nameLabel, nameField, soilLabel, soilField, typeLabel, typeField);

        // Creating a TitledPane to hold the plant information
        TitledPane plantInfoPane = new TitledPane();
        plantInfoPane.setContent(plantDetails);

        // Event listener to update TitledPane title when focus leaves the name field
        nameField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) { // Focus lost
                plantInfoPane.setText(nameField.getText());
            }
        });

        // Creating a BarChart to display hydration and humidity
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis(0, 100, 10); // Range from 0 to 100 with 10 unit intervals
        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setTitle("Plant Monitoring");
        chart.setLegendVisible(false);

        // Create label for alert message
        Label alertLabel = new Label();

        // Adding the BarChart and the label for the alert message to the plant details VBox
        plantDetails.getChildren().addAll(alertLabel, chart);

        // Adding the TitledPane to the plant list
        plantList.getChildren().add(plantInfoPane);

        // Adding the plant details VBox to the list
        plantDetailsList.add(plantDetails);

        // Update chart data and monitor moisture periodically
        updateChartData(chart, alertLabel);
        monitorMoisture(nameField.getText(), alertLabel);
    }

    // Method to remove a plant from the list
    private void removePlant(VBox plantList) {
        if (plantDetailsList.isEmpty()) {
            return;
        }

        // Create a combo box with checkboxes for selecting plants to remove
        ComboBox<VBox> comboBox = new ComboBox<>();
        comboBox.getItems().addAll(plantDetailsList);
        comboBox.setCellFactory(param -> new CheckBoxListCell<>());

        // Create an alert dialog for removing plants
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Remove Plant");
        alert.setHeaderText("Select the plant you want to remove:");
        alert.getDialogPane().setContent(comboBox);

        // Add OK and Cancel buttons
        ButtonType buttonTypeOk = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(buttonTypeOk, buttonTypeCancel);

        // Show dialog and wait for user response
        alert.showAndWait().ifPresent(buttonType -> {
            if (buttonType == buttonTypeOk) {
                plantList.getChildren().remove(comboBox.getValue().getParent()); // Remove the plant details VBox from the plant list
                plantDetailsList.remove(comboBox.getValue()); // Remove the plant details VBox from the list
            }
        });
    }

    // Method to update chart data periodically
    private void updateChartData(BarChart<String, Number> chart, Label alertLabel) {
        AnimationTimer timer = new AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long now) {
                if (now - lastUpdate >= 180_000_000_000L) { // 180,000,000,000 nanoseconds = 3 minutes
                    double hydrationData = arduinoConnector.getHydrationData();
                    double humidityData = arduinoConnector.getHumidityData();
                    XYChart.Series<String, Number> series = new XYChart.Series<>();
                    series.getData().add(new XYChart.Data<>("Hydration", hydrationData));
                    series.getData().add(new XYChart.Data<>("Humidity", humidityData));
                    chart.getData().setAll(series);
                    lastUpdate = now;
                }
            }
        };
        timer.start();
    }

    // Method to monitor moisture level of a plant and display text advice below the chart
    private void monitorMoisture(String plantName, Label alertLabel) {
        new Thread(() -> {
            while (true) {
                int currentMoistureLevel = arduinoConnector.getMoistureLevel(); // Assuming this method returns moisture level
                if (currentMoistureLevel < MoistureAlertSystem.DRY_THRESHOLD) {
                    showAlert(alertLabel, "Moisture level is too low for plant '" + plantName + "'. Please water it.");
                } else if (currentMoistureLevel > MoistureAlertSystem.WET_THRESHOLD) {
                    showAlert(alertLabel, "Moisture level is too high for plant '" + plantName + "'. Consider reducing watering.");
                }
                try {
                    Thread.sleep(60000); // Check moisture level every minute
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    // Method to display alerts within the plant details
    private void showAlert(Label alertLabel, String message) {
        alertLabel.setText(message);
    }

    public static void main(String[] args) {
        launch(args);
    }

    // Inner class representing an Arduino connector
    private static class ArduinoConnector {
        private ArduinoConnectorSimulator simulator;

        public ArduinoConnector(String filePath) {
            simulator = new ArduinoConnectorSimulator(filePath);
        }

        public void start() {
            simulator.start();
        }

        public void stop() {
            simulator.stop();
        }

        // Methods to get hydration, humidity, and moisture level data
        public double getHydrationData() {
            // For now, return a random value for demonstration purposes
            return Math.random() * 100;
        }

        public double getHumidityData() {
            // For now, return a random value for demonstration purposes
            return Math.random() * 100;
        }

        public int getMoistureLevel() {
            // For now, return a random value for demonstration purposes
            return (int) (Math.random() * 100);
        }
    }

    // Inner class representing an Arduino connector simulator
    private static class ArduinoConnectorSimulator {
        private String filePath;
        private ScheduledExecutorService executor;
        private final int INTERVAL = 1000; // Interval in milliseconds

        public ArduinoConnectorSimulator(String filePath) {
            this.filePath = filePath;
        }

        public void start() {
            try {
                List<String> lines = Files.readAllLines(Paths.get(filePath));
                executor = Executors.newSingleThreadScheduledExecutor();
                final int[] counter = {0}; // Use an array to allow modification inside lambda

                Runnable command = () -> {
                    if (counter[0] < lines.size()) {
                        String line = lines.get(counter[0]++);
                        // Assuming the format is "HydrationValue, HumidityValue"
                        String[] values = line.split(",");
                        double hydrationValue = Double.parseDouble(values[0]);
                        double humidityValue = Double.parseDouble(values[1]);
                        // Update the hydration and humidity values
                        updateValues(hydrationValue, humidityValue);
                    } else {
                        executor.shutdown(); // Shut down the executor once all lines are read
                    }
                };

                // Schedule the command to run at fixed intervals
                executor.scheduleAtFixedRate(command, 0, INTERVAL, TimeUnit.MILLISECONDS);

            } catch (IOException e) {
                System.out.println("Error reading file: " + e.getMessage());
            }
        }

        private void updateValues(double hydrationValue, double humidityValue) {
            // Do something with the values, such as updating the UI
            // For now, just print them
            System.out.println("Hydration: " + hydrationValue + ", Humidity: " + humidityValue);
        }

        public void stop() {
            if (executor != null) {
                executor.shutdownNow();
            }
        }
    }

    // Inner class representing the Moisture Alert System
    private static class MoistureAlertSystem {
        private static final int DRY_THRESHOLD = 20; // can be removed to set thresholds within database per plant.
        private static final int WET_THRESHOLD = 80; // can be removed to set thresholds within database per plant.
    }

    // Custom ListCell with a checkbox
    private static class CheckBoxListCell<T> extends ListCell<T> {
        private final CheckBox checkBox = new CheckBox();

        public CheckBoxListCell() {
            checkBox.setOnAction(e -> {
                T item = getItem();
                if (item != null) {
                    checkBox.setSelected(!checkBox.isSelected());
                }
            });
        }

        @Override
        protected void updateItem(T item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setGraphic(null);
            } else {
                checkBox.setText(item.toString());
                setGraphic(checkBox);
            }
        }
    }
}


