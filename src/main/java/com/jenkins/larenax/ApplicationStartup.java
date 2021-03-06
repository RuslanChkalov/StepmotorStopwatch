package com.jenkins.larenax;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javafx.scene.image.Image;


public class ApplicationStartup extends Application {

    private static final Logger logger = LogManager.getLogger(ApplicationStartup.class);
    private MainController mainController;

    public static void main(String[] args) {
        logger.info("Entering application.");
        launch(args);
    }

    /**
     * GLOBAL Method for GUI init
     */
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui.fxml"));
        Parent root = loader.load();
        mainController = loader.getController();
        stage.setTitle("Mixer");
        stage.getIcons().add(new Image(String.valueOf(getClass().getResource("/icon.png"))));
        stage.setResizable(false);
        Scene scene = new Scene(root, 562, 530);
        scene.getStylesheets().add(String.valueOf(getClass().getResource("/stylesheet.css")));
        stage.setScene(scene);
        stage.setOnCloseRequest(e -> Platform.exit());
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
    public void stop() {
        mainController.onApplicationClose();
        logger.info("Application Stopped");
        System.exit(0);
    }

}