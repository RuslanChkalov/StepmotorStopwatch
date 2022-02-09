package com.jenkins.larenax.devices.customSerialDevice;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.TimerTask;


public abstract class BaseCheckTask extends TimerTask {
    public static final Logger logger = LogManager.getLogger(BaseCheckTask.class);

    private Object guiController;

    public BaseCheckTask(Object guiController) {
        this.guiController = guiController;
    }

    public BaseCheckTask() {}

    /**
     * On-timer-tick method. Runs in a separate thread. Intended to be used to check {@link SerialDevice} status.
     */
    @Override
    public void run() {
        logger.debug("BaseCheckTask RunEvent");
    }

    /**
     * Timer-on-close method. Intended for performing actions related to disconnecting of {@link SerialDevice}.
     */
    public void closeEvent() { logger.debug("BaseCheckTask CloseEvent"); }

    /**
     * On-timer-initialised method. Intended for performing actions related to connecting of {@link SerialDevice}.
     */
    public void connectedEvent() { logger.debug("BaseCheckTask ConnectedEvent"); }
}
