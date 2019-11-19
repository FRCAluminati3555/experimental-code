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

package org.aluminati3555.aluminativision.web;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.ArrayList;

import org.aluminati3555.aluminativision.ServerConfig;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfInt;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

/**
 * This is a simple mjpeg server for streaming video to the driver station
 * 
 * @author Caleb Heydon
 */
public class MJPEGServer extends Thread {
	private static final String SERVER_NAME = "MJPEGServer";

	/**
	 * Compresses an image
	 * 
	 * @param mat
	 */
	private static void compress(Mat mat) {
		MatOfByte matOfByte = new MatOfByte();
		MatOfInt params = new MatOfInt(Imgcodecs.IMWRITE_JPEG_QUALITY, ServerConfig.getConfig().streamCompression);
		Imgcodecs.imencode(".jpg", mat, matOfByte, params);
		params.release();

		Mat output = Imgcodecs.imdecode(matOfByte, Imgcodecs.IMREAD_COLOR);
		output.copyTo(mat);
		output.release();
	}

	/**
	 * Converts a mat to a compressed jpeg
	 * 
	 * @param mat
	 * @return
	 */
	private static byte[] getJPEGBytes(Mat mat) {
		MatOfByte matOfByte = new MatOfByte();
		Imgcodecs.imencode(".jpg", mat, matOfByte);

		byte[] buffer = matOfByte.toArray();
		matOfByte.release();

		return buffer;
	}

	private ServerSocket serverSocket;
	private ArrayList<ClientHandler> clients;
	private DecimalFormat decimalFormat;

	@Override
	public void run() {
		while (true) {
			try {
				Socket socket = serverSocket.accept();
				ClientHandler client = new ClientHandler(socket);
				client.start();
				clients.add(client);
			} catch (IOException e) {
				System.err.println("Warning: Socket error");
				continue;
			}
		}
	}

	/**
	 * Sends a frame to the clients
	 * 
	 * @param frame
	 */
	public synchronized void sendFrame(Mat frame, double fps) {
		fps = Double.parseDouble(decimalFormat.format(fps));
		Imgproc.putText(frame, fps + " FPS", new Point(5, 10), 0, 0.25, new Scalar(0, 255, 0));

		compress(frame);

		byte[] buffer = getJPEGBytes(frame);

		for (int i = 0; i < clients.size(); i++) {
			try {
				clients.get(i).sendFrame(buffer);
			} catch (IOException e) {
				clients.remove(i);
				i--;
			}
		}

		frame.release();
	}

	public MJPEGServer(int port) throws IOException {
		serverSocket = new ServerSocket(port);
		clients = new ArrayList<ClientHandler>();
		decimalFormat = new DecimalFormat("###.#");
	}

	private class ClientHandler {
		private OutputStream outputStream;

		public void start() throws IOException {
			outputStream.write(("HTTP/1.0 200 OK\r\nServer: " + SERVER_NAME
					+ "\r\nContent-Type: multipart/x-mixed-replace; boundary=--BoundaryString\r\n\r\n").getBytes());
			outputStream.flush();
		}

		public void sendFrame(byte[] frame) throws IOException {
			outputStream.write(
					("--BoundaryString\r\nContent-Type: image/jpeg\r\nContent-Length: " + frame.length + "\r\n\r\n")
							.getBytes());
			outputStream.write(frame);

			outputStream.flush();
		}

		public ClientHandler(Socket socket) throws IOException {
			this.outputStream = socket.getOutputStream();
		}
	}
}
