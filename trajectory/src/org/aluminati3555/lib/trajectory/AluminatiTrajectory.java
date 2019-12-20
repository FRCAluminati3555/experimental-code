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

package org.aluminati3555.lib.trajectory;

import java.util.ArrayList;
import java.util.List;

import org.aluminati3555.lib.data.AluminatiData;
import org.aluminati3555.lib.util.AluminatiUtil;

import com.team254.lib.geometry.Pose2d;
import com.team254.lib.geometry.Pose2dWithCurvature;
import com.team254.lib.geometry.Rotation2d;
import com.team254.lib.trajectory.DistanceView;
import com.team254.lib.trajectory.TimedView;
import com.team254.lib.trajectory.Trajectory;
import com.team254.lib.trajectory.TrajectoryIterator;
import com.team254.lib.trajectory.TrajectorySamplePoint;
import com.team254.lib.trajectory.TrajectoryUtil;
import com.team254.lib.trajectory.timing.TimedState;
import com.team254.lib.trajectory.timing.TimingConstraint;
import com.team254.lib.trajectory.timing.TimingUtil;
import com.team319.follower.SrxMotionProfile;
import com.team319.follower.SrxTrajectory;

/**
 * This class generates motion profiles on the robot
 * 
 * @author Caleb Heydon
 */
public class AluminatiTrajectory extends SrxTrajectory {
    // Waypoints
    ArrayList<Pose2d> waypoints;
    ArrayList<Double> x;
    ArrayList<Double> y;

    // Motion profile
    private double[][] centerPoints;

    // Path variables
    private boolean reversed;
    private double startVelocity;
    private double endVelocity;
    private double maxVelocity;
    private double maxAcceleration;
    private double dt;

    @Override
    public String toString() {
        return "[AluminatiTrajectory]";
    }
    
    /**
     * Returns the ArrayList of waypoints
     * 
     * @return
     */
    public ArrayList<Pose2d> getWaypoints() {
        return waypoints;
    }

    /**
     * Returns true if the path is flipped
     */
    public boolean isFlipped() {
        return flipped;
    }

    /**
     * Returns true if the path is reversed
     * 
     * @return
     */
    public boolean isReversed() {
        return reversed;
    }
    
    /**
     * Returns the start velocity
     * 
     * @return
     */
    public double getStartVelocity() {
        return startVelocity;
    }

    /**
     * Returns the end velocity
     */
    public double getEndVelocity() {
        return endVelocity;
    }

    /**
     * Returns the max velocity
     * 
     * @return
     */
    public double getMaxVelocity() {
        return maxVelocity;
    }

    /**
     * Returns the max acceleration
     * 
     * @return
     */
    public double getMaxAcceleration() {
        return maxAcceleration;
    }

    /**
     * Returns the dt
     */
    public double getDT() {
        return dt;
    }
    
    /**
     * Returns the x coordinates
     * @return
     */
    public ArrayList<Double> getXPoints() {
    	return x;
    }
    
    /**
     * Returns the y coordinates
     * @return
     */
    public ArrayList<Double> getYPoints() {
    	return y;
    }

    /**
     * Generate trajectory method from team 254 with modifications. See
     * https://github.com/Team254/FRC-2018-Public/blob/master/src/main/java/com/team254/frc2018/planners/DriveMotionPlanner.java
     * 
     * @param reversed
     * @param waypoints
     * @param constraints
     * @param startVelocity
     * @param endVelocity
     * @param maxVelocity
     * @param maxAcceleration
     * @param maxDx
     * @param maxDy
     * @param maxDTheta
     * @return
     */
    private Trajectory<TimedState<Pose2dWithCurvature>> generateTrajectory(boolean reversed, List<Pose2d> waypoints,
            List<TimingConstraint<Pose2dWithCurvature>> constraints, double startVelocity, double endVelocity,
            double maxVelocity, double maxAcceleration, double maxDx, double maxDy, double maxDTheta) {
        List<Pose2d> waypointsMaybeFlipped = waypoints;
        Pose2d flip = Pose2d.fromRotation(new Rotation2d(-1, 0, false));

        if (reversed) {
            waypointsMaybeFlipped = new ArrayList<>(waypoints.size());
            for (int i = 0; i < waypoints.size(); ++i) {
                waypointsMaybeFlipped.add(waypoints.get(i).transformBy(flip));
            }
        }

        // Create a trajectory from splines.
        Trajectory<Pose2dWithCurvature> trajectory = TrajectoryUtil.trajectoryFromSplineWaypoints(waypointsMaybeFlipped,
                maxDx, maxDy, maxDTheta);

        if (reversed) {
            List<Pose2dWithCurvature> flippedPath = new ArrayList<>(trajectory.length());
            for (int i = 0; i < trajectory.length(); ++i) {
                flippedPath.add(new Pose2dWithCurvature(trajectory.getState(i).getPose().transformBy(flip),
                        -trajectory.getState(i).getCurvature(), trajectory.getState(i).getDCurvatureDs()));
            }
            trajectory = new Trajectory<>(flippedPath);
        }

        List<TimingConstraint<Pose2dWithCurvature>> allConstraints = new ArrayList<>();
        if (constraints != null) {
            allConstraints.addAll(constraints);
        }

        // Generate the timed trajectory.
        Trajectory<TimedState<Pose2dWithCurvature>> timedTrajectory = TimingUtil.timeParameterizeTrajectory(reversed,
                new DistanceView<>(trajectory), maxDx, allConstraints, startVelocity, endVelocity, maxVelocity,
                maxAcceleration);

        return timedTrajectory;
    }

