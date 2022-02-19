package com.jenkins.larenax.services;

import com.jenkins.larenax.MainController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Base service functions for {@link MainController} realised with Singleton pattern.
 */
public final class ServiceFunctions {

    private static ServiceFunctions instance;
    private final MainController mainController;

    private TimerTask messageFieldCleanTask;
    private Timer messageFieldCleanTimer;

    private ServiceFunctions(Object mainController) {
        this.mainController = (MainController) mainController;
    }

    /**
     * Instance calling method.
     */
    public static ServiceFunctions getInstance(Object mainController) {
        if (instance == null) {
            instance = new ServiceFunctions(mainController);
        }
        return instance;
    }

    /**
     * Print information messages for users.
     * @param text The text displayed to user.
     * @param messageLevel Message purpose.
     * @param cleanMessageField Printed message will be automatically removed from messageField in 3 seconds.
     */
    public void printMessage(String text, MessageLevel messageLevel, Boolean cleanMessageField) {
        switch (messageLevel) {
            case INFO:
                mainController.getMessageField().setStyle("-fx-text-fill: grey");
                break;
            case SUCCESS:
                mainController.getMessageField().setStyle("-fx-text-fill: green");
                break;
            case WARNING:
                mainController.getMessageField().setStyle("-fx-text-fill: red");
                break;
            default:
                new RuntimeException("MessageLevel not found.");
        }
        mainController.getMessageField().setText(text);

        if (messageFieldCleanTask != null) {
            messageFieldCleanTask.cancel();
            messageFieldCleanTask = null;
        }
        if (messageFieldCleanTimer != null) {
            messageFieldCleanTimer.cancel();
            messageFieldCleanTimer = null;
        }
        if (cleanMessageField) {
            messageFieldCleanTask = new TimerTask() {
                public void run() {
                    cleanMessageField();
                }
            };
            messageFieldCleanTimer = new Timer();
            messageFieldCleanTimer.schedule(messageFieldCleanTask, 5000L);
        }
    }

    /**
     * Clean message field.
     */
    public void cleanMessageField() {
        mainController.getMessageField().setText("");
    }

    //ToDo Больше проверок условий для целочисленных значений
    public void textFieldNumericInputOnly(TextField textField) {
        textField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    textField.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
    }
}
