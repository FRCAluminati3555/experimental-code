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

import java.io.IOException;

import org.aluminati3555.aluminativision.camera.VisionCamera;
import org.aluminati3555.aluminativision.net.IVisionOutput;
import org.aluminati3555.aluminativision.net.VisionData;
import org.aluminati3555.aluminativision.pipeline.IVisionPipeline;
import org.aluminati3555.aluminativision.web.MJPEGServer;
import org.opencv.core.CvException;
import org.opencv.core.Mat;

/**
 * This class polls the camera at the target fps and sends the data to the robot
 * controller
 * 
 * @author Caleb Heydon
 */
public class VisionLoop extends Thread {

	private VisionCamera camera;
	private MJPEGServer cameraServer;
	private IVisionPipeline visionPipeline;
	private IVisionOutput visionOutput;

	private double lastTime;
	private double currentFPS;

	private boolean wantsExit;

	/**
	 * Returns the camera
	 * 
	 * @return
	 */
	public VisionCamera getCamera() {
		return camera;
	}

	/**
	 * Returns the vision pipeline
	 * 
	 * @return
	 */
	public IVisionPipeline getVisionPipeline() {
		return visionPipeline;
	}

	/**
	 * Sets the vision pipeline
	 * 
	 * @param visionPipeline
	 */
	public synchronized void setVisionPipeline(IVisionPipeline visionPipeline) {
		this.visionPipeline = visionPipeline;
	}

	/**
	 * Signals the vision loop to stop
	 * 
	 * @param wantsExit
	 */
	public synchronized void setWantsExit(boolean wantsExit) {
		this.wantsExit = wantsExit;
	}

	/**
	 * The start of the vision thread
	 */
	@Override
	public void run() {
		lastTime = VisionUtil.getTime();
		Mat frame = new Mat();

		while (!wantsExit) {
			// Update the camera settings
			visionPipeline.updateCamera(camera);
			
			camera.grabFrame(frame);
			try {
				frame = visionPipeline.process(frame, currentFPS);
			} catch (CvException e) {
				System.err.println("Error: Unable to read from " + camera.getName());
				continue;
			}
			
			// Get vision output
			VisionData output = visionPipeline.getOutput();
			if (visionOutput != null) {
				try {
					visionOutput.send(output);
				} catch (IOException e) {
					System.err.println("Error: Unable to send vision data");
				}
			}

			if (cameraServer != null) {
				VisionUtil.resize(frame, ServerConfig.getConfig().streamFrameWidth,
						ServerConfig.getConfig().streamFrameHeight);
				cameraServer.sendFrame(frame, currentFPS);
			}

			double endTime = VisionUtil.getTime();
			currentFPS = 1 / (endTime - lastTime);
			lastTime = endTime;
		}

		frame.release();
	}

	/**
	 * Starts the camera server
	 * 
	 * @param port
	 * @throws IOException
	 */
	public void startCameraServer(int port) throws IOException {
		cameraServer = new MJPEGServer(port);
		cameraServer.setName(camera.getName() + "-MJPEG");
		cameraServer.setPriority(Thread.MAX_PRIORITY);
		cameraServer.start();
	}

	public VisionLoop(VisionCamera camera, IVisionPipeline visionPipeline, IVisionOutput visionOutput) {
		this.camera = camera;
		this.visionPipeline = visionPipeline;
		this.visionOutput = visionOutput;
		
		currentFPS = camera.getFPS();

		this.wantsExit = false;
	}
}
