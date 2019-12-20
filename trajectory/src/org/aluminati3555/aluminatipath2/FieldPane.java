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

package org.aluminati3555.aluminatipath2;

import java.text.DecimalFormat;
import java.util.ArrayList;

import org.aluminati3555.lib.data.AluminatiData;
import org.aluminati3555.lib.trajectory.AluminatiTrajectory;

import com.team254.lib.geometry.Pose2d;
import com.team254.lib.geometry.Rotation2d;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;

/**
 * This class provides the basis for the field component of the gui
 * 
 * @author Caleb Heydon
 */
public class FieldPane extends BorderPane {
	// The constant name of the field image file
	public static final String FIELD_IMAGE = "field.png";

	// The image object of the field
	private Image field;

	private ImageView fieldView;
	private PropertiesPane propertiesPane;

	// List of robots
	private ArrayList<Robot> robots;

	// Properties
	private SimpleDoubleProperty lengthValue = new SimpleDoubleProperty(39);
	private SimpleDoubleProperty widthValue = new SimpleDoubleProperty(33);

	private double startVelocityValue = 0;
	private double endVelocityValue = 0;
	private double maxVelocityValue = 100;
	private double maxAccelerationValue = 100;
	private double dtValue = 0.01;

	// Current trajectory
	private AluminatiTrajectory currentTrajectory;

	/**
	 * This method returns the image of the field
	 * 
	 * @return
	 */
	public Image getField() {
		return field;
	}

	/**
	 * Unselects all robots
	 */
	public void unselectAll() {
		for (int i = 0; i < robots.size(); i++) {
			robots.get(i).unselect();
		}
	}

	/**
	 * Generates an info string for the current trajectory
	 * 
	 * @return
	 */
	public String getTrajectoryInfoString() {
		if (currentTrajectory == null || robots.size() < 2) {
			return null;
		}

		String info = "";

		info += "AluminatiData.wheelDiameter = " + AluminatiData.wheelDiamater + ";\n";
		info += "AluminatiData.encoderUnitsPerRotation = " + AluminatiData.encoderUnitsPerRotation + ";\n\n";

		info += "boolean reversed = " + propertiesPane.isReversed() + ";\n";
		info += "double startAngle = " + -robots.get(0).getRobot().getRotate() + ";\n";
		info += "double startVelocity = " + startVelocityValue + ";\n";
		info += "double endVelocity = " + endVelocityValue + ";\n";
		info += "double maxVelocity = " + maxVelocityValue + ";\n";
		info += "double maxAcceleration = " + maxAccelerationValue + ";\n\n";

		for (int i = 0; i < currentTrajectory.getWaypoints().size(); i++) {
			DecimalFormat decimalFormat = new DecimalFormat("#####.##");

			String x = decimalFormat.format(currentTrajectory.getWaypoints().get(i).getTranslation().x());
			String y = decimalFormat.format(currentTrajectory.getWaypoints().get(i).getTranslation().y()
					- (robots.get(i).getRobot().getHeight() / 2.0) / Field.ratio);
			String angle = decimalFormat.format(currentTrajectory.getWaypoints().get(i).getRotation().getDegrees());

			info += "new Pose2d(" + x + ", " + y + ", Rotation2d.fromDegrees(" + angle + "));\n";
		}

		return info;
	}

	/**
	 * Adds a robot
	 * 
	 * @param e
	 */
	public void addRobot(MouseEvent e) {
		Robot lastRobot;
		if (robots.size() == 0) {
			lastRobot = null;
		} else {
			lastRobot = robots.get(robots.size() - 1);
		}

		Robot robot = new Robot(lengthValue.doubleValue(), widthValue.doubleValue());
		robot.setTranslateX(e.getSceneX() - robot.getRobot().getWidth() / 2);
		robot.getRobot().widthProperty().addListener((a, b, c) -> {
			robot.setTranslateX(e.getSceneX() + b.doubleValue() / 2);
			robot.setTranslateX(e.getSceneX() - c.doubleValue() / 2);
		});
		robot.setTranslateY(e.getSceneY());
		if (lastRobot != null) {
			double angle = 0;
			if (!propertiesPane.isReversed()) {
				angle = (lastRobot.getTranslateX() < robot.getTranslateX()) ? 0 : 180;
			} else {
				angle = (lastRobot.getTranslateX() < robot.getTranslateX()) ? 180 : 0;
			}

			robot.getRobot().setRotate(-angle);
		}
		robot.setOnMouseClicked(e1 -> {
			if (e1.getButton() == MouseButton.PRIMARY) {
				unselectAll();
				propertiesPane.select(robot);
				robot.select();
			} else if (e1.getButton() == MouseButton.SECONDARY) {
				if (robot.isSelected()) {
					propertiesPane.reset();
				}
				robots.remove(robot);
				robot.unselect();
				render();
			}
		});

		unselectAll();
		propertiesPane.select(robot);
		robot.select();
		robots.add(robot);
		render();
	}

