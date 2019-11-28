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

/**
 * License for original code:
 *
 * Copyright (c) 2018 Team319
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

package com.team319.follower;

import com.ctre.phoenix.motion.BufferedTrajectoryPointStream;
import com.ctre.phoenix.motion.TrajectoryPoint;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FollowerType;
import com.team319.follower.FollowsArc;
import com.team319.follower.SrxMotionProfile;
import com.team319.follower.SrxTrajectory;

import org.aluminati3555.lib.data.AluminatiData;
import org.aluminati3555.lib.drivers.AluminatiTalonSRX;

public class FollowArc {
	public static final int DISTANCE_PID_SLOT = 0;
	public static final int ROTATION_PID_SLOT = 1;

	private SrxTrajectory trajectory;
	private boolean flipLeftAndRight;
	private boolean flipRobot;
	private BufferedTrajectoryPointStream buffer = new BufferedTrajectoryPointStream();
	private FollowsArc drivetrain;

	public FollowArc(FollowsArc drivetrain, SrxTrajectory trajectory) {
		this(drivetrain, trajectory, false, false);
	}

	public FollowArc(FollowsArc drivetrain, SrxTrajectory trajectory, boolean flipLeftAndRight, boolean flipRobot) {
		this.drivetrain = drivetrain;
		this.trajectory = trajectory;
		this.flipLeftAndRight = flipLeftAndRight;
		this.flipRobot = flipRobot;
	}

	public void start() {
		setUpTalon(drivetrain.getLeft());
		setUpTalon(drivetrain.getRight());

		loadBuffer(trajectory, drivetrain.getDistance(), flipLeftAndRight, flipRobot);

		drivetrain.getLeft().follow(drivetrain.getRight(), FollowerType.AuxOutput1);
		drivetrain.getRight().startMotionProfile(buffer, 10, ControlMode.MotionProfileArc);
	}

	public boolean isFinished() {
		return drivetrain.getRight().isMotionProfileFinished();
	}

	public void end() {
		resetTalon(drivetrain.getRight(), ControlMode.PercentOutput, 0);
		resetTalon(drivetrain.getLeft(), ControlMode.PercentOutput, 0);
	}

	public void interrupt() {
		drivetrain.getRight().clearMotionProfileTrajectories();
		resetTalon(drivetrain.getRight(), ControlMode.PercentOutput, 0);
		resetTalon(drivetrain.getLeft(), ControlMode.PercentOutput, 0);
	}

	// set up the talon for motion profile control
	private void setUpTalon(AluminatiTalonSRX talon) {
		talon.clearMotionProfileTrajectories();
		talon.changeMotionControlFramePeriod(5);
		talon.clearMotionProfileHasUnderrun(10);
	}

	// set the to the desired controlMode
	// used at the end of the motion profile
	private void resetTalon(AluminatiTalonSRX talon, ControlMode controlMode, double setValue) {
		talon.clearMotionProfileTrajectories();
		talon.clearMotionProfileHasUnderrun(10);
		talon.changeMotionControlFramePeriod(10);
		talon.set(controlMode, setValue);
	}

	private void loadBuffer(SrxTrajectory trajectory, double startPosition, boolean flipLeftAndRight,
			boolean flipRobot) {
		TrajectoryPoint point = new TrajectoryPoint(); // temp for for loop, since unused params are initialized
														// automatically, you can alloc just one

		/* clear the buffer, in case it was used elsewhere */
		buffer.Clear();
		SrxMotionProfile profile = trajectory.centerProfile;

		double direction = flipRobot ? -1 : 1;
		double flippedLeftAndRight = flipLeftAndRight ? -1 : 1;
		double[][] path = profile.points;
		/* Insert every point into buffer, no limit on size */
		for (int i = 0; i < profile.numPoints; ++i) {
			/* for each point, fill our structure and pass it to API */
			point.timeDur = (int) path[i][2];

			/* drive part */
			point.position = direction * path[i][0] + startPosition;
			point.velocity = direction * path[i][1];
			point.arbFeedFwd = 0;

			/* turn part */
			point.auxiliaryPos = flippedLeftAndRight * AluminatiData.pigeonTurnUnitsPerDegree * (path[i][3]);
			point.auxiliaryVel = 0;
			point.auxiliaryArbFeedFwd = 0;

			point.profileSlotSelect0 = DISTANCE_PID_SLOT;
			point.profileSlotSelect1 = ROTATION_PID_SLOT;
			point.zeroPos = false;
			point.isLastPoint = ((i + 1) == profile.numPoints);
			point.useAuxPID = true;

			buffer.Write(point);
		}
	}
}
