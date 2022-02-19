package com.jenkins.larenax;

import com.jenkins.larenax.devices.customSerialDevice.Baudrate;
import com.jenkins.larenax.devices.customSerialDevice.exceptions.SerialDeviceException;
import com.jenkins.larenax.devices.stepmotorSerialDevice.AngularDisplacement;
import com.jenkins.larenax.devices.stepmotorSerialDevice.StepDivider;
import com.jenkins.larenax.devices.stepmotorSerialDevice.StepmotorController;
import com.jenkins.larenax.services.ButtonsActionImpl;
import com.jenkins.larenax.services.FirstCheckTask;
import com.jenkins.larenax.services.MessageLevel;
import com.jenkins.larenax.services.ServiceFunctions;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import jssc.SerialPortException;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for gui.fxml (code structure generates by "Scene Builder" software)
 */
public class MainController {
    private ServiceFunctions serviceFunctions;
    private StepmotorController serialDevice;
    //--------------CUSTOM-CODE------------------
    private ButtonsActionImpl buttonsAction;
    //-------------------------------------------
    @FXML
    private ResourceBundle resources;
    @FXML
    private URL location;
    @FXML
    private Button conDeviceButton;

    @FXML
    private AnchorPane controlBox;

    @FXML
    private Button disconDeviceButton;

    @FXML
    private TextField inputSpeedField;

    @FXML
    private TextArea messageField;

    @FXML
    private Label numericTimerLabel;

    @FXML
    private Button startWorkButton;

    @FXML
    private Button stopWorkButton;

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
        assert conDeviceButton != null : "fx:id=\"conDeviceButton\" was not injected: check your FXML file 'gui.fxml'.";
        assert controlBox != null : "fx:id=\"controlBox\" was not injected: check your FXML file 'gui.fxml'.";
        assert disconDeviceButton != null : "fx:id=\"disconDeviceButton\" was not injected: check your FXML file 'gui.fxml'.";
        assert inputSpeedField != null : "fx:id=\"inputSpeedField\" was not injected: check your FXML file 'gui.fxml'.";
        assert messageField != null : "fx:id=\"messageField\" was not injected: check your FXML file 'gui.fxml'.";
        assert numericTimerLabel != null : "fx:id=\"numericTimerLabel\" was not injected: check your FXML file 'gui.fxml'.";
        assert startWorkButton != null : "fx:id=\"startWorkButton\" was not injected: check your FXML file 'gui.fxml'.";
        assert stopWorkButton != null : "fx:id=\"stopWorkButton\" was not injected: check your FXML file 'gui.fxml'.";
        serviceFunctions = ServiceFunctions.getInstance(this);
        //--------------CUSTOM-CODE------------------
        serviceFunctions.textFieldNumericInputOnly(inputSpeedField);
        buttonsAction = ButtonsActionImpl.getInstance(this);
        //-------------------------------------------
    }

    @FXML
    void disconDeviceButtonAction(ActionEvent event) throws SerialPortException, SerialDeviceException {
        try {
            //--------------CUSTOM-CODE------------------
            buttonsAction.stopWorkButtonActionImpl();
            //-------------------------------------------
            serialDevice.close();
        } catch (Exception e) {
        }
    }

    @FXML
    void conDeviceButtonAction(ActionEvent event) throws SerialPortException, SerialDeviceException {
        if (serialDevice == null) {
            serialDevice = new StepmotorController(AngularDisplacement.DEGREE_7_5, StepDivider.DIVIDE_BY_32);
        }
        try {
            serviceFunctions.printMessage("Подключение устройства", MessageLevel.INFO, false);
            serialDevice.init(Baudrate.BAUDRATE_115200, null, "IsStepper", "ImStepper", "StopStepper", true, false);
            serialDevice.addTimer(new FirstCheckTask(this));
        } catch (Exception e) {
        }
    }

    //--------------CUSTOM-CODE------------------
    @FXML
    void startWorkButtonAction(ActionEvent event) throws SerialPortException, SerialDeviceException {
        buttonsAction.startWorkButtonActionImpl();
    }

    @FXML
    public void stopWorkButtonAction(ActionEvent event) throws SerialPortException, SerialDeviceException {
        buttonsAction.stopWorkButtonActionImpl();
    }
    //--------------------------------------------

    public Button getConDeviceButton() {
        return conDeviceButton;
    }

    public Button getDisconDeviceButton() {
        return disconDeviceButton;
    }

    public StepmotorController getSerialDevice() {
        return this.serialDevice;
    }

    public TextArea getMessageField() {
        return messageField;
    }

    public TextField getInputSpeedField() {
        return inputSpeedField;
    }

    public AnchorPane getControlBox() {
        return controlBox;
    }

    public Button getStartWorkButton() {
        return startWorkButton;
    }

    public Button getStopWorkButton() {
        return stopWorkButton;
    }

    public Label getNumericTimerLabel() {
        return numericTimerLabel;
    }
}