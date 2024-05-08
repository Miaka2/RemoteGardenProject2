package edu.redwoods.cis18.scam.remotegardenproject2.jfx;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.sql.ResultSet;
import java.sql.SQLException;

import edu.redwoods.cis18.scam.remotegardenproject2.db.DatabaseManager;

public class Main extends Application {

    private final DatabaseManager databaseManager = new DatabaseManager("jdbc:mysql://localhost:3306/remotegarden", "username", "password");

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        root.setCenter(barChart);

        Scene scene = new Scene(root, 800, 600);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Moisture Data Bar Chart");
        primaryStage.show();

        // Fetch data from the database and populate the bar chart
        fetchAndPopulateData(barChart);
    }

    private void fetchAndPopulateData(BarChart<String, Number> barChart) {
        try {
            // Execute a query to retrieve moisture data
            databaseManager.performQuery("SELECT timestamp, sensor_type, moisture_level, percentage FROM SoilMoisture");

            // Retrieve the result set from the DatabaseManager
            ResultSet resultSet = databaseManager.getResultSet();

            // Create a data series for the bar chart
            BarChart.Series<String, Number> series = new BarChart.Series<>();

            // Add data points to the data series
            while (resultSet.next()) {
                String timestamp = resultSet.getString("timestamp");
                String sensorType = resultSet.getString("sensor_type");
                int moistureLevel = resultSet.getInt("moisture_level");
                double percentage = resultSet.getDouble("percentage");

                String label = timestamp + " - " + sensorType;
                series.getData().add(new BarChart.Data<>(label, moistureLevel));
                series.getData().add(new BarChart.Data<>(label, percentage));
            }

            // Add the data series to the bar chart
            barChart.getData().add(series);

            // Close the result set
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
