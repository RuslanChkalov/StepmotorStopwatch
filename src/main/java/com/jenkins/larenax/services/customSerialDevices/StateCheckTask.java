package com.jenkins.larenax.services.customSerialDevices;

import com.jenkins.larenax.MainController;

import java.util.TimerTask;


public class StateCheckTask extends TimerTask {

    private final MainController mainController;

    public StateCheckTask(MainController mainController) {
        this.mainController = mainController;
    }

    /**
     * On-timer-tick method. Runs in a separate thread. Intended to be used to check SerialDevice status.
     */
    @Override
    public void run() {
        System.out.println(mainController.getSerialDevice().isOpened());
    }
}
