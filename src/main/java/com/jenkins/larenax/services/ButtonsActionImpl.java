package com.jenkins.larenax.services;

import com.jenkins.larenax.MainController;
import com.jenkins.larenax.devices.customSerialDevice.exceptions.SerialDeviceException;
import javafx.application.Platform;
import jssc.SerialPortException;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.commons.lang3.time.StopWatch;

import java.util.Timer;
import java.util.TimerTask;

public class ButtonsActionImpl {

    private static ButtonsActionImpl instance;
    private final MainController mainController;

    private TimerTask timerTask;
    private Timer timer;
    private StopWatch stopWatch = new StopWatch();

    /**
     * Custom actions class.
     */
    private ButtonsActionImpl(Object mainController) {
        this.mainController = (MainController) mainController;
    }

    /**
     * Instance calling method.
     */
    public static ButtonsActionImpl getInstance(Object mainController) {
        if (instance == null) {
            instance = new ButtonsActionImpl(mainController);
        }
        return instance;
    }

    public void startWorkButtonActionImpl() throws SerialPortException, SerialDeviceException {
        mainController.getSerialDevice().rotateClockwise(Double.valueOf(mainController.getInputSpeedField().getText())*6);
        mainController.getStartWorkButton().setDisable(true);
        mainController.getStopWorkButton().setDisable(false);
        mainController.getInputSpeedField().setDisable(true);
        stopWatch.start();
        timerTask = new TimerTask() {
            public void run() {
                Platform.runLater(() -> {
                    mainController.getNumericTimerLabel().setText(DurationFormatUtils.formatDuration(stopWatch.getTime(), "HH:mm:ss"));
                });
            }
        };
        timer = new Timer();
        timer.schedule(timerTask, 0, 1000l);
    }

    public void stopWorkButtonActionImpl() throws SerialPortException, SerialDeviceException {
        if (timerTask != null) {
            timerTask.cancel();
        }
        if (timer != null) {
            timer.cancel();
        }
        mainController.getSerialDevice().stopRotation();
        if (stopWatch.isStarted()) {
            stopWatch.stop();
            stopWatch.reset();
        }
        mainController.getStartWorkButton().setDisable(false);
        mainController.getStopWorkButton().setDisable(true);
        mainController.getInputSpeedField().setDisable(false);
    }
}
