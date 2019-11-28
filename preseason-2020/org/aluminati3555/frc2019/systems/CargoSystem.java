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

import org.aluminati3555.lib.drivers.AluminatiJoystick;
import org.aluminati3555.lib.drivers.AluminatiRelay;
import org.aluminati3555.lib.drivers.AluminatiVictorSPX;
import org.aluminati3555.lib.pneumatics.AluminatiDoubleSolenoid;
import org.aluminati3555.lib.system.AluminatiSystem;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Ultrasonic;
import edu.wpi.first.wpilibj.Ultrasonic.Unit;

/**
 * This class controls the cargo handler
 * 
 * @author Caleb Heydon
 */
public class CargoSystem implements AluminatiSystem {
    private AluminatiVictorSPX frontMotor;
    private AluminatiVictorSPX backMotor;
    private Ultrasonic ultrasonicSensor;
    private AluminatiDoubleSolenoid pistons;
    private AluminatiRelay light;

    private AluminatiJoystick operatorJoystick;

    public void update(double timestamp, boolean enabled) {
        if (!frontMotor.isOK() || !backMotor.isOK()) {
            DriverStation.reportError("Fault detected in cargo handler", false);
        }

        if (enabled) {
            // Control height and intake
            if (operatorJoystick.getPOV() == 90) {
                pistons.reverse();
            } else if (operatorJoystick.getPOV() == 270) {
                pistons.forward();
            } else if (operatorJoystick.getPOV() == 0) {
                frontMotor.set(ControlMode.PercentOutput, 0.65);
                backMotor.set(ControlMode.PercentOutput, 0.65);
            } else if (operatorJoystick.getPOV() == 180) {
                frontMotor.set(ControlMode.PercentOutput, -0.5);
                backMotor.set(ControlMode.PercentOutput, -0.5);
            } else if (operatorJoystick.getRawButton(11)) {
                if (ultrasonicSensor.pidGet() <= 13) {
                    light.forward();
                    frontMotor.set(ControlMode.PercentOutput, 0);
                    backMotor.set(ControlMode.PercentOutput, 0);
                } else {
                    light.disable();
                    frontMotor.set(ControlMode.PercentOutput, 0);
                    backMotor.set(ControlMode.PercentOutput, 0.5);
                }
            } else {
                frontMotor.set(ControlMode.PercentOutput, 0);
                backMotor.set(ControlMode.PercentOutput, 0);
            }

            if (!operatorJoystick.getRawButton(12)) {
                light.disable();
            }
        } else {
            light.disable();
            frontMotor.set(ControlMode.PercentOutput, 0);
            backMotor.set(ControlMode.PercentOutput, 0);
        }
    }

    public CargoSystem(AluminatiVictorSPX frontMotor, AluminatiVictorSPX backMotor, Ultrasonic ultrasonicSensor,
            AluminatiDoubleSolenoid pistons, AluminatiRelay light, AluminatiJoystick operatorJoystick) {
        this.frontMotor = frontMotor;
        this.backMotor = backMotor;
        this.ultrasonicSensor = ultrasonicSensor;
        this.pistons = pistons;
        this.light = light;
        this.operatorJoystick = operatorJoystick;

        frontMotor.configOpenloopRamp(0.1);
        backMotor.configOpenloopRamp(0.1);

        ultrasonicSensor.setDistanceUnits(Unit.kInches);
        ultrasonicSensor.setAutomaticMode(true);
    }
}
