package edu.redwoods.cis18.scam.remotegardenproject2.be;


import edu.redwoods.cis18.scam.remotegardenproject2.db.DatabaseManager;
import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortEventListener;
import jssc.SerialPortEvent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ArduinoSerialPortObserver {

	protected final SerialPort serialPort;

	public ArduinoSerialPortObserver(String portName) {
		serialPort = new SerialPort(portName);
		try {
			serialPort.openPort();
			serialPort.setParams(SerialPort.BAUDRATE_9600,
					SerialPort.DATABITS_8,
					SerialPort.STOPBITS_1,
					SerialPort.PARITY_NONE);

			serialPort.addEventListener(new SerialPortReader(), SerialPort.MASK_RXCHAR);
			System.out.println("Started listening on " + portName);
		} catch (SerialPortException ex) {
			System.out.println("There was an error initializing the serial port: " + ex);
		}
	}

	private class SerialPortReader implements SerialPortEventListener {
		protected StringBuilder messageBuilder = new StringBuilder();

		@Override
		public void serialEvent(SerialPortEvent event) {
			if (event.isRXCHAR()) {
				try {
					String receivedData = serialPort.readString(event.getEventValue());
					messageBuilder.append(receivedData);

					int newLineIndex = messageBuilder.indexOf("\n");
					while (newLineIndex != -1) {
						String line = messageBuilder.substring(0, newLineIndex).trim();
						messageBuilder.delete(0, newLineIndex + 1);

						processLine(line);

						newLineIndex = messageBuilder.indexOf("\n");
					}
				} catch (SerialPortException ex) {
					System.out.println("Error reading from serial port: " + ex);
				}
			}
		}

		private void processLine(String line) {
			try {
				// Parsing the line
				String sensorType = line.substring(0, line.indexOf("(")).trim();
				String[] rangeParts = line.substring(line.indexOf("(") + 1, line.indexOf(")")).split("-");
				int maxRange = Integer.parseInt(rangeParts[1]);
				int sensorValue = Integer.parseInt(line.split(": ")[1].trim());
				double percentage = (double) sensorValue / maxRange * 100;

				// Prepare timestamp
				String formattedDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

				// Insert data into the database
				DatabaseManager.insert
						(formattedDateTime, sensorType, sensorValue, percentage);   //RENAME method to interface with db
			} catch (Exception e) {
				System.out.println("Error processing line: " + e.getMessage());
			}
		}
	}

	public static void main(String[] args) {
		new ArduinoSerialPortObserver("COM4");
	}
}