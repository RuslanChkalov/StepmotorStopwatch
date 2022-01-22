package services.devices.readers;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

/**
 * Default serialport data receiver. Each PortReader contains unique actions for each SerialDevice on data receive.
 */
public class PortReader implements SerialPortEventListener {

    private SerialPort serialPort;

    public PortReader(SerialPort serialPort) {
        this.serialPort = serialPort;
    }

    @Override
    public void serialEvent(SerialPortEvent event) {
        if (event.isRXCHAR() && event.getEventValue() > 0) {
            try {
                String receivedData = serialPort.readString(event.getEventValue());
                System.out.println(receivedData);
            } catch (SerialPortException ex) {
                System.out.println("Error in receiving response from port: " + ex);
            }
        }
    }
}
