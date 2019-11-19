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
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.aluminati3555.net;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.ImageView;

/**
 * This is a special image view for the mjpeg format
 * 
 * @author Caleb Heydon
 */
public class CameraView extends ImageView implements MjpegViewer {
	private MjpegRunner runner;
	
	/**
	 * Sets the current frame
	 */
	public void setBufferedImage(BufferedImage frame) {
		this.setImage(SwingFXUtils.toFXImage(frame, null));
	}

	/**
	 * Returns true if it is still running
	 * @return
	 */
	public boolean isRunning() {
		return runner.isRunning();
	}

	public CameraView(String stream) throws MalformedURLException, IOException {
		super();
		runner = new MjpegRunner(this, new URL(stream));
		
		Thread cameraThread = new Thread(runner);
		cameraThread.setName("Camera Thread");
		cameraThread.setDaemon(true);
		cameraThread.setPriority(Thread.MAX_PRIORITY);
		cameraThread.start();
	}
}
