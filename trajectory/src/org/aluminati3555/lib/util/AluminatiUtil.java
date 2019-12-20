package org.aluminati3555.lib.util;

import com.team254.lib.physics.DCMotorTransmission;
import com.team254.lib.physics.DifferentialDrive;

import org.aluminati3555.lib.data.AluminatiData;

/**
 * This class provides utilities
 * 
 * @author Caleb Heydon
 */
public class AluminatiUtil {
    /**
     * This function converts encoder ticks to inches travelled
     * 
     * @param x
     * @param wheelDiameter
     * @param encoderTicks
     * @return
     */
    public static double inchesToEncoderTicks(double x, double wheelDiameter, int encoderTicks) {
        double circumference = Math.PI * wheelDiameter;
        return (x / circumference) * encoderTicks;
    }

    /**
     * This function converts inches to meters
     */
    public static double inchesToMeters(double inches) {
        return inches * 0.0254;
    }

    /**
     * This function returns the drivetrain model
     */
    public static DifferentialDrive getDriveModel() {
        DCMotorTransmission transmission = new DCMotorTransmission(
                1 / AluminatiData.kV, inchesToMeters(AluminatiData.wheelDiamater / 2)
                        * inchesToMeters(AluminatiData.wheelDiamater / 2) / (2 * AluminatiData.kA),
                AluminatiData.vIntercept);

        DifferentialDrive model = new DifferentialDrive(AluminatiData.linearInertia, AluminatiData.angularInertia,
                AluminatiData.angularDrag, inchesToMeters(AluminatiData.wheelDiamater / 2),
                inchesToMeters(AluminatiData.driveWidth / 2 * AluminatiData.scrubFactor), transmission, transmission);

        return model;
    }

    /**
     * Converts rotations to inches
     */
    public static double rotationsToInches(double rotations) {
        return rotations* (AluminatiData.wheelDiamater * Math.PI);
    }

    /**
     * Converts rpm to inches per second
     */
    public static double rpmToInchesPerSecond(double rpm) {
        return rotationsToInches(rpm) / 60;
    }

    /**
     * Converts rpm to native units
     */
    public static int convertRPMToNativeUnits(double rpm) {
		return (int) (rpm * AluminatiData.encoderUnitsPerRotation / 600.0);
	}

    /**
     * Converts native units to rpm
     */
	public static int convertNativeUnitsToRPM(double nativeUnits) {
		return (int) (nativeUnits / AluminatiData.encoderUnitsPerRotation * 600.0);
    }
    
    /**
     * Converts inches to rotations
     */
    public static double inchesToRotations(double inches) {
        return inches / (AluminatiData.wheelDiamater * Math.PI);
    }

    /**
     * Converts inches per second to rpm
     */
    public static double inchesPerSecondToRPM(double inchesPerSecond) {
        return inchesToRotations(inchesPerSecond) * 60;
    }

    /**
     * Updates the path following feedforward values
     */
    public static void generatePathFollowingFeedforwardValues() {
        AluminatiData.pathFollowingProfileKFFV = 1 / AluminatiData.pathFollowingMaxVel;
		AluminatiData.pathFollowingProfileKFFA = 1 / AluminatiData.pathFollowingMaxAccel;
    }
}
