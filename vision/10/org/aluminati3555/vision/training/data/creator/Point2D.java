/**
 * FRC Team 3555
 * Deep Space
 */

package org.aluminati3555.vision.training.data.creator;

/**
 * This is a simple utility class to hold the coordinates of one point.
 * 
 * @author Caleb Heydon
 */

public class Point2D {
	// Coordinate values
	double x;
	double y;

	/**
	 * This method returns the x value of the coordinate.
	 * 
	 * @return The x value.
	 */
	public double getX() {
		return x;
	}

	/**
	 * This method returns the y value of the coordinate.
	 * 
	 * @return The y value.
	 */
	public double getY() {
		return y;
	}

	/**
	 * This constructor allows the x and y values to be set.
	 * 
	 * @param x The x value.
	 * @param y The y value.
	 */
	public Point2D(double x, double y) {
		this.x = x;
		this.y = y;
	}
}