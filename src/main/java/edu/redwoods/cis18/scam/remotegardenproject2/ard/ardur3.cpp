const int soilMoisturePin = A0;

void setup() {
  Serial.begin(9600);
}

void loop() {
  int sensorValue = analogRead(soilMoisturePin);

  Serial.print("Soil Moisture Level (0-1023): ");
  Serial.println(sensorValue);

  delay(1000);
}