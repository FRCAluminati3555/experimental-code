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

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Translate;

/**
 * This class is the robot object shown on the screen
 * 
 * @author Caleb Heydon
 */
public class Robot extends Group {
	private Rectangle robot;
	private Rectangle cover;
	
	// Dimensions
	private double length;
	private double width;
	
	/**
	 * Returns the length of the robot in inches
	 * @return
	 */
	public double getRobotLength() {
		return length;
	}
	
	/**
	 * Returns the width of the robot in inches
	 * @return
	 */
	public double getRobotWidth() {
		return width;
	}
	
	/**
	 * Returns the robot rectangle
	 * @return
	 */
	public Rectangle getRobot() {
		return robot;
	}
	
	/**
	 * Highlights this robot
	 */
	public void select() {
		robot.setFill(Color.GREENYELLOW);
	}
	
	/**
	 * Unselects the robot
	 */
	public void unselect() {
		robot.setFill(Color.RED);
	}
	
	/**
	 * Returns true if this robot is selected
	 * @return
	 */
	public boolean isSelected() {
		return (robot.getFill() == Color.GREENYELLOW);
	}
	
	/**
	 * Sets the dimensions of the robot
	 * @param length
	 * @param width
	 */
	public void setDimensions(double length, double width) {
		robot.setWidth(length * Field.ratio);
		robot.setHeight(width * Field.ratio);
		
		cover.setWidth(length * Field.ratio - 10);
		cover.setHeight(width * Field.ratio - 10);
	}
	
	public Robot(double length, double width) {
		this.length = length;
		this.width = width;
		
		robot = new Rectangle(length * Field.ratio, width * Field.ratio);
		robot.setArcWidth(10);
		robot.setArcHeight(10);
		robot.setFill(Color.RED);
		robot.setOpacity(0.65);
		this.getChildren().add(robot);
		
		cover = new Rectangle(length * Field.ratio - 10, width * Field.ratio - 10);
		cover.setTranslateX(5);
		cover.setTranslateY(5);
		cover.rotateProperty().bind(robot.rotateProperty());
		cover.setFill(Color.DARKBLUE);
		cover.setOpacity(0.65);
		this.getChildren().add(cover);
		
		Translate translate = new Translate();
		translate.setX(0);
		translate.yProperty().bind(robot.heightProperty().divide(-2));
		this.getTransforms().add(translate);
	}
}
