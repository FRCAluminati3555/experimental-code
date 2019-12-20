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

package org.aluminati3555.aluminatipath2;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * This is the main class of the AluminatiPath2 path generator
 * 
 * @author Caleb Heydon
 */
public class AluminatiPath2 extends Application {
	/**
	 * This main method starts JavaFx
	 * @param args
	 */
	public static void main(String[] args) {
		// Launch JavaFx application
		launch(args);
	}
	
	/**
	 * This is the real main method of the application
	 */
	public void start(Stage window) {
		// Initialize basic settings
		
		// Shutdown nicely when the window is closed
		window.setOnCloseRequest(e -> {
			
			// Use Platform.runLater() to prevent the window from not closing smoothly (freezes and then closes)
			Platform.runLater(() -> {
				System.exit(0);
			});
		});
		
		// Set scene to field scene
		FieldPane fieldPane = new FieldPane();
		Scene scene = new Scene(fieldPane, fieldPane.getField().getWidth() + 200, fieldPane.getField().getHeight());
		window.setScene(scene);
		
		// Initialize window
		window.setTitle("AluminatiPath2");
		window.setResizable(false);
		window.show();
	}
}
