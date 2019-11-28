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

package org.aluminati3555.frc2019;

import java.util.ArrayList;

import com.team254.lib.geometry.Pose2d;
import com.team254.lib.geometry.Pose2dWithCurvature;
import com.team254.lib.geometry.Rotation2d;
import com.team254.lib.physics.DifferentialDrive;
import com.team254.lib.trajectory.timing.DifferentialDriveDynamicsConstraint;
import com.team254.lib.trajectory.timing.TimingConstraint;

import org.aluminati3555.lib.trajectory.AluminatiTrajectory;
import org.aluminati3555.lib.util.AluminatiUtil;

import edu.wpi.first.wpilibj.Timer;

/**
 * This class generates the trajectories for the robot
 * 
 * @author Caleb Heydon
 */
public class TrajectoryGenerator {
    // Constants
    public static final double MAX_VELOCITY = 113;
    public static final double MAX_ACCELERATION = 108;
    public static final double DT = 0.01;

    private DifferentialDrive driveModel;
    private ArrayList<TimingConstraint<Pose2dWithCurvature>> driveConstraints;

    public AluminatiTrajectory exampleTrajectory;

    /**
     * Returns the drive model in use
     */
    public DifferentialDrive getDriveModel() {
        return driveModel;
    }

    private void generateExampleTrajectory() {
        ArrayList<Pose2d> waypoints = new ArrayList<Pose2d>();
        waypoints.add(new Pose2d(0, 0, Rotation2d.fromDegrees(0)));
        waypoints.add(new Pose2d(36, 24, Rotation2d.fromDegrees(0)));

        boolean reversed = false;
        boolean flipped = false;
        double startVelocity = 0;
        double endVelocity = 0;

        exampleTrajectory = new AluminatiTrajectory(waypoints, null, flipped, reversed, startVelocity, endVelocity,
                MAX_VELOCITY, MAX_ACCELERATION, DT);
    }

    /**
     * Generates the robot's trajectories
     */
    public void generateTrajectories() {
        double startTime = Timer.getFPGATimestamp();

        generateExampleTrajectory();

        double endTime = Timer.getFPGATimestamp();

        System.out.println("Trajectories generated in " + (endTime - startTime) + " seconds");
    }

    public TrajectoryGenerator() {
        driveModel = AluminatiUtil.getDriveModel();

        driveConstraints = new ArrayList<TimingConstraint<Pose2dWithCurvature>>();
        driveConstraints.add(new DifferentialDriveDynamicsConstraint<Pose2dWithCurvature>(driveModel, 12));
    }
}
