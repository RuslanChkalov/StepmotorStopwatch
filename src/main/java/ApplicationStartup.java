import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class ApplicationStartup extends Application {

    private static final Logger logger = LogManager.getLogger(ApplicationStartup.class);

    /**
     * GLOBAL Method for GUI init
     */
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("gui.fxml"));
        stage.setTitle("Title");
        stage.setScene(new Scene(root, 500, 500));
        stage.show();
    }

    /**
     * GLOBAL Init-method before starting the application
     */
    @Override
    public void init() throws Exception {
        logger.info("Application Started");
        super.init();
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
        launch(args);
    }

}