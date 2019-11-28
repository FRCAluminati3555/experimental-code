/**
 * Copyright (c) 2019 Team 3555
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.aluminati3555.frc2019.auto;

import org.aluminati3555.lib.auto.AluminatiAutoTask;

import org.aluminati3555.frc2019.controllers.PIDTurnController;
import org.aluminati3555.frc2019.systems.DriveSystem;

/**
 * This action turns the robot using the gyro sensor and a pid control loop
 * 
 * @author Caleb Heydon
 */
public class ActionTurnToYaw implements AluminatiAutoTask {
    private static final double MIN_OUTPUT = 0.1;
    private static final double MAX_OUTPUT = 0.5;

    private boolean running;
    private double targetAngle;
    private double maxTime;
    private double delay;

    private double startTime;

    private DriveSystem driveSystem;

    private PIDTurnController controller;

    public void start(double timestamp) {
        this.startTime = timestamp;

        // Creat new PID controller
        this.controller = new PIDTurnController(0.05, 0, 0, 0, MIN_OUTPUT, MAX_OUTPUT, maxTime, timestamp);

        running = true;
    }

    public void update(double timestamp) {
        if (timestamp < startTime + delay) {
            return;
        }

        if (running) {
            double output = -controller.update(targetAngle, driveSystem.getGyro().getYaw(), timestamp);

            // Stop when the robot is near the target
            if (Math.abs(output) == MIN_OUTPUT) {
                output = 0;
            }

            // Check if complete
            if (output == 0) {
                running = false;
            }

            driveSystem.manualArcadeDrive(output, 0);
        }
    }

    public void stop() {
        running = false;
        driveSystem.manualArcadeDrive(0, 0);
    }

    public boolean isComplete() {
        return !running;
    }

    /**
     * This method does nothing in this action
     */
    public void advanceState() {

    }

    public ActionTurnToYaw(double targetAngle, long maxTime, DriveSystem driveSystem) {
        this.targetAngle = targetAngle;
        this.maxTime = maxTime;

        this.driveSystem = driveSystem;
    }
}
