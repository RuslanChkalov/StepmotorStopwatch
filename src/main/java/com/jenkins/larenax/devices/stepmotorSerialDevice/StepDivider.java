package com.jenkins.larenax.devices.stepmotorSerialDevice;

/**
 * Motor step division factor. Configured by stepper motor driver.
 */
public enum StepDivider {
    FULL_STEP(1),
    HALF_OF_STEP(2),
    DIVIDE_BY_4(4),
    DIVIDE_BY_8(8),
    DIVIDE_BY_16(16),
    DIVIDE_BY_32(32),
    DIVIDE_BY_64(64),
    DIVIDE_BY_128(128),
    DIVIDE_BY_256(256);


    public final int value;

    StepDivider(int value) {
        this.value = value;
    }

}
