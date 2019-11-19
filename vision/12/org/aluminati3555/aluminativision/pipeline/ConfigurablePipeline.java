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

package org.aluminati3555.aluminativision.pipeline;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

import org.aluminati3555.aluminativision.VisionUtil;
import org.aluminati3555.aluminativision.camera.VisionCamera;
import org.aluminati3555.aluminativision.net.VisionData;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

/**
 * A pipeline that can be configured
 * 
 * @author Caleb Heydon
 */
public class ConfigurablePipeline implements IVisionPipeline {
	private VisionData visionData;
	private PipelineConfig pipelineConfig;

	private Mat blurFrame;
	private Mat thresholdFrame;

	private ArrayList<MatOfPoint> contours;

	private Mat outputFrame;

	/**
	 * Returns the pipeline configuration
	 * 
	 * @return
	 */
	public PipelineConfig getPipelineConfig() {
		return pipelineConfig;
	}

	/**
	 * Gets the vision output
	 */
	public VisionData getOutput() {
		return visionData;
	}

	/**
	 * Sets the configuration
	 * 
	 * @param pipelineConfig
	 */
	public synchronized void setPipelineConfig(PipelineConfig pipelineConfig) {
		this.pipelineConfig = pipelineConfig;
	}
	
	public void updateCamera(VisionCamera visionCamera) {
		try {
			visionCamera.setBrightness(pipelineConfig.brightness);
			//visionCamera.setWhiteBalance(pipelineConfig.whiteBalance);
			//visionCamera.setExposure(pipelineConfig.exposure);
		} catch (IOException e) {
			System.err.println("Error: " + visionCamera.getName() + " does not support the vision pipeline");
			System.exit(-1);
		}
		
		visionData.camera = visionCamera.getID();
	}

	/**
	 * Picks a group of contours for dual target modes
	 */
	public void sortContours(ArrayList<MatOfPoint> contours) {
		if (contours.size() < 2) {
			return;
		}

		contours.sort(new Comparator<MatOfPoint>() {
			public int compare(MatOfPoint contour1, MatOfPoint contour2) {
				double area1 = VisionUtil.computeBoxArea(contour1);
				double area2 = VisionUtil.computeBoxArea(contour2);

				if (area1 < area2) {
					return 1;
				} else if (area1 == area2) {
					return 0;
				} else {
					return -1;
				}
			}
		});
	}

