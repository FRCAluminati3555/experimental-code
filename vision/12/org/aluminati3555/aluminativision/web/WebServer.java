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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import org.aluminati3555.aluminativision.VisionUtil;
import org.aluminati3555.aluminativision.net.NetworkConfig;
import org.aluminati3555.aluminativision.pipeline.ConfigurablePipeline.PipelineMode;
import org.aluminati3555.aluminativision.pipeline.ConfigurablePipeline.TargetMode;
import org.aluminati3555.aluminativision.pipeline.PipelineManager;

/**
 * This is a simple web server that has an api interface
 * 
 * @author Caleb heydon
 */
public class WebServer extends Thread {
	private static final String DEFAULT_FILE = "/index.html";
	private static final String API_DIRECTORY = "/api";

	private static final String SERVER_NAME = "AluminatiVision";

	private static final String RESPONSE_400 = "<h1>Bad request</h1>";
	private static final String RESPONSE_404 = "<h1>File not found</h1>";

	private String root;
	private int port;

	private ServerSocket serverSocket;

	/**
	 * Returns the root directory
	 * 
	 * @return
	 */
	public String getRoot() {
		return root;
	}

	/**
	 * Returns the port
	 * 
	 * @return
	 */
	public int getPort() {
		return port;
	}

	@Override
	public void run() {
		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			System.err.println("Error: Unable to start web server");
			System.exit(-1);
		}

