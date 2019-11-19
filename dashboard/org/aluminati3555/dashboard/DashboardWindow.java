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

package org.aluminati3555.dashboard;

import java.io.IOException;

import org.aluminati3555.net.CameraView;

import javafx.application.Platform;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

/**
 * This class is for the main view of the dashboard
 * 
 * @author Caleb Heydon
 */
public class DashboardWindow extends BorderPane {
	public static final String CAMERA_1 = "USB Camera 0";
	public static final String CAMERA_2 = "limelight";

	/**
	 * Gets the left side of the window
	 * 
	 * @throws IOException
	 */
	private VBox getLeftContent() {
		String stream = null;
		try {
			stream = AluminatiDashboard.networkManager.getNT().getTable("CameraPublisher").getSubTable(CAMERA_1)
					.getEntry("streams").getStringArray(null)[1].replaceAll("mjpg:", "");
		} catch (NullPointerException e) {
			return null;
		}

		VBox pane = new VBox();
		pane.setStyle("-fx-background-color: grey;");
		pane.minWidthProperty().bind(this.widthProperty().divide(2));

		CameraView cameraView;
		try {
			cameraView = new CameraView(stream);
		} catch (IOException | NullPointerException e) {
			return null;
		}

		cameraView.fitWidthProperty().bind(pane.widthProperty());
		cameraView.fitHeightProperty().bind(pane.heightProperty());
		pane.getChildren().add(cameraView);

		return pane;
	}

	/**
	 * Gets the right side of the window
	 */
	private VBox getRightContent() {
		String stream = null;
		try {
			stream = AluminatiDashboard.networkManager.getNT().getTable("CameraPublisher").getSubTable(CAMERA_2)
					.getEntry("streams").getStringArray(null)[1].replaceAll("mjpg:", "");
		} catch (NullPointerException e) {
			return null;
		}

		stream = stream.replaceAll("mjpg:", "");

		VBox pane = new VBox();
		pane.setStyle("-fx-background-color: grey;");
		pane.minWidthProperty().bind(this.widthProperty().divide(2));

		CameraView cameraView;
		try {
			cameraView = new CameraView(stream);
		} catch (IOException | NullPointerException e) {
			return null;
		}

		cameraView.fitWidthProperty().bind(pane.widthProperty());
		cameraView.fitHeightProperty().bind(pane.heightProperty());
		pane.getChildren().add(cameraView);

		return pane;
	}

	/**
	 * Initializes the left camera feed
	 */
	public boolean setupLeft() {
		Platform.runLater(() -> {
			VBox pane = getLeftContent();
			if (pane == null) {
				return;
			}

			this.setLeft(pane);
		});

		return !(this.getLeft() == null);
	}

	/**
	 * Initializes the right camera feed
	 */
	public boolean setupRight() {
		Platform.runLater(() -> {
			VBox pane = getRightContent();
			if (pane == null) {
				return;
			}

			this.setRight(pane);
		});

		return !(this.getRight() == null);
	}
}
