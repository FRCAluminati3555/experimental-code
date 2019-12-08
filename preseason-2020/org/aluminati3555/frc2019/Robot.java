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

import org.aluminati3555.lib.auto.AluminatiAutoTask;
import org.aluminati3555.lib.data.AluminatiData;
import org.aluminati3555.lib.drivers.AluminatiMotorGroup;
import org.aluminati3555.lib.drivers.AluminatiJoystick;
import org.aluminati3555.lib.drivers.AluminatiPigeon;
import org.aluminati3555.lib.loops.Loop;
import org.aluminati3555.lib.loops.Looper;
import org.aluminati3555.lib.net.AluminatiTunable;
import org.aluminati3555.lib.drivers.AluminatiRelay;
import org.aluminati3555.lib.drivers.AluminatiTalonSRX;
import org.aluminati3555.lib.drivers.AluminatiVictorSPX;
import org.aluminati3555.lib.pneumatics.AluminatiCompressor;
import org.aluminati3555.lib.pneumatics.AluminatiDoubleSolenoid;
import org.aluminati3555.lib.robot.AluminatiRobot;
import org.aluminati3555.lib.trajectoryfollowingmotion.AluminatiRobotStateEstimator;
import org.aluminati3555.lib.trajectoryfollowingmotion.RobotState;
import org.aluminati3555.lib.util.AluminatiUtil;
import org.aluminati3555.lib.vision.AluminatiCameraHelper;
import org.aluminati3555.lib.vision.AluminatiLimelight;

import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Ultrasonic;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import com.team254.lib.geometry.Pose2d;
import com.team254.lib.geometry.Rotation2d;

import org.aluminati3555.frc2019.auto.ModeDoNothing;
import org.aluminati3555.frc2019.auto.ModeExamplePath;
import org.aluminati3555.frc2019.auto.ModeExampleTurn;
import org.aluminati3555.frc2019.auto.ModeGrabHatch;
import org.aluminati3555.frc2019.auto.ModePlaceHatch;
import org.aluminati3555.frc2019.systems.CargoSystem;
import org.aluminati3555.frc2019.systems.ClimberSystem;
import org.aluminati3555.frc2019.systems.DriveSystem;
import org.aluminati3555.frc2019.systems.HatchSystem;

/**
 * This is the main class of the robot
 * 
 * @author Caleb Heydon
 */
public class Robot extends AluminatiRobot {
  // Constants
  public static final String[] AUTO_MODES = { "Manual", "DoNothing", "ExampleTurn", "PlaceHatch", "GrabHatch",
      "ExamplePath" };

  // Robot state
  private RobotMode robotMode;
  private RobotState robotState;
  private AluminatiAutoTask autoTask;

  private boolean matchStarted;

  // Looper
  private Looper looper;

  // Robot state estimator
  private AluminatiRobotStateEstimator robotStateEstimator;

  // Power distribution
  private PowerDistributionPanel pdp;

  // Joystick
  private AluminatiJoystick driverJoystick;
  private AluminatiJoystick operatorJoystick;

  // Systems
  private DriveSystem driveSystem;
  private AluminatiLimelight limelight;
  private AluminatiCompressor compressor;
  private ClimberSystem climberSystem;
  private CargoSystem cargoSystem;
  private HatchSystem hatchSystem;

  @Override
  public void robotInit() {
    // Configure pid
    AluminatiData.velocityKF = 0.3;
    AluminatiData.velocityKP = 0.2;
    AluminatiData.velocityKI = 0.0001;
    AluminatiData.velocityKD = 0.25;

    // Add UDP listener for PID
    new AluminatiTunable(5805) {
      protected void update(TuningData data) {
        AluminatiData.velocityKP = data.kP;
        AluminatiData.velocityKI = data.kI;
        AluminatiData.velocityKD = data.kP;
      }
    };

    // Configure pure pursuit
    AluminatiData.pathFollowingProfileKP = 5;
    AluminatiData.pathFollowingProfileKI = 0.0001;
    AluminatiData.inertiaSteeringGain = 1;
    AluminatiData.pathFollowingProfileKV = 0.1;
    AluminatiData.pathFollowingProfileKS = 0.1;

    AluminatiData.pathFollowingMaxVel = 113;
    AluminatiData.pathFollowingMaxAccel = 108;

    AluminatiUtil.generatePathFollowingFeedforwardValues();

    // Add udp listener for pure pursuit
    new AluminatiTunable(5806) {
      protected void update(TuningData data) {
        AluminatiData.pathFollowingProfileKP = data.kP;
        AluminatiData.pathFollowingProfileKI = data.kI;
      }
    };

    // Set encoder data
    AluminatiData.encoderUnitsPerRotation = 4096;

    // Set robot physical constants
    AluminatiData.wheelDiamater = 5.75;
    AluminatiData.driveWidth = 21.868;

    // Set thread priority
    Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
    Thread.currentThread().setName("Robot-Thread");

    // Set default robot state and mode
    robotState = new RobotState();
    robotMode = RobotMode.OPERATOR_CONTROL;

    // Setup looper
    looper = new Looper();

    // Disable LiveWindow telemetry
    LiveWindow.disableAllTelemetry();

    // Setup pdp
    pdp = new PowerDistributionPanel();
    pdp.clearStickyFaults();

    // Setup joysticks
    driverJoystick = new AluminatiJoystick(1);
    operatorJoystick = new AluminatiJoystick(0);

    // Configure systems
    configureSystems();

    // Setup robot state estimator
    robotStateEstimator = new AluminatiRobotStateEstimator(robotState, driveSystem);
    looper.register(robotStateEstimator);

    // Setup data reporter
    looper.register(new DataReporter());

    // Start looper
    looper.start();

    // Start camera
    AluminatiCameraHelper.start(0);

    // Display auto modes on dashboard
    sendAutoModes();
  }

