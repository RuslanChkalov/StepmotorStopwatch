package com.jenkins.larenax.devices.stepmotorSerialDevice;

import com.jenkins.larenax.devices.customSerialDevice.Baudrate;
import com.jenkins.larenax.devices.customSerialDevice.PortReader;
import com.jenkins.larenax.devices.customSerialDevice.SerialDevice;
import com.jenkins.larenax.devices.customSerialDevice.exceptions.SerialDeviceException;
import jssc.SerialPortException;

import java.util.ArrayList;
import java.util.List;

public class StepmotorController extends SerialDevice {

    private int divider;
    private double angularDisplacement;

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
    public void rotateClockwise(double speedInAnglesPerSecond) throws SerialPortException, SerialDeviceException {
        double speedInStepsPerSecond = divider * speedInAnglesPerSecond / angularDisplacement;
        String frequency = String.valueOf(Math.round(speedInStepsPerSecond));
        this.write(frequency + "|0");
    }

    /**
     * Endless rotation starting method, conterclockwise direction. The real direction is determined by connecting the motor windings to driver.
     */
    public void rotateConterclockwise(double speedInAnglesPerSecond) throws SerialPortException, SerialDeviceException {
        rotateClockwise(-speedInAnglesPerSecond);
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
        this.write("0|0");
    }
}
