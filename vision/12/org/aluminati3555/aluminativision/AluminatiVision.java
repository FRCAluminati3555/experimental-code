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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;

import org.aluminati3555.aluminativision.camera.VisionCamera;
import org.aluminati3555.aluminativision.camera.VisionCamera.CameraResolution;
import org.aluminati3555.aluminativision.net.NTStarter;
import org.aluminati3555.aluminativision.net.NetworkConfig;
import org.aluminati3555.aluminativision.pipeline.DefaultPipeline;
import org.aluminati3555.aluminativision.pipeline.PipelineManager;
import org.aluminati3555.aluminativision.web.WebServer;
import org.opencv.core.Core;
import org.opencv.videoio.VideoCapture;

/**
 * This is the main class responsible for starting the vision server
 * 
 * @author Caleb Heydon
 */
public class AluminatiVision {
	public static final String VERSION = "v2019.1";

	private static final String CONFIG_FILE = "/etc/server-config.dat";
	private static final String HOSTNAME_FILE = "/etc/hostname";
	private static final String WEB_ROOT = "/home/pi/AluminatiVision/web";

	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	private static VisionCamera camera0;
	private static VisionCamera camera1;

	/**
	 * Prints the banner
	 */
	private static void printBanner() {
		System.out.println("AluminatiVision\nVersion " + VERSION + "\nCopyright (c) 2019 Team 3555\n");
	}

	/**
	 * Loads the configuration file
	 */
	private static void loadConfig() {
		ServerConfig config = null;
		try {
			config = ServerConfig.parse(new FileInputStream(CONFIG_FILE));
		} catch (IOException e) {
			System.out.println("Creating new server config...");

			// Create a new config file
			config = new ServerConfig();

			try {
				ServerConfig.write(config, new FileOutputStream(CONFIG_FILE));
			} catch (IOException e1) {
				System.err.println("Warning: Unable to create new server config (using default)");
			}
		}

		ServerConfig.setConfig(config);
		System.out.println("Server config: \n" + config.toString());
	}

	/**
	 * This method probes the hardware
	 */
	private static void probeHardware() {
		HardwareConfig config = new HardwareConfig();

		// Detect cameras
		int numberOfCameras = 0;
		for (int i = 0; i < 2; i++) {
			VideoCapture camera = new VideoCapture(i);

			if (camera.isOpened()) {
				numberOfCameras++;
			}
		}
		config.numberOfCameras = numberOfCameras;
		if (numberOfCameras < 1) {
			System.err.println("Error: No cameras were detected");
			System.exit(-1);
		}

		System.out.println(numberOfCameras + " camera(s) were detected");

		HardwareConfig.setConfig(config);
	}

	/**
	 * Starts network tables
	 */
	private static void startNetworkTables() {
		NTStarter ntStarter = new NTStarter();
		ntStarter.setName("NT-Starter");
		ntStarter.setPriority(Thread.MAX_PRIORITY);
		ntStarter.start();
	}

	/**
	 * Gets the network config
	 */
	private static void probeNetwork() {
		NetworkConfig config = new NetworkConfig();

		try {
			Scanner scanner = new Scanner(new File(HOSTNAME_FILE));
			String hostname = scanner.nextLine() + ".local";
			scanner.close();

			config.hostname = hostname;
		} catch (FileNotFoundException e) {
			System.err.println("Warning: Unable to read hostname (using default)");
		}

		NetworkConfig.setConfig(config);
	}

	/**
	 * Creates the camera objects
	 */
	private static void createCameras() {
		CameraResolution resolution = new CameraResolution(ServerConfig.getConfig().visionProcessingFrameWidth,
				ServerConfig.getConfig().visionProcessingFrameHeight);

		camera0 = new VisionCamera("camera0", 0);
		try {
			camera0.setFPS(ServerConfig.getConfig().targetFPS);
		} catch (IOException e) {
			System.err.println(camera0.getName() + " does not support the target FPS");
			System.exit(-1);
		}
		try {
			camera0.setResolution(resolution);
		} catch (IOException e) {
			System.err.println("Warning: The requested resolution is not supported on " + camera0.getName()
					+ " (using max resolution)");
			camera0.setMaxResolution();
		}

		System.out.println("camera0: width = " + camera0.getResolution().getWidth() + ", height = "
				+ camera0.getResolution().getHeight() + ", fps = " + camera0.getFPS());

		if (HardwareConfig.getConfig().numberOfCameras > 1) {
			camera1 = new VisionCamera("camera1", 1);
			try {
				camera1.setFPS(ServerConfig.getConfig().targetFPS);
			} catch (IOException e) {
				System.err.println(camera1.getName() + " does not support the target FPS");
				System.exit(-1);
			}
			try {
				camera1.setResolution(resolution);
			} catch (IOException e) {
				System.err.println("Warning: The requested resolution is not supported on " + camera1.getName()
						+ " (using max resolution)");
				camera1.setMaxResolution();
			}

			System.out.println("camera1: width = " + camera1.getResolution().getWidth() + ", height = "
					+ camera1.getResolution().getHeight() + ", fps = " + camera1.getFPS());
		}
	}

