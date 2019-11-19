package org.aluminati3555.net;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;

import javax.imageio.ImageIO;

public class MjpegRunner implements Runnable {
	private static final String CONTENT_LENGTH = "Content-Length: ";
	private final URL url;
	private MjpegViewer viewer;
	private InputStream urlStream;
	private boolean isRunning = true;

	public MjpegRunner(MjpegViewer viewer, URL url) throws IOException {
		this.viewer = viewer;
		this.url = url;

		start();
	}

	private synchronized void start() throws IOException {
		URLConnection urlConn = url.openConnection();
		// change the timeout to taste, I like 1 second
		urlConn.setReadTimeout(5000);
		urlConn.connect();
		urlStream = urlConn.getInputStream();
	}

	public synchronized void stop() {
		isRunning = false;
	}

	/**
	 * Keeps running while process() returns true
	 * <p>
	 * Each loop asks for the next JPEG image and then sends it to our JPanel to
	 * draw
	 *
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		while (isRunning) {
			boolean error = false;
			try {
				byte[] imageBytes = retrieveNextImage();
				ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes);

				BufferedImage frame = ImageIO.read(bais);
				viewer.setBufferedImage(frame);
			} catch (SocketTimeoutException ste) {
				System.err.println("Failed stream read: " + ste);
				error = true;
			} catch (IOException e) {
				System.err.println("Failed stream read: " + e);
				error = true;
			}
			
			if (error) {
				try {
					start();
				} catch (IOException e) {
					
				}
			}
		}
	}

	/**
	 * Using the urlStream get the next JPEG image as a byte[]
	 *
	 * @return byte[] of the JPEG
	 * @throws IOException
	 */
	private byte[] retrieveNextImage() throws IOException {
		int currByte = -1;

		// build headers
		// the DCS-930L stops it's headers

		boolean captureContentLength = false;
		StringWriter contentLengthStringWriter = new StringWriter(128);
		StringWriter headerWriter = new StringWriter(128);

		int contentLength = 0;

		while ((currByte = urlStream.read()) > -1) {
			if (captureContentLength) {
				if (currByte == 10 || currByte == 13) {
					contentLength = Integer.parseInt(contentLengthStringWriter.toString());
					break;
				}
				contentLengthStringWriter.write(currByte);

			} else {
				headerWriter.write(currByte);
				String tempString = headerWriter.toString();
				int indexOf = tempString.indexOf(CONTENT_LENGTH);
				if (indexOf > 0) {
					captureContentLength = true;
				}
			}
		}

		// 255 indicates the start of the jpeg image
		while ((urlStream.read()) != 255) {
			// just skip extras
		}

		// rest is the buffer
		byte[] imageBytes = new byte[contentLength + 1];
		// since we ate the original 255 , shove it back in
		imageBytes[0] = (byte) 255;
		int offset = 1;
		int numRead = 0;
		while (offset < imageBytes.length
				&& (numRead = urlStream.read(imageBytes, offset, imageBytes.length - offset)) >= 0) {
			offset += numRead;
		}

		return imageBytes;
	}

	/**
	 * Returns true if running
	 * 
	 * @return
	 */
	public boolean isRunning() {
		return isRunning;
	}
}