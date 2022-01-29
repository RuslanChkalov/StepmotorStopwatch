package services.customSerialDevices.readers;

import jssc.SerialPortEvent;
import jssc.SerialPortException;

public class FirstWorkingReader extends PortReader{

    @Override
    public void serialEvent(SerialPortEvent event) {
        if (event.isRXCHAR() && event.getEventValue() > 0) {
            try {
                String receivedData = serialPort.readString(event.getEventValue());
                logger.info(receivedData);
            } catch (SerialPortException ex) {
                logger.info("Error in receiving response from port: " + ex);
            }
        }
    }
}
