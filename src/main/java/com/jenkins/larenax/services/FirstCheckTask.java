package com.jenkins.larenax.services;

import com.jenkins.larenax.MainController;
import com.jenkins.larenax.devices.customSerialDevice.BaseCheckTask;


public class FirstCheckTask extends BaseCheckTask {

    private final MainController mainController;
    private ServiceFunctions serviceFunctions;

    public FirstCheckTask(MainController mainController) {
        this.mainController = mainController;
        serviceFunctions = ServiceFunctions.getInstance(this);
    }

    @Override
    public void run() {
        if (mainController.getSerialDevice().isDeviceConnectingProcess()){
            mainController.getConDeviceButton().setDisable(true);
            mainController.getDisconDeviceButton().setDisable(true);
        }
        else if (!mainController.getSerialDevice().isOpened()){
            mainController.getSerialDevice().close();
            return;
        }
        if (mainController.getSerialDevice().isOpened()){
            mainController.getDisconDeviceButton().setDisable(false);
        }
        logger.debug(mainController.getSerialDevice().isOpened());
    }

    @Override
    public void closeEvent(){
        mainController.getDisconDeviceButton().setDisable(true);
        mainController.getConDeviceButton().setDisable(false);
        serviceFunctions.printMessage("Устройство отключено", MessageLevel.WARNING);
        logger.debug("FirstCheckTask CloseEvent");
    }

    @Override
    public void connectedEvent() {
        serviceFunctions.printMessage("Устройство подключено", MessageLevel.SUCCESS);
    }
}