	/**
	 * This method updates the paths on the screen
	 */
	public void updatePaths() {
		if (robots.size() > 1) {
			ArrayList<Pose2d> waypoints = new ArrayList<Pose2d>();

			for (int i = 0; i < robots.size(); i++) {
				double xValue = (robots.get(i).getTranslateX() + robots.get(i).getRobot().getWidth() / 2.0)
						/ Field.ratio;
				double yValue = (this.getHeight() - robots.get(i).getTranslateY()
						+ robots.get(i).getRobot().getHeight() / 2.0) / Field.ratio;
				double angleValue = -robots.get(i).getRobot().getRotate();

				waypoints.add(new Pose2d(xValue, yValue, Rotation2d.fromDegrees(angleValue)));
			}

			currentTrajectory = new AluminatiTrajectory(waypoints, null, false, propertiesPane.isReversed(),
					startVelocityValue, endVelocityValue, maxVelocityValue, maxAccelerationValue, dtValue);

			for (int i = 0; i < currentTrajectory.getXPoints().size(); i++) {
				Ellipse ellipse = new Ellipse(5, 5);
				ellipse.setFill(Color.GREENYELLOW);

				ellipse.setTranslateX(currentTrajectory.getXPoints().get(i) * Field.ratio);
				ellipse.setTranslateY(this.getHeight() - currentTrajectory.getYPoints().get(i) * Field.ratio
						+ robots.get(0).getRobot().getHeight() / 2);

				ellipse.setOnMouseClicked(e -> {
					if (e.getSceneX() <= field.getWidth()) {
						addRobot(e);
					}
				});

				if (ellipse.getTranslateX() <= field.getWidth()) {
					this.getChildren().add(ellipse);
				}
			}

			int ms = 0;
			for (int i = 0; i < currentTrajectory.centerProfile.numPoints; i++) {
				ms += currentTrajectory.centerProfile.points[i][2];
			}

			double time = ms / 1000.0;
			propertiesPane.getTimeLabel().setText("Time: " + time + " (seconds)");
		} else {
			propertiesPane.getTimeLabel().setText("Time: -");
		}
	}

	/**
	 * Updates the dimensions of the robots
	 */
	public void updateRobots() {
		for (int i = 0; i < robots.size(); i++) {
			robots.get(i).setDimensions(lengthValue.doubleValue(), widthValue.doubleValue());
			this.getChildren().add(robots.get(i));
		}
	}

	/**
	 * This method renders the screen
	 */
	public void render() {
		this.getChildren().clear();
		this.setLeft(fieldView);
		this.setRight(propertiesPane);

		updatePaths();
		updateRobots();
	}

	public FieldPane() {
		field = new Image(FIELD_IMAGE);
		robots = new ArrayList<Robot>();

		fieldView = new ImageView(field);
		fieldView.setOnMouseClicked(e -> {
			if (e.getButton() == MouseButton.PRIMARY) {
				addRobot(e);
			}
		});

		propertiesPane = new PropertiesPane();
		propertiesPane.setPadding(new Insets(10, 10, 10, 10));

		render();
	}

	/**
	 * This class provides the properties view
	 * 
	 * @author Caleb Heydon
	 */
	private class PropertiesPane extends VBox {
		// GUI elements
		private TextField length;
		private TextField width;
		private TextField wheelDiameter;
		private TextField encoderTicksPerRevolution;

		private TextField startVelocity;
		private TextField endVelocity;
		private TextField maxVelocity;
		private TextField maxAcceleration;

		private TextField x;
		private TextField y;
		private TextField angle;
		private Label time;

		private CheckBox reversed;

		// Listeners
		private ChangeListener<? super String> xListener;
		private ChangeListener<? super String> yListener;
		private ChangeListener<? super String> angleListener;

		/**
		 * Returns the time label
		 * 
		 * @return
		 */
		public Label getTimeLabel() {
			return time;
		}

		/**
		 * Returns true if the path is reversed
		 * 
		 * @return
		 */
		public boolean isReversed() {
			return reversed.isSelected();
		}

		/**
		 * Resets the properties window when a robot is unselected
		 */
		public void reset() {
			x.setDisable(true);
			y.setDisable(true);
			angle.setDisable(true);

			x.setText("");
			y.setText("");
			angle.setText("");
		}

		/**
		 * Parses the properties from the inputs
		 */
		private void parseProperties() {
			try {
				lengthValue.set(Double.parseDouble(length.getText()));
				widthValue.set(Double.parseDouble(width.getText()));
				AluminatiData.wheelDiamater = Double.parseDouble(wheelDiameter.getText());
				AluminatiData.encoderUnitsPerRotation = Integer.parseInt(encoderTicksPerRevolution.getText());

				startVelocityValue = Double.parseDouble(startVelocity.getText());
				endVelocityValue = Double.parseDouble(endVelocity.getText());

				if (maxVelocityValue > 0 && maxAccelerationValue > 0) {
					maxVelocityValue = Double.parseDouble(maxVelocity.getText());
					maxAccelerationValue = Double.parseDouble(maxAcceleration.getText());

					render();
				}
			} catch (NumberFormatException e) {
				return;
			}
		}

