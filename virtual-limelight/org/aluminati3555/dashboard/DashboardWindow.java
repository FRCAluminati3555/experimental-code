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

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.text.Font;

/**
 * This class is for the main view of the dashboard
 * 
 * @author Caleb Heydon
 */
public class DashboardWindow extends BorderPane {
	private SimpleDoubleProperty tx = new SimpleDoubleProperty(0);
	private SimpleDoubleProperty ty = new SimpleDoubleProperty(0);
	private SimpleDoubleProperty ta = new SimpleDoubleProperty(50);
	
	private SimpleIntegerProperty tv = new SimpleIntegerProperty(0);
	
	/**
	 * Gets the left side of the window
	 * 
	 * @throws IOException
	 */
	private VBox getLeftContent() {
		VBox pane = new VBox();
		pane.setStyle("-fx-background-color: black;");
		pane.minWidthProperty().bind(this.widthProperty().divide(2));
		pane.setAlignment(Pos.CENTER);
		
		Ellipse target = new Ellipse(100, 100);
		target.setFill(Color.LIME);
		target.radiusXProperty().bind(ta);
		target.radiusYProperty().bind(ta);
		pane.getChildren().add(target);
		
		pane.setOnMouseClicked(e -> {
			Platform.runLater(() -> {
				double x = e.getX() - pane.getWidth() / 2;
				double y = e.getY() - pane.getHeight() / 2;
				
				target.setTranslateX(x);
				target.setTranslateY(y);
				
				tx.set((x / (pane.getWidth() / 2)) * 29.8);
				ty.set((-y / (pane.getHeight() / 2)) * 24.85);
			});
		});
		
		return pane;
	}

	/**
	 * Gets the right side of the window
	 */
	private VBox getRightContent() {
		VBox pane = new VBox(10);
		pane.setPadding(new Insets(20, 20, 20, 20));
		pane.minWidthProperty().bind(this.widthProperty().divide(2));
		
		Label xLabel = new Label("x: 0.0");
		xLabel.setFont(Font.font(20));
		tx.addListener((e, oldValue, newValue) -> {
			xLabel.setText("x: " + newValue);
		});
		pane.getChildren().add(xLabel);
		
		Label yLabel = new Label("y: 0.0");
		yLabel.setFont(Font.font(20));
		ty.addListener((e, oldValue, newValue) -> {
			yLabel.setText("y: " + newValue);
		});
		pane.getChildren().add(yLabel);
		
		Label aLabel = new Label("a: 50.0");
		aLabel.setFont(Font.font(20));
		ta.addListener((e, oldValue, newValue) -> {
			aLabel.setText("a: " + newValue);
		});
		pane.getChildren().add(aLabel);
		
		Slider aSlider = new Slider(0, 100, 50);
		aSlider.setShowTickMarks(true);
		aSlider.setShowTickLabels(true);
		ta.bind(aSlider.valueProperty());
		pane.getChildren().add(aSlider);
		
		Label vLabel = new Label("v: 0");
		vLabel.setFont(Font.font(20));
		tv.addListener((e, oldValue, newValue) -> {
			vLabel.setText("v: " + newValue);
		});
		pane.getChildren().add(vLabel);
		
		ToggleGroup toggleGroup = new ToggleGroup();
		RadioButton visibleButton = new RadioButton("Visible");
		RadioButton hiddenButton = new RadioButton("Hidden");
		
		visibleButton.setToggleGroup(toggleGroup);
		hiddenButton.setToggleGroup(toggleGroup);
		toggleGroup.selectToggle(hiddenButton);
		
		toggleGroup.selectedToggleProperty().addListener((e, oldValue, newValue) -> {
			if (newValue == visibleButton) {
				tv.set(1);
			} else {
				tv.set(0);
			}
		});
		
		pane.getChildren().add(visibleButton);
		pane.getChildren().add(hiddenButton);
		
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
	
	public DashboardWindow() {
		NetworkTableInstance nt = AluminatiDashboard.networkManager.getNT();
		NetworkTable table = nt.getTable("limelight");
		
		table.getEntry("tx").setNumber(tx.get());
		table.getEntry("ty").setNumber(ty.get());
		table.getEntry("ta").setNumber(ta.get());
		table.getEntry("tv").setNumber(tv.get());
		
		tx.addListener((e, oldValue, newValue) -> {
			table.getEntry("tx").setNumber(newValue);
		});
		
		ty.addListener((e, oldValue, newValue) -> {
			table.getEntry("ty").setNumber(newValue);
		});
		
		ta.addListener((e, oldValue, newValue) -> {
			table.getEntry("ta").setNumber(newValue);
		});
		
		tv.addListener((e, oldValue, newValue) -> {
			table.getEntry("tv").setNumber(newValue);
		});
	}
}
