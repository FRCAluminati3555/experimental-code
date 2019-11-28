package org.aluminati3555.lib.util;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.RemoteSensorSource;
import com.ctre.phoenix.motorcontrol.SensorTerm;
import com.ctre.phoenix.motorcontrol.StatusFrame;
import com.team254.lib.physics.DCMotorTransmission;
import com.team254.lib.physics.DifferentialDrive;
import com.team319.follower.FollowArc;

import org.aluminati3555.lib.data.AluminatiData;
import org.aluminati3555.lib.drivers.AluminatiPigeon;
import org.aluminati3555.lib.drivers.AluminatiTalonSRX;

/**
 * This class provides utilities
 * 
 * @author Caleb Heydon
 */
public class AluminatiUtil {
    /**
     * This method configures a talon for use with trajectory following. Note that
     * you still need to configure the sensor phase. See BobTrajectory wiki.
     * 
     * @param left  The left master talon
     * @param right The right master talon
     * @param gyro
     */
    public static void configTalonsTrajectoryFollowing(AluminatiTalonSRX left, AluminatiTalonSRX right,
            AluminatiPigeon gyro) {
        // Configure left talon
        left.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder);
        left.setStatusFramePeriod(StatusFrame.Status_2_Feedback0, 5);

        // Configure right talon (master)
        right.configRemoteFeedbackFilter(left.getDeviceID(), RemoteSensorSource.TalonSRX_SelectedSensor, 0);
        right.configRemoteFeedbackFilter(gyro.getDeviceID(), RemoteSensorSource.GadgeteerPigeon_Yaw, 1);

        right.configSensorTerm(SensorTerm.Sum0, FeedbackDevice.RemoteSensor0);
        right.configSensorTerm(SensorTerm.Sum1, FeedbackDevice.QuadEncoder);
        right.configSelectedFeedbackSensor(FeedbackDevice.SensorSum, FollowArc.DISTANCE_PID_SLOT, 0);
        right.configSelectedFeedbackCoefficient(0.5, FollowArc.DISTANCE_PID_SLOT, 0);

        right.configSelectedFeedbackSensor(FeedbackDevice.RemoteSensor1, FollowArc.ROTATION_PID_SLOT, 0);
        right.configSelectedFeedbackCoefficient(AluminatiData.pigeonTurnUnitsPerDegree, FollowArc.ROTATION_PID_SLOT, 0);

        // Configure pid for trajectory following
        right.config_kF(FollowArc.DISTANCE_PID_SLOT, AluminatiData.encoderF);
        right.config_kP(FollowArc.DISTANCE_PID_SLOT, AluminatiData.encoderP);
        right.config_kI(FollowArc.DISTANCE_PID_SLOT, AluminatiData.encoderI);
        right.config_kD(FollowArc.DISTANCE_PID_SLOT, AluminatiData.encoderD);
        right.config_IntegralZone(FollowArc.DISTANCE_PID_SLOT, AluminatiData.iZone);

        right.config_kF(FollowArc.ROTATION_PID_SLOT, AluminatiData.gyroF);
        right.config_kP(FollowArc.ROTATION_PID_SLOT, AluminatiData.gyroP);
        right.config_kI(FollowArc.ROTATION_PID_SLOT, AluminatiData.gyroI);
        right.config_kD(FollowArc.ROTATION_PID_SLOT, AluminatiData.gyroD);
        right.config_IntegralZone(FollowArc.ROTATION_PID_SLOT, AluminatiData.iZone);
    }

    /**
     * Config the talons for path following
     * 
     * @param left
     * @param right
     */
    public static void configTalonsPathFollowing(AluminatiTalonSRX left, AluminatiTalonSRX right) {
        // Configure left talon
        left.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder);
        left.setStatusFramePeriod(StatusFrame.Status_2_Feedback0, 5);

        // Configure right talon for velocity mode
        right.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, 0);
        right.setStatusFramePeriod(StatusFrame.Status_2_Feedback0, 5);

        // Configure pid for velocity mode (used in path following)
        left.config_kF(0, AluminatiData.encoderF);
        left.config_kP(0, AluminatiData.encoderP);
        left.config_kI(0, AluminatiData.encoderI);
        left.config_kD(0, AluminatiData.encoderD);
        left.config_IntegralZone(0, AluminatiData.iZone);

        right.config_kF(0, AluminatiData.encoderF);
        right.config_kP(0, AluminatiData.encoderP);
        right.config_kI(0, AluminatiData.encoderI);
        right.config_kD(0, AluminatiData.encoderD);
        right.config_IntegralZone(0, AluminatiData.iZone);
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
     * This function converts encoder ticks to inches travelled
     * 
     * @param x
     * @return
     */
    public static double inchesToEncoderTicks(double x) {
        double circumference = Math.PI * AluminatiData.wheelDiamater;
        return (x / circumference) * AluminatiData.encoderUnitsPerRotation;
    }

    /**
     * This function converts inches to meters
     */
    public static double inchesToMeters(double inches) {
        return inches * 0.0254;
    }

    /**
     * Converts rotations to inches
     */
    public static double rotationsToInches(double rotations) {
        return rotations * (AluminatiData.wheelDiamater * Math.PI);
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
