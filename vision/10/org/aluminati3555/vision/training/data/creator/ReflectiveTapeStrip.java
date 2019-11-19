/**
 * FRC Team 3555
 * Deep Space
 */

package org.aluminati3555.vision.training.data.creator;

import java.util.ArrayList;

/**
 * This class holds information about a strip of reflective tape. This only
 * includes the points for the polygon.
 * 
 * @author Caleb Heydon
 */

public class ReflectiveTapeStrip {
	// The points of the polygon
	ArrayList<Point2D> points;

	/**
	 * This method returns an ArrayList with the points of the tape strip.
	 * 
	 * @return The ArrayList with the points.
	 */
	public ArrayList<Point2D> getPoints() {
		return points;
	}

	/**
	 * This constructor is for an empty list of points.
	 */
	public ReflectiveTapeStrip() {
		points = new ArrayList<Point2D>();
	}

	/**
	 * This constructor is for setting the list of points.
	 * 
	 * @param points The ArrayList of points.
	 */
	public ReflectiveTapeStrip(ArrayList<Point2D> points) {
		this.points = points;
	}
}