	/**
	 * Starts the vision loops
	 */
	private static void startVisionLoops() {
		VisionLoop loop0 = new VisionLoop(camera0, new DefaultPipeline(), null);
		VisionLoop loop1 = null;

		loop0.setName("camera0");
		loop0.setPriority(Thread.MAX_PRIORITY);

		if (HardwareConfig.getConfig().numberOfCameras > 1) {
			loop1 = new VisionLoop(camera1, new DefaultPipeline(), null);
			loop1.setName("camera1");
			loop1.setPriority(Thread.MAX_PRIORITY);
		}

		VisionLoopManager visionLoopManager = new VisionLoopManager();
		visionLoopManager.camera0 = loop0;
		visionLoopManager.camera1 = loop1;
		VisionLoopManager.setManager(visionLoopManager);

		loop0.start();
		try {
			loop0.startCameraServer(ServerConfig.getConfig().cameraServerPort1);
		} catch (IOException e) {
			System.err.println("Error: Unable to start server for camera0");
			System.exit(-1);
		}

		if (HardwareConfig.getConfig().numberOfCameras > 1) {
			loop1.start();
			try {
				loop1.startCameraServer(ServerConfig.getConfig().cameraServerPort1);
			} catch (IOException e) {
				System.err.println("Error: Unable to start server for camera1");
				System.exit(-1);
			}
		}
	}
	
	/**
	 * Initializes the pipelines
	 */
	private static void initPipelines() {
		PipelineManager.createAllNew();
		
		VisionLoopManager.getManager().camera0.setVisionPipeline(PipelineManager.pipelines[0]);
		
		if (HardwareConfig.getConfig().numberOfCameras > 1) {
			VisionLoopManager.getManager().camera1.setVisionPipeline(PipelineManager.pipelines[0]);
		}
	}

	/**
	 * Publishes the camera streams
	 */
	private static void publishCameraStreams() {
//		NetworkTableInstance.getDefault().getTable("CameraPublisher").getSubTable(camera0.getName()).getEntry("streams")
//				.setStringArray(
//						new String[] { "mjpg:" + VisionUtil.getCameraStream(NetworkConfig.getConfig().hostname, 0) });
//
//		if (HardwareConfig.getConfig().numberOfCameras > 1) {
//			NetworkTableInstance.getDefault().getTable("CameraPublisher").getSubTable(camera1.getName())
//					.getEntry("streams").setStringArray(new String[] {
//							"mjpg:" + VisionUtil.getCameraStream(NetworkConfig.getConfig().hostname, 1) });
//		}
	}

	/**
	 * Starts the web server
	 */
	private static void startWebServer(String root) {
		if (!new File(root).exists()) {
			System.err.println("Error: The web server's root directory does not exist");
			System.exit(-1);
		}

		WebServer webServer = new WebServer(root, ServerConfig.getConfig().webPort);
		webServer.setName("Web-Server");
		webServer.setPriority(Thread.MIN_PRIORITY);
		webServer.start();
	}

	public static void main(String[] args) {
		// Print the banner
		printBanner();

		// Load the configuration file
		loadConfig();

		// Probe hardware
		probeHardware();

		// Start network tables
		startNetworkTables();

		// Probe network settings
		probeNetwork();

		// Create camera objects
		createCameras();

		// Start vision loops
		startVisionLoops();
		
		// Initialize pipelines
		initPipelines();

		// Publish cameras
		publishCameraStreams();

		// Start web server
		startWebServer((args.length > 0) ? args[0] : WEB_ROOT);

		System.out.println("Initialization complete");
	}
}
