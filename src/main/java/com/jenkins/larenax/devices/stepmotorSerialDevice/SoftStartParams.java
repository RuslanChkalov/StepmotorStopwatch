package com.jenkins.larenax.devices.stepmotorSerialDevice;

/**
 * A class defines the parameters of {@link StepmotorController} soft start procedure.
 */
public class SoftStartParams {
    private Double startingSpeed;
    private Double step;
    private Integer timeLimit;

    /**
     * {@link SoftStartParams} not null check method.
     */
    public boolean isParamsSet(){
        return startingSpeed != null && step != null && timeLimit != null;
    }

    /**
     * {@link SoftStartParams} constructor.
     * @param startingSpeed Starting speed for stepmotor soft-start method. Given in angles per second.
     * @param step          Speed increase per iteration. Given in angles per second.
     * @param timeLimit     The time limit for the stepmotor to reach its final speed. Given in seconds.
     */
    public SoftStartParams(Double startingSpeed, Double step, Integer timeLimit) {
        this.startingSpeed = startingSpeed;
        this.step = step;
        this.timeLimit = timeLimit;
    }

    public double getStartingSpeed() {
        return startingSpeed;
    }

    public void setStartingSpeed(double startingSpeed) {
        this.startingSpeed = startingSpeed;
    }

    public double getStep() {
        return step;
    }

    public void setStep(double step) {
        this.step = step;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }
}
