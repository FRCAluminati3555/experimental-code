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

import org.aluminati3555.lib.auto.AluminatiAutoTask;
import org.aluminati3555.lib.drivers.AluminatiJoystick;
import org.aluminati3555.lib.pneumatics.AluminatiDoubleSolenoid;
import org.aluminati3555.lib.system.AluminatiSystem;
import org.aluminati3555.lib.vision.AluminatiLimelight;

import edu.wpi.first.wpilibj.Timer;
import org.aluminati3555.frc2019.auto.ActionGrabHatch;
import org.aluminati3555.frc2019.auto.ActionPlaceHatch;
import org.aluminati3555.frc2019.controllers.TurnInPlaceController;

/**
 * This class controls the hatch mechanism on the robot
 * 
 * @author Caleb Heydon
 */
public class HatchSystem implements AluminatiSystem {
    private AluminatiDoubleSolenoid extender;
    private AluminatiDoubleSolenoid clamper;

    private DriveSystem driveSystem;
    private TurnInPlaceController controller;
    private AluminatiLimelight limelight;

    private AluminatiJoystick driverJoystick;
    private AluminatiJoystick operatorJoystick;

    // Mechanism action
    private AluminatiAutoTask action;

    public void update(double timestamp, boolean enabled) {
        if (enabled) {
            // Is the vision system in use
            if (driverJoystick.getRawButton(1)) {
                limelightControl();
            } else {
                driveSystem.setUsingLimelight(false);
            }

            if (operatorJoystick.getRawButtonPressed(4) | driverJoystick.getRawButtonPressed(6)) {
                // Start placing hatch

                // Stop an action if there is one running
                if (action != null && !action.isComplete()) {
                    action.stop();
                }

                // Create new action
                action = new ActionPlaceHatch(this);
                action.start(timestamp);
            } else if (operatorJoystick.getRawButtonReleased(4) | driverJoystick.getRawButtonReleased(6)) {
                // Advance state machine
                if (action != null) {
                    action.advanceState();
                }
            }

            if (operatorJoystick.getRawButtonPressed(6) | driverJoystick.getRawButtonPressed(7)) {
                // Start grabbing hatch

                // Stop an action if there is one running
                if (action != null && !action.isComplete()) {
                    action.stop();
                }

                // Create new action
                action = new ActionGrabHatch(this);
                action.start(timestamp);
            } else if (operatorJoystick.getRawButtonReleased(6) | driverJoystick.getRawButtonReleased(7)) {
                // Advance state machine
                if (action != null) {
                    action.advanceState();
                }
            }

            if (action != null && !action.isComplete()) {
                action.update(timestamp);
            }
        }
    }

    /**
     * Extends the mechanism
     */
    public void extend() {
        extender.reverse();
    }

    /**
     * Retracts the mechanism
     */
    public void retract() {
        extender.forward();
    }

    /**
     * Closes the claw
     */
    public void clamp() {
        clamper.reverse();
    }

    /**
     * Opens the claw
     */
    public void release() {
        clamper.forward();
    }

    /**
     * Drives the robot using the limelight
     */
    public void limelightControl() {
        if (limelight.hasTarget()) {
            // Disable normal driving
            driveSystem.setUsingLimelight(true);
            double x = limelight.getX();

            // Note that only the y joystick value is squared here
            driveSystem.manualArcadeDrive(-controller.update(0, x, Timer.getFPGATimestamp()),
                    driveSystem.isInverted() ? -driverJoystick.getSquaredY() : driverJoystick.getSquaredY());
        } else {
            driveSystem.setUsingLimelight(false);
        }
    }

    public HatchSystem(AluminatiDoubleSolenoid extender, AluminatiDoubleSolenoid clamper, DriveSystem driveSystem,
            AluminatiLimelight limelight, AluminatiJoystick driverJoystick, AluminatiJoystick operatorJoystick) {
        this.extender = extender;
        this.clamper = clamper;
        this.driveSystem = driveSystem;
        this.limelight = limelight;
        this.driverJoystick = driverJoystick;
        this.operatorJoystick = operatorJoystick;

        this.controller = new TurnInPlaceController(0.014, 0, 0.2, 0.35, 0.16);
    }
}
