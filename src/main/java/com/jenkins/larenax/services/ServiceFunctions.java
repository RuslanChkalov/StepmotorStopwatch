package com.jenkins.larenax.services;

import com.jenkins.larenax.MainController;

/**
 * Base service functions for {@link MainController} realised with Singleton pattern.
 */
public final class ServiceFunctions {
    private static ServiceFunctions instance;
    private final MainController mainController;

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

    //ToDo Таймер для затирания сообщений через Н секунд после отрисовки
    /**
     * Print information messages for users.
     */
    public void printMessage(String text, MessageLevel messageLevel) {
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
    }

}
