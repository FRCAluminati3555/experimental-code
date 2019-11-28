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

package org.aluminati3555.frc2019.controllers;

/**
 * This is a simple pid controller
 * 
 * @author Caleb Heydon
 */
public class PIDTurnController {
    private double kP;
    private double kI;
    private double kD;

    private double allowableError;
    private double minOutput;
    private double maxOutput;
    private double maxTime;
    private double startTime;
    
    private double integral;
    private double lastError;

    /**
     * Returns the last error
     * 
     * @return
     */
    public double getLastError() {
        return lastError;
    }

    /**
     * Updates the controller and returns the output
     * 
     * @param setPoint
     * @param currentValue
     * @return
     */
    public double update(double setPoint, double currentValue, double currentTime) {
        double error = setPoint - currentValue;

        if (Math.abs(error) <= allowableError && Math.abs(lastError) <= allowableError) {
            return 0;
        }

        // Calculate PID
        double p = error * kP;

        integral += error;
        double i = integral * kI;

        double d = (error - lastError) * kD;

        // Save error
        lastError = error;

        double output = p + i + d;

        if (output < 0 && output > -minOutput) {
            output = -minOutput;
        } else if (output > 0 && output < minOutput) {
            output = minOutput;
        }

        if (output < -maxOutput) {
            output = -maxOutput;
        } else if (output > maxOutput) {
            output = maxOutput;
        }

        if (maxTime > 0) {
            // Stop pid loop if it is taking too long. This will signal whatever is using
            // the controller to stop

            if (currentTime >= (startTime + maxTime)) {
                output = 0;
            }
        }

        return output;
    }

    public PIDTurnController(double kP, double kI, double kD, double allowableError, double minOutput, double maxOutput,
            double maxTime, double startTime) {
        this.kP = kP;
        this.kI = kI;
        this.kD = kD;

        this.allowableError = allowableError;
        this.minOutput = minOutput;
        this.maxOutput = maxOutput;
        this.maxTime = maxTime;

        this.startTime = startTime;
    }
}
