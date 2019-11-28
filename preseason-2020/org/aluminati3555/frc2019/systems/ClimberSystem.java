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

package org.aluminati3555.frc2019.systems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;

import org.aluminati3555.lib.drivers.AluminatiJoystick;
import org.aluminati3555.lib.drivers.AluminatiTalonSRX;
import org.aluminati3555.lib.pneumatics.AluminatiDoubleSolenoid;
import org.aluminati3555.lib.system.AluminatiSystem;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DriverStation;

/**
 * This class controls the robot's climber
 * 
 * @author Caleb Heydon
 */

public class ClimberSystem implements AluminatiSystem {
    private AluminatiTalonSRX motor;
    private DigitalInput limitSwitch;
    private AluminatiDoubleSolenoid pistons;

    private AluminatiJoystick driverJoystick;
    private AluminatiJoystick operatorJoystick;

    private boolean auto = false;

    /**
     * Returns true if the climber is not being controlled by the driver
     */
    public boolean isAuto() {
        return auto;
    }

    /**
     * Set to true while the driver is not in control of the climber
     */
    public void setAuto(boolean auto) {
        this.auto = auto;
    }

    /**
     * Extends the climber pistons
     */
    public void extendPistons() {
        pistons.reverse();
    }

    /**
     * Retracts the climber pistons
     */
    public void retractPistons() {
        pistons.forward();
    }

    public void update(double timestamp, boolean enabled) {
        // Report faults
        if (!motor.isOK()) {
            DriverStation.reportError("Fault detected in climber", false);
        }

        if (enabled) {
            if (!limitSwitch.get() && operatorJoystick.getRawButton(1) && operatorJoystick.getY() > 0) {
                motor.set(ControlMode.PercentOutput, 0);
            } else if (operatorJoystick.getRawButton(1)) {
                motor.set(ControlMode.PercentOutput, operatorJoystick.getY());
            } else {
                motor.set(ControlMode.PercentOutput, 0);
            }

            if (driverJoystick.getRawButtonPressed(9)) {
                // Retract
                retractPistons();
            } else if (driverJoystick.getRawButtonPressed(8)) {
                // Extend
                extendPistons();
            }
        } else if (!auto) {
            motor.set(ControlMode.PercentOutput, 0);
        }
    }

    public ClimberSystem(AluminatiTalonSRX motor, DigitalInput limitSwitch, AluminatiDoubleSolenoid pistons,
            AluminatiJoystick driverJoystick, AluminatiJoystick operatorJoystick) {
        this.motor = motor;
        this.limitSwitch = limitSwitch;
        this.pistons = pistons;
        this.driverJoystick = driverJoystick;
        this.operatorJoystick = operatorJoystick;

        this.motor.configPeakCurrentDuration(500);
        this.motor.configPeakCurrentLimit(20);
        this.motor.configContinuousCurrentLimit(20);
        this.motor.enableCurrentLimit(true);

        this.motor.configSelectedFeedbackSensor(FeedbackDevice.Analog);
        this.motor.config_kF(0, 0);
        this.motor.config_kP(0, 0.1);
        this.motor.config_kI(0, 0);
        this.motor.config_kD(0, 0);
    }
}
