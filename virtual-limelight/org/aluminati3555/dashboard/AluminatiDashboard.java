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

import org.aluminati3555.net.NetworkManager;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * This is the main class of the custom dashboard
 * 
 * @author Caleb Heydon
 */
public class AluminatiDashboard extends Application {
	public static final int TEAM = 3555;
	public static final long WAIT_TIME = 5000;
	public static final long THREAD_WAIT_TIME = 1000;

	public static NetworkManager networkManager;
	public static DashboardWindow dashboard;

	@Override
	public void start(Stage window) {
		networkManager = new NetworkManager();
		
		// Setup window
		window.setResizable(false);
		window.setTitle("Virtual Limelight");

		window.setX(0);
		window.setY(0);

		window.xProperty().addListener((ov, oldValue, newValue) -> {
			window.setX(0);
		});

		window.yProperty().addListener((ov, oldValue, newValue) -> {
			window.setY(0);
		});

		double width = Screen.getPrimary().getBounds().getWidth();
		double height = Screen.getPrimary().getBounds().getHeight() / 2;

		window.setMinWidth(width);
		window.setMaxWidth(width);

		window.setMinHeight(height);
		window.setMaxHeight(height);

		dashboard = new DashboardWindow();
		
		window.setScene(new Scene(dashboard));

		window.setOnCloseRequest(e -> {
			Platform.runLater(() -> {
				System.exit(0);
			});
		});
		
		window.show();
		
		Thread leftThread = new Thread(() -> {
			while (!dashboard.setupLeft()) {
				try {
					Thread.sleep(THREAD_WAIT_TIME);
				} catch (InterruptedException e) {
					
				}
			}
		});
		leftThread.setName("Left Setup Thread");
		leftThread.setDaemon(true);
		leftThread.setPriority(Thread.MAX_PRIORITY);
		leftThread.start();
		
		Thread rightThread = new Thread(() -> {
			while (!dashboard.setupRight()) {
				try {
					Thread.sleep(THREAD_WAIT_TIME);
				} catch (InterruptedException e) {
					
				}
			}
		});
		rightThread.setName("Right Setup Thread");
		rightThread.setDaemon(true);
		rightThread.setPriority(Thread.MAX_PRIORITY);
		rightThread.start();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
