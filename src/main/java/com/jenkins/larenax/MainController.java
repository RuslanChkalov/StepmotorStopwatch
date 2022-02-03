package com.jenkins.larenax;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.TimerTask;

import com.jenkins.larenax.services.customSerialDevices.StateCheckTask;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import jssc.SerialPortException;
import com.jenkins.larenax.services.customSerialDevices.Baudrate;
import com.jenkins.larenax.services.customSerialDevices.SerialDevice;
import com.jenkins.larenax.services.customSerialDevices.exceptions.SerialDeviceException;
import com.jenkins.larenax.services.customSerialDevices.readers.FirstWorkingReader;

/**
 * Controller for gui.fxml (code structure generates by "Scene Builder" software
 */
public class MainController {
    private SerialDevice serialDevice;
    @FXML
    private ResourceBundle resources;
    @FXML
    private URL location;
    @FXML
    private Button firstButton;
    @FXML
    private Button secondButton;
    @FXML
    private Button thirdButton;

    public SerialDevice getSerialDevice() {
        return serialDevice;
    }

    /**
     * Custom on-close application method.
     */
    public void onApplicationClose() {
        try {
            serialDevice.close();
        } catch (Exception e) {
        }
    }

    @FXML
    void initialize() {
        assert firstButton != null : "fx:id=\"firstButton\" was not injected: check your FXML file 'gui.fxml'.";
        assert secondButton != null : "fx:id=\"secondButton\" was not injected: check your FXML file 'gui.fxml'.";
        assert thirdButton != null : "fx:id=\"thirdButton\" was not injected: check your FXML file 'gui.fxml'.";

    }

    @FXML
    void thirdButtonAction(ActionEvent event) throws SerialPortException, SerialDeviceException {
        try {
            serialDevice.close();
        } catch (Exception e) {
        }
    }

    @FXML
    void firstButtonAction(ActionEvent event) throws SerialPortException, SerialDeviceException {
        if (serialDevice == null) {
            serialDevice = new SerialDevice();
        }
        try {
            serialDevice.init(Baudrate.BAUDRATE_115200, new FirstWorkingReader(this), "IsArduino", "ImArduino", "ClosePhrase");
            serialDevice.addTimer(new StateCheckTask(this));
        } catch (Exception e) {
        }
    }

    @FXML
    void secondButtonAction(ActionEvent event) throws SerialPortException, SerialDeviceException {
        try {
            serialDevice.write("K");
        } catch (Exception e) {
        }
    }
}