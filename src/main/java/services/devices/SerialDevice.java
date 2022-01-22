package services.devices;

import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import services.devices.readers.PortReader;

import java.util.concurrent.TimeUnit;

/**
 * Universal Serial device class, contains unified methods.
 */
public class SerialDevice {
    private static final Logger logger = LogManager.getLogger(SerialDevice.class);

    private SerialPort serialPort;
    boolean isOpened = false;

    public boolean isOpened() {
        return serialPort.isOpened();
    }

    public void setOpened(boolean opened) { }

    public SerialDevice() {
    }

    public void init(int baudrate, String sendCodePhrase, String expectedCodePhrase) throws SerialPortException {
        String[] portNames = SerialPortList.getPortNames();
        for (String portName : portNames) {
            System.out.println(portName);

            serialPort = new SerialPort(portName);
            try {
                serialPort.openPort();

                serialPort.setParams(SerialPort.BAUDRATE_115200,
                        SerialPort.DATABITS_8,
                        SerialPort.STOPBITS_1,
                        SerialPort.PARITY_NONE);

                serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);

                TimeUnit.MILLISECONDS.sleep(1000);
                serialPort.writeString(sendCodePhrase);
                TimeUnit.MILLISECONDS.sleep(1000);
                String receivedData=serialPort.readString();
                if (receivedData!=null && receivedData.equals(expectedCodePhrase)) {
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
            serialPort.addEventListener(new PortReader(serialPort), SerialPort.MASK_RXCHAR);
        }
    }

    public void write(String text) throws SerialPortException {
        serialPort.writeString(text);
    }
}
