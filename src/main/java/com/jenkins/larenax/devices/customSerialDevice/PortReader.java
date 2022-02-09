package com.jenkins.larenax.devices.customSerialDevice;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Default {@link SerialDevice} data receiver. Each PortReader contains unique actions for each {@link SerialDevice} on data receive.
 */
public abstract class PortReader implements SerialPortEventListener {
    public static final Logger logger = LogManager.getLogger(PortReader.class);

    protected SerialPort serialPort;
    private Object guiController;

    public PortReader(Object guiController) {
        this.guiController = guiController;
    }

    public PortReader() {
    }

    public void setSerialPort(SerialPort serialPort) {
        this.serialPort = serialPort;
    }

    /**
     * On COM-port data receiver method.
     */
    @Override
    public void serialEvent(SerialPortEvent event) {
        if (event.isRXCHAR() && event.getEventValue() > 0) {
            try {
                String receivedData = serialPort.readString(event.getEventValue());
                logger.debug(receivedData);
            } catch (SerialPortException ex) {
                logger.debug("Error in receiving response from port: " + ex);
            }
        }
    }
}
