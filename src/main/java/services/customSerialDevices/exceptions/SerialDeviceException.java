package services.customSerialDevices.exceptions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import services.customSerialDevices.SerialDevice;

public class SerialDeviceException extends Exception{
    private static final Logger logger = LogManager.getLogger(SerialDeviceException.class);
    public SerialDeviceException(String message) {
        super(message);
        logger.warn(message);
    }
}