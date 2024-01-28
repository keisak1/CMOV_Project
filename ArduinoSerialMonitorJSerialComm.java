package edu.ufp.cm.arduinoserialmonitor;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortTimeoutException;

import java.awt.event.*;
import java.io.*;
import java.util.concurrent.TimeUnit;

public class ArduinoSerialMonitorJSerialComm implements WindowListener, ActionListener {
    private SerialPort serialPort;

    private OutputStream outputStream;
    private InputStream inputStream;
    private BufferedReader bufferedReader;

    private int peopleCounter = 0;
    private static final long DEBOUNCE_TIME_MS = 3000;
    private static final long STANDING_TIME_THRESHOLD_MS = 5000;
    private long lastEntryTimeA0 = 0;
    private long lastEntryTimeA1 = 0;
    private long standingStartTimeA0 = 0;
    private long standingStartTimeA1 = 0;

    private volatile boolean running = true;

    public static void main(String[] args) {
        ArduinoSerialMonitorJSerialComm monitor = new ArduinoSerialMonitorJSerialComm();
        monitor.initialize();
    }

    public void initialize() {
        SerialPort[] ports = SerialPort.getCommPorts();

        for (SerialPort port : ports) {
            if (port.getSystemPortName().equals("COM5")) {
                serialPort = port;
                break;
            }
        }

        if (serialPort != null) {
            try {
                serialPort.openPort();
                serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 100, 0);

                outputStream = serialPort.getOutputStream();
                inputStream = serialPort.getInputStream();
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                Thread readThread = new Thread(() -> {
                    while (running) {
                        try {
                            String line = readLineWithRetry();
                            if (line != null) {
                                processSerialData(line);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                readThread.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String readLineWithRetry() throws IOException {
        StringBuilder sb = new StringBuilder();
        while (true) {
            try {
                int data = bufferedReader.read();
                if (data == -1 || data == '\n') {
                    break;
                }
                sb.append((char) data);
            } catch (SerialPortTimeoutException e) {
                System.err.println("Timeout occurred while reading from the serial port. Retrying...");
                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return sb.toString();
    }

    private void processSerialData(String data) {
        try {
            String[] parts = data.split(":");
            if (parts.length == 2) {
                double distanceA0 = Double.parseDouble(parts[0]);
                double distanceA1 = Double.parseDouble(parts[1]);

                processSerialDataPortA0(distanceA0);
                processSerialDataPortA1(distanceA1);
            } else {
                System.err.println("Invalid data format: \"" + data + "\"");
            }
        } catch (NumberFormatException e) {
            System.err.println("Invalid data received: \"" + data + "\"");
        }
    }

    private void processSerialDataPortA0(double distance) {
        // Process data from A0 (incrementing)
        if (distance < 50 || distance > 70) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastEntryTimeA0 >= DEBOUNCE_TIME_MS) {
                if (standingStartTimeA0 == 0 || currentTime - standingStartTimeA0 <= STANDING_TIME_THRESHOLD_MS) {
                    peopleCounter++;
                    System.out.println("People counter incremented for A0. Total people: " + peopleCounter);
                }
                lastEntryTimeA0 = currentTime;
                standingStartTimeA0 = 0;
            } else if (standingStartTimeA0 == 0) {
                standingStartTimeA0 = currentTime;
            }
        } else {
            standingStartTimeA0 = 0;
        }
    }

    private void processSerialDataPortA1(double distance) {
        // Process data from A1 (decrementing)
        if (distance < 50 || distance > 70) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastEntryTimeA1 >= DEBOUNCE_TIME_MS) {
                if (standingStartTimeA1 == 0 || currentTime - standingStartTimeA1 <= STANDING_TIME_THRESHOLD_MS) {
                    peopleCounter--;
                    System.out.println("People counter decremented for A1. Total people: " + peopleCounter);
                }
                lastEntryTimeA1 = currentTime;
                standingStartTimeA1 = 0;
            } else if (standingStartTimeA1 == 0) {
                standingStartTimeA1 = currentTime;
            }
        } else {
            standingStartTimeA1 = 0;
        }
    }

    public void close() {
        running = false;
        try {
            if (inputStream != null) inputStream.close();
            if (outputStream != null) outputStream.close();
            if (bufferedReader != null) bufferedReader.close();
            if (serialPort != null) serialPort.closePort();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Handle action events here if needed
    }

    @Override
    public void windowOpened(WindowEvent e) {}

    @Override
    public void windowClosing(WindowEvent e) {
        close();
        System.exit(0);
    }

    @Override
    public void windowClosed(WindowEvent e) {
        close();
        System.exit(0);
    }

    @Override
    public void windowIconified(WindowEvent e) {}

    @Override
    public void windowDeiconified(WindowEvent e) {}

    @Override
    public void windowActivated(WindowEvent e) {}

    @Override
    public void windowDeactivated(WindowEvent e) {}
}
