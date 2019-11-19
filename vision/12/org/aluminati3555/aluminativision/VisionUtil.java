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

package org.aluminati3555.aluminativision;

import java.util.concurrent.locks.LockSupport;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

/**
 * Utility methods for vision processing
 * 
 * @author Caleb Heydon
 */
public class VisionUtil {
	/**
	 * Returns the area of the bounding box of a contour
	 * 
	 * @param contour
	 * @return
	 */
	public static double computeBoxArea(MatOfPoint contour) {
		Rect rect = Imgproc.boundingRect(contour);
		double area = (double) (rect.width) * rect.height;

		return area;
	}

	/**
	 * Computes the density of the contour from the box area and the pixels used
	 * (between 0 and 1)
	 * 
	 * @param contour
	 * @return
	 */
	public static double computeDensity(MatOfPoint contour) {
		return Imgproc.contourArea(contour) / computeBoxArea(contour);
	}

	/**
	 * Gets the current time in seconds
	 * 
	 * @return
	 */
	public static double getTime() {
		return (System.nanoTime() / 1000000000.0);
	}

	/**
	 * Computes the width of the quadrilateral
	 * 
	 * @param a
	 * @param b
	 * @param c
	 * @param d
	 * @return
	 */
	public static double computeQuadrilateralWidth(Point upperLeft, Point upperRight, Point lowerLeft,
			Point lowerRight) {
		return Math.max(upperRight.x - upperLeft.x, lowerRight.x - lowerLeft.x);
	}

	/**
	 * Computes the height of the quadrilateral
	 * 
	 * @param a
	 * @param b
	 * @param c
	 * @param d
	 * @return
	 */
	public static double computeQuadrilateralHeight(Point upperLeft, Point upperRight, Point lowerLeft,
			Point lowerRight) {
		return Math.max(lowerLeft.y - upperLeft.y, lowerLeft.y - upperLeft.y);
	}

	/**
	 * Draws a quadrilateral
	 * 
	 * @param mat
	 * @param upperLeft
	 * @param upperRight
	 * @param lowerLeft
	 * @param lowerRight
	 * @return
	 */
	public static void drawQuadrilateral(Mat mat, int thickness, Point upperLeft, Point upperRight, Point lowerLeft,
			Point lowerRight) {
		Imgproc.line(mat, upperLeft, upperRight, new Scalar(255, 0, 0), thickness);
		Imgproc.line(mat, upperRight, lowerRight, new Scalar(255, 0, 0), thickness);
		Imgproc.line(mat, lowerLeft, lowerRight, new Scalar(255, 0, 0), thickness);
		Imgproc.line(mat, upperLeft, lowerLeft, new Scalar(255, 0, 0), thickness);
	}

	/**
	 * Computes the area of the quadrilateral
	 * 
	 * @param a
	 * @param b
	 * @param c
	 * @param d
	 * @return
	 */
	public static double computeQuadrilateralArea(Point upperLeft, Point upperRight, Point lowerLeft,
			Point lowerRight) {
		double height = computeQuadrilateralHeight(upperLeft, upperRight, lowerLeft, lowerRight);
		double area1 = 0.5 * (lowerRight.x - lowerLeft.x) * height;
		double area2 = 0.5 * (upperRight.x - upperLeft.x) * height;

		return area1 + area2;
	}

	/**
	 * Resizes a frame
	 * 
	 * @param mat
	 * @param width
	 * @param height
	 */
	public static void resize(Mat mat, int width, int height) {
		Imgproc.resize(mat, mat, new Size(width, height));
	}

	/**
	 * Sleeps for seconds
	 * 
	 * @param time
	 */
	public static void sleep(double time) {
		LockSupport.parkNanos(Math.round(time * 1000000000));
	}

	/**
	 * Returns a camera stream for the selected camera
	 * 
	 * @param host
	 * @param id
	 * @return
	 */
	public static String getCameraStream(String host, int id) {
		if (id == 0  && HardwareConfig.getConfig().numberOfCameras > 0) {
			return "http://" + host + ":" + ServerConfig.getConfig().cameraServerPort1;
		} else if (id == 1 && HardwareConfig.getConfig().numberOfCameras > 1) {
			return "http://" + host + ":" + ServerConfig.getConfig().cameraServerPort2;
		} else {
			return null;
		}
	}
}
