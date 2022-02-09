package com.jenkins.larenax;

import com.jenkins.larenax.devices.customSerialDevice.Baudrate;
import com.jenkins.larenax.devices.customSerialDevice.SerialDevice;
import com.jenkins.larenax.devices.customSerialDevice.exceptions.SerialDeviceException;
import com.jenkins.larenax.services.FirstCheckTask;
import com.jenkins.larenax.services.FirstPortReader;
import com.jenkins.larenax.services.MessageLevel;
import com.jenkins.larenax.services.ServiceFunctions;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import jssc.SerialPortException;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for gui.fxml (code structure generates by "Scene Builder" software)
 */
public class MainController {
    private ServiceFunctions serviceFunctions;
    private SerialDevice serialDevice;
    @FXML
    private ResourceBundle resources;
    @FXML
    private URL location;
    @FXML
    private Button conDeviceButton;
    @FXML
    private Button secondButton;
    @FXML
    private Button disconDeviceButton;
    @FXML
    private TextArea messageField;

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
        assert conDeviceButton != null : "fx:id=\"firstButton\" was not injected: check your FXML file 'gui.fxml'.";
        assert secondButton != null : "fx:id=\"secondButton\" was not injected: check your FXML file 'gui.fxml'.";
        assert disconDeviceButton != null : "fx:id=\"thirdButton\" was not injected: check your FXML file 'gui.fxml'.";
        serviceFunctions = ServiceFunctions.getInstance(this);
    }

    @FXML
    void disconDeviceButtonAction(ActionEvent event) throws SerialPortException, SerialDeviceException {
        try {
            serialDevice.close();
        } catch (Exception e) {
        }
    }

    @FXML
    void conDeviceButtonAction(ActionEvent event) throws SerialPortException, SerialDeviceException {
        if (serialDevice == null) {
            serialDevice = new SerialDevice();
        }
        try {
            serviceFunctions.printMessage("Подключение устройства", MessageLevel.INFO);
            serialDevice.init(Baudrate.BAUDRATE_115200, new FirstPortReader(this), "IsArduino", "ImArduino", "ClosePhrase");
            serialDevice.addTimer(new FirstCheckTask(this));
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

    public Button getConDeviceButton() {
        return conDeviceButton;
    }

    public Button getSecondButton() {
        return secondButton;
    }

    public Button getDisconDeviceButton() {
        return disconDeviceButton;
    }

    public SerialDevice getSerialDevice() {
        return this.serialDevice;
    }

    public TextArea getMessageField() {
        return messageField;
    }
}