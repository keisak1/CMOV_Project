# CMOV_Project
 
## Project Description:
The project aims to create a system that monitors the presence of individuals using distance sensors connected to an Arduino board. The system utilizes two distance sensors, one attached to pin A0 and the other to pin A1 on the Arduino. The Arduino continuously reads the sensor data and sends it to a computer via a serial connection. On the computer side, a Java program receives the sensor data, processes it, and maintains separate counters for each sensor, incrementing one counter when someone is detected by the sensor connected to pin A0 and decrementing the other counter when someone is detected by the sensor connected to pin A1.

## Arduino Code:
The Arduino code is responsible for reading data from the distance sensors and transmitting it over the serial port. It continuously reads the sensor values from pins A0 and A1, calculates the corresponding distances using a formula, and sends the distances over the serial port. Each distance value is sent with an identifier to indicate which sensor it corresponds to.

## Java Code:
The Java code runs on the computer and receives the data transmitted by the Arduino over the serial port. It parses the received data, distinguishing between the sensor readings from A0 and A1 based on the identifiers sent by the Arduino. For each sensor reading, it checks if the distance falls within a certain range, indicating the presence of an individual. If an individual is detected by the sensor connected to pin A0, the program increments a counter for A0; if detected by the sensor connected to pin A1, it decrements a counter for A1. Additionally, the program implements debounce logic to ensure reliable counting of individuals.
