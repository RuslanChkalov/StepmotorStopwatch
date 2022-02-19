package com.jenkins.larenax.devices.stepmotorSerialDevice;

/**
 * Rotation angle of motor full step.
 */
public enum AngularDisplacement {
    DEGREE_1_8(1.8),
    DEGREE_0_9(0.9),
    DEGREE_7_5(7.5);

    public final double value;

    AngularDisplacement(double value) {
        this.value = value;
    }

}