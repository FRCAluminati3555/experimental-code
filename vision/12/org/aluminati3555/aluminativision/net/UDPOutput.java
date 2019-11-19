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

package org.aluminati3555.aluminativision.net;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import org.aluminati3555.aluminativision.ServerConfig;

/**
 * This class sends udp packets with vision target info to the robot
 * 
 * @author Caleb Heydon
 */
public class UDPOutput implements IVisionOutput {
	private String target;
	private DatagramSocket socket;

	/**
	 * Sends vision data to the robot
	 * 
	 * @throws IOException
	 */
	public void send(VisionData data) throws IOException {
		// Prepare output
		ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
		DataOutputStream output = new DataOutputStream(byteOutput);

		output.writeInt(data.camera);
		output.writeDouble(data.fps);
		output.writeBoolean(data.hasTarget);

		output.writeDouble(data.x);
		output.writeDouble(data.y);

		output.writeDouble(data.targetWidth);
		output.writeDouble(data.targetHeight);
		output.writeDouble(data.targetArea);

		output.close();

		byte[] bytes = byteOutput.toByteArray();
		DatagramPacket packet = new DatagramPacket(bytes, bytes.length);
		packet.setAddress(InetAddress.getByName(target));
		packet.setPort(ServerConfig.getConfig().robotPort);

		socket.send(packet);
	}

	public UDPOutput(String target) throws SocketException {
		this.target = target;
		this.socket = new DatagramSocket();
	}
}
