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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * This class holds all of the vision server's settings
 * 
 * @author Caleb Heydon
 */
public class ServerConfig {
	// Static methods to get a byte array representation and to interpret one

	/**
	 * Parses a configuration from a byte array
	 * 
	 * @param config
	 * @return
	 */
	public static ServerConfig parse(byte[] array) {
		ServerConfig config = new ServerConfig();

		try {
			ByteArrayInputStream byteStream = new ByteArrayInputStream(array);
			DataInputStream stream = new DataInputStream(byteStream);

			config.targetFPS = stream.readInt();
			config.webPort = stream.readInt();
			config.cameraServerPort1 = stream.readInt();
			config.cameraServerPort2 = stream.readInt();
			config.robotPort = stream.readInt();
			config.networkMode = stream.readInt();
			config.teamNumber = stream.readInt();
			config.visionProcessingFrameWidth = stream.readInt();
			config.visionProcessingFrameHeight = stream.readInt();
			config.streamFrameWidth = stream.readInt();
			config.streamFrameHeight = stream.readInt();
			config.streamCompression = stream.readInt();
			config.robotIP = stream.readUTF();

			stream.close();
		} catch (IOException e) {
			// This should never happen if a proper byte array is passed
			return null;
		}

		// Return default config or as much as was able to be read
		return config;
	}

	/**
	 * Reads a config from an input stream
	 * 
	 * @param inputStream
	 * @return
	 * @throws IOException
	 */
	public static ServerConfig parse(InputStream inputStream) throws IOException {
		ServerConfig config = new ServerConfig();

		DataInputStream stream = new DataInputStream(inputStream);

		config.targetFPS = stream.readInt();
		config.webPort = stream.readInt();
		config.cameraServerPort1 = stream.readInt();
		config.cameraServerPort2 = stream.readInt();
		config.robotPort = stream.readInt();
		config.networkMode = stream.readInt();
		config.teamNumber = stream.readInt();
		config.visionProcessingFrameWidth = stream.readInt();
		config.visionProcessingFrameHeight = stream.readInt();
		config.streamFrameWidth = stream.readInt();
		config.streamFrameHeight = stream.readInt();
		config.streamCompression = stream.readInt();
		config.robotIP = stream.readUTF();

		// Return the config
		return config;
	}

	/**
	 * Returns a byte array of a config
	 */
	public static byte[] getBytes(ServerConfig config) {
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		DataOutputStream stream = new DataOutputStream(byteStream);

		try {
			stream.writeInt(config.targetFPS);
			stream.writeInt(config.webPort);
			stream.writeInt(config.cameraServerPort1);
			stream.writeInt(config.cameraServerPort2);
			stream.writeInt(config.robotPort);
			stream.writeInt(config.networkMode);
			stream.writeInt(config.teamNumber);
			stream.writeInt(config.visionProcessingFrameWidth);
			stream.writeInt(config.visionProcessingFrameHeight);
			stream.writeInt(config.streamFrameWidth);
			stream.writeInt(config.streamFrameHeight);
			stream.writeInt(config.streamCompression);
			stream.writeUTF(config.robotIP);

			stream.close();
		} catch (IOException e) {
			return null;
		}

		return byteStream.toByteArray();
	}

	/**
	 * Writes a config to an output stream
	 * 
	 * @param config
	 * @param outputStream
	 * @throws IOException
	 */
	public static void write(ServerConfig config, OutputStream outputStream) throws IOException {
		DataOutputStream stream = new DataOutputStream(outputStream);

		stream.writeInt(config.targetFPS);
		stream.writeInt(config.webPort);
		stream.writeInt(config.cameraServerPort1);
		stream.writeInt(config.cameraServerPort2);
		stream.writeInt(config.robotPort);
		stream.writeInt(config.networkMode);
		stream.writeInt(config.teamNumber);
		stream.writeInt(config.visionProcessingFrameWidth);
		stream.writeInt(config.visionProcessingFrameHeight);
		stream.writeInt(config.streamFrameWidth);
		stream.writeInt(config.streamFrameHeight);
		stream.writeInt(config.streamCompression);
		stream.writeUTF(config.robotIP);
	}

	// Store a static instance here
	private static ServerConfig instance;

	/**
	 * Sets the configuration
	 * 
	 * @param config
	 */
	public static synchronized void setConfig(ServerConfig config) {
		instance = config;
	}

	/**
	 * Returns the configuration
	 * 
	 * @return
	 */
	public static ServerConfig getConfig() {
		return instance;
	}

	// Vision parameters
	public int targetFPS = 30;

	// Network port parameters
	public int webPort = 5801;
	public int cameraServerPort1 = 5802;
	public int cameraServerPort2 = 5803;
	public int robotPort = 5801;

	// Other network parameters

	// 0 = UDP, 1 = Network Tables
	public int networkMode = 0;

	// Team
	public int teamNumber = 3555;

	// Camera resolution
	public int visionProcessingFrameWidth = 320;
	public int visionProcessingFrameHeight = 240;

	public int streamFrameWidth = 160;
	public int streamFrameHeight = 120;
	public int streamCompression = 50;
	
	// Robot ip
	public String robotIP = "127.0.0.1";

	@Override
	public String toString() {
		String string = "";

		string += "targetFPS = " + targetFPS + "\n";
		string += "webPort = " + webPort + "\n";
		string += "cameraServerPort1 = " + cameraServerPort1 + "\n";
		string += "cameraServerPort2 = " + cameraServerPort2 + "\n";
		string += "robotPort = " + robotPort + "\n";
		string += "networkMode = " + networkMode + "\n";
		string += "teamNumber = " + teamNumber + "\n";
		string += "visionProcessingFrameWidth = " + visionProcessingFrameWidth + "\n";
		string += "visionProcessingFrameHeight = " + visionProcessingFrameHeight + "\n";
		string += "streamFrameWidth = " + streamFrameWidth + "\n";
		string += "streamFrameHeight = " + streamFrameHeight + "\n";
		string += "streamCompression = " + streamCompression + "\n";
		string += "robotIP = " + robotIP + "\n";

		return string;
	}
}
