package com.jenkins.larenax.services.customSerialDevices.readers;

import com.jenkins.larenax.MainController;
import jssc.SerialPortEvent;
import jssc.SerialPortException;

public class FirstWorkingReader extends PortReader {

    private final MainController mainController;

    public FirstWorkingReader(MainController mainController) {
        this.mainController = mainController;
    }

    @Override
    public void serialEvent(SerialPortEvent event) {
        if (event.isRXCHAR() && event.getEventValue() > 0) {
            try {

                String receivedData = serialPort.readString(event.getEventValue());
//                logger.info(receivedData);
            } catch (SerialPortException ex) {
                logger.info("Error in receiving response from port: " + ex);
            }
        }
    }
}
