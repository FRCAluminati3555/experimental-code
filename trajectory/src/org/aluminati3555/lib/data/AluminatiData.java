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
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.aluminati3555.lib.data;

/**
 * This class holds basic information about the robot. The data starts out
 * zeroed and it initialized by the program
 * 
 * WARNING: These default values are for the pigeon gyro and a drivetrain with
 * ctre mag encoders
 * 
 * @author Caleb Heydon
 */
public final class AluminatiData {
    // Library
    public static final int LIBRARY_VERSION = 3;

    // Robot delay
    public static double robotDelay = 0.02; // Seconds

    // PID
    public static double encoderF = 1;
    public static double encoderP = 1;
    public static double encoderI = 0;
    public static double encoderD = 0;

    public static double gyroF = 1;
    public static double gyroP = 1;
    public static double gyroI = 0;
    public static double gyroD = 0;

    public static int primaryPIDSlot = 0;
    public static int auxPIDSlot = 1;

    public static int iZone = 400;
    public static int peakOutput = 1;

    // Deadband
    public static double deadband = 0.00001;

    // Encoders
    public static int encoderUnitsPerRotation = 4096;

    // Pigeon
    public static double pigeonTurnUnitsPerDegree = 8192 / 360.0;

    // TalonSRX minimum firmware version
    public static int minTalonSRXFirmareVersion = 0;

    // VictorSPX minimum firmware version
    public static int minVictorSPXFirmwareVersion = 0;

    // Tipping detection
    public static double minTippingAngle = 10; // Degrees

    // Motion profile generation
    public static double maxDx = 2;
    public static double maxDy = 0.25;
    public static double maxDTheta = Math.toRadians(5);
    public static double wheelDiamater = 4; // Inches

    // Drive characterization
    public static double kV = 0.18; // V/(rad/s^2)
    public static double kA = 0.008; // V/(rad/s)
    public static double vIntercept = 0.3; // V
    public static double linearInertia = 60; // Kg
    public static double angularInertia = 120;
    public static double angularDrag = 0.1;
    public static double scrubFactor = 1;
    public static double driveWidth = 20;  // Inches

    // Pure pursuit
    public static double maxTrackerDistance = 9;
    public static double maxGoalTrackAge = 2.5;
    public static double maxGoalTrackSmoothingTime = 0.5;
    public static double cameraFrameRate = 90;

    public static double pathFollowingMaxVel = 100;
    public static double pathFollowingMaxAccel = 100;

    public static double minLookAhead = 12;
    public static double maxLookAhead = 48;
    public static double minLookAheadSpeed = 12;
    public static double maxLookAheadSpeed = 120;

    public static double inertiaSteeringGain = 1;
    public static double pathFollowingProfileKP = 1;
    public static double pathFollowingProfileKI = 0;
    public static double pathFollowingProfileKV = 0;
    public static double pathFollowingProfileKFFV = 1 / pathFollowingMaxVel;
    public static double pathFollowingProfileKFFA = 1 / pathFollowingMaxAccel;
    public static double pathFollowingProfileKS = 0;
    public static double pathFollowingGoalPosTolerance = 1;
    public static double pathFollowingGoalVelTolerance = 12;
    public static double pathStopSteeringDistance = 12;

    // Loops
    public static double looperDT = 0.01;
}