  @Override
  public void robotPeriodic() {

  }

  @Override
  public void disabledInit() {

  }

  @Override
  public void disabledPeriodic() {
    // Use brake mode if connected to the driverstation/fms
    if (DriverStation.getInstance().isDSAttached() || DriverStation.getInstance().isFMSAttached()) {
      driveSystem.brake();
    } else {
      // Use coast for vision calibration
      driveSystem.coast();
    }

    // Zero gyro if waiting for match to start
    if (!matchStarted) {
      driveSystem.getGyro().setHeading(Rotation2d.fromDegrees(0));
    }

    // Update systems
    double timestamp = Timer.getFPGATimestamp();
    driveSystem.update(timestamp, false);
    climberSystem.update(timestamp, false);
    cargoSystem.update(timestamp, false);
    hatchSystem.update(timestamp, false);
  }

  @Override
  public void autonomousInit() {
    matchStarted = true;

    // Set brake mode
    driveSystem.brake();

    // Stop auto task if one is running
    if (autoTask != null) {
      autoTask.stop();
    }

    // Put limelight into camera mode and put leds back in pipeline mode
    limelight.setPipeline(1);
    limelight.setLEDMode(AluminatiLimelight.LEDMode.CURRENT_PIPELINE);

    double timestamp = Timer.getFPGATimestamp();

    // Reset robot state
    robotState.reset(timestamp, new Pose2d(0, 0, Rotation2d.fromDegrees(0)), driveSystem);

    loadAutoMode();
    if (autoTask != null) {
      robotMode = RobotMode.AUTONOMOUS;
      autoTask.start(timestamp);
    }
  }

  @Override
  public void autonomousPeriodic() {
    double timestamp = Timer.getFPGATimestamp();

    autoControl(timestamp);

    // Update systems
    boolean enabled = (robotMode == RobotMode.OPERATOR_CONTROL);

    driveSystem.update(timestamp, enabled);
    climberSystem.update(timestamp, enabled);
    cargoSystem.update(timestamp, enabled);
    hatchSystem.update(timestamp, enabled);
  }

  @Override
  public void teleopInit() {
    if (!matchStarted) {
      // Put limelight into camera mode and put leds back in pipeline mode if
      // autonomousInit() was not called first
      limelight.setPipeline(1);
      limelight.setLEDMode(AluminatiLimelight.LEDMode.CURRENT_PIPELINE);
    }

    matchStarted = true;
  }

  @Override
  public void teleopPeriodic() {
    double timestamp = Timer.getFPGATimestamp();

    autoControl(timestamp);

    // Update systems
    boolean enabled = (robotMode == RobotMode.OPERATOR_CONTROL);

    driveSystem.update(timestamp, enabled);
    climberSystem.update(timestamp, enabled);
    cargoSystem.update(timestamp, enabled);
    hatchSystem.update(timestamp, enabled);
  }

  @Override
  public void testInit() {
    driveSystem.coast();

    // Turn on limelight for testing
    limelight.setPipeline(0);

    // Set control mode
    robotMode = RobotMode.OPERATOR_CONTROL;
  }

  @Override
  public void testPeriodic() {
    teleopPeriodic();
  }