    /**
     * Generates the entire path
     */
    private void generatePath() {
        Trajectory<TimedState<Pose2dWithCurvature>> path = generateTrajectory(reversed, waypoints, null, startVelocity,
                endVelocity, maxVelocity, maxAcceleration, AluminatiData.maxDx, AluminatiData.maxDy,
                AluminatiData.maxDTheta);

        TrajectoryIterator<TimedState<Pose2dWithCurvature>> pathIterator = new TrajectoryIterator<TimedState<Pose2dWithCurvature>>(
                new TimedView<Pose2dWithCurvature>(path));

        ArrayList<Double> position = new ArrayList<Double>();
        ArrayList<Double> velocity = new ArrayList<Double>();
        ArrayList<Double> heading = new ArrayList<Double>();

        boolean first = true;
        while (!pathIterator.isDone()) {
            TrajectorySamplePoint<TimedState<Pose2dWithCurvature>> sample;

            if (first) {
                sample = pathIterator.getSample();
                first = false;
            } else {
                sample = pathIterator.advance(dt);
            }
            
            x.add(sample.state().state().getTranslation().x());
            y.add(sample.state().state().getTranslation().y());

            position.add(AluminatiUtil.inchesToEncoderTicks(sample.state().distance(path.getFirstState()),
                    AluminatiData.wheelDiamater, AluminatiData.encoderUnitsPerRotation));

            velocity.add(AluminatiUtil.inchesToEncoderTicks(sample.state().velocity(), AluminatiData.wheelDiamater,
                    AluminatiData.encoderUnitsPerRotation) / 10);

            heading.add(sample.state().state().getRotation().getDegrees());
        }

        centerPoints = new double[position.size()][4];
        double pointDT = dt * 1000;
        double lastAngle = heading.get(0).doubleValue();
        double accumulator = 0;

        for (int i = 0; i < centerPoints.length; i++) {
        	centerPoints[i][0] = isReversed() ? -position.get(i).doubleValue() : position.get(i).doubleValue();
            centerPoints[i][1] = velocity.get(i).doubleValue();
            centerPoints[i][2] = pointDT;

            double angle = heading.get(i).doubleValue();
            if (Math.abs(angle - lastAngle) > 300) {
            	if (angle >= 0) {
            		accumulator -= 360;
            	} else {
            		accumulator += 360;
            	}
            }
            centerPoints[i][3] = angle + accumulator;

            lastAngle = angle;
        }

        centerProfile = new SrxMotionProfile(centerPoints.length, centerPoints);
    }

    public AluminatiTrajectory(ArrayList<Pose2d> waypoints,
            ArrayList<TimingConstraint<Pose2dWithCurvature>> constraints, boolean flipped, boolean reversed,
            double startVelocity, double endVelocity, double maxVelocity, double maxAcceleration,
            double dt) {
    	x = new ArrayList<Double>();
    	y = new ArrayList<Double>();
    	
        this.waypoints = waypoints;

        this.highGear = true;
        this.flipped = flipped;

        this.reversed = reversed;
        this.startVelocity = startVelocity;
        this.endVelocity = endVelocity;
        this.maxVelocity = maxVelocity;
        this.maxAcceleration = maxAcceleration;
        this.dt = dt;

        generatePath();
    }

    public AluminatiTrajectory(ArrayList<Pose2d> waypoints, double maxVelocity, double maxAcceleration) {
        this(waypoints, null, false, false, 0, 0, maxVelocity, maxAcceleration, 0.01);
    }
}
