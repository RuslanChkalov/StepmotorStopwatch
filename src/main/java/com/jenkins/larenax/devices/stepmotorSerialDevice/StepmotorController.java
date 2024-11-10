package com.jenkins.larenax.devices.stepmotorSerialDevice;

import com.jenkins.larenax.devices.customSerialDevice.Baudrate;
import com.jenkins.larenax.devices.customSerialDevice.PortReader;
import com.jenkins.larenax.devices.customSerialDevice.SerialDevice;
import com.jenkins.larenax.devices.customSerialDevice.exceptions.SerialDeviceException;
import jssc.SerialPortException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class StepmotorController extends SerialDevice {

    private final int divider;
    private final double angularDisplacement;
    private boolean softStartProcess;
    private SoftStartParams softStartParams;

    /**
     * {@link StepmotorController} constructor.
     *
     * @param angularDisplacement Rotation angle of motor full step.
     * @param stepDivider         Motor step division factor. Configured by stepper motor driver.
     */
    public StepmotorController(AngularDisplacement angularDisplacement, StepDivider stepDivider) {
        this.divider = stepDivider.value;
        this.angularDisplacement = angularDisplacement.value;
    }

    /**
     * {@link StepmotorController} constructor.
     *
     * @param pulsePerRev         Stepper motor driver characteristics. Step division variation. Describes the number of steps the motor will take in one complete revolution.
     * @param angularDisplacement Rotation angle of motor full step.
     */
    public StepmotorController(int pulsePerRev, AngularDisplacement angularDisplacement) {
        // pulsePerRev is set by manufacturers for a step of 1.8 degrees. Automatically recalculated for the given step value.
        this.divider = (int) (pulsePerRev / (200 * 1.8 / angularDisplacement.value));
        this.angularDisplacement = angularDisplacement.value;
    }

    /**
     * {@link StepmotorController} init-method, looking for a correct COM-port by code phrase.
     * Allows to attach custom Reader to EventListener.
     *
     * @param baudrate           Serial Device {@link Baudrate}.
     * @param portReader         {@link PortReader} receiver event for bidirectional connection. Can be null for unidirectional connection.
     * @param sendCodePhrase     Message sent to device to establish connection.
     * @param expectedCodePhrase Message expected from the device to establish connection.
     * @param closePhrase        Message sent to device before SerialPort is closed. Can be null.
     * @param powerOnActionOnly  Apply voltage to stepper motor only at the time of operation.
     * @param powerSupplyControl The device will send a message containing the status of power circuit.
     */
    public void init(
            Baudrate baudrate,
            PortReader portReader,
            String sendCodePhrase,
            String expectedCodePhrase,
            String closePhrase,
            boolean powerOnActionOnly,
            boolean powerSupplyControl
    ) {
        List<String> paramsOnConnection = new ArrayList<>();
        if (powerOnActionOnly) {
            paramsOnConnection.add("powerOnAction");
        }
        if (powerSupplyControl) {
            paramsOnConnection.add("powerSupplyControl");
        }
        super.init(baudrate, portReader, sendCodePhrase, expectedCodePhrase, closePhrase, paramsOnConnection);
    }

    /**
     * Endless rotation starting method, clockwise direction. The real direction is determined by connecting the motor windings to driver.
     */
    public void rotateClockwise(double speedInAnglesPerSecond, boolean softStart) throws SerialPortException, SerialDeviceException {
        if (softStart) {
            try {
                softStartMethod(speedInAnglesPerSecond);
            } catch (Exception e) {
            }
        } else {
            this.write(speedToStringConvert(speedInAnglesPerSecond));
        }
    }

    /**
     * Endless rotation starting method, conterclockwise direction. The real direction is determined by connecting the motor windings to driver.
     */
    public void rotateConterclockwise(double speedInAnglesPerSecond, boolean softStart) throws SerialPortException, SerialDeviceException {
        if (softStart) {
            try {
                softStartMethod(-speedInAnglesPerSecond);
            } catch (Exception e) {
            }
        } else {
            this.write(speedToStringConvert(-speedInAnglesPerSecond));
        }
    }

    /**
     * Rotate at fixed angle, clockwise direction. The real direction is determined by connecting the motor windings to driver.
     */
    public void rotateClockwiseAtAngle(double angle, double speedInAnglesPerSecond) throws SerialPortException, SerialDeviceException {
        double speedInStepsPerSecond = divider * speedInAnglesPerSecond / angularDisplacement;
        String frequency = String.valueOf(Math.round(speedInStepsPerSecond));
        double pulseNumber = divider * angle / angularDisplacement;
        String rotateAtNumberOfPulses = String.valueOf(Math.round(pulseNumber));
        this.write(frequency + "|" + rotateAtNumberOfPulses);
    }

    /**
     * Rotate at fixed angle, conterclockwise direction. The real direction is determined by connecting the motor windings to driver.
     */
    public void rotateConterclockwiseAtAngle(double angle, double speedInAnglesPerSecond) throws SerialPortException, SerialDeviceException {
        rotateClockwiseAtAngle(angle, -speedInAnglesPerSecond);
    }

    /**
     * Stop rotation method.
     */
    public void stopRotation() throws SerialPortException, SerialDeviceException {
        softStartProcess = false;
        if (this.isOpened()) {
            this.write("0|0");
        }
    }

    /**
     * The method is designed to gradually increase the speed of the stepper motor according to the soft start algorithm.
     * {@link SoftStartParams} must be set.
     *
     * @param finalSpeed The speed that stepmotor must reach as a result of soft-start method. Given in angles per second.
     */
    private void softStartMethod(double finalSpeed) throws SerialDeviceException {
        if (softStartProcess) {
            throw new SerialDeviceException("Process already started");
        }
        if (softStartParams == null || !softStartParams.isParamsSet()) {
            throw new SerialDeviceException("Soft start parameters not set");
        }

        softStartProcess = true;
        long numberOfSteps = Math.round((Math.abs(finalSpeed) - softStartParams.getStartingSpeed()) / softStartParams.getStep());
        long delay = 1000 * softStartParams.getTimeLimit() / numberOfSteps;

        new Thread(() -> {
            double currentSpeed = softStartParams.getStartingSpeed();
            for (int i = 0; i < numberOfSteps; i++) {
                if (!softStartProcess) {
                    break;
                }
                try {
                    logger.debug(speedToStringConvert(finalSpeed > 0 ? currentSpeed : -currentSpeed));
                    this.write(speedToStringConvert(finalSpeed > 0 ? currentSpeed : -currentSpeed));
                    TimeUnit.MILLISECONDS.sleep(delay);
                    currentSpeed += softStartParams.getStep();
                } catch (Exception e) {
                    logger.debug(e.getMessage());
                }
            }
            try {
                if (softStartProcess) {
                    logger.debug(speedToStringConvert(finalSpeed));
                    this.write(speedToStringConvert(finalSpeed));
                }
            } catch (Exception e) {
                logger.debug(e.getMessage());
            }
        }).start();
    }

    /**
     * {@link SoftStartParams} definition method.
     * Optional, used for {@link #rotateConterclockwise} and {@link #rotateClockwise} methods, if defined with softStart param.
     *
     * @param startingSpeed Starting speed for stepmotor soft-start method. Given in angles per second.
     * @param step          Speed increase per iteration. Given in angles per second.
     * @param timeLimit     The time limit for the stepmotor to reach its final speed. Given in seconds.
     */
    public void setSoftStartParams(double startingSpeed, double step, int timeLimit) {
        this.softStartParams = new SoftStartParams(startingSpeed, step, timeLimit);
    }

    /**
     * {@link SoftStartParams} definition method.
     */
    public void setSoftStartParams(SoftStartParams softStartParams) {
        this.softStartParams = softStartParams;
    }

    /**
     * System method. Speed in angles per second to controller command converter.
     */
    private String speedToStringConvert(double speedInAnglesPerSecond) {
        double speedInStepsPerSecond = divider * speedInAnglesPerSecond / angularDisplacement;
        return (Math.round(speedInStepsPerSecond) + "|0");
    }
}