  /**
   * Configures the robot systems
   */
  private void configureSystems() {
    // Setup drivetrain
    AluminatiMotorGroup left = new AluminatiMotorGroup(new AluminatiTalonSRX(41), new AluminatiTalonSRX(43));
    AluminatiMotorGroup right = new AluminatiMotorGroup(true, new AluminatiTalonSRX(42), new AluminatiTalonSRX(44));
    AluminatiPigeon gyro = new AluminatiPigeon((AluminatiTalonSRX) left.getMotors()[1]);

    left.getMaster().setSensorPhase(true);
    right.getMaster().setSensorPhase(true);
    driveSystem = new DriveSystem(looper, robotState, left, right, gyro, driverJoystick);

    // Setup limelight
    limelight = new AluminatiLimelight();
    limelight.setPipeline(0);
    limelight.setLEDMode(AluminatiLimelight.LEDMode.OFF);

    // Setup compressor
    compressor = new AluminatiCompressor();
    compressor.start();

    // Setup climber
    climberSystem = new ClimberSystem(new AluminatiTalonSRX(45), new DigitalInput(9), new AluminatiDoubleSolenoid(4, 5),
        driverJoystick, operatorJoystick);

    // Setup cargo handler
    cargoSystem = new CargoSystem(new AluminatiVictorSPX(46), new AluminatiVictorSPX(47), new Ultrasonic(1, 0),
        new AluminatiDoubleSolenoid(6, 7), new AluminatiRelay(0), operatorJoystick);

    // Setup hatch mechanism
    hatchSystem = new HatchSystem(new AluminatiDoubleSolenoid(2, 3), new AluminatiDoubleSolenoid(0, 1), driveSystem,
        limelight, driverJoystick, operatorJoystick);
  }

  /**
   * Sends the auto modes to the dashboard
   */
  private void sendAutoModes() {
    NetworkTableInstance.getDefault().getTable("SmartDashboard").getEntry("Auto List").setStringArray(AUTO_MODES);
  }

  /**
   * Loads the selected auto into autoTask
   */
  private void loadAutoMode() {
    String auto = NetworkTableInstance.getDefault().getTable("SmartDashboard").getEntry("Auto Selector")
        .getString("Manual");

    if (auto.equals(AUTO_MODES[0])) {
      // Manual

      autoTask = null;
    } else if (auto.equals(AUTO_MODES[1])) {
      // DoNothing

      autoTask = new ModeDoNothing();
    } else if (auto.equals(AUTO_MODES[2])) {
      // ExampleTurn

      autoTask = new ModeExampleTurn(driveSystem);
    } else if (auto.equals(AUTO_MODES[3])) {
      // PlaceHatch

      autoTask = new ModePlaceHatch(driveSystem, hatchSystem, limelight);
    } else if (auto.equals(AUTO_MODES[4])) {
      // GrabHatch

      autoTask = new ModeGrabHatch(driveSystem, hatchSystem, limelight);
    } else if (auto.equals(AUTO_MODES[5])) {
      // ExamplePath

      autoTask = new ModeExamplePath(robotState, driveSystem);
    }
  }

  /**
   * Controls the robot during auto
   */
  private void autoControl(double timestamp) {
    if (driverJoystick.getRawButtonPressed(11)) {
      // Stop task and cleanup
      autoTask.stop();
      robotMode = RobotMode.OPERATOR_CONTROL;

      // Put drive in coast mode
      driveSystem.coast();
    }

    if (robotMode == RobotMode.OPERATOR_CONTROL) {
      driveSystem.coast();

      // Set limelight mode
      if (driverJoystick.getRawButton(1)) {
        // Enable if trigger is down
        limelight.setPipeline(0);
      } else {
        limelight.setPipeline(1);
      }
    } else if (autoTask != null) {
      if (autoTask.isComplete()) {
        // Stop task and cleanup
        autoTask.stop();
        robotMode = RobotMode.OPERATOR_CONTROL;

        // Put drive in coast mode
        driveSystem.coast();
      } else {
        autoTask.update(timestamp);
      }
    }
  }

  private enum RobotMode {
    AUTONOMOUS, OPERATOR_CONTROL
  }

  private class DataReporter implements Loop {
    public void onStart(double timestamp) {

    }

    public void onLoop(double timestamp) {
      // Do not report data if connected to the fms
      if (!DriverStation.getInstance().isFMSAttached()) {
        SmartDashboard.putNumber("leftPower", driveSystem.getLeftGroup().getMaster().getMotorOutputPercent());
        SmartDashboard.putNumber("rightPower", driveSystem.getRightGroup().getMaster().getMotorOutputPercent());
      }
    }

    public void onStop(double timestamp) {

    }

    public String getName() {
      return "[DataReporter]";
    }
  }
}
