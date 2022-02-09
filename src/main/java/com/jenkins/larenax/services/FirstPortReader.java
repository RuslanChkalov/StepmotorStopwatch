package com.jenkins.larenax.services;

import com.jenkins.larenax.MainController;
import com.jenkins.larenax.devices.customSerialDevice.PortReader;
import jssc.SerialPortEvent;
import jssc.SerialPortException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FirstPortReader extends PortReader {

    private final MainController mainController;

    public FirstPortReader(MainController mainController) {
        this.mainController = mainController;
    }

    @Override
    public void serialEvent(SerialPortEvent event) {

        if (event.isRXCHAR() && event.getEventValue() > 0) {
            try {
                String[] receivedRowData = serialPort.readString(event.getEventValue()).split("\r\n");
                List structuredData = Arrays.stream(receivedRowData)
                        .filter(line -> (line.charAt(0)=='s' && line.charAt(line.length()-1)=='f') )
                        .map(line -> line.replace("s",""))
                        .map(line -> line.replace("f",""))
                        .collect(Collectors.toList());
                logger.info(structuredData);
            } catch (SerialPortException ex) {
                logger.info("Error in receiving response from port: " + ex);
            }
        }
    }
}