		while (true) {
			try {
				Socket socket = serverSocket.accept();

				ClientHandler clientHandler = new ClientHandler(socket);
				clientHandler.run();
			} catch (IOException e) {
				continue;
			}
		}
	}

	public WebServer(String root, int port) {
		this.root = root;
		this.port = port;
	}

	private class ClientHandler {
		private Socket socket;
		private String host;

		/**
		 * Reads a request from the client
		 * 
		 * @return
		 * @throws IOException
		 */
		private String readRequest() throws IOException {
			String request = "";

			while (!request.endsWith("\r\n\r\n")) {
				request += (char) socket.getInputStream().read();
			}

			String[] requestArray = request.split("\r\n");

			if (requestArray.length == 0) {
				throw new IOException();
			}

			String firstLine = requestArray[0];

			// Remove up through GET
			if (!firstLine.contains("GET")) {
				// Invalid request
				throw new IOException();
			}

			try {
				firstLine = firstLine.substring(firstLine.indexOf("GET") + 4);
			} catch (IndexOutOfBoundsException e) {
				throw new IOException();
			}

			// Remove trailing HTTP/1.1
			if (!firstLine.contains("HTTP")) {
				// Invalid request
				throw new IOException();
			}

			try {
				firstLine = firstLine.substring(0, firstLine.indexOf("HTTP") - 1);
			} catch (IndexOutOfBoundsException e) {
				throw new IOException();
			}

			// Search for host
			for (int i = 0; i < requestArray.length; i++) {
				if (requestArray[i].startsWith("Host: ")) {
					host = requestArray[i].replaceFirst("Host: ", "");
					if (host.contains(":")) {
						host = host.substring(0, host.indexOf(":"));
					}
					break;
				}
			}

			return firstLine;
		}

		/**
		 * Handles an API request
		 * 
		 * @param request
		 */
		private void handleAPI(String request) throws IOException {
			request = request.replaceFirst("/api/", "");
			String[] command = request.split("/");

			if (command.length < 2) {
				throw new IOException();
			}

			if (command[0].equals("set")) {

			} else if (command[0].equals("get")) {
				String response = "null";

				if (command[1].equals("camera-stream")) {
					if (command.length < 3) {
						throw new IOException();
					}

					try {
						int id = Integer.parseInt(command[2]);
						String tmp = VisionUtil.getCameraStream(host, id);

						if (tmp != null) {
							response = tmp;
						}
					} catch (NumberFormatException e) {
						throw new IOException();
					}
				} else if (command[1].equals("pipeline")) {
					if (command.length < 4) {
						throw new IOException();
					}

					try {
						int pipeline = Integer.parseInt(command[2]);

						if (command[3].equals("pipeline-mode")) {
							if (PipelineManager.pipelines[pipeline]
									.getPipelineConfig().pipelineMode == PipelineMode.DRIVER) {
								response = "DRIVER";
							} else {
								response = "PROCESSING";
							}
						} else if (command[3].equals("target-mode")) {
							if (PipelineManager.pipelines[pipeline]
									.getPipelineConfig().targetMode == TargetMode.SINGLE) {
								response = "SINGLE";
							} else if (PipelineManager.pipelines[pipeline]
									.getPipelineConfig().targetMode == TargetMode.DUAL_HORIZONTAL) {
								response = "DUAL_HORIZONTAL";
							} else {
								response = "DUAL_VERTICAL";
							}
						} else if (command[3].equals("brightness")) {
							response = Integer
									.toString(PipelineManager.pipelines[pipeline].getPipelineConfig().brightness);
						} else if (command[3].equals("white-balance")) {
							response = Integer
									.toString(PipelineManager.pipelines[pipeline].getPipelineConfig().whiteBalance);
						} else if (command[3].equals("exposure")) {
							response = Integer
									.toString(PipelineManager.pipelines[pipeline].getPipelineConfig().exposure);
						} else if (command[3].equals("threshold-hue-min")) {
							response = Double
									.toString(PipelineManager.pipelines[pipeline].getPipelineConfig().thresholdHueMin);
						} else if (command[3].equals("threshold-hue-max")) {
							response = Double
									.toString(PipelineManager.pipelines[pipeline].getPipelineConfig().thresholdHueMax);
						} else if (command[3].equals("threshold-luminence-min")) {
							response = Double.toString(
									PipelineManager.pipelines[pipeline].getPipelineConfig().thresholdLuminenceMin);
						} else if (command[3].equals("threshold-luminence-max")) {
							response = Double.toString(
									PipelineManager.pipelines[pipeline].getPipelineConfig().thresholdLuminenceMax);
						} else if (command[3].equals("threshold-saturation-min")) {
							response = Double.toString(
									PipelineManager.pipelines[pipeline].getPipelineConfig().thresholdSaturationMin);
						} else if (command[3].equals("threshold-saturation-max")) {
							response = Double.toString(
									PipelineManager.pipelines[pipeline].getPipelineConfig().thresholdSaturationMax);
						} else if (command[3].equals("contour-area-min")) {
							response = Double
									.toString(PipelineManager.pipelines[pipeline].getPipelineConfig().contourAreaMin);
						} else if (command[3].equals("contour-area-max")) {
							response = Double
									.toString(PipelineManager.pipelines[pipeline].getPipelineConfig().contourAreaMax);
						} else if (command[3].equals("contour-ratio-min")) {
							response = Double
									.toString(PipelineManager.pipelines[pipeline].getPipelineConfig().contourRatioMin);
						} else if (command[3].equals("contour-ratio-max")) {
							response = Double
									.toString(PipelineManager.pipelines[pipeline].getPipelineConfig().contourRatioMax);
						} else if (command[3].equals("contour-density-min")) {
							response = Double.toString(
									PipelineManager.pipelines[pipeline].getPipelineConfig().contourDensityMin);
						} else if (command[3].equals("contour-density-max")) {
							response = Double.toString(
									PipelineManager.pipelines[pipeline].getPipelineConfig().contourDensityMax);
						} else {
							throw new IOException();
						}
					} catch (NumberFormatException e) {
						throw new IOException();
					}
				} else {
					socket.getOutputStream()
							.write(("HTTP/1.1 400 Bad Request\r\nServer: " + SERVER_NAME
									+ "\r\nConnection: close\r\nContent-Type: text/html\r\nContent-Length: "
									+ RESPONSE_400.length() + "\r\n\r\n" + RESPONSE_400).getBytes());
					socket.getOutputStream().flush();

					return;
				}

				socket.getOutputStream()
						.write(("HTTP/1.1 200 OK\r\nServer: " + SERVER_NAME
								+ "\r\nConnection: close\r\nContent-Type: text/plain\r\nContent-Length: "
								+ response.length() + "\r\n\r\n" + response).getBytes());

				socket.getOutputStream().flush();
			} else {
				throw new IOException();
			}
		}

		/**
		 * Handles a file request
		 * 
		 * @param request
		 */
		private void handleFile(String request) throws IOException {
			String path = root + request;
			File file = new File(path);

			if (!file.exists()) {
				socket.getOutputStream()
						.write(("HTTP/1.1 404 Not Found\r\nServer: " + SERVER_NAME
								+ "\r\nConnection: close\r\nContent-Type: text/html\r\nContent-Length: "
								+ RESPONSE_404.length() + "\r\n\r\n" + RESPONSE_404).getBytes());
				socket.getOutputStream().flush();
			} else {
				String type;

				if (path.toLowerCase().endsWith(".html")) {
					type = "text/html";
				} else if (path.toLowerCase().endsWith(".css")) {
					type = "text/css";
				} else if (path.toLowerCase().endsWith(".js")) {
					type = "text/javascript";
				} else {
					type = "text/plain";
				}

				FileInputStream fileInputStream = new FileInputStream(path);
				String response = "";
				int c;

				while ((c = fileInputStream.read()) != -1) {
					response += (char) c;
				}
				fileInputStream.close();

				socket.getOutputStream()
						.write(("HTTP/1.1 200 OK\r\nServer: " + SERVER_NAME + "\r\nConnection: close\r\nContent-Type: "
								+ type + "\r\nContent-Length: " + response.length() + "\r\n\r\n" + response)
										.getBytes());

				socket.getOutputStream().flush();
			}
		}

		public void run() {
			try {
				// Read the request
				String request = readRequest();

				// Decode
				request = URLDecoder.decode(request, StandardCharsets.UTF_8);

				// Sanitize the input
				request = WebUtil.sanitizeBlock(request);

				if (request.equals("/")) {
					request = DEFAULT_FILE;
				}

				// Handle request
				if (request.startsWith(API_DIRECTORY)) {
					handleAPI(request);
				} else {
					handleFile(request);
				}
			} catch (IOException | IllegalArgumentException e) {

			} finally {
				try {
					socket.close();
				} catch (IOException e1) {
					System.err.println("Warning: Unable to close socket");
				}
			}
		}

		public ClientHandler(Socket socket) throws IOException {
			this.socket = socket;
			this.host = NetworkConfig.getConfig().hostname;
		}
	}
}
