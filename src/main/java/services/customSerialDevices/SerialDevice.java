package services.customSerialDevices;

import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import services.customSerialDevices.exceptions.SerialDeviceException;
import services.customSerialDevices.readers.PortReader;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * Universal Serial device class, contains unified methods.
 */
public class SerialDevice {
    private static final Logger logger = LogManager.getLogger(SerialDevice.class);

    private SerialPort serialPort;

    /**
     * SerialDevice check state method.
     */
    public boolean isOpened() {
        if (serialPort == null) {
            return false;
        }
        //Check if device was disconnected after init
        if (Arrays.stream(SerialPortList.getPortNames()).noneMatch(a -> a.equals(serialPort.getPortName()))) {
            serialPort = null;
            return false;
        }
        return serialPort.isOpened();
    }

    public SerialDevice() {
    }

    /**
     * SerialDevices init-method, looking for a correct COM-port by code phrase.
     * Allows to attach custom Reader to EventListener.
     */
    public void init(Baudrate baudrate, PortReader newReaderExample, String sendCodePhrase, String expectedCodePhrase) throws SerialPortException, SerialDeviceException {
        if (this.serialPort != null && this.serialPort.isOpened()) {
            throw new SerialDeviceException("SerialDevice is already initialized");
        }
        if (this.serialPort != null && !this.serialPort.isOpened()) {
            serialPort = null;
        }
        String[] portNames = SerialPortList.getPortNames();
        for (String portName : portNames) {

            serialPort = new SerialPort(portName);
            try {
                serialPort.openPort();

                serialPort.setParams(baudrate.value,
                        SerialPort.DATABITS_8,
                        SerialPort.STOPBITS_1,
                        SerialPort.PARITY_NONE);

                serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);

                TimeUnit.MILLISECONDS.sleep(1000);
                serialPort.writeString(sendCodePhrase);
                TimeUnit.MILLISECONDS.sleep(1000);
                String receivedData = serialPort.readString();
                if (receivedData != null && receivedData.equals(expectedCodePhrase)) {
                    break;
                } else {
                    logger.debug("Wrong port: " + serialPort.getPortName());
                    serialPort = null;
                }
            } catch (SerialPortException | InterruptedException ex) {
                logger.debug("Wrong port: " + ex);
                serialPort = null;
            }
        }
        if (serialPort != null) {
            logger.info("Port found: " + serialPort.getPortName());
            newReaderExample.setSerialPort(serialPort);
            serialPort.addEventListener(newReaderExample, SerialPort.MASK_RXCHAR);
        }
    }

    /**
     * To SerialDevice writing method.
     */
    public void write(String text) throws SerialPortException, SerialDeviceException {
        if (!this.isOpened()) {
            throw new SerialDeviceException("SerialDevice is not ready for writing");
        }
        serialPort.writeString(text);
    }
}
