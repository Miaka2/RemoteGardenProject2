package edu.redwoods.cis18.scam.remotegardenproject2.ard;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ArduinoConnectorSimulator {
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