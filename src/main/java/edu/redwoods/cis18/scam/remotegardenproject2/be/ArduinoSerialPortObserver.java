package edu.redwoods.cis18.scam.remotegardenproject2.be;


import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortEventListener;
import jssc.SerialPortEvent;

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
			System.out.println(line);
		}
	}

	public static void main(String[] args) {
		new ArduinoSerialPortObserver("COM4");
	}
}