import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import jssc.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import services.devices.SerialDevice;


public class HelloFX extends Application {

    private static final Logger logger = LogManager.getLogger(HelloFX.class);
    private SerialDevice serialDevice;

    /**
     * GLOBAL Method for GUI init
     */
    @Override
    public void start(Stage stage) {
        String javaVersion = System.getProperty("java.version");
        String javafxVersion = System.getProperty("javafx.version");
        Label l = new Label("Hello, JavaFX " + javafxVersion + ", running on Java " + javaVersion + ".");
        Button button = new Button("My Button");
        button.setOnAction(value ->  {
            try {
                serialDevice.write("K");
            } catch (SerialPortException e) {
                e.printStackTrace();
            }
            System.out.println("3244");
        });
        HBox hbox = new HBox(button, l);

        Scene scene = new Scene(hbox, 200, 100);
//        Scene scene = new Scene(new StackPane(l), 640, 480);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * GLOBAL Init-method before starting the application
     */
    @Override
    public void init() throws Exception {
        logger.info("Application Started");
        super.init();
        serialDevice = new SerialDevice();
        serialDevice.init(3,"IsNema","IsNema");
    }

    /**
     * GLOBAL Method called when the application terminates
     */
    @Override
    public void stop() throws Exception {
        logger.info("Application Stopped");
        super.stop();
    }

    public static void main(String[] args) {
        logger.info("Entering application.");
        launch();
    }

}