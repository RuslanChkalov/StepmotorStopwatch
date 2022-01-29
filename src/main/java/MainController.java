import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import jssc.SerialPortException;
import services.customSerialDevices.Baudrate;
import services.customSerialDevices.SerialDevice;
import services.customSerialDevices.exceptions.SerialDeviceException;
import services.customSerialDevices.readers.FirstWorkingReader;

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
    void firstButtonAction(ActionEvent event) throws SerialPortException, SerialDeviceException {
        if (serialDevice == null) {
            serialDevice = new SerialDevice();
        }
        try {
            serialDevice.init(Baudrate.BAUDRATE_115200, new FirstWorkingReader(), "IsNema", "IsNema");
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

    @FXML
    void initialize() {
        assert firstButton != null : "fx:id=\"firstButton\" was not injected: check your FXML file 'gui.fxml'.";
        assert secondButton != null : "fx:id=\"secondButton\" was not injected: check your FXML file 'gui.fxml'.";

    }

}