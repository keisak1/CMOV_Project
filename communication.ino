

const int sensorPin = A0;
const int sensorPinA1 = A1;
void setup() {
    Serial.begin(9600);
}

void loop() {
    int sensorValue = analogRead(sensorPin);
    float voltage = sensorValue * 5.0 / 1023.0;
    float distance = 16.2537 * pow(voltage, 4) - 129.1893 * pow(voltage, 3) + 382.268 * pow(voltage, 2) - 512.611 * voltage + 306.439;

    int sensorValueA1 = analogRead(sensorPinA1);
    float voltageA1 = sensorValueA1 * 5.0 / 1023.0;
    float distanceA1 = 16.2537 * pow(voltageA1, 4) - 129.1893 * pow(voltageA1, 3) + 382.268 * pow(voltageA1, 2) - 512.611 * voltageA1 + 306.439;
    Serial.print(distance); // Value for A0
    Serial.print(":"); // Separator
    Serial.println(distanceA1); // Value for A1
    
    delay(100);
}