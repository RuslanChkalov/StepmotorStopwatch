package com.jenkins.larenax.devices.customSerialDevice;

import com.jenkins.larenax.devices.customSerialDevice.exceptions.SerialDeviceException;
import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

/**
 * Universal Serial device class, contains unified methods.
 */
public class SerialDevice {
    private static final Logger logger = LogManager.getLogger(SerialDevice.class);

    private BaseCheckTask task;
    private Timer timer;

    private SerialPort serialPort;
    private String closePhrase;

    private boolean deviceConnectingProcess = false;

    public SerialDevice() {
    }

    /**
     * {@link SerialDevice} check state method.
     */
    public boolean isOpened() {
        if (serialPort == null || deviceConnectingProcess) {
            return false;
        }
        //Check if device was disconnected after init
        if (Arrays.stream(SerialPortList.getPortNames()).noneMatch(a -> a.equals(serialPort.getPortName()))) {
            serialPort = null;
            return false;
        }
        return serialPort.isOpened();
    }

    /**
     * Get connecting process state method.
     */
    public boolean isDeviceConnectingProcess() {
        return deviceConnectingProcess;
    }

    /**
     * The timer allows to check the status of {@link SerialDevice} in background.
     */
    public void addTimer(BaseCheckTask task) {
        if (task == null) {
            return;
        }
        removeTimer();
        timer = new Timer();
        this.task = task;
        timer.schedule(this.task, 0, 1000l);
    }

    /**
     * Timer cancelling method.
     */
    public void removeTimer() {
        if (task != null) {
            task.cancel();
            task.closeEvent();
            task = null;
        }
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
    }

    /**
     * {@link SerialDevice} init-method, looking for a correct COM-port by code phrase.
     * Allows to attach custom Reader to EventListener.
     */
    public void init(Baudrate baudrate, PortReader newReaderExample, String sendCodePhrase, String expectedCodePhrase, String closePhrase) throws SerialDeviceException, SerialPortException {
        close();
        String[] portNames = SerialPortList.getPortNames();
        deviceConnectingProcess = true;

        new Thread(() -> {
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
                    TimeUnit.MILLISECONDS.sleep(2000);
                    String receivedData = serialPort.readString();

                    if (receivedData != null && receivedData.contains(expectedCodePhrase)) {
                        break;
                    } else {
                        logger.debug("Wrong port: " + serialPort.getPortName());
                        serialPort.closePort();
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
                this.closePhrase = closePhrase;
                try {
                    serialPort.addEventListener(newReaderExample, SerialPort.MASK_RXCHAR);
                    if (task!=null) {
                        task.connectedEvent();
                    }
                } catch (SerialPortException e) {
                    logger.warn(e);
                }
            } else {
                removeTimer();
            }
            deviceConnectingProcess = false;
        }).start();
    }

    /**
     * To {@link SerialDevice} writing method.
     */
    public void write(String text) throws SerialPortException, SerialDeviceException {
        if (!isOpened()) {
            throw new SerialDeviceException("SerialDevice is not ready for writing");
        }
        serialPort.writeString(text);
    }

    /**
     * {@link SerialDevice} closing method.
     */
    public void close() {
        removeTimer();
        try {
            if (closePhrase != null) {
                serialPort.writeString(closePhrase);
            }
            serialPort.closePort();
        } catch (Exception e) {
        }
        serialPort = null;
    }
}
