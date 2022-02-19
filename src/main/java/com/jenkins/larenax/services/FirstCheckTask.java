package com.jenkins.larenax.services;

import com.jenkins.larenax.MainController;
import com.jenkins.larenax.devices.customSerialDevice.BaseCheckTask;
import com.jenkins.larenax.devices.customSerialDevice.exceptions.SerialDeviceException;
import jssc.SerialPortException;


public class FirstCheckTask extends BaseCheckTask {

    private final MainController mainController;
    private ServiceFunctions serviceFunctions;
    private ButtonsActionImpl buttonsAction;

    public FirstCheckTask(MainController mainController) {
        this.mainController = mainController;
        serviceFunctions = ServiceFunctions.getInstance(this);
        buttonsAction = ButtonsActionImpl.getInstance(this);
    }

    @Override
    public void run() {
        if (mainController.getSerialDevice().isDeviceConnectingProcess()) {
            mainController.getConDeviceButton().setDisable(true);
            mainController.getDisconDeviceButton().setDisable(true);
        } else if (!mainController.getSerialDevice().isOpened()) {
            //--------------CUSTOM-CODE------------------
            try {
                buttonsAction.stopWorkButtonActionImpl();
            } catch (Exception e) {
            }
            //-------------------------------------------
            mainController.getSerialDevice().close();
            return;
        }
        if (mainController.getSerialDevice().isOpened()) {
            mainController.getDisconDeviceButton().setDisable(false);
            mainController.getControlBox().setDisable(false);

        }
        logger.debug(mainController.getSerialDevice().isOpened());
    }

    @Override
    public void closeEvent() {
        mainController.getDisconDeviceButton().setDisable(true);
        mainController.getConDeviceButton().setDisable(false);
        serviceFunctions.printMessage("Устройство не подключено", MessageLevel.WARNING, true); //ToDo разделить функции между экстренным отключением и по кнопке
        logger.debug("FirstCheckTask CloseEvent");
        mainController.getControlBox().setDisable(true);
    }

    @Override
    public void connectedEvent() {
        serviceFunctions.printMessage("Устройство подключено", MessageLevel.SUCCESS, true);
    }
}