	/**
	 * Processes a frame
	 */
	public Mat process(Mat frame, double fps) {
		visionData.fps = fps;
		visionData.hasTarget = false;
		visionData.targetWidth = 0;
		visionData.targetHeight = 0;
		visionData.targetArea = 0;
		visionData.x = 0;
		visionData.y = 0;

		if (pipelineConfig.pipelineMode == PipelineMode.DRIVER) {
			return frame;
		}

		// Blur
		double kernel = 2 * pipelineConfig.blurRadius + 1;
		Imgproc.blur(frame, blurFrame, new Size(kernel, kernel));

		// Thresholding
		Imgproc.cvtColor(blurFrame, thresholdFrame, Imgproc.COLOR_BGR2HLS);
		Core.inRange(thresholdFrame,
				new Scalar(pipelineConfig.thresholdHueMin, pipelineConfig.thresholdLuminenceMin,
						pipelineConfig.thresholdSaturationMin),
				new Scalar(pipelineConfig.thresholdHueMax, pipelineConfig.thresholdLuminenceMax,
						pipelineConfig.thresholdSaturationMax),
				thresholdFrame);

		// Contours
		contours.clear();

		Mat hierarchy = new Mat();
		Imgproc.findContours(thresholdFrame, contours, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
		hierarchy.release();

		// Filter contours
		for (int i = 0; i < contours.size(); i++) {
			double area = Imgproc.contourArea(contours.get(i)) / (double) (frame.width() * frame.height());

			Rect rect = Imgproc.boundingRect(contours.get(i));
			double ratio = (double) (rect.width) / rect.height;

			double density = VisionUtil.computeDensity(contours.get(i));

			if (area < pipelineConfig.contourAreaMin || area > pipelineConfig.contourAreaMax
					|| ratio < pipelineConfig.contourRatioMin || ratio > pipelineConfig.contourRatioMax
					|| density < pipelineConfig.contourDensityMin || density > pipelineConfig.contourDensityMax) {
				contours.remove(i);
				i--;
			}
		}

		Imgproc.cvtColor(thresholdFrame, outputFrame, Imgproc.COLOR_GRAY2RGB);

		if (contours.size() > 0) {
			Imgproc.drawContours(outputFrame, contours, -1, new Scalar(0, 0, 255), 3);
			sortContours(contours);

			Rect rect1 = Imgproc.boundingRect(contours.get(0));
			Imgproc.rectangle(outputFrame, rect1, new Scalar(0, 255, 0), 3);

			if ((pipelineConfig.targetMode == TargetMode.DUAL_HORIZONTAL
					|| pipelineConfig.targetMode == TargetMode.DUAL_VERTICAL) && contours.size() > 1) {
				visionData.hasTarget = true;

				Rect rect2 = Imgproc.boundingRect(contours.get(1));
				Imgproc.rectangle(outputFrame, rect2, new Scalar(0, 255, 0), 3);

				Point upperLeft = null;
				Point upperRight = null;
				Point lowerLeft = null;
				Point lowerRight = null;

				if (pipelineConfig.targetMode == TargetMode.DUAL_HORIZONTAL) {
					// Properly order the targets
					if (rect2.x < rect1.x) {
						Rect temp = rect1;
						rect1 = rect2;
						rect2 = temp;
					}

					upperLeft = new Point(rect1.x, rect1.y);
					upperRight = new Point(rect2.x + rect2.width, rect2.y);
					lowerLeft = new Point(rect1.x, rect1.y + rect1.height);
					lowerRight = new Point(rect2.x + rect2.width, rect2.y + rect2.height);
				} else {
					// Properly order the targets
					if (rect2.y < rect1.y) {
						Rect temp = rect1;
						rect1 = rect2;
						rect2 = temp;
					}

					upperLeft = new Point(rect1.x, rect1.y);
					upperRight = new Point(rect1.x + rect1.width, rect1.y);
					lowerLeft = new Point(rect2.x, rect2.y + rect2.height);
					lowerRight = new Point(rect2.x + rect2.width, rect2.y + rect2.height);
				}

				VisionUtil.drawQuadrilateral(outputFrame, 3, upperLeft, upperRight, lowerLeft, lowerRight);

				visionData.targetWidth = VisionUtil.computeQuadrilateralWidth(upperLeft, upperRight, lowerLeft,
						lowerRight) / (double) (frame.width() * frame.height());
				visionData.targetHeight = VisionUtil.computeQuadrilateralHeight(upperLeft, upperRight, lowerLeft,
						lowerRight) / (double) (frame.width() * frame.height());
				visionData.targetArea = VisionUtil.computeQuadrilateralArea(upperLeft, upperRight, lowerLeft,
						lowerRight) / (double) (frame.width() * frame.height());
			} else if (pipelineConfig.targetMode == TargetMode.SINGLE) {
				visionData.hasTarget = true;
				visionData.targetWidth = (double) (rect1.width) / frame.width();
				visionData.targetHeight = (double) (rect1.height) / frame.height();
				visionData.targetArea = ((double) rect1.width * rect1.height)
						/ (double) (frame.width() * frame.height());
			}
		}

		return outputFrame;
	}

	public ConfigurablePipeline(PipelineConfig pipelineConfig) {
		setPipelineConfig(pipelineConfig);

		visionData = new VisionData();

		blurFrame = new Mat();
		thresholdFrame = new Mat();

		contours = new ArrayList<MatOfPoint>();

		outputFrame = new Mat();
	}

	public static class PipelineConfig {
		// Pipeline mode
		public PipelineMode pipelineMode = PipelineMode.DRIVER;

		// Camera settings
		public int brightness = 64;
		public int whiteBalance = 6500;
		public int exposure = 5;

		// Processing

		// Blur
		public double blurRadius = 5;

		// Thresholding
		public double thresholdHueMin = 0;
		public double thresholdHueMax = 180;

		public double thresholdLuminenceMin = 0;
		public double thresholdLuminenceMax = 255;

		public double thresholdSaturationMin = 0;
		public double thresholdSaturationMax = 255;

		// Contour filtering
		public double contourAreaMin = 0;
		public double contourAreaMax = 1;

		public double contourRatioMin = 0.5;
		public double contourRatioMax = 1.5;

		public double contourDensityMin = 0;
		public double contourDensityMax = 1;

		// Target mode
		public TargetMode targetMode = TargetMode.SINGLE;
	}

	public enum PipelineMode {
		DRIVER, PROCESSING
	}

	public enum TargetMode {
		SINGLE, DUAL_HORIZONTAL, DUAL_VERTICAL
	}
}
