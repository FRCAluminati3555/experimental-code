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

package org.aluminati3555.aluminativision.camera;

import java.io.IOException;

import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

/**
 * This class interfaces with a camera
 * 
 * @author Caleb Heydon
 */
public class VisionCamera {
	private static final int LARGE_NUMBER = 10000;

	private String name;
	private int id;
	private VideoCapture videoCapture;
	private CameraResolution cameraResolution;

	/**
	 * Returns the name of the camera
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the camera id
	 * 
	 * @return
	 */
	public int getID() {
		return id;
	}

	/**
	 * Returns the camera resolution
	 * 
	 * @return
	 */
	public CameraResolution getResolution() {
		return cameraResolution;
	}

	/**
	 * Sets the camera resolution
	 * 
	 * @param resolution
	 * @throws IOException
	 */
	public void setResolution(CameraResolution resolution) throws IOException {
		videoCapture.set(Videoio.CAP_PROP_FRAME_WIDTH, resolution.width);
		videoCapture.set(Videoio.CAP_PROP_FRAME_HEIGHT, resolution.height);

		int actualWidth = (int) videoCapture.get(Videoio.CAP_PROP_FRAME_WIDTH);
		int actualHeight = (int) videoCapture.get(Videoio.CAP_PROP_FRAME_HEIGHT);

		if (actualWidth != resolution.width || actualHeight != resolution.height) {
			throw new IOException("Unsupported resolution");
		}

		cameraResolution = resolution;
	}

	/**
	 * Sets the camera to the maximum supported resolution
	 */
	public void setMaxResolution() {
		// Set the camera to a very large resolution. Then read it back and it should be
		// the largest supported resolution.
		videoCapture.set(Videoio.CAP_PROP_FRAME_WIDTH, LARGE_NUMBER);
		videoCapture.set(Videoio.CAP_PROP_FRAME_HEIGHT, LARGE_NUMBER);

		// It is ok to cast it to an int here because there is not going to be half a
		// pixel
		int width = (int) videoCapture.get(Videoio.CAP_PROP_FRAME_WIDTH);
		int height = (int) videoCapture.get(Videoio.CAP_PROP_FRAME_HEIGHT);

		// Set it to the values again just to be safe
		videoCapture.set(Videoio.CAP_PROP_FRAME_WIDTH, width);
		videoCapture.set(Videoio.CAP_PROP_FRAME_HEIGHT, height);

		cameraResolution = new CameraResolution(width, height);
	}

	/**
	 * Sets the camera fps
	 * 
	 * @param fps
	 */
	public synchronized void setFPS(int fps) throws IOException {
		// Attempt to set it to the target fps
		videoCapture.set(Videoio.CAP_PROP_FPS, fps);

		// Read it back to determine if it was supported
		int actual = (int) videoCapture.get(Videoio.CAP_PROP_FPS);
		if (fps != actual) {
			throw new IOException("Unsupported FPS");
		}
	}

	/**
	 * Sets the camera's exposure
	 * 
	 * @param exposure
	 * @throws IOException
	 */
	public synchronized void setExposure(int exposure) throws IOException {
		// Attempt to set it to the exposure
		videoCapture.set(Videoio.CAP_PROP_EXPOSURE, exposure);

		// Read it back to determine if it was supported
		int actual = (int) videoCapture.get(Videoio.CAP_PROP_EXPOSURE);
		if (exposure != actual) {
			throw new IOException("Unsupported exposure");
		}
	}

	/**
	 * Sets the camera's brightness
	 * 
	 * @param brightness
	 * @throws IOException
	 */
	public synchronized void setBrightness(int brightness) throws IOException {
		// Attempt to set it to the brightness
		videoCapture.set(Videoio.CAP_PROP_BRIGHTNESS, brightness);

		// Read it back to determine if it was supported
		int actual = (int) videoCapture.get(Videoio.CAP_PROP_BRIGHTNESS);
		if (brightness != actual) {
			throw new IOException("Unsupported brightness");
		}
	}

	/**
	 * Sets the camera's white balance
	 * 
	 * @param whiteBalance
	 * @throws IOException
	 */
	public synchronized void setWhiteBalance(int whiteBalance) throws IOException {
		// Attempt to set it to the white balance
		videoCapture.set(Videoio.CAP_PROP_WB_TEMPERATURE, whiteBalance);

		// Read it back to determine if it was supported
		int actual = (int) videoCapture.get(Videoio.CAP_PROP_WB_TEMPERATURE);
		if (whiteBalance != actual) {
			throw new IOException("Unsupported white balance");
		}
	}

	/**
	 * Returns the configured fps
	 * 
	 * @return
	 */
	public int getFPS() {
		return (int) videoCapture.get(Videoio.CAP_PROP_FPS);
	}

	/**
	 * Reads a frame
	 * 
	 * @param frame
	 */
	public void grabFrame(Mat frame) {
		videoCapture.read(frame);
	}

	public VisionCamera(String name, int id) {
		this.name = name;
		this.id = id;

		videoCapture = new VideoCapture(id);

		setMaxResolution();
	}

	public VisionCamera(int id) {
		this("camera", id);
	}

	public static class CameraResolution {
		private int width;
		private int height;

		public int getWidth() {
			return width;
		}

		public int getHeight() {
			return height;
		}

		public CameraResolution(int width, int height) {
			this.width = width;
			this.height = height;
		}
	}
}