		/**
		 * Selects a robot
		 * 
		 * @param robot
		 */
		public void select(Robot robot) {
			if (xListener != null && yListener != null && angleListener != null) {
				x.textProperty().removeListener(xListener);
				y.textProperty().removeListener(yListener);
				angle.textProperty().removeListener(angleListener);
			}

			xListener = (a, b, c) -> {
				try {
					double value = Double.parseDouble(c) * Field.ratio - robot.getRobot().getWidth() / 2.0;
					robot.setTranslateX(value);
				} catch (NumberFormatException e) {
					return;
				}

				render();
			};

			yListener = (a, b, c) -> {
				try {
					double value = this.getHeight() - (Double.parseDouble(c) * Field.ratio);
					robot.setTranslateY(value);
				} catch (NumberFormatException e) {
					return;
				}

				render();
			};

			angleListener = (a, b, c) -> {
				try {
					double value = Double.parseDouble(c);
					robot.getRobot().setRotate(-value);
				} catch (NumberFormatException e) {
					return;
				}

				render();
			};

			DecimalFormat decimalFormat = new DecimalFormat("#####.##");
			double xValue = (robot.getTranslateX() + robot.getRobot().getWidth() / 2.0) / Field.ratio;
			double yValue = (this.getHeight() - robot.getTranslateY()) / Field.ratio;
			double angleValue = -robot.getRobot().getRotate();

			x.setText(decimalFormat.format(xValue));
			y.setText(decimalFormat.format(yValue));
			if (robot.getRobot().getRotate() == 0) {
				angle.setText("0");
			} else {
				angle.setText("" + decimalFormat.format(angleValue));
			}

			x.textProperty().addListener(xListener);
			y.textProperty().addListener(yListener);
			angle.textProperty().addListener(angleListener);

			x.setDisable(false);
			y.setDisable(false);
			angle.setDisable(false);
		}

		public PropertiesPane() {
			super();

			length = new TextField("" + lengthValue.doubleValue());
			width = new TextField("" + widthValue.doubleValue());
			wheelDiameter = new TextField("" + AluminatiData.wheelDiamater);
			encoderTicksPerRevolution = new TextField("" + AluminatiData.encoderUnitsPerRotation);

			startVelocity = new TextField("" + startVelocityValue);
			endVelocity = new TextField("" + endVelocityValue);
			maxVelocity = new TextField("" + maxVelocityValue);
			maxAcceleration = new TextField("" + maxAccelerationValue);

			ChangeListener<? super String> listener = (a, b, c) -> {
				parseProperties();
			};

			length.textProperty().addListener(listener);
			width.textProperty().addListener(listener);
			wheelDiameter.textProperty().addListener(listener);
			encoderTicksPerRevolution.textProperty().addListener(listener);
			startVelocity.textProperty().addListener(listener);
			endVelocity.textProperty().addListener(listener);
			maxVelocity.textProperty().addListener(listener);
			maxAcceleration.textProperty().addListener(listener);

			this.getChildren().add(new Label("Robot length (in)"));
			this.getChildren().add(length);
			this.getChildren().add(new Label("Robot width (in)"));
			this.getChildren().add(width);
			this.getChildren().add(new Label("Wheel diameter (in)"));
			this.getChildren().add(wheelDiameter);
			this.getChildren().add(new Label("Encoder ticks/rev"));
			this.getChildren().add(encoderTicksPerRevolution);
			this.getChildren().add(new Label("Start velocity (in/s)"));
			this.getChildren().add(startVelocity);
			this.getChildren().add(new Label("End velocity (in/s)"));
			this.getChildren().add(endVelocity);
			this.getChildren().add(new Label("Max velocity (in/s)"));
			this.getChildren().add(maxVelocity);
			this.getChildren().add(new Label("Max acceleration (in/s^2)"));
			this.getChildren().add(maxAcceleration);

			x = new TextField();
			y = new TextField();
			angle = new TextField();

			x.setDisable(true);
			y.setDisable(true);
			angle.setDisable(true);

			this.getChildren().add(new Label("X (inches)"));
			this.getChildren().add(x);
			this.getChildren().add(new Label("Y (inches)"));
			this.getChildren().add(y);
			this.getChildren().add(new Label("Angle (degrees)"));
			this.getChildren().add(angle);

			time = new Label("Time: -");
			this.getChildren().add(time);

			reversed = new CheckBox("Reversed");
			reversed.selectedProperty().addListener((a, b, c) -> {
				parseProperties();
			});
			this.getChildren().add(reversed);

			Button displayButton = new Button("Display Output");
			displayButton.setOnMouseClicked(e -> {
				new OutputWindow(getTrajectoryInfoString()).show();
			});
			this.getChildren().add(displayButton);
		}
	}
}